package tn.esprit.spring.kaddem.controllers;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.services.IUniversiteService;

import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/universite")
public class UniversiteRestController {

	private static final Logger logger = LoggerFactory.getLogger(UniversiteRestController.class);

	@Autowired
	IUniversiteService universiteService;

	// http://localhost:8089/Kaddem/universite/retrieve-all-universites
	@GetMapping("/retrieve-all-universites")
	public List<Universite> getUniversites() {
		logger.info("Récupération de toutes les universités");
		List<Universite> listUniversites = universiteService.retrieveAllUniversites();
		logger.info("Nombre d'universités récupérées: {}", listUniversites.size());
		return listUniversites;
	}

	// http://localhost:8089/Kaddem/universite/retrieve-universite/8
	@GetMapping("/retrieve-universite/{universite-id}")
	public Universite retrieveUniversite(@PathVariable("universite-id") Integer universiteId) {
		logger.info("Récupération de l'université avec l'ID: {}", universiteId);
		Universite universite = universiteService.retrieveUniversite(universiteId);
		logger.info("Université récupérée: {}", universite.getNomUniv());
		return universite;
	}

	// http://localhost:8089/Kaddem/universite/add-universite
	@PostMapping("/add-universite")
	public Universite addUniversite(@RequestBody Universite u) {
		logger.info("Ajout d'une nouvelle université: {}", u.getNomUniv());
		Universite universite = universiteService.addUniversite(u);
		logger.info("Université ajoutée: {}", universite.getNomUniv());
		return universite;
	}

	// http://localhost:8089/Kaddem/universite/remove-universite/1
	@DeleteMapping("/remove-universite/{universite-id}")
	public void removeUniversite(@PathVariable("universite-id") Integer universiteId) {
		logger.info("Suppression de l'université avec l'ID: {}", universiteId);
		universiteService.deleteUniversite(universiteId);
		logger.info("Université avec l'ID {} supprimée", universiteId);
	}

	// http://localhost:8089/Kaddem/universite/update-universite
	@PutMapping("/update-universite")
	public Universite updateUniversite(@RequestBody Universite u) {
		logger.info("Mise à jour de l'université: {}", u.getNomUniv());
		Universite updatedUniversite = universiteService.updateUniversite(u);
		logger.info("Université mise à jour: {}", updatedUniversite.getNomUniv());
		return updatedUniversite;
	}

	@PutMapping(value = "/affecter-universite-departement/{universiteId}/{departementId}")
	public void affectertUniversiteToDepartement(@PathVariable("universiteId") Integer universiteId, @PathVariable("departementId") Integer departementId) {
		logger.info("Affectation de l'université avec l'ID {} au département avec l'ID {}", universiteId, departementId);
		universiteService.assignUniversiteToDepartement(universiteId, departementId);
		logger.info("Université avec l'ID {} affectée au département avec l'ID {}", universiteId, departementId);
	}

	@GetMapping(value = "/listerDepartementsUniversite/{idUniversite}")
	public Set<Departement> listerDepartementsUniversite(@PathVariable("idUniversite") Integer idUniversite) {
		logger.info("Liste des départements pour l'université avec l'ID {}", idUniversite);
		Set<Departement> departements = universiteService.retrieveDepartementsByUniversite(idUniversite);
		logger.info("Nombre de départements récupérés: {}", departements.size());
		return departements;
	}
}
