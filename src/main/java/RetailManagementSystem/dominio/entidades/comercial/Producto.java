package RetailManagementSystem.dominio.entidades.comercial;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.enums.TipoItem;
import RetailManagementSystem.dominio.enums.TipoProducto;
import RetailManagementSystem.dominio.excepciones.reglasDeNegocio.StockInsuficienteException;
import RetailManagementSystem.dominio.financiero.calculos.MatematicaFinanciera;

import java.math.BigDecimal;
import java.util.UUID;

import static RetailManagementSystem.dominio.enums.TipoItem.PRODUCTO;

public abstract class Producto implements ItemFacturable, Stockeable {

    //ATRIBUTOS:

    private final TipoProducto tipoProducto;

    private final String codigo;

    private String nombre;

    private BigDecimal valorCompra;

    private BigDecimal porcentajeGanancia;

    private int stock;

    private Impuesto impuesto;

    private Descuento descuento;

    private boolean activo;

    //GETTERS Y SETTERS:

    @Override
    public TipoItem getTipoItem() {
        return PRODUCTO;
    }

    public TipoProducto getTipoProducto(){
        return tipoProducto;
    }

    @Override
    public String getCodigo() {
        return codigo;
    }

    @Override
    public String getNombre() {
        return nombre;
    }
    protected void setNombre(String nombre) {
        this.nombre = nombre.trim();
    }

    public BigDecimal getValorCompra() {
        return valorCompra;
    }
    protected void setValorCompra(BigDecimal valorCompra) {
        this.valorCompra = valorCompra.setScale(
                MatematicaFinanciera.ESCALA_CALCULO, MatematicaFinanciera.REDONDEO_ESTANDAR
        );
    }

    public BigDecimal getPorcentajeGanancia() {
        return porcentajeGanancia;
    }
    protected void setPorcentajeGanancia(BigDecimal porcentajeGanancia) {
        this.porcentajeGanancia = porcentajeGanancia.setScale(
                MatematicaFinanciera.ESCALA_CALCULO, MatematicaFinanciera.REDONDEO_ESTANDAR
        );
    }

    public int getStock() {
        return stock;
    }

    @Override
    public Impuesto getImpuesto() {
        return impuesto;
    }

    public Descuento getDescuento(){
        return descuento;
    }

    public boolean isActivo(){
        return activo;
    }

    //VALIDACIONES:

    private void validarTipoProducto(TipoProducto tipoProducto){
        if (tipoProducto == null){
            throw new IllegalArgumentException("El Tipo de Producto es Obligatorio.");
        }
    }

    private void validarCodigo(String codigo){
        if (codigo == null || codigo.isBlank()){
            throw new IllegalArgumentException("El Código del Producto esta Vacío");
        }
        if (codigo.length() > 50){
            throw new IllegalArgumentException("El Código del Producto excede los Caracteres Máximos Posibles");
        }
    }

    private void validarNombre(String nombre){
        if (nombre==null || nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Producto Invalido");
        }
    }

    private void validarValorCompra(BigDecimal valorCompra){
        if (valorCompra == null || valorCompra.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor de Compra del Producto Invalido");
        }
    }

    private void validarPorcentajeGanancia(BigDecimal porcentajeGanancia){
        if (
            porcentajeGanancia == null ||
            porcentajeGanancia.compareTo(BigDecimal.ZERO) <= 0  ||
            porcentajeGanancia.compareTo(MatematicaFinanciera.CIEN) > 0)
        {
            throw new IllegalArgumentException("Porcentaje de Ganancia del Producto Invalido");
        }
    }

    private void validarStock(Integer stock){
        if (stock == null || stock<0){
            throw new IllegalArgumentException("Stock del Producto Invalido");
        }
    }

    private void validarImpuesto(Impuesto impuesto){
        if (impuesto==null){
            throw new IllegalArgumentException("El Producto Debe Tener Impuesto Obligatoriamente");
        }
    }

    private void validarEstadoImpuesto(Impuesto impuesto){
        if (!impuesto.isActivo()){
            throw new IllegalArgumentException("El Impuesto que le quieres poner al Producto esta Inactivo");
        }
    }

    private void validarDescuento(Descuento descuento){
        if (descuento==null){
            throw new IllegalArgumentException("El Producto Debe Tener Descuento Obligatoriamente");
        }
    }

