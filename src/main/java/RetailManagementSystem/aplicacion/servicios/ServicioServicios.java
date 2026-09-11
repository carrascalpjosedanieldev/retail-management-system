package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.entidades.comercial.Servicio;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioDescuentos;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioImpuestos;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioServicio;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;

import java.math.BigDecimal;
import java.util.List;

public class ServicioServicios {

    //ATRIBUTOS:

    private final RepositorioImpuestos repositorioImpuestos;

    private final RepositorioDescuentos repositorioDescuentos;

    private final RepositorioServicio repositorioServicio;

    private final GestorTransaccional gestorTransaccional;

    //CONSTRUCTOR:

    public ServicioServicios(
            RepositorioImpuestos repositorioImpuestos, RepositorioDescuentos repositorioDescuentos,
            RepositorioServicio repositorioServicio, GestorTransaccional gestorTransaccional
    ) {
        this.repositorioImpuestos = repositorioImpuestos;
        this.repositorioDescuentos = repositorioDescuentos;
        this.repositorioServicio = repositorioServicio;
        this.gestorTransaccional = gestorTransaccional;
    }

    //MÉTODOS:

    private Servicio obtenerServicio(String codigoServicio){
        return this.repositorioServicio.obtenerServicio(codigoServicio);
    }

    private void actualizarServicio(Servicio servicio){
        this.repositorioServicio.actualizarServicio(servicio);
    }

    public Servicio obtenerServicioActivoParaLaVenta(String codigoServicio){
        return this.gestorTransaccional.ejecutarEnTransaccionDeLectura(()->
                this.repositorioServicio.obtenerServicioActivoSoloPorCodigo(codigoServicio)
        );
    }

    public boolean existeServicio(String codigoServicio){
        return this.gestorTransaccional.ejecutarEnTransaccionDeLectura(()->
                this.repositorioServicio.existeServicio(codigoServicio)
        );
    }

    public Servicio registrarServicioNuevo(
            String nombreServicio, BigDecimal precioBase, int idImpuesto, int idDescuento
    ) {
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->{
            Impuesto impuesto = this.repositorioImpuestos.obtenerImpuesto(idImpuesto);
            Descuento descuento = this.repositorioDescuentos.obtenerDescuento(idDescuento);
            Servicio servicio = Servicio.crearNuevo(nombreServicio, precioBase, impuesto, descuento);
            this.repositorioServicio.insertarServicio(servicio);
            return servicio;
        });
    }

    public Servicio actualizarServicio(
            String codigoServicio, String nombre, BigDecimal precioBase, int idImpuesto, int idDescuento
    ) {
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->{
            Servicio servicio = obtenerServicio(codigoServicio);
            servicio.cambiarNombreServicio(nombre);
            servicio.cambiarPrecioBase(precioBase);
            if (servicio.getIdImpuesto() != idImpuesto){
                Impuesto impuesto = this.repositorioImpuestos.obtenerImpuesto(idImpuesto);
                servicio.cambiarImpuesto(impuesto);
            }
            if (servicio.getIdDescuento() != idDescuento){
                Descuento descuento = this.repositorioDescuentos.obtenerDescuento(idDescuento);
                servicio.cambiarDescuento(descuento);
            }
            actualizarServicio(servicio);
            return servicio;
        });
    }

    public void cambiarEstadoServicio(String codigoServicio){
        this.gestorTransaccional.ejecutarEnTransaccion(()->{
            Servicio servicio = obtenerServicio(codigoServicio);
            if (servicio.isActivo()){
                servicio.desactivarServicio();
            } else {
                servicio.activarServicio();
            }
            actualizarServicio(servicio);
        });
    }

    public List<Servicio> obtenerTodosLosServicios(){
        return this.gestorTransaccional.ejecutarEnTransaccionDeLectura(
                this.repositorioServicio::obtenerTodosLosServicios
        );
    }

}//===================================================================================================================//

