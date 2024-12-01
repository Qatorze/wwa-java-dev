package com.qatorze.wwa.config;

import javax.naming.AuthenticationException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qatorze.wwa.dtos.RegisterRequestDTO;
import com.qatorze.wwa.dtos.UserResponseDTO;
import com.qatorze.wwa.services.AuthService;
import com.qatorze.wwa.services.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Fichier responsable de la gestion des processus d'authentification 
 * (connexion ou enregistrement) et de la génération des tokens JWT.
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager; // Gestionnaire d'authentification.
    private final JwtService jwtService; // Service pour gérer les tokens JWT.
    private final AuthService authService; // Service pour gérer l'enregistrement et la connexion.

    /**
     * Constructeur pour injecter les dépendances nécessaires.
     */
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtService jwtService, AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.authService = authService;
        setFilterProcessesUrl("/login"); // Spécifie l'URL de l'endpoint de connexion.
    }
    
    /**
     * Tente une authentification selon le type de requête (login ou register).
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
    	 // Vérifie si l'utilisateur essaie de s'enregistrer.
        if (request.getRequestURI().contains("/register")) {
            return handleRegister(request, response);
        }
        return handleLogin(request, response);
    }
    
    /**
     * Gestion des tentatives de connexion.
     */
    private Authentication handleLogin(HttpServletRequest request, HttpServletResponse response) {
        try {
        	// Récupère les informations de connexion de la requête.
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            // Crée un objet UsernamePasswordAuthenticationToken et tente l'authentification.
            return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (Exception e) {
            throw new RuntimeException("Invalid credentials");
        }
    }

    /**
     * Gestion des tentatives d'enregistrement.
     */
    private Authentication handleRegister(HttpServletRequest request, HttpServletResponse response) {
        try {    
        	// Récupère les données d'enregistrement de la requête.
            String surname = request.getParameter("surname");
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            
            // Crée un DTO pour l'enregistrement et appelle le service d'authentification.
            RegisterRequestDTO registerRequest = new RegisterRequestDTO(surname, name, email, password);
            authService.register(registerRequest); // Enregistre l'utilisateur.

            // Pas besoin d'authentifier après l'enregistrement.
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Registration failed");
        }
    }
    
    /**
     * Réponse pour les authentifications échouées.
     */
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
            throws IOException, ServletException {
    	// Reponse en cas d'erreur lors de l'authentification
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Code 401
        response.setContentType("application/json");
        // Message d'erreur en JSON.
        response.getWriter().write("{\"error\": \"Invalid email or password\"}");
    }

    /**
     * Réponse pour les authentifications réussies.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
    	// Récupère les détails de l'utilisateur authentifié.
        UserResponseDTO user = (UserResponseDTO) authResult.getPrincipal();

        // Génère le token JWT.
        String token = jwtService.generateToken(user);

        // Prépare une réponse JSON avec le token.
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Restituisci il token JWT nella risposta
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("token", token);

        // Retourne la réponse.
        new ObjectMapper().writeValue(response.getOutputStream(), responseBody);
    }
}