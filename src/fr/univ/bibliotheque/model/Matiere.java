package fr.univ.bibliotheque.model;

import java.util.*;

public class Matiere {
    private String code;
    private String intitule;
    private Specialite specialite;
    private Set<Media> medias;

    public Matiere(String code, String intitule, Specialite specialite) {
        this.code = Objects.requireNonNull(code, "Le code ne peut pas être null");
        this.intitule = intitule != null ? intitule : "";
        this.specialite = Objects.requireNonNull(specialite, "La spécialité ne peut pas être null");
        this.medias = new HashSet<>();
    }

    public void ajouter_media(Media media) {
        if (media != null) {
            medias.add(media);
        }
    }

    public void retirer_media(Media media) {
        if (media != null) {
            medias.remove(media);
        }
    }

    public List<Media> get_medias_par_acces() {
        List<Media> liste_medias = new ArrayList<>(medias);
        liste_medias.sort((m1, m2) -> Integer.compare(m2.get_compteur_acces(), m1.get_compteur_acces()));
        return liste_medias;
    }

    public String get_code() {
        return code;
    }

    public String get_intitule() {
        return intitule;
    }

    public Specialite get_specialite() {
        return specialite;
    }

    public Set<Media> get_medias() {
        return Collections.unmodifiableSet(medias);
    }

    public int get_nombre_medias() {
        return medias.size();
    }

    public void set_code(String code) {
        this.code = Objects.requireNonNull(code, "Le code ne peut pas être null");
    }

    public void set_intitule(String intitule) {
        this.intitule = intitule != null ? intitule : "";
    }

    public void set_specialite(Specialite specialite) {
        this.specialite = Objects.requireNonNull(specialite, "La spécialité ne peut pas être null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matiere matiere = (Matiere) o;
        return Objects.equals(code, matiere.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Matiere{code='").append(code).append("'");
        sb.append(", intitule='").append(intitule).append("'");
        if (specialite != null) {
            sb.append(", specialite='").append(specialite.get_nom()).append("'");
        }
        sb.append(", nombre_medias=").append(medias.size());
        sb.append("}");
        return sb.toString();
    }
}
