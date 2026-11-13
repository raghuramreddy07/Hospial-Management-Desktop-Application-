package com.hospital.ui;

import com.hospital.service.AuthService;
import javax.swing.*;
import java.awt.*;

/** Modal staff login shown before the main application window. */
public class LoginDialog extends JDialog {
    private boolean authenticated;
    public LoginDialog(Frame owner,AuthService service){
        super(owner,"Hospital Appointment Management System - Login",true);
        JTextField username=new JTextField("admin",18);JPasswordField password=new JPasswordField(18);
        JPanel fields=UiSupport.formPanel();UiSupport.addField(fields,"Username",username,0);UiSupport.addField(fields,"Password",password,1);
        JButton login=new JButton("Sign in"),exit=new JButton("Exit");
        JPanel actions=UiSupport.buttons(login,exit);JPanel content=new JPanel(new BorderLayout(8,8));content.setBorder(BorderFactory.createEmptyBorder(18,18,12,18));
        content.add(new JLabel("Staff sign-in",SwingConstants.CENTER),BorderLayout.NORTH);content.add(fields,BorderLayout.CENTER);content.add(actions,BorderLayout.SOUTH);setContentPane(content);
        login.addActionListener(e->{try{if(service.authenticate(username.getText(),new String(password.getPassword()))){authenticated=true;dispose();}
            else JOptionPane.showMessageDialog(this,"Username or password is incorrect.","Sign-in failed",JOptionPane.ERROR_MESSAGE);}
            catch(Exception ex){UiSupport.showError(this,ex);}});
        password.addActionListener(e->login.doClick());exit.addActionListener(e->dispose());setDefaultCloseOperation(DISPOSE_ON_CLOSE);pack();setResizable(false);setLocationRelativeTo(owner);
    }
    public boolean isAuthenticated(){return authenticated;}
}
