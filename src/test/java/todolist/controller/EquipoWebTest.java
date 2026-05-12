package todolist.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import todolist.authentication.ManagerUserSession;
import todolist.dto.EquipoData;
import todolist.dto.UsuarioData;
import todolist.service.EquipoService;
import todolist.service.UsuarioService;

import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class EquipoWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EquipoService equipoService;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private ManagerUserSession managerUserSession;

    @Test
    public void listadoEquiposMuestraLosEquiposYLaNavbar() throws Exception {
        // GIVEN
        whenLoggedUserIsPresent();

        UsuarioData usuario = new UsuarioData();
        usuario.setId(10L);
        usuario.setNombre("Ana García");
        usuario.setEmail("ana@umh.es");
        org.mockito.Mockito.when(usuarioService.findById(10L)).thenReturn(usuario);

        EquipoData equipo1 = new EquipoData();
        equipo1.setId(1L);
        equipo1.setNombre("Backend");

        EquipoData equipo2 = new EquipoData();
        equipo2.setId(2L);
        equipo2.setNombre("Frontend");

        org.mockito.Mockito.when(equipoService.findAllOrdenadoPorNombre())
                .thenReturn(Arrays.asList(equipo1, equipo2));

        // WHEN, THEN
        this.mockMvc.perform(get("/equipos"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Teams")))
                .andExpect(content().string(containsString("Backend")))
                .andExpect(content().string(containsString("Frontend")))
                .andExpect(content().string(containsString("ToDoList")))
                .andExpect(content().string(containsString("Tasks")));
    }

    @Test
    public void listadoEquiposDevuelve401SiNoHayUsuarioLogeado() throws Exception {
        // GIVEN
        org.mockito.Mockito.when(managerUserSession.usuarioLogeado()).thenReturn(null);

        // WHEN, THEN
        this.mockMvc.perform(get("/equipos"))
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason(containsString("Usuario no autorizado")));
    }

    @Test
    public void descripcionEquipoMuestraEquipoYMiembros() throws Exception {
        // GIVEN
        whenLoggedUserIsPresent();

        UsuarioData usuario = new UsuarioData();
        usuario.setId(10L);
        usuario.setNombre("Ana García");
        usuario.setEmail("ana@umh.es");
        org.mockito.Mockito.when(usuarioService.findById(10L)).thenReturn(usuario);

        EquipoData equipo = new EquipoData();
        equipo.setId(1L);
        equipo.setNombre("Backend");

        UsuarioData miembro = new UsuarioData();
        miembro.setId(2L);
        miembro.setEmail("member@umh.es");

        org.mockito.Mockito.when(equipoService.recuperarEquipo(1L)).thenReturn(equipo);
        org.mockito.Mockito.when(equipoService.usuariosEquipo(1L)).thenReturn(java.util.Arrays.asList(miembro));

        // WHEN, THEN
        this.mockMvc.perform(get("/equipos/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Backend")))
                .andExpect(content().string(containsString("member@umh.es")))
                .andExpect(content().string(containsString("ToDoList")));
    }

    @Test
    public void descripcionEquipoDevuelve401SiNoHayUsuarioLogeado() throws Exception {
        // GIVEN
        org.mockito.Mockito.when(managerUserSession.usuarioLogeado()).thenReturn(null);

        // WHEN, THEN
        this.mockMvc.perform(get("/equipos/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason(containsString("Usuario no autorizado")));
    }

    @Test
    public void crearEquipoPostCreaEquipoYRedirige() throws Exception {
        whenLoggedUserIsPresent();
        EquipoData nuevo = new EquipoData();
        nuevo.setId(5L);
        nuevo.setNombre("Infra");
        when(equipoService.crearEquipo("Infra")).thenReturn(nuevo);

        this.mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/equipos/nuevo")
                        .param("nombre", "Infra"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos"));
    }

    @Test
    public void addUserEndpointAddsUserAndRedirects() throws Exception {
        whenLoggedUserIsPresent();
        UsuarioData usuario = new UsuarioData();
        usuario.setId(2L);
        usuario.setEmail("member@umh.es");
        when(usuarioService.findByEmail("member@umh.es")).thenReturn(usuario);
        doNothing().when(equipoService).añadirUsuarioAEquipo(1L, 2L);

        this.mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/equipos/1/addUser")
                        .param("email", "member@umh.es"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos/1"));
    }

    @Test
    public void removeUserEndpointRemovesUserAndRedirects() throws Exception {
        whenLoggedUserIsPresent();
        doNothing().when(equipoService).quitarUsuarioDeEquipo(1L, 2L);

        this.mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/equipos/1/removeUser")
                        .param("userId", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos/1"));
    }

    @Test
    public void userAccountPageMuestaPerfil() throws Exception {
        whenLoggedUserIsPresent();

        UsuarioData usuario = new UsuarioData();
        usuario.setId(10L);
        usuario.setNombre("Ana García");
        usuario.setEmail("ana@umh.es");
        usuario.setIsAdmin(false);
        when(usuarioService.findById(10L)).thenReturn(usuario);

        EquipoData equipo = new EquipoData();
        equipo.setId(1L);
        equipo.setNombre("Backend");
        when(equipoService.equiposUsuario(10L)).thenReturn(java.util.Arrays.asList(equipo));

        this.mockMvc.perform(get("/usuarios/10/cuenta"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("My Account")))
                .andExpect(content().string(containsString("ana@umh.es")))
                .andExpect(content().string(containsString("Ana García")))
                .andExpect(content().string(containsString("Backend")))
                .andExpect(content().string(containsString("Leave")));
    }

    @Test
    public void userAccountPageDevuelve401SiNoEsElUsuarioLogeado() throws Exception {
        whenLoggedUserIsPresent();

        this.mockMvc.perform(get("/usuarios/999/cuenta"))
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason(containsString("Usuario no autorizado")));
    }

    @Test
    public void leaveTeamEndpointRemovesUserAndRedireccionaCuenta() throws Exception {
        whenLoggedUserIsPresent();
        doNothing().when(equipoService).quitarUsuarioDeEquipo(1L, 10L);

        this.mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/equipos/1/leave"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios/10/cuenta"));
    }

    private void whenLoggedUserIsPresent() {
        org.mockito.Mockito.when(managerUserSession.usuarioLogeado()).thenReturn(10L);
    }
}
