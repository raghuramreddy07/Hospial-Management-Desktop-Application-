package com.hospital.ui;

import com.hospital.model.Patient;
import com.hospital.service.PatientService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Receptionist form and table for patient records. */
public class PatientPanel extends JPanel {
    private final PatientService service;
    private final JTextField name=new JTextField(22),phone=new JTextField(22),age=new JTextField(22),address=new JTextField(22),search=new JTextField(18);
    private final JComboBox<String> gender=new JComboBox<>(new String[]{"Female","Male","Other","Prefer not to say"});
    private final DefaultTableModel model=new DefaultTableModel(new Object[]{"ID","Name","Phone","Age","Gender","Address"},0){@Override public boolean isCellEditable(int r,int c){return false;}};
    private final JTable table=new JTable(model);private int selectedId;
    public PatientPanel(PatientService service){
        this.service=service;setLayout(new BorderLayout(8,8));setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JPanel form=UiSupport.formPanel();UiSupport.addField(form,"Name",name,0);UiSupport.addField(form,"Phone",phone,1);
        UiSupport.addField(form,"Age",age,2);UiSupport.addField(form,"Gender",gender,3);UiSupport.addField(form,"Address",address,4);
        JButton save=new JButton("Save patient"),clear=new JButton("Clear"),delete=new JButton("Delete");
        JPanel top=new JPanel(new BorderLayout());top.add(form,BorderLayout.CENTER);top.add(UiSupport.buttons(save,clear,delete),BorderLayout.SOUTH);
        JPanel find=new JPanel(new FlowLayout(FlowLayout.LEADING));find.add(new JLabel("Search by name"));find.add(search);JButton searchButton=new JButton("Search");find.add(searchButton);
        JPanel north=new JPanel(new BorderLayout());north.add(top,BorderLayout.CENTER);north.add(find,BorderLayout.SOUTH);add(north,BorderLayout.NORTH);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);add(new JScrollPane(table),BorderLayout.CENTER);
        save.addActionListener(e->save());clear.addActionListener(e->clear());delete.addActionListener(e->UiSupport.confirmDelete(this,this::delete));
        searchButton.addActionListener(e->refresh());search.addActionListener(e->refresh());table.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting())selectRow();});refresh();
    }
    private void save(){try{Patient p=new Patient(selectedId,UiSupport.value(name),UiSupport.value(phone),Integer.parseInt(UiSupport.value(age)),(String)gender.getSelectedItem(),UiSupport.value(address));service.save(p);clear();refresh();JOptionPane.showMessageDialog(this,"Patient saved successfully.");}
        catch(NumberFormatException ex){JOptionPane.showMessageDialog(this,"Enter the patient's age as a whole number.","Invalid age",JOptionPane.ERROR_MESSAGE);}catch(Exception ex){UiSupport.showError(this,ex);}}
    private void delete(){if(selectedId==0){JOptionPane.showMessageDialog(this,"Select a patient first.");return;}try{service.delete(selectedId);clear();refresh();}catch(Exception ex){UiSupport.showError(this,ex);}}
    public void reload(){try{model.setRowCount(0);for(Patient p:service.findAll(UiSupport.value(search)))model.addRow(new Object[]{p.getId(),p.getName(),p.getPhone(),p.getAge(),p.getGender(),p.getAddress()});}catch(Exception ex){UiSupport.showError(this,ex);}}
    private void selectRow(){int row=table.getSelectedRow();if(row<0)return;selectedId=(int)model.getValueAt(row,0);name.setText((String)model.getValueAt(row,1));phone.setText((String)model.getValueAt(row,2));age.setText(model.getValueAt(row,3).toString());gender.setSelectedItem(model.getValueAt(row,4));address.setText((String)model.getValueAt(row,5));}
    private void refresh(){reload();}
    private void clear(){selectedId=0;name.setText("");phone.setText("");age.setText("");gender.setSelectedIndex(0);address.setText("");table.clearSelection();}
}
