package fr.univ.bibliotheque.repository;

import fr.univ.bibliotheque.model.*;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.*;

/**
 * Repository pour charger les données des étudiants depuis un fichier XML.
 * 
 * @author Bibliothèque Universitaire
 * @version 2.0
 */
public class EtudiantRepository {
    private static final String DEFAULT_XML_FILE = "data/universite.xml";
    
    private Map<String, Specialite> specialites;
    private Map<String, Etudiant> etudiants;
    
    public EtudiantRepository() {
        this.specialites = new HashMap<>();
        this.etudiants = new HashMap<>();
    }
    
    /**
     * Charge les données depuis le fichier XML.
     * 
     * @param file_path Le chemin vers le fichier XML
     * @return true si le chargement a réussi
     */
    public boolean charger_depuis_xml(String file_path) {
        String chemin_fichier = file_path != null ? file_path : DEFAULT_XML_FILE;
        
        try {
            File fichier_xml = new File(chemin_fichier);
            
            if (!fichier_xml.exists()) {
                System.err.println("ERREUR : Le fichier '" + chemin_fichier + "' est introuvable.");
                return false;
            }
            
            if (!fichier_xml.canRead()) {
                System.err.println("ERREUR : Impossible de lire le fichier '" + chemin_fichier + "'.");
                return false;
            }
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document document = builder.parse(fichier_xml);
            
            document.getDocumentElement().normalize();
            
            specialites.clear();
            etudiants.clear();
            
            // Parser les spécialités
            NodeList specialites_nodes = document.getElementsByTagName("specialites");
            if (specialites_nodes.getLength() > 0) {
                Element specialites_element = (Element) specialites_nodes.item(0);
                NodeList specialite_nodes = specialites_element.getElementsByTagName("specialite");
                
                for (int i = 0; i < specialite_nodes.getLength(); i++) {
                    Node specialite_node = specialite_nodes.item(i);
                    if (specialite_node.getNodeType() == Node.ELEMENT_NODE) {
                        Element specialite_element = (Element) specialite_node;
                        parser_specialite(specialite_element);
                    }
                }
            }
            
            // Parser les étudiants
            NodeList etudiants_nodes = document.getElementsByTagName("etudiants");
            if (etudiants_nodes.getLength() > 0) {
                Element etudiants_element = (Element) etudiants_nodes.item(0);
                NodeList etudiant_nodes = etudiants_element.getElementsByTagName("etudiant");
                
                for (int i = 0; i < etudiant_nodes.getLength(); i++) {
                    Node etudiant_node = etudiant_nodes.item(i);
                    if (etudiant_node.getNodeType() == Node.ELEMENT_NODE) {
                        Element etudiant_element = (Element) etudiant_node;
                        parser_etudiant_independant(etudiant_element);
                    }
                }
            }
            
            System.out.println("Chargement réussi : " + specialites.size() + " spécialité(s), " + etudiants.size() + " étudiant(s)");
            return true;
            
        } catch (Exception e) {
            System.err.println("ERREUR lors du parsing du fichier XML '" + chemin_fichier + "' :");
            System.err.println("  " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("  Cause : " + e.getCause().getMessage());
            }
            return false;
        }
    }
    
    private void parser_specialite(Element specialite_element) {
        String nom_specialite = specialite_element.getAttribute("nom");
        
        if (nom_specialite == null || nom_specialite.trim().isEmpty()) {
            System.err.println("ATTENTION : Spécialité sans nom ignorée.");
            return;
        }
        
        Specialite specialite = specialites.get(nom_specialite);
        if (specialite == null) {
            specialite = new Specialite(nom_specialite);
            specialites.put(nom_specialite, specialite);
        }
        
        NodeList matiere_nodes = specialite_element.getElementsByTagName("matiere");
        
        for (int i = 0; i < matiere_nodes.getLength(); i++) {
            Node matiere_node = matiere_nodes.item(i);
            
            if (matiere_node.getNodeType() == Node.ELEMENT_NODE) {
                Element matiere_element = (Element) matiere_node;
                String code_matiere = matiere_element.getAttribute("code");
                String intitule_matiere = matiere_element.getAttribute("intitule");
                
                if (code_matiere != null && !code_matiere.trim().isEmpty()) {
                    Matiere matiere = new Matiere(code_matiere, intitule_matiere, specialite);
                    specialite.ajouter_matiere(matiere);
                }
            }
        }
    }
    
