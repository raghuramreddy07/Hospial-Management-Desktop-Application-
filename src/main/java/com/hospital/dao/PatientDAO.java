package com.hospital.dao;

import com.hospital.model.Patient;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Persistence contract for patient records. */
public interface PatientDAO {
    List<Patient> findAll(String name) throws SQLException;
    Optional<Patient> findById(int id) throws SQLException;
    Patient save(Patient patient) throws SQLException;
    void delete(int id) throws SQLException;
    boolean hasBookedAppointments(int id) throws SQLException;
}
