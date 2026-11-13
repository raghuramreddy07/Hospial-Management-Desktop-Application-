package com.hospital.service;

import com.hospital.dao.PatientDAO;
import com.hospital.exception.*;
import com.hospital.model.Patient;
import java.sql.SQLException;
import java.util.List;

/** Validates and coordinates patient operations. */
public class PatientService {
    private final PatientDAO dao;
    public PatientService(PatientDAO dao){this.dao=dao;}
    public List<Patient> findAll(String name)throws SQLException{return dao.findAll(name);}
    public Patient save(Patient p)throws ValidationException,RecordNotFoundException,SQLException{
        DoctorService.required(p.getName(),"Patient name");DoctorService.validatePhone(p.getPhone());
        DoctorService.required(p.getGender(),"Gender");DoctorService.required(p.getAddress(),"Address");
        if(p.getAge()<0||p.getAge()>130)throw new ValidationException("Age must be between 0 and 130.");
        if(p.getId()!=0&&dao.findById(p.getId()).isEmpty())throw new RecordNotFoundException("The selected patient no longer exists.");
        return dao.save(p);
    }
    public void delete(int id)throws SQLException,ValidationException,RecordNotFoundException{
        if(dao.findById(id).isEmpty())throw new RecordNotFoundException("The selected patient no longer exists.");
        if(dao.hasBookedAppointments(id))throw new ValidationException("This patient has booked appointments and cannot be deleted. Cancel or complete them first.");
        dao.delete(id);
    }
}
