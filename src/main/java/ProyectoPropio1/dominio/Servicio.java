package ProyectoPropio1.dominio;

public class Servicio implements Impuestable, ItemFacturable {

    //ATRIBUTOS:

    private String nombre;

    private double precioBase;

    private final int codigoServicio;

    private static final int IMPUESTO_SERVICIOS = 10;

    //GETTERS Y SETTERS:

    @Override
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

    public Servicio(int codigoServicio, String nombre, double precioBase){
        if (nombre==null || nombre.isBlank()){
            throw new IllegalArgumentException("Nombre Vacio");
        }
        if (precioBase<=0){
            throw new IllegalArgumentException("Precio del Servicio Invalido");
        }
        this.nombre = nombre;
        this.precioBase = precioBase;
        this.codigoServicio = codigoServicio;
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
    public String getTipoItem() {
        return "Servicio";
    }

    @Override
    public int getCantidad() {
        return 1;
    }

    @Override
    public double getValorCobrado() {
        return obtenerPrecioFinal();
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

