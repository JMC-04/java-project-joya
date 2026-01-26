package fr.univ.bibliotheque.service;

import fr.univ.bibliotheque.model.*;
import fr.univ.bibliotheque.strategy.filter.MediaFilter;
import fr.univ.bibliotheque.observer.MediaObserver;

import java.util.*;

/**
 * Gestionnaire centralisé pour tous les médias de la bibliothèque.
 * 
 * @author Bibliothèque Universitaire
 * @version 2.0
 */
public class Catalog {
    
    private Map<String, Media> medias;
    private Map<String, Set<Matiere>> media_matieres;
    private List<MediaObserver> observateurs;
    
    public Catalog() {
        this.medias = new HashMap<>();
        this.media_matieres = new HashMap<>();
        this.observateurs = new ArrayList<>();
    }
    
    /**
     * Ajoute un nouveau média au catalogue.
     * 
     * @param media Le média à ajouter
     * @param matieres Les matières associées
     * @return true si le média a été ajouté
     * @throws IllegalArgumentException si le média est null
     */
    public boolean ajouter_media(Media media, Collection<Matiere> matieres) {
        if (media == null) {
            throw new IllegalArgumentException("Le média ne peut pas être null");
        }
        
        String id = media.get_id();
        
        if (medias.containsKey(id)) {
            return false;
        }
        
        medias.put(id, media);
        
        Set<Matiere> matieres_set = new HashSet<>();
        if (matieres != null) {
            for (Matiere matiere : matieres) {
                if (matiere != null) {
                    matiere.ajouter_media(media);
                    matieres_set.add(matiere);
                }
            }
        }
        media_matieres.put(id, matieres_set);
        
        notifier_observateurs(media);
        
        return true;
    }
    
    public boolean ajouter_media(Media media) {
        return ajouter_media(media, null);
    }
    
    /**
     * Supprime un média du catalogue par son ID.
     * 
     * @param id L'identifiant du média à supprimer
     * @return Le média supprimé, ou null
     */
    public Media supprimer_media(String id) {
        if (id == null) {
            return null;
        }
        
        Media media = medias.remove(id);
        
        if (media != null) {
            Set<Matiere> matieres = media_matieres.remove(id);
            if (matieres != null) {
                for (Matiere matiere : matieres) {
                    matiere.retirer_media(media);
                }
            }
        }
        
        return media;
    }
    
    public boolean supprimer_media(Media media) {
        if (media == null) {
            return false;
        }
        return supprimer_media(media.get_id()) != null;
    }
    
    /**
     * Modifie les propriétés de base d'un média.
     * 
     * @param id L'identifiant du média à modifier
     * @param nouveau_titre Le nouveau titre
     * @param nouvel_auteur Le nouvel auteur
     * @param nouvelle_annee La nouvelle année
     * @param nouvelle_description La nouvelle description
     * @return true si le média a été modifié
     */
    public boolean modifier_media(String id, String nouveau_titre, String nouvel_auteur, 
                                 int nouvelle_annee, String nouvelle_description) {
        Media media = trouver_media_par_id(id);
        
        if (media == null) {
            return false;
        }
        
        if (nouveau_titre != null) {
            media.set_titre(nouveau_titre);
        }
        if (nouvel_auteur != null) {
            media.set_auteur(nouvel_auteur);
        }
        if (nouvelle_annee >= 0) {
            media.set_annee(nouvelle_annee);
        }
        if (nouvelle_description != null) {
            media.set_description(nouvelle_description);
        }
        
        return true;
    }
    
    /**
     * Modifie les matières associées à un média.
     * 
     * @param id L'identifiant du média
     * @param nouvelles_matieres Les nouvelles matières
     * @return true si les matières ont été modifiées
     */
    public boolean modifier_matieres_media(String id, Collection<Matiere> nouvelles_matieres) {
        Media media = trouver_media_par_id(id);
        
        if (media == null) {
            return false;
        }
        
        Set<Matiere> anciennes_matieres = media_matieres.get(id);
        if (anciennes_matieres != null) {
            for (Matiere matiere : anciennes_matieres) {
                matiere.retirer_media(media);
            }
        }
        
        Set<Matiere> matieres_set = new HashSet<>();
        if (nouvelles_matieres != null) {
            for (Matiere matiere : nouvelles_matieres) {
                if (matiere != null) {
                    matiere.ajouter_media(media);
                    matieres_set.add(matiere);
                }
            }
        }
        media_matieres.put(id, matieres_set);
        
        return true;
    }
    
