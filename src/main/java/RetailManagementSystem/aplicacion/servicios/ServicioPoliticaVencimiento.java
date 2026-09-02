package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.gestion.PoliticaVencimiento;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioPoliticaVencimiento;

import java.math.BigDecimal;
import java.util.List;

public class ServicioPoliticaVencimiento {

    //ATRIBUTOS:

    private final RepositorioPoliticaVencimiento repositorioPoliticaVencimiento;

    //CONSTRUCTOR:

    public ServicioPoliticaVencimiento(RepositorioPoliticaVencimiento repositorioPoliticaVencimiento) {
        this.repositorioPoliticaVencimiento = repositorioPoliticaVencimiento;
    }

    //MÉTODOS:

    public PoliticaVencimiento registrarPoliticaVencimiento(
            String nombre, int diasUmbral, BigDecimal porcentaje, boolean activa
    ) {
        PoliticaVencimiento politicaVencimiento = PoliticaVencimiento.crearNuevo(nombre, diasUmbral, porcentaje, activa);
        return this.repositorioPoliticaVencimiento.insertarPoliticaVencimiento(politicaVencimiento);
    }

    public PoliticaVencimiento obtenerPoliticaVencimiento(int idPoliticaVencimiento){
        return this.repositorioPoliticaVencimiento.obtenerPoliticaVencimiento(idPoliticaVencimiento);
    }

    public PoliticaVencimiento actualizarPoliticaVencimiento(
            int idPolitica, String nombre, int diasUmbral, BigDecimal porcentaje
    ) {
        PoliticaVencimiento politicaVencimiento = obtenerPoliticaVencimiento(idPolitica);
        politicaVencimiento.cambiarNombrePolitica(nombre);
        politicaVencimiento.cambiarDiasUmbral(diasUmbral);
        politicaVencimiento.cambiarPorcentajeDescuento(porcentaje);
        actualizarPoliticaVencimiento(politicaVencimiento);
        return politicaVencimiento;
    }

    private void actualizarPoliticaVencimiento(PoliticaVencimiento politicaVencimiento){
        this.repositorioPoliticaVencimiento.actualizarPoliticaVencimiento(politicaVencimiento);
    }

    public void cambiarEstadoPoliticaDeVencimiento(int idPolitica){
        PoliticaVencimiento politicaVencimiento = this.obtenerPoliticaVencimiento(idPolitica);
        politicaVencimiento.cambiarEstado();
        actualizarPoliticaVencimiento(politicaVencimiento);
    }

    public List<PoliticaVencimiento> obtenerPoliticasVencimientoActivas(){
        return this.repositorioPoliticaVencimiento.obtenerPoliticasVencimientoActivas();
    }

    public List<PoliticaVencimiento> obtenerTodasLasPoliticasDeVencimiento(){
        return this.repositorioPoliticaVencimiento.obtenerTodasLasPoliticasDeVencimiento();
    }


}//===================================================================================================================//

