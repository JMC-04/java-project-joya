package fr.univ.bibliotheque.strategy.report;

import fr.univ.bibliotheque.model.Media;

import java.util.List;

/**
 * Interface pour la génération de rapports statistiques.
 * 
 * @author Bibliothèque Universitaire
 * @version 2.0
 */
public interface StatisticsReport {
    
    /**
     * Génère le rapport statistique.
     * 
     * @return Une liste de médias triés selon les critères du rapport
     */
    List<Media> generer();
    
    /**
     * Retourne le titre du rapport.
     * 
     * @return Le titre du rapport
     */
    String get_titre();
    
    /**
     * Retourne une description textuelle du rapport.
     * 
     * @return La description du rapport
     */
    String get_description();
}
