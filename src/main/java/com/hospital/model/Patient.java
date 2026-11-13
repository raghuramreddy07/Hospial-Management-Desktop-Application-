package com.hospital.model;

/** A patient record maintained by reception staff. */
public class Patient extends Person {
    private int age;
    private String gender;
    private String address;
    public Patient(int id, String name, String phone, int age, String gender, String address) {
        super(id, name, phone); this.age = age; this.gender = gender; this.address = address;
    }
    public int getAge() { return age; }
    public void setAge(int value) { age = value; }
    public String getGender() { return gender; }
    public void setGender(String value) { gender = value; }
    public String getAddress() { return address; }
    public void setAddress(String value) { address = value; }
    @Override public String toString() { return getName(); }
}
