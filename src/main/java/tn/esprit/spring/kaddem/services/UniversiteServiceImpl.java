package tn.esprit.spring.kaddem.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.repositories.UniversiteRepository;

import java.util.List;
import java.util.Set;

@Service
public class UniversiteServiceImpl implements IUniversiteService {
    @Autowired
    UniversiteRepository universiteRepository;

    @Autowired
    DepartementRepository departementRepository;

    private static final Logger logger = LoggerFactory.getLogger(UniversiteServiceImpl.class);

    public List<Universite> retrieveAllUniversites() {
        logger.info("Retrieving all universities");
        List<Universite> universities = (List<Universite>) universiteRepository.findAll();
        logger.info("Retrieved {} universities", universities.size());
        return universities;
    }

    public Universite addUniversite(Universite u) {
        logger.info("Adding university: {}", u);
        Universite savedUniversite = universiteRepository.save(u);
        logger.info("Added university with ID: {}", savedUniversite.getIdUniv());
        return savedUniversite;
    }

    public Universite updateUniversite(Universite u) {
        logger.info("Updating university with ID: {}", u.getIdUniv());
        Universite updatedUniversite = universiteRepository.save(u);
        logger.info("Updated university: {}", updatedUniversite);
        return updatedUniversite;
    }

    public Universite retrieveUniversite(Integer idUniversite) {
        logger.info("Retrieving university with ID: {}", idUniversite);
        Universite u = universiteRepository.findById(idUniversite).orElse(null);
        if (u == null) {
            logger.warn("University with ID: {} not found", idUniversite);
        } else {
            logger.info("Retrieved university: {}", u);
        }
        return u;
    }

    public void deleteUniversite(Integer idUniversite) {
        logger.info("Deleting university with ID: {}", idUniversite);
        Universite universite = retrieveUniversite(idUniversite);
        if (universite != null) {
            universiteRepository.delete(universite);
            logger.info("Deleted university with ID: {}", idUniversite);
        } else {
            logger.warn("Attempted to delete university with ID: {} that does not exist", idUniversite);
        }
    }

    public void assignUniversiteToDepartement(Integer idUniversite, Integer idDepartement) {
        logger.info("Assigning university ID: {} to department ID: {}", idUniversite, idDepartement);
        Universite u = universiteRepository.findById(idUniversite).orElse(null);
        Departement d = departementRepository.findById(idDepartement).orElse(null);

        if (u != null && d != null) {
            u.getDepartements().add(d);
            universiteRepository.save(u);
            logger.info("Assigned university {} to department {}", u, d);
        } else {
            logger.warn("University or department not found for IDs: {}, {}", idUniversite, idDepartement);
        }
    }

    public Set<Departement> retrieveDepartementsByUniversite(Integer idUniversite) {
        logger.info("Retrieving departments for university ID: {}", idUniversite);
        Universite u = universiteRepository.findById(idUniversite).orElse(null);
        if (u == null) {
            logger.warn("University with ID: {} not found", idUniversite);
            return null;
        }
        Set<Departement> departements = u.getDepartements();
        logger.info("Retrieved {} departments for university ID: {}", departements.size(), idUniversite);
        return departements;
    }
}
