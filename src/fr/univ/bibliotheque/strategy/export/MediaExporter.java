package fr.univ.bibliotheque.strategy.export;

import fr.univ.bibliotheque.model.Media;

import java.util.Collection;

/**
 * Interface pour l'exportation de médias dans différents formats.
 * 
 * @author Bibliothèque Universitaire
 * @version 2.0
 */
public interface MediaExporter {
    
    /**
     * Exporte une collection de médias dans un fichier.
     * 
     * @param medias La collection de médias à exporter
     * @param chemin_fichier Le chemin du fichier de destination
     * @return true si l'export a réussi
     * @throws IllegalArgumentException si la collection ou le chemin est null
     */
    boolean exporter(Collection<Media> medias, String chemin_fichier);
    
    /**
     * Retourne l'extension de fichier associée à ce format d'export.
     * 
     * @return L'extension
     */
    String get_extension();
    
    /**
     * Retourne la description du format d'export.
     * 
     * @return La description
     */
    String get_description();
}
