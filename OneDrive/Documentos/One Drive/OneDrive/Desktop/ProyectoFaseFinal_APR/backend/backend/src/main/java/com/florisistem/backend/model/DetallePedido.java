package com.florisistem.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id", referencedColumnName = "id")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "insumo_id", referencedColumnName = "id")
    private Insumo insumo;

    @Column(name = "cantidad_utilizada", nullable = false)
    private Integer cantidadUtilizada;

    @Column(name = "precio_subtotal", nullable = false)
    private BigDecimal precioSubtotal;

    // Métodos Getter y Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public Insumo getInsumo() { return insumo; }
    public void setInsumo(Insumo insumo) { this.insumo = insumo; }

    public Integer getCantidadUtilizada() { return cantidadUtilizada; }
    public void setCantidadUtilizada(Integer cantidadUtilizada) { this.cantidadUtilizada = cantidadUtilizada; }

    public BigDecimal getPrecioSubtotal() { return precioSubtotal; }
    public void setPrecioSubtotal(BigDecimal precioSubtotal) { this.precioSubtotal = precioSubtotal; }
}
