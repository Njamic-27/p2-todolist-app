package todolist.controller;

import todolist.authentication.ManagerUserSession;
import todolist.controller.exception.PermisosInsuficientesException;
import todolist.controller.exception.UsuarioNoLogeadoException;
import org.springframework.web.bind.annotation.PathVariable;
import todolist.dto.UsuarioData;
import todolist.service.UsuarioService;
import todolist.service.UsuarioServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ManagerUserSession managerUserSession;

    private void comprobarAdministradorLogeado() {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado == null) {
            throw new UsuarioNoLogeadoException();
        }
        if (!usuarioService.esAdministrador(idUsuarioLogeado)) {
            throw new PermisosInsuficientesException();
        }
    }

    @GetMapping("/registered")
    public String registeredUsers(Model model) {
        comprobarAdministradorLogeado();
        List<UsuarioData> usuarios = usuarioService.findAllUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "listaUsuarios";
    }

    @GetMapping("/registered/{id}")
    public String registeredUserDescription(@PathVariable Long id, Model model) {
        comprobarAdministradorLogeado();
        UsuarioData usuario = usuarioService.findById(id);
        model.addAttribute("usuario", usuario);
        return "descripcionUsuario";
    }

    @PostMapping("/registered/{id}/block")
    public String bloquearUsuario(@PathVariable Long id, RedirectAttributes flash) {
        comprobarAdministradorLogeado();
        try {
            usuarioService.bloquearUsuario(id);
            flash.addFlashAttribute("mensaje", "Usuario bloqueado correctamente");
        } catch (UsuarioServiceException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/registered";
    }

    @PostMapping("/registered/{id}/unblock")
    public String desbloquearUsuario(@PathVariable Long id, RedirectAttributes flash) {
        comprobarAdministradorLogeado();
        try {
            usuarioService.desbloquearUsuario(id);
            flash.addFlashAttribute("mensaje", "Usuario desbloqueado correctamente");
        } catch (UsuarioServiceException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/registered";
    }
}