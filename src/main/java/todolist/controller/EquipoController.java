package todolist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import todolist.authentication.ManagerUserSession;
import todolist.controller.exception.UsuarioNoLogeadoException;
import todolist.dto.EquipoData;
import todolist.dto.UsuarioData;
import todolist.service.EquipoService;
import todolist.service.UsuarioService;

import java.util.List;

@Controller
public class EquipoController {

    @Autowired
    private EquipoService equipoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ManagerUserSession managerUserSession;

    private void comprobarUsuarioLogeado() {
        if (managerUserSession.usuarioLogeado() == null) {
            throw new UsuarioNoLogeadoException();
        }
    }

    private void anadirDatosNavbar(Model model) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        model.addAttribute("usuarioId", idUsuarioLogeado);

        UsuarioData usuarioLogeado = usuarioService.findById(idUsuarioLogeado);
        if (usuarioLogeado != null && usuarioLogeado.getNombre() != null) {
            model.addAttribute("usuarioNombre", usuarioLogeado.getNombre());
        } else if (usuarioLogeado != null && usuarioLogeado.getEmail() != null) {
            model.addAttribute("usuarioNombre", usuarioLogeado.getEmail());
        } else {
            model.addAttribute("usuarioNombre", "Usuario");
        }
    }

    @GetMapping("/equipos")
    public String listadoEquipos(Model model) {
        comprobarUsuarioLogeado();
        anadirDatosNavbar(model);

        List<EquipoData> equipos = equipoService.findAllOrdenadoPorNombre();
        model.addAttribute("equipos", equipos);
        return "listaEquipos";
    }

    @GetMapping("/equipos/{id}")
    public String descripcionEquipo(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        comprobarUsuarioLogeado();
        anadirDatosNavbar(model);

        EquipoData equipo = equipoService.recuperarEquipo(id);
        model.addAttribute("equipo", equipo);

        java.util.List<UsuarioData> usuarios = equipoService.usuariosEquipo(id);
        model.addAttribute("usuarios", usuarios);

        return "descripcionEquipo";
    }
}
