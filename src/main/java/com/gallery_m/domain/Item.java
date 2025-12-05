package com.gallery_m.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
@AllArgsConstructor
public class Item implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Referencia a la entidad Zapato (ya cargada de la BD)
    private Zapato zapato;
    
    // Cantidad deseada por el usuario
    private int cantidad;
    
    // Precio histórico (el precio al momento de agregar al carrito)
    private BigDecimal precioHistorico;
    
    /**
     * Calcula el subtotal del item (precio × cantidad)
     * @return Subtotal del item
     */
    public BigDecimal getSubTotal() {
        return precioHistorico.multiply(new BigDecimal(cantidad));
    }
}