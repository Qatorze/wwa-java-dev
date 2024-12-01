package com.qatorze.wwa.utils;

import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.qatorze.wwa.exceptions.InvalidCredentialsException;
import com.qatorze.wwa.exceptions.UserByIdNotFoundException;
import com.qatorze.wwa.exceptions.UserEmailAlreadyInUseException;

public class GlobalExceptionHandler {

	/*
     * Mapping des types d'erreurs HTTP renvoyées par l'application.
     * Exemple :
     * - NOT_FOUND -> 404
     * - UNAUTHORIZED -> 401
     */
    
    /**
     * Gère l'exception `UserByIdNotFoundException` pour retourner une erreur 404.
     * @param e L'exception levée.
     * @return Réponse avec le statut 404 et le message d'erreur.
     */
    @ExceptionHandler(UserByIdNotFoundException.class)
    public ResponseEntity<String> handleUserByIdNotFoundException(UserByIdNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
    
    /**
     * Gère l'exception `InvalidCredentialsException` pour retourner une erreur 401.
     * @param e L'exception levée.
     * @return Réponse avec le statut 401 et le message d'erreur.
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentialsException(InvalidCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
    
    /**
     * Gère l'exception `UserEmailAlreadyInUse` pour retourner une erreur 401.
     * @param e L'exception levée.
     * @return Réponse avec le statut 401 et le message d'erreur.
     */
    @ExceptionHandler(UserEmailAlreadyInUseException.class)
    public ResponseEntity<String> handleClienteEmailAlreadyInUse(UserEmailAlreadyInUseException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
}
