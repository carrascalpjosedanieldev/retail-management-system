package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarServicios;

import RetailManagementSystem.aplicacion.dto.comercial.ServicioDTO;
import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorServicios;
import RetailManagementSystem.vista.utilidades.FormateadorNumeros;
import RetailManagementSystem.vista.utilidades.GestorAlertas;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class CrearServicioControlador {

    //ATRIBUTOS:

    @FXML private Button btnCancelar;
    @FXML private ComboBox<DescuentoDTO> cbDescuento;
    @FXML private ComboBox<ImpuestoDTO> cbImpuesto;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecioBase;

    private ObservableList<ServicioDTO> listaObservable;

    private final OrquestadorServicios orquestadorServicios;

    //CONSTRUCTOR:

    public CrearServicioControlador(OrquestadorServicios orquestadorServicios) {
        this.orquestadorServicios = orquestadorServicios;
    }

    //MÉTODOS:

    private Window getVentana(){
        return btnCancelar.getScene().getWindow();
    }


    public void cargarDatos(
            ObservableList<ServicioDTO> listaObservable, List<ImpuestoDTO> listaImpuestos,
            List<DescuentoDTO> listaDescuentos
    ){
        this.listaObservable = listaObservable;
        configurarComboBox(cbImpuesto, listaImpuestos, "Seleccione un Impuesto...",
                imp -> imp.nombre() + " (" + imp.porcentaje() + "%)");
        configurarComboBox(cbDescuento, listaDescuentos, "Seleccione un Descuento...",
                desc -> desc.nombre() + " (" + desc.porcentaje() + "%)");
        Platform.runLater(()->btnCancelar.requestFocus());
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
    void accionGuardar(ActionEvent event) {
        guardarServicio();
    }

    private void guardarServicio(){
        String nombre = txtNombre.getText().trim();
        String  precioBaseTexto = txtPrecioBase.getText().trim();
        ImpuestoDTO impuestoSeleccionado = cbImpuesto.getValue();
        DescuentoDTO descuentoSeleccionado = cbDescuento.getValue();
        if (nombre.isEmpty() || precioBaseTexto.isEmpty()){
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Datos Incompletos",
                    "El Nombre y el Precio Base son Obligatorios.",
                    "Por favor escribe un Nombre y un Precio Base Validos."
            );
            return;
        }
        BigDecimal precioBase;
        try {
            precioBase = FormateadorNumeros.stringAPrecio(precioBaseTexto);
        } catch (IllegalArgumentException e){
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Precio Invalido", null,
                    "Por favor, escribe un Precio Base Valido.\n" +
                            "Error:  " + e.getMessage()
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
                this.orquestadorServicios.registrarServicio(
                        nombre, precioBase, impuestoSeleccionado.idImpuesto(), descuentoSeleccionado.idDescuento(), LocalDate.now()
                )
        ).thenAccept(servicioRegistrado->
                Platform.runLater(()->{
                    listaObservable.add(servicioRegistrado);
                    GestorAlertas.mostrarAlertaInformacion(
                            getVentana(), "Éxito", null,
                            "El Servicio ha sido Registrado Correctamente."
                    );
                    cerrarPantalla();
                })
        ).exceptionally(ex->{
            Platform.runLater(() -> {
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

