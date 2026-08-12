package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarImpuestos;

import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorImpuestos;
import RetailManagementSystem.dominio.excepciones.ImpuestoNoEncontradoException;
import RetailManagementSystem.vista.excepciones.CargarVistaException;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
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

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        DialogPane panelAlerta = alerta.getDialogPane();
        panelAlerta.setMinHeight(Region.USE_PREF_SIZE);
        //aplicarCSS(panelAlerta);
        alerta.showAndWait();
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
        FilteredList<ImpuestoDTO> listaFiltrada = new FilteredList<>(listaObservableImpuestos, b -> true);
        txtBuscar.textProperty().addListener((observable, valorViejo, valorNuevo) -> {
            listaFiltrada.setPredicate(impuesto -> {
                if (valorNuevo == null || valorNuevo.isBlank()) return true;
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
        ).thenAccept(listaImpuestos -> {
            Platform.runLater(()-> {
                listaObservableImpuestos.setAll(listaImpuestos);
            });
        }).exceptionally(ex ->{
            Platform.runLater(()->{
                GestorAlertas.mostrarAlertaError(
                        "Error Critico",
                        "NO se pudo Completar la Acción.",
                        "Notificale al Administrador este Error:\n" + ex.getMessage()
                );
                Stage stageActual = (Stage) tablaImpuestos.getScene().getWindow();
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
                    "Atención", null,
                    "Por favor, Selecciona un Impuesto de la Tabla para Modificarlo."
            );
            return;
        }
        String rutaFxml = RutasVista.EDITAR_IMPUESTO_VIEW;
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(rutaFxml);
            Parent root = loader.load();
            EditarImpuestoControlador controlador = loader.getController();
            controlador.cargarDatos(seleccionado, listaObservableImpuestos);
            Stage stageEdicion = new Stage();
            stageEdicion.setTitle("Editando Impuesto");
            stageEdicion.initModality(Modality.APPLICATION_MODAL);
            stageEdicion.setResizable(false);
            Scene escenaEdicion = new Scene(root);
            stageEdicion.setScene(escenaEdicion);
            stageEdicion.showAndWait();
        } catch (IOException e) {
            throw new CargarVistaException(rutaFxml, "NO se pudo Cargar el Archivo FXML.", e);
        }
    }


    @FXML
    void abrirFormularioNuevo(ActionEvent event) {
        String rutaFxml = RutasVista.CREAR_IMPUESTO_VIEW;
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(rutaFxml);
            Parent root = loader.load();
            CrearImpuestoControlador controlador = loader.getController();
            controlador.cargarDatos(listaObservableImpuestos);
            Stage stageEdicion = new Stage();
            stageEdicion.setTitle("Creando Impuesto");
            stageEdicion.initModality(Modality.APPLICATION_MODAL);
            stageEdicion.setResizable(false);
            Scene escenaEdicion = new Scene(root);
            stageEdicion.setScene(escenaEdicion);
            stageEdicion.showAndWait();
        } catch (IOException e) {
            throw new CargarVistaException(rutaFxml, "NO se pudo Cargar el Archivo FXML.", e);
        }
    }


    @FXML
    void cambiarEstadoImpuesto(ActionEvent event) {
        cambiarEstadoImpuesto();
    }

    private void cambiarEstadoImpuesto(){
        ImpuestoDTO impuestoSeleccionado = tablaImpuestos.getSelectionModel().getSelectedItem();
        if (impuestoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención",
                    "Por favor, Selecciona un Impuesto de la Tabla para Cambiar su Estado.");
            return;
        }
        //boolean esActivo = impuestoSeleccionado.activo().equalsIgnoreCase("Activo");
        //String accion = esActivo ? "Desactivar" : "Activar";
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar cambio de activo");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de que deseas " + " el Impuesto -" + impuestoSeleccionado.nombre() + "-?");
        DialogPane panelConfirmacion = confirmacion.getDialogPane();
        panelConfirmacion.setMinHeight(Region.USE_PREF_SIZE);
        //aplicarCSS(panelConfirmacion);
        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            try {
                this.orquestadorImpuestos.cambiarEstadoImpuesto(impuestoSeleccionado.idImpuesto());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                        "El Estado se ha Actualizado Correctamente.");
                cargarDatosTabla();
            } catch (IllegalArgumentException | IllegalStateException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "La Acción NO fue Completada",
                        "Error:  " + e.getMessage());
            } catch (ImpuestoNoEncontradoException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Impuesto NO Encontrado",
                        "Error:  " + e.getMessage());
            }
        }
    }


    @FXML
    void volverAlPanel(ActionEvent event) {
        Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTIONAR_TIENDA_VIEW);
    }


}//===================================================================================================================//

