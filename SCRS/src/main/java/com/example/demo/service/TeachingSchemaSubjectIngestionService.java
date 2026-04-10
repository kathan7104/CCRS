/*
 * File: src/main/java/com/example/demo/service/TeachingSchemaSubjectIngestionService.java
 * Role: Service
 * MVC Fit: Contains business logic used by controllers.
 * Connects To: Controller calls Service, Service calls Repository
 */

package com.example.demo.service;

import com.example.demo.entity.Subject;
import com.example.demo.entity.TeachingSchema;
import com.example.demo.repository.SubjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

// Class Summary: Service class that contains business logic used by controllers.
// @Service marks the business logic layer for Spring to manage as a bean.
@Service
public class TeachingSchemaSubjectIngestionService {
// Field: stores log for this class.
// Service method: contains business logic and coordinates repositories.
    private static final Logger log = LoggerFactory.getLogger(TeachingSchemaSubjectIngestionService.class);
// Field: stores SEMESTER_PATTERN for this class.
// Service method: contains business logic and coordinates repositories.
    private static final Pattern SEMESTER_PATTERN = Pattern.compile("(?i)\\bsem(?:ester)?\\s*[-:]?\\s*(\\d{1,2})\\b");
// Field: stores SEMESTER_INDEX_PATTERN for this class.
// Service method: contains business logic and coordinates repositories.
    private static final Pattern SEMESTER_INDEX_PATTERN = Pattern.compile("(?i)\\bsem(?:ester)?\\s*[-:]?\\s*(\\d{1,2})\\b");
// Field: stores LINE_PATTERN for this class.
// Service method: contains business logic and coordinates repositories.
    private static final Pattern LINE_PATTERN = Pattern.compile(
            "^([A-Za-z]{2,}[A-Za-z0-9-]{1,20})\\s*[:|-]?\\s+(.+)$"
    );
// Field: stores SUBJECT_CODE_PATTERN for this class.
// Service method: contains business logic and coordinates repositories.
    private static final Pattern SUBJECT_CODE_PATTERN = Pattern.compile("\\b([A-Za-z]{2,}[A-Za-z0-9-]{1,20}\\d[A-Za-z0-9-]*)\\b");
// Field: stores TRAILING_NUMBERS_PATTERN for this class.
// Service method: contains business logic and coordinates repositories.
    private static final Pattern TRAILING_NUMBERS_PATTERN = Pattern.compile("\\s+\\d+(?:\\.\\d+)?(?:\\s+\\d+(?:\\.\\d+)?){0,3}\\s*$");

// Field: stores subjectRepository for this class.
    private final SubjectRepository subjectRepository;

// Constructor: Spring injects dependencies here.
    public TeachingSchemaSubjectIngestionService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

// Service method: contains business logic and coordinates repositories.
    public int ingestSubjects(TeachingSchema schema, Path filePath, String originalFileName) throws IOException {
        if (schema == null || filePath == null || !Files.exists(filePath)) {
            return 0;
        }
        String text;
        try {
            text = extractText(filePath, normalize(originalFileName));
        } catch (Throwable ex) {
            log.warn("Teaching schema extraction skipped for {} due to parser/runtime issue: {}",
                    filePath, ex.toString());
            return 0;
        }
        if (text.isBlank()) {
            return 0;
        }

        List<ParsedSubject> parsedSubjects = parse(text);
        if (parsedSubjects.isEmpty()) {
            // Fallback parser for heavily formatted documents where line structure collapses.
            parsedSubjects = parseByCodeSpans(text);
        }
        if (parsedSubjects.isEmpty()) {
            return 0;
        }

        int savedCount = 0;
        for (ParsedSubject parsed : parsedSubjects) {
            Optional<Subject> existing = subjectRepository.findByDepartmentIgnoreCaseAndSubjectCodeIgnoreCase(
                    schema.getDepartment(), parsed.subjectCode());
            Subject subject = existing.orElseGet(Subject::new);
            subject.setDepartment(schema.getDepartment());
            subject.setProgramName(schema.getProgramName());
            subject.setSubjectCode(parsed.subjectCode());
            subject.setSubjectName(parsed.subjectName());
            subject.setSemester(parsed.semester());
            subject.setCredits(parsed.credits());
            subject.setTeachingSchema(schema);
            subjectRepository.save(subject);
            savedCount++;
        }
        return savedCount;
    }

// Service method: contains business logic and coordinates repositories.
    private String extractText(Path filePath, String originalFileName) throws IOException {
        String lower = originalFileName.toLowerCase();
        if (lower.endsWith(".pdf")) {
            return extractPdfText(filePath);
        }
        if (lower.endsWith(".docx")) {
            try {
                return extractDocxText(filePath);
            } catch (Throwable ex) {
                log.warn("DOCX parser unavailable for {}. Falling back to XML-based extraction: {}", filePath, ex.toString());
                return extractDocxTextFallback(filePath);
            }
        }
        if (lower.endsWith(".doc")) {
            return extractDocText(filePath);
        }
        return "";
    }

// Service method: contains business logic and coordinates repositories.
    private String extractPdfText(Path filePath) throws IOException {
        try (PDDocument document = Loader.loadPDF(filePath.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return normalizeExtractedText(stripper.getText(document));
        }
    }

// Service method: contains business logic and coordinates repositories.
    private String extractDocxText(Path filePath) throws IOException {
        try (InputStream in = Files.newInputStream(filePath);
             XWPFDocument doc = new XWPFDocument(in);
             XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
            return normalizeExtractedText(extractor.getText());
        }
    }

// Service method: contains business logic and coordinates repositories.
    private String extractDocText(Path filePath) throws IOException {
        try (InputStream in = Files.newInputStream(filePath);
             HWPFDocument doc = new HWPFDocument(in);
             WordExtractor extractor = new WordExtractor(doc)) {
            return normalizeExtractedText(extractor.getText());
        }
    }

// Service method: contains business logic and coordinates repositories.
    private String extractDocxTextFallback(Path filePath) throws IOException {
        try (ZipFile zipFile = new ZipFile(filePath.toFile(), StandardCharsets.UTF_8)) {
            ZipEntry entry = zipFile.getEntry("word/document.xml");
            if (entry == null) {
                return "";
            }
            try (InputStream in = zipFile.getInputStream(entry)) {
                String xml = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                return normalizeExtractedText(cleanDocxXmlText(xml));
            }
        }
    }

// Service method: contains business logic and coordinates repositories.
    private String cleanDocxXmlText(String xml) {
        if (xml == null || xml.isBlank()) {
            return "";
        }
        String text = xml;
        text = text.replaceAll("(?is)<w:tab\\s*/>", "\t");
        text = text.replaceAll("(?is)</w:p>", "\n");
        text = text.replaceAll("(?is)<[^>]+>", " ");
        text = text.replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&apos;", "'");
        return text;
    }

// Service method: contains business logic and coordinates repositories.
    private List<ParsedSubject> parse(String text) {
        String[] lines = text.split("\\R");
        Integer semester = null;
        Map<String, ParsedSubject> byCode = new LinkedHashMap<>();

        for (String rawLine : lines) {
            String line = normalize(rawLine);
            if (line.isBlank()) {
                continue;
            }

            Matcher semesterMatcher = SEMESTER_PATTERN.matcher(line);
            if (semesterMatcher.find()) {
                semester = parseInteger(semesterMatcher.group(1));
            }
            if (looksLikeHeader(line)) {
                continue;
            }

            ParsedSubject parsed = parseLine(line, semester);
            if (parsed == null) {
                continue;
            }
            byCode.put(parsed.subjectCode().toUpperCase(), parsed);
        }

        return new ArrayList<>(byCode.values());
    }

// Service method: contains business logic and coordinates repositories.
    private List<ParsedSubject> parseByCodeSpans(String text) {
        String normalizedText = normalizeExtractedText(text);
        Matcher semesterMatcher = SEMESTER_INDEX_PATTERN.matcher(normalizedText);
        List<int[]> semesterMarks = new ArrayList<>();
        while (semesterMatcher.find()) {
            Integer sem = parseInteger(semesterMatcher.group(1));
            if (sem != null) {
                semesterMarks.add(new int[]{semesterMatcher.start(), sem});
            }
        }

        Matcher codeMatcher = SUBJECT_CODE_PATTERN.matcher(normalizedText);
        List<int[]> codes = new ArrayList<>();
        List<String> codeValues = new ArrayList<>();
        while (codeMatcher.find()) {
            String code = normalize(codeMatcher.group(1));
            if (!isValidCode(code.toUpperCase())) {
                continue;
            }
            codes.add(new int[]{codeMatcher.start(), codeMatcher.end()});
            codeValues.add(code);
        }
        if (codes.isEmpty()) {
            return List.of();
        }

        Map<String, ParsedSubject> parsed = new LinkedHashMap<>();
        for (int i = 0; i < codes.size(); i++) {
            int start = codes.get(i)[0];
            int end = codes.get(i)[1];
            int nextStart = (i + 1 < codes.size()) ? codes.get(i + 1)[0] : normalizedText.length();
            String code = codeValues.get(i);
            String span = normalizedText.substring(Math.min(end, normalizedText.length()), Math.min(nextStart, normalizedText.length()));
            if (span.isBlank()) {
                continue;
            }
            String cleaned = normalize(span)
                    .replaceAll("(?i)^[:|\\-\\s]+", "")
                    .replaceAll("(?i)\\bsem(?:ester)?\\s*[-:]?\\s*\\d{1,2}\\b", "")
                    .trim();
            String name = cleaned.replaceAll("(?i)\\bcredits?\\s*[:=-]?\\s*\\d{1,2}\\b.*$", "").trim();
            if (!isValidName(name)) {
                continue;
            }
            Integer semester = resolveSemesterForIndex(semesterMarks, start);
            Integer credits = extractCredits(code + " " + cleaned);
            ParsedSubject subject = new ParsedSubject(code.toUpperCase(), name, semester, credits);
            parsed.put(subject.subjectCode(), subject);
        }
        return new ArrayList<>(parsed.values());
    }

// Service method: contains business logic and coordinates repositories.
    private Integer resolveSemesterForIndex(List<int[]> semesterMarks, int index) {
        Integer semester = null;
        for (int[] mark : semesterMarks) {
            if (mark[0] > index) {
                break;
            }
            semester = mark[1];
        }
        return semester;
    }

// Service method: contains business logic and coordinates repositories.
    private ParsedSubject parseLine(String line, Integer semester) {
        String normalizedLine = line
                .replaceAll("^[\\p{Punct}\\s•·]+", "")
                .replaceAll("^\\d{1,3}[\\).:-]\\s*", "");

        Matcher matcher = LINE_PATTERN.matcher(normalizedLine);
        if (!matcher.find()) {
            String[] columns = normalizedLine.split("\\t+|\\s{2,}|\\s*\\|\\s*");
            if (columns.length < 2) {
                return null;
            }

            // Table rows can be like: SrNo | SubjectCode | SubjectName | Credits
            // Find first valid subject-code column, then use the next meaningful column as name.
            for (int i = 0; i < columns.length; i++) {
                String codeCandidate = normalize(columns[i]).replaceAll("\\s+", "");
                if (!isValidCode(codeCandidate.toUpperCase())) {
                    continue;
                }
                for (int j = i + 1; j < columns.length; j++) {
                    String nameCandidate = normalize(columns[j]);
                    if (nameCandidate.isBlank() || nameCandidate.matches("^\\d+(?:\\.\\d+)?$")) {
                        continue;
                    }
                    return buildSubject(codeCandidate, nameCandidate, semester, normalizedLine);
                }
                break;
            }
            return null;
        }

        String code = normalize(matcher.group(1));
        String name = normalize(matcher.group(2));
        return buildSubject(code, name, semester, normalizedLine);
    }

// Service method: contains business logic and coordinates repositories.
    private ParsedSubject buildSubject(String code, String name, Integer semester, String fullLine) {
        String normalizedCode = code.toUpperCase();
        if (!isValidCode(normalizedCode)) {
            return null;
        }

        String cleanedName = TRAILING_NUMBERS_PATTERN.matcher(name).replaceAll("").trim();
        if (!isValidName(cleanedName)) {
            return null;
        }

        Integer credits = extractCredits(fullLine);
        return new ParsedSubject(normalizedCode, cleanedName, semester, credits);
    }

// Service method: contains business logic and coordinates repositories.
    private Integer extractCredits(String line) {
        Matcher matcher = Pattern.compile("(?i)\\bcredits?\\s*[:=-]?\\s*(\\d{1,2})\\b").matcher(line);
        if (matcher.find()) {
            return parseInteger(matcher.group(1));
        }
        return null;
    }

// Service method: contains business logic and coordinates repositories.
    private boolean isValidCode(String code) {
        if (code == null || code.length() < 4 || code.length() > 22) {
            return false;
        }
        if (!code.matches("^[A-Z]{2,}[A-Z0-9-]{1,20}$")) {
            return false;
        }
        return code.chars().anyMatch(Character::isDigit);
    }

// Service method: contains business logic and coordinates repositories.
    private boolean isValidName(String name) {
        if (name == null || name.length() < 3 || name.length() > 255) {
            return false;
        }
        if (!name.matches(".*[A-Za-z].*")) {
            return false;
        }
        String lower = name.toLowerCase();
        return !lower.contains("subject code")
                && !lower.contains("subject name")
                && !lower.contains("teaching schema")
                && !lower.contains("syllabus");
    }

// Service method: contains business logic and coordinates repositories.
    private boolean looksLikeHeader(String line) {
        String lower = line.toLowerCase();
        return lower.contains("subject code")
                || lower.contains("subject name")
                || lower.contains("credits") && lower.contains("subject")
                || lower.contains("program") && lower.contains("department");
    }

// Service method: contains business logic and coordinates repositories.
    private Integer parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            return null;
        }
    }

// Service method: contains business logic and coordinates repositories.
    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.replace('\u00A0', ' ').trim().replaceAll("\\s+", " ");
    }

// Service method: contains business logic and coordinates repositories.
    private String normalizeExtractedText(String value) {
        if (value == null) {
            return "";
        }
        // Keep line breaks for row parsing; only normalize whitespace within each line later.
        return value.replace('\u00A0', ' ')
                .replace("\r\n", "\n")
                .replace('\r', '\n');
    }

// Service method: contains business logic and coordinates repositories.
    private record ParsedSubject(String subjectCode, String subjectName, Integer semester, Integer credits) {
    }
}
