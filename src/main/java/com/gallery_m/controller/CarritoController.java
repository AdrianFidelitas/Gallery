package com.gallery_m.controller;

import com.gallery_m.domain.Item;
import com.gallery_m.domain.Usuario;
import com.gallery_m.domain.Factura;
import com.gallery_m.service.CarritoService;
import com.gallery_m.service.UsuarioService;
import com.gallery_m.service.FacturaService;

import jakarta.servlet.http.HttpSession;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CarritoController {

    private final CarritoService carritoService;
    private final UsuarioService usuarioService;
    private final FacturaService facturaService;   // <-- FALTABA ESTO

    public CarritoController(CarritoService carritoService,
            UsuarioService usuarioService,
            FacturaService facturaService) {
        this.carritoService = carritoService;
        this.usuarioService = usuarioService;
        this.facturaService = facturaService;
    }

    // ============================
    // 1. LISTADO DEL CARRITO
    // ============================
    @GetMapping("/carrito/listado")
    public String listado(HttpSession session, Model model) {
        List<Item> carrito = carritoService.obtenerCarrito(session);

        model.addAttribute("carritoItems", carrito);
        model.addAttribute("totalCarrito", carritoService.calcularTotal(carrito));
        model.addAttribute("totalUnidades", carritoService.contarUnidades(carrito));

        return "/carrito/listado";
    }

    // ============================
    // 2. AGREGAR ZAPATO
    // ============================
    @PostMapping("/carrito/agregar")
    public String agregar(@RequestParam("idZapato") Integer idZapato,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            List<Item> carrito = carritoService.obtenerCarrito(session);
            carritoService.agregarZapato(carrito, idZapato);
            carritoService.guardarCarrito(session, carrito);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Zapato agregado al carrito correctamente");

            return "redirect:/carrito/listado";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/zapato/listado";
        }
    }

    @PostMapping("/carrito/limpiar")
    public String limpiarCarrito(
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        carritoService.limpiarCarrito(session);
        redirectAttributes.addFlashAttribute("mensaje", "Carrito vaciado correctamente.");

        return "redirect:/carrito/listado";
    }

    // ============================
    // 3. ELIMINAR ITEM
    // ============================
    @PostMapping("/carrito/eliminar/{idZapato}")
    public String eliminarItem(@PathVariable("idZapato") Integer idZapato,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        List<Item> carrito = carritoService.obtenerCarrito(session);
        carritoService.eliminarItem(carrito, idZapato);
        carritoService.guardarCarrito(session, carrito);

        redirectAttributes.addFlashAttribute("mensaje", "Zapato eliminado del carrito.");
        return "redirect:/carrito/listado";
    }

    // ============================
    // 4. MODIFICAR CANTIDAD
    // ============================
    @GetMapping("/carrito/modificar/{idZapato}")
    public String modificar(@PathVariable("idZapato") Integer idZapato,
            HttpSession session,
            Model model) {

        List<Item> carrito = carritoService.obtenerCarrito(session);
        Item item = carritoService.buscarItem(carrito, idZapato);

        if (item == null) {
            return "redirect:/carrito/listado";
        }

        model.addAttribute("item", item);
        return "/carrito/modifica";
    }

    // ============================
    // 5. ACTUALIZAR CANTIDAD
    // ============================
    @PostMapping("/carrito/actualizar")
    public String actualizarCantidad(@RequestParam("zapato.idZapato") Integer idZapato,
            @RequestParam("cantidad") int cantidad,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            List<Item> carrito = carritoService.obtenerCarrito(session);
            carritoService.actualizarCantidad(carrito, idZapato, cantidad);
            carritoService.guardarCarrito(session, carrito);

            redirectAttributes.addFlashAttribute("mensaje", "Cantidad actualizada correctamente.");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/carrito/listado";
    }

    // ============================
    // 6. FACTURAR CARRITO
    // ============================
    @GetMapping("/carrito/facturar")
    public String facturarCarrito(HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            List<Item> carrito = carritoService.obtenerCarrito(session);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Usuario usuario = usuarioService.getUsuarioPorUsername(username)
                    .orElseThrow(() -> new RuntimeException("El usuario no fue encontrado en la base de datos"));

            // Procesar factura
            Factura factura = carritoService.procesarCompra(carrito, usuario);

            carritoService.limpiarCarrito(session);

            redirectAttributes.addFlashAttribute("idFactura", factura.getIdFactura());
            redirectAttributes.addFlashAttribute("mensaje",
                    "Compra realizada con éxito. Factura #" + factura.getIdFactura());

            return "redirect:/carrito/verFactura";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/carrito/listado";
        }
    }

    // ============================
    // 7. VER FACTURA
    // ============================
    @GetMapping("/carrito/verFactura")
    public String verFactura(@ModelAttribute("idFactura") Integer idFactura, Model model) {

        if (idFactura == null) {
            return "redirect:/";
        }

        Factura factura = facturaService.getFacturaConVentas(idFactura);

        model.addAttribute("factura", factura);
        return "/carrito/verFactura";
    }
}
