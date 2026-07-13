package ProyectoPropio1.dominio;

import java.math.BigDecimal;

public class PoliticaVencimiento {

    //ATRIBUTOS:

    private final Integer idPolitica;

    private String nombre;

    private int diasUmbral;

    private BigDecimal porcentajeDescuento;

    private boolean activa;

    //GETTERS Y SETTERS:

    public Integer getIdPolitica() {
        return idPolitica;
    }

    public String getNombre() {
        return nombre;
    }
    private void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getDiasUmbral() { return diasUmbral; }
    private void setDiasUmbral(int diasUmbral) {
        this.diasUmbral = diasUmbral;
    }

    public BigDecimal getPorcentajeDescuento() { return porcentajeDescuento; }
    private void setPorcentajeDescuento(BigDecimal porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public boolean isActiva() { return activa; }
    private void setActiva(boolean activa) {
        this.activa = activa;
    }
    //CONSTRUCTORES:

    private PoliticaVencimiento(Integer idPolitica, String nombrePolitica, int diasUmbral, BigDecimal porcentajeDescuento,
                                boolean activa){
        if (nombrePolitica==null || nombrePolitica.isBlank()){
            throw new IllegalArgumentException("Nombre de la Politica de Vencimiento Vacio");
        }
        if (diasUmbral < 0) {
            throw new IllegalArgumentException("Dias Umbral de Politica de Vencimiento Invalido");
        }
        if (porcentajeDescuento.compareTo(BigDecimal.ZERO) < 0 || porcentajeDescuento.compareTo(new BigDecimal("100")) > 0){
            throw new IllegalArgumentException("Porcentaje de Descuento de Politica de Vencimiento Invalido");
        }
        this.idPolitica = idPolitica;
        this.nombre = nombrePolitica;
        this.diasUmbral = diasUmbral;
        this.porcentajeDescuento = porcentajeDescuento;
        this.activa = activa;
    }

    public static PoliticaVencimiento reconstruirDesdeBD(int idPolitica, String nombrePolitica, int diasUmbral,
                                                  BigDecimal porcentajeDescuento, boolean activa){
        return new PoliticaVencimiento(idPolitica, nombrePolitica, diasUmbral, porcentajeDescuento, activa);
    }

    private PoliticaVencimiento(String nombrePolitica, int diasUmbral, BigDecimal porcentajeDescuento, boolean activa) {
        this(null, nombrePolitica, diasUmbral, porcentajeDescuento, activa);
    }

    public static PoliticaVencimiento crearNuevo(String  nombrePolitica, int diasUmbral, BigDecimal porcentajeDescuento){
        return new PoliticaVencimiento(nombrePolitica, diasUmbral, porcentajeDescuento, true);
    }

    //METODOS:

    public void cambiarNombrePolitica(String nombreNuevo){
        if (nombreNuevo==null || nombreNuevo.isBlank()){
            throw new IllegalArgumentException("Nombre Nuevo para la Politica de Vencimiento Vacio");
        }
        setNombre(nombreNuevo);
    }

    public void cambiarDiasUmbral(int diasUmbral){
        if (diasUmbral< 0){
            throw new IllegalArgumentException("Dias Umbral de Politica de Vencimiento Invalido");
        }
        setDiasUmbral(diasUmbral);
    }

    public void cambiarPorcentajeDescuento(BigDecimal porcentajeDescuento){
        if (porcentajeDescuento.compareTo(BigDecimal.ZERO) <= 0 || porcentajeDescuento.compareTo(new BigDecimal("100")) > 0){
            throw new IllegalArgumentException("Porcentaje de Descuento de Politica de Vencimiento Invalido");
        }
        setPorcentajeDescuento(porcentajeDescuento);
    }

    public void activar(){
        if (isActiva()){
            throw new IllegalStateException("La Politica de Vencimiento ya esta Activo");
        }
        setActiva(true);
    }

    public void desactivar(){
        if (!isActiva()){
            throw new IllegalStateException("La Politica de Vencimiento ya esta Inactivo");
        }
        setActiva(false);
    }

}