    private void parser_etudiant_independant(Element etudiant_element) {
        String username = etudiant_element.getAttribute("username");
        String password = etudiant_element.getAttribute("password");
        String nom_specialite = etudiant_element.getAttribute("specialite");
        
        if (username == null || username.trim().isEmpty()) {
            System.err.println("ATTENTION : Étudiant sans username ignoré.");
            return;
        }
        
        if (password == null || password.trim().isEmpty()) {
            System.err.println("ATTENTION : Étudiant '" + username + "' sans password - utilisation d'un mot de passe par défaut.");
            password = "default";
        }
        
        Specialite specialite = null;
        if (nom_specialite != null && !nom_specialite.trim().isEmpty()) {
            specialite = specialites.get(nom_specialite.trim());
            if (specialite == null) {
                System.err.println("ATTENTION : Spécialité '" + nom_specialite + "' non trouvée pour l'étudiant '" + username + 
                                 "'. L'étudiant sera créé sans spécialité.");
            }
        }
        
        Etudiant etudiant;
        if (specialite != null) {
            etudiant = new Etudiant(username, password, specialite);
        } else {
            etudiant = new Etudiant(username, password);
        }
        
        NodeList matieres_nodes = etudiant_element.getElementsByTagName("matieres");
        if (matieres_nodes.getLength() > 0) {
            Element matieres_element = (Element) matieres_nodes.item(0);
            NodeList valeur_nodes = matieres_element.getElementsByTagName("valeur");
            
            for (int i = 0; i < valeur_nodes.getLength(); i++) {
                Node valeur_node = valeur_nodes.item(i);
                
                if (valeur_node.getNodeType() == Node.ELEMENT_NODE) {
                    String code_matiere = valeur_node.getTextContent().trim();
                    
                    if (!code_matiere.isEmpty()) {
                        Matiere matiere = trouver_matiere_par_code(code_matiere);
                        
                        if (matiere != null) {
                            boolean ajoutee = etudiant.ajouter_matiere(matiere);
                            if (!ajoutee) {
                                System.err.println("ATTENTION : Impossible d'ajouter la matière '" + code_matiere + 
                                                 "' à l'étudiant '" + username + "'.");
                            }
                        } else {
                            System.err.println("ATTENTION : Matière '" + code_matiere + "' non trouvée pour l'étudiant '" + username + "'.");
                        }
                    }
                }
            }
        }
        
        if (etudiants.containsKey(username)) {
            System.err.println("ATTENTION : Étudiant '" + username + "' déjà existant - remplacement.");
        }
        
        etudiants.put(username, etudiant);
    }
    
    private Matiere trouver_matiere_par_code(String code) {
        for (Specialite spec : specialites.values()) {
            Matiere matiere = spec.trouver_matiere_par_code(code);
            if (matiere != null) {
                return matiere;
            }
        }
        return null;
    }
    
    public Collection<Specialite> get_specialites() {
        return Collections.unmodifiableCollection(specialites.values());
    }
    
    public Collection<Etudiant> get_etudiants() {
        return Collections.unmodifiableCollection(etudiants.values());
    }
    
    public Specialite trouver_specialite_par_nom(String nom) {
        return specialites.get(nom);
    }
    
    public Etudiant trouver_etudiant_par_username(String username) {
        return etudiants.get(username);
    }
    
    public int get_nombre_specialites() {
        return specialites.size();
    }
    
    public int get_nombre_etudiants() {
        return etudiants.size();
    }
    
    public boolean ajouter_specialite(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            return false;
        }
        if (specialites.containsKey(nom)) {
            return false;
        }
        Specialite specialite = new Specialite(nom.trim());
        specialites.put(nom.trim(), specialite);
        return true;
    }
    
    public boolean ajouter_matiere(String code, String intitule, String nom_specialite) {
        if (code == null || code.trim().isEmpty() || nom_specialite == null || nom_specialite.trim().isEmpty()) {
            return false;
        }
        
        Specialite specialite = specialites.get(nom_specialite.trim());
        if (specialite == null) {
            return false;
        }
        
        Matiere matiere_existante = specialite.trouver_matiere_par_code(code.trim());
        if (matiere_existante != null) {
            return false;
        }
        
        Matiere matiere = new Matiere(code.trim(), intitule != null ? intitule.trim() : "", specialite);
        specialite.ajouter_matiere(matiere);
        return true;
    }
    
    public boolean ajouter_etudiant(String username, String password, String nom_specialite) {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty() ||
            nom_specialite == null || nom_specialite.trim().isEmpty()) {
            return false;
        }
        
        if (etudiants.containsKey(username.trim())) {
            return false;
        }
        
        Specialite specialite = specialites.get(nom_specialite.trim());
        if (specialite == null) {
            return false;
        }
        
        Etudiant etudiant = new Etudiant(username.trim(), password.trim(), specialite);
        etudiants.put(username.trim(), etudiant);
        return true;
    }
    
    public boolean assigner_matiere_a_etudiant(String username, String code_matiere) {
        if (username == null || username.trim().isEmpty() || code_matiere == null || code_matiere.trim().isEmpty()) {
            return false;
        }
        
        Etudiant etudiant = etudiants.get(username.trim());
        if (etudiant == null) {
            return false;
        }
        
        Matiere matiere = null;
        for (Specialite spec : specialites.values()) {
            Matiere m = spec.trouver_matiere_par_code(code_matiere.trim());
            if (m != null) {
                matiere = m;
                break;
            }
        }
        
        if (matiere == null) {
            return false;
        }
        
        return etudiant.ajouter_matiere(matiere);
    }
    
    /**
     * Sauvegarde les données vers le fichier XML.
     * 
     * @param file_path Le chemin vers le fichier XML
     * @return true si la sauvegarde a réussi
     */
    public boolean sauvegarder_vers_xml(String file_path) {
        String chemin_fichier = file_path != null ? file_path : DEFAULT_XML_FILE;
        
        try {
            File fichier = new File(chemin_fichier);
            File parent_dir = fichier.getParentFile();
            if (parent_dir != null && !parent_dir.exists()) {
                parent_dir.mkdirs();
            }
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document document = builder.newDocument();
            
            org.w3c.dom.Element racine = document.createElement("universite");
            document.appendChild(racine);
            
            // Section spécialités
            org.w3c.dom.Element specialites_element = document.createElement("specialites");
            for (Specialite specialite : specialites.values()) {
                org.w3c.dom.Element specialite_element = document.createElement("specialite");
                specialite_element.setAttribute("nom", specialite.get_nom());
                
                for (Matiere matiere : specialite.get_matieres()) {
                    org.w3c.dom.Element matiere_element = document.createElement("matiere");
                    matiere_element.setAttribute("code", matiere.get_code());
                    matiere_element.setAttribute("intitule", matiere.get_intitule());
                    specialite_element.appendChild(matiere_element);
                }
                
                specialites_element.appendChild(specialite_element);
            }
            racine.appendChild(specialites_element);
            
            // Section étudiants
            org.w3c.dom.Element etudiants_element = document.createElement("etudiants");
            for (Etudiant etudiant : etudiants.values()) {
                org.w3c.dom.Element etudiant_element = document.createElement("etudiant");
                etudiant_element.setAttribute("username", etudiant.get_username());
                etudiant_element.setAttribute("password", etudiant.get_password());
                
                if (etudiant.get_specialite() != null) {
                    etudiant_element.setAttribute("specialite", etudiant.get_specialite().get_nom());
                }
                
                List<Matiere> matieres_etudiant = etudiant.get_matieres_suivies();
                if (!matieres_etudiant.isEmpty()) {
                    org.w3c.dom.Element matieres_element = document.createElement("matieres");
                    for (Matiere matiere : matieres_etudiant) {
                        org.w3c.dom.Element valeur_element = document.createElement("valeur");
                        valeur_element.setTextContent(matiere.get_code());
                        matieres_element.appendChild(valeur_element);
                    }
                    etudiant_element.appendChild(matieres_element);
                }
                
                etudiants_element.appendChild(etudiant_element);
            }
            racine.appendChild(etudiants_element);
            
            TransformerFactory transformer_factory = TransformerFactory.newInstance();
            Transformer transformer = transformer_factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(fichier);
            transformer.transform(source, result);
            
            System.out.println("Sauvegarde XML réussie : " + specialites.size() + " spécialité(s), " + 
                             etudiants.size() + " étudiant(s) sauvegardé(s) dans '" + chemin_fichier + "'");
            return true;
            
        } catch (Exception e) {
            System.err.println("ERREUR lors de la sauvegarde XML vers '" + chemin_fichier + "' :");
            System.err.println("  " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("  Cause : " + e.getCause().getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }
}
