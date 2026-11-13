package com.hospital.service;

import com.hospital.dao.*;
import com.hospital.exception.AppointmentConflictException;
import com.hospital.model.*;
import com.hospital.util.DBConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.*;
import static org.junit.jupiter.api.Assertions.*;

/** Integration tests for appointment conflict detection against an isolated SQLite database. */
class AppointmentServiceTest {
    @TempDir Path temp;
    private AppointmentService service;private Doctor firstDoctor,secondDoctor;private Patient firstPatient,secondPatient;
    private LocalDate date;
    @BeforeEach void setUp()throws Exception{
        DBConnection.useDatabase(temp.resolve("test.db"));DBConnection.initialize();
        DoctorDAO doctors=new JdbcDoctorDAO();PatientDAO patients=new JdbcPatientDAO();
        firstDoctor=doctors.findAll("").get(0);secondDoctor=doctors.findAll("").get(1);
        firstPatient=patients.findAll("").get(0);secondPatient=patients.findAll("").get(1);
        service=new AppointmentService(new JdbcAppointmentDAO(),doctors,patients);date=LocalDate.now().plusDays(10);
    }
    @Test void exactOverlapConflicts()throws Exception{book(10,0,10,30,firstDoctor,firstPatient);assertConflict(10,0,10,30,firstDoctor,secondPatient);}
    @Test void partialOverlapConflicts()throws Exception{book(10,0,10,30,firstDoctor,firstPatient);assertConflict(10,15,10,45,firstDoctor,secondPatient);}
    @Test void nestedAppointmentConflicts()throws Exception{book(10,0,11,0,firstDoctor,firstPatient);assertConflict(10,15,10,45,firstDoctor,secondPatient);}
    @Test void backToBackAppointmentsDoNotConflict()throws Exception{book(10,0,10,30,firstDoctor,firstPatient);assertDoesNotThrow(()->book(10,30,11,0,firstDoctor,secondPatient));}
    @Test void differentDoctorDoesNotConflictForDifferentPatients()throws Exception{book(10,0,10,30,firstDoctor,firstPatient);assertDoesNotThrow(()->book(10,0,10,30,secondDoctor,secondPatient));}
    @Test void differentDateDoesNotConflict()throws Exception{book(10,0,10,30,firstDoctor,firstPatient);assertDoesNotThrow(()->book(10,0,10,30,firstDoctor,secondPatient,date.plusDays(1)));}
    @Test void cancelledAppointmentDoesNotConflict()throws Exception{Appointment old=book(10,0,10,30,firstDoctor,firstPatient);service.cancel(old.getId());assertDoesNotThrow(()->book(10,0,10,30,firstDoctor,secondPatient));}
    @Test void reschedulingExcludesAppointmentItself()throws Exception{
        Appointment a=book(10,0,10,30,firstDoctor,firstPatient);
        a.setStartTime(LocalTime.of(10,15));a.setEndTime(LocalTime.of(10,45));
        assertDoesNotThrow(()->service.reschedule(a));
    }
    @Test void patientCannotHaveOverlappingAppointmentsWithAnotherDoctor()throws Exception{
        book(10,0,10,30,firstDoctor,firstPatient);
        assertConflict(10,15,10,45,secondDoctor,firstPatient);
    }
    @Test void databaseGuardAlsoRejectsConflictingWrites()throws Exception{
        book(10,0,10,30,firstDoctor,firstPatient);
        Appointment overlapping=new Appointment(0,firstDoctor.getId(),secondPatient.getId(),date,LocalTime.of(10,15),LocalTime.of(10,45),AppointmentStatus.BOOKED,"Consultation");
        SQLException error=assertThrows(SQLException.class,()->new JdbcAppointmentDAO().save(overlapping));
        assertTrue(error.getMessage().contains("APPOINTMENT_CONFLICT"));
    }
    private Appointment book(int sh,int sm,int eh,int em,Doctor d,Patient p)throws Exception{return book(sh,sm,eh,em,d,p,date);}
    private Appointment book(int sh,int sm,int eh,int em,Doctor d,Patient p,LocalDate day)throws Exception{
        return service.book(new Appointment(0,d.getId(),p.getId(),day,LocalTime.of(sh,sm),LocalTime.of(eh,em),AppointmentStatus.BOOKED,"Consultation"));
    }
    private void assertConflict(int sh,int sm,int eh,int em,Doctor d,Patient p){
        assertThrows(AppointmentConflictException.class,()->book(sh,sm,eh,em,d,p));
    }
}
