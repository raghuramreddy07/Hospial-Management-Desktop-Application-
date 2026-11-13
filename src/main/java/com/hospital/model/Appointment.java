package com.hospital.model;

import java.time.LocalDate;
import java.time.LocalTime;

/** A scheduled visit with database IDs and optional joined display names. */
public class Appointment {
    private int id, doctorId, patientId;
    private LocalDate date;
    private LocalTime startTime, endTime;
    private AppointmentStatus status;
    private String reason, doctorName, patientName;
    public Appointment(int id, int doctorId, int patientId, LocalDate date, LocalTime startTime, LocalTime endTime,
                       AppointmentStatus status, String reason) {
        this.id = id; this.doctorId = doctorId; this.patientId = patientId; this.date = date;
        this.startTime = startTime; this.endTime = endTime; this.status = status; this.reason = reason;
    }
    public int getId() { return id; } public void setId(int v) { id = v; }
    public int getDoctorId() { return doctorId; } public void setDoctorId(int v) { doctorId = v; }
    public int getPatientId() { return patientId; } public void setPatientId(int v) { patientId = v; }
    public LocalDate getDate() { return date; } public void setDate(LocalDate v) { date = v; }
    public LocalTime getStartTime() { return startTime; } public void setStartTime(LocalTime v) { startTime = v; }
    public LocalTime getEndTime() { return endTime; } public void setEndTime(LocalTime v) { endTime = v; }
    public AppointmentStatus getStatus() { return status; } public void setStatus(AppointmentStatus v) { status = v; }
    public String getReason() { return reason; } public void setReason(String v) { reason = v; }
    public String getDoctorName() { return doctorName; } public void setDoctorName(String v) { doctorName = v; }
    public String getPatientName() { return patientName; } public void setPatientName(String v) { patientName = v; }
}
