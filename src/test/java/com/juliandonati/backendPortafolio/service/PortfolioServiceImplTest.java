package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.domain.Portfolio;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.repository.PortfolioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceImplTest {
    @Mock
    PortfolioRepository portfolioRepository;

    @InjectMocks
    PortfolioServiceImpl portfolioService;

    @Test
    void testFindPortfolioByIdReturnsPortfolio() {
        Long mockId = 1L;
        Portfolio mockPortfolio = new Portfolio();
        when(portfolioRepository.findById(mockId)).thenReturn(Optional.of(mockPortfolio));

        Portfolio result = portfolioService.findById(mockId);

        assertNotNull(result);
        verify(portfolioRepository,times(1)).findById(mockId);
    }

    @Test
    void testFindPortfolioByIdThrowsResourceNotFoundException(){
        Long mockId = 999L;
        when(portfolioRepository.findById(mockId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()->portfolioService.findById(mockId));
        verify(portfolioRepository,times(1)).findById(mockId);
    }

    @Test
    void testFindPortfolioByOwnerUsernameReturnsPortfolio() {
        String ownerUsername = "usuario.ominoso";
        Portfolio mockPortfolio = new Portfolio();
        when(portfolioRepository.findByOwnerUsername(ownerUsername)).thenReturn(Optional.of(mockPortfolio));

        Portfolio result = portfolioService.findByOwnerUsername(ownerUsername);

        assertNotNull(result);
        verify(portfolioRepository,times(1)).findByOwnerUsername(ownerUsername);
    }

    @Test
    void testFindPortfolioByOwnerUsernameThrowsResourceNotFoundException() {
        String ownerUsername = "usuario.inexistente";
        when(portfolioRepository.findByOwnerUsername(ownerUsername)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()->portfolioService.findByOwnerUsername(ownerUsername));
        verify(portfolioRepository,times(1)).findByOwnerUsername(ownerUsername);
    }

    @Test
    void testExistsPortfolioByOwnerUsernameReturnsTrue() {
        String ownerUsername = "usuario.existente";
        when(portfolioRepository.existsByOwnerUsername(ownerUsername)).thenReturn(true);

        boolean result = portfolioService.existsByOwnerUsername(ownerUsername);

        assertTrue(result);
        verify(portfolioRepository,times(1)).existsByOwnerUsername(ownerUsername);
    }

    @Test
    void testExistsPortfolioByOwnerUsernameReturnsFalse() {
        String ownerUsername = "usuario.inexistente";
        when(portfolioRepository.existsByOwnerUsername(ownerUsername)).thenReturn(false);

        boolean result = portfolioService.existsByOwnerUsername(ownerUsername);

        assertFalse(result);
        verify(portfolioRepository,times(1)).existsByOwnerUsername(ownerUsername);
    }

    @Test
    void testSavePortfolioSavesPortfolioSuccessfully() {
        Portfolio mockPortfolio = new Portfolio();
        when(portfolioRepository.save(mockPortfolio)).thenReturn(mockPortfolio);

        Portfolio result = portfolioService.save(mockPortfolio);

        assertNotNull(result);
        verify(portfolioRepository,times(1)).save(mockPortfolio);
    }

    @Test
    void testUpdatePortfolioUpdatesPortfolioSuccessfully() {
        Long mockId = 45L;
        Portfolio mockOldPortfolio = new Portfolio();
        when(portfolioRepository.findById(mockId)).thenReturn(Optional.of(mockOldPortfolio));
        Portfolio mockUpdatedPortfolio = new Portfolio();
        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(mockUpdatedPortfolio);

        Portfolio mockNewPortfolio = new Portfolio();
        Portfolio result = portfolioService.update(mockNewPortfolio,mockId);

        assertNotNull(result);
        verify(portfolioRepository,times(1)).findById(mockId);
        verify(portfolioRepository,times(1)).save(any(Portfolio.class));
    }

    @Test
    void testUpdatePortfolioThrowsResourceNotFoundException() {
        Long mockId = 999L;
        when(portfolioRepository.findById(mockId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()->portfolioService.update(new Portfolio(),mockId));
        verify(portfolioRepository,times(1)).findById(mockId);
        verify(portfolioRepository,never()).save(any(Portfolio.class));
    }

    @Test
    void testDeletePortfolioByIdDeletesPortfolioSuccesfully() {
        Long mockId = 34L;
        when(portfolioRepository.existsById(mockId)).thenReturn(true);

        assertDoesNotThrow(()->portfolioService.deleteById(mockId),"El método falló y lanzó una excepción, debería haber finalizado con éxito y silenciosamente");
        verify(portfolioRepository,times(1)).existsById(mockId);
        verify(portfolioRepository,times(1)).deleteById(mockId);
    }

    @Test
    void testDeletePortfolioByIdThrowsResourceNotFoundException() {
        Long mockId = 999L;
        when(portfolioRepository.existsById(mockId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,()->portfolioService.deleteById(mockId));
        verify(portfolioRepository,times(1)).existsById(mockId);
        verify(portfolioRepository,never()).deleteById(any(Long.class));
    }
}