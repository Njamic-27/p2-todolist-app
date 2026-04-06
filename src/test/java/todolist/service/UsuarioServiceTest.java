package todolist.service;

import todolist.dto.UsuarioData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    // Método para inicializar los datos de prueba en la BD
    // Devuelve el identificador del usuario de la BD
    Long addUsuarioBD() {
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("richard@umh.es");
        usuario.setNombre("Richard Stallman");
        usuario.setPassword("1234");
        UsuarioData nuevoUsuario = usuarioService.registrar(usuario);
        return nuevoUsuario.getId();
    }

    @Test
    public void servicioLoginUsuario() {
        // GIVEN
        // Un usuario en la BD

        addUsuarioBD();

        // WHEN
        // intentamos logear un usuario y contraseña correctos
        UsuarioService.LoginStatus loginStatus1 = usuarioService.login("richard@umh.es", "1234");

        // intentamos logear un usuario correcto, con una contraseña incorrecta
        UsuarioService.LoginStatus loginStatus2 = usuarioService.login("richard@umh.es", "0000");

        // intentamos logear un usuario que no existe,
        UsuarioService.LoginStatus loginStatus3 = usuarioService.login("ricardo.perez@gmail.com", "12345678");

        // THEN

        // el valor devuelto por el primer login es LOGIN_OK,
        assertThat(loginStatus1).isEqualTo(UsuarioService.LoginStatus.LOGIN_OK);

        // el valor devuelto por el segundo login es ERROR_PASSWORD,
        assertThat(loginStatus2).isEqualTo(UsuarioService.LoginStatus.ERROR_PASSWORD);

        // y el valor devuelto por el tercer login es USER_NOT_FOUND.
        assertThat(loginStatus3).isEqualTo(UsuarioService.LoginStatus.USER_NOT_FOUND);
    }

    @Test
    public void servicioRegistroUsuario() {
        // WHEN
        // Registramos un usuario con un e-mail no existente en la base de datos,

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("usuario.prueba2@gmail.com");
        usuario.setPassword("12345678");

        usuarioService.registrar(usuario);

        // THEN
        // el usuario se añade correctamente al sistema.

        UsuarioData usuarioBaseDatos = usuarioService.findByEmail("usuario.prueba2@gmail.com");
        assertThat(usuarioBaseDatos).isNotNull();
        assertThat(usuarioBaseDatos.getEmail()).isEqualTo("usuario.prueba2@gmail.com");
    }

    @Test
    public void servicioRegistroUsuarioExcepcionConNullPassword() {
        // WHEN, THEN
        // Si intentamos registrar un usuario con un password null,
        // se produce una excepción de tipo UsuarioServiceException

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("usuario.prueba@gmail.com");

        Assertions.assertThrows(UsuarioServiceException.class, () -> {
            usuarioService.registrar(usuario);
        });
    }


    @Test
    public void servicioRegistroUsuarioExcepcionConEmailRepetido() {
        // GIVEN
        // Un usuario en la BD

        addUsuarioBD();

        // THEN
        // Si registramos un usuario con un e-mail ya existente en la base de datos,
        // , se produce una excepción de tipo UsuarioServiceException

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("richard@umh.es");
        usuario.setPassword("12345678");

        Assertions.assertThrows(UsuarioServiceException.class, () -> {
            usuarioService.registrar(usuario);
        });
    }

    @Test
    public void servicioRegistroUsuarioDevuelveUsuarioConId() {

        // WHEN
        // Si registramos en el sistema un usuario con un e-mail no existente en la base de datos,
        // y un password no nulo,

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("usuario.prueba@gmail.com");
        usuario.setPassword("12345678");

        UsuarioData usuarioNuevo = usuarioService.registrar(usuario);

        // THEN
        // se actualiza el identificador del usuario

        assertThat(usuarioNuevo.getId()).isNotNull();

        // con el identificador que se ha guardado en la BD.

        UsuarioData usuarioBD = usuarioService.findById(usuarioNuevo.getId());
        assertThat(usuarioBD).isEqualTo(usuarioNuevo);
    }

    @Test
    public void servicioConsultaUsuarioDevuelveUsuario() {
        // GIVEN
        // Un usuario en la BD

        Long usuarioId = addUsuarioBD();

        // WHEN
        // recuperamos un usuario usando su e-mail,

        UsuarioData usuario = usuarioService.findByEmail("richard@umh.es");

        // THEN
        // el usuario obtenido es el correcto.

        assertThat(usuario.getId()).isEqualTo(usuarioId);
        assertThat(usuario.getEmail()).isEqualTo("richard@umh.es");
        assertThat(usuario.getNombre()).isEqualTo("Richard Stallman");
    }

    @Test
    public void servicioConsultaListaUsuariosDevuelveUsuariosRegistrados() {
        // GIVEN
        addUsuarioBD();

        UsuarioData usuario2 = new UsuarioData();
        usuario2.setEmail("ada@umh.es");
        usuario2.setNombre("Ada Lovelace");
        usuario2.setPassword("5678");
        usuarioService.registrar(usuario2);

        // WHEN
        java.util.List<UsuarioData> usuarios = usuarioService.findAllUsuarios();

        // THEN
        assertThat(usuarios).hasSize(2);
        assertThat(usuarios).extracting(UsuarioData::getEmail)
                .contains("richard@umh.es", "ada@umh.es");
    }

    @Test
    public void servicioConsultaDescripcionUsuarioDevuelveDatosSinPasswordVerificadoPorId() {
        // GIVEN
        Long usuarioId = addUsuarioBD();

        // WHEN
        UsuarioData usuario = usuarioService.findById(usuarioId);

        // THEN
        assertThat(usuario).isNotNull();
        assertThat(usuario.getId()).isEqualTo(usuarioId);
        assertThat(usuario.getEmail()).isEqualTo("richard@umh.es");
        assertThat(usuario.getNombre()).isEqualTo("Richard Stallman");
    }

    @Test
    public void servicioRegistroAdministrador() {
        // WHEN
        // Registramos un usuario como administrador cuando no existe admin aún

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("admin@umh.es");
        usuario.setPassword("adminpass");
        usuario.setNombre("Admin User");

        UsuarioData usuarioNuevo = usuarioService.registrar(usuario, true);

        // THEN
        // el usuario se registra como administrador
        assertThat(usuarioNuevo.getIsAdmin()).isTrue();
        
        // y se puede verificar que existe un admin en el sistema
        assertThat(usuarioService.existsAdmin()).isTrue();
        
        // y podemos obtener al usuario administrador
        UsuarioData admin = usuarioService.getAdmin();
        assertThat(admin).isNotNull();
        assertThat(admin.getEmail()).isEqualTo("admin@umh.es");
    }

    @Test
    public void servicioExistenceAdminRetornaFalsoCuandoNoHayAdmin() {
        // WHEN
        // No hay ningún administrador registrado

        // THEN
        // existsAdmin retorna false
        assertThat(usuarioService.existsAdmin()).isFalse();
    }

    @Test
    public void servicioRegistroAdministradorExcepcionSiYaExisteAdmin() {
        // GIVEN
        // Ya existe un administrador en el sistema
        UsuarioData adminUser = new UsuarioData();
        adminUser.setEmail("admin1@umh.es");
        adminUser.setPassword("pass123");
        usuarioService.registrar(adminUser, true);

        // WHEN, THEN
        // Si intentamos registrar otro usuario como administrador,
        // se produce una excepción
        UsuarioData usuario2 = new UsuarioData();
        usuario2.setEmail("admin2@umh.es");
        usuario2.setPassword("pass456");

        Assertions.assertThrows(UsuarioServiceException.class, () -> {
            usuarioService.registrar(usuario2, true);
        });
    }

    @Test
    public void servicioRegistroUsuarioNoAdminFuncionaAunqueExistaAdmin() {
        // GIVEN
        // Ya existe un administrador en el sistema
        UsuarioData adminUser = new UsuarioData();
        adminUser.setEmail("admin@umh.es");
        adminUser.setPassword("pass123");
        usuarioService.registrar(adminUser, true);

        // WHEN
        // Registramos un usuario normal cuando ya existe admin
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("usuario@umh.es");
        usuario.setPassword("userpass");

        UsuarioData usuarioNuevo = usuarioService.registrar(usuario, false);

        // THEN
        // el usuario se registra correctamente como no-admin
        assertThat(usuarioNuevo.getIsAdmin()).isFalse();
        assertThat(usuarioNuevo.getEmail()).isEqualTo("usuario@umh.es");
    }

    @Test
    public void servicioGetAdminRetornaNull() {
        // WHEN
        // No hay ningún administrador registrado

        // THEN
        // getAdmin retorna null
        assertThat(usuarioService.getAdmin()).isNull();
    }

    @Test
    public void servicioEsAdministradorRetornaTrueParaAdmin() {
        // GIVEN
        UsuarioData adminUser = new UsuarioData();
        adminUser.setEmail("admin@umh.es");
        adminUser.setPassword("pass123");
        UsuarioData adminGuardado = usuarioService.registrar(adminUser, true);

        // WHEN
        boolean esAdmin = usuarioService.esAdministrador(adminGuardado.getId());

        // THEN
        assertThat(esAdmin).isTrue();
    }

    @Test
    public void servicioEsAdministradorRetornaFalseParaUsuarioNormal() {
        // GIVEN
        Long usuarioId = addUsuarioBD();

        // WHEN
        boolean esAdmin = usuarioService.esAdministrador(usuarioId);

        // THEN
        assertThat(esAdmin).isFalse();
    }

    @Test
    public void servicioEsAdministradorRetornaFalseSiUsuarioNoExiste() {
        // WHEN
        boolean esAdmin = usuarioService.esAdministrador(999L);

        // THEN
        assertThat(esAdmin).isFalse();
    }

    @Test
    public void servicioLoginUsuarioBloqueadoDevuelveUserBlocked() {
        // GIVEN
        Long usuarioId = addUsuarioBD();
        usuarioService.bloquearUsuario(usuarioId);

        // WHEN
        UsuarioService.LoginStatus loginStatus = usuarioService.login("richard@umh.es", "1234");

        // THEN
        assertThat(loginStatus).isEqualTo(UsuarioService.LoginStatus.USER_BLOCKED);
    }

    @Test
    public void servicioBloquearYDesbloquearUsuario() {
        // GIVEN
        Long usuarioId = addUsuarioBD();

        // WHEN
        usuarioService.bloquearUsuario(usuarioId);
        UsuarioData bloqueado = usuarioService.findById(usuarioId);

        // THEN
        assertThat(bloqueado.getIsBlocked()).isTrue();

        // WHEN
        usuarioService.desbloquearUsuario(usuarioId);
        UsuarioData habilitado = usuarioService.findById(usuarioId);

        // THEN
        assertThat(habilitado.getIsBlocked()).isFalse();
    }

    @Test
    public void servicioNoPermiteBloquearAdministrador() {
        // GIVEN
        UsuarioData adminUser = new UsuarioData();
        adminUser.setEmail("admin@umh.es");
        adminUser.setPassword("pass123");
        UsuarioData admin = usuarioService.registrar(adminUser, true);

        // WHEN, THEN
        Assertions.assertThrows(UsuarioServiceException.class, () -> {
            usuarioService.bloquearUsuario(admin.getId());
        });
    }

    @Test
    public void servicioBloquearUsuarioNoExistenteLanzaExcepcion() {
        // WHEN, THEN
        Assertions.assertThrows(UsuarioServiceException.class, () -> {
            usuarioService.bloquearUsuario(999L);
        });
    }
}