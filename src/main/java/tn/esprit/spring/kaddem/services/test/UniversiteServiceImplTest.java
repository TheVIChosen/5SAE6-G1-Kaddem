package tn.esprit.spring.kaddem.services.test;


import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.repositories.UniversiteRepository;
import tn.esprit.spring.kaddem.services.UniversiteServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
public class UniversiteServiceImplTest {

    @InjectMocks
    private UniversiteServiceImpl universiteService;

    @Mock
    private UniversiteRepository universiteRepository;

    @Mock
    private DepartementRepository departementRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRetrieveAllUniversites() {
        Universite u1 = new Universite();
        Universite u2 = new Universite();
        List<Universite> universites = Arrays.asList(u1, u2);

        Mockito.when(universiteRepository.findAll()).thenReturn(universites);

        List<Universite> result = universiteService.retrieveAllUniversites();

        Assertions.assertEquals(2, result.size());
        Mockito.verify(universiteRepository, Mockito.times(1)).findAll();
    }

    @Test
    void testAddUniversite() {
        Universite u = new Universite();

        Mockito.when(universiteRepository.save(u)).thenReturn(u);

        Universite result = universiteService.addUniversite(u);

        Assertions.assertEquals(u, result);
        Mockito.verify(universiteRepository, Mockito.times(1)).save(u);
    }

    @Test
    void testUpdateUniversite() {
        Universite u = new Universite();

        Mockito.when(universiteRepository.save(u)).thenReturn(u);

        Universite result = universiteService.updateUniversite(u);

        Assertions.assertEquals(u, result);
        Mockito.verify(universiteRepository, Mockito.times(1)).save(u);
    }

    @Test
    void testRetrieveUniversite() {
        Integer idUniversite = 1;
        Universite u = new Universite();

        Mockito.when(universiteRepository.findById(idUniversite)).thenReturn(Optional.of(u));

        Universite result = universiteService.retrieveUniversite(idUniversite);

        Assertions.assertEquals(u, result);
        Mockito.verify(universiteRepository, Mockito.times(1)).findById(idUniversite);
    }

    @Test
    void testDeleteUniversite() {
        Integer idUniversite = 1;
        Universite u = new Universite();

        Mockito.when(universiteRepository.findById(idUniversite)).thenReturn(Optional.of(u));

        universiteService.deleteUniversite(idUniversite);

        Mockito.verify(universiteRepository, Mockito.times(1)).delete(u);
    }

    @Test
    void testAssignUniversiteToDepartement() {
        Integer idUniversite = 1;
        Integer idDepartement = 2;
        Universite u = new Universite();
        Departement d = new Departement();

        Mockito.when(universiteRepository.findById(idUniversite)).thenReturn(Optional.of(u));
        Mockito.when(departementRepository.findById(idDepartement)).thenReturn(Optional.of(d));

        universiteService.assignUniversiteToDepartement(idUniversite, idDepartement);

        Assertions.assertTrue(u.getDepartements().contains(d));
        Mockito.verify(universiteRepository, Mockito.times(1)).save(u);
    }

    @Test
    void testRetrieveDepartementsByUniversite() {
        Integer idUniversite = 1;
        Universite u = new Universite();
        Set<Departement> departements = Set.of(new Departement(), new Departement());
        u.setDepartements(departements);

        Mockito.when(universiteRepository.findById(idUniversite)).thenReturn(Optional.of(u));

        Set<Departement> result = universiteService.retrieveDepartementsByUniversite(idUniversite);

        Assertions.assertEquals(departements, result);
        Mockito.verify(universiteRepository, Mockito.times(1)).findById(idUniversite);
    }
}