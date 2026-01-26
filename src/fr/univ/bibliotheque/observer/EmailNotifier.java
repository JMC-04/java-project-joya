package fr.univ.bibliotheque.observer;

import fr.univ.bibliotheque.service.Catalog;
import fr.univ.bibliotheque.repository.EtudiantRepository;
import fr.univ.bibliotheque.model.*;

import java.util.*;

/**
 * Observateur qui envoie des notifications par email aux étudiants concernés
 * lors de l'ajout d'un nouveau média.
 * 
 * @author Bibliothèque Universitaire
 * @version 2.0
 */
public class EmailNotifier implements MediaObserver {
    
    private final Catalog catalog;
    private final EtudiantRepository etudiant_repository;
    
    /**
     * Constructeur.
     * 
     * @param catalog Le catalogue des médias
     * @param etudiant_repository Le repository des étudiants
     */
    public EmailNotifier(Catalog catalog, EtudiantRepository etudiant_repository) {
        this.catalog = Objects.requireNonNull(catalog, "Le catalogue ne peut pas être null");
        this.etudiant_repository = Objects.requireNonNull(etudiant_repository, 
                                                         "Le repository des étudiants ne peut pas être null");
    }
    
    @Override
    public void notify(Media nouveau_media) {
        Set<String> matieres_media = catalog.get_matieres_media(nouveau_media.get_id())
                .stream()
                .map(Matiere::get_code)
                .collect(java.util.stream.Collectors.toSet());
        
        if (matieres_media.isEmpty()) {
            System.out.println("[EMAIL] Nouveau média '" + nouveau_media.get_titre() + 
                             "' ajouté mais aucune matière associée. Aucun email envoyé.");
            return;
        }
        
        Set<String> etudiants_notifies = new HashSet<>();
        
        for (Etudiant etudiant : etudiant_repository.get_etudiants()) {
            List<String> codes_matieres_etudiant = etudiant.get_codes_matieres();
            
            boolean etudiant_concerne = false;
            for (String code_matiere : codes_matieres_etudiant) {
                if (matieres_media.contains(code_matiere)) {
                    etudiant_concerne = true;
                    break;
                }
            }
            
            if (etudiant_concerne) {
                etudiants_notifies.add(etudiant.get_username());
                envoyer_email_simule(etudiant, nouveau_media);
            }
        }
        
        if (etudiants_notifies.isEmpty()) {
            System.out.println("[EMAIL] Nouveau média '" + nouveau_media.get_titre() + 
                             "' ajouté mais aucun étudiant concerné. Aucun email envoyé.");
        } else {
            System.out.println("[EMAIL] " + etudiants_notifies.size() + " email(s) envoyé(s) pour le nouveau média '" + 
                             nouveau_media.get_titre() + "'");
        }
    }
    
    /**
     * Simule l'envoi d'un email à un étudiant.
     * 
     * @param etudiant L'étudiant destinataire
     * @param media Le média concerné
     */
    private void envoyer_email_simule(Etudiant etudiant, Media media) {
        System.out.println("  → Email envoyé à " + etudiant.get_username() + 
                         " : Nouveau média disponible - " + media.get_titre() + 
                         " (" + media.get_type_media() + ")");
    }
}
