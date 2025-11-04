package com.gallery_m.controller;

import com.gallery_m.domain.Marca;
import com.gallery_m.service.MarcaService;
import jakarta.validation.Valid;
import java.util.Locale;
import java.util.Optional;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/marca")
public class MarcaController {

    private final MarcaService marcaService;
    private final MessageSource messageSource;

    //Constructor
    public MarcaController(MarcaService marcaService, MessageSource messageSource) {
        this.marcaService = marcaService;
        this.messageSource = messageSource;
    }

    //Metodo listado
    @GetMapping("/listado")
    public String listado(Model model) {
        var marcas = marcaService.getMarcas();
        model.addAttribute("marcas", marcas);
        model.addAttribute("totalMarcas", marcas.size());
        return "marca/listado";
    }

    //Metodo guardar
    @PostMapping("/guardar")
    public String guardar(@Valid Marca marca, RedirectAttributes redirectAttributes) {
        marcaService.save(marca);
        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));
        return "redirect:/marca/listado";
    }

    //Metodo Elimnar
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idMarca, RedirectAttributes redirectAttributes) {
        marcaService.delete(idMarca);
        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("mensaje.eliminado", null, Locale.getDefault()));
        return "redirect:/marca/listado";
    }
    
    //Metodo modificar por idMarca
    @GetMapping("/modificar/{idMarca}")
    public String modificar(@PathVariable Integer idMarca, Model model) {
        Optional<Marca> marcaOpt = marcaService.getMarca(idMarca);
        if (marcaOpt.isEmpty()) {
            return "redirect:/marca/listado";
        }
        model.addAttribute("marca", marcaOpt.get());
        return "/marca/modifica";
    }
}
