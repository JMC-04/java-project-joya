package fr.univ.bibliotheque.model;

import java.util.*;

public class Specialite {
    private String nom;
    private Set<Matiere> matieres;

    public Specialite(String nom) {
        this.nom = Objects.requireNonNull(nom, "Le nom de la spécialité ne peut pas être null");
        this.matieres = new HashSet<>();
    }

    public void ajouter_matiere(Matiere matiere) {
        if (matiere != null) {
            matieres.add(matiere);
            if (matiere.get_specialite() != this) {
                matiere.set_specialite(this);
            }
        }
    }

    public void retirer_matiere(Matiere matiere) {
        if (matiere != null) {
            matieres.remove(matiere);
        }
    }

    public boolean contient_matiere(Matiere matiere) {
        return matieres.contains(matiere);
    }

    public Matiere trouver_matiere_par_code(String code) {
        return matieres.stream()
                .filter(m -> m.get_code().equals(code))
                .findFirst()
                .orElse(null);
    }

    public String get_nom() {
        return nom;
    }

    public Set<Matiere> get_matieres() {
        return Collections.unmodifiableSet(matieres);
    }

    public int get_nombre_matieres() {
        return matieres.size();
    }

    public void set_nom(String nom) {
        this.nom = Objects.requireNonNull(nom, "Le nom de la spécialité ne peut pas être null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Specialite that = (Specialite) o;
        return Objects.equals(nom, that.nom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nom);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Specialite{nom='").append(nom).append("'");
        sb.append(", nombre_matieres=").append(matieres.size());
        if (!matieres.isEmpty()) {
            sb.append(", matieres=[");
            List<String> codes = matieres.stream()
                    .map(Matiere::get_code)
                    .sorted()
                    .toList();
            sb.append(String.join(", ", codes));
            sb.append("]");
        }
        sb.append("}");
        return sb.toString();
    }
}
