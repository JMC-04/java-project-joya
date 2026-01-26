package fr.univ.bibliotheque.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public abstract class Media implements Serializable {
    
    private static final long serialVersionUID = 2L;
    
    private final String id;
    private String titre;
    private String auteur;
    private int annee;
    private String description;
    private int compteur_acces;
    
    protected Media(String titre, String auteur, int annee, String description) {
        this.id = UUID.randomUUID().toString();
        this.titre = Objects.requireNonNull(titre, "Le titre ne peut pas être null");
        this.auteur = Objects.requireNonNull(auteur, "L'auteur ne peut pas être null");
        this.annee = valider_annee(annee);
        this.description = Objects.requireNonNull(description, "La description ne peut pas être null");
        this.compteur_acces = 0;
    }
    
    protected Media(String id, String titre, String auteur, int annee, String description) {
        this.id = Objects.requireNonNull(id, "L'ID ne peut pas être null");
        this.titre = Objects.requireNonNull(titre, "Le titre ne peut pas être null");
        this.auteur = Objects.requireNonNull(auteur, "L'auteur ne peut pas être null");
        this.annee = valider_annee(annee);
        this.description = Objects.requireNonNull(description, "La description ne peut pas être null");
        this.compteur_acces = 0;
    }
    
    private int valider_annee(int annee) {
        if (annee < 0) {
            throw new IllegalArgumentException("L'année ne peut pas être négative : " + annee);
        }
        return annee;
    }
    
    public abstract String ouvrir();
    
    public void enregistrer_acces() {
        compteur_acces++;
    }
    
    public abstract String get_type_media();
    
    public String get_id() {
        return id;
    }
    
    public String get_titre() {
        return titre;
    }
    
    public String get_auteur() {
        return auteur;
    }
    
    public int get_annee() {
        return annee;
    }
    
    public String get_description() {
        return description;
    }
    
    public int get_compteur_acces() {
        return compteur_acces;
    }
    
    public void reinitialiser_compteur_acces() {
        this.compteur_acces = 0;
    }
    
    public void set_titre(String titre) {
        this.titre = Objects.requireNonNull(titre, "Le titre ne peut pas être null");
    }
    
    public void set_auteur(String auteur) {
        this.auteur = Objects.requireNonNull(auteur, "L'auteur ne peut pas être null");
    }
    
    public void set_annee(int annee) {
        this.annee = valider_annee(annee);
    }
    
    public void set_description(String description) {
        this.description = Objects.requireNonNull(description, "La description ne peut pas être null");
    }
    
    public String get_informations() {
        StringBuilder sb = new StringBuilder();
        sb.append("Type: ").append(get_type_media()).append("\n");
        sb.append("ID: ").append(id).append("\n");
        sb.append("Titre: ").append(titre).append("\n");
        sb.append("Auteur: ").append(auteur).append("\n");
        sb.append("Année: ").append(annee).append("\n");
        sb.append("Description: ").append(description);
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Media media = (Media) o;
        return Objects.equals(id, media.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("%s{id='%s', titre='%s', auteur='%s', annee=%d}",
                get_type_media(), id, titre, auteur, annee);
    }
}
