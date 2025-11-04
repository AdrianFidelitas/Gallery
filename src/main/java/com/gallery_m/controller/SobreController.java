package com.gallery_m.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author user
 */
@Controller
public class SobreController {
    
    @GetMapping("/sobre")
    public String sobreNosotros(Model model) {
        // Agregar datos para la vista
        model.addAttribute("tituloPagina", "Sobre Nosotros - Gallery Brands");
        model.addAttribute("paginaActiva", "sobre");
        
        // Información de la empresa
        model.addAttribute("nombreEmpresa", "Gallery Brands");
        model.addAttribute("slogan", "Tu estilo, nuestro calzado");
        model.addAttribute("descripcion", "Somos una tienda especializada en calzado de alta calidad para todos los estilos y ocasiones.");
        model.addAttribute("anioFundacion", "2024");
        
        // Misión, Visión, Valores
        model.addAttribute("mision", "Ofrecer calzado de la más alta calidad que combine estilo, comodidad y durabilidad, satisfaciendo las necesidades de nuestros clientes más exigentes.");
        model.addAttribute("vision", "Ser la tienda de calzado líder en la región, reconocida por nuestra excelencia en servicio al cliente y variedad de productos.");
        model.addAttribute("valores", new String[]{
            "Calidad", "Innovación", "Servicio al cliente", "Sostenibilidad", "Pasion por el calzado"
        });
        
        return "sobre/sobre";
    }
    
    @GetMapping("/contacto")
    public String contacto(Model model) {
        model.addAttribute("tituloPagina", "Contacto - Gallery Brands");
        model.addAttribute("paginaActiva", "contacto");
        
        // Información de contacto
        model.addAttribute("telefono", "+506 2222-5555");
        model.addAttribute("email", "info@gallerybrands.com");
        model.addAttribute("direccion", "San José, Costa Rica");
        model.addAttribute("horario", "Lunes a Viernes: 9:00 AM - 6:00 PM");
        
        return "sobre/contacto";
    }
    
    @GetMapping("/terminos")
    public String terminosCondiciones(Model model) {
        model.addAttribute("tituloPagina", "Términos y Condiciones - Gallery Brands");
        model.addAttribute("paginaActiva", "terminos");
        return "sobre/terminos";
    }
    
    @GetMapping("/privacidad")
    public String politicaPrivacidad(Model model) {
        model.addAttribute("tituloPagina", "Política de Privacidad - Gallery Brands");
        model.addAttribute("paginaActiva", "privacidad");
        return "sobre/privacidad";
    }
}