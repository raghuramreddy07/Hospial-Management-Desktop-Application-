package com.hospital.ui;

import com.hospital.model.*;
import com.hospital.service.*;
import com.hospital.util.DateTimeUtil;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.List;

/** Appointment booking, rescheduling, schedule search, cancellation, and CSV export. */
public class AppointmentPanel extends JPanel {
    private final AppointmentService service;private final DoctorService doctorService;private final PatientService patientService;
    private final JComboBox<Doctor> doctor=new JComboBox<>();private final JComboBox<Patient> patient=new JComboBox<>();
    private final JTextField date=new JTextField(LocalDate.now().toString(),18),start=new JTextField("10:00",18),end=new JTextField("10:30",18),reason=new JTextField(22);
    private final JTextField filterDate=new JTextField(12),filterDoctor=new JTextField(12),filterPatient=new JTextField(12);
    private final DefaultTableModel model=new DefaultTableModel(new Object[]{"ID","Date","Start","End","Doctor","Patient","Status","Reason"},0){@Override public boolean isCellEditable(int r,int c){return false;}};
    private final JTable table=new JTable(model);private int selectedId;
    public AppointmentPanel(AppointmentService service,DoctorService doctorService,PatientService patientService){
        this.service=service;this.doctorService=doctorService;this.patientService=patientService;
        setLayout(new BorderLayout(8,8));setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JPanel form=UiSupport.formPanel();UiSupport.addField(form,"Doctor",doctor,0);UiSupport.addField(form,"Patient",patient,1);
        UiSupport.addField(form,"Date (YYYY-MM-DD)",date,2);UiSupport.addField(form,"Start time (HH:mm)",start,3);
        UiSupport.addField(form,"End time (HH:mm)",end,4);UiSupport.addField(form,"Reason",reason,5);
        JButton slot=new JButton("+ 30 min"),save=new JButton("Book / reschedule"),cancel=new JButton("Cancel appointment"),complete=new JButton("Mark completed"),clear=new JButton("Clear");
        JPanel actions=UiSupport.buttons(slot,save,cancel,complete,clear);
        JPanel top=new JPanel(new BorderLayout());top.add(form,BorderLayout.CENTER);top.add(actions,BorderLayout.SOUTH);
        JPanel filters=new JPanel(new FlowLayout(FlowLayout.LEADING));filters.add(new JLabel("Date"));filters.add(filterDate);filters.add(new JLabel("Doctor"));filters.add(filterDoctor);filters.add(new JLabel("Patient"));filters.add(filterPatient);
        JButton search=new JButton("Search"),today=new JButton("Today's appointments"),export=new JButton("Export day's CSV"),all=new JButton("Show all");
        filters.add(search);filters.add(today);filters.add(all);filters.add(export);
        JPanel north=new JPanel(new BorderLayout());north.add(top,BorderLayout.CENTER);north.add(filters,BorderLayout.SOUTH);add(north,BorderLayout.NORTH);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);table.setDefaultRenderer(Object.class,new StatusRenderer());add(new JScrollPane(table),BorderLayout.CENTER);
        slot.addActionListener(e->setThirtyMinuteEnd());save.addActionListener(e->save());cancel.addActionListener(e->cancel());complete.addActionListener(e->complete());
        clear.addActionListener(e->clear());search.addActionListener(e->refresh());filterDate.addActionListener(e->refresh());
        today.addActionListener(e->{filterDate.setText(LocalDate.now().toString());refresh();});
        all.addActionListener(e->{filterDate.setText("");filterDoctor.setText("");filterPatient.setText("");refresh();});
        export.addActionListener(e->exportCsv());table.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting())selectRow();});
        reloadChoices();refresh();
    }
    public void reloadChoices() {
        try{Doctor currentDoctor=(Doctor)doctor.getSelectedItem();Patient currentPatient=(Patient)patient.getSelectedItem();
            int doctorId=currentDoctor==null?0:currentDoctor.getId(),patientId=currentPatient==null?0:currentPatient.getId();
            List<Doctor> doctors=doctorService.findAll("");doctor.removeAllItems();doctors.forEach(doctor::addItem);selectId(doctor,doctorId);
            List<Patient> patients=patientService.findAll("");patient.removeAllItems();patients.forEach(patient::addItem);selectId(patient,patientId);}
        catch(Exception ex){UiSupport.showError(this,ex);}
    }
    private <T>void selectId(JComboBox<T> combo,int id){
        if(id==0)return;
        for(int i=0;i<combo.getItemCount();i++){
            Object item=combo.getItemAt(i);
            if(item instanceof Doctor d&&d.getId()==id||item instanceof Patient p&&p.getId()==id){combo.setSelectedIndex(i);return;}
        }
    }
    private void setThirtyMinuteEnd(){try{end.setText(LocalTime.parse(UiSupport.value(start)).plusMinutes(30).toString());}catch(DateTimeParseException ex){JOptionPane.showMessageDialog(this,"Enter the start time using HH:mm, for example 10:00.","Invalid time",JOptionPane.ERROR_MESSAGE);}}
    private void save(){
        try{Doctor d=(Doctor)doctor.getSelectedItem();Patient p=(Patient)patient.getSelectedItem();
            if(d==null||p==null)throw new IllegalArgumentException("Add at least one doctor and one patient before booking.");
            Appointment a=new Appointment(selectedId,d.getId(),p.getId(),DateTimeUtil.parseDate(UiSupport.value(date)),
                    DateTimeUtil.parseTime(UiSupport.value(start)),DateTimeUtil.parseTime(UiSupport.value(end)),AppointmentStatus.BOOKED,UiSupport.value(reason));
            if(selectedId==0)service.book(a);else service.reschedule(a);clear();refresh();JOptionPane.showMessageDialog(this,"Appointment saved successfully.");
        }catch(DateTimeParseException ex){JOptionPane.showMessageDialog(this,"Enter the date as YYYY-MM-DD and times as HH:mm.","Invalid date or time",JOptionPane.ERROR_MESSAGE);}
        catch(Exception ex){UiSupport.showError(this,ex);}
    }
    private void cancel(){if(selectedId==0){JOptionPane.showMessageDialog(this,"Select a booked appointment first.");return;}
        if(JOptionPane.showConfirmDialog(this,"Cancel this appointment? The record will be kept.","Confirm cancellation",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;
        try{service.cancel(selectedId);clear();refresh();}catch(Exception ex){UiSupport.showError(this,ex);}}
    private void complete(){if(selectedId==0){JOptionPane.showMessageDialog(this,"Select an appointment first.");return;}
        try{service.complete(selectedId);clear();refresh();
        }catch(Exception ex){UiSupport.showError(this,ex);}}
    public void reload(){refresh();}
    private void refresh(){
        try{LocalDate dateFilter=UiSupport.value(filterDate).isBlank()?null:DateTimeUtil.parseDate(UiSupport.value(filterDate));
            model.setRowCount(0);for(Appointment a:service.search(dateFilter,UiSupport.value(filterDoctor),UiSupport.value(filterPatient)))
                model.addRow(new Object[]{a.getId(),a.getDate(),a.getStartTime(),a.getEndTime(),a.getDoctorName(),a.getPatientName(),a.getStatus(),a.getReason()});
        }catch(DateTimeParseException ex){JOptionPane.showMessageDialog(this,"Enter the filter date as YYYY-MM-DD.","Invalid date",JOptionPane.ERROR_MESSAGE);}
        catch(Exception ex){UiSupport.showError(this,ex);}
    }
    private void selectRow(){int row=table.getSelectedRow();if(row<0)return;selectedId=(int)model.getValueAt(row,0);date.setText(model.getValueAt(row,1).toString());
        start.setText(model.getValueAt(row,2).toString());end.setText(model.getValueAt(row,3).toString());reason.setText((String)model.getValueAt(row,7));
        setChoice(doctor,(String)model.getValueAt(row,4));setChoice(patient,(String)model.getValueAt(row,5));}
    private <T>void setChoice(JComboBox<T> combo,String name){for(int i=0;i<combo.getItemCount();i++)if(combo.getItemAt(i).toString().startsWith(name)){combo.setSelectedIndex(i);return;}}
    private void clear(){selectedId=0;date.setText(LocalDate.now().toString());start.setText("10:00");end.setText("10:30");reason.setText("");table.clearSelection();}
    private void exportCsv(){
        LocalDate target;try{String filter=UiSupport.value(filterDate);target=filter.isEmpty()?LocalDate.now():DateTimeUtil.parseDate(filter);}
        catch(DateTimeParseException ex){JOptionPane.showMessageDialog(this,"Enter the date to export as YYYY-MM-DD.","Invalid date",JOptionPane.ERROR_MESSAGE);return;}
        JFileChooser chooser=new JFileChooser();chooser.setSelectedFile(new java.io.File("schedule-"+target+".csv"));
        if(chooser.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION)return;
        try{List<Appointment> list=service.search(target,"","");
            try(BufferedWriter writer=Files.newBufferedWriter(chooser.getSelectedFile().toPath(),StandardCharsets.UTF_8)){
                writer.write("Date,Start,End,Doctor,Patient,Status,Reason");writer.newLine();
                for(Appointment a:list){writer.write(csv(a.getDate().toString())+","+csv(a.getStartTime().toString())+","+csv(a.getEndTime().toString())+","+csv(a.getDoctorName())+","+csv(a.getPatientName())+","+csv(a.getStatus().name())+","+csv(a.getReason()));writer.newLine();}
            }
            JOptionPane.showMessageDialog(this,"Schedule exported successfully.");
        }catch(Exception ex){UiSupport.showError(this,ex);}
    }
    private String csv(String value){return "\""+value.replace("\"","\"\"")+"\"";}
    private static class StatusRenderer extends DefaultTableCellRenderer{
        @Override public Component getTableCellRendererComponent(JTable t,Object value,boolean selected,boolean focus,int row,int col){
            Component c=super.getTableCellRendererComponent(t,value,selected,focus,row,col);
            if(!selected){Object state=t.getModel().getValueAt(t.convertRowIndexToModel(row),6);
                c.setBackground(AppointmentStatus.BOOKED.name().equals(state)?new Color(218,242,221):AppointmentStatus.CANCELLED.name().equals(state)?new Color(250,220,220):new Color(230,230,230));}
            return c;
        }
    }
}
