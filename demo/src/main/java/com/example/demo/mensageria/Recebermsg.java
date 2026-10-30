
package com.example.demo.mensageria;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.example.demo.model.Carro;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class Recebermsg implements CommandLineRunner {

    private static final String QUEUE_NAME = "rafael_claudemyr";

    @Value("${spring.datasource.url}")
    private String urlBanco;

    @Value("${spring.datasource.username}")
    private String usuarioBanco;

    @Value("${spring.datasource.password}")
    private String senhaBanco;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Override
    public void run(String... args) {
        lerMensagem();
    }

    public void lerMensagem() {

        ConnectionFactory factory = new ConnectionFactory();

        try {
            factory.setHost("163.176.90.156");
            factory.setPort(5672);
            factory.setUsername("guest");
            factory.setPassword("guest");

            com.rabbitmq.client.Connection connection =
                    factory.newConnection();

            Channel channel = connection.createChannel();

            channel.queueDeclare(
                    QUEUE_NAME,
                    true,
                    false,
                    false,
                    null
            );

            System.out.println("Aguardando mensagens do RabbitMQ...");

            DeliverCallback callback = (consumerTag, delivery) -> {

                long deliveryTag =
                        delivery.getEnvelope().getDeliveryTag();

                try {
                    String message = new String(
                            delivery.getBody(),
                            java.nio.charset.StandardCharsets.UTF_8
                    );

                    System.out.println("JSON RECEBIDO: " + message);

                    // Converte o JSON em objeto Java
                    Carro carro = mapper.readValue(
                            message,
                            Carro.class
                    );

                    // Salva o carro no MySQL
                    salvarCarro(carro);

                    // Confirma o recebimento após salvar
                    channel.basicAck(deliveryTag, false);

                    System.out.println(
                            "Carro salvo com sucesso no MySQL!"
                    );

                } catch (Exception e) {

                    System.err.println(
                            "Erro ao processar mensagem: "
                                    + e.getMessage()
                    );

                    try {
                        // Não recoloca mensagens inválidas na fila
                        channel.basicNack(
                                deliveryTag,
                                false,
                                false
                        );
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            };

            channel.basicConsume(
                    QUEUE_NAME,
                    false,
                    callback,
                    consumerTag -> {
                    }
            );

        } catch (Exception e) {
            System.err.println(
                    "Erro ao iniciar consumidor RabbitMQ:"
            );
            e.printStackTrace();
        }
    }

    public void salvarCarro(Carro carro) throws SQLException {

        String sql = """
                INSERT INTO carroslog
                    (id, modelo, ano, preco, data_hora)
                VALUES (?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    modelo = VALUES(modelo),
                    ano = VALUES(ano),
                    preco = VALUES(preco),
                    data_hora = VALUES(data_hora)
                """;

        try (
                java.sql.Connection conn =
                        DriverManager.getConnection(
                                urlBanco,
                                usuarioBanco,
                                senhaBanco
                        );

                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setLong(1, carro.getId());
            stmt.setString(2, carro.getModelo());
            stmt.setInt(3, carro.getAno());

            stmt.setBigDecimal(
                    4,
                    BigDecimal.valueOf(carro.getPreco())
            );

            stmt.setTimestamp(
                    5,
                    Timestamp.valueOf(carro.getTimestamp())
            );

            stmt.executeUpdate();
        }
    }
}
