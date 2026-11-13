# Project report outline

## 1. Problem statement

Manual appointment scheduling is susceptible to duplicate bookings, inconsistent records, and slow retrieval. Reception staff need a simple local desktop tool to manage schedules on patients' behalf.

## 2. Objectives

- Provide receptionist-only management of doctors, patients, and appointments.
- Prevent overlapping bookings for either a doctor or a patient.
- Validate appointment times against past dates and doctor working hours.
- Persist records across application restarts and support schedule search and export.

## 3. Scope

The application is a standalone, single-user Swing client backed by a local SQLite database. Patients do not log in or interact with the application. Online booking, multi-site coordination, and clinical records are outside the current scope.

## 4. Architecture

Describe the strict UI → Service → DAO → SQLite layering. The Swing panels collect inputs and display service outcomes; service classes own validation and conflict rules; JDBC DAO implementations perform parameterized persistence. SQLite triggers provide a final overlap guard for concurrent writes.

## 5. Technologies

Java 17, Swing, JDBC, SQLite (`org.xerial:sqlite-jdbc`), `java.time`, Maven, JUnit 5, and Maven Shade Plugin.

## 6. Data model

Summarize Doctor, Patient, Appointment, AppointmentStatus, and staff users. Include the ER diagram from `er-diagram.md`; explain ISO-formatted date/time storage and foreign keys.

## 7. User interface and workflow

Describe staff login, Doctor and Patient maintenance, appointment booking/rescheduling/cancellation/completion, schedule filters, today's view, and CSV export. Include screenshots captured during the live demonstration.

## 8. Testing

Report the automated `AppointmentServiceTest` results covering exact, partial, nested, back-to-back, separate doctor/date, cancelled, patient overlap, and self-excluding reschedule cases. Record manual checks from `test-checklist.md`.

## 9. Limitations and future enhancements

- Replace demonstration credentials with managed, salted password hashes and role-based permissions.
- Add audit history, database backup/restore, localization, and stronger accessibility support.
- For multi-user use, move to a server database with transactional scheduling and authentication.
- A future patient web portal could allow patients to request appointments, subject to privacy, identity, and clinical security controls.

## 10. Conclusion

Summarize how the implemented local desktop application meets its objectives and the results of automated and manual tests.
