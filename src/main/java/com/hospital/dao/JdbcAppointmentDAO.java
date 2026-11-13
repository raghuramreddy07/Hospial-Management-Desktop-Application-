package com.hospital.dao;

import com.hospital.model.*;
import com.hospital.util.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/** SQLite implementation with parameterized joined schedule queries. */
public class JdbcAppointmentDAO implements AppointmentDAO {
    private static final String SELECT = "SELECT a.*,d.name doctor_name,p.name patient_name FROM appointment a JOIN doctor d ON d.id=a.doctor_id JOIN patient p ON p.id=a.patient_id";
    private Appointment map(ResultSet rs)throws SQLException{
        Appointment a=new Appointment(rs.getInt("id"),rs.getInt("doctor_id"),rs.getInt("patient_id"),
                LocalDate.parse(rs.getString("appointment_date")),LocalTime.parse(rs.getString("start_time")),
                LocalTime.parse(rs.getString("end_time")),AppointmentStatus.valueOf(rs.getString("status")),rs.getString("reason"));
        a.setDoctorName(rs.getString("doctor_name"));a.setPatientName(rs.getString("patient_name"));return a;
    }
    @Override public List<Appointment> findAll(LocalDate date,String doctor,String patient)throws SQLException{
        StringBuilder sql=new StringBuilder(SELECT+" WHERE 1=1");
        if(date!=null)sql.append(" AND a.appointment_date=?");
        sql.append(" AND lower(d.name) LIKE lower(?) AND lower(p.name) LIKE lower(?) ORDER BY a.appointment_date,a.start_time");
        List<Appointment> result=new ArrayList<>();
        try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement(sql.toString())){
            int i=1;if(date!=null)ps.setString(i++,date.toString());ps.setString(i++,"%"+(doctor==null?"":doctor.trim())+"%");ps.setString(i,"%"+(patient==null?"":patient.trim())+"%");
            try(ResultSet rs=ps.executeQuery()){while(rs.next())result.add(map(rs));}
        }return result;
    }
    @Override public List<Appointment> findByDoctorAndDate(int doctorId,LocalDate date)throws SQLException{return findConflicts("a.doctor_id=?",doctorId,date);}
    @Override public List<Appointment> findByPatientAndDate(int patientId,LocalDate date)throws SQLException{return findConflicts("a.patient_id=?",patientId,date);}
    private List<Appointment> findConflicts(String column,int id,LocalDate date)throws SQLException{
        List<Appointment> result=new ArrayList<>();
        try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement(SELECT+" WHERE "+column+" AND a.appointment_date=? AND a.status='BOOKED' ORDER BY a.start_time")){
            ps.setInt(1,id);ps.setString(2,date.toString());try(ResultSet rs=ps.executeQuery()){while(rs.next())result.add(map(rs));}
        }return result;
    }
    @Override public Optional<Appointment> findById(int id)throws SQLException{
        try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement(SELECT+" WHERE a.id=?")){
            ps.setInt(1,id);try(ResultSet rs=ps.executeQuery()){return rs.next()?Optional.of(map(rs)):Optional.empty();}
        }
    }
    @Override public Appointment save(Appointment a)throws SQLException{
        String sql=a.getId()==0?"INSERT INTO appointment(doctor_id,patient_id,appointment_date,start_time,end_time,status,reason) VALUES(?,?,?,?,?,?,?)":"UPDATE appointment SET doctor_id=?,patient_id=?,appointment_date=?,start_time=?,end_time=?,status=?,reason=? WHERE id=?";
        try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1,a.getDoctorId());ps.setInt(2,a.getPatientId());ps.setString(3,a.getDate().toString());ps.setString(4,a.getStartTime().toString());ps.setString(5,a.getEndTime().toString());ps.setString(6,a.getStatus().name());ps.setString(7,a.getReason());
            if(a.getId()!=0)ps.setInt(8,a.getId());ps.executeUpdate();if(a.getId()==0)try(ResultSet keys=ps.getGeneratedKeys()){if(keys.next())a.setId(keys.getInt(1));}
        }return a;
    }
    @Override public void cancel(int id)throws SQLException{
        try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement("UPDATE appointment SET status='CANCELLED' WHERE id=? AND status='BOOKED'")){ps.setInt(1,id);ps.executeUpdate();}
    }
}
