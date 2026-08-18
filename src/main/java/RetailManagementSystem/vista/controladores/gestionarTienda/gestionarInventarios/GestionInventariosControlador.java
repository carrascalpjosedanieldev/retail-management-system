package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios;

import RetailManagementSystem.aplicacion.dto.gestion.InventarioDTO;
import RetailManagementSystem.aplicacion.servicios.ServicioInventario;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOInventario;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos.GestionProductosControlador;
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
import javafx.stage.Window;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class GestionInventariosControlador {

    @FXML private TableColumn<InventarioDTO, Integer> colCapacidadLibre;
    @FXML private TableColumn<InventarioDTO, Integer> colCapacidadMax;
    @FXML private TableColumn<InventarioDTO, Integer> colCapacidadOcupada;
    @FXML private TableColumn<InventarioDTO, Integer> colId;
    @FXML private TableColumn<InventarioDTO, String> colNombre;
    @FXML private TableView<InventarioDTO> tablaInventarios;
    @FXML private TextField txtBuscar;

    private final ServicioInventario servicioInventario;

    private final EnsambladorDTOInventario ensambladorDTOInventario;

    private final ObservableList<InventarioDTO> listaObservable = FXCollections.observableArrayList();

    //CONSTRUCTOR:

    public GestionInventariosControlador(
            ServicioInventario servicioInventario, EnsambladorDTOInventario ensambladorDTOInventario
    ) {
        this.servicioInventario = servicioInventario;
        this.ensambladorDTOInventario = ensambladorDTOInventario;
    }

    //MÉTODOS:

    private Window getVentana(){
        return tablaInventarios.getScene().getWindow();
    }


    @FXML
    public void initialize() {
        configurarColumnasTabla();
        configurarFiltroBusqueda();
        cargarDatosTabla();
    }

    private void configurarColumnasTabla(){
        colId.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().idInventario())
        );
        colNombre.setCellValueFactory(celda -> new SimpleStringProperty(
                celda.getValue().nombre())
        );
        colCapacidadMax.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().capacidadMaxima())
        );
        colCapacidadOcupada.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().capacidadOcupada())
        );
        colCapacidadLibre.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().capacidadLibre())
        );
        colCapacidadLibre.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(Integer libre, boolean empty) {
                super.updateItem(libre, empty);
                getStyleClass().removeAll("capacidad-agotada", "capacidad-baja", "capacidad-ok");
                if (empty || libre == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(libre));
                    if (libre <= 0) {
                        getStyleClass().add("capacidad-agotada");
                    } else if (libre <= 10) {
                        getStyleClass().add("capacidad-baja");
                    } else {
                        getStyleClass().add("capacidad-ok");
                    }
                }
            }
        });
    }

    private void configurarFiltroBusqueda(){
        FilteredList<InventarioDTO> listaFiltrada = new FilteredList<>(listaObservable, inv -> true);
        txtBuscar.textProperty().addListener((observable, valorViejo, valorNuevo) -> {
            if (valorNuevo == null || valorNuevo.isBlank()){
                listaFiltrada.setPredicate(inv -> true);
                return;
            }
            String filtro = valorNuevo.toLowerCase().trim();
            listaFiltrada.setPredicate(inv -> {
                String idComoTexto = String.valueOf(inv.idInventario());
                String nombre = inv.nombre() != null ? inv.nombre().toLowerCase() : "";
                return nombre.contains(filtro) || idComoTexto.contains(filtro);
            });
        });
        SortedList<InventarioDTO> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaInventarios.comparatorProperty());
        tablaInventarios.setItems(listaOrdenada);
    }

    private void cargarDatosTabla() {
        CompletableFuture.supplyAsync(() ->
            this.ensambladorDTOInventario.ensamblarDetalleInventarioGeneral(
                    this.servicioInventario.obtenerTodosLosInventarios()
            )
        ).thenAcceptAsync(
                listaObservable::setAll, Platform::runLater
        ).exceptionally(ex -> {
            Platform.runLater(() -> {
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error", null,
                        "No se pudieron cargar los inventarios: " + causa.getMessage()
                );
            });
            return null;
        });
    }


    @FXML
    void abrirFormularioEdicion(ActionEvent event) {
        abrirFormularioEdicion();
    }

    private void abrirFormularioEdicion(){
        InventarioDTO seleccionado = tablaInventarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Atención", null,
                    "Por favor, Selecciona un Inventario de la Tabla para Modificarlo."
            );
            return;
        }
        String rutaFxml = RutasVista.EDITAR_INVENTARIO_VIEW;
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(rutaFxml);
            Parent root = loader.load();
            EditarInventarioControlador controlador = loader.getController();
            controlador.cargarDatos(seleccionado, listaObservable);
            Stage stageEdicion = new Stage();
            stageEdicion.setTitle("Editando Inventario");
            stageEdicion.initModality(Modality.APPLICATION_MODAL);
            stageEdicion.setResizable(false);
            Scene escenaEdicion = new Scene(root);
            stageEdicion.setScene(escenaEdicion);
            stageEdicion.showAndWait();
        } catch (IOException e){
            throw new CargarVistaException(rutaFxml, "NO se pudo Cargar el Archivo FXML.", e);
        }
    }


    @FXML
    void abrirFormularioNuevo(ActionEvent event) {
        String rutaFxml = RutasVista.CREAR_INVENTARIO_VIEW;
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(rutaFxml);
            Parent root = loader.load();
            CrearInventarioControlador controlador = loader.getController();
            controlador.cargarDatos(listaObservable);
            Stage stageCrear = new Stage();
            stageCrear.setTitle("Creando Inventario");
            stageCrear.initModality(Modality.APPLICATION_MODAL);
            stageCrear.setResizable(false);
            Scene escenaCrear = new Scene(root);
            stageCrear.setScene(escenaCrear);
            stageCrear.showAndWait();
        } catch (IOException e){
            throw new CargarVistaException(rutaFxml, "NO se pudo Cargar el Archivo FXML.", e);
        }
    }


    @FXML
    void editarProductosInventario(ActionEvent event) {
        InventarioDTO seleccionado = tablaInventarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Atención", null,
                    "Selecciona un Inventario para ver sus Productos."
            );
            return;
        }
        String rutaFxml = RutasVista.GESTIONAR_PRODUCTOS_VIEW;
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(rutaFxml);
            Parent root = loader.load();
            GestionProductosControlador controlador = loader.getController();
            controlador.inicializarConInventario(seleccionado.idInventario());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException | IllegalStateException e) {
            throw new CargarVistaException(rutaFxml, "NO se pudo Cargar el Archivo FXML.", e);
        }
    }


    @FXML
    void volverAlPanel(ActionEvent event) {
        Stage stageActual = (Stage) getVentana();
        CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTIONAR_TIENDA_VIEW);
    }


}//===================================================================================================================//

