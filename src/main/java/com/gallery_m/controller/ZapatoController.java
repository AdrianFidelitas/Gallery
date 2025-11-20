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
    private final CategoriaService categoriaService;
    private final MarcaService marcaService;
    private final MessageSource messageSource;

    public ZapatoController(ZapatoService zapatoService,
            CategoriaService categoriaService,
            MarcaService marcaService,
            MessageSource messageSource) {
        this.zapatoService = zapatoService;
        this.categoriaService = categoriaService;
        this.marcaService = marcaService;
        this.messageSource = messageSource;
    }

    @GetMapping("/categoria/{idCategoria}")
    public String listarPorCategoria(@PathVariable Long idCategoria, Model model) {
        try {
            System.out.println("=== Buscando zapatos de categoría: " + idCategoria + " ===");
            
            // Filtrar zapatos por categoría CON VALIDACIÓN DE NULL
            var zapatos = zapatoService.getZapatos(false)
                    .stream()
                    .filter(z -> z.getCategoria() != null)  // Evitar null pointer
                    .filter(z -> z.getCategoria().getIdCategoria() != null)
                    .filter(z -> z.getCategoria().getIdCategoria().equals(idCategoria))
                    .toList();
            
            System.out.println("Zapatos encontrados: " + zapatos.size());
            
            model.addAttribute("zapatos", zapatos);
            model.addAttribute("totalZapatos", zapatos.size());
            model.addAttribute("categoriaSeleccionada", idCategoria);

            var categorias = categoriaService.getCategorias(false);
            model.addAttribute("categorias", categorias);

            var marcas = marcaService.getMarcas();
            model.addAttribute("marcas", marcas);

            return "zapato/listado";
            
        } catch (Exception e) {
            System.err.println("ERROR en /zapato/categoria/" + idCategoria);
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar zapatos: " + e.getMessage());
            return "redirect:/zapato/listado";
        }
    }

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

    @PostMapping("/guardar")
    public String guardar(@Valid Zapato zapato,
            @RequestParam MultipartFile imagenFile,
            RedirectAttributes redirectAttributes) {
        zapatoService.save(zapato, imagenFile);
        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));
        return "redirect:/zapato/listado";
    }

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
