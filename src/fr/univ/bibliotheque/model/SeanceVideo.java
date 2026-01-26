package fr.univ.bibliotheque.model;

public class SeanceVideo extends Media {
    
    private static final long serialVersionUID = 1L;
    private int duree;
    
    public SeanceVideo(String titre, String auteur, int annee, String description, int duree) {
        super(titre, auteur, annee, description);
        this.duree = valider_duree(duree);
    }
    
    public SeanceVideo(String id, String titre, String auteur, int annee, String description, int duree) {
        super(id, titre, auteur, annee, description);
        this.duree = valider_duree(duree);
    }
    
    private int valider_duree(int duree) {
        if (duree <= 0) {
            throw new IllegalArgumentException("La durée doit être positive : " + duree + " minutes");
        }
        return duree;
    }
    
    public int get_duree() {
        return duree;
    }
    
    public String get_duree_formatee() {
        int heures = duree / 60;
        int minutes = duree % 60;
        
        if (heures > 0 && minutes > 0) {
            return heures + "h " + minutes + "min";
        } else if (heures > 0) {
            return heures + "h";
        } else {
            return minutes + "min";
        }
    }
    
    public void set_duree(int duree) {
        this.duree = valider_duree(duree);
    }
    
    @Override
    public String ouvrir() {
        enregistrer_acces();
        return get_description();
    }
    
    @Override
    public String get_type_media() {
        return "SeanceVideo";
    }
    
    @Override
    public String get_informations() {
        return super.get_informations() + "\nDurée: " + get_duree_formatee() + " (" + duree + " minutes)";
    }
    
    @Override
    public String toString() {
        return String.format("SeanceVideo{id='%s', titre='%s', auteur='%s', annee=%d, duree=%d min}",
                get_id(), get_titre(), get_auteur(), get_annee(), duree);
    }
}
