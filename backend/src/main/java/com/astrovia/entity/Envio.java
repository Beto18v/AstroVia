package com.astrovia.entity;

import com.astrovia.enums.EstadoEnvio;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad que representa un envío en el sistema logístico
 */
@Entity
@Table(name = "envio")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 20, nullable = false, updatable = false)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    @NotNull(message = "El cliente es obligatorio")
    private Usuario cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_origen", nullable = false)
    @NotNull(message = "La sucursal origen es obligatoria")
    private Sucursal sucursalOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_destino", nullable = false)
    @NotNull(message = "La sucursal destino es obligatoria")
    private Sucursal sucursalDestino;

    @DecimalMin(value = "0.01", message = "El peso debe ser mayor a 0")
    @Column(precision = 8, scale = 2, nullable = false)
    @NotNull(message = "El peso es obligatorio")
    private BigDecimal peso;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private EstadoEnvio estado = EstadoEnvio.CREADO;

    @DecimalMin(value = "0.00", message = "El precio no puede ser negativo")
    @Column(precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_estimada_entrega")
    private LocalDateTime fechaEstimadaEntrega;

    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String observaciones;

    // Relación bidireccional con Paquete
    @OneToMany(mappedBy = "envio", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Paquete> paquetes = new ArrayList<>();

    // Relación bidireccional con Tracking
    @OneToMany(mappedBy = "envio", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Tracking> trackings = new ArrayList<>();

    // Constructores
    public Envio() {
    }

    public Envio(Usuario cliente, Sucursal sucursalOrigen, Sucursal sucursalDestino, 
                 BigDecimal peso, String observaciones) {
        this.cliente = cliente;
        this.sucursalOrigen = sucursalOrigen;
        this.sucursalDestino = sucursalDestino;
        this.peso = peso;
        this.observaciones = observaciones;
        this.estado = EstadoEnvio.CREADO;
    }

    // Método para establecer datos automáticamente antes de persistir
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.codigo == null || this.codigo.isEmpty()) {
            this.codigo = generateCodigo();
        }
    }

    // Genera un código único para el envío
    private String generateCodigo() {
        String prefix = "ENV";
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        return prefix + uuid;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }

    public Sucursal getSucursalOrigen() {
        return sucursalOrigen;
    }

    public void setSucursalOrigen(Sucursal sucursalOrigen) {
        this.sucursalOrigen = sucursalOrigen;
    }

    public Sucursal getSucursalDestino() {
        return sucursalDestino;
    }

    public void setSucursalDestino(Sucursal sucursalDestino) {
        this.sucursalDestino = sucursalDestino;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public EstadoEnvio getEstado() {
        return estado;
    }

    public void setEstado(EstadoEnvio estado) {
        this.estado = estado;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaEstimadaEntrega() {
        return fechaEstimadaEntrega;
    }

    public void setFechaEstimadaEntrega(LocalDateTime fechaEstimadaEntrega) {
        this.fechaEstimadaEntrega = fechaEstimadaEntrega;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<Paquete> getPaquetes() {
        return paquetes;
    }

    public void setPaquetes(List<Paquete> paquetes) {
        this.paquetes = paquetes;
    }

    public List<Tracking> getTrackings() {
        return trackings;
    }

    public void setTrackings(List<Tracking> trackings) {
        this.trackings = trackings;
    }

    // Métodos de utilidad para relaciones bidireccionales
    public void addPaquete(Paquete paquete) {
        paquetes.add(paquete);
        paquete.setEnvio(this);
    }

    public void removePaquete(Paquete paquete) {
        paquetes.remove(paquete);
        paquete.setEnvio(null);
    }

    public void addTracking(Tracking tracking) {
        trackings.add(tracking);
        tracking.setEnvio(this);
    }

    public void removeTracking(Tracking tracking) {
        trackings.remove(tracking);
        tracking.setEnvio(null);
    }

    @Override
    public String toString() {
        return "Envio{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", peso=" + peso +
                ", estado=" + estado +
                ", precio=" + precio +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaEstimadaEntrega=" + fechaEstimadaEntrega +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }
}