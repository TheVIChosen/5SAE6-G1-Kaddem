package tn.esprit.spring.kaddem.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;

class DepartementServiceImplTest {

    @Mock
    private DepartementRepository departementRepository;

    @InjectMocks
    private DepartementServiceImpl departementService;

    private Departement departement;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        departement = new Departement(1, "Departement A"); // Create a sample Departement object
    }

    @Test
    void testRetrieveAllDepartements() {
        List<Departement> departements = new ArrayList<>();
        departements.add(departement);
        when(departementRepository.findAll()).thenReturn(departements); // Mock the repository response

        List<Departement> result = departementService.retrieveAllDepartements(); // Call the service method

        assertEquals(1, result.size()); // Check the size of the result
        verify(departementRepository, times(1)).findAll(); // Verify the repository interaction
    }

    @Test
    void testAddDepartement() {
        when(departementRepository.save(any(Departement.class))).thenReturn(departement); // Mock the save method

        Departement result = departementService.addDepartement(departement); // Call the service method

        assertNotNull(result); // Check the result is not null
        assertEquals("Departement A", result.getNomDepart()); // Verify the properties of the result
        verify(departementRepository, times(1)).save(departement); // Verify the save call
    }

    @Test
    void testDeleteDepartement() {
        when(departementRepository.findById(anyInt())).thenReturn(Optional.of(departement)); // Mock finding the departement

        departementService.deleteDepartement(1); // Call the service method

        verify(departementRepository, times(1)).delete(departement); // Verify the delete call
    }

    @Test
    void testRetrieveDepartement() {
        when(departementRepository.findById(anyInt())).thenReturn(Optional.of(departement)); // Mock finding the departement

        Departement result = departementService.retrieveDepartement(1); // Call the service method

        assertNotNull(result); // Check the result is not null
        assertEquals(1, result.getIdDepart()); // Verify the ID of the result
        verify(departementRepository, times(1)).findById(1); // Verify the findById call
    }

    @Test
    void testUpdateDepartement() {
        when(departementRepository.save(any(Departement.class))).thenReturn(departement); // Mock the save method

        Departement result = departementService.updateDepartement(departement); // Call the service method

        assertNotNull(result); // Check the result is not null
        assertEquals("Departement A", result.getNomDepart()); // Verify the properties of the result
        verify(departementRepository, times(1)).save(departement); // Verify the save call
    }
}
