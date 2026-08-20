package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarServicios;

import RetailManagementSystem.aplicacion.dto.comercial.ServicioDTO;
import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorServicios;
import RetailManagementSystem.vista.utilidades.FormateadorNumeros;
import RetailManagementSystem.vista.utilidades.GestorAlertas;

import RetailManagementSystem.vista.utilidades.UtilidadesLista;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class EditarServicioControlador {

    //ATRIBUTOS:

    @FXML private Button btnCancelar;
    @FXML private ComboBox<DescuentoDTO> cbDescuento;
    @FXML private ComboBox<ImpuestoDTO> cbImpuesto;
    @FXML private Label lblCodigoServicio;
    @FXML private Label lblNombreServicio;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecioBase;

    private ObservableList<ServicioDTO> listaObservable;

    private ServicioDTO servicioSeleccionado;

    private final OrquestadorServicios orquestadorServicios;

    //CONSTRUCTOR:

    public EditarServicioControlador(OrquestadorServicios orquestadorServicios) {
        this.orquestadorServicios = orquestadorServicios;
    }

    //MÉTODOS:

    private Window getVentana(){
        return btnCancelar.getScene().getWindow();
    }


    public void cargarDatos(
            ServicioDTO seleccionado, ObservableList<ServicioDTO> listaObservable, List<ImpuestoDTO> listaImpuestos,
            List<DescuentoDTO> listaDescuentos
    ){
        if (seleccionado==null){
            throw new IllegalArgumentException("NO puedes editar un Servicio Nulo");
        }
        this.listaObservable = listaObservable;
        this.servicioSeleccionado = seleccionado;
        lblNombreServicio.setText(seleccionado.nombre());
        lblCodigoServicio.setText(seleccionado.codigo());
        txtNombre.setText(seleccionado.nombre());
        txtPrecioBase.setText(seleccionado.precioBase().toString());
        configurarComboBox(
                cbImpuesto, listaImpuestos, "Seleccione un Impuesto...",
                imp -> imp.nombre() + " (" + imp.porcentaje() + "%)"
        );
        listaImpuestos.stream().filter(imp ->
                imp.idImpuesto() == seleccionado.datosImpuesto().idImpuesto()).findFirst().ifPresent(
                cbImpuesto.getSelectionModel()::select
        );
        configurarComboBox(
                cbDescuento, listaDescuentos, "Seleccione un Descuento...",
                desc -> desc.nombre() + " (" + desc.porcentaje() + "%)"
        );
        listaDescuentos.stream().filter(desc ->
                desc.idDescuento() == seleccionado.datosDescuento().idDescuento()).findFirst().ifPresent(
                cbDescuento.getSelectionModel()::select
        );
        Platform.runLater(()-> btnCancelar.requestFocus());
    }

    private <T> void configurarComboBox(
            ComboBox<T> comboBox, List<T> items, String prompt, Function<T, String> extractorTexto
    ) {
        comboBox.setItems(FXCollections.observableArrayList(items));
        comboBox.setPromptText(prompt);
        comboBox.setMaxWidth(Double.MAX_VALUE);
        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(T item) {
                return (item == null) ? "" : extractorTexto.apply(item);
            }
            @Override
            public T fromString(String string) {
                return null;
            }
        });
    }


    @FXML
    void accionActualizar(ActionEvent event) {
        String nuevoNombre = txtNombre.getText().trim();
        String nuevoPrecioBaseTexto = txtPrecioBase.getText().trim();
        ImpuestoDTO impuestoSeleccionado = cbImpuesto.getValue();
        DescuentoDTO descuentoSeleccionado = cbDescuento.getValue();
        if (nuevoNombre.isEmpty() || nuevoPrecioBaseTexto.isEmpty()){
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Datos Incompletos", null,
                    "El Nombre y el Precio Base son Obligatorios. Escribelos por favor"
            );
            return;
        }
        BigDecimal nuevoPrecioBase;
        try {
            nuevoPrecioBase = FormateadorNumeros.stringAPrecio(nuevoPrecioBaseTexto);
        } catch (IllegalArgumentException e){
            GestorAlertas.mostrarAlertaError(
                    getVentana(), "Precio Base Invalido", null,
                    "Escribe un Precio Base Valido.\n" +
                            e.getMessage()
            );
            return;
        }
        if (impuestoSeleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Datos Incompletos", null,
                    "Por favor selecciona un Impuesto."
            );
            return;
        }
        if (descuentoSeleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Datos Incompletos", null,
                    "Por favor selecciona un Descuento."
            );
            return;
        }
        CompletableFuture.supplyAsync(()->
                this.orquestadorServicios.actualizarServicio(
                        this.servicioSeleccionado.codigo(), nuevoNombre, nuevoPrecioBase, impuestoSeleccionado.idImpuesto(),
                        descuentoSeleccionado.idDescuento(), LocalDate.now()
                )
        ).thenAccept(servicioActualizado->
            Platform.runLater(()->{
                UtilidadesLista.reemplazarPorIdentidad(
                        listaObservable,
                        servicioActualizado,
                        item-> item.codigo().equals(servicioActualizado.codigo())
                );
                GestorAlertas.mostrarAlertaInformacion(
                        getVentana(), "Éxito", null,
                        "El Servicio se ha Actualizado con Éxito"
                );
                cerrarPantalla();
            })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                if (causa instanceof IllegalArgumentException){
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Error en los Datos Ingresados", null,
                            "Error:  " + causa.getMessage()
                    );
                } else {
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Error Critico",
                            "NO se pudo Completar la Acción.",
                            "Notificale al Administrador este Error:\n" + causa.getMessage()
                    );
                }
            });
            return null;
        });
    }

    private void cerrarPantalla(){
        Stage stageActual = (Stage) getVentana();
        stageActual.close();
    }


    @FXML
    void accionCancelar(ActionEvent event) {
        cerrarPantalla();
    }

}//===================================================================================================================//

