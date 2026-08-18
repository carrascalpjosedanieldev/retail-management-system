package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarDescuentos;

import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorDescuentos;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Window;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

public class GestionDescuentosControlador {

    //ATRIBUTOS:

    @FXML private TableView<DescuentoDTO> tablaDescuentos;
    @FXML private TableColumn<DescuentoDTO, Integer> colId;
    @FXML private TableColumn<DescuentoDTO, String> colNombre;
    @FXML private TableColumn<DescuentoDTO, BigDecimal> colPorcentaje;
    @FXML private TableColumn<DescuentoDTO, String> colEstado;
    @FXML private TextField txtBuscar;

    private final OrquestadorDescuentos orquestadorDescuentos;

    private final ObservableList<DescuentoDTO> listaObservableDescuentos = FXCollections.observableArrayList();

    //CONSTRUCTOR:

    public GestionDescuentosControlador(OrquestadorDescuentos orquestadorDescuentos) {
        this.orquestadorDescuentos = orquestadorDescuentos;
    }

    //MÉTODOS:

    private Window getVentana(){
        return tablaDescuentos.getScene().getWindow();
    }


    @FXML
    public void initialize() {
        configurarColumnasTabla();
        configurarFiltroBusqueda();
        cargarDatosTabla();
    }

    private void configurarColumnasTabla(){
        colId.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().idDescuento())
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
        colEstado.setCellFactory(columna -> new TableCell<DescuentoDTO, String>() {
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

    private void configurarFiltroBusqueda(){
        FilteredList<DescuentoDTO> listaFiltrada = new FilteredList<>(
                listaObservableDescuentos, b -> true
        );
        txtBuscar.textProperty().addListener((observable, valorViejo, valorNuevo) -> {
            listaFiltrada.setPredicate(descuento -> {
                if (valorNuevo == null || valorNuevo.isBlank()) {
                    return true;
                }
                String filtro = valorNuevo.toLowerCase().trim();
                return String.valueOf(descuento.idDescuento()).contains(filtro) ||
                        descuento.nombre().toLowerCase().contains(filtro);
            });
        });
        SortedList<DescuentoDTO> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaDescuentos.comparatorProperty());
        tablaDescuentos.setItems(listaOrdenada);
    }

    private void cargarDatosTabla() {
        CompletableFuture.supplyAsync(
                this.orquestadorDescuentos::obtenerTodosLosDescuentos
        ).thenAccept(listaDescuentos ->
            Platform.runLater(() ->
                    listaObservableDescuentos.setAll(listaDescuentos)
            )
        ).exceptionally(ex -> {
            Platform.runLater(() -> {
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
        DescuentoDTO seleccionado = tablaDescuentos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Atención", null,
                    "Por favor, Selecciona un Descuento de la Tabla para Modificarlo."
            );
            return;
        }
        CargadorVistas.abrirModalInyectada(
                RutasVista.EDITAR_DESCUENTO_VIEW,
                "Editando Descuento", getVentana(),
                (EditarDescuentoControlador c)->{
                    c.cargarDatos(seleccionado, listaObservableDescuentos);
                }
        );
    }


    @FXML
    void abrirFormularioNuevo(ActionEvent event) {
        CargadorVistas.abrirModalInyectada(
                RutasVista.CREAR_DESCUENTO_VIEW,
                "Creando Descuento", getVentana(),
                (CrearDescuentoControlador c)->{
                    c.cargarDatos(listaObservableDescuentos);
                }
        );
    }


    @FXML
    void cambiarEstadoDescuento(ActionEvent event) {
        cambiarEstadoDescuento();
    }

    private void cambiarEstadoDescuento(){
        DescuentoDTO descuentoSeleccionado = tablaDescuentos.getSelectionModel().getSelectedItem();
        if (descuentoSeleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Atención", null,
                    "Por favor, selecciona un Descuento de la Tabla para cambiar su Estado."
            );
            return;
        }
        if (!GestorAlertas.mostrarConfirmacion(getVentana(), "Confirmar", null,
                "¿Estás Seguro de Cambiar el Estado del Descuento?")) {
            return;
        }
        CompletableFuture.runAsync(()->
            this.orquestadorDescuentos.cambiarEstadoDescuento(descuentoSeleccionado.idDescuento())
        ).thenRun(()->
            Platform.runLater(()->{
                GestorAlertas.mostrarAlertaInformacion(
                        getVentana(), "Éxito", null,
                        "El Estado se ha Actualizado Correctamente."
                );
                DescuentoDTO actualizado = new DescuentoDTO(
                        descuentoSeleccionado.idDescuento(),
                        descuentoSeleccionado.nombre(),
                        descuentoSeleccionado.porcentaje(),
                        !descuentoSeleccionado.activo()
                );
                int indice = listaObservableDescuentos.indexOf(descuentoSeleccionado);
                listaObservableDescuentos.set(indice, actualizado);
            })
        ).exceptionally(ex -> {
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
    private void volverAlPanel(ActionEvent event) {
        Stage stageActual = (Stage) getVentana();
        CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTIONAR_TIENDA_VIEW);
    }


} //==================================================================================================================//

