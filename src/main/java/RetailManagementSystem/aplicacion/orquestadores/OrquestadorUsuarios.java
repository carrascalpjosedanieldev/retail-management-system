package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.seguridad.*;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOUsuario;
import RetailManagementSystem.aplicacion.servicios.ServicioUsuario;
import RetailManagementSystem.dominio.entidades.seguridad.Rol;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class OrquestadorUsuarios {

    //ATRIBUTOS:

    private final ServicioUsuario servicioUsuario;

    private final EnsambladorDTOUsuario ensambladorDTOUsuario;

    //CONSTRUCTOR:

    public OrquestadorUsuarios(ServicioUsuario servicioUsuario, EnsambladorDTOUsuario ensambladorDTOUsuario) {
        this.servicioUsuario = servicioUsuario;
        this.ensambladorDTOUsuario = ensambladorDTOUsuario;
    }

    //MÉTODOS:

    public List<UsuarioDTOBasico> obtenerTodosLosUsuarios(){
        return this.ensambladorDTOUsuario.ensamblarDetalleUsuarios(
                this.servicioUsuario.obtenerTodosLosUsuarios()
        );
    }

    public ResultadoRegistroDTO registrarUsuarioYObtenerContrasenaTemporal(
            UsuarioDTOCompleto usuario, String nombre, String apellido, String email, boolean activo
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.REGISTRAR_USUARIOS);
        String caracteresPermitidos = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        SecureRandom random = new SecureRandom();
        StringBuilder claveTemporal = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            claveTemporal.append(caracteresPermitidos.charAt(random.nextInt(caracteresPermitidos.length())));
        }
        char[] claveOriginal = claveTemporal.toString().toCharArray();
        char[] copiaParaServicio = claveOriginal.clone();
        UsuarioDTOBasico usuarioDTOCompleto = this.ensambladorDTOUsuario.ensamblarDTOUsuarioBasico(
                this.servicioUsuario.registrarUsuario(
                        nombre, apellido, email, copiaParaServicio, activo
                )
        );
        return this.ensambladorDTOUsuario.ensamblarDTOResultadoRegistro(
                usuarioDTOCompleto, claveOriginal
        );
    }

    public UsuarioDTOBasico actualizarDatosUsuario(
            UsuarioDTOCompleto usuario, Long idUsuario, String nuevoNombre, String nuevoApellido, String nuevoEmail
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.EDITAR_USUARIOS);
        return this.ensambladorDTOUsuario.ensamblarDTOUsuarioBasico(
                this.servicioUsuario.actualizarDatosUsuario(
                        idUsuario, nuevoNombre, nuevoApellido, nuevoEmail
                )
        );
    }

    public void cambiarEstadoUsuario(UsuarioDTOCompleto usuario, Long idUsuario){
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.CAMBIAR_ESTADO_USUARIOS);
        this.servicioUsuario.cambiarEstadoUsuario(idUsuario);
    }

    public UsuarioDTOCompleto obtenerDatosTotalesUsuario(Long idUsuario){
        return this.ensambladorDTOUsuario.ensamblarDTOUsuarioCompleto(
                this.servicioUsuario.obtenerUsuario(idUsuario)
        );
    }

    public char[] restablecerContrasenaPorAdmin(UsuarioDTOCompleto usuario, Long idUsuario){
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.RESTABLECER_CONTRASENA_USUARIO);
        return this.servicioUsuario.restablecerContrasenaPorAdmin(idUsuario);
    }

    public void actualizarRolesUsuario(
            UsuarioDTOCompleto usuario, Long idUsuario, List<RolDTO> listaRolesActualizada
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.GESTIONAR_ROLES_USUARIO);
        List<Rol> rolesParaElUsuario = new ArrayList<>();
        for (RolDTO rolDTO:listaRolesActualizada){
            Rol rol = Rol.reconstruirDesdeBD(
                    rolDTO.idRol(),
                    rolDTO.nombre(),
                    rolDTO.activo()
            );
            rolesParaElUsuario.add(rol);
        }
        this.servicioUsuario.actualizarRolesUsuario(idUsuario, rolesParaElUsuario);
    }

}//===================================================================================================================//

