package com.hospital.dao;

import com.hospital.model.Appointment;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Persistence contract for appointments and schedule queries. */
public interface AppointmentDAO {
    List<Appointment> findAll(LocalDate date, String doctorName, String patientName) throws SQLException;
    List<Appointment> findByDoctorAndDate(int doctorId, LocalDate date) throws SQLException;
    List<Appointment> findByPatientAndDate(int patientId, LocalDate date) throws SQLException;
    Optional<Appointment> findById(int id) throws SQLException;
    Appointment save(Appointment appointment) throws SQLException;
    void cancel(int id) throws SQLException;
}
