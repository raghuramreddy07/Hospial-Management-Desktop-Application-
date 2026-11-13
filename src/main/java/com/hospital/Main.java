package com.hospital;

import com.hospital.dao.JdbcAuthDAO;
import com.hospital.ui.LoginDialog;
import com.hospital.ui.MainFrame;
import com.hospital.service.AuthService;
import com.hospital.util.DBConnection;
import javax.swing.*;

/** Application entry point. */
public class Main {
    /** Starts the desktop app on Swing's event-dispatch thread. */
    public static void main(String[] args){
        SwingUtilities.invokeLater(()->{
            try{
                DBConnection.initialize();
                LoginDialog login=new LoginDialog(null,new AuthService(new JdbcAuthDAO()));login.setVisible(true);
                if(login.isAuthenticated())new MainFrame().setVisible(true);
            }catch(Exception ex){
                JOptionPane.showMessageDialog(null,"The application could not start because its local database could not be opened. Check that this folder is writable.",
                        "Startup error",JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
