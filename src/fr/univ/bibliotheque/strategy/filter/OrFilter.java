package fr.univ.bibliotheque.strategy.filter;

import fr.univ.bibliotheque.model.Media;

/**
 * Filtre composé qui combine deux filtres avec un OU logique.
 * 
 * @author Bibliothèque Universitaire
 * @version 2.0
 */
public class OrFilter implements MediaFilter {
    
    private final MediaFilter filtre1;
    private final MediaFilter filtre2;
    
    public OrFilter(MediaFilter filtre1, MediaFilter filtre2) {
        if (filtre1 == null || filtre2 == null) {
            throw new IllegalArgumentException("Les filtres ne peuvent pas être null");
        }
        this.filtre1 = filtre1;
        this.filtre2 = filtre2;
    }
    
    @Override
    public boolean matches(Media media) {
        return filtre1.matches(media) || filtre2.matches(media);
    }
}
