package com.patpro.cloudSNS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;

import java.net.URISyntaxException;

import static com.patpro.cloudSNS.AwsSnsUtilityMethods.getSnsClient;

@Component
@RestController
public class sendEmailController {


    public static String ACCESS_KEY;
    @Value("${cloud.aws.credentials.accessKey}")
    public void setAccesskey(String key) {
        ACCESS_KEY = key;
    }
    public static String SECRET_KEY;
    @Value("${cloud.aws.credentials.secretKey}")
    public void setSecretkey(String key) {
        SECRET_KEY = key;
    }


    public static String REGION_KEY;
    @Value("${cloud.aws.region.static}")
    public void setRegionkey(String key) {
        REGION_KEY = key;
    }

    @Autowired
    NotificationMessagingTemplate notificationMessagingTemplate;

    @RequestMapping("getProductos")
    @ResponseBody
    public String[][] getMessage() {
        String[][] productos = {
                {"Chapas de madera", "Stock Mínimo: 50", "Stock Actual: 60"},
                {"Madera Laminada", "Stock Mínimo: 100", "Stock Actual: 100"},
                {"Madera aserrada", "Stock Mínimo: 20", "Stock Actual: 10"},
                {"Madera en rollo", "Stock Mínimo: 60", "Stock Actual: 40"},
                {"Madera recuperada", "Stock Mínimo: 90", "Stock Actual: 200"},
        };
        return productos;
    }

    @RequestMapping("/createTopic")
    private String createTopic(@RequestParam("topic_name") String topicName) throws URISyntaxException {
        //topic name cannot contain spaces
        final CreateTopicRequest topicCreateRequest = CreateTopicRequest.builder().name(topicName).build();

        SnsClient snsClient = getSnsClient();

        final CreateTopicResponse topicCreateResponse = snsClient.createTopic(topicCreateRequest);

        if (topicCreateResponse.sdkHttpResponse().isSuccessful()) {
            System.out.println("Topic creation successful");
            System.out.println("Topics: " + snsClient.listTopics());
        } else {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, topicCreateResponse.sdkHttpResponse().statusText().get()
            );
        }

        snsClient.close();
        return "Topic ARN: " + topicCreateResponse.topicArn();
    }

    @RequestMapping("/addSubscribers")
    private String addSubscriberToTopic(@RequestParam("arn") String arn, @RequestParam("email") String email) throws URISyntaxException {

        SnsClient snsClient = getSnsClient();

        final SubscribeRequest subscribeRequest = SubscribeRequest.builder()
                .topicArn(arn)
                .protocol("email")
                .endpoint(email)
                .build();

        SubscribeResponse subscribeResponse = snsClient.subscribe(subscribeRequest);

        if (subscribeResponse.sdkHttpResponse().isSuccessful()) {
            System.out.println("Subscriber creation successful");
        } else {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, subscribeResponse.sdkHttpResponse().statusText().get()
            );
        }

        snsClient.close();

        return "Subscription ARN request is pending. To confirm the subscription, check your email.";
    }

    @RequestMapping("/generateAlert")
    private String sendEmail(@RequestParam("arn") String arn) throws URISyntaxException {

        String mensaje="Alerta! Los siguientes productos han llegado o están por debajo del stock mínimo permitido\n\n";

        String[][] productos = {
                {"Chapas de madera", "50", "60"},
                {"Madera Laminada", "100", "100"},
                {"Madera aserrada", "20", "10"},
                {"Madera en rollo", "60", "40"},
                {"Madera recuperada", "90", "200"},
        };

        for (String[] producto : productos)
        {
            System.out.println(producto[0]+" - "+producto[1]+" - "+producto[2]);
            if(Integer.parseInt(producto[2])<=Integer.parseInt(producto[1])){
                mensaje=mensaje+
                        "El producto "+producto[0]+" - "+
                        "tiene "+producto[2]+" unidades"+
                        " y su stock mínimo es de "+producto[1]+
                        "\n";
            }

            //System.out.println(producto.length);

        }

        mensaje=mensaje+"\n"+"Estimado gerente, se le recomienda realizar un pedido con los productos indicados";
        System.out.println(mensaje);

       // final String msg = "Los productos han llegado a su stock mínimo!";
        this.notificationMessagingTemplate.sendNotification(arn, mensaje, "Stock");
        return "Email sent to subscribers";
    }

}