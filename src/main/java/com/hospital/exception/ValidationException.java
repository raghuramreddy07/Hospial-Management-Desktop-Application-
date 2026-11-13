package com.hospital.exception;

/** A user-correctable validation or business-rule error. */
public class ValidationException extends Exception { public ValidationException(String message) { super(message); } }
