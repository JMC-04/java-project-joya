package fr.univ.bibliotheque.strategy.filter;

import fr.univ.bibliotheque.model.Media;

/**
 * Filtre les médias par auteur (recherche partielle, insensible à la casse).
 * 
 * @author Bibliothèque Universitaire
 * @version 2.0
 */
public class AuteurFilter implements MediaFilter {
    
    private final String auteur_recherche;
    
    public AuteurFilter(String auteur) {
        if (auteur == null || auteur.trim().isEmpty()) {
            throw new IllegalArgumentException("L'auteur ne peut pas être null ou vide");
        }
        this.auteur_recherche = auteur.toLowerCase().trim();
    }
    
    @Override
    public boolean matches(Media media) {
        if (media == null || media.get_auteur() == null) {
            return false;
        }
        return media.get_auteur().toLowerCase().contains(auteur_recherche);
    }
}
