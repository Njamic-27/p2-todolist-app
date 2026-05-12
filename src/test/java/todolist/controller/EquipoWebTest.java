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

    private void whenLoggedUserIsPresent() {
        org.mockito.Mockito.when(managerUserSession.usuarioLogeado()).thenReturn(10L);
    }
}
