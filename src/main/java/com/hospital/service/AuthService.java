package com.hospital.service;

import com.hospital.dao.AuthDAO;
import java.sql.SQLException;

/** Authenticates staff before opening the appointment workspace. */
public class AuthService {
    private final AuthDAO dao;
    public AuthService(AuthDAO dao){this.dao=dao;}
    public boolean authenticate(String username,String password)throws SQLException{
        return username!=null&&!username.isBlank()&&password!=null&&dao.authenticate(username.trim(),password);
    }
}
