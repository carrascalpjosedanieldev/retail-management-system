package ProyectoPropio1.dominio;

import ProyectoPropio1.dto.DatosLineaFacturaDTO;
import ProyectoPropio1.dto.DatosServicioDTO;

public class Servicio implements Impuestable, ItemFacturable {

    //ATRIBUTOS:

    private String nombre;

    private double precioBase;

    private final int codigoServicio;

    private static int codigoSiguiente=1;

    private static final int IMPUESTO_SERVICIOS = 10;

    //GETTERS Y SETTERS:

    public String getNombre() {
        return nombre;
    }

    private void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    private void setPrecioBase(double precioBase) {
        this.precioBase = precioBase;
    }

    public int getCodigoServicio() {
        return codigoServicio;
    }

    //CONSTRUCTOR:

    public Servicio(String nombre, double precioBase){
        if (nombre==null || nombre.isBlank()){
            throw new IllegalArgumentException("Nombre Vacio");
        }
        if (precioBase<=0){
            throw new IllegalArgumentException("Precio del Servicio Invalido");
        }
        this.nombre = nombre;
        this.precioBase = precioBase;
        this.codigoServicio = codigoSiguiente++;
    }

    //METODOS:

    @Override
    public double calcularImpuesto(double precioBase) {
        return precioBase*((double)IMPUESTO_SERVICIOS/100);
    }

    private double obtenerPrecioFinal() {
        return this.precioBase + calcularImpuesto(this.precioBase);
    }

    @Override
    public double getValorCobrado() {
        return obtenerPrecioFinal();
    }

    @Override
    public DatosLineaFacturaDTO obtenerDatosLinea() {
        return new DatosLineaFacturaDTO("Servicio", this.nombre, 1, this.getValorCobrado());
    }

    public DatosServicioDTO exportarDatosServicio(){
        return new DatosServicioDTO(this.codigoServicio, this.getNombre(), this.getValorCobrado());
    }

    //METODOS MODIFICAR SERVICIO:

    public void cambiarNombreServicio(String nombreServicio){
        if (nombreServicio==null || nombreServicio.isBlank()){
            throw new IllegalArgumentException("Nombre Vacio");
        }
        setNombre(nombreServicio);
    }

    public void cambiarPrecioBase(double precioNuevo){
        if (precioNuevo<=0){
            throw new IllegalArgumentException("Precio Invalido");
        }
        setPrecioBase(precioNuevo);
    }

}

