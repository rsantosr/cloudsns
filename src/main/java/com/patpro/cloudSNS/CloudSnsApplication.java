package com.patpro.cloudSNS;

import com.amazonaws.services.sns.AmazonSNS;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CloudSnsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudSnsApplication.class, args);
    }

    @Bean
    public NotificationMessagingTemplate notificationMessagingTemplate(
            AmazonSNS amazonSNS) {
        return new NotificationMessagingTemplate(amazonSNS);
    }
}
