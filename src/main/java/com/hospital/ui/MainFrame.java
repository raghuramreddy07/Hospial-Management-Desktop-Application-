package com.hospital.ui;

import com.hospital.dao.*;
import com.hospital.service.*;
import javax.swing.*;
import java.awt.*;

/** Main application window and composition root for Swing panels. */
public class MainFrame extends JFrame {
    public MainFrame(){
        super("Hospital Appointment Management System");
        DoctorDAO doctorDAO=new JdbcDoctorDAO();PatientDAO patientDAO=new JdbcPatientDAO();AppointmentDAO appointmentDAO=new JdbcAppointmentDAO();
        DoctorService doctors=new DoctorService(doctorDAO);PatientService patients=new PatientService(patientDAO);
        AppointmentService appointments=new AppointmentService(appointmentDAO,doctorDAO,patientDAO);
        DoctorPanel doctorPanel=new DoctorPanel(doctors);PatientPanel patientPanel=new PatientPanel(patients);
        AppointmentPanel appointmentPanel=new AppointmentPanel(appointments,doctors,patients);
        JTabbedPane tabs=new JTabbedPane();tabs.addTab("Doctors",doctorPanel);tabs.addTab("Patients",patientPanel);tabs.addTab("Appointments",appointmentPanel);
        tabs.addChangeListener(e->{doctorPanel.reload();patientPanel.reload();appointmentPanel.reloadChoices();appointmentPanel.reload();});
        setLayout(new BorderLayout());add(tabs,BorderLayout.CENTER);setDefaultCloseOperation(EXIT_ON_CLOSE);setMinimumSize(new Dimension(980,680));setSize(1120,760);setLocationRelativeTo(null);
    }
}
