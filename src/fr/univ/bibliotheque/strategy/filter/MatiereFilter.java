package fr.univ.bibliotheque.strategy.filter;

import fr.univ.bibliotheque.model.Media;
import fr.univ.bibliotheque.model.Matiere;
import fr.univ.bibliotheque.service.Catalog;

/**
 * Filtre les médias par matière.
 * 
 * @author Bibliothèque Universitaire
 * @version 2.0
 */
public class MatiereFilter implements MediaFilter {
    
    private final Matiere matiere_cible;
    private final Catalog catalog;
    
    public MatiereFilter(Matiere matiere, Catalog catalog) {
        if (matiere == null) {
            throw new IllegalArgumentException("La matière ne peut pas être null");
        }
        if (catalog == null) {
            throw new IllegalArgumentException("Le catalogue ne peut pas être null");
        }
        this.matiere_cible = matiere;
        this.catalog = catalog;
    }
    
    @Override
    public boolean matches(Media media) {
        if (media == null) {
            return false;
        }
        return catalog.get_matieres_media(media.get_id()).contains(matiere_cible);
    }
}
