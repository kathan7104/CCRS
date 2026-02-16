-- Schema for CCRS: Courses, Enrollments, Invoices, InvoiceItems, Payments
-- Assumes existing tables: users, user_roles, otp_verifications
ALTER TABLE users ADD COLUMN IF NOT EXISTS department VARCHAR(100);

CREATE TABLE IF NOT EXISTS courses (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(50) NOT NULL UNIQUE,
  name VARCHAR(255) NOT NULL,
  department VARCHAR(100) NOT NULL,
  capacity INT NOT NULL,
  remaining_seats INT NOT NULL,
  credits INT NOT NULL,
  fee INT NOT NULL,
  program_level VARCHAR(20) NOT NULL,
  level VARCHAR(20) NOT NULL,
  duration_years INT NOT NULL,
  required_qualification VARCHAR(255) NOT NULL,
  version BIGINT,
  created_at DATETIME NOT NULL,
  updated_at DATETIME,
  INDEX idx_course_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS course_prerequisites (
  course_id BIGINT NOT NULL,
  prerequisite_id BIGINT NOT NULL,
  PRIMARY KEY (course_id, prerequisite_id),
  CONSTRAINT fk_cp_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE,
  CONSTRAINT fk_cp_prerequisite FOREIGN KEY (prerequisite_id) REFERENCES courses (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS enrollments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  student_id BIGINT NOT NULL,
  course_id BIGINT NOT NULL,
  status VARCHAR(20) NOT NULL,
  registered_at DATETIME NOT NULL,
  finalized_at DATETIME,
  CONSTRAINT uk_student_course UNIQUE (student_id, course_id),
  INDEX idx_enrollment_student (student_id),
  INDEX idx_enrollment_course (course_id),
  CONSTRAINT fk_enrollment_student FOREIGN KEY (student_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_enrollment_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS invoices (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  invoice_number VARCHAR(100) NOT NULL UNIQUE,
  student_id BIGINT NOT NULL,
  total_amount DECIMAL(16,2) NOT NULL DEFAULT 0,
  status VARCHAR(20) NOT NULL,
  issued_at DATETIME NOT NULL,
  due_date DATETIME,
  INDEX idx_invoice_number (invoice_number),
  CONSTRAINT fk_invoice_student FOREIGN KEY (student_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS invoice_items (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  invoice_id BIGINT NOT NULL,
  course_id BIGINT,
  description VARCHAR(255),
  amount DECIMAL(16,2) NOT NULL DEFAULT 0,
  INDEX idx_invoice_item_invoice (invoice_id),
  CONSTRAINT fk_invoice_item_invoice FOREIGN KEY (invoice_id) REFERENCES invoices (id) ON DELETE CASCADE,
  CONSTRAINT fk_invoice_item_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS payments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  invoice_id BIGINT NOT NULL,
  amount DECIMAL(16,2) NOT NULL DEFAULT 0,
  method VARCHAR(20) NOT NULL,
  transaction_id VARCHAR(255),
  status VARCHAR(20) NOT NULL,
  paid_at DATETIME,
  INDEX idx_payment_tx (transaction_id),
  CONSTRAINT fk_payment_invoice FOREIGN KEY (invoice_id) REFERENCES invoices (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS fee_structures (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  cost_per_credit DECIMAL(16,2) NOT NULL DEFAULT 0,
  lab_fee DECIMAL(16,2) NOT NULL DEFAULT 0,
  differential_fee DECIMAL(16,2) NOT NULL DEFAULT 0,
  late_penalty DECIMAL(16,2) NOT NULL DEFAULT 0,
  effective_from DATE NOT NULL,
  active BIT NOT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS fee_structure_audit_logs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  fee_structure_id BIGINT,
  action VARCHAR(20) NOT NULL,
  changed_by VARCHAR(255) NOT NULL,
  change_summary VARCHAR(1000) NOT NULL,
  changed_at DATETIME NOT NULL,
  CONSTRAINT fk_fee_audit_structure FOREIGN KEY (fee_structure_id) REFERENCES fee_structures (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS faculty_course_assignments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  faculty_id BIGINT NOT NULL,
  course_id BIGINT NOT NULL,
  assigned_at DATETIME NOT NULL,
  CONSTRAINT uk_faculty_course UNIQUE (faculty_id, course_id),
  CONSTRAINT fk_assignment_faculty FOREIGN KEY (faculty_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_assignment_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
