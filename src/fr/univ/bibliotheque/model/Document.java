package fr.univ.bibliotheque.model;

public class Document extends Media {
    
    private static final long serialVersionUID = 1L;
    private int nombre_de_pages;
    
    public Document(String titre, String auteur, int annee, String description, int nombre_de_pages) {
        super(titre, auteur, annee, description);
        this.nombre_de_pages = valider_nombre_pages(nombre_de_pages);
    }
    
    public Document(String id, String titre, String auteur, int annee, String description, int nombre_de_pages) {
        super(id, titre, auteur, annee, description);
        this.nombre_de_pages = valider_nombre_pages(nombre_de_pages);
    }
    
    private int valider_nombre_pages(int nombre_de_pages) {
        if (nombre_de_pages <= 0) {
            throw new IllegalArgumentException("Le nombre de pages doit être positif : " + nombre_de_pages);
        }
        return nombre_de_pages;
    }
    
    public int get_nombre_de_pages() {
        return nombre_de_pages;
    }
    
    public void set_nombre_de_pages(int nombre_de_pages) {
        this.nombre_de_pages = valider_nombre_pages(nombre_de_pages);
    }
    
    @Override
    public String ouvrir() {
        enregistrer_acces();
        return get_description();
    }
    
    @Override
    public String get_type_media() {
        return "Document";
    }
    
    @Override
    public String get_informations() {
        return super.get_informations() + "\nNombre de pages: " + nombre_de_pages;
    }
    
    @Override
    public String toString() {
        return String.format("Document{id='%s', titre='%s', auteur='%s', annee=%d, nombre_de_pages=%d}",
                get_id(), get_titre(), get_auteur(), get_annee(), nombre_de_pages);
    }
}
