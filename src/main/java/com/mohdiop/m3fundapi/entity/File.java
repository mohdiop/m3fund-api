package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.entity.enums.FileExtension;
import com.mohdiop.m3fundapi.entity.enums.FileType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "files")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
