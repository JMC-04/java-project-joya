package fr.univ.bibliotheque.exception;

/**
 * Exception levée lorsque des données invalides sont détectées.
 * 
 * @author Bibliothèque Universitaire
 * @version 2.0
 */
public class InvalidDataException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    public InvalidDataException(String message) {
        super(message);
    }
    
    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
