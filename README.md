# Hospital Appointment Management System

A standalone Java Swing desktop application for hospital receptionists and administrative staff to manage doctors, patients, and appointments. It uses SQLite for local persistence and prevents overlapping doctor or patient bookings.

## Requirements

- Java 17 or newer
- Maven 3.8 or newer (to build and test)

## Build, test, and run

```sh
mvn clean package
mvn test
java -jar target/hospital-app.jar
```

Or use `run.sh` on Linux/macOS, or `run.bat` on Windows. The executable shaded JAR includes SQLite JDBC.

**Default login:** username `admin`, password `admin123`.

The application creates `hospital.db` automatically beside the executable JAR (in `target/` for the command above). That folder must be writable. Doctor and patient sample records are inserted only when their respective tables are empty. Deleting a doctor or patient also removes their cancelled/completed appointment history; deletion is blocked while any linked appointment is still booked.

## Features

- Create, edit, search, and delete doctor and patient records.
- Book, reschedule, cancel, and complete appointments.
- Prevent overlapping bookings for both doctors and patients; cancellation preserves the appointment row and releases the time slot.
- Validate phone numbers, work hours, date/time ranges, and required fields.
- Filter schedules by date, doctor, or patient; jump to today's appointments.
- Export a day's schedule to CSV.
- Local-first SQLite persistence; no web UI, server, or patient-facing access.

## Project structure

```text
src/main/java/com/hospital/
  Main.java
  model/       Person, Doctor, Patient, Appointment, AppointmentStatus
  dao/         DAO contracts and SQLite implementations
  service/     Validation and business rules
  exception/   Checked business exceptions
  util/        SQLite initialization and date/time helpers
  ui/          Login dialog, main frame, and Swing panels
src/test/java/com/hospital/service/
  AppointmentServiceTest.java
docs/
  uml-class-diagram.puml
  er-diagram.md
  test-checklist.md
  project-report-outline.md
```

## Data and security notes

The local database contains sensitive patient information and should be protected using the operating system's file permissions and backups. The default credentials are intended for a course demonstration; change or replace them before any real-world use. This single-user desktop project is not designed for clinical or production use.
