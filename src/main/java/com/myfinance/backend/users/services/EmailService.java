package com.myfinance.backend.users.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.myfinance.backend.users.entities.user.AppUser;
import com.myfinance.backend.users.repositories.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private UserRepository userRepository;

    public void sendRecoveryEmail(String toEmail, String token) throws MessagingException, IOException {

        // URL dinámica
        String recoveryUrl = "https://mi-aplicacion.com/reset-password?token=" + token;

        // Cargar plantilla HTML
        Resource resource = resourceLoader.getResource("classpath:templates/recovery-email.html");
        String content = new String(Files.readAllBytes(resource.getFile().toPath()));

        // Obtener el usuario
        AppUser user = userRepository.findByEmail(toEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Reemplazar los marcadores con los valores correspondientes
        content = content.replace("${recoveryUrl}", recoveryUrl); // Reemplazar ${recoveryUrl} con la URL
        content = content.replace("${userName}", user.getName()); // Reemplazar ${userName} con el nombre del usuario

        // Configurar el correo
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(toEmail);
        helper.setSubject("Recuperación de contraseña");
        helper.setText(content, true); // Establecer el contenido con HTML

        // Enviar el correo
        mailSender.send(mimeMessage);
    }

}
