package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarDescuentos;

import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorDescuentos;
import RetailManagementSystem.vista.excepciones.CargarVistaException;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.FormateadorNumeros;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.application.Platform;
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
import javafx.scene.layout.GridPane;
import javafx.scene.control.TableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
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
        ).thenAccept(listaDescuentos -> {
            Platform.runLater(() -> {
                listaObservableDescuentos.setAll(listaDescuentos);
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                GestorAlertas.mostrarAlertaError("Error Critico",
                        "NO se pudo Completar la Acción.",
                        "Notificale al Administrador este Error:\n" +
                        ex.getMessage());
                Stage stageActual = (Stage) tablaDescuentos.getScene().getWindow();
                CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTIONAR_TIENDA_VIEW);
            });
            return null;
        });
    }


    private Dialog<ButtonType> crearDialogo(String titulo, String cabecera, String textoBotonAccion) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.setHeaderText(cabecera);
        DialogPane dialogPane = dialog.getDialogPane();
        //aplicarCSS(dialogPane); Metodo que ya borramos y que me hizo un desorden en el css
        ButtonType btnAccion = new ButtonType(textoBotonAccion, ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(btnAccion, ButtonType.CANCEL);
        return dialog;
    }

    private GridPane crearGridPane(){
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 20, 20));
        return grid;
    }

    private void validarCampos(Dialog<ButtonType> dialog, TextField campoNombre, TextField campoPorcentaje){
        ButtonType btnTipoGuardar = dialog.getDialogPane().getButtonTypes().stream()
                .filter(b -> b.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                .findFirst().orElse(null);
        Button botonFisicoGuardar = (Button) dialog.getDialogPane().lookupButton(btnTipoGuardar);
        botonFisicoGuardar.addEventFilter(ActionEvent.ACTION, event -> {
            String nombre = campoNombre.getText().trim();
            String porcentajeTexto = campoPorcentaje.getText().trim();
            if (nombre.isEmpty()) {
                GestorAlertas.mostrarAlertaError(
                        "Error de Validación", null,
                        "El Nombre del Descuento NO puede estar Vacío."
                );
                event.consume();
                return;
            }
            if (porcentajeTexto.isEmpty()) {
                GestorAlertas.mostrarAlertaError(
                        "Error de Validación", null,
                        "El Porcentaje del Descuento NO puede estar Vacío"
                );
                event.consume();
                return;
            }
            try {
                FormateadorNumeros.stringAPorcentaje(porcentajeTexto);
            } catch (NumberFormatException e) {
                GestorAlertas.mostrarAlertaWarning(
                        "Número Inválido", null,
                        "Error al Ingresar el Porcentaje:\n" + e.getMessage()
                );
                event.consume();
            }
        });
    }


    @FXML
    void abrirFormularioEdicion(ActionEvent event) {
        DescuentoDTO seleccionado = tablaDescuentos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    "Atención", null,
                    "Por favor, Selecciona un Descuento de la Tabla para Modificarlo."
            );
            return;
        }
        String rutaFxml = RutasVista.EDITAR_DESCUENTO_VIEW;
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(rutaFxml);
            Parent root = loader.load();
            EditarDescuentoControlador controlador = loader.getController();
            controlador.cargarDatos(seleccionado, listaObservableDescuentos);
            Stage stageEdicion = new Stage();
            stageEdicion.setTitle("Editando Descuento");
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
        String rutaFxml = RutasVista.CREAR_DESCUENTO_VIEW;
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(rutaFxml);
            Parent root = loader.load();
            CrearDescuentoControlador controlador = loader.getController();
            controlador.cargarDatos(listaObservableDescuentos);
            Stage stageEdicion = new Stage();
            stageEdicion.setTitle("Creando Descuento");
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
    void cambiarEstadoDescuento(ActionEvent event) {
        cambiarEstadoDescuento();
    }

    private void cambiarEstadoDescuento(){
        DescuentoDTO descuentoSeleccionado = tablaDescuentos.getSelectionModel().getSelectedItem();
        if (descuentoSeleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    "Atención", null,
                    "Por favor, selecciona un Descuento de la Tabla para cambiar su Estado."
            );
            return;
        }
        if (!GestorAlertas.mostrarConfirmacion("Confirmar", null,
                "¿Estás Seguro de Cambiar el Estado del Descuento?")) {
            return;
        }
        CompletableFuture.runAsync(()-> {
            this.orquestadorDescuentos.cambiarEstadoDescuento(descuentoSeleccionado.idDescuento());
        }).thenRun(()->{
            Platform.runLater(()->{
                GestorAlertas.mostrarAlertaInformacion(
                        "Éxito", null,
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
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                GestorAlertas.mostrarAlertaError("Error Critico",
                        "NO se pudo Completar la Acción.",
                        "Notificale al Administrador este Error:\n" +
                                ex.getMessage());
            });
            return null;
        });
    }


    @FXML
    private void volverAlPanel(ActionEvent event) {
        Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTIONAR_TIENDA_VIEW);
    }


} //==================================================================================================================//

