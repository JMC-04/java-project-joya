package fr.univ.bibliotheque.strategy.report;

import fr.univ.bibliotheque.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Rapport statistique des ressources les plus affichées par matière.
 * 
 * @author Bibliothèque Universitaire
 * @version 2.0
 */
public class ParMatiereReport implements StatisticsReport {
    
    private final Matiere matiere;
    private final Integer limite;
    
    public ParMatiereReport(Matiere matiere, Integer limite) {
        this.matiere = matiere;
        this.limite = limite;
    }
    
    public ParMatiereReport(Matiere matiere) {
        this(matiere, null);
    }
    
    @Override
    public List<Media> generer() {
        List<Media> medias_matiere = new ArrayList<>(matiere.get_medias());
        
        List<Media> resultats = medias_matiere.stream()
                .sorted((m1, m2) -> Integer.compare(m2.get_compteur_acces(), m1.get_compteur_acces()))
                .collect(Collectors.toList());
        
        if (limite != null && limite > 0 && resultats.size() > limite) {
            return resultats.subList(0, limite);
        }
        
        return resultats;
    }
    
    @Override
    public String get_titre() {
        String titre = "Rapport : Ressources les plus affichées - Matière " + matiere.get_code();
        if (limite != null && limite > 0) {
            titre += " (Top " + limite + ")";
        }
        return titre;
    }
    
    @Override
    public String get_description() {
        return "Liste des médias associés à la matière '" + matiere.get_code() + 
               " (" + matiere.get_intitule() + ")' triés par nombre d'accès décroissant";
    }
}
