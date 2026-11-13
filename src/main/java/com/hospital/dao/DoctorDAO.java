package com.hospital.dao;

import com.hospital.model.Doctor;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Persistence contract for doctor records. */
public interface DoctorDAO {
    List<Doctor> findAll(String name) throws SQLException;
    Optional<Doctor> findById(int id) throws SQLException;
    Doctor save(Doctor doctor) throws SQLException;
    void delete(int id) throws SQLException;
    boolean hasBookedAppointments(int id) throws SQLException;
}
