package com.astrovia.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Entidad que representa un paquete dentro de un envío
 */
@Entity
@Table(name = "paquete")
public class Paquete {
    // Entidad que representa un paquete dentro de un envío

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_envio", nullable = false)
    @NotNull(message = "El envío es obligatorio")
    private Envio envio;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    @Column(length = 200, nullable = false)
    private String descripcion;

    @DecimalMin(value = "0.00", message = "El valor declarado no puede ser negativo")
    @Column(name = "valor_declarado", precision = 10, scale = 2)
    private BigDecimal valorDeclarado;

    @DecimalMin(value = "0.01", message = "El peso debe ser mayor a 0")
    @Column(precision = 8, scale = 2)
    private BigDecimal peso;

    @Size(max = 50, message = "Las dimensiones no pueden exceder 50 caracteres")
    @Column(length = 50)
    private String dimensiones;

    // Constructores
    public Paquete() {
    }

    public Paquete(Envio envio, String descripcion, BigDecimal valorDeclarado, 
                   BigDecimal peso, String dimensiones) {
        this.envio = envio;
        this.descripcion = descripcion;
        this.valorDeclarado = valorDeclarado;
        this.peso = peso;
        this.dimensiones = dimensiones;
    }

    public Paquete(String descripcion, BigDecimal valorDeclarado, 
                   BigDecimal peso, String dimensiones) {
        this.descripcion = descripcion;
        this.valorDeclarado = valorDeclarado;
        this.peso = peso;
        this.dimensiones = dimensiones;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Envio getEnvio() {
        return envio;
    }

    public void setEnvio(Envio envio) {
        this.envio = envio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getValorDeclarado() {
        return valorDeclarado;
    }

    public void setValorDeclarado(BigDecimal valorDeclarado) {
        this.valorDeclarado = valorDeclarado;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public String getDimensiones() {
        return dimensiones;
    }

    public void setDimensiones(String dimensiones) {
        this.dimensiones = dimensiones;
    }

    @Override
    public String toString() {
        return "Paquete{" +
                "id=" + id +
                ", descripcion='" + descripcion + '\'' +
                ", valorDeclarado=" + valorDeclarado +
                ", peso=" + peso +
                ", dimensiones='" + dimensiones + '\'' +
                '}';
    }
}