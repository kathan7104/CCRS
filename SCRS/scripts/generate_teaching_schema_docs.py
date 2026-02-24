from pathlib import Path
import shutil
import zipfile


PROGRAMS = {
    "BCA": {
        "department": "Computer Applications",
        "semesters": {
            1: [
                ("BCA-S1-101", "Programming Fundamentals", 4),
                ("BCA-S1-102", "Discrete Mathematics", 3),
                ("BCA-S1-103", "Digital Computer Basics", 3),
                ("BCA-S1-104", "Communication Skills", 2),
                ("BCA-S1-105", "Environmental Studies", 2),
            ],
            2: [
                ("BCA-S2-201", "Data Structures", 4),
                ("BCA-S2-202", "Database Management Systems", 4),
                ("BCA-S2-203", "Operating Systems", 3),
                ("BCA-S2-204", "Object Oriented Concepts", 3),
                ("BCA-S2-205", "Business Accounting Basics", 2),
            ],
            3: [
                ("BCA-S3-301", "Java Programming", 4),
                ("BCA-S3-302", "Web Technologies", 4),
                ("BCA-S3-303", "Computer Networks", 3),
                ("BCA-S3-304", "Software Engineering", 3),
                ("BCA-S3-305", "Statistical Methods", 2),
            ],
            4: [
                ("BCA-S4-401", "Python Programming", 4),
                ("BCA-S4-402", "Advanced DBMS", 4),
                ("BCA-S4-403", "Mobile Application Development", 3),
                ("BCA-S4-404", "Cloud Fundamentals", 3),
                ("BCA-S4-405", "Aptitude and Reasoning", 2),
            ],
            5: [
                ("BCA-S5-501", "Data Analytics", 4),
                ("BCA-S5-502", "Machine Learning Basics", 3),
                ("BCA-S5-503", "Information Security", 3),
                ("BCA-S5-504", "Internet of Things", 3),
                ("BCA-S5-505", "Elective I", 2),
            ],
            6: [
                ("BCA-S6-601", "Project Work", 6),
                ("BCA-S6-602", "Software Testing", 3),
                ("BCA-S6-603", "DevOps Basics", 3),
                ("BCA-S6-604", "Internship", 4),
                ("BCA-S6-605", "Elective II", 2),
            ],
        },
    },
    "MCA": {
        "department": "Computer Applications",
        "semesters": {
            1: [
                ("MCA-S1-101", "Advanced Data Structures", 4),
                ("MCA-S1-102", "Design and Analysis of Algorithms", 4),
                ("MCA-S1-103", "Advanced DBMS", 4),
                ("MCA-S1-104", "Research Methodology", 3),
                ("MCA-S1-105", "Professional Communication", 2),
            ],
            2: [
                ("MCA-S2-201", "Cloud Computing", 4),
                ("MCA-S2-202", "Machine Learning Basics", 4),
                ("MCA-S2-203", "Software Project Management", 3),
                ("MCA-S2-204", "Cyber Security", 3),
                ("MCA-S2-205", "Elective I", 2),
            ],
            3: [
                ("MCA-S3-301", "Big Data Analytics", 4),
                ("MCA-S3-302", "Deep Learning Fundamentals", 4),
                ("MCA-S3-303", "Enterprise Java", 3),
                ("MCA-S3-304", "Microservices Architecture", 3),
                ("MCA-S3-305", "Elective II", 2),
            ],
            4: [
                ("MCA-S4-401", "Major Project", 8),
                ("MCA-S4-402", "Internship", 4),
                ("MCA-S4-403", "Seminar and Viva", 2),
            ],
        },
    },
    "BBA": {
        "department": "Management",
        "semesters": {
            1: [
                ("BBA-S1-101", "Principles of Management", 3),
                ("BBA-S1-102", "Business Communication", 3),
                ("BBA-S1-103", "Financial Accounting", 4),
                ("BBA-S1-104", "Micro Economics", 3),
                ("BBA-S1-105", "Business Mathematics", 3),
            ],
            2: [
                ("BBA-S2-201", "Human Resource Management", 3),
                ("BBA-S2-202", "Marketing Management", 4),
                ("BBA-S2-203", "Business Statistics", 4),
                ("BBA-S2-204", "Macro Economics", 3),
                ("BBA-S2-205", "Computer Applications", 2),
            ],
            3: [
                ("BBA-S3-301", "Operations Management", 4),
                ("BBA-S3-302", "Cost Accounting", 4),
                ("BBA-S3-303", "Business Law", 3),
                ("BBA-S3-304", "Entrepreneurship Development", 3),
                ("BBA-S3-305", "Elective I", 2),
            ],
            4: [
                ("BBA-S4-401", "Financial Management", 4),
                ("BBA-S4-402", "Consumer Behavior", 3),
                ("BBA-S4-403", "Supply Chain Management", 3),
                ("BBA-S4-404", "Management Information Systems", 3),
                ("BBA-S4-405", "Elective II", 2),
            ],
            5: [
                ("BBA-S5-501", "Strategic Management", 4),
                ("BBA-S5-502", "International Business", 4),
                ("BBA-S5-503", "Sales and Distribution", 3),
                ("BBA-S5-504", "Business Analytics", 3),
                ("BBA-S5-505", "Project I", 2),
            ],
            6: [
                ("BBA-S6-601", "Project II", 4),
                ("BBA-S6-602", "Internship", 4),
                ("BBA-S6-603", "Business Ethics", 2),
                ("BBA-S6-604", "Corporate Governance", 2),
                ("BBA-S6-605", "Seminar", 2),
            ],
        },
    },
    "MBA": {
        "department": "Management",
        "semesters": {
            1: [
                ("MBA-S1-101", "Managerial Economics", 4),
                ("MBA-S1-102", "Organizational Behavior", 3),
                ("MBA-S1-103", "Corporate Finance", 4),
                ("MBA-S1-104", "Quantitative Techniques", 3),
                ("MBA-S1-105", "Business Communication", 2),
            ],
            2: [
                ("MBA-S2-201", "Strategic Management", 4),
                ("MBA-S2-202", "Business Analytics", 4),
                ("MBA-S2-203", "Operations Strategy", 3),
                ("MBA-S2-204", "Marketing Strategy", 3),
                ("MBA-S2-205", "Elective I", 2),
            ],
            3: [
                ("MBA-S3-301", "Leadership and Change", 3),
                ("MBA-S3-302", "International Finance", 4),
                ("MBA-S3-303", "Digital Marketing", 3),
                ("MBA-S3-304", "HR Analytics", 3),
                ("MBA-S3-305", "Elective II", 2),
            ],
            4: [
                ("MBA-S4-401", "Capstone Project", 6),
                ("MBA-S4-402", "Internship", 4),
                ("MBA-S4-403", "Seminar and Viva", 2),
            ],
        },
    },
    "BTECH": {
        "department": "Engineering",
        "semesters": {
            1: [
                ("BTECH-S1-101", "Engineering Mathematics I", 4),
                ("BTECH-S1-102", "Engineering Physics", 4),
                ("BTECH-S1-103", "Basic Electrical Engineering", 3),
                ("BTECH-S1-104", "Engineering Graphics", 3),
                ("BTECH-S1-105", "Communication Skills", 2),
            ],
            2: [
                ("BTECH-S2-201", "Data Structures and Algorithms", 4),
                ("BTECH-S2-202", "Object Oriented Programming", 4),
                ("BTECH-S2-203", "Computer Organization", 3),
                ("BTECH-S2-204", "Engineering Mathematics II", 4),
                ("BTECH-S2-205", "Environmental Studies", 2),
            ],
            3: [
                ("BTECH-S3-301", "Database Management Systems", 4),
                ("BTECH-S3-302", "Operating Systems", 4),
                ("BTECH-S3-303", "Software Engineering", 3),
                ("BTECH-S3-304", "Probability and Statistics", 3),
                ("BTECH-S3-305", "Mini Project I", 2),
            ],
            4: [
                ("BTECH-S4-401", "Computer Networks", 4),
                ("BTECH-S4-402", "Theory of Computation", 4),
                ("BTECH-S4-403", "Web Technologies", 3),
                ("BTECH-S4-404", "Microprocessors", 3),
                ("BTECH-S4-405", "Mini Project II", 2),
            ],
            5: [
                ("BTECH-S5-501", "Artificial Intelligence", 4),
                ("BTECH-S5-502", "Machine Learning", 4),
                ("BTECH-S5-503", "Cloud Computing", 3),
                ("BTECH-S5-504", "Compiler Design", 3),
                ("BTECH-S5-505", "Elective I", 2),
            ],
            6: [
                ("BTECH-S6-601", "Big Data Analytics", 4),
                ("BTECH-S6-602", "Internet of Things", 3),
                ("BTECH-S6-603", "DevOps", 3),
                ("BTECH-S6-604", "Information Security", 3),
                ("BTECH-S6-605", "Elective II", 2),
            ],
            7: [
                ("BTECH-S7-701", "Distributed Systems", 4),
                ("BTECH-S7-702", "Mobile Application Development", 3),
                ("BTECH-S7-703", "Data Mining", 3),
                ("BTECH-S7-704", "Project Phase I", 4),
                ("BTECH-S7-705", "Elective III", 2),
            ],
            8: [
                ("BTECH-S8-801", "Project Phase II", 8),
                ("BTECH-S8-802", "Internship", 4),
                ("BTECH-S8-803", "Seminar and Viva", 2),
            ],
        },
    },
    "MTECH": {
        "department": "Engineering",
        "semesters": {
            1: [
                ("MTECH-S1-101", "Research Methodology", 3),
                ("MTECH-S1-102", "Advanced Computing Systems", 4),
                ("MTECH-S1-103", "High Performance Computing", 4),
                ("MTECH-S1-104", "Mathematical Foundations", 3),
                ("MTECH-S1-105", "Technical Writing", 2),
            ],
            2: [
                ("MTECH-S2-201", "Distributed Systems", 4),
                ("MTECH-S2-202", "AI for Engineers", 4),
                ("MTECH-S2-203", "Seminar and Review", 2),
                ("MTECH-S2-204", "Cloud Native Architecture", 3),
                ("MTECH-S2-205", "Elective I", 2),
            ],
            3: [
                ("MTECH-S3-301", "Thesis Phase I", 8),
                ("MTECH-S3-302", "Advanced Elective", 3),
            ],
            4: [
                ("MTECH-S4-401", "Thesis Phase II", 10),
                ("MTECH-S4-402", "Viva Voce", 2),
            ],
        },
    },
    "BHM": {
        "department": "Hospitality",
        "semesters": {
            1: [
                ("BHM-S1-101", "Front Office Operations", 3),
                ("BHM-S1-102", "Food Production Basics", 4),
                ("BHM-S1-103", "Hospitality Communication", 3),
                ("BHM-S1-104", "Housekeeping Fundamentals", 3),
                ("BHM-S1-105", "Nutrition and Hygiene", 2),
            ],
            2: [
                ("BHM-S2-201", "Housekeeping Management", 3),
                ("BHM-S2-202", "Food and Beverage Service", 4),
                ("BHM-S2-203", "Hospitality Marketing", 3),
                ("BHM-S2-204", "Hotel Accounting", 3),
                ("BHM-S2-205", "Computer Applications", 2),
            ],
            3: [
                ("BHM-S3-301", "Food Production Advanced", 4),
                ("BHM-S3-302", "Accommodation Operations", 3),
                ("BHM-S3-303", "Customer Relationship Management", 3),
                ("BHM-S3-304", "Travel and Tourism Basics", 3),
                ("BHM-S3-305", "Event Management", 2),
            ],
            4: [
                ("BHM-S4-401", "Hotel Facility Planning", 3),
                ("BHM-S4-402", "Hospitality HRM", 3),
                ("BHM-S4-403", "Hospitality Law", 3),
                ("BHM-S4-404", "Bakery and Confectionery", 3),
                ("BHM-S4-405", "Elective I", 2),
            ],
            5: [
                ("BHM-S5-501", "Strategic Hospitality Management", 4),
                ("BHM-S5-502", "International Hospitality", 3),
                ("BHM-S5-503", "Revenue Management", 3),
                ("BHM-S5-504", "Internship I", 4),
                ("BHM-S5-505", "Elective II", 2),
            ],
            6: [
                ("BHM-S6-601", "Internship II", 6),
                ("BHM-S6-602", "Project Work", 4),
                ("BHM-S6-603", "Seminar", 2),
                ("BHM-S6-604", "Hospitality Entrepreneurship", 2),
            ],
        },
    },
    "BCOM": {
        "department": "Commerce",
        "semesters": {
            1: [
                ("BCOM-S1-101", "Financial Accounting I", 4),
                ("BCOM-S1-102", "Business Economics I", 4),
                ("BCOM-S1-103", "Business Organization and Management", 4),
                ("BCOM-S1-104", "Business Communication", 3),
                ("BCOM-S1-105", "Environmental Studies", 2),
            ],
            2: [
                ("BCOM-S2-201", "Financial Accounting II", 4),
                ("BCOM-S2-202", "Business Economics II", 4),
                ("BCOM-S2-203", "Corporate Accounting", 4),
                ("BCOM-S2-204", "Business Mathematics", 3),
                ("BCOM-S2-205", "Computer Applications in Business", 3),
            ],
            3: [
                ("BCOM-S3-301", "Cost Accounting", 4),
                ("BCOM-S3-302", "Company Law", 4),
                ("BCOM-S3-303", "Income Tax Law and Practice", 4),
                ("BCOM-S3-304", "Banking and Insurance", 3),
                ("BCOM-S3-305", "Principles of Marketing", 3),
            ],
            4: [
                ("BCOM-S4-401", "Management Accounting", 4),
                ("BCOM-S4-402", "Goods and Services Tax", 4),
                ("BCOM-S4-403", "Auditing Principles and Practice", 4),
                ("BCOM-S4-404", "Human Resource Management", 3),
                ("BCOM-S4-405", "Business Statistics", 3),
            ],
            5: [
                ("BCOM-S5-501", "Financial Management", 4),
                ("BCOM-S5-502", "E Commerce", 3),
                ("BCOM-S5-503", "Entrepreneurship Development", 3),
                ("BCOM-S5-504", "International Business", 4),
                ("BCOM-S5-505", "Retail Management", 3),
            ],
            6: [
                ("BCOM-S6-601", "Advanced Accounting", 4),
                ("BCOM-S6-602", "Business Research Methods", 3),
                ("BCOM-S6-603", "Strategic Management", 4),
                ("BCOM-S6-604", "Project Work", 4),
                ("BCOM-S6-605", "Elective Commerce Paper", 3),
            ],
        },
    },
    "MCOM": {
        "department": "Commerce",
        "semesters": {
            1: [
                ("MCOM-S1-101", "Advanced Corporate Accounting", 4),
                ("MCOM-S1-102", "Managerial Economics", 4),
                ("MCOM-S1-103", "Research Methodology", 3),
                ("MCOM-S1-104", "Business Environment", 3),
            ],
            2: [
                ("MCOM-S2-201", "Advanced Cost and Management Accounting", 4),
                ("MCOM-S2-202", "Financial Markets and Institutions", 4),
                ("MCOM-S2-203", "Income Tax Planning", 3),
                ("MCOM-S2-204", "E Commerce and Digital Business", 3),
            ],
            3: [
                ("MCOM-S3-301", "International Finance", 4),
                ("MCOM-S3-302", "Advanced Auditing", 4),
                ("MCOM-S3-303", "Entrepreneurship and Innovation", 3),
                ("MCOM-S3-304", "Elective I", 3),
            ],
            4: [
                ("MCOM-S4-401", "Dissertation", 8),
                ("MCOM-S4-402", "Seminar and Viva", 2),
                ("MCOM-S4-403", "Elective II", 3),
            ],
        },
    },
}


