package com.lite.thinking.app.application.usecase;

import com.lite.thinking.app.application.dto.CompanyRequestDto;
import com.lite.thinking.app.application.dto.CompanyResponseDto;
import com.lite.thinking.app.domain.exception.EntityAlreadyExistsException;
import com.lite.thinking.app.domain.exception.EntityNotFoundException;
import com.lite.thinking.app.domain.model.Company;
import com.lite.thinking.app.domain.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceImplTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyServiceImpl companyService;

    @Test
    void createCompany_whenNitIsAvailable_savesCompany() {
        when(companyRepository.existsByNit("900")).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompanyResponseDto response = companyService.createCompany(request("900", "Empresa", true));

        assertEquals("900", response.getNit());
        assertEquals("Empresa", response.getName());
        verify(companyRepository).save(any(Company.class));
    }

    @Test
    void createCompany_whenNitExists_throwsEntityAlreadyExistsException() {
        when(companyRepository.existsByNit("900")).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class,
                () -> companyService.createCompany(request("900", "Empresa", true)));

        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void getCompanyByNit_whenExists_returnsCompany() {
        when(companyRepository.findByNit("900")).thenReturn(Optional.of(company("900", "Empresa", true)));

        CompanyResponseDto response = companyService.getCompanyByNit("900");

        assertEquals("900", response.getNit());
        assertEquals("Empresa", response.getName());
    }

    @Test
    void getAllCompanies_returnsMappedCompanies() {
        when(companyRepository.findAll()).thenReturn(List.of(
                company("900", "Empresa", true),
                company("901", "Inactiva", false)
        ));

        List<CompanyResponseDto> response = companyService.getAllCompanies();

        assertEquals(2, response.size());
        assertEquals("Inactiva", response.get(1).getName());
        assertFalse(response.get(1).isActive());
    }

    @Test
    void updateCompany_whenExists_updatesCompany() {
        when(companyRepository.findByNit("900")).thenReturn(Optional.of(company("900", "Empresa", true)));
        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompanyResponseDto response = companyService.updateCompany("900", request("900", "Nueva Empresa", false));

        assertEquals("Nueva Empresa", response.getName());
        assertFalse(response.isActive());

        ArgumentCaptor<Company> captor = ArgumentCaptor.forClass(Company.class);
        verify(companyRepository).save(captor.capture());
        assertEquals("Calle 1", captor.getValue().getAddress());
    }

    @Test
    void updateCompany_whenDoesNotExist_throwsEntityNotFoundException() {
        when(companyRepository.findByNit("900")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> companyService.updateCompany("900", request("900", "Empresa", true)));

        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void deleteCompany_whenExists_deletesCompany() {
        when(companyRepository.existsByNit("900")).thenReturn(true);

        companyService.deleteCompany("900");

        verify(companyRepository).deleteByNit("900");
    }

    @Test
    void deleteCompany_whenDoesNotExist_throwsEntityNotFoundException() {
        when(companyRepository.existsByNit("900")).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> companyService.deleteCompany("900"));

        verify(companyRepository, never()).deleteByNit("900");
    }

    private CompanyRequestDto request(String nit, String name, Boolean isActive) {
        return CompanyRequestDto.builder()
                .nit(nit)
                .name(name)
                .address("Calle 1")
                .phone("300")
                .isActive(isActive)
                .build();
    }

    private Company company(String nit, String name, boolean isActive) {
        return Company.builder()
                .nit(nit)
                .name(name)
                .address("Calle 1")
                .phone("300")
                .isActive(isActive)
                .build();
    }
}
