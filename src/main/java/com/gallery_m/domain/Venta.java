package com.gallery_m.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "venta")
@NoArgsConstructor 
@AllArgsConstructor
public class Venta implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Integer idVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_factura")
    private Factura factura;

    // ✅ CAMBIADO: Ahora usa Zapato en lugar de Producto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_zapato")
    private Zapato zapato;

    // Precio al momento de la venta (histórico)
    @Column(name = "precio_historico", precision = 12, scale = 2)
    private BigDecimal precioHistorico;
    
    // Cantidad vendida
    @Column(nullable = false)
    private int cantidad;
    
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
    
    /**
     * Calcula el subtotal de esta venta (precio × cantidad)
     */
    public BigDecimal getSubtotal() {
        return precioHistorico.multiply(new BigDecimal(cantidad));
    }
    
    // Hooks de JPA para gestionar fechas automáticamente
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaModificacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
    }
}