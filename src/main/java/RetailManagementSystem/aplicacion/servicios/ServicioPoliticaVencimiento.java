package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.gestion.PoliticaVencimiento;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioPoliticaVencimiento;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;

import java.math.BigDecimal;
import java.util.List;

public class ServicioPoliticaVencimiento {

    //ATRIBUTOS:

    private final RepositorioPoliticaVencimiento repositorioPoliticaVencimiento;

    private final GestorTransaccional gestorTransaccional;

    //CONSTRUCTOR:

    public ServicioPoliticaVencimiento(
            RepositorioPoliticaVencimiento repositorioPoliticaVencimiento, GestorTransaccional gestorTransaccional
    ) {
        this.repositorioPoliticaVencimiento = repositorioPoliticaVencimiento;
        this.gestorTransaccional = gestorTransaccional;
    }

    //MÉTODOS:

    public PoliticaVencimiento registrarPoliticaVencimiento(
            String nombre, int diasUmbral, BigDecimal porcentaje, boolean activa
    ) {
        PoliticaVencimiento politicaVencimiento = PoliticaVencimiento.crearNuevo(nombre, diasUmbral, porcentaje, activa);
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->
                this.repositorioPoliticaVencimiento.insertarPoliticaVencimiento(politicaVencimiento)
        );
    }

    public PoliticaVencimiento obtenerPoliticaVencimiento(int idPoliticaVencimiento){
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->
                this.repositorioPoliticaVencimiento.obtenerPoliticaVencimiento(idPoliticaVencimiento)
        );
    }

    public PoliticaVencimiento actualizarPoliticaVencimiento(
            int idPolitica, String nombre, int diasUmbral, BigDecimal porcentaje
    ) {
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->{
            PoliticaVencimiento politicaVencimiento =
                    this.repositorioPoliticaVencimiento.obtenerPoliticaVencimiento(idPolitica);
            politicaVencimiento.cambiarNombrePolitica(nombre);
            politicaVencimiento.cambiarDiasUmbral(diasUmbral);
            politicaVencimiento.cambiarPorcentajeDescuento(porcentaje);
            actualizarPoliticaVencimiento(politicaVencimiento);
            return politicaVencimiento;
        });
    }

    private void actualizarPoliticaVencimiento(PoliticaVencimiento politicaVencimiento){
        this.repositorioPoliticaVencimiento.actualizarPoliticaVencimiento(politicaVencimiento);
    }

    public void cambiarEstadoPoliticaDeVencimiento(int idPolitica){
        this.gestorTransaccional.ejecutarEnTransaccion(()->{
            PoliticaVencimiento politicaVencimiento =
                    this.repositorioPoliticaVencimiento.obtenerPoliticaVencimiento(idPolitica);
            politicaVencimiento.cambiarEstado();
            actualizarPoliticaVencimiento(politicaVencimiento);
        });
    }

    public List<PoliticaVencimiento> obtenerPoliticasVencimientoActivas(){
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(
                this.repositorioPoliticaVencimiento::obtenerPoliticasVencimientoActivas
        );
    }

    public List<PoliticaVencimiento> obtenerTodasLasPoliticasDeVencimiento(){
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(
                this.repositorioPoliticaVencimiento::obtenerTodasLasPoliticasDeVencimiento
        );
    }

}//===================================================================================================================//

