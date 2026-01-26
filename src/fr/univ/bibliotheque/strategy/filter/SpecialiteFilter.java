package fr.univ.bibliotheque.strategy.filter;

import fr.univ.bibliotheque.model.Media;
import fr.univ.bibliotheque.model.Matiere;
import fr.univ.bibliotheque.model.Specialite;
import fr.univ.bibliotheque.service.Catalog;

/**
 * Filtre les médias par spécialité.
 * 
 * @author Bibliothèque Universitaire
 * @version 2.0
 */
public class SpecialiteFilter implements MediaFilter {
    
    private final Specialite specialite_cible;
    private final Catalog catalog;
    
    public SpecialiteFilter(Specialite specialite, Catalog catalog) {
        if (specialite == null) {
            throw new IllegalArgumentException("La spécialité ne peut pas être null");
        }
        if (catalog == null) {
            throw new IllegalArgumentException("Le catalogue ne peut pas être null");
        }
        this.specialite_cible = specialite;
        this.catalog = catalog;
    }
    
    @Override
    public boolean matches(Media media) {
        if (media == null) {
            return false;
        }
        
        for (Matiere matiere : catalog.get_matieres_media(media.get_id())) {
            if (matiere.get_specialite().equals(specialite_cible)) {
                return true;
            }
        }
        return false;
    }
}
