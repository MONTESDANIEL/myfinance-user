package com.myfinance.backend.users.config;

import org.springframework.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.myfinance.backend.users.services.JwtTokenProvider;
import com.myfinance.backend.users.entities.security.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
            UserDetailsService userDetailsService,
            ObjectMapper objectMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Verifica si la URL de la solicitud requiere autenticación
        String requestURI = request.getRequestURI();
        if (shouldFilter(requestURI)) {

            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        "No se envio el token. Por favor, asegúrese de que sea enviado correctamente.");
                return;
            }

            String jwtToken = authHeader.substring(7);

            try {
                // Validar el token
                if (!jwtTokenProvider.validateToken(jwtToken)) {
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado.");
                    return;
                }

                String username = jwtTokenProvider.getUsernameFromToken(jwtToken);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception ex) {
                // Manejo de errores
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "Se produjo un error al procesar el token JWT: " + ex.getMessage());
                SecurityContextHolder.clearContext();
                return;
            }
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    // Método para verificar si la URL de la solicitud debe ser filtrada
    private boolean shouldFilter(String requestURI) {
        return requestURI.startsWith("/api/user"); // Proteger todas las rutas que comienzan con "/user"
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        ApiResponse<String> apiResponse = new ApiResponse<>(message, null);
        response.setStatus(status);
        response.setContentType("application/json");

        // Usa ObjectMapper para convertir ApiResponse a JSON
        String jsonResponse = objectMapper.writeValueAsString(apiResponse);
        PrintWriter writer = response.getWriter();
        writer.write(jsonResponse);
        writer.flush();
    }
}
