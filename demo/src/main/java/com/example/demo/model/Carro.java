package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;


import java.time.LocalDateTime;
@Entity
@Table(name = "carroslog")
public class Carro {

    @Id
    private Long id;

    private String modelo;

    private Integer ano;

    private Double preco;

    private LocalDateTime data_hora;


    public Carro() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public LocalDateTime getTimestamp() {
        return data_hora;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.data_hora = timestamp;
    }
}