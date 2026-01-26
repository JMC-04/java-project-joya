package fr.univ.bibliotheque.exception;

/**
 * Exception levée lorsqu'un média n'est pas trouvé.
 * 
 * @author Bibliothèque Universitaire
 * @version 2.0
 */
public class MediaNotFoundException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    public MediaNotFoundException(String message) {
        super(message);
    }
    
    public MediaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
