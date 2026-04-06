package todolist.controller;

import todolist.authentication.ManagerUserSession;
import todolist.dto.LoginData;
import todolist.dto.RegistroData;
import todolist.dto.UsuarioData;
import todolist.service.UsuarioService;
import todolist.service.UsuarioServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class LoginController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ManagerUserSession managerUserSession;

    @GetMapping("/")
    public String home(Model model) {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginData", new LoginData());
        return "formLogin";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute LoginData loginData, Model model, HttpSession session) {

        // Llamada al servicio para comprobar si el login es correcto
        UsuarioService.LoginStatus loginStatus = usuarioService.login(loginData.geteMail(), loginData.getPassword());

        if (loginStatus == UsuarioService.LoginStatus.LOGIN_OK) {
            UsuarioData usuario = usuarioService.findByEmail(loginData.geteMail());

            managerUserSession.logearUsuario(usuario.getId());

            // Si el usuario es admin, redirigir a la lista de usuarios
            if (usuario.getIsAdmin() != null && usuario.getIsAdmin()) {
                return "redirect:/registered";
            }

            return "redirect:/usuarios/" + usuario.getId() + "/tareas";
        } else if (loginStatus == UsuarioService.LoginStatus.USER_NOT_FOUND) {
            model.addAttribute("error", "No existe usuario");
            return "formLogin";
        } else if (loginStatus == UsuarioService.LoginStatus.ERROR_PASSWORD) {
            model.addAttribute("error", "Contraseña incorrecta");
            return "formLogin";
        } else if (loginStatus == UsuarioService.LoginStatus.USER_BLOCKED) {
            model.addAttribute("error", "Usuario deshabilitado. Contacte con el administrador");
            return "formLogin";
        }
        return "formLogin";
    }

    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("registroData", new RegistroData());
        // Solo mostrar checkbox de admin si no existe admin aún
        model.addAttribute("canBeAdmin", !usuarioService.existsAdmin());
        return "formRegistro";
    }

   @PostMapping("/registro")
   public String registroSubmit(@Valid RegistroData registroData, BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("canBeAdmin", !usuarioService.existsAdmin());
            return "formRegistro";
        }

        if (usuarioService.findByEmail(registroData.getEmail()) != null) {
            model.addAttribute("registroData", registroData);
            model.addAttribute("error", "El usuario " + registroData.getEmail() + " ya existe");
            model.addAttribute("canBeAdmin", !usuarioService.existsAdmin());
            return "formRegistro";
        }

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail(registroData.getEmail());
        usuario.setPassword(registroData.getPassword());
        usuario.setFechaNacimiento(registroData.getFechaNacimiento());
        usuario.setNombre(registroData.getNombre());

        try {
            // Determinar si se registra como admin
            boolean isAdmin = registroData.getIsAdmin() != null && registroData.getIsAdmin() && !usuarioService.existsAdmin();
            usuarioService.registrar(usuario, isAdmin);
        } catch (UsuarioServiceException e) {
            model.addAttribute("registroData", registroData);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("canBeAdmin", !usuarioService.existsAdmin());
            return "formRegistro";
        }
        
        return "redirect:/login";
   }

   @GetMapping("/logout")
   public String logout(HttpSession session) {
        managerUserSession.logout();
        return "redirect:/login";
   }
}
