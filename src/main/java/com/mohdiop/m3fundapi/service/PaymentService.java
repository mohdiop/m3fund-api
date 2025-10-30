package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.response.PaymentResponse;
import com.mohdiop.m3fundapi.entity.CapitalPurchase;
import com.mohdiop.m3fundapi.entity.Contributor;
import com.mohdiop.m3fundapi.entity.Gift;
import com.mohdiop.m3fundapi.repository.ContributorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {

    private final ContributorRepository contributorRepository;

    public PaymentService(ContributorRepository contributorRepository) {
        this.contributorRepository = contributorRepository;
    }

    public List<PaymentResponse> getAllContributorPayments(
            Long contributorId
    ) {
        Contributor contributor = contributorRepository.findById(contributorId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Utilisateur introuvable.")
                );
        List<PaymentResponse> payments = new ArrayList<>();
        for (Gift gift : contributor.getGifts()) {
            payments.add(
                    gift.getPayment().toResponse()
            );
        }
        for (CapitalPurchase capitalPurchase : contributor.getCapitalPurchases()) {
            payments.add(
                    capitalPurchase.getPayment().toResponse()
            );
        }
        return payments;
    }

}
