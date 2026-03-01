package com.example.psycho.service;

import com.example.psycho.dto.PublicDoctorDetailsDto;
import com.example.psycho.dto.PublicDoctorSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PublicDoctorService {
    Page<PublicDoctorSummaryDto> getDoctors(String query, Pageable pageable);

    PublicDoctorDetailsDto getDoctorById(Long id);
}
