package com.hospital.service;

import com.hospital.dao.*;
import com.hospital.exception.*;
import com.hospital.model.*;
import com.hospital.util.DateTimeUtil;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.stream.Stream;

/** Enforces appointment validation, working hours, and overlap rules. */
public class AppointmentService {
    private final AppointmentDAO appointments;
    private final DoctorDAO doctors;
    private final PatientDAO patients;
    public AppointmentService(AppointmentDAO appointments,DoctorDAO doctors,PatientDAO patients){
        this.appointments=appointments;this.doctors=doctors;this.patients=patients;
    }
    public List<Appointment> search(LocalDate date,String doctor,String patient)throws SQLException{return appointments.findAll(date,doctor,patient);}
    public Optional<Appointment> findById(int id)throws SQLException{return appointments.findById(id);}
    public Appointment book(Appointment a)throws SQLException,ValidationException,RecordNotFoundException,AppointmentConflictException{
        a.setId(0);a.setStatus(AppointmentStatus.BOOKED);return save(a);
    }
    public Appointment reschedule(Appointment a)throws SQLException,ValidationException,RecordNotFoundException,AppointmentConflictException{
        Appointment current=appointments.findById(a.getId()).orElseThrow(()->new RecordNotFoundException("The selected appointment no longer exists."));
        if(current.getStatus()!=AppointmentStatus.BOOKED)throw new ValidationException("Only booked appointments can be rescheduled.");
        a.setStatus(AppointmentStatus.BOOKED);return save(a);
    }
    private Appointment save(Appointment a)throws SQLException,ValidationException,RecordNotFoundException,AppointmentConflictException{
        validate(a);
        Doctor doctor=doctors.findById(a.getDoctorId()).orElseThrow(()->new RecordNotFoundException("The selected doctor no longer exists."));
        if(patients.findById(a.getPatientId()).isEmpty())throw new RecordNotFoundException("The selected patient no longer exists.");
        if(a.getStartTime().isBefore(doctor.getWorkStart())||a.getEndTime().isAfter(doctor.getWorkEnd()))
            throw new ValidationException("Appointment must be within Dr. "+doctor.getName()+"'s working hours ("+doctor.getWorkStart()+"–"+doctor.getWorkEnd()+").");
        for(Appointment existing:Stream.concat(appointments.findByDoctorAndDate(a.getDoctorId(),a.getDate()).stream(),
                appointments.findByPatientAndDate(a.getPatientId(),a.getDate()).stream()).distinct()
                .sorted(Comparator.comparing(Appointment::getStartTime)).toList()){
            if(existing.getId()!=a.getId()&&overlaps(a,existing)){
                if(existing.getDoctorId()==a.getDoctorId())
                    throw new AppointmentConflictException("Dr. "+doctor.getName()+" already has an appointment from "+existing.getStartTime()+" to "+existing.getEndTime()+" on "+DateTimeUtil.displayDate(a.getDate())+".");
                throw new AppointmentConflictException("This patient already has an appointment from "+existing.getStartTime()+" to "+existing.getEndTime()+" on "+DateTimeUtil.displayDate(a.getDate())+".");
            }
        }
        try{return appointments.save(a);}catch(SQLException ex){
            if(ex.getMessage()!=null&&ex.getMessage().contains("APPOINTMENT_CONFLICT"))
                throw new AppointmentConflictException("This time is no longer available. Refresh the schedule and choose another slot.");
            throw ex;
        }
    }
    private void validate(Appointment a)throws ValidationException{
        if(a.getDate()==null||a.getStartTime()==null||a.getEndTime()==null)throw new ValidationException("Date, start time, and end time are required.");
        if(a.getDate().isBefore(LocalDate.now()))throw new ValidationException("Appointments cannot be booked in the past.");
        if(!a.getStartTime().isBefore(a.getEndTime()))throw new ValidationException("End time must be after start time.");
        DoctorService.required(a.getReason(),"Reason");
        if(a.getDoctorId()<=0)throw new ValidationException("Select a doctor.");
        if(a.getPatientId()<=0)throw new ValidationException("Select a patient.");
    }
    private boolean overlaps(Appointment first,Appointment second){
        return first.getStartTime().isBefore(second.getEndTime())&&first.getEndTime().isAfter(second.getStartTime());
    }
    public void cancel(int id)throws SQLException,RecordNotFoundException,ValidationException{
        Appointment a=appointments.findById(id).orElseThrow(()->new RecordNotFoundException("The selected appointment no longer exists."));
        if(a.getStatus()!=AppointmentStatus.BOOKED)throw new ValidationException("Only booked appointments can be cancelled.");
        appointments.cancel(id);
    }
    public void complete(int id)throws SQLException,RecordNotFoundException,ValidationException{
        Appointment appointment=appointments.findById(id).orElseThrow(()->new RecordNotFoundException("The selected appointment no longer exists."));
        if(appointment.getStatus()!=AppointmentStatus.BOOKED)throw new ValidationException("Only booked appointments can be marked completed.");
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointments.save(appointment);
    }
}
