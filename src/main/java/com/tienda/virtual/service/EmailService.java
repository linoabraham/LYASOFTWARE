// src/main/java/com/tienda/virtual/service/EmailService.java
package com.tienda.virtual.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String toEmail, String userName, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("richar.romero.romero@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("¡Bienvenido a Consultora RYL! Verifica tu cuenta");

            String htmlContent = "<!DOCTYPE html>"
                    + "<html lang=\"es\">"
                    + "<head>"
                    + "    <meta charset=\"UTF-8\">"
                    + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                    + "    <title>¡Bienvenido a Consultora RYL! Verifica tu cuenta</title>"
                    + "    <link href=\"https://fonts.googleapis.com/css2?family=Inter:wght@400;700&family=Playfair+Display:wght@700&display=swap\" rel=\"stylesheet\">"
                    + "    <style>"
                    + "        body { font-family: 'Inter', sans-serif; background-color: #f8f5f2; margin: 0; padding: 0; color: #333; }"
                    + "        .container { max-width: 600px; margin: 30px auto; background-color: #fff; border-radius: 15px; overflow: hidden; box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1); border: 1px solid #e0e0e0; }"
                    + "        .header { background-color: #fefefe; padding: 25px; text-align: center; border-bottom: 1px solid #f0f0f0; }"
                    + "        .header img { max-width: 180px; height: auto; margin-bottom: 15px; display: block; margin-left: auto; margin-right: auto; }"
                    + "        .header h1 { margin: 0; font-family: 'Playfair Display', serif; font-size: 30px; color: #004080; font-weight: 700; }"
                    + "        .content { padding: 35px; line-height: 1.7; text-align: center; }"
                    + "        .content h2 { font-family: 'Playfair Display', serif; color: #004080; font-size: 26px; margin-top: 0; margin-bottom: 20px; }"
                    + "        .content p { margin-bottom: 15px; font-size: 16px; }"
                    + "        .verification-code { background-color: #f0f8ff; color: #004080; font-size: 38px; font-weight: bold; text-align: center; padding: 20px 25px; border-radius: 10px; margin: 30px auto; letter-spacing: 4px; max-width: 280px; border: 1px dashed #a0c4ff; }"
                    + "        .note { font-size: 14px; color: #999999; margin-top: 25px; line-height: 1.5; }"
                    + "        .footer { background-color: #f0f0f0; padding: 25px; text-align: center; font-size: 13px; color: #777; border-bottom-left-radius: 15px; border-bottom-right-radius: 15px; border-top: 1px solid #e0e0e0; }"
                    + "        .footer p { margin: 5px 0; }"
                    + "        .signature { margin-top: 30px; font-style: italic; color: #555; }"
                    + "    </style>"
                    + "</head>"
                    + "<body>"
                    + "    <div class=\"container\">"
                    + "        <div class=\"header\">"
                    + "            <img src=\"https://res.cloudinary.com/dod56svuf/image/upload/v1751876631/Logotipo_reposter%C3%ADa_y_macarrones_marr%C3%B3n_qa2wd1.png\" alt=\"Logotipo de Consultora RYL\">"
                    + "            <h1>Consultora RYL</h1>"
                    + "        </div>"
                    + "        <div class=\"content\">"
                    + "            <h2>¡Hola, " + userName + "!</h2>"
                    + "            <p>¡Qué gusto tenerte con nosotros! Gracias por unirte a la familia de <strong>Consultora RYL</strong>.</p>"
                    + "            <p>Para activar tu cuenta y comenzar a disfrutar de nuestros servicios, por favor usa el siguiente código de verificación:</p>"
                    + "            <div class=\"verification-code\">" + verificationCode + "</div>"
                    + "            <p class=\"note\">Este código es válido por <strong>4 minutos</strong>. Si expira, solicita uno nuevo.</p>"
                    + "            <p>Si no solicitaste este registro, ignora este mensaje. Protegemos tu seguridad.</p>"
                    + "            <p class=\"signature\">Con aprecio,</p>"
                    + "            <p class=\"signature\">El equipo de Consultora RYL</p>"
                    + "        </div>"
                    + "        <div class=\"footer\">"
                    + "            <p>&copy; " + java.time.Year.now().getValue() + " Consultora RYL. Todos los derechos reservados.</p>"
                    + "            <p>¡Gracias por confiar en nosotros!</p>"
                    + "        </div>"
                    + "    </div>"
                    + "</body>"
                    + "</html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            System.out.println("Correo de verificación HTML enviado exitosamente a: " + toEmail);
        } catch (MailException | MessagingException e) {
            System.err.println("Error al enviar correo de verificación HTML a " + toEmail + ": " + e.getMessage());
            throw new RuntimeException("No se pudo enviar el correo de verificación. Por favor, inténtalo de nuevo más tarde.", e);
        }
    }
}
