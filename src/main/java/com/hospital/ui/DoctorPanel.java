package com.hospital.ui;

import com.hospital.model.Doctor;
import com.hospital.service.DoctorService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;

/** Receptionist form and table for doctor records. */
public class DoctorPanel extends JPanel {
    private final DoctorService service;
    private final JTextField name=new JTextField(22),phone=new JTextField(22),specialization=new JTextField(22),
            email=new JTextField(22),workStart=new JTextField("09:00",22),workEnd=new JTextField("17:00",22),search=new JTextField(18);
    private final DefaultTableModel model=new DefaultTableModel(new Object[]{"ID","Name","Phone","Specialization","Email","Work start","Work end"},0){
        @Override public boolean isCellEditable(int r,int c){return false;}
    };
    private final JTable table=new JTable(model);
    private int selectedId;
    public DoctorPanel(DoctorService service){
        this.service=service;setLayout(new BorderLayout(8,8));setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JPanel form=UiSupport.formPanel();UiSupport.addField(form,"Name",name,0);UiSupport.addField(form,"Phone",phone,1);
        UiSupport.addField(form,"Specialization",specialization,2);UiSupport.addField(form,"Email",email,3);
        UiSupport.addField(form,"Work start (HH:mm)",workStart,4);UiSupport.addField(form,"Work end (HH:mm)",workEnd,5);
        JButton save=new JButton("Save doctor"),clear=new JButton("Clear"),delete=new JButton("Delete");
        JPanel top=new JPanel(new BorderLayout());top.add(form,BorderLayout.CENTER);top.add(UiSupport.buttons(save,clear,delete),BorderLayout.SOUTH);
        JPanel find=new JPanel(new FlowLayout(FlowLayout.LEADING));find.add(new JLabel("Search by name"));find.add(search);JButton searchButton=new JButton("Search");find.add(searchButton);
        JPanel north=new JPanel(new BorderLayout());north.add(top,BorderLayout.CENTER);north.add(find,BorderLayout.SOUTH);add(north,BorderLayout.NORTH);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);add(new JScrollPane(table),BorderLayout.CENTER);
        save.addActionListener(e->save());clear.addActionListener(e->clear());delete.addActionListener(e->UiSupport.confirmDelete(this,this::delete));
        searchButton.addActionListener(e->refresh());search.addActionListener(e->refresh());
        table.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting())selectRow();});
        refresh();
    }
    private void save(){
        try{Doctor d=new Doctor(selectedId,UiSupport.value(name),UiSupport.value(phone),UiSupport.value(specialization),UiSupport.value(email),
                LocalTime.parse(UiSupport.value(workStart)),LocalTime.parse(UiSupport.value(workEnd)));
            service.save(d);clear();refresh();JOptionPane.showMessageDialog(this,"Doctor saved successfully.");
        }catch(java.time.format.DateTimeParseException ex){JOptionPane.showMessageDialog(this,"Enter working hours using HH:mm, for example 09:00.","Invalid time",JOptionPane.ERROR_MESSAGE);}
        catch(Exception ex){UiSupport.showError(this,ex);}
    }
    private void delete(){if(selectedId==0){JOptionPane.showMessageDialog(this,"Select a doctor first.");return;}try{service.delete(selectedId);clear();refresh();}catch(Exception ex){UiSupport.showError(this,ex);}}
    public void reload(){try{model.setRowCount(0);for(Doctor d:service.findAll(UiSupport.value(search)))model.addRow(new Object[]{d.getId(),d.getName(),d.getPhone(),d.getSpecialization(),d.getEmail(),d.getWorkStart(),d.getWorkEnd()});}catch(Exception ex){UiSupport.showError(this,ex);}}
    private void selectRow(){int row=table.getSelectedRow();if(row<0)return;selectedId=(int)model.getValueAt(row,0);name.setText((String)model.getValueAt(row,1));phone.setText((String)model.getValueAt(row,2));specialization.setText((String)model.getValueAt(row,3));email.setText((String)model.getValueAt(row,4));workStart.setText(model.getValueAt(row,5).toString());workEnd.setText(model.getValueAt(row,6).toString());}
    private void refresh(){reload();}
    private void clear(){selectedId=0;name.setText("");phone.setText("");specialization.setText("");email.setText("");workStart.setText("09:00");workEnd.setText("17:00");table.clearSelection();}
}
