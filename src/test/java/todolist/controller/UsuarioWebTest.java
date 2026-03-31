package todolist.controller;

import todolist.dto.UsuarioData;
import todolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
//
// A diferencia de los tests web de tarea, donde usábamos los datos
// de prueba de la base de datos, aquí vamos a practicar otro enfoque:
// moquear el usuarioService.
public class UsuarioWebTest {

    @Autowired
    private MockMvc mockMvc;

    // Moqueamos el usuarioService.
    // En los tests deberemos proporcionar el valor devuelto por las llamadas
    // a los métodos de usuarioService que se van a ejecutar cuando se realicen
    // las peticiones a los endpoint.
    @MockBean
    private UsuarioService usuarioService;

    @Test
    public void servicioLoginUsuarioOK() throws Exception {
        // GIVEN
        // Moqueamos la llamada a usuarioService.login para que
        // devuelva un LOGIN_OK y la llamada a usuarioServicie.findByEmail
        // para que devuelva un usuario determinado.

        UsuarioData anaGarcia = new UsuarioData();
        anaGarcia.setNombre("Ana García");
        anaGarcia.setId(1L);

        when(usuarioService.login("ana.garcia@gmail.com", "12345678"))
                .thenReturn(UsuarioService.LoginStatus.LOGIN_OK);
        when(usuarioService.findByEmail("ana.garcia@gmail.com"))
                .thenReturn(anaGarcia);

        // WHEN, THEN
        // Realizamos una petición POST al login pasando los datos
        // esperados en el mock, la petición devolverá una redirección a la
        // URL con las tareas del usuario

        this.mockMvc.perform(post("/login")
                        .param("eMail", "ana.garcia@gmail.com")
                        .param("password", "12345678"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios/1/tareas"));
    }

    @Test
    public void servicioLoginUsuarioNotFound() throws Exception {
        // GIVEN
        // Moqueamos el método usuarioService.login para que devuelva
        // USER_NOT_FOUND
        when(usuarioService.login("pepito.perez@gmail.com", "12345678"))
                .thenReturn(UsuarioService.LoginStatus.USER_NOT_FOUND);

        // WHEN, THEN
        // Realizamos una petición POST con los datos del usuario mockeado y
        // se debe devolver una página que contenga el mensaja "No existe usuario"
        this.mockMvc.perform(post("/login")
                        .param("eMail","pepito.perez@gmail.com")
                        .param("password","12345678"))
                .andExpect(content().string(containsString("No existe usuario")));
    }

    @Test
    public void servicioLoginUsuarioErrorPassword() throws Exception {
        // GIVEN
        // Moqueamos el método usuarioService.login para que devuelva
        // ERROR_PASSWORD
        when(usuarioService.login("ana.garcia@gmail.com", "000"))
                .thenReturn(UsuarioService.LoginStatus.ERROR_PASSWORD);

        // WHEN, THEN
        // Realizamos una petición POST con los datos del usuario mockeado y
        // se debe devolver una página que contenga el mensaja "Contraseña incorrecta"
        this.mockMvc.perform(post("/login")
                        .param("eMail","ana.garcia@gmail.com")
                        .param("password","000"))
                .andExpect(content().string(containsString("Contraseña incorrecta")));
    }

    @Test
    public void paginaUsuariosRegistradosMuestraListaUsuarios() throws Exception {
        // GIVEN
        UsuarioData usuario1 = new UsuarioData();
        usuario1.setId(1L);
        usuario1.setEmail("richard@umh.es");

        UsuarioData usuario2 = new UsuarioData();
        usuario2.setId(2L);
        usuario2.setEmail("ada@umh.es");

        when(usuarioService.findAllUsuarios())
                .thenReturn(Arrays.asList(usuario1, usuario2));

        // WHEN, THEN
        this.mockMvc.perform(get("/registered"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Registered users")))
                .andExpect(content().string(containsString("richard@umh.es")))
                .andExpect(content().string(containsString("ada@umh.es")));
    }

    @Test
    public void paginaDescripcionUsuarioMuestraDatosUsuarioSinPassword() throws Exception {
        // GIVEN
        UsuarioData usuario = new UsuarioData();
        usuario.setId(1L);
        usuario.setEmail("richard@umh.es");
        usuario.setNombre("Richard Stallman");

        when(usuarioService.findById(1L)).thenReturn(usuario);

        // WHEN, THEN
        this.mockMvc.perform(get("/registered/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("User description")))
                .andExpect(content().string(containsString("richard@umh.es")))
                .andExpect(content().string(containsString("Richard Stallman")))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("1234"))));
    }

    @Test
    public void paginaUsuariosRegistradosContieneEnlaceADescripcion() throws Exception {
        // GIVEN
        UsuarioData usuario1 = new UsuarioData();
        usuario1.setId(1L);
        usuario1.setEmail("richard@umh.es");

        when(usuarioService.findAllUsuarios())
                .thenReturn(java.util.Arrays.asList(usuario1));

        // WHEN, THEN
        this.mockMvc.perform(get("/registered"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/registered/1")))
                .andExpect(content().string(containsString("Description")));
    }
}
