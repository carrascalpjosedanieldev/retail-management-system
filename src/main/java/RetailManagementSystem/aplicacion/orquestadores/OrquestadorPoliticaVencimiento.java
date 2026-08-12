package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.gestion.PoliticaVencimientoDTO;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOPoliticaVencimiento;
import RetailManagementSystem.aplicacion.servicios.ServicioPoliticaVencimiento;

import java.math.BigDecimal;
import java.util.List;

public class OrquestadorPoliticaVencimiento {

    //ATRIBUTOS:

    private final ServicioPoliticaVencimiento servicioPoliticaVencimiento;

    private final EnsambladorDTOPoliticaVencimiento ensambladorDTOPoliticaVencimiento;

    //CONSTRUCTOR:

    public OrquestadorPoliticaVencimiento(
            ServicioPoliticaVencimiento servicioPoliticaVencimiento, EnsambladorDTOPoliticaVencimiento ensambladorDTOPoliticaVencimiento
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

    public PoliticaVencimientoDTO registrarPoliticaVencimiento(
            String nombre, int diasUmbral, BigDecimal porcentaje, boolean activo
    ) {
        return this.ensambladorDTOPoliticaVencimiento.ensamblarDatosPoliticaVencimiento(
                this.servicioPoliticaVencimiento.registrarPoliticaVencimiento(
                        nombre, diasUmbral, porcentaje, activo
                )
        );
    }

    public PoliticaVencimientoDTO actualizarPoliticaVencimiento(
            int idPoliticaV, String nuevoNombre, int nuevoDiasUmbral, BigDecimal nuevoPorcentaje
    ) {
        return this.ensambladorDTOPoliticaVencimiento.ensamblarDatosPoliticaVencimiento(
                this.servicioPoliticaVencimiento.actualizarPoliticaVencimiento(
                        idPoliticaV, nuevoNombre, nuevoDiasUmbral, nuevoPorcentaje
                )
        );
    }

    public void cambiarEstadoPoliticaV(int idPoliticaV) {
        this.servicioPoliticaVencimiento.cambiarEstadoPoliticaDeVencimiento(idPoliticaV);
    }

}//===================================================================================================================//

