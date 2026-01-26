package fr.univ.bibliotheque.model;

import java.util.*;

public class Etudiant {
    private String username;
    private String password;
    private Specialite specialite;
    private List<Matiere> matieres_suivies;

    public Etudiant(String username, String password) {
        this.username = Objects.requireNonNull(username, "Le username ne peut pas être null");
        this.password = Objects.requireNonNull(password, "Le password ne peut pas être null");
        this.specialite = null;
        this.matieres_suivies = new ArrayList<>();
    }

    public Etudiant(String username, String password, Specialite specialite) {
        this.username = Objects.requireNonNull(username, "Le username ne peut pas être null");
        this.password = Objects.requireNonNull(password, "Le password ne peut pas être null");
        this.specialite = specialite;
        this.matieres_suivies = new ArrayList<>();
    }

    public boolean ajouter_matiere(Matiere matiere) {
        if (matiere == null) {
            return false;
        }
        if (specialite != null && !matiere.get_specialite().equals(specialite)) {
            return false;
        }
        if (!matieres_suivies.contains(matiere)) {
            matieres_suivies.add(matiere);
            return true;
        }
        return false;
    }

    public boolean retirer_matiere(Matiere matiere) {
        return matieres_suivies.remove(matiere);
    }

    public boolean suit_matiere(Matiere matiere) {
        return matieres_suivies.contains(matiere);
    }

    public boolean suit_matiere_par_code(String code_matiere) {
        return matieres_suivies.stream()
                .anyMatch(m -> m.get_code().equals(code_matiere));
    }

    public boolean verifier_password(String password) {
        return this.password.equals(password);
    }

    public String get_username() {
        return username;
    }
    
    public String get_password() {
        return password;
    }

    public Specialite get_specialite() {
        return specialite;
    }

    public List<Matiere> get_matieres_suivies() {
        return Collections.unmodifiableList(matieres_suivies);
    }

    public List<String> get_codes_matieres() {
        return matieres_suivies.stream()
                .map(Matiere::get_code)
                .toList();
    }

    public void set_username(String username) {
        this.username = Objects.requireNonNull(username, "Le username ne peut pas être null");
    }

    public void set_password(String password) {
        this.password = Objects.requireNonNull(password, "Le password ne peut pas être null");
    }

    public void set_specialite(Specialite specialite) {
        this.specialite = specialite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Etudiant etudiant = (Etudiant) o;
        return Objects.equals(username, etudiant.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Etudiant{username='").append(username).append("'");
        if (specialite != null) {
            sb.append(", specialite='").append(specialite.get_nom()).append("'");
        }
        sb.append(", nombre_matieres=").append(matieres_suivies.size());
        if (!matieres_suivies.isEmpty()) {
            sb.append(", matieres=[");
            List<String> codes = matieres_suivies.stream()
                    .map(Matiere::get_code)
                    .toList();
            sb.append(String.join(", ", codes));
            sb.append("]");
        }
        sb.append("}");
        return sb.toString();
    }
}
