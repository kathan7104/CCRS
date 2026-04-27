from pathlib import Path
from zipfile import ZipFile, ZIP_DEFLATED

CONTENT_TYPES = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
</Types>
'''

RELS = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
</Relationships>
'''

PROGRAM = "NURSING"
DEPARTMENT = "Nursing & Health Sciences"
SEMESTERS = {
    1: [
        ("NURS-S1-101", "Anatomy & Physiology I", 4),
        ("NURS-S1-102", "Fundamentals of Nursing I", 4),
        ("NURS-S1-103", "Psychology for Nurses", 3),
        ("NURS-S1-104", "Nutrition & Dietetics", 3),
        ("NURS-S1-105", "English Communication", 2),
    ],
    2: [
        ("NURS-S2-201", "Anatomy & Physiology II", 4),
        ("NURS-S2-202", "Fundamentals of Nursing II", 4),
        ("NURS-S2-203", "Sociology for Health", 3),
        ("NURS-S2-204", "Computer Applications in Nursing", 2),
        ("NURS-S2-205", "Environmental Health", 2),
    ],
    3: [
        ("NURS-S3-301", "Medical-Surgical Nursing I", 4),
        ("NURS-S3-302", "Microbiology for Nurses", 3),
        ("NURS-S3-303", "Pharmacology for Nurses", 3),
        ("NURS-S3-304", "Nutrition in Health Care", 2),
        ("NURS-S3-305", "Health Assessment", 2),
    ],
    4: [
        ("NURS-S4-401", "Medical-Surgical Nursing II", 4),
        ("NURS-S4-402", "Community Health Nursing I", 4),
        ("NURS-S4-403", "Mental Health Nursing", 3),
        ("NURS-S4-404", "Pathophysiology", 3),
        ("NURS-S4-405", "Professional Ethics", 2),
    ],
    5: [
        ("NURS-S5-501", "Child Health Nursing", 4),
        ("NURS-S5-502", "Obstetrics and Gynecological Nursing", 4),
        ("NURS-S5-503", "Research in Nursing", 3),
        ("NURS-S5-504", "Health Education & Communication", 2),
        ("NURS-S5-505", "Applied Pharmacology", 2),
    ],
    6: [
        ("NURS-S6-601", "Community Health Nursing II", 4),
        ("NURS-S6-602", "Leadership and Management in Nursing", 4),
        ("NURS-S6-603", "Emergency and Critical Care Nursing", 3),
        ("NURS-S6-604", "Rehabilitation Nursing", 3),
        ("NURS-S6-605", "Elective I", 2),
    ],
    7: [
        ("NURS-S7-701", "Nursing Informatics", 3),
        ("NURS-S7-702", "Advanced Clinical Nursing Practice", 4),
        ("NURS-S7-703", "Geriatric Nursing", 3),
        ("NURS-S7-704", "Nursing Administration", 3),
        ("NURS-S7-705", "Elective II", 2),
    ],
    8: [
        ("NURS-S8-801", "Project in Nursing", 6),
        ("NURS-S8-802", "Internship/Clinical Practice", 6),
        ("NURS-S8-803", "Seminar and Viva", 2),
    ],
}


def xml_escape(value: str) -> str:
    return (
        value.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace('"', "&quot;")
        .replace("'", "&apos;")
    )


def build_document_xml() -> str:
    lines = [
        '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>',
        '<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">',
        '  <w:body>',
        f'    <w:p><w:r><w:t>{xml_escape(PROGRAM)} TEACHING SCHEMA</w:t></w:r></w:p>',
        f'    <w:p><w:r><w:t>Department: {xml_escape(DEPARTMENT)}</w:t></w:r></w:p>',
        f'    <w:p><w:r><w:t>Program: {xml_escape(PROGRAM)}</w:t></w:r></w:p>',
    ]

    for sem in sorted(SEMESTERS.keys()):
        lines.append(f'    <w:p><w:r><w:t>SEMESTER {sem}</w:t></w:r></w:p>')
        for code, title, credits in SEMESTERS[sem]:
            lines.append(f'    <w:p><w:r><w:t>{xml_escape(code)} {xml_escape(title)} Credits: {credits}</w:t></w:r></w:p>')

    lines.extend(['    <w:sectPr/>', '  </w:body>', '</w:document>'])
    return "\n".join(lines) + "\n"


def write_docx(path: Path, document_xml: str) -> None:
    with ZipFile(path, "w", ZIP_DEFLATED) as zf:
        zf.writestr("[Content_Types].xml", CONTENT_TYPES)
        zf.writestr("_rels/.rels", RELS)
        zf.writestr("word/document.xml", document_xml)


if __name__ == "__main__":
    out_dir = Path("d:/proj/CCRS/SCRS/sample_docs/all_course_teaching_schemas")
    out_dir.mkdir(parents=True, exist_ok=True)
    out_file = out_dir / "NURSING_Teaching_Schema.docx"
    write_docx(out_file, build_document_xml())
    print(f"Created: {out_file}")
