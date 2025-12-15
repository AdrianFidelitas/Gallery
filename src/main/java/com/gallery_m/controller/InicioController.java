package com.gallery_m.controller;

import com.gallery_m.service.ZapatoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author Jafet
 */
@Controller
public class InicioController {

    @Autowired
    private ZapatoService zapatoService;

    @GetMapping({"/", "/Inicio"})
    public String inicio(Model model) {
        // Datos generales de la página
        model.addAttribute("tituloPagina", "Inicio - Gallery Brands");
        model.addAttribute("paginaActiva", "inicio");
        model.addAttribute("nombreEmpresa", "Gallery Brands");

        // Banner principal
        model.addAttribute("bannerTitulo", "GALLERY BRANDS");
        model.addAttribute("bannerBoton", "Comprar Ahora");
        model.addAttribute("bannerImagenCentro",
                "https://firebasestorage.googleapis.com/v0/b/proyectowebpatrones-7562d.firebasestorage.app/o/Imagenes%2FChicagoSign12_1024x1024.webp?alt=media&token=61de6be0-a517-4909-85e7-979bce8ccaff");
        model.addAttribute("bannerImagenIzquierda",
                "https://firebasestorage.googleapis.com/v0/b/tu_proyecto.appspot.com/o/banner-left.jpg?alt=media");
        model.addAttribute("bannerImagenDerecha",
                "https://firebasestorage.googleapis.com/v0/b/tu_proyecto.appspot.com/o/banner-right.jpg?alt=media");

        // Productos recomendados (ejemplo)
        model.addAttribute("productos", zapatoService.getRecomendados());

        return "inicio/inicio";
    }
}
