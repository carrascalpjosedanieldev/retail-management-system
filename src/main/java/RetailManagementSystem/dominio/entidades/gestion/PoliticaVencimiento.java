package RetailManagementSystem.dominio.entidades.gestion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class PoliticaVencimiento {

    private static final BigDecimal CIEN = new BigDecimal("100");

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
        this.nombre = nombre.trim();
    }

    public int getDiasUmbral() {
        return diasUmbral;
    }

    public BigDecimal getPorcentajeDescuento() {
        return porcentajeDescuento;
    }
    private void setPorcentajeDescuento(BigDecimal porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento.setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isActiva() { return activa; }

    //VALIDACIONES:

    private void validarNombre(String nombre){
        if (nombre==null || nombre.isBlank()){
            throw new IllegalArgumentException("El Nombre de la Política de Vencimiento NO puede estar Vacío");
        }
    }

    private void validarDiasUmbral(Integer diasUmbral){
        if (diasUmbral == null || diasUmbral < 0) {
            throw new IllegalArgumentException("Dias Umbral de la Política de Vencimiento Inválidos");
        }
    }

    private void validarPorcentaje(BigDecimal porcentaje){
        if (porcentaje == null){
            throw new IllegalArgumentException("El Porcentaje de la Política de Vencimiento NO puede ser Nulo");
        }
        if (porcentaje.compareTo(BigDecimal.ZERO) < 0 || porcentaje.compareTo(CIEN) > 0) {
            throw new IllegalArgumentException("Porcentaje de Descuento de Política de Vencimiento Invalido:  " + porcentaje + "%");
        }
    }

    //CONSTRUCTORES:

    private PoliticaVencimiento(
            Integer idPolitica, String nombrePolitica, Integer diasUmbral, BigDecimal porcentajeDescuento,
            Boolean activa
    ) {
        validarNombre(nombrePolitica);
        validarDiasUmbral(diasUmbral);
        validarPorcentaje(porcentajeDescuento);
        if (activa == null){
            throw new IllegalArgumentException("El Estado de la Política de Vencimiento es Obligatorio");
        }
        this.idPolitica = idPolitica;
        setNombre(nombrePolitica);
        this.diasUmbral = diasUmbral;
        setPorcentajeDescuento(porcentajeDescuento);
        this.activa = activa;
    }

    public static PoliticaVencimiento reconstruirDesdeBD(
            Integer idPolitica, String nombrePolitica, Integer diasUmbral, BigDecimal porcentajeDescuento, Boolean activa
    ) {
        return new PoliticaVencimiento(idPolitica, nombrePolitica, diasUmbral, porcentajeDescuento, activa);
    }

    public static PoliticaVencimiento crearNuevo(
            String  nombrePolitica, Integer diasUmbral, BigDecimal porcentajeDescuento, Boolean activa
    ) {
        return new PoliticaVencimiento(null, nombrePolitica, diasUmbral, porcentajeDescuento, activa);
    }

    //MÉTODOS:

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PoliticaVencimiento politicaV = (PoliticaVencimiento) o;
        if (this.idPolitica == null || politicaV.getIdPolitica() == null) {
            return false;
        }
        return Objects.equals(this.idPolitica, politicaV.getIdPolitica());
    }

    @Override
    public int hashCode() {
        return idPolitica != null ? idPolitica.hashCode() : getClass().hashCode();
    }

    public void cambiarNombrePolitica(String nombreNuevo){
        validarNombre(nombreNuevo);
        setNombre(nombreNuevo);
    }

    public void cambiarDiasUmbral(Integer diasUmbral){
        validarDiasUmbral(diasUmbral);
        this.diasUmbral = diasUmbral;
    }

    public void cambiarPorcentajeDescuento(BigDecimal porcentajeDescuento){
        validarPorcentaje(porcentajeDescuento);
        setPorcentajeDescuento(porcentajeDescuento);
    }

    public void cambiarEstado(){
        this.activa = !this.isActiva();
    }

}//===================================================================================================================//