CONTENT_TYPES = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
</Types>
"""

RELS = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
</Relationships>
"""


def xml_escape(value: str) -> str:
    return (
        value.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace('"', "&quot;")
        .replace("'", "&apos;")
    )


def doc_xml(program: str, department: str, semesters: dict[int, list[tuple[str, str, int]]]) -> str:
    lines: list[str] = [
        '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>',
        '<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">',
        "  <w:body>",
        f"    <w:p><w:r><w:t>{xml_escape(program)} TEACHING SCHEMA</w:t></w:r></w:p>",
        f"    <w:p><w:r><w:t>Department: {xml_escape(department)}</w:t></w:r></w:p>",
        f"    <w:p><w:r><w:t>Program: {xml_escape(program)}</w:t></w:r></w:p>",
    ]

    for sem in sorted(semesters.keys()):
        lines.append(f"    <w:p><w:r><w:t>SEMESTER {sem}</w:t></w:r></w:p>")
        for code, name, credits in semesters[sem]:
            row = f"{code} {name} Credits: {credits}"
            lines.append(f"    <w:p><w:r><w:t>{xml_escape(row)}</w:t></w:r></w:p>")

    lines.extend(
        [
            "    <w:sectPr/>",
            "  </w:body>",
            "</w:document>",
        ]
    )
    return "\n".join(lines) + "\n"


