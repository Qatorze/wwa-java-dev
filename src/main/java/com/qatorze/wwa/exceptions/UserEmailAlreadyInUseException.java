package com.qatorze.wwa.exceptions;

/**
 * Exception levée lorsqu'une adresse email est déjà utilisée par un autre utilisateur.
 */
public class UserEmailAlreadyInUseException extends RuntimeException {
    
    private static final long serialVersionUID = 1L; // Requis pour la sérialisation.

    /**
     * Constructeur par défaut avec un message d'erreur prédéfini.
     */
    public UserEmailAlreadyInUseException() {
        super("Email already in use.");
    }
}
