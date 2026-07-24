package ProyectoPropio1.servicios.aplicacion;

import ProyectoPropio1.dominio.PoliticaVencimiento;
import ProyectoPropio1.dominio.puertos.RepositorioPoliticaVencimiento;

import java.math.BigDecimal;
import java.util.List;

public class ServicioPoliticaVencimiento {

    private final RepositorioPoliticaVencimiento repositorioPoliticaVencimiento;

    public ServicioPoliticaVencimiento(RepositorioPoliticaVencimiento repositorioPoliticaVencimiento) {
        this.repositorioPoliticaVencimiento = repositorioPoliticaVencimiento;
    }

    public void registrarPoliticaVencimiento(String nombre, int diasUmbral, BigDecimal porcentaje, boolean activa){
        PoliticaVencimiento politicaVencimiento = PoliticaVencimiento.crearNuevo(nombre, diasUmbral, porcentaje, activa);
        this.repositorioPoliticaVencimiento.insertarPoliticaVencimiento(politicaVencimiento);
    }

    public PoliticaVencimiento obtenerPoliticaVencimiento(int idPoliticaVencimiento){
        return this.repositorioPoliticaVencimiento.obtenerPoliticaVencimiento(idPoliticaVencimiento);
    }

    public void actualizarPoliticaVencimiento(int idPolitica, String nombre, int diasUmbral, BigDecimal porcentaje){
        PoliticaVencimiento politicaVencimiento = this.obtenerPoliticaVencimiento(idPolitica);
        politicaVencimiento.cambiarNombrePolitica(nombre);
        politicaVencimiento.cambiarDiasUmbral(diasUmbral);
        politicaVencimiento.cambiarPorcentajeDescuento(porcentaje);
        this.actualizarPoliticaVencimiento(politicaVencimiento);
    }

    private void actualizarPoliticaVencimiento(PoliticaVencimiento politicaVencimiento){
        this.repositorioPoliticaVencimiento.actualizarPoliticaVencimiento(politicaVencimiento);
    }

    public void cambiarEstadoPoliticaDeVencimiento(int idPolitica){
        PoliticaVencimiento politicaVencimiento = this.obtenerPoliticaVencimiento(idPolitica);
        if (politicaVencimiento.isActiva()){
            politicaVencimiento.desactivar();
        } else {
            politicaVencimiento.activar();
        }
        this.actualizarPoliticaVencimiento(politicaVencimiento);
    }

    public List<PoliticaVencimiento> obtenerPoliticasVencimientoActivas(){
        return this.repositorioPoliticaVencimiento.obtenerPoliticasVencimientoActivas();
    }

    public List<PoliticaVencimiento> obtenerPoliticasVencimientoInactivas(){
        return this.repositorioPoliticaVencimiento.obtenerPoliticasVencimientoInactivas();
    }


}
