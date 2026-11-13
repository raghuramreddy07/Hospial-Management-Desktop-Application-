package com.hospital.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Opens SQLite connections and initializes the schema, default login, and first-run sample data. */
public final class DBConnection {
    private static String jdbcUrl = "jdbc:sqlite:" + defaultDatabasePath();
    private DBConnection() {}
    private static Path defaultDatabasePath() {
        try {
            Path location = Paths.get(DBConnection.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (location.toString().endsWith(".jar")) return location.resolveSibling("hospital.db");
        } catch (java.net.URISyntaxException ex) {
            throw new IllegalStateException("Unable to locate the application database folder.", ex);
        }
        return Paths.get("hospital.db").toAbsolutePath();
    }
    public static synchronized void useDatabase(Path path) { jdbcUrl = "jdbc:sqlite:" + path.toAbsolutePath(); }
    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(jdbcUrl);
        try (Statement statement = connection.createStatement()) { statement.execute("PRAGMA foreign_keys=ON"); }
        return connection;
    }
    public static void initialize() throws SQLException {
        try (Connection connection = getConnection(); Statement s = connection.createStatement()) {
            s.executeUpdate("CREATE TABLE IF NOT EXISTS doctor (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, phone TEXT NOT NULL, specialization TEXT NOT NULL, email TEXT, work_start TEXT NOT NULL, work_end TEXT NOT NULL)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS patient (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, phone TEXT NOT NULL, age INTEGER NOT NULL, gender TEXT NOT NULL, address TEXT NOT NULL)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS appointment (id INTEGER PRIMARY KEY AUTOINCREMENT, doctor_id INTEGER NOT NULL REFERENCES doctor(id) ON DELETE CASCADE, patient_id INTEGER NOT NULL REFERENCES patient(id) ON DELETE CASCADE, appointment_date TEXT NOT NULL, start_time TEXT NOT NULL, end_time TEXT NOT NULL, status TEXT NOT NULL, reason TEXT NOT NULL)");
            s.executeUpdate("CREATE INDEX IF NOT EXISTS idx_appointment_doctor_date ON appointment(doctor_id, appointment_date, status)");
            s.executeUpdate("CREATE INDEX IF NOT EXISTS idx_appointment_patient_date ON appointment(patient_id, appointment_date, status)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS users (username TEXT PRIMARY KEY, password TEXT NOT NULL, role TEXT NOT NULL)");
            s.executeUpdate("INSERT OR IGNORE INTO users(username,password,role) VALUES('admin','admin123','ADMIN')");
            s.executeUpdate("CREATE TRIGGER IF NOT EXISTS prevent_appointment_overlap_insert BEFORE INSERT ON appointment WHEN NEW.status='BOOKED' AND EXISTS (SELECT 1 FROM appointment e WHERE e.status='BOOKED' AND e.appointment_date=NEW.appointment_date AND (e.doctor_id=NEW.doctor_id OR e.patient_id=NEW.patient_id) AND NEW.start_time<e.end_time AND NEW.end_time>e.start_time) BEGIN SELECT RAISE(ABORT,'APPOINTMENT_CONFLICT'); END");
            s.executeUpdate("CREATE TRIGGER IF NOT EXISTS prevent_appointment_overlap_update BEFORE UPDATE OF doctor_id,patient_id,appointment_date,start_time,end_time,status ON appointment WHEN NEW.status='BOOKED' AND EXISTS (SELECT 1 FROM appointment e WHERE e.id<>NEW.id AND e.status='BOOKED' AND e.appointment_date=NEW.appointment_date AND (e.doctor_id=NEW.doctor_id OR e.patient_id=NEW.patient_id) AND NEW.start_time<e.end_time AND NEW.end_time>e.start_time) BEGIN SELECT RAISE(ABORT,'APPOINTMENT_CONFLICT'); END");
            try (ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM doctor")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    s.executeUpdate("INSERT INTO doctor(name,phone,specialization,email,work_start,work_end) VALUES('Dr. Anil Rao','9876543210','General Medicine','rao@example.com','09:00','17:00'),('Dr. Meera Shah','9876543211','Pediatrics','shah@example.com','09:00','17:00')");
                }
            }
            try (ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM patient")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    s.executeUpdate("INSERT INTO patient(name,phone,age,gender,address) VALUES('Aarav Kumar','9876543212',32,'Male','12 Lake Road'),('Isha Patel','9876543213',26,'Female','8 Park Street'),('Kabir Singh','9876543214',45,'Male','4 Hill Avenue')");
                }
            }
        }
    }
}
