package com.astrovia.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una sucursal del sistema logístico
 */
    // Entidad que representa una sucursal del sistema logístico
@Entity
@Table(name = "sucursal")
public class Sucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la sucursal es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(length = 100, nullable = false)
    private String nombre;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 50, message = "La ciudad no puede exceder 50 caracteres")
    @Column(length = 50, nullable = false)
    private String ciudad;

    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    @Column(length = 200)
    private String direccion;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    @Column(length = 20)
    private String telefono;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // Relación bidireccional con Envio (sucursal origen)
    @OneToMany(mappedBy = "sucursalOrigen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Envio> enviosOrigen = new ArrayList<>();

    // Relación bidireccional con Envio (sucursal destino)
    @OneToMany(mappedBy = "sucursalDestino", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Envio> enviosDestino = new ArrayList<>();

    // Constructores
    public Sucursal() {
    }

    public Sucursal(String nombre, String ciudad, String direccion, String telefono) {
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    // Método para establecer la fecha de creación automáticamente
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<Envio> getEnviosOrigen() {
        return enviosOrigen;
    }

    public void setEnviosOrigen(List<Envio> enviosOrigen) {
        this.enviosOrigen = enviosOrigen;
    }

    public List<Envio> getEnviosDestino() {
        return enviosDestino;
    }

    public void setEnviosDestino(List<Envio> enviosDestino) {
        this.enviosDestino = enviosDestino;
    }

    // Métodos de utilidad para las relaciones bidireccionales
    public void addEnvioOrigen(Envio envio) {
        enviosOrigen.add(envio);
        envio.setSucursalOrigen(this);
    }

    public void removeEnvioOrigen(Envio envio) {
        enviosOrigen.remove(envio);
        envio.setSucursalOrigen(null);
    }

    public void addEnvioDestino(Envio envio) {
        enviosDestino.add(envio);
        envio.setSucursalDestino(this);
    }

    public void removeEnvioDestino(Envio envio) {
        enviosDestino.remove(envio);
        envio.setSucursalDestino(null);
    }

    @Override
    public String toString() {
        return "Sucursal{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}