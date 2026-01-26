package fr.univ.bibliotheque.observer;

import fr.univ.bibliotheque.model.Media;

/**
 * Interface pour les observateurs qui souhaitent être notifiés
 * lors de l'ajout de nouveaux médias au catalogue.
 * 
 * @author Bibliothèque Universitaire
 * @version 2.0
 */
public interface MediaObserver {
    
    /**
     * Méthode appelée lorsqu'un nouveau média est ajouté au catalogue.
     * 
     * @param nouveau_media Le nouveau média ajouté
     */
    void notify(Media nouveau_media);
}
