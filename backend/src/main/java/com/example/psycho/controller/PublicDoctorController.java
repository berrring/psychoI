package com.example.psycho.controller;

import com.example.psycho.dto.PublicDoctorDetailsDto;
import com.example.psycho.dto.PublicDoctorSummaryDto;
import com.example.psycho.service.PublicDoctorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/doctors")
public class PublicDoctorController {
    private final PublicDoctorService publicDoctorService;

    public PublicDoctorController(PublicDoctorService publicDoctorService) {
        this.publicDoctorService = publicDoctorService;
    }

    @GetMapping
    public ResponseEntity<Page<PublicDoctorSummaryDto>> list(@RequestParam(required = false) String query,
                                                             Pageable pageable) {
        return ResponseEntity.ok(publicDoctorService.getDoctors(query, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicDoctorDetailsDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(publicDoctorService.getDoctorById(id));
    }
}
