package com.lite.thinking.app.application.usecase;

import com.lite.thinking.app.application.dto.CompanyRequestDto;
import com.lite.thinking.app.application.dto.CompanyResponseDto;
import com.lite.thinking.app.application.mapper.CompanyMapper;
import com.lite.thinking.app.domain.exception.EntityAlreadyExistsException;
import com.lite.thinking.app.domain.exception.EntityNotFoundException;
import com.lite.thinking.app.domain.model.Company;
import com.lite.thinking.app.domain.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Override
    @Transactional
    public CompanyResponseDto createCompany(CompanyRequestDto requestDto) {
        if (companyRepository.existsByNit(requestDto.getNit())) {
            throw new EntityAlreadyExistsException("La empresa con NIT " + requestDto.getNit() + " ya existe.");
        }
        Company company = CompanyMapper.toDomain(requestDto);
        Company savedCompany = companyRepository.save(company);
        return CompanyMapper.toResponseDto(savedCompany);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyResponseDto getCompanyByNit(String nit) {
        Company company = companyRepository.findByNit(nit)
                .orElseThrow(() -> new EntityNotFoundException("La empresa con NIT " + nit + " no fue encontrada."));
        return CompanyMapper.toResponseDto(company);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyResponseDto> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(CompanyMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CompanyResponseDto updateCompany(String nit, CompanyRequestDto requestDto) {
        Company existingCompany = companyRepository.findByNit(nit)
                .orElseThrow(() -> new EntityNotFoundException("La empresa con NIT " + nit + " no fue encontrada."));
        
        existingCompany.setName(requestDto.getName());
        existingCompany.setAddress(requestDto.getAddress());
        existingCompany.setPhone(requestDto.getPhone());
        if (requestDto.getIsActive() != null) {
            existingCompany.setActive(requestDto.getIsActive());
        }
        
        Company updatedCompany = companyRepository.save(existingCompany);
        return CompanyMapper.toResponseDto(updatedCompany);
    }

    @Override
    @Transactional
    public void deleteCompany(String nit) {
        if (!companyRepository.existsByNit(nit)) {
            throw new EntityNotFoundException("La empresa con NIT " + nit + " no fue encontrada.");
        }
        companyRepository.deleteByNit(nit);
    }
}
