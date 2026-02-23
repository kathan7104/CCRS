package com.example.demo.service;

import com.example.demo.entity.Subject;
import com.example.demo.entity.TeachingSchema;
import com.example.demo.repository.SubjectRepository;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TeachingSchemaSubjectIngestionService {
    private static final Pattern SEMESTER_PATTERN = Pattern.compile("(?i)\\bsem(?:ester)?\\s*[-:]?\\s*(\\d{1,2})\\b");
    private static final Pattern LINE_PATTERN = Pattern.compile(
            "^([A-Za-z]{2,}[A-Za-z0-9-]{1,20})\\s*[:|-]?\\s+(.+)$"
    );
    private static final Pattern TRAILING_NUMBERS_PATTERN = Pattern.compile("\\s+\\d+(?:\\.\\d+)?(?:\\s+\\d+(?:\\.\\d+)?){0,3}\\s*$");

    private final SubjectRepository subjectRepository;

    public TeachingSchemaSubjectIngestionService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public int ingestSubjects(TeachingSchema schema, Path filePath, String originalFileName) throws IOException {
        if (schema == null || filePath == null || !Files.exists(filePath)) {
            return 0;
        }
        String text = extractText(filePath, normalize(originalFileName));
        if (text.isBlank()) {
            return 0;
        }

        List<ParsedSubject> parsedSubjects = parse(text);
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

    private String extractText(Path filePath, String originalFileName) throws IOException {
        String lower = originalFileName.toLowerCase();
        if (lower.endsWith(".pdf")) {
            return extractPdfText(filePath);
        }
        if (lower.endsWith(".docx")) {
            return extractDocxText(filePath);
        }
        if (lower.endsWith(".doc")) {
            return extractDocText(filePath);
        }
        return "";
    }

    private String extractPdfText(Path filePath) throws IOException {
        try (PDDocument document = Loader.loadPDF(filePath.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return normalize(stripper.getText(document));
        }
    }

    private String extractDocxText(Path filePath) throws IOException {
        try (InputStream in = Files.newInputStream(filePath);
             XWPFDocument doc = new XWPFDocument(in);
             XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
            return normalize(extractor.getText());
        }
    }

    private String extractDocText(Path filePath) throws IOException {
        try (InputStream in = Files.newInputStream(filePath);
             HWPFDocument doc = new HWPFDocument(in);
             WordExtractor extractor = new WordExtractor(doc)) {
            return normalize(extractor.getText());
        }
    }

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

    private ParsedSubject parseLine(String line, Integer semester) {
        Matcher matcher = LINE_PATTERN.matcher(line);
        if (!matcher.find()) {
            String[] columns = line.split("\\t+|\\s{2,}|\\s*\\|\\s*");
            if (columns.length < 2) {
                return null;
            }
            String code = normalize(columns[0]);
            String name = normalize(columns[1]);
            return buildSubject(code, name, semester, line);
        }

        String code = normalize(matcher.group(1));
        String name = normalize(matcher.group(2));
        return buildSubject(code, name, semester, line);
    }

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

    private Integer extractCredits(String line) {
        Matcher matcher = Pattern.compile("(?i)\\bcredits?\\s*[:=-]?\\s*(\\d{1,2})\\b").matcher(line);
        if (matcher.find()) {
            return parseInteger(matcher.group(1));
        }
        return null;
    }

    private boolean isValidCode(String code) {
        if (code == null || code.length() < 4 || code.length() > 22) {
            return false;
        }
        if (!code.matches("^[A-Z]{2,}[A-Z0-9-]{1,20}$")) {
            return false;
        }
        return code.chars().anyMatch(Character::isDigit);
    }

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

    private boolean looksLikeHeader(String line) {
        String lower = line.toLowerCase();
        return lower.contains("subject code")
                || lower.contains("subject name")
                || lower.contains("credits") && lower.contains("subject")
                || lower.contains("program") && lower.contains("department");
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            return null;
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.replace('\u00A0', ' ').trim().replaceAll("\\s+", " ");
    }

    private record ParsedSubject(String subjectCode, String subjectName, Integer semester, Integer credits) {
    }
}
