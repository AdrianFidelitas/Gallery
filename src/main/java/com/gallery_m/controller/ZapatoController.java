package com.gallery_m.controller;

import com.gallery_m.domain.Zapato;
import com.gallery_m.service.CategoriaService;
import com.gallery_m.service.MarcaService;
import com.gallery_m.service.ZapatoService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/zapato")
public class ZapatoController {

    private final ZapatoService zapatoService;

    //Agregamos este para la categoria, conectarlo para que sea seleccionable en el dropdown de agregar
    private final CategoriaService categoriaService;
    private final MarcaService marcaService;
    private final MessageSource messageSource;

    //Constructor
    public ZapatoController(ZapatoService zapatoService,
            CategoriaService categoriaService,
            MarcaService marcaService,
            MessageSource messageSource) {
        this.zapatoService = zapatoService;
        this.categoriaService = categoriaService;
        this.marcaService = marcaService;
        this.messageSource = messageSource;
    }

    //Metodo listados
    @GetMapping("/listado")
    public String listado(Model model) {
        var zapatos = zapatoService.getZapatos(false);
        model.addAttribute("zapatos", zapatos);
        model.addAttribute("totalZapatos", zapatos.size());

        var categorias = categoriaService.getCategorias(false);
        model.addAttribute("categorias", categorias);

        var marcas = marcaService.getMarcas();
        model.addAttribute("marcas", marcas);

        return "zapato/listado";
    }

    //Metodo para Guardar Zapato
    @PostMapping("/guardar")
    public String guardar(@Valid Zapato zapato,
            @RequestParam MultipartFile imagenFile,
            RedirectAttributes redirectAttributes) {
        zapatoService.save(zapato, imagenFile);
        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));
        return "redirect:/zapato/listado";
    }

    //Metodo para Eliminar
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idZapato, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        try {
            zapatoService.delete(idZapato);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = "zapato.error01";
        } catch (IllegalStateException e) {
            titulo = "error";
            detalle = "zapato.error02";
        } catch (Exception e) {
            titulo = "error";
            detalle = "zapato.error03";
        }
        redirectAttributes.addFlashAttribute(titulo, messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/zapato/listado";
    }

    //Metodo para modificar por idZapato
    @GetMapping("/modificar/{idZapato}")
    public String modificar(@PathVariable Integer idZapato,
            Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Zapato> zapatoOpt = zapatoService.getZapato(idZapato);
        if (zapatoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("zapato.error01", null, Locale.getDefault()));
            return "redirect:/zapato/listado";
        }
        model.addAttribute("zapato", zapatoOpt.get());

        var categorias = categoriaService.getCategorias(false);
        model.addAttribute("categorias", categorias);

        var marcas = marcaService.getMarcas();
        model.addAttribute("marcas", marcas);

        return "/zapato/modifica";
    }
}
