package fr.univ.bibliotheque.repository;

import fr.univ.bibliotheque.service.Catalog;
import fr.univ.bibliotheque.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository pour la persistance des médias via sérialisation binaire.
 * 
 * @author Bibliothèque Universitaire
 * @version 2.0
 */
public class MediaRepository {
    
    private static final String DEFAULT_FILE_PATH = "data/medias.ser";
    
    /**
     * Classe interne pour stocker les données à sérialiser.
     */
    private static class MediaData implements Serializable {
        private static final long serialVersionUID = 1L;
        
        public List<Media> medias;
        public Map<String, List<String>> media_matieres;
        
        public MediaData() {
            this.medias = new ArrayList<>();
            this.media_matieres = new HashMap<>();
        }
    }
    
    /**
     * Sauvegarde les médias du catalogue dans un fichier binaire.
     * 
     * @param catalog Le catalogue contenant les médias à sauvegarder
     * @param file_path Le chemin du fichier
     * @return true si la sauvegarde a réussi
     */
    public boolean sauvegarder_donnees(Catalog catalog, String file_path) {
        String chemin_fichier = file_path != null ? file_path : DEFAULT_FILE_PATH;
        
        try {
            File fichier = new File(chemin_fichier);
            File parent_dir = fichier.getParentFile();
            if (parent_dir != null && !parent_dir.exists()) {
                parent_dir.mkdirs();
            }
            
            MediaData data = new MediaData();
            data.medias = new ArrayList<>(catalog.get_all_medias());
            
            for (Media media : data.medias) {
                var matieres = catalog.get_matieres_media(media.get_id());
                List<String> codes_matieres = new ArrayList<>();
                for (Matiere matiere : matieres) {
                    codes_matieres.add(matiere.get_code());
                }
                data.media_matieres.put(media.get_id(), codes_matieres);
            }
            
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fichier))) {
                oos.writeObject(data);
            }
            
            System.out.println("Sauvegarde réussie : " + data.medias.size() + 
                             " média(s) sauvegardé(s) dans '" + chemin_fichier + "'");
            return true;
            
        } catch (IOException e) {
            System.err.println("ERREUR lors de la sauvegarde dans '" + chemin_fichier + "' :");
            System.err.println("  " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("  Cause : " + e.getCause().getMessage());
            }
            return false;
        }
    }
    
    /**
     * Charge les médias depuis un fichier binaire et les ajoute au catalogue.
     * 
     * @param catalog Le catalogue dans lequel charger les médias
     * @param file_path Le chemin du fichier
     * @param matieres_disponibles Map des matières disponibles
     * @return true si le chargement a réussi
     */
    public boolean charger_donnees(Catalog catalog, String file_path, 
                                  Map<String, Matiere> matieres_disponibles) {
        String chemin_fichier = file_path != null ? file_path : DEFAULT_FILE_PATH;
        
        try {
            File fichier = new File(chemin_fichier);
            
            if (!fichier.exists()) {
                System.out.println("Aucun fichier de sauvegarde trouvé : '" + chemin_fichier + "'");
                System.out.println("Initialisation avec un catalogue vide.");
                return true;
            }
            
            if (!fichier.canRead()) {
                System.err.println("ERREUR : Impossible de lire le fichier '" + chemin_fichier + "'.");
                return false;
            }
            
            MediaData data;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichier))) {
                data = (MediaData) ois.readObject();
            }
            
            // Vérifier que les données sont valides
            if (data.medias == null) {
                data.medias = new ArrayList<>();
            }
            if (data.media_matieres == null) {
                data.media_matieres = new HashMap<>();
            }
            
            int medias_charges = 0;
            int relations_restaurees = 0;
            
            for (Media media : data.medias) {
                List<String> codes_matieres = data.media_matieres.get(media.get_id());
                List<Matiere> matieres_media = new ArrayList<>();
                
                if (codes_matieres != null && matieres_disponibles != null) {
                    for (String code : codes_matieres) {
                        Matiere matiere = matieres_disponibles.get(code);
                        if (matiere != null) {
                            matieres_media.add(matiere);
                            relations_restaurees++;
                        } else {
                            System.err.println("ATTENTION : Matière '" + code + 
                                             "' non trouvée pour le média '" + media.get_titre() + "'");
                        }
                    }
                }
                
                catalog.ajouter_media(media, matieres_media);
                medias_charges++;
            }
            
            System.out.println("Chargement réussi : " + medias_charges + 
                             " média(s) chargé(s), " + relations_restaurees + 
                             " relation(s) restaurée(s) depuis '" + chemin_fichier + "'");
            return true;
            
        } catch (FileNotFoundException e) {
            System.out.println("Aucun fichier de sauvegarde trouvé : '" + chemin_fichier + "'");
            System.out.println("Initialisation avec un catalogue vide.");
            return true;
            
        } catch (ClassNotFoundException e) {
            System.err.println("ERREUR : Version de classe incompatible lors du chargement de '" + chemin_fichier + "' :");
            System.err.println("  " + e.getMessage());
            System.err.println("  Le fichier a peut-être été créé avec une version différente de l'application.");
            return false;
            
        } catch (InvalidClassException e) {
            System.err.println("ERREUR : Classe invalide lors du chargement de '" + chemin_fichier + "' :");
            System.err.println("  " + e.getMessage());
            System.err.println("  Les classes Media ont peut-être été modifiées depuis la dernière sauvegarde.");
            return false;
            
        } catch (IOException e) {
            System.err.println("ERREUR lors du chargement de '" + chemin_fichier + "' :");
            System.err.println("  " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("  Cause : " + e.getCause().getMessage());
            }
            return false;
        }
    }
    
    public boolean charger_donnees(Catalog catalog, Map<String, Matiere> matieres_disponibles) {
        return charger_donnees(catalog, null, matieres_disponibles);
    }
}
