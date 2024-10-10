package com.user_management.user_management_service.helpers;

import com.user_management.user_management_service.dto.RegistrationEmailRequest;
import com.user_management.user_management_service.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EmailHelper {

    private static final Logger logger = LoggerFactory.getLogger(EmailHelper.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${email.service.url}")
    private String emailServiceUrl;

    public void sendActivationEmail(User savedUser, String resetToken) {
        try {
            // Prepare the EmailRequest DTO for the email service
            RegistrationEmailRequest emailRequest = new RegistrationEmailRequest(savedUser.getEmail(), savedUser.getName(), resetToken);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create the HttpEntity containing the EmailRequest
            HttpEntity<RegistrationEmailRequest> requestEntity = new HttpEntity<>(emailRequest, headers);

            // Send a POST request to the email service
            ResponseEntity<String> response = restTemplate.postForEntity(
                    emailServiceUrl + "/sendActivationEmail",
                    requestEntity,
                    String.class
            );

            // Handle the response if needed
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Activation email sent successfully to: {}", savedUser.getEmail());
            } else {
                logger.error("Failed to send activation email: {}", response.getBody());
            }
        } catch (Exception e) {
            logger.error("Error sending activation email: {}", e.getMessage());
        }
    }
}
