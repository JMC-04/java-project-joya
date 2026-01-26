package fr.univ.bibliotheque.model;

import java.util.Objects;

public class QuizOnline extends Media {
    
    private static final long serialVersionUID = 1L;
    private int duree_estimee;
    private String niveau_difficulte;
    
    public static final String FACILE = "Facile";
    public static final String MOYEN = "Moyen";
    public static final String DIFFICILE = "Difficile";
    
    public QuizOnline(String titre, String auteur, int annee, String description, 
                      int duree_estimee, String niveau_difficulte) {
        super(titre, auteur, annee, description);
        this.duree_estimee = valider_duree_estimee(duree_estimee);
        this.niveau_difficulte = Objects.requireNonNull(niveau_difficulte, 
                "Le niveau de difficulté ne peut pas être null");
    }
    
    public QuizOnline(String id, String titre, String auteur, int annee, String description,
                      int duree_estimee, String niveau_difficulte) {
        super(id, titre, auteur, annee, description);
        this.duree_estimee = valider_duree_estimee(duree_estimee);
        this.niveau_difficulte = Objects.requireNonNull(niveau_difficulte,
                "Le niveau de difficulté ne peut pas être null");
    }
    
    private int valider_duree_estimee(int duree_estimee) {
        if (duree_estimee <= 0) {
            throw new IllegalArgumentException("La durée estimée doit être positive : " + duree_estimee + " minutes");
        }
        return duree_estimee;
    }
    
    public int get_duree_estimee() {
        return duree_estimee;
    }
    
    public String get_niveau_difficulte() {
        return niveau_difficulte;
    }
    
    public void set_duree_estimee(int duree_estimee) {
        this.duree_estimee = valider_duree_estimee(duree_estimee);
    }
    
    public void set_niveau_difficulte(String niveau_difficulte) {
        this.niveau_difficulte = Objects.requireNonNull(niveau_difficulte,
                "Le niveau de difficulté ne peut pas être null");
    }
    
    @Override
    public String ouvrir() {
        enregistrer_acces();
        return get_description();
    }
    
    @Override
    public String get_type_media() {
        return "QuizOnline";
    }
    
    @Override
    public String get_informations() {
        return super.get_informations() + 
               "\nDurée estimée: " + duree_estimee + " minutes" +
               "\nNiveau de difficulté: " + niveau_difficulte;
    }
    
    @Override
    public String toString() {
        return String.format("QuizOnline{id='%s', titre='%s', auteur='%s', annee=%d, duree_estimee=%d min, niveau_difficulte='%s'}",
                get_id(), get_titre(), get_auteur(), get_annee(), duree_estimee, niveau_difficulte);
    }
}
