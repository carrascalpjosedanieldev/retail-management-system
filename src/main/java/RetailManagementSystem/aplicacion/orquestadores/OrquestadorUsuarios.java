package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.seguridad.*;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOUsuario;
import RetailManagementSystem.aplicacion.servicios.ServicioUsuario;
import RetailManagementSystem.dominio.entidades.seguridad.Rol;

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
            String nombre, String apellido, String email, boolean activo
    ) {
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
            Long idUsuario, String nuevoNombre, String nuevoApellido, String nuevoEmail
    ) {
        return this.ensambladorDTOUsuario.ensamblarDTOUsuarioBasico(
                this.servicioUsuario.actualizarDatosUsuario(
                        idUsuario, nuevoNombre, nuevoApellido, nuevoEmail
                )
        );
    }

    public void cambiarEstadoUsuario(Long idUsuario){
        this.servicioUsuario.cambiarEstadoUsuario(idUsuario);
    }

    public UsuarioDTOCompleto obtenerDatosTotalesUsuario(Long idUsuario){
        return this.ensambladorDTOUsuario.ensamblarDTOUsuarioCompleto(
                this.servicioUsuario.obtenerUsuario(idUsuario)
        );
    }

    public char[] restablecerContrasenaPorAdmin(Long idUsuario){
        return this.servicioUsuario.restablecerContrasenaPorAdmin(idUsuario);
    }

    public void actualizarRolesUsuario(Long idUsuario, List<RolDTO> listaRolesActualizada){
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

