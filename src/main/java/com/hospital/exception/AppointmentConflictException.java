package com.hospital.exception;

/** Raised when a booked time overlaps another active appointment. */
public class AppointmentConflictException extends Exception { public AppointmentConflictException(String message) { super(message); } }
