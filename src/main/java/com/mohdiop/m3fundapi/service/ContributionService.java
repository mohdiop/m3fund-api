package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.response.ContributionResponse;
import com.mohdiop.m3fundapi.dto.response.GiftResponse;
import com.mohdiop.m3fundapi.dto.response.VolunteerResponse;
import com.mohdiop.m3fundapi.entity.CapitalPurchase;
import com.mohdiop.m3fundapi.entity.Volunteer;
import com.mohdiop.m3fundapi.repository.GiftRepository;
import com.mohdiop.m3fundapi.repository.VolunteerRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ContributionService {

    private final GiftRepository giftRepository;
    private final VolunteerRepository volunteerRepository;

    public ContributionService(GiftRepository giftRepository, VolunteerRepository volunteerRepository) {
        this.giftRepository = giftRepository;
        this.volunteerRepository = volunteerRepository;
    }

    public ContributionResponse getAllContributorSContribution(
            Long contributorId
    ) {
        Set<GiftResponse> gifts = new HashSet<>();
        Set<VolunteerResponse> volunteering = new HashSet<>();
        gifts = giftRepository.findByContributorId(contributorId).stream().map(e -> e.toResponse(new ArrayList<>())).collect(Collectors.toSet());
        volunteering = volunteerRepository.findByContributorId(contributorId).stream().map(Volunteer::toResponse).collect(Collectors.toSet());
        return new ContributionResponse(gifts, volunteering);
    }
}
