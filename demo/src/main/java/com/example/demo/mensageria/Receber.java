package com.example.demo.mensageria;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Receber {

    static Enviar l = new Enviar();

    public static void main(String[] args) {
        try {
            lerMensagem();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void lerMensagem() throws IOException {
        ConnectionFactory factory;
        Connection connection;
        Channel channel;

        String QUEUE_NAME1 = "rafael_claudemyr";
        String EXCHANGE_NAME1 = "rafael_claudemyr";
        try {
            factory = new ConnectionFactory();
            factory.setUri("amqp://163.176.90.156:5672");
            factory.setUsername("guest");
            factory.setPassword("guest");

            connection = factory.newConnection();
            channel = connection.createChannel();

            //declaring exchanges
            channel.exchangeDeclare(EXCHANGE_NAME1, "direct", true, false, null);

            //declaring queues
            channel.queueDeclare(QUEUE_NAME1, true, false, false, null);

            //binding queues to exchanges
            channel.queueBind(QUEUE_NAME1, EXCHANGE_NAME1, "");

            DeliverCallback deliverCallbackQuestion = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("MENSAGEM RECEBIDA = '" + message + "'");

                String partes[] = message.split(",");

               if(partes[1].equals("12345")){
                    Enviar.enviarMensagem("true", partes[0]);
               }

            };

            channel.basicConsume(QUEUE_NAME1, true, deliverCallbackQuestion, consumerTag -> {
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}