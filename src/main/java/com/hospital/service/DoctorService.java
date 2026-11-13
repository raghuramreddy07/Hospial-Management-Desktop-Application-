package com.hospital.service;

import com.hospital.dao.DoctorDAO;
import com.hospital.exception.*;
import com.hospital.model.Doctor;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

/** Validates and coordinates doctor operations. */
public class DoctorService {
    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9][0-9 ()-]{6,19}$");
    private final DoctorDAO dao;
    public DoctorService(DoctorDAO dao) { this.dao = dao; }
    public List<Doctor> findAll(String name) throws SQLException { return dao.findAll(name); }
    public Doctor save(Doctor d) throws ValidationException, RecordNotFoundException, SQLException {
        required(d.getName(),"Doctor name"); required(d.getSpecialization(),"Specialization");
        validatePhone(d.getPhone());
        if (d.getWorkStart()==null || d.getWorkEnd()==null || !d.getWorkStart().isBefore(d.getWorkEnd()))
            throw new ValidationException("Working-hours end time must be after start time.");
        if(d.getId()!=0 && dao.findById(d.getId()).isEmpty())throw new RecordNotFoundException("The selected doctor no longer exists.");
        return dao.save(d);
    }
    public void delete(int id)throws SQLException,ValidationException,RecordNotFoundException{
        if(dao.findById(id).isEmpty())throw new RecordNotFoundException("The selected doctor no longer exists.");
        if(dao.hasBookedAppointments(id))throw new ValidationException("This doctor has booked appointments and cannot be deleted. Cancel or complete them first.");
        dao.delete(id);
    }
    static void required(String value,String label)throws ValidationException{
        if(value==null||value.trim().isEmpty())throw new ValidationException(label+" is required.");
    }
    static void validatePhone(String phone)throws ValidationException{
        if(phone==null||!PHONE.matcher(phone.trim()).matches())throw new ValidationException("Enter a valid phone number (7–20 digits, with optional +, spaces, parentheses, or hyphens).");
    }
}
