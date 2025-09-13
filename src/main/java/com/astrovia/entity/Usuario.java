package com.astrovia.entity;

import com.astrovia.enums.Rol;
import jakarta.persistence.*;
    // Entidad que representa un usuario del sistema (clientes, operadores y administradores)
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un usuario del sistema (incluye clientes, operadores y administradores)
 */
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El username es obligatorio")
    @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
    @Column(unique = true, length = 50, nullable = false)
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100, message = "Los nombres no pueden exceder 100 caracteres")
    @Column(length = 100, nullable = false)
    private String nombres;

    @Email(message = "Debe ser un email válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(length = 100, nullable = false)
    private String email;

    @Size(max = 20, message = "El documento no puede exceder 20 caracteres")
    @Column(length = 20)
    private String doc;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    @Column(length = 20)
    private String telefono;

    @Size(max = 500, message = "La dirección no puede exceder 500 caracteres")
    @Column(length = 500)
    private String direccion;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "El rol es obligatorio")
    @Column(length = 20, nullable = false)
    private Rol rol;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // Relación bidireccional con Tracking
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tracking> trackings = new ArrayList<>();

    // Relación bidireccional con Envio (para clientes)
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Envio> envios = new ArrayList<>();

    // Constructores
    public Usuario() {
    }

    public Usuario(String username, String password, String nombres, String email, Rol rol) {
        this.username = username;
        this.password = password;
        this.nombres = nombres;
        this.email = email;
        this.rol = rol;
        this.activo = true;
    }

    public Usuario(String username, String password, String nombres, String email, String doc, String telefono, String direccion, Rol rol) {
        this.username = username;
        this.password = password;
        this.nombres = nombres;
        this.email = email;
        this.doc = doc;
        this.telefono = telefono;
        this.direccion = direccion;
        this.rol = rol;
        this.activo = true;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<Tracking> getTrackings() {
        return trackings;
    }

    public void setTrackings(List<Tracking> trackings) {
        this.trackings = trackings;
    }

    public List<Envio> getEnvios() {
        return envios;
    }

    public void setEnvios(List<Envio> envios) {
        this.envios = envios;
    }

    // Métodos de utilidad para la relación bidireccional con Tracking
    public void addTracking(Tracking tracking) {
        trackings.add(tracking);
        tracking.setUsuario(this);
    }

    public void removeTracking(Tracking tracking) {
        trackings.remove(tracking);
        tracking.setUsuario(null);
    }

}