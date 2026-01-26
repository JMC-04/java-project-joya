package fr.univ.bibliotheque.strategy.export;

import fr.univ.bibliotheque.service.Catalog;
import fr.univ.bibliotheque.model.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Collection;
import java.util.Objects;

/**
 * Exporteur XML pour les médias.
 * 
 * @author Bibliothèque Universitaire
 * @version 2.0
 */
public class XMLExporter implements MediaExporter {
    
    private final Catalog catalog;
    
    public XMLExporter(Catalog catalog) {
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
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document document = builder.newDocument();
            
            org.w3c.dom.Element racine = document.createElement("medias");
            document.appendChild(racine);
            
            for (Media media : medias) {
                org.w3c.dom.Element element_media = creer_element_media(document, media);
                racine.appendChild(element_media);
            }
            
            TransformerFactory transformer_factory = TransformerFactory.newInstance();
            Transformer transformer = transformer_factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(fichier);
            transformer.transform(source, result);
            
            System.out.println("Export XML réussi : " + medias.size() + 
                             " média(s) exporté(s) dans '" + chemin_fichier + "'");
            return true;
            
        } catch (Exception e) {
            System.err.println("ERREUR lors de l'export XML vers '" + chemin_fichier + "' :");
            System.err.println("  " + e.getMessage());
            return false;
        }
    }
    
    private org.w3c.dom.Element creer_element_media(org.w3c.dom.Document document, Media media) {
        org.w3c.dom.Element element_media = document.createElement("media");
        
        element_media.setAttribute("id", media.get_id());
        element_media.setAttribute("type", media.get_type_media());
        element_media.setAttribute("titre", media.get_titre());
        element_media.setAttribute("auteur", media.get_auteur());
        element_media.setAttribute("annee", String.valueOf(media.get_annee()));
        element_media.setAttribute("compteur_acces", String.valueOf(media.get_compteur_acces()));
        
        org.w3c.dom.Element element_description = document.createElement("description");
        element_description.setTextContent(media.get_description());
        element_media.appendChild(element_description);
        
        if (media instanceof Document) {
            Document doc = (Document) media;
            element_media.setAttribute("nombre_de_pages", String.valueOf(doc.get_nombre_de_pages()));
        } else if (media instanceof SeanceVideo) {
            SeanceVideo video = (SeanceVideo) media;
            element_media.setAttribute("duree", String.valueOf(video.get_duree()));
        } else if (media instanceof QuizOnline) {
            QuizOnline quiz = (QuizOnline) media;
            element_media.setAttribute("duree_estimee", String.valueOf(quiz.get_duree_estimee()));
            element_media.setAttribute("niveau_difficulte", quiz.get_niveau_difficulte());
        }
        
        var matieres = catalog.get_matieres_media(media.get_id());
        if (!matieres.isEmpty()) {
            org.w3c.dom.Element element_matieres = document.createElement("matieres");
            for (Matiere matiere : matieres) {
                org.w3c.dom.Element element_matiere = document.createElement("matiere");
                element_matiere.setAttribute("code", matiere.get_code());
                element_matiere.setAttribute("intitule", matiere.get_intitule());
                element_matiere.setAttribute("specialite", matiere.get_specialite().get_nom());
                element_matieres.appendChild(element_matiere);
            }
            element_media.appendChild(element_matieres);
        }
        
        return element_media;
    }
    
    @Override
    public String get_extension() {
        return "xml";
    }
    
    @Override
    public String get_description() {
        return "XML";
    }
}
