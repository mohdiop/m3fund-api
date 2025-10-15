package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.dto.response.LocalizationResponse;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "localizations")
@Builder
public class Localization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public LocalizationResponse toResponse() {
        return new LocalizationResponse(
                id,
                town,
                region,
                street,
                longitude,
                latitude
        );
    }
}