    /**
     * Recherche un média par son identifiant unique.
     * 
     * @param id L'identifiant unique du média
     * @return Le média trouvé, ou null
     */
    public Media trouver_media_par_id(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return medias.get(id.trim());
    }
    
    /**
     * Recherche des médias par titre.
     * 
     * @param titre Le titre recherché
     * @return Une liste des médias correspondants
     */
    public List<Media> rechercher_par_titre(String titre) {
        if (titre == null || titre.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        String titre_lower = titre.toLowerCase().trim();
        List<Media> resultats = new ArrayList<>();
        
        for (Media media : medias.values()) {
            if (media.get_titre().toLowerCase().contains(titre_lower)) {
                resultats.add(media);
            }
        }
        
        return Collections.unmodifiableList(resultats);
    }
    
    /**
     * Recherche des médias par auteur.
     * 
     * @param auteur L'auteur recherché
     * @return Une liste des médias correspondants
     */
    public List<Media> rechercher_par_auteur(String auteur) {
        if (auteur == null || auteur.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        String auteur_lower = auteur.toLowerCase().trim();
        List<Media> resultats = new ArrayList<>();
        
        for (Media media : medias.values()) {
            if (media.get_auteur().toLowerCase().contains(auteur_lower)) {
                resultats.add(media);
            }
        }
        
        return Collections.unmodifiableList(resultats);
    }
    
    /**
     * Recherche générique de médias en utilisant un filtre.
     * 
     * @param filtre Le filtre à appliquer
     * @return Une liste des médias satisfaisant le critère
     * @throws IllegalArgumentException si le filtre est null
     */
    public List<Media> rechercher(MediaFilter filtre) {
        if (filtre == null) {
            throw new IllegalArgumentException("Le filtre ne peut pas être null");
        }
        
        List<Media> resultats = new ArrayList<>();
        
        for (Media media : medias.values()) {
            if (filtre.matches(media)) {
                resultats.add(media);
            }
        }
        
        return Collections.unmodifiableList(resultats);
    }
    
    public Collection<Media> get_all_medias() {
        return Collections.unmodifiableCollection(medias.values());
    }
    
    public Set<Matiere> get_matieres_media(String id) {
        Set<Matiere> matieres = media_matieres.get(id);
        return matieres != null ? Collections.unmodifiableSet(matieres) : Collections.emptySet();
    }
    
    public int get_nombre_medias() {
        return medias.size();
    }
    
    public boolean contient_media(String id) {
        return id != null && medias.containsKey(id);
    }
    
    /**
     * Ajoute un observateur au catalogue.
     * 
     * @param observateur L'observateur à ajouter
     * @return true si l'observateur a été ajouté
     */
    public boolean ajouter_observateur(MediaObserver observateur) {
        if (observateur == null) {
            throw new IllegalArgumentException("L'observateur ne peut pas être null");
        }
        if (!observateurs.contains(observateur)) {
            observateurs.add(observateur);
            return true;
        }
        return false;
    }
    
    public boolean retirer_observateur(MediaObserver observateur) {
        return observateurs.remove(observateur);
    }
    
    public int get_nombre_observateurs() {
        return observateurs.size();
    }
    
    /**
     * Notifie tous les observateurs de l'ajout d'un nouveau média.
     * 
     * @param nouveau_media Le nouveau média ajouté
     */
    private void notifier_observateurs(Media nouveau_media) {
        for (MediaObserver observateur : observateurs) {
            try {
                observateur.notify(nouveau_media);
            } catch (Exception e) {
                System.err.println("ERREUR lors de la notification de l'observateur : " + e.getMessage());
            }
        }
    }
    
    /**
     * Vide le catalogue.
     */
    public void vider() {
        for (Media media : medias.values()) {
            Set<Matiere> matieres = media_matieres.get(media.get_id());
            if (matieres != null) {
                for (Matiere matiere : matieres) {
                    matiere.retirer_media(media);
                }
            }
        }
        
        medias.clear();
        media_matieres.clear();
    }
}
