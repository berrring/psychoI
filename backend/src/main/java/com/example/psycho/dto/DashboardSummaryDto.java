package com.example.psycho.dto;

public record DashboardSummaryDto(
        long clinics,
        long doctors,
        long patients,
        long appointments,
        long articles
) {
}