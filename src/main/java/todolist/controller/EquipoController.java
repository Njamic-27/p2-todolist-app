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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/equipos/nuevo")
    public String formNuevoEquipo(Model model) {
        comprobarUsuarioLogeado();
        anadirDatosNavbar(model);
        return "formNuevoEquipo";
    }

    @PostMapping("/equipos/nuevo")
    public String crearEquipo(String nombre, RedirectAttributes flash) {
        comprobarUsuarioLogeado();
        try {
            equipoService.crearEquipo(nombre);
            flash.addFlashAttribute("mensaje", "Equipo creado correctamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/equipos/nuevo";
        }
        return "redirect:/equipos";
    }

    @PostMapping("/equipos/{id}/addUser")
    public String addUserToTeam(@PathVariable Long id, String email, RedirectAttributes flash) {
        comprobarUsuarioLogeado();
        try {
            UsuarioData usuario = usuarioService.findByEmail(email);
            if (usuario == null) {
                flash.addFlashAttribute("error", "Usuario no encontrado");
            } else {
                equipoService.añadirUsuarioAEquipo(id, usuario.getId());
                flash.addFlashAttribute("mensaje", "Usuario añadido al equipo");
            }
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/equipos/" + id;
    }

    @PostMapping("/equipos/{id}/removeUser")
    public String removeUserFromTeam(@PathVariable Long id, Long userId, RedirectAttributes flash) {
        comprobarUsuarioLogeado();
        try {
            equipoService.quitarUsuarioDeEquipo(id, userId);
            flash.addFlashAttribute("mensaje", "Usuario eliminado del equipo");
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/equipos/" + id;
    }

    @GetMapping("/usuarios/{id}/cuenta")
    public String userAccount(@PathVariable Long id, Model model) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado == null || !idUsuarioLogeado.equals(id)) {
            throw new UsuarioNoLogeadoException();
        }

        anadirDatosNavbar(model);
        UsuarioData usuario = usuarioService.findById(id);
        model.addAttribute("usuario", usuario);

        List<EquipoData> misEquipos = equipoService.equiposUsuario(id);
        model.addAttribute("equipos", misEquipos);

        return "cuentaUsuario";
    }

    @PostMapping("/equipos/{id}/leave")
    public String leaveTeam(@PathVariable Long id, RedirectAttributes flash) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado == null) {
            throw new UsuarioNoLogeadoException();
        }

        try {
            equipoService.quitarUsuarioDeEquipo(id, idUsuarioLogeado);
            flash.addFlashAttribute("mensaje", "Te has salido del equipo correctamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios/" + idUsuarioLogeado + "/cuenta";
    }
}
