package com.mohdiop.m3fundapi.dto.request.create;

import com.mohdiop.m3fundapi.entity.Reward;
import com.mohdiop.m3fundapi.entity.enums.RewardType;
import jakarta.validation.constraints.*;

public record CreateRewardRequest(

        @NotBlank(message = "Le nom de la récompense est obligatoire.")
        @Size(min = 3, max = 100, message = "Le nom de la récompense doit comporter entre 3 et 100 caractères.")
        String name,

        @NotBlank(message = "La description de la récompense est obligatoire.")
        @Size(min = 10, max = 500, message = "La description doit comporter entre 10 et 500 caractères.")
        String description,

        @NotNull(message = "Le type de récompense est obligatoire.")
        RewardType type,

        @Positive(message = "La quantité doit être supérieure à 0.")
        long quantity,

        @NotNull(message = "Le montant de déblocage est obligatoire.")
        @DecimalMin(value = "0.0", inclusive = false, message = "Le montant de déblocage doit être supérieur à 0.")
        @Digits(integer = 12, fraction = 2, message = "Le montant de déblocage doit être un nombre valide avec au maximum 2 décimales.")
        Double unlockAmount

) {

    public Reward toReward() {
        return Reward.builder()
                .id(null)
                .name(name)
                .description(description)
                .quantity(quantity)
                .type(type)
                .unlockAmount(unlockAmount)
                .build();
    }
}
