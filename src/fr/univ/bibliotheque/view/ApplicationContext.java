package fr.univ.bibliotheque.view;

import fr.univ.bibliotheque.service.Catalog;
import fr.univ.bibliotheque.observer.EmailNotifier;
import fr.univ.bibliotheque.observer.UIRefresher;
import fr.univ.bibliotheque.repository.EtudiantRepository;
import fr.univ.bibliotheque.repository.MediaRepository;

import java.util.HashMap;
import java.util.Map;

public class ApplicationContext {
    
    private static ApplicationContext instance;
    
    private Catalog catalog;
    private EtudiantRepository etudiant_repository;
    private MediaRepository media_repository;
    
    private ApplicationContext() {
        initialiser();
    }
    
    public static synchronized ApplicationContext get_instance() {
        if (instance == null) {
            instance = new ApplicationContext();
        }
        return instance;
    }
    
    private void initialiser() {
        try {
            etudiant_repository = new EtudiantRepository();
            media_repository = new MediaRepository();
            
            boolean etudiants_charges = etudiant_repository.charger_depuis_xml(null);
            if (!etudiants_charges) {
                System.err.println("ATTENTION : Échec du chargement des étudiants depuis universite.xml");
            }
            
            catalog = new Catalog();
            
            Map<String, fr.univ.bibliotheque.model.Matiere> matieres_map = new HashMap<>();
            for (fr.univ.bibliotheque.model.Specialite spec : etudiant_repository.get_specialites()) {
                for (fr.univ.bibliotheque.model.Matiere matiere : spec.get_matieres()) {
                    matieres_map.put(matiere.get_code(), matiere);
                }
            }
            
            boolean medias_charges = media_repository.charger_donnees(catalog, matieres_map);
            if (!medias_charges) {
                System.err.println("ATTENTION : Échec du chargement des médias depuis medias.ser");
            }
            
            EmailNotifier email_notifier = new EmailNotifier(catalog, etudiant_repository);
            UIRefresher ui_refresher = new UIRefresher();
            catalog.ajouter_observateur(email_notifier);
            catalog.ajouter_observateur(ui_refresher);
            
        } catch (Exception e) {
            System.err.println("ERREUR CRITIQUE lors de l'initialisation :");
            System.err.println("  " + e.getMessage());
            e.printStackTrace();
            if (catalog == null) {
                catalog = new Catalog();
            }
            if (etudiant_repository == null) {
                etudiant_repository = new EtudiantRepository();
            }
            if (media_repository == null) {
                media_repository = new MediaRepository();
            }
        }
    }
    
    public Catalog get_catalog() {
        return catalog;
    }
    
    public EtudiantRepository get_etudiant_repository() {
        return etudiant_repository;
    }
    
    public MediaRepository get_media_repository() {
        return media_repository;
    }
}
