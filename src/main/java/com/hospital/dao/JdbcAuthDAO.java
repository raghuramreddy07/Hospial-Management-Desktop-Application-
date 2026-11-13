package com.hospital.dao;

import com.hospital.util.DBConnection;
import java.sql.*;

/** SQLite implementation of application authentication. */
public class JdbcAuthDAO implements AuthDAO {
    @Override public boolean authenticate(String username,String password)throws SQLException{
        try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement("SELECT 1 FROM users WHERE username=? AND password=?")){
            ps.setString(1,username);ps.setString(2,password);try(ResultSet rs=ps.executeQuery()){return rs.next();}
        }
    }
}
