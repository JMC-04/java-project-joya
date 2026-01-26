package fr.univ.bibliotheque.strategy.export;

import fr.univ.bibliotheque.service.Catalog;
import fr.univ.bibliotheque.model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

/**
 * Exporteur CSV pour les médias.
 * 
 * @author Bibliothèque Universitaire
 * @version 2.0
 */
public class CSVExporter implements MediaExporter {
    
    private final Catalog catalog;
    private static final String SEPARATEUR = ",";
    private static final String SEPARATEUR_MATIERES = ";";
    
    public CSVExporter(Catalog catalog) {
        this.catalog = Objects.requireNonNull(catalog, "Le catalogue ne peut pas être null");
    }
    
    @Override
    public boolean exporter(Collection<Media> medias, String chemin_fichier) {
        if (medias == null) {
            throw new IllegalArgumentException("La collection de médias ne peut pas être null");
        }
        if (chemin_fichier == null || chemin_fichier.trim().isEmpty()) {
            throw new IllegalArgumentException("Le chemin du fichier ne peut pas être null ou vide");
        }
        
        try {
            File fichier = new File(chemin_fichier);
            File parent_dir = fichier.getParentFile();
            if (parent_dir != null && !parent_dir.exists()) {
                parent_dir.mkdirs();
            }
            
            try (FileWriter writer = new FileWriter(fichier, false)) {
                writer.write(creer_en_tete());
                writer.write("\n");
                
                int total_acces = 0;
                for (Media media : medias) {
                    writer.write(creer_ligne_media(media));
                    writer.write("\n");
                    total_acces += media.get_compteur_acces();
                }
                
                writer.write(creer_ligne_total(total_acces));
                writer.write("\n");
            }
            
            System.out.println("Export CSV réussi : " + medias.size() + 
                             " média(s) exporté(s) dans '" + chemin_fichier + "'");
            return true;
            
        } catch (IOException e) {
            System.err.println("ERREUR lors de l'export CSV vers '" + chemin_fichier + "' :");
            System.err.println("  " + e.getMessage());
            return false;
        }
    }
    
    private String creer_en_tete() {
        String[] en_tetes = {
            "ID", "Type", "Titre", "Auteur", "Annee", "Description", "Compteur Acces",
            "Nombre de Pages", "Duree (minutes)", "Duree Estimee (minutes)", "Niveau Difficulte",
            "Matieres (Code)", "Matieres (Intitule)", "Matieres (Specialite)"
        };
        
        StringBuilder ligne_en_tete = new StringBuilder();
        for (int i = 0; i < en_tetes.length; i++) {
            if (i > 0) {
                ligne_en_tete.append(SEPARATEUR);
            }
            ligne_en_tete.append(en_tetes[i]);
        }
        
        return ligne_en_tete.toString();
    }
    
    private String creer_ligne_media(Media media) {
        StringBuilder ligne = new StringBuilder();
        
        ligne.append(echapper_csv(media.get_id())).append(SEPARATEUR);
        ligne.append(echapper_csv(media.get_type_media())).append(SEPARATEUR);
        ligne.append(echapper_csv(media.get_titre())).append(SEPARATEUR);
        ligne.append(echapper_csv(media.get_auteur())).append(SEPARATEUR);
        ligne.append(media.get_annee()).append(SEPARATEUR);
        ligne.append(echapper_csv(media.get_description())).append(SEPARATEUR);
        ligne.append(media.get_compteur_acces()).append(SEPARATEUR);
        
        int nombre_de_pages = 0;
        int duree = 0;
        int duree_estimee = 0;
        String niveau_difficulte = "";
        
        if (media instanceof Document) {
            Document doc = (Document) media;
            nombre_de_pages = doc.get_nombre_de_pages();
        } else if (media instanceof SeanceVideo) {
            SeanceVideo video = (SeanceVideo) media;
            duree = video.get_duree();
        } else if (media instanceof QuizOnline) {
            QuizOnline quiz = (QuizOnline) media;
            duree_estimee = quiz.get_duree_estimee();
            niveau_difficulte = quiz.get_niveau_difficulte();
        }
        
        ligne.append(nombre_de_pages > 0 ? nombre_de_pages : "").append(SEPARATEUR);
        ligne.append(duree > 0 ? duree : "").append(SEPARATEUR);
        ligne.append(duree_estimee > 0 ? duree_estimee : "").append(SEPARATEUR);
        ligne.append(echapper_csv(niveau_difficulte)).append(SEPARATEUR);
        
        var matieres = catalog.get_matieres_media(media.get_id());
        StringBuilder codes_matieres = new StringBuilder();
        StringBuilder intitules_matieres = new StringBuilder();
        StringBuilder specialites_matieres = new StringBuilder();
        
        for (Matiere matiere : matieres) {
            if (codes_matieres.length() > 0) {
                codes_matieres.append(SEPARATEUR_MATIERES);
                intitules_matieres.append(SEPARATEUR_MATIERES);
                specialites_matieres.append(SEPARATEUR_MATIERES);
            }
            codes_matieres.append(matiere.get_code());
            intitules_matieres.append(matiere.get_intitule());
            if (matiere.get_specialite() != null) {
                specialites_matieres.append(matiere.get_specialite().get_nom());
            }
        }
        
        ligne.append(echapper_csv(codes_matieres.toString())).append(SEPARATEUR);
        ligne.append(echapper_csv(intitules_matieres.toString())).append(SEPARATEUR);
        ligne.append(echapper_csv(specialites_matieres.toString()));
        
        return ligne.toString();
    }
    
    private String creer_ligne_total(int total_acces) {
        StringBuilder ligne = new StringBuilder();
        ligne.append("Total");
        for (int i = 0; i < 5; i++) {
            ligne.append(SEPARATEUR);
        }
        ligne.append(SEPARATEUR).append(total_acces);
        for (int i = 0; i < 7; i++) {
            ligne.append(SEPARATEUR);
        }
        return ligne.toString();
    }
    
    private String echapper_csv(String valeur) {
        if (valeur == null || valeur.isEmpty()) {
            return "";
        }
        
        if (valeur.contains("\"") || valeur.contains(SEPARATEUR) || valeur.contains("\n") || valeur.contains("\r")) {
            return "\"" + valeur.replace("\"", "\"\"") + "\"";
        }
        
        return valeur;
    }
    
    @Override
    public String get_extension() {
        return "csv";
    }
    
    @Override
    public String get_description() {
        return "CSV";
    }
}
