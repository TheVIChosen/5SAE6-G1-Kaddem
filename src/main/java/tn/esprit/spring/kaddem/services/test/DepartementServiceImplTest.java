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
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.services.DepartementServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class DepartementServiceImplTest {

    @InjectMocks
    private DepartementServiceImpl departementService;

    @Mock
    private DepartementRepository departementRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRetrieveAllDepartements() {
        Departement d1 = new Departement();
        Departement d2 = new Departement();
        List<Departement> departements = Arrays.asList(d1, d2);

        Mockito.when(departementRepository.findAll()).thenReturn(departements);

        List<Departement> result = departementService.retrieveAllDepartements();

        Assertions.assertEquals(2, result.size());
        Mockito.verify(departementRepository, Mockito.times(1)).findAll();
    }

    @Test
    void testAddDepartement() {
        Departement d = new Departement();

        Mockito.when(departementRepository.save(d)).thenReturn(d);

        Departement result = departementService.addDepartement(d);

        Assertions.assertEquals(d, result);
        Mockito.verify(departementRepository, Mockito.times(1)).save(d);
    }

    @Test
    void testUpdateDepartement() {
        Departement d = new Departement();

        Mockito.when(departementRepository.save(d)).thenReturn(d);

        Departement result = departementService.updateDepartement(d);

        Assertions.assertEquals(d, result);
        Mockito.verify(departementRepository, Mockito.times(1)).save(d);
    }

    @Test
    void testRetrieveDepartement() {
        Integer idDepart = 1;
        Departement d = new Departement();

        Mockito.when(departementRepository.findById(idDepart)).thenReturn(Optional.of(d));

        Departement result = departementService.retrieveDepartement(idDepart);

        Assertions.assertEquals(d, result);
        Mockito.verify(departementRepository, Mockito.times(1)).findById(idDepart);
    }

    @Test
    void testDeleteDepartement() {
        Integer idDepart = 1;
        Departement d = new Departement();

        Mockito.when(departementRepository.findById(idDepart)).thenReturn(Optional.of(d));

        departementService.deleteDepartement(idDepart);

        Mockito.verify(departementRepository, Mockito.times(1)).delete(d);
    }
}
