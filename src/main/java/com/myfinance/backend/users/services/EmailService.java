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

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private UserRepository userRepository;

    public void sendRecoveryEmail(String toEmail, String token) throws MessagingException, IOException {

        // Generar la URL dinámica incluyendo el token
        String recoveryUrl = "http://192.168.1.2:5173/reset-password?token=" + token;

        // Cargar plantilla HTML
        Resource resource = resourceLoader.getResource("classpath:templates/recovery-email.html");
        String content = new String(Files.readAllBytes(resource.getFile().toPath()));

        // Obtener el usuario
        AppUser user = userRepository.findByEmail(toEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Reemplazar los marcadores con los valores correspondientes
        content = content.replace("${recoveryUrl}", recoveryUrl);
        content = content.replace("${userName}", user.getName());

        // Configurar el correo
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(toEmail);
        helper.setSubject("Recuperación de contraseña");
        helper.setText(content, true);
        // Agregar íconos como adjuntos en línea
        helper.addInline("logoImage", resourceLoader.getResource("classpath:templates/LogoVerde.png"));
        helper.addInline("githubIcon", resourceLoader.getResource("classpath:templates/github.png"));
        helper.addInline("linkedinIcon", resourceLoader.getResource("classpath:templates/linkedin.png"));
        helper.addInline("whatsappIcon", resourceLoader.getResource("classpath:templates/whatsapp.png"));

        // Enviar el correo
        mailSender.send(mimeMessage);
    }

}
