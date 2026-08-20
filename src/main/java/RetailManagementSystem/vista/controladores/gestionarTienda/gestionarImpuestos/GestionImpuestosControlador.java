package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarImpuestos;

import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorImpuestos;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import RetailManagementSystem.vista.utilidades.UtilidadesLista;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

public class GestionImpuestosControlador {

    //ATRIBUTOS:

    @FXML private TableView<ImpuestoDTO> tablaImpuestos;
    @FXML private TableColumn<ImpuestoDTO, Integer> colId;
    @FXML private TableColumn<ImpuestoDTO, String > colNombre;
    @FXML private TableColumn<ImpuestoDTO, BigDecimal> colPorcentaje;
    @FXML private TableColumn<ImpuestoDTO, String> colEstado;
    @FXML private TextField txtBuscar;

    private final OrquestadorImpuestos orquestadorImpuestos;

    private final ObservableList<ImpuestoDTO> listaObservableImpuestos = FXCollections.observableArrayList();

    //CONSTRUCTOR:

    public GestionImpuestosControlador(OrquestadorImpuestos orquestadorImpuestos) {
        this.orquestadorImpuestos = orquestadorImpuestos;
    }

    //MÉTODOS:

    private Window getVentana(){
        return tablaImpuestos.getScene().getWindow();
    }


    @FXML
    public void initialize() {
        configurarColumnasTabla();
        configurarFiltroBusqueda();
        cargarDatosTabla();
    }

    private void configurarColumnasTabla() {
        colId.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().idImpuesto())
        );
        colNombre.setCellValueFactory(celda -> new SimpleStringProperty(
                celda.getValue().nombre())
        );
        colPorcentaje.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().porcentaje())
        );
        colEstado.setCellValueFactory(celda -> {
            boolean esActivo = celda.getValue().activo();
            String textoEstado = esActivo ? "Activo" : "Inactivo";
            return new SimpleStringProperty(textoEstado);
        });
        colEstado.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                getStyleClass().removeAll("estado-activo", "estado-inactivo");
                if (empty || estado == null) {
                    setText(null);
                } else {
                    setText(estado);
                    String estiloCss = estado.equalsIgnoreCase("Activo") ? "estado-activo" : "estado-inactivo";
                    getStyleClass().add(estiloCss);
                }
            }
        });
    }

    private void configurarFiltroBusqueda() {
        FilteredList<ImpuestoDTO> listaFiltrada = new FilteredList<>(
                listaObservableImpuestos, b -> true
        );
        txtBuscar.textProperty().addListener((observable, valorViejo, valorNuevo) -> {
            listaFiltrada.setPredicate(impuesto -> {
                if (valorNuevo == null || valorNuevo.isBlank()) {
                    return true;
                }
                String filtro = valorNuevo.toLowerCase();
                return String.valueOf(impuesto.idImpuesto()).contains(filtro) ||
                        impuesto.nombre().toLowerCase().contains(filtro);
            });
        });
        SortedList<ImpuestoDTO> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaImpuestos.comparatorProperty());
        tablaImpuestos.setItems(listaOrdenada);
    }

    private void cargarDatosTabla() {
        CompletableFuture.supplyAsync(
                this.orquestadorImpuestos::obtenerTodosLosImpuestos
        ).thenAccept(listaImpuestos ->
            Platform.runLater(()-> {
                listaObservableImpuestos.setAll(listaImpuestos);
            })
        ).exceptionally(ex ->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error Critico",
                        "NO se pudo Completar la Acción.",
                        "Notificale al Administrador este Error:\n" + causa.getMessage()
                );
                Stage stageActual = (Stage) getVentana();
                CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTIONAR_TIENDA_VIEW);
            });
            return null;
        });
    }


    @FXML
    void abrirFormularioEdicion(ActionEvent event) {
        ImpuestoDTO seleccionado = tablaImpuestos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Atención", null,
                    "Por favor, Selecciona un Impuesto de la Tabla para Modificarlo."
            );
            return;
        }
        CargadorVistas.abrirModalConInyeccion(
                RutasVista.EDITAR_IMPUESTO_VIEW,
                "Editando Impuesto", getVentana(),
                (EditarImpuestoControlador c)->{
                    c.cargarDatos(seleccionado, listaObservableImpuestos);
                }
        );
    }


    @FXML
    void abrirFormularioNuevo(ActionEvent event) {
        CargadorVistas.abrirModalConInyeccion(
                RutasVista.CREAR_IMPUESTO_VIEW,
                "Creando Impuesto", getVentana(),
                (CrearImpuestoControlador c)->{
                    c.cargarDatos(listaObservableImpuestos);
                }
        );
    }


    @FXML
    void cambiarEstadoImpuesto(ActionEvent event) {
        cambiarEstadoImpuesto();
    }

    private void cambiarEstadoImpuesto(){
        ImpuestoDTO impuestoSeleccionado = tablaImpuestos.getSelectionModel().getSelectedItem();
        if (impuestoSeleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Atención", null,
                    "Por favor, selecciona un Impuesto de la Tabla para cambiar su Estado."
            );
            return;
        }
        if (!GestorAlertas.mostrarConfirmacion(getVentana(), "Confirmar", null,
                "¿Estás Seguro de Cambiar el Estado del Impuesto?")) {
            return;
        }
        CompletableFuture.runAsync(()-> {
            this.orquestadorImpuestos.cambiarEstadoImpuesto(impuestoSeleccionado.idImpuesto());
        }).thenRun(()->{
            Platform.runLater(()->{
                GestorAlertas.mostrarAlertaInformacion(
                        getVentana(), "Éxito", null,
                        "El Estado se ha Actualizado Correctamente."
                );
                ImpuestoDTO actualizado = new ImpuestoDTO(
                        impuestoSeleccionado.idImpuesto(),
                        impuestoSeleccionado.nombre(),
                        impuestoSeleccionado.porcentaje(),
                        !impuestoSeleccionado.activo()
                );
                UtilidadesLista.reemplazarPorIdentidad(
                        listaObservableImpuestos,
                        actualizado,
                        item -> item.idImpuesto() == actualizado.idImpuesto()
                );
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error Critico",
                        "NO se pudo Completar la Acción.",
                        "Notificale al Administrador este Error:\n" + causa.getMessage()
                );
            });
            return null;
        });
    }


    @FXML
    void volverAlPanel(ActionEvent event) {
        Stage stageActual = (Stage) getVentana();
        CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTIONAR_TIENDA_VIEW);
    }


}//===================================================================================================================//

