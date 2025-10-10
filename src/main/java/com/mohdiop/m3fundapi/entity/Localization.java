package com.mohdiop.m3fundapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "localizations")
public class Localization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String town;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private double latitude;
}
