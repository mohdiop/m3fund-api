package com.mohdiop.m3fundapi.dto.request.create;

import com.mohdiop.m3fundapi.entity.Localization;
import jakarta.validation.constraints.*;

public record CreateLocalizationRequest(
        @NotBlank(message = "Le pays est obligatoire et ne peut pas être vide.")
        @Size(max = 50, message = "Le pays ne doit pas dépasser {max} caractères.")
        String country,

        @NotBlank(message = "La ville est obligatoire et ne peut pas être vide.")
        @Size(max = 50, message = "La ville ne doit pas dépasser {max} caractères.")
        String town,

        @Size(max = 50, message = "La région ne doit pas dépasser {max} caractères.")
        String region,

        @Size(max = 100, message = "La rue/adresse ne doit pas dépasser {max} caractères.")
        String street,

        @NotNull(message = "La longitude est obligatoire.")
        @DecimalMin(value = "-180.0", message = "La longitude doit être supérieure ou égale à {value}.")
        @DecimalMax(value = "180.0", message = "La longitude doit être inférieure ou égale à {value}.")
        double longitude,

        @NotNull(message = "La latitude est obligatoire.")
        @DecimalMin(value = "-90.0", message = "La latitude doit être supérieure ou égale à {value}.")
        @DecimalMax(value = "90.0", message = "La latitude doit être inférieure ou égale à {value}.")
        double latitude
) {
    public Localization toLocalization() {
        return Localization.builder()
                .id(null)
                .country(country)
                .town(town)
                .region(region)
                .street(street)
                .longitude(longitude)
                .latitude(latitude)
                .build();
    }
}
