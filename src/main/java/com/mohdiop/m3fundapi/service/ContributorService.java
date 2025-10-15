package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.request.create.CreateContributorRequest;
import com.mohdiop.m3fundapi.dto.response.ContributorResponse;
import com.mohdiop.m3fundapi.repository.ContributorRepository;
import com.mohdiop.m3fundapi.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

@Service
public class ContributorService {

    private final ContributorRepository contributorRepository;
    private final UserRepository userRepository;

    public ContributorService(ContributorRepository contributorRepository, UserRepository userRepository) {
        this.contributorRepository = contributorRepository;
        this.userRepository = userRepository;
    }

    public ContributorResponse createContributor(
            CreateContributorRequest createContributorRequest
    ) throws BadRequestException {
        if (userRepository.findByEmail(createContributorRequest.email()).isPresent()) {
            throw new BadRequestException("Email invalide!");
        }
        if (userRepository.findByPhone(createContributorRequest.phone()).isPresent()) {
            throw new BadRequestException("Numéro de téléphone invalide!");
        }
        return contributorRepository.save(
                createContributorRequest.toContributor()
        ).toResponse();
    }
}
