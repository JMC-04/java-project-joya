package fr.univ.bibliotheque.strategy.filter;

import fr.univ.bibliotheque.model.Media;

/**
 * Filtre les médias par année.
 * 
 * @author Bibliothèque Universitaire
 * @version 2.0
 */
public class AnneeFilter implements MediaFilter {
    
    private final int annee_recherchee;
    
    public AnneeFilter(int annee) {
        if (annee < 0) {
            throw new IllegalArgumentException("L'année ne peut pas être négative");
        }
        this.annee_recherchee = annee;
    }
    
    public AnneeFilter(String annee_str) {
        try {
            this.annee_recherchee = Integer.parseInt(annee_str.trim());
            if (annee_recherchee < 0) {
                throw new IllegalArgumentException("L'année ne peut pas être négative");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Format d'année invalide : " + annee_str, e);
        }
    }
    
    @Override
    public boolean matches(Media media) {
        if (media == null) {
            return false;
        }
        return media.get_annee() == annee_recherchee;
    }
}
