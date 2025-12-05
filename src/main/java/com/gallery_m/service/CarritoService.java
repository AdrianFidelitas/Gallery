package com.gallery_m.service;

import com.gallery_m.domain.*;
import com.gallery_m.repository.FacturaRepository;
import com.gallery_m.repository.VentaRepository;
import com.gallery_m.repository.ZapatoRepository;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarritoService {

    private static final String ATTRIBUTE_CARRITO = "carrito";

    private final ZapatoRepository zapatoRepository;
    private final FacturaRepository facturaRepository;
    private final VentaRepository ventaRepository;

    public CarritoService(
            ZapatoRepository zapatoRepository,
            FacturaRepository facturaRepository,
            VentaRepository ventaRepository) {

        this.zapatoRepository = zapatoRepository;
        this.facturaRepository = facturaRepository;
        this.ventaRepository = ventaRepository;
    }

    // --- 1. GESTIÓN DE SESIÓN ---
    /**
     * Obtiene el carrito de la sesión actual Si no existe, crea uno nuevo
     */
    public List<Item> obtenerCarrito(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<Item> carrito = (List<Item>) session.getAttribute(ATTRIBUTE_CARRITO);
        if (carrito == null) {
            carrito = new ArrayList<>();
        }
        return carrito;
    }

    /**
     * Guarda el carrito en la sesión
     */
    public void guardarCarrito(HttpSession session, List<Item> carrito) {
        session.setAttribute(ATTRIBUTE_CARRITO, carrito);
    }

    // --- 2. AGREGAR ZAPATO AL CARRITO ---
    /**
     * Agrega un zapato al carrito (cantidad por defecto = 1) Si ya existe,
     * incrementa la cantidad Valida stock disponible
     */
    public void agregarZapato(List<Item> carrito, Integer idZapato) {
        // 1. Buscar el zapato en BD
        Zapato zapato = zapatoRepository.findById(idZapato)
                .orElseThrow(() -> new RuntimeException("Zapato no encontrado."));

        // Validar que el zapato esté activo
        if (!zapato.isActivo()) {
            throw new RuntimeException("Este zapato no está disponible.");
        }

        // 2. Buscar si el item ya existe en el carrito
        Optional<Item> itemExistente = carrito.stream()
                .filter(i -> i.getZapato().getIdZapato().equals(idZapato))
                .findFirst();

        int cantidad = 1; // Por defecto agregamos 1 unidad

        if (itemExistente.isPresent()) {
            // Ya existe en el carrito, incrementamos cantidad
            Item item = itemExistente.get();
            int nuevaCantidad = item.getCantidad() + cantidad;

            // 3. VALIDACIÓN DE STOCK
            if (nuevaCantidad > zapato.getExistencias()) {
                throw new RuntimeException("Stock insuficiente. Solo hay "
                        + zapato.getExistencias() + " unidades disponibles.");
            }
            item.setCantidad(nuevaCantidad);

        } else {
            // 4. Nuevo Item - Validación de Stock
            if (cantidad > zapato.getExistencias()) {
                throw new RuntimeException("Stock insuficiente. Solo hay "
                        + zapato.getExistencias() + " unidades disponibles.");
            }

            // 5. Crear y añadir nuevo Item
            Item nuevoItem = new Item();
            nuevoItem.setZapato(zapato);
            nuevoItem.setCantidad(cantidad);
            nuevoItem.setPrecioHistorico(zapato.getPrecio()); // Capturar precio actual
            carrito.add(nuevoItem);
        }
    }

    // --- 3. BUSCAR ITEM EN EL CARRITO ---
    /**
     * Busca un item en el carrito por ID de zapato
     */
    public Item buscarItem(List<Item> carrito, Integer idZapato) {
        if (carrito == null) {
            return null;
        }

        return carrito.stream()
                .filter(item -> item.getZapato().getIdZapato().equals(idZapato))
                .findFirst()
                .orElse(null);
    }

    // --- 4. ELIMINAR ITEM DEL CARRITO ---
    /**
     * Elimina un item del carrito por ID de zapato
     */
    public void eliminarItem(List<Item> carrito, Integer idZapato) {
        carrito.removeIf(item -> item.getZapato().getIdZapato().equals(idZapato));
    }

    // --- 5. ACTUALIZAR CANTIDAD ---
    /**
     * Actualiza la cantidad de un item en el carrito Si la nueva cantidad es 0
     * o menor, elimina el item
     */
    public void actualizarCantidad(List<Item> carrito, Integer idZapato, int nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            eliminarItem(carrito, idZapato);
            return;
        }

        Optional<Item> itemExistente = carrito.stream()
                .filter(i -> i.getZapato().getIdZapato().equals(idZapato))
                .findFirst();

        if (itemExistente.isPresent()) {
            Item item = itemExistente.get();
            Zapato zapato = item.getZapato();

            // Validar stock disponible
            if (nuevaCantidad > zapato.getExistencias()) {
                throw new RuntimeException("No hay suficiente stock disponible. "
                        + "Solo hay " + zapato.getExistencias() + " unidades.");
            }
            item.setCantidad(nuevaCantidad);
        }
    }

    // --- 6. CÁLCULOS ---
    /**
     * Cuenta el total de unidades en el carrito
     */
    public int contarUnidades(List<Item> carrito) {
        if (carrito == null || carrito.isEmpty()) {
            return 0;
        }
        return carrito.stream()
                .mapToInt(Item::getCantidad)
                .sum();
    }

    /**
     * Calcula el total del carrito (suma de subtotales)
     */
    public BigDecimal calcularTotal(List<Item> carrito) {
        return carrito.stream()
                .map(Item::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // --- 7. LIMPIAR CARRITO ---
    /**
     * Limpia completamente el carrito de la sesión
     */
    public void limpiarCarrito(HttpSession session) {
        List<Item> carrito = obtenerCarrito(session);
        if (carrito != null) {
            carrito.clear();
        }
        guardarCarrito(session, carrito);
    }

    @Transactional
    public Factura procesarCompra(List<Item> carrito, Usuario usuario) {

        if (carrito == null || carrito.isEmpty()) {
            throw new RuntimeException("El carrito está vacío.");
        }

        // Crear factura
        Factura factura = new Factura();
        factura.setUsuario(usuario);
        factura.setFecha(LocalDateTime.now());
        factura.setTotal(calcularTotal(carrito));
        factura.setEstado(EstadoFactura.Pagada);
        factura = facturaRepository.save(factura);

        // Crear ventas y actualizar stock
        for (Item item : carrito) {
            Zapato zapato = zapatoRepository.findById(item.getZapato().getIdZapato())
                    .orElseThrow(() -> new RuntimeException("Zapato no encontrado."));

            if (item.getCantidad() > zapato.getExistencias()) {
                throw new RuntimeException("Stock insuficiente para " + zapato.getNombreZapato());
            }

            Venta venta = new Venta();
            venta.setFactura(factura);
            venta.setZapato(zapato);
            venta.setPrecioHistorico(item.getPrecioHistorico());
            venta.setCantidad(item.getCantidad());
            ventaRepository.save(venta);

            zapato.setExistencias(zapato.getExistencias() - item.getCantidad());
            zapatoRepository.save(zapato);
        }

        return factura;
    }

}
