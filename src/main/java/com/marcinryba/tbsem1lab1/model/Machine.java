package com.marcinryba.tbsem1lab1.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "machines")
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nazwa maszyny jest wymagana")
    @Size(max = 100, message = "Nazwa maszyny może mieć najwyżej 100 znaków")
    @Pattern(regexp = "^[a-zA-Z].*", message = "Nazwa maszyny musi zaczynać się od litery")
    private String name;

    private LocalDateTime launchDate;

    private boolean operational;

    public Machine() {
        this.launchDate = LocalDateTime.now();
        this.operational = true;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(LocalDateTime launchDate) {
        this.launchDate = launchDate;
    }

    public boolean isOperational() {
        return operational;
    }

    public void setOperational(boolean operational) {
        this.operational = operational;
    }
}
