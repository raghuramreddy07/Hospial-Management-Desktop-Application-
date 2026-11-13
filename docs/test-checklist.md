# Manual demonstration checklist

## Startup and records

- [ ] Run the executable JAR and verify the login screen appears.
- [ ] Sign in with `admin` / `admin123`; verify incorrect credentials are rejected.
- [ ] Confirm the first launch creates the local database and sample doctor/patient records.
- [ ] Add a doctor with working hours and a patient with a valid phone number.
- [ ] Edit each record and confirm the table reflects the changes.
- [ ] Search doctors and patients by partial name.
- [ ] Try an invalid phone number and confirm a human-readable validation message.
- [ ] Try deleting a doctor or patient with appointment history and confirm deletion is blocked.

## Scheduling and conflict prevention

- [ ] Book a future appointment from 10:00 to 10:30 for a doctor and patient.
- [ ] Attempt 10:15 to 10:45 for the same doctor; confirm a conflict message names the doctor and occupied time.
- [ ] Book 10:30 to 11:00 for the same doctor and a different patient; confirm back-to-back bookings are accepted.
- [ ] Attempt an overlapping appointment for the same patient with another doctor; confirm it is rejected.
- [ ] Attempt to book outside the doctor's working hours, in the past, or with an end time before the start; confirm validation.
- [ ] Reschedule an appointment to an available slot; confirm its former slot becomes available.
- [ ] Cancel an appointment and verify it remains in the table as `CANCELLED` and its slot can be reused.
- [ ] Mark an appointment completed and verify its status is `COMPLETED`.

## Search, export, and persistence

- [ ] Filter appointments by date, doctor name, and patient name.
- [ ] Use **Today's appointments** and **Show all**.
- [ ] Export a selected day's schedule to CSV and inspect the headers and values.
- [ ] Close and reopen the application; verify records and appointment statuses persist.