    private void validarEstadoDescuento(Descuento descuento){
        if (!descuento.isActivo()){
            throw new IllegalArgumentException("El Descuento que le quieres poner al Producto esta Inactivo");
        }
    }

    //CONSTRUCTOR:

        //Reconstruir desde DB
    protected Producto(
            String codigo, String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia,
            Integer stock, Impuesto impuesto, Descuento descuento, Boolean activo, TipoProducto tipoProducto
    ) {
        validarTipoProducto(tipoProducto);
        validarCodigo(codigo);
        validarNombre(nombre);
        validarValorCompra(valorCompra);
        validarPorcentajeGanancia(porcentajeGanancia);
        validarStock(stock);
        validarImpuesto(impuesto);
        validarDescuento(descuento);
        if (activo == null){
            throw new IllegalArgumentException("El Estado del Producto es Obligatorio");
        }
        this.tipoProducto = tipoProducto;
        this.codigo = codigo;
        setNombre(nombre);
        setValorCompra(valorCompra);
        setPorcentajeGanancia(porcentajeGanancia);
        this.descuento = descuento;
        this.stock = stock;
        this.impuesto = impuesto;
        this.activo = activo;
    }

        //Crear Nuevo
    protected Producto(
            String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia, Integer stock,
            Impuesto impuesto, Descuento descuento, TipoProducto tipoProducto
    ) {
        this(
            UUID.randomUUID().toString(), nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento,
            true, tipoProducto
        );
        validarEstadoImpuesto(impuesto);
        validarEstadoDescuento(descuento);
    }

    //MÉTODOS:

    protected BigDecimal dividirEntreCien(BigDecimal valor){
        return valor.divide(
                MatematicaFinanciera.CIEN, MatematicaFinanciera.ESCALA_CALCULO, MatematicaFinanciera.REDONDEO_ESTANDAR
        );
    }

    public BigDecimal getPrecioBase(){
        return getValorCompra().multiply(
                (BigDecimal.ONE).add(dividirEntreCien(getPorcentajeGanancia()))
        )
        .setScale(MatematicaFinanciera.ESCALA_CALCULO, MatematicaFinanciera.REDONDEO_ESTANDAR);
    }

    //MÉTODOS MODIFICAR PRODUCTO:

    public void cambiarNombreProducto(String nombre){
        validarNombre(nombre);
        setNombre(nombre);
    }

    public void cambiarPorcentajeGanancia(BigDecimal porcentajeGanancia) {
        validarPorcentajeGanancia(porcentajeGanancia);
        setPorcentajeGanancia(porcentajeGanancia);
    }

    public void cambiarValorCompra(BigDecimal valorNuevo){
        validarValorCompra(valorNuevo);
        setValorCompra(valorNuevo);
    }

    public void aumentarStock(Integer cantidad){
        if (cantidad == null || cantidad<=0){
            throw new IllegalArgumentException("Cantidad de Producto a Reponer Invalida");
        }
        this.stock = this.getStock() + cantidad;
    }

    public void reducirStock(Integer cantidad){
        if (cantidad == null || cantidad<=0){
            throw new IllegalArgumentException("Cantidad de Producto a Retirar Invalida");
        }
        int stockTotal = this.getStock() - cantidad;
        if (stockTotal<0){
            throw new StockInsuficienteException("La Cantidad de Producto a Reducir es Mayor a la Cantidad Existente");
        }
        this.stock = stockTotal;
    }

    public void cambiarImpuesto(Impuesto impuesto){
        validarImpuesto(impuesto);
        validarEstadoImpuesto(impuesto);
        this.impuesto = impuesto;
    }

    public void cambiarDescuento(Descuento descuento){
        validarDescuento(descuento);
        validarEstadoDescuento(descuento);
        this.descuento = descuento;
    }

    public void cambiarEstado(){
        this.activo = !this.isActivo();
    }

    //STOCKEABLE:

    @Override
    public void validarStockDisponible(int cantidadSolicitada) {
        if (cantidadSolicitada > getStock()) {
            throw new StockInsuficienteException("Stock del Producto -" + getNombre() + "- Insuficiente\n" +
                    "Cantidad Solicitada:  " + cantidadSolicitada + ", Cantidad Existente:  " + getStock());
        }
    }

}//===================================================================================================================//