def write_docx(path: Path, doc_text: str) -> None:
    if path.exists():
        path.unlink()
    with zipfile.ZipFile(path, "w", compression=zipfile.ZIP_DEFLATED) as zf:
        zf.writestr("[Content_Types].xml", CONTENT_TYPES)
        zf.writestr("_rels/.rels", RELS)
        zf.writestr("word/document.xml", doc_text)


def main() -> None:
    base = Path("d:/proj/CCRS/SCRS")
    out_dir = base / "sample_docs" / "all_course_teaching_schemas"
    downloads_dir = Path("C:/Users/ADMIN/Downloads/all_course_teaching_schemas")
    out_dir.mkdir(parents=True, exist_ok=True)
    downloads_dir.mkdir(parents=True, exist_ok=True)

    generated: list[Path] = []
    for program, cfg in PROGRAMS.items():
        file_name = f"{program}_Teaching_Schema.docx"
        file_path = out_dir / file_name
        document_xml = doc_xml(program, cfg["department"], cfg["semesters"])
        write_docx(file_path, document_xml)
        shutil.copy2(file_path, downloads_dir / file_name)
        generated.append(file_path)

    print("Generated files:")
    for path in generated:
        print(path)
    print(f"Downloads copy: {downloads_dir}")


if __name__ == "__main__":
    main()
