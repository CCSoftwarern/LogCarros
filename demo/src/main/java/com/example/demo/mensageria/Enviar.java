package com.example.demo.mensageria;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

public class Enviar {


    public static void main(String[] args) {

        String json = """
                {
                    "id": 21,
                    "modelo": "Camaro Amarelo 3",
                    "ano": 2000,
                    "preco": 1.0E7,
                    "timestamp": "2026-10-29T10:49:13.385360100"
                }
                """;

        enviarMensagem(json, "rafael_claudemyr");
    }

    public static void enviarMensagem(String message, String queue) {
        ConnectionFactory factory = new ConnectionFactory();

        try {
            factory.setUri("amqp://163.176.90.156:5672");
            factory.setUsername("guest");
            factory.setPassword("guest");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            channel.queueDeclare(queue, true, false, false, null);
            channel.basicPublish("", queue, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("MENSAGEM ENVIADA = '" + message + "'");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }


}
