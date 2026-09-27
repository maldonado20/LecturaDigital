package com.florisistem.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", referencedColumnName = "id", nullable = true)
    private Usuario cliente;

    @Column(name = "nombre_receptor", nullable = false)
    private String nombreReceptor;

    @Column(name = "telefono_receptor", nullable = false)
    private String telefonoReceptor;

    @Column(name = "direccion_entrega", nullable = false, columnDefinition = "TEXT")
    private String direccionEntrega;

    @Column(name = "fecha_entrega", nullable = false)
    private LocalDate fechaEntrega;

    @Column(name = "hora_entrega", nullable = false)
    private LocalTime laHoraEntrega;

    @Column(columnDefinition = "TEXT")
    private String dedicatoria;

    @Column(name = "precio_total", nullable = false)
    private BigDecimal precioTotal;

    @Column(nullable = false)
    private String estado = "PENDIENTE";

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // Métodos Getter y Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getCliente() { return cliente; }
    public void setCliente(Usuario cliente) { this.cliente = cliente; }

    public String getNombreReceptor() { return nombreReceptor; }
    public void setNombreReceptor(String nombreReceptor) { this.nombreReceptor = nombreReceptor; }

    public String getTelefonoReceptor() { return telefonoReceptor; }
    public void setTelefonoReceptor(String telefonoReceptor) { this.telefonoReceptor = telefonoReceptor; }

    public String getDireccionEntrega() { return direccionEntrega; }
    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }

    public LocalDate getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDate fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public LocalTime getLaHoraEntrega() { return laHoraEntrega; }
    public void setLaHoraEntrega(LocalTime laHoraEntrega) { this.laHoraEntrega = laHoraEntrega; }

    public String getDedicatoria() { return dedicatoria; }
    public void setDedicatoria(String dedicatoria) { this.dedicatoria = dedicatoria; }

    public BigDecimal getPrecioTotal() { return precioTotal; }
    public void setPrecioTotal(BigDecimal precioTotal) { this.precioTotal = precioTotal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
