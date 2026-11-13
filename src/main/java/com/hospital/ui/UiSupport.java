package com.hospital.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/** Shared Swing helpers for consistent table, form, and error presentation. */
final class UiSupport {
    private UiSupport() {}
    static JPanel formPanel() { return new JPanel(new GridBagLayout()); }
    static void addField(JPanel panel,String label,Component field,int row) {
        GridBagConstraints c=new GridBagConstraints();c.insets=new Insets(4,6,4,6);c.anchor=GridBagConstraints.LINE_START;
        c.gridx=0;c.gridy=row;c.weightx=0;c.fill=GridBagConstraints.NONE;panel.add(new JLabel(label),c);
        c.gridx=1;c.weightx=1;c.fill=GridBagConstraints.HORIZONTAL;panel.add(field,c);
    }
    static JPanel buttons(JButton... buttons) {
        JPanel p=new JPanel(new FlowLayout(FlowLayout.LEADING));for(JButton b:buttons)p.add(b);return p;
    }
    static void showError(Component owner,Exception ex) {
        String message;
        if(ex instanceof SQLException) message="The change could not be saved because of a database problem. Please try again.";
        else message=ex.getMessage()==null?"The requested action could not be completed.":ex.getMessage();
        JOptionPane.showMessageDialog(owner,message,"Unable to complete action",JOptionPane.ERROR_MESSAGE);
    }
    static String value(JTextField field){return field.getText().trim();}
    static void confirmDelete(Component owner,Runnable action) {
        if(JOptionPane.showConfirmDialog(owner,"Delete this record? Its appointment history will also be removed. This cannot be undone.","Confirm delete",
                JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION)action.run();
    }
}
