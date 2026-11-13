package com.hospital.model;

import java.time.LocalTime;

/** A doctor and their appointment availability. */
public class Doctor extends Person {
    private String specialization;
    private String email;
    private LocalTime workStart;
    private LocalTime workEnd;
    public Doctor(int id, String name, String phone, String specialization, String email, LocalTime workStart, LocalTime workEnd) {
        super(id, name, phone); this.specialization = specialization; this.email = email; this.workStart = workStart; this.workEnd = workEnd;
    }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String value) { specialization = value; }
    public String getEmail() { return email; }
    public void setEmail(String value) { email = value; }
    public LocalTime getWorkStart() { return workStart; }
    public void setWorkStart(LocalTime value) { workStart = value; }
    public LocalTime getWorkEnd() { return workEnd; }
    public void setWorkEnd(LocalTime value) { workEnd = value; }
    @Override public String toString() { return getName() + " (" + specialization + ")"; }
}
