package fr.univ.bibliotheque.observer;

import fr.univ.bibliotheque.model.Media;

/**
 * Observateur qui rafraîchit l'interface utilisateur lors de l'ajout d'un nouveau média.
 * 
 * @author Bibliothèque Universitaire
 * @version 2.0
 */
public class UIRefresher implements MediaObserver {
    
    @Override
    public void notify(Media nouveau_media) {
        System.out.println("[UI] Nouveau média ajouté : " + nouveau_media.get_titre());
        System.out.println("     L'interface doit être rafraîchie si elle est visible.");
    }
}
