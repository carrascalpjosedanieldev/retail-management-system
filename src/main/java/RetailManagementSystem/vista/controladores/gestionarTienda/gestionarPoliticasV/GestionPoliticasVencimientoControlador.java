package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarPoliticasV;

import RetailManagementSystem.aplicacion.dto.gestion.PoliticaVencimientoDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorPoliticaVencimiento;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

public class GestionPoliticasVencimientoControlador {

    //ATRIBUTOS:

    @FXML private TableView<PoliticaVencimientoDTO> tablaPoliticasVencimiento;
    @FXML private TableColumn<PoliticaVencimientoDTO, Integer> colId;
    @FXML private TableColumn<PoliticaVencimientoDTO, String> colNombre;
    @FXML private TableColumn<PoliticaVencimientoDTO, Integer> colDiasUmbral;
    @FXML private TableColumn<PoliticaVencimientoDTO, BigDecimal> colPorcentaje;
    @FXML private TableColumn<PoliticaVencimientoDTO, String> colEstado;
    @FXML private TextField txtBuscar;

    private final OrquestadorPoliticaVencimiento orquestadorPoliticaVencimiento;

    private final ObservableList<PoliticaVencimientoDTO> listaObservablePoliticasVencimiento = FXCollections.observableArrayList();

    //CONSTRUCTOR:

    public GestionPoliticasVencimientoControlador(OrquestadorPoliticaVencimiento orquestadorPoliticaVencimiento) {
        this.orquestadorPoliticaVencimiento = orquestadorPoliticaVencimiento;
    }

    //MÉTODOS:

    @FXML
    public void initialize() {
        configurarColumnasTabla();
        configurarFiltroBusqueda();
        cargarDatosTabla();
    }

    private void configurarColumnasTabla() {
        colId.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().idPoliticaVencimiento())
        );
        colNombre.setCellValueFactory(celda -> new SimpleStringProperty(
                celda.getValue().nombrePolitica())
        );
        colDiasUmbral.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().diasUmbral())
        );
        colPorcentaje.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().porcentajeDescuento())
        );
        colEstado.setCellValueFactory(celda -> {
            boolean esActiva = celda.getValue().activo();
            String textoEstado = esActiva ? "Activa" : "Inactiva";
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
                    String estiloCss = estado.equalsIgnoreCase("Activa") ? "estado-activo" : "estado-inactivo";
                    getStyleClass().add(estiloCss);
                }
            }
        });
    }

    private void configurarFiltroBusqueda() {
        FilteredList<PoliticaVencimientoDTO> listaFiltrada = new FilteredList<>(
                listaObservablePoliticasVencimiento, b -> true
        );
        txtBuscar.textProperty().addListener((observable, valorViejo, valorNuevo) -> {
            listaFiltrada.setPredicate(politicaVencimiento -> {
                if (valorNuevo == null || valorNuevo.isBlank()) {
                    return true;
                }
                String filtro = valorNuevo.toLowerCase();
                return String.valueOf(politicaVencimiento.idPoliticaVencimiento()).contains(filtro) ||
                        politicaVencimiento.nombrePolitica().toLowerCase().contains(filtro);
            });
        });
        SortedList<PoliticaVencimientoDTO> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaPoliticasVencimiento.comparatorProperty());
        tablaPoliticasVencimiento.setItems(listaOrdenada);
    }

    private void cargarDatosTabla() {
        CompletableFuture.supplyAsync(
                this.orquestadorPoliticaVencimiento::obtenerTodasLasPoliticasV
        ).thenAccept(listaPoliticasV -> {
            Platform.runLater(()->{
                listaObservablePoliticasVencimiento.setAll(listaPoliticasV);
            });
        }).exceptionally(ex ->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        "Error Critico",
                        "NO se pudo Completar la Acción.",
                        "Notificale al Administrador este Error:\n" + causa.getMessage()
                );
                Stage stageActual = (Stage) tablaPoliticasVencimiento.getScene().getWindow();
                CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTIONAR_TIENDA_VIEW);
            });
            return null;
        });
    }


    @FXML
    void abrirFormularioEdicion(ActionEvent event) {
        PoliticaVencimientoDTO seleccionado = tablaPoliticasVencimiento.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    "Atención", null,
                    "Por favor, Selecciona una Política de Vencimiento de la Tabla para Modificarlo."
            );
            return;
        }
        String rutaFxml = RutasVista.EDITAR_POLITICA_V_VIEW;
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(rutaFxml);
            Parent root = loader.load();
            EditarPoliticaVencimientoControlador controlador = loader.getController();
            controlador.cargarDatos(seleccionado, listaObservablePoliticasVencimiento);
            Stage stageEdicion = new Stage();
            stageEdicion.setTitle("Editando Política de Vencimiento");
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
        String rutaFxml = RutasVista.CREAR_POLITiCA_V_VIEW;
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(rutaFxml);
            Parent root = loader.load();
            CrearPoliticaVencimiento controlador = loader.getController();
            controlador.cargarDatos(listaObservablePoliticasVencimiento);
            Stage stageEdicion = new Stage();
            stageEdicion.setTitle("Creando Política de Vencimiento");
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
    void cambiarEstadoPoliticaV(ActionEvent event) {
        cambiarEstadoPoliticaV();
    }

    private void cambiarEstadoPoliticaV(){
        PoliticaVencimientoDTO politicaSeleccionado = tablaPoliticasVencimiento.getSelectionModel().getSelectedItem();
        if (politicaSeleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    "Atención", null,
                    "Por favor, Selecciona una Política de Vencimiento de la Tabla para Cambiar su Estado."
            );
            return;
        }
        if (!GestorAlertas.mostrarConfirmacion("Confirmar", null,
                "¿Estás Seguro de Cambiar el Estado de la Política de Vencimiento?")) {
            return;
        }

        CompletableFuture.runAsync(()->{
            this.orquestadorPoliticaVencimiento.cambiarEstadoPoliticaV(politicaSeleccionado.idPoliticaVencimiento());
        }).thenRun(()->{
            Platform.runLater(()->{
                GestorAlertas.mostrarAlertaInformacion(
                        "Éxito", null,
                        "El Estado se ha Actualizado Correctamente."
                );
                PoliticaVencimientoDTO actualizado = new PoliticaVencimientoDTO(
                        politicaSeleccionado.idPoliticaVencimiento(),
                        politicaSeleccionado.nombrePolitica(),
                        politicaSeleccionado.diasUmbral(),
                        politicaSeleccionado.porcentajeDescuento(),
                        !politicaSeleccionado.activo()
                );
                int indice = listaObservablePoliticasVencimiento.indexOf(politicaSeleccionado);
                listaObservablePoliticasVencimiento.set(indice, actualizado);
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        "Error Critico",
                        "NO se pudo Completar la Acción.",
                        "Notificale al Administrador este Error:\n" + causa.getMessage()
                );
            });
            return null;
        });
    }


    @FXML
    void volverAlPanel(ActionEvent event) {
        Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTIONAR_TIENDA_VIEW);
    }


}//===================================================================================================================//

