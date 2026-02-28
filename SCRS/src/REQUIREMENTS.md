# Requirements Analysis

The Requirements Analysis phase translates the findings of the problem analysis and feasibility study into precise, measurable specifications for the development team.

## 3.1 Functional Requirements (FRs)

Functional requirements define what the system must do to support the business operations, focusing on the core transactional and management features.

The following list incorporates changes regarding the centralized Admin Super User, the department-specific Director role, and the revised permissions for the Faculty role.

| ID | Module | Requirement Description (Use Case) | Justification/Link to Problem |
| :--- | :--- | :--- | :--- |
| **FR-S1** | Student | **Secure Login & Authentication** for all users (Student, Admin, Faculty). This must include robust credential checking and session management. | Security and Data Integrity. |
| **FR-S2** | Student | **Enrollment Engine**: Allow a student to add/Drop courses. Must perform an instant seat availability check prior to confirmation, acting as a pre-check to the final transactional lock. | Real-Time Concurrency and Overbooking Prevention ("Race Condition"). |
| **FR-S3** | Student | **Prerequisite Validation**: System must check a student's academic history against course requirements before enabling registration to maintain academic compliance. | Data Integrity and Academic Compliance. |
| **FR-S4** | Fee/Payment | **Automated Invoicing**: System must generate a real-time, itemised bill based on selected courses, factoring in credits, mandatory lab fees, differential fees, and late penalties. | Eliminate Complex Fee Calculation Errors and manual financial discrepancies. |
| **FR-S5** | Fee/Payment | **Integrated Payment Processing**: Allow students to view pending dues and complete payments via a secure online gateway (simulated or sandbox for initial deployment). | Unified Workflow and Operational Efficiency addresses the disjointed payment process. |
| **FR-S6** | Fee/Payment | **Enrollment Finalisation**: Official course enrollment status must be locked only upon successful payment transaction confirmation, ensuring no provisional seats are held indefinitely. | Unified Workflow: Payment transaction serves as the final confirmation of seat reservation. |
| **FR-A1** | Admin | **Fee Structure Management**: Allow administrators to define and modify cost-per-credit, lab fees, and penalty structures with audit logging. (Centralized Function) | System flexibility and Financial Control over revenue streams. |
| **FR-A3** | Admin | **Reporting Generation**: Generate standard reports ("Unpaid Students," "Enrollment Stats," "Total Revenue") for management analysis and strategic decision-making. | Operational visibility and Economic Reporting. |
| **FR-A4** | Admin | **Super User Management**: Allow the system administrator to manage (add/edit/delete) all high-level user accounts, specifically the Department Director and other Admin accounts. | Centralized High-Level Control (The ultimate authority for managing Department Leads). |
| **FR-D1** | Director | **Department Course Catalogue Management**: Allow Department Directors to add, edit, or delete courses, define course capacity, and set prerequisites only for their assigned department's catalogue. | Granular Control, Departmental Autonomy. |
| **FR-D2** | Director | **Department Staff & Student Management**: Allow Department Directors to manage (add/edit/delete) Faculty and Student accounts within their assigned department and assign courses to faculty. | Departmental Operations and Staff Onboarding/Offboarding. |
| **FR-F1** | Faculty | **Departmental Class Roster View**: Allow faculty to view a list of officially enrolled and paid students for their assigned courses. This view is strictly scoped to the Faculty member's assigned courses and department. | Operational Efficiency (Faculty) and data accuracy for class instruction, ensured by departmental scoping. |
