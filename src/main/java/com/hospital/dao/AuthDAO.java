package com.hospital.dao;

import java.sql.SQLException;

/** Persistence contract for receptionist/admin login. */
public interface AuthDAO { boolean authenticate(String username, String password) throws SQLException; }
