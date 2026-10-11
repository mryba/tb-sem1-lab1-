package com.marcinryba.tbsem1lab1.model;

public class MachineNotFoundException extends RuntimeException {

    public MachineNotFoundException(Long id) {
        super("Nie znaleziono maszyny o ID: " + id);
    }
}
