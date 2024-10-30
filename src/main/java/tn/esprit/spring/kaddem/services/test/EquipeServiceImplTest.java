package tn.esprit.spring.kaddem.services.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.kaddem.entities.Equipe;
import tn.esprit.spring.kaddem.entities.Niveau;
import tn.esprit.spring.kaddem.repositories.EquipeRepository;
import tn.esprit.spring.kaddem.services.EquipeServiceImpl;


@AllArgsConstructor
class EquipeServiceImplTest {

    @Mock
    private EquipeRepository equipeRepository;

    @InjectMocks
    private EquipeServiceImpl equipeService;

    private Equipe equipe;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        equipe = new Equipe(1, "Equipe A", Niveau.JUNIOR);
    }

    @Test
    void testRetrieveAllEquipes() {
        List<Equipe> equipes = new ArrayList<>();
        equipes.add(equipe);
        Mockito.when(equipeRepository.findAll()).thenReturn(equipes);

        List<Equipe> result = equipeService.retrieveAllEquipes();

        Assertions.assertEquals(1, result.size());
        Mockito.verify(equipeRepository, Mockito.times(1)).findAll();
    }

    @Test
    void testAddEquipe() {
        when(equipeRepository.save(any(Equipe.class))).thenReturn(equipe);

        Equipe result = equipeService.addEquipe(equipe);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Equipe A", result.getNomEquipe());
        Mockito.verify(equipeRepository, Mockito.times(1)).save(equipe);
    }

    @Test
    void testDeleteEquipe() {
        when(equipeRepository.findById(anyInt())).thenReturn(Optional.of(equipe));

        equipeService.deleteEquipe(1);

        Mockito.verify(equipeRepository, Mockito.times(1)).delete(equipe);
    }

    @Test
    void testRetrieveEquipe() {
        when(equipeRepository.findById(anyInt())).thenReturn(Optional.of(equipe));

        Equipe result = equipeService.retrieveEquipe(1);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getIdEquipe());
        Mockito.verify(equipeRepository, Mockito.times(1)).findById(1);
    }

    @Test
    void testUpdateEquipe() {
        when(equipeRepository.save(any(Equipe.class))).thenReturn(equipe);

        Equipe result = equipeService.updateEquipe(equipe);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Equipe A", result.getNomEquipe());
        Mockito.verify(equipeRepository, Mockito.times(1)).save(equipe);
    }
}
