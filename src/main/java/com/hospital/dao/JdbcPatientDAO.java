package com.hospital.dao;

import com.hospital.model.Patient;
import com.hospital.util.DBConnection;
import java.sql.*;
import java.util.*;

/** SQLite implementation of patient persistence. */
public class JdbcPatientDAO implements PatientDAO {
    private Patient map(ResultSet rs) throws SQLException {
        return new Patient(rs.getInt("id"),rs.getString("name"),rs.getString("phone"),rs.getInt("age"),rs.getString("gender"),rs.getString("address"));
    }
    @Override public List<Patient> findAll(String name) throws SQLException {
        List<Patient> result = new ArrayList<>();
        try (Connection c=DBConnection.getConnection(); PreparedStatement ps=c.prepareStatement("SELECT * FROM patient WHERE lower(name) LIKE lower(?) ORDER BY name")) {
            ps.setString(1,"%" + (name == null ? "" : name.trim()) + "%");
            try (ResultSet rs=ps.executeQuery()) { while (rs.next()) result.add(map(rs)); }
        }
        return result;
    }
    @Override public Optional<Patient> findById(int id) throws SQLException {
        try (Connection c=DBConnection.getConnection(); PreparedStatement ps=c.prepareStatement("SELECT * FROM patient WHERE id=?")) {
            ps.setInt(1,id); try (ResultSet rs=ps.executeQuery()) { return rs.next()?Optional.of(map(rs)):Optional.empty(); }
        }
    }
    @Override public Patient save(Patient p) throws SQLException {
        String sql=p.getId()==0?"INSERT INTO patient(name,phone,age,gender,address) VALUES(?,?,?,?,?)":"UPDATE patient SET name=?,phone=?,age=?,gender=?,address=? WHERE id=?";
        try (Connection c=DBConnection.getConnection(); PreparedStatement ps=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,p.getName()); ps.setString(2,p.getPhone()); ps.setInt(3,p.getAge()); ps.setString(4,p.getGender()); ps.setString(5,p.getAddress());
            if(p.getId()!=0) ps.setInt(6,p.getId());
            ps.executeUpdate();
            if(p.getId()==0) try(ResultSet keys=ps.getGeneratedKeys()){if(keys.next())p.setId(keys.getInt(1));}
        }
        return p;
    }
    @Override public void delete(int id) throws SQLException {
        try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement("DELETE FROM patient WHERE id=?")){ps.setInt(1,id);ps.executeUpdate();}
    }
    @Override public boolean hasBookedAppointments(int id) throws SQLException {
        try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement("SELECT 1 FROM appointment WHERE patient_id=? AND status='BOOKED' LIMIT 1")){
            ps.setInt(1,id);try(ResultSet rs=ps.executeQuery()){return rs.next();}
        }
    }
}
