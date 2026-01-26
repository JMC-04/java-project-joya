package fr.univ.bibliotheque.strategy.filter;

import fr.univ.bibliotheque.model.Media;

/**
 * Interface pour les filtres de médias.
 * 
 * @author Bibliothèque Universitaire
 * @version 2.0
 */
public interface MediaFilter {
    
    /**
     * Vérifie si un média satisfait le critère du filtre.
     * 
     * @param media Le média à vérifier
     * @return true si le média satisfait le critère
     */
    boolean matches(Media media);
}
