package fr.univ.bibliotheque.strategy.report;

import fr.univ.bibliotheque.service.Catalog;
import fr.univ.bibliotheque.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Rapport statistique des ressources les plus affichées par spécialité.
 * 
 * @author Bibliothèque Universitaire
 * @version 2.0
 */
public class ParSpecialiteReport implements StatisticsReport {
    
    private final Catalog catalog;
    private final Specialite specialite;
    private final Integer limite;
    
    public ParSpecialiteReport(Catalog catalog, Specialite specialite, Integer limite) {
        this.catalog = catalog;
        this.specialite = specialite;
        this.limite = limite;
    }
    
    public ParSpecialiteReport(Catalog catalog, Specialite specialite) {
        this(catalog, specialite, null);
    }
    
    @Override
    public List<Media> generer() {
        List<Media> medias_specialite = new ArrayList<>();
        
        for (Matiere matiere : specialite.get_matieres()) {
            for (Media media : matiere.get_medias()) {
                if (!medias_specialite.contains(media)) {
                    medias_specialite.add(media);
                }
            }
        }
        
        List<Media> resultats = medias_specialite.stream()
                .sorted((m1, m2) -> Integer.compare(m2.get_compteur_acces(), m1.get_compteur_acces()))
                .collect(Collectors.toList());
        
        if (limite != null && limite > 0 && resultats.size() > limite) {
            return resultats.subList(0, limite);
        }
        
        return resultats;
    }
    
    @Override
    public String get_titre() {
        String titre = "Rapport : Ressources les plus affichées - Spécialité " + specialite.get_nom();
        if (limite != null && limite > 0) {
            titre += " (Top " + limite + ")";
        }
        return titre;
    }
    
    @Override
    public String get_description() {
        return "Liste des médias associés à la spécialité '" + specialite.get_nom() + 
               "' triés par nombre d'accès décroissant";
    }
}
