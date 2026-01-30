package com.example.demo.Service;

import com.example.demo.Dto.KycRequestDto;
import com.example.demo.Dto.KycResponseDto;
import com.example.demo.Entity.KycDetails;
import com.example.demo.Entity.KycStatus;
import com.example.demo.repository.KycRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class KycService {

    private final KycRepository kycRepository;

    @Transactional
    public KycResponseDto submitKyc(String username, KycRequestDto request) {

        if (kycRepository.findByUsername(username).isPresent()) {
            return KycResponseDto.builder()
                    .status("FAILED")
                    .message("KYC already submitted")
                    .build();
        }

        KycDetails kyc = KycDetails.builder()
                .username(username)
                .aadhaarNumber(request.getAadhaarNumber())
                .panNumber(request.getPanNumber())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .status(KycStatus.APPROVED)
                .submittedAt(LocalDateTime.now())
                .build();

        kycRepository.save(kyc);

        return KycResponseDto.builder()
                .status("Approved")
                .message("KYC submitted successfully")
                .build();
    }

    public KycStatus getKycStatus(String username) {
        return kycRepository.findByUsername(username)
                .map(KycDetails::getStatus)
                .orElse(KycStatus.NOT_SUBMITTED);
    }
}
