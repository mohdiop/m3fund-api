package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.dto.response.CampaignResponse;
import com.mohdiop.m3fundapi.entity.ProjectOwner;
import com.mohdiop.m3fundapi.entity.enums.CampaignState;
import com.mohdiop.m3fundapi.entity.enums.CampaignType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectOwnerRepository extends JpaRepository<ProjectOwner, Long> {
}
