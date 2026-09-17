package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.gestion.PoliticaVencimientoDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOPoliticaVencimiento;
import RetailManagementSystem.aplicacion.servicios.ServicioPoliticaVencimiento;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;

import java.math.BigDecimal;
import java.util.List;

public class OrquestadorPoliticaVencimiento {

    //ATRIBUTOS:

    private final ServicioPoliticaVencimiento servicioPoliticaVencimiento;

    private final EnsambladorDTOPoliticaVencimiento ensambladorDTOPoliticaVencimiento;

    //CONSTRUCTOR:

    public OrquestadorPoliticaVencimiento(
            ServicioPoliticaVencimiento servicioPoliticaVencimiento,
            EnsambladorDTOPoliticaVencimiento ensambladorDTOPoliticaVencimiento
    ) {
        this.servicioPoliticaVencimiento = servicioPoliticaVencimiento;
        this.ensambladorDTOPoliticaVencimiento = ensambladorDTOPoliticaVencimiento;
    }

    //MÉTODOS:

    public List<PoliticaVencimientoDTO> obtenerTodasLasPoliticasV() {
        return this.ensambladorDTOPoliticaVencimiento.ensamblarDetallePoliticasVencimiento(
                this.servicioPoliticaVencimiento.obtenerTodasLasPoliticasDeVencimiento()
        );
    }

    public List<PoliticaVencimientoDTO> obtenerPoliticasVActivas() {
        return this.ensambladorDTOPoliticaVencimiento.ensamblarDetallePoliticasVencimiento(
                this.servicioPoliticaVencimiento.obtenerPoliticasVencimientoActivas()
        );
    }

    public PoliticaVencimientoDTO registrarPoliticaVencimiento(
            UsuarioDTOCompleto usuario, String nombre, int diasUmbral, BigDecimal porcentaje, boolean activo
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.REGISTRAR_POLITICAS_V);
        return this.ensambladorDTOPoliticaVencimiento.ensamblarDatosPoliticaVencimiento(
                this.servicioPoliticaVencimiento.registrarPoliticaVencimiento(
                        nombre, diasUmbral, porcentaje, activo
                )
        );
    }

    public PoliticaVencimientoDTO actualizarPoliticaVencimiento(
            UsuarioDTOCompleto usuario, int idPoliticaV, String nuevoNombre, int nuevoDiasUmbral,
            BigDecimal nuevoPorcentaje
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.MODIFICAR_POLITICAS_V);
        return this.ensambladorDTOPoliticaVencimiento.ensamblarDatosPoliticaVencimiento(
                this.servicioPoliticaVencimiento.actualizarPoliticaVencimiento(
                        idPoliticaV, nuevoNombre, nuevoDiasUmbral, nuevoPorcentaje
                )
        );
    }

    public void cambiarEstadoPoliticaV(UsuarioDTOCompleto usuario, int idPoliticaV) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.CAMBIAR_ESTADO_POLITICAS_V);
        this.servicioPoliticaVencimiento.cambiarEstadoPoliticaDeVencimiento(idPoliticaV);
    }

}//===================================================================================================================//

