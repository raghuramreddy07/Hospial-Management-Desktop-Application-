package com.hospital.exception;

/** Raised when a requested doctor, patient, or appointment no longer exists. */
public class RecordNotFoundException extends Exception { public RecordNotFoundException(String message) { super(message); } }
