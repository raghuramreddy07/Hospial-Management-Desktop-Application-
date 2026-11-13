package com.hospital.model;

/** Common identity and contact information for people managed by the hospital. */
public abstract class Person {
    private int id;
    private String name;
    private String phone;

    protected Person(int id, String name, String phone) { this.id = id; this.name = name; this.phone = phone; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
