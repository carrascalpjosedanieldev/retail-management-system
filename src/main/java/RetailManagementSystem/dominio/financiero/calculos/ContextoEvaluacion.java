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

    public ContextoEvaluacion(LocalDate fechaEvaluacion) {
        this.fechaEvaluacion = fechaEvaluacion;
    }

}//===================================================================================================================//

