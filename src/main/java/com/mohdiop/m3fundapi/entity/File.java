package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.entity.enums.FileExtension;
import com.mohdiop.m3fundapi.entity.enums.FileType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "files")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double sizeKo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileExtension extension;

    @Column(nullable = false)
    private String url;
}
