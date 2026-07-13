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

    public int registrarPoliticaVencimiento(String nombre, int diasUmbral, BigDecimal porcentaje){
        PoliticaVencimiento borrador = PoliticaVencimiento.crearNuevo(nombre, diasUmbral, porcentaje);
        PoliticaVencimiento politicaVencimiento = this.repositorioPoliticaVencimiento.insertarPoliticaVencimiento(borrador);
        return politicaVencimiento.getIdPolitica();
    }

    public PoliticaVencimiento obtenerPoliticaVencimiento(int idPoliticaVencimiento){
        return this.repositorioPoliticaVencimiento.obtenerPoliticaVencimiento(idPoliticaVencimiento);
    }

    private void actualizarPoliticaVencimiento(PoliticaVencimiento politicaVencimiento){
        this.repositorioPoliticaVencimiento.actualizarPoliticaVencimiento(politicaVencimiento);
    }

    public void activarPoliticaVencimiento(int idPoliticaVencimiento){
        PoliticaVencimiento politicaVencimiento = this.obtenerPoliticaVencimiento(idPoliticaVencimiento);
        politicaVencimiento.activar();
        this.actualizarPoliticaVencimiento(politicaVencimiento);
    }

    public void desactivarPoliticaVencimiento(int idPoliticaVencimiento){
        PoliticaVencimiento politicaVencimiento = this.obtenerPoliticaVencimiento(idPoliticaVencimiento);
        politicaVencimiento.desactivar();
        this.actualizarPoliticaVencimiento(politicaVencimiento);
    }

    public void cambiarNombrePoliticaVencimiento(int idPoliticaVencimiento, String nombreNuevo){
        PoliticaVencimiento politicaVencimiento = this.obtenerPoliticaVencimiento(idPoliticaVencimiento);
        politicaVencimiento.cambiarNombrePolitica(nombreNuevo);
        this.actualizarPoliticaVencimiento(politicaVencimiento);
    }

    public void cambiarDiasUmbralPoliticaVencimiento(int idPoliticaVencimiento, int diasUmbral){
        PoliticaVencimiento politicaVencimiento = this.obtenerPoliticaVencimiento(idPoliticaVencimiento);
        politicaVencimiento.cambiarDiasUmbral(diasUmbral);
        this.actualizarPoliticaVencimiento(politicaVencimiento);
    }

    public void cambiarPorcentajePoliticaVencimiento(int idPoliticaVencimiento, BigDecimal porcentajeNuevo){
        PoliticaVencimiento politicaVencimiento = this.obtenerPoliticaVencimiento(idPoliticaVencimiento);
        politicaVencimiento.cambiarPorcentajeDescuento(porcentajeNuevo);
        this.actualizarPoliticaVencimiento(politicaVencimiento);
    }

    public List<PoliticaVencimiento> obtenerPoliticasVencimientoActivas(){
        return this.repositorioPoliticaVencimiento.obtenerPoliticasVencimientoActivas();
    }

    public List<PoliticaVencimiento> obtenerPoliticasVencimientoInactivas(){
        return this.repositorioPoliticaVencimiento.obtenerPoliticasVencimientoInactivas();
    }


}
