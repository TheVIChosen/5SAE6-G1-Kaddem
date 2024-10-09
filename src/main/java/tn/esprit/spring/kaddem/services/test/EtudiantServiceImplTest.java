package tn.esprit.spring.kaddem.services.test;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.kaddem.entities.Contrat;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Equipe;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.repositories.ContratRepository;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.repositories.EquipeRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;
import tn.esprit.spring.kaddem.services.EtudiantServiceImpl;

import java.util.Optional;

@AllArgsConstructor
public class EtudiantServiceImplTest {
    @InjectMocks
    private EtudiantServiceImpl etudiantService;

    @Mock
    private EtudiantRepository etudiantRepository;

    @Mock
    private DepartementRepository departementRepository;

    @Mock
    private ContratRepository contratRepository;

    @Mock
    private EquipeRepository equipeRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddEtudiant() {
        Etudiant etudiant = new Etudiant();
        Mockito.when(etudiantRepository.save(etudiant)).thenReturn(etudiant);

        Etudiant result = etudiantService.addEtudiant(etudiant);

        Assertions.assertEquals(etudiant, result);
        Mockito.verify(etudiantRepository, Mockito.times(1)).save(etudiant);
    }

    @Test
    void testRetrieveEtudiant() {
        Etudiant etudiant = new Etudiant();
        etudiant.setIdEtudiant(1);

        Mockito.when(etudiantRepository.findById(1)).thenReturn(Optional.of(etudiant));

        Etudiant result = etudiantService.retrieveEtudiant(1);

        Assertions.assertEquals(etudiant, result);
        Mockito.verify(etudiantRepository, Mockito.times(1)).findById(1);
    }

    @Test
    void testUpdateEtudiant() {
        Etudiant etudiant = new Etudiant();
        etudiant.setIdEtudiant(1);

        Mockito.when(etudiantRepository.save(etudiant)).thenReturn(etudiant);

        Etudiant result = etudiantService.updateEtudiant(etudiant);

        Assertions.assertEquals(etudiant, result);
        Mockito.verify(etudiantRepository, Mockito.times(1)).save(etudiant);
    }

    @Test
    void testRemoveEtudiant() {
        Etudiant etudiant = new Etudiant();
        etudiant.setIdEtudiant(1);

        Mockito.when(etudiantRepository.findById(1)).thenReturn(Optional.of(etudiant));

        etudiantService.removeEtudiant(1);

        Mockito.verify(etudiantRepository, Mockito.times(1)).delete(etudiant);
    }

    @Test
    void testAssignEtudiantToDepartement() {
        Etudiant etudiant = new Etudiant();
        Departement departement = new Departement();

        Mockito.when(etudiantRepository.findById(1)).thenReturn(Optional.of(etudiant));
        Mockito.when(departementRepository.findById(2)).thenReturn(Optional.of(departement));

        etudiantService.assignEtudiantToDepartement(1, 2);

        Assertions.assertEquals(departement, etudiant.getDepartement());
        Mockito.verify(etudiantRepository, Mockito.times(1)).save(etudiant);
    }

    @Test
    void testAddAndAssignEtudiantToEquipeAndContract() {
        Etudiant etudiant = new Etudiant();
        Contrat contrat = new Contrat();
        Equipe equipe = new Equipe();

        Mockito.when(contratRepository.findById(1)).thenReturn(Optional.of(contrat));
        Mockito.when(equipeRepository.findById(2)).thenReturn(Optional.of(equipe));

        Etudiant result = etudiantService.addAndAssignEtudiantToEquipeAndContract(etudiant, 1, 2);

        Assertions.assertEquals(etudiant, contrat.getEtudiant());
        Assertions.assertTrue(equipe.getEtudiants().contains(etudiant));
        Assertions.assertEquals(etudiant, result);
    }
}
