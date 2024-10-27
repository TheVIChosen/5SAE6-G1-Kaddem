package tn.esprit.spring.kaddem.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.repositories.UniversiteRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class UniversiteServiceImplTest {

    @Mock
    private UniversiteRepository universiteRepository;

    @Mock
    private DepartementRepository departementRepository;

    @InjectMocks
    private UniversiteServiceImpl universiteService;

    private Universite universite;
    private Departement departement;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        universite = new Universite(1, "Université de Test");
        departement = new Departement(1, "Département de Test");
        Set<Departement> departements = new HashSet<>();
        departements.add(departement);
        universite.setDepartements(departements);
    }

    @Test
    void testRetrieveAllUniversites() {
        List<Universite> universites = new ArrayList<>();
        universites.add(universite);
        when(universiteRepository.findAll()).thenReturn(universites);

        List<Universite> result = universiteService.retrieveAllUniversites();

        assertEquals(1, result.size());
        verify(universiteRepository, times(1)).findAll();
    }

    @Test
    void testAddUniversite() {
        when(universiteRepository.save(any(Universite.class))).thenReturn(universite);

        Universite result = universiteService.addUniversite(universite);

        assertNotNull(result);
        assertEquals("Université de Test", result.getNomUniv());
        verify(universiteRepository, times(1)).save(universite);
    }

    @Test
    void testUpdateUniversite() {
        when(universiteRepository.save(any(Universite.class))).thenReturn(universite);

        Universite result = universiteService.updateUniversite(universite);

        assertNotNull(result);
        assertEquals("Université de Test", result.getNomUniv());
        verify(universiteRepository, times(1)).save(universite);
    }

    @Test
    void testRetrieveUniversite() {
        when(universiteRepository.findById(anyInt())).thenReturn(Optional.of(universite));

        Universite result = universiteService.retrieveUniversite(1);

        assertNotNull(result);
        assertEquals(1, result.getIdUniv());
        verify(universiteRepository, times(1)).findById(1);
    }

    @Test
    void testRetrieveUniversite_NotFound() {
        when(universiteRepository.findById(anyInt())).thenReturn(Optional.empty());

        Universite result = universiteService.retrieveUniversite(2);

        assertNull(result);
        verify(universiteRepository, times(1)).findById(2);
    }

    @Test
    void testDeleteUniversite() {
        when(universiteRepository.findById(anyInt())).thenReturn(Optional.of(universite));

        universiteService.deleteUniversite(1);

        verify(universiteRepository, times(1)).delete(universite);
    }

    @Test
    void testDeleteUniversite_NotFound() {
        when(universiteRepository.findById(anyInt())).thenReturn(Optional.empty());

        universiteService.deleteUniversite(2); // Attempt to delete non-existing university

        verify(universiteRepository, never()).delete(any());
    }

    @Test
    void testAssignUniversiteToDepartement() {
        when(universiteRepository.findById(1)).thenReturn(Optional.of(universite));
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));

        universiteService.assignUniversiteToDepartement(1, 1);

        assertTrue(universite.getDepartements().contains(departement));
        verify(universiteRepository, times(1)).save(universite);
    }

    @Test
    void testAssignUniversiteToDepartement_UniversityNotFound() {
        when(universiteRepository.findById(1)).thenReturn(Optional.empty());
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));

        universiteService.assignUniversiteToDepartement(1, 1);

        verify(universiteRepository, never()).save(any());
    }

    @Test
    void testRetrieveDepartementsByUniversite() {
        when(universiteRepository.findById(1)).thenReturn(Optional.of(universite));

        Set<Departement> result = universiteService.retrieveDepartementsByUniversite(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(departement));
        verify(universiteRepository, times(1)).findById(1);
    }

    @Test
    void testRetrieveDepartementsByUniversite_NotFound() {
        when(universiteRepository.findById(1)).thenReturn(Optional.empty());

        Set<Departement> result = universiteService.retrieveDepartementsByUniversite(1);

        assertNull(result);
        verify(universiteRepository, times(1)).findById(1);
    }
}
