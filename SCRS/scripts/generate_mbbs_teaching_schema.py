from pathlib import Path
import zipfile

CONTENT_TYPES = """<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>
<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">
  <Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>
  <Default Extension=\"xml\" ContentType=\"application/xml\"/>
  <Override PartName=\"/word/document.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml\"/>
</Types>
"""

RELS = """<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>
<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">
  <Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"word/document.xml\"/>
</Relationships>
"""

MBBS_SEMESTERS = {
    1: [
        ("MBBS-S1-101", "Human Anatomy I", 6),
        ("MBBS-S1-102", "Human Physiology I", 5),
        ("MBBS-S1-103", "Biochemistry I", 4),
        ("MBBS-S1-104", "Professional Development & Ethics I", 2),
    ],
    2: [
        ("MBBS-S2-201", "Human Anatomy II", 6),
        ("MBBS-S2-202", "Human Physiology II", 5),
        ("MBBS-S2-203", "Biochemistry II", 4),
        ("MBBS-S2-204", "Early Clinical Exposure I", 2),
    ],
    3: [
        ("MBBS-S3-301", "Pathology I", 5),
        ("MBBS-S3-302", "Pharmacology I", 5),
        ("MBBS-S3-303", "Microbiology I", 4),
        ("MBBS-S3-304", "Forensic Medicine I", 3),
    ],
    4: [
        ("MBBS-S4-401", "Pathology II", 5),
        ("MBBS-S4-402", "Pharmacology II", 5),
        ("MBBS-S4-403", "Microbiology II", 4),
        ("MBBS-S4-404", "Forensic Medicine II", 3),
        ("MBBS-S4-405", "Clinical Skills Lab I", 2),
    ],
    5: [
        ("MBBS-S5-501", "Community Medicine I", 4),
        ("MBBS-S5-502", "Ophthalmology", 3),
        ("MBBS-S5-503", "Otorhinolaryngology (ENT)", 3),
        ("MBBS-S5-504", "General Medicine I", 5),
        ("MBBS-S5-505", "General Surgery I", 5),
    ],
    6: [
        ("MBBS-S6-601", "Community Medicine II", 4),
        ("MBBS-S6-602", "General Medicine II", 5),
        ("MBBS-S6-603", "General Surgery II", 5),
        ("MBBS-S6-604", "Orthopedics", 3),
        ("MBBS-S6-605", "Radiodiagnosis Basics", 2),
    ],
    7: [
        ("MBBS-S7-701", "Obstetrics & Gynecology I", 5),
        ("MBBS-S7-702", "Pediatrics I", 4),
        ("MBBS-S7-703", "Dermatology", 2),
        ("MBBS-S7-704", "Psychiatry", 2),
        ("MBBS-S7-705", "Emergency Medicine & Critical Care I", 3),
    ],
    8: [
        ("MBBS-S8-801", "Obstetrics & Gynecology II", 5),
        ("MBBS-S8-802", "Pediatrics II", 4),
        ("MBBS-S8-803", "General Medicine III", 5),
        ("MBBS-S8-804", "General Surgery III", 5),
        ("MBBS-S8-805", "Emergency Medicine & Critical Care II", 3),
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


def doc_xml() -> str:
    lines = [
        '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>',
        '<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">',
        '  <w:body>',
        '    <w:p><w:r><w:t>BACHELOR OF MEDICINE AND BACHELOR OF SURGERY (MBBS) TEACHING SCHEMA</w:t></w:r></w:p>',
        '    <w:p><w:r><w:t>Department: Medical Sciences</w:t></w:r></w:p>',
        '    <w:p><w:r><w:t>Program: MBBS</w:t></w:r></w:p>',
        '    <w:p><w:r><w:t>Duration: 8 Semesters</w:t></w:r></w:p>',
    ]

    for sem in sorted(MBBS_SEMESTERS.keys()):
        lines.append(f'    <w:p><w:r><w:t>SEMESTER {sem}</w:t></w:r></w:p>')
        for code, name, credits in MBBS_SEMESTERS[sem]:
            row = f"{code} {name} Credits: {credits}"
            lines.append(f'    <w:p><w:r><w:t>{xml_escape(row)}</w:t></w:r></w:p>')

    lines.extend([
        '    <w:sectPr/>',
        '  </w:body>',
        '</w:document>',
    ])
    return "\n".join(lines) + "\n"


def write_docx(path: Path, document_xml: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    if path.exists():
        path.unlink()
    with zipfile.ZipFile(path, "w", compression=zipfile.ZIP_DEFLATED) as zf:
        zf.writestr("[Content_Types].xml", CONTENT_TYPES)
        zf.writestr("_rels/.rels", RELS)
        zf.writestr("word/document.xml", document_xml)


if __name__ == "__main__":
    out = Path("d:/proj/CCRS/SCRS/sample_docs/all_course_teaching_schemas/MBBS_Teaching_Schema.docx")
    write_docx(out, doc_xml())
    print(out)
