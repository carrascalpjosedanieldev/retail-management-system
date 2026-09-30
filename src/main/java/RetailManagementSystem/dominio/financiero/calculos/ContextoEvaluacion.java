package RetailManagementSystem.dominio.financiero.calculos;

import java.time.LocalDate;
import java.util.Optional;

public class ContextoEvaluacion {

    //ATRIBUTOS:

    private final LocalDate fechaEvaluacion;

    //GETTERS Y SETTERS

    public Optional<LocalDate> getFechaEvaluacion(){
        return Optional.ofNullable(this.fechaEvaluacion);
    }

    //CONSTRUCTOR:

    private ContextoEvaluacion(LocalDate fechaEvaluacion) {
        this.fechaEvaluacion = fechaEvaluacion;
    }

    public static ContextoEvaluacion crearNuevo(LocalDate fechaEvaluacion){
        return new ContextoEvaluacion(fechaEvaluacion);
    }

}//===================================================================================================================//

