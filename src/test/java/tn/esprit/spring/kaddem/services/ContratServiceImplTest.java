package tn.esprit.spring.kaddem.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.kaddem.entities.Contrat;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.entities.Specialite;
import tn.esprit.spring.kaddem.repositories.ContratRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

class ContratServiceImplTest {

    @InjectMocks
    private ContratServiceImpl contratService; // Service testé

    @Mock
    private ContratRepository contratRepository; // Mock

    @Mock
    private EtudiantRepository etudiantRepository; // Mock

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialisation des mocks
    }

    @Test
    void testRetrieveAllContrats() {
        Contrat c1 = new Contrat();
        Contrat c2 = new Contrat();
        List<Contrat> contrats = Arrays.asList(c1, c2);

        Mockito.when(contratRepository.findAll()).thenReturn(contrats);

        List<Contrat> result = contratService.retrieveAllContrats();

        Assertions.assertEquals(2, result.size());
        Mockito.verify(contratRepository, Mockito.times(1)).findAll();
    }

    @Test
    void testAddContrat() {
        Contrat c = new Contrat();

        Mockito.when(contratRepository.save(c)).thenReturn(c);

        Contrat result = contratService.addContrat(c);

        Assertions.assertEquals(c, result);
        Mockito.verify(contratRepository, Mockito.times(1)).save(c);
    }

    @Test
    void testUpdateContrat() {
        Contrat c = new Contrat();

        Mockito.when(contratRepository.save(c)).thenReturn(c);

        Contrat result = contratService.updateContrat(c);

        Assertions.assertEquals(c, result);
        Mockito.verify(contratRepository, Mockito.times(1)).save(c);
    }

    @Test
    void testRetrieveContrat() {
        Integer idContrat = 1;
        Contrat c = new Contrat();

        Mockito.when(contratRepository.findById(idContrat)).thenReturn(Optional.of(c));

        Contrat result = contratService.retrieveContrat(idContrat);

        Assertions.assertEquals(c, result);
        Mockito.verify(contratRepository, Mockito.times(1)).findById(idContrat);
    }

    @Test
    void testRemoveContrat() {
        Integer idContrat = 1;
        Contrat c = new Contrat();

        Mockito.when(contratRepository.findById(idContrat)).thenReturn(Optional.of(c));

        contratService.removeContrat(idContrat);

        Mockito.verify(contratRepository, Mockito.times(1)).delete(c);
    }

    @Test
    void testAffectContratToEtudiant() {
        Integer idContrat = 1;
        String nomE = "John";
        String prenomE = "Doe";
        Etudiant e = new Etudiant();
        Contrat c = new Contrat();

        Mockito.when(etudiantRepository.findByNomEAndPrenomE(nomE, prenomE)).thenReturn(e);
        Mockito.when(contratRepository.findByIdContrat(idContrat)).thenReturn(c);

        c.setEtudiant(e); // Simule l'affectation

        Contrat result = contratService.affectContratToEtudiant(idContrat, nomE, prenomE);

        Assertions.assertEquals(c, result);
        Mockito.verify(contratRepository, Mockito.times(1)).save(c);
    }

    @Test
    void testGetChiffreAffaireEntreDeuxDates() {
        // Dates de l'intervalle
        Date startDate = new Date(System.currentTimeMillis() - 100000); // il y a un peu de temps
        Date endDate = new Date(System.currentTimeMillis() + 100000);   // un peu dans le futur

        // Contrats avec des dates valides dans l'intervalle
        Contrat c1 = new Contrat();
        c1.setSpecialite(Specialite.IA);
        c1.setDateDebutContrat(new Date(System.currentTimeMillis() - 50000));
        c1.setDateFinContrat(new Date(System.currentTimeMillis() + 50000));

        Contrat c2 = new Contrat();
        c2.setSpecialite(Specialite.CLOUD);
        c2.setDateDebutContrat(new Date(System.currentTimeMillis() - 50000));
        c2.setDateFinContrat(new Date(System.currentTimeMillis() + 50000));

        List<Contrat> contrats = Arrays.asList(c1, c2);

        Mockito.when(contratRepository.findAll()).thenReturn(contrats);

        // Appel de la méthode testée
        float result = contratService.getChiffreAffaireEntreDeuxDates(startDate, endDate);

        // Vérification
        Assertions.assertTrue(result > 0, "Le chiffre d'affaire devrait être supérieur à 0");
        Mockito.verify(contratRepository, Mockito.times(1)).findAll();
    }

}
