package com.hospital.dao;

import com.hospital.model.Doctor;
import com.hospital.util.DBConnection;
import java.sql.*;
import java.time.LocalTime;
import java.util.*;

/** SQLite implementation of doctor persistence. */
public class JdbcDoctorDAO implements DoctorDAO {
    private Doctor map(ResultSet rs) throws SQLException {
        return new Doctor(rs.getInt("id"), rs.getString("name"), rs.getString("phone"), rs.getString("specialization"),
                rs.getString("email"), LocalTime.parse(rs.getString("work_start")), LocalTime.parse(rs.getString("work_end")));
    }
    @Override public List<Doctor> findAll(String name) throws SQLException {
        List<Doctor> result = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM doctor WHERE lower(name) LIKE lower(?) ORDER BY name")) {
            ps.setString(1, "%" + (name == null ? "" : name.trim()) + "%");
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) result.add(map(rs)); }
        }
        return result;
    }
    @Override public Optional<Doctor> findById(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM doctor WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? Optional.of(map(rs)) : Optional.empty(); }
        }
    }
    @Override public Doctor save(Doctor d) throws SQLException {
        String sql = d.getId() == 0 ? "INSERT INTO doctor(name,phone,specialization,email,work_start,work_end) VALUES(?,?,?,?,?,?)"
                : "UPDATE doctor SET name=?,phone=?,specialization=?,email=?,work_start=?,work_end=? WHERE id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,d.getName()); ps.setString(2,d.getPhone()); ps.setString(3,d.getSpecialization()); ps.setString(4,d.getEmail());
            ps.setString(5,d.getWorkStart().toString()); ps.setString(6,d.getWorkEnd().toString());
            if (d.getId() != 0) ps.setInt(7,d.getId());
            ps.executeUpdate();
            if (d.getId() == 0) try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) d.setId(keys.getInt(1)); }
        }
        return d;
    }
    @Override public void delete(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM doctor WHERE id=?")) { ps.setInt(1,id); ps.executeUpdate(); }
    }
    @Override public boolean hasBookedAppointments(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT 1 FROM appointment WHERE doctor_id=? AND status='BOOKED' LIMIT 1")) {
            ps.setInt(1,id); try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }
}
