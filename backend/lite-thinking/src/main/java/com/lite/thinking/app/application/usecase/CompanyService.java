package com.lite.thinking.app.application.usecase;

import com.lite.thinking.app.application.dto.CompanyRequestDto;
import com.lite.thinking.app.application.dto.CompanyResponseDto;
import java.util.List;

public interface CompanyService {
    CompanyResponseDto createCompany(CompanyRequestDto requestDto);
    CompanyResponseDto getCompanyByNit(String nit);
    List<CompanyResponseDto> getAllCompanies();
    CompanyResponseDto updateCompany(String nit, CompanyRequestDto requestDto);
    void deleteCompany(String nit);
}
