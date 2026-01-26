package fr.univ.bibliotheque.strategy.filter;

import fr.univ.bibliotheque.model.Media;

/**
 * Filtre les médias par type.
 * 
 * @author Bibliothèque Universitaire
 * @version 2.0
 */
public class TypeFilter implements MediaFilter {
    
    private final String type_recherche;
    
    public TypeFilter(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Le type ne peut pas être null ou vide");
        }
        this.type_recherche = type.toLowerCase().trim();
    }
    
    @Override
    public boolean matches(Media media) {
        if (media == null) {
            return false;
        }
        return media.get_type_media().toLowerCase().contains(type_recherche);
    }
}
