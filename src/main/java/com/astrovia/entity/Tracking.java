package com.astrovia.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Entidad que representa un evento de seguimiento de un envío
 */
@Entity
@Table(name = "tracking")
public class Tracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_envio", nullable = false)
    @NotNull(message = "El envío es obligatorio")
    private Envio envio;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Size(max = 100, message = "La ubicación no puede exceder 100 caracteres")
    @Column(length = 100)
    private String ubicacion;

    @NotBlank(message = "El evento es obligatorio")
    @Size(max = 100, message = "El evento no puede exceder 100 caracteres")
    @Column(length = 100, nullable = false)
    private String evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String observaciones;

    // Constructores
    public Tracking() {
    }

    public Tracking(Envio envio, String ubicacion, String evento, Usuario usuario, String observaciones) {
        this.envio = envio;
        this.ubicacion = ubicacion;
        this.evento = evento;
        this.usuario = usuario;
        this.observaciones = observaciones;
    }

    public Tracking(Envio envio, String evento, Usuario usuario) {
        this.envio = envio;
        this.evento = evento;
        this.usuario = usuario;
    }

    public Tracking(Envio envio, String evento, String ubicacion) {
        this.envio = envio;
        this.evento = evento;
        this.ubicacion = ubicacion;
    }

    // Método para establecer la fecha y hora automáticamente
    @PrePersist
    protected void onCreate() {
        if (this.fechaHora == null) {
            this.fechaHora = LocalDateTime.now();
        }
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

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return "Tracking{" +
                "id=" + id +
                ", fechaHora=" + fechaHora +
                ", ubicacion='" + ubicacion + '\'' +
                ", evento='" + evento + '\'' +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }
}