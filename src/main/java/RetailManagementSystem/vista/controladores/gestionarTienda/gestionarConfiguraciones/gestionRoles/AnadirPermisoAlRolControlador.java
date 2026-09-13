package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.gestionRoles;

import RetailManagementSystem.aplicacion.dto.seguridad.PermisoDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorPermisos;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;
import RetailManagementSystem.vista.configuracion.ConfiguradorExcepciones;
import RetailManagementSystem.vista.utilidades.GestorAlertas;

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
import javafx.scene.text.Text;
import javafx.stage.Window;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AnadirPermisoAlRolControlador {

    //ATRIBUTOS:

    @FXML private Button btnAnadir;
    @FXML private Button btnCancelar;
    @FXML private ComboBox<String> cbFiltroModulo;
    @FXML private TableColumn<PermisoDTO, String> colDescripcion;
    @FXML private TableColumn<PermisoDTO, String> colEstado;
    @FXML private TableColumn<PermisoDTO, Integer> colId;
    @FXML private TableColumn<PermisoDTO, String> colModulo;
    @FXML private TableColumn<PermisoDTO, String> colNombre;
    @FXML private TableView<PermisoDTO> tablaPermisos;
    @FXML private TextField txtBuscar;

    private Runnable notificadorCambios;

    private final OrquestadorPermisos orquestadorPermisos;

    private final ObservableList<PermisoDTO> listaObservable = FXCollections.observableArrayList();

    private final FilteredList<PermisoDTO> listaFiltrada = new FilteredList<>(listaObservable, p -> true);

    private List<PermisoDTO> permisosDelRol;

    private UsuarioDTOCompleto usuarioActual;

    //CONSTRUCTOR:

    public AnadirPermisoAlRolControlador(OrquestadorPermisos orquestadorPermisos) {
        this.orquestadorPermisos = orquestadorPermisos;
    }

    //MÉTODOS:

    private Window getVentana(){
        return btnCancelar.getScene() != null ? btnCancelar.getScene().getWindow() : null;
    }


    public void cargarDatos(
            UsuarioDTOCompleto usuarioActual, List<PermisoDTO> permisosDelRol, Runnable notificadorCambios
    ) {
        ValidadorSeguridad.exigirPermiso(usuarioActual, PermisosApp.EDITAR_ROLES);
        this.usuarioActual = usuarioActual;
        this.permisosDelRol = permisosDelRol;
        this.notificadorCambios = notificadorCambios;
        llenarTablaPermisosYComboBox();
    }


    @FXML
    public void initialize(){
        configurarColumnas();
        SortedList<PermisoDTO> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaPermisos.comparatorProperty());
        tablaPermisos.setItems(listaOrdenada);
        txtBuscar.textProperty().addListener(
                (observable, oldValue, newValue) -> aplicarFiltros()
        );
        cbFiltroModulo.valueProperty().addListener(
                (observable, oldValue, newValue) -> aplicarFiltros()
        );
    }

    private void configurarColumnas(){
        colId.setCellValueFactory(cellData-> new SimpleObjectProperty<>(
                cellData.getValue().idPermiso()
        ));
        colNombre.setCellValueFactory(cellData-> new SimpleStringProperty(
                cellData.getValue().nombre()
        ));
        colModulo.setCellValueFactory(cellData-> new SimpleStringProperty(
                cellData.getValue().modulo()
        ));
        colDescripcion.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().descripcion()
        ));
        colDescripcion.setCellFactory(columna -> {
            return new TableCell<PermisoDTO, String>() {
                private final Text textoMultiline = new Text();
                @Override
                protected void updateItem(String descripcion, boolean empty) {
                    super.updateItem(descripcion, empty);
                    if (empty || descripcion == null) {
                        setGraphic(null);
                    } else {
                        textoMultiline.setText(descripcion);
                        textoMultiline.wrappingWidthProperty().bind(columna.widthProperty().subtract(10));
                        setGraphic(textoMultiline);
                    }
                }
            };
        });
        colEstado.setCellValueFactory(cellData-> {
            boolean esActivo = cellData.getValue().activo();
            String estado = esActivo ? "Activo" : "Inactivo";
            return new SimpleStringProperty(estado);
        });
        colEstado.setCellFactory(columna-> new TableCell<PermisoDTO, String>(){
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

    private void llenarTablaPermisosYComboBox(){
        CompletableFuture.supplyAsync(
                this.orquestadorPermisos::obtenerTodosLosPermisos
        ).thenApply(todosLosPermisos -> {
            List<Integer> idsQueYaTiene = this.permisosDelRol.stream()
                    .map(PermisoDTO::idPermiso)
                    .toList();
            return todosLosPermisos.stream()
                    .filter(permiso -> !idsQueYaTiene.contains(permiso.idPermiso()))
                    .toList();
        }).thenAccept(permisosListosParaMostrar -> {
            Platform.runLater(() -> {
                listaObservable.setAll(permisosListosParaMostrar);
                List<String> modulosUnicos = permisosListosParaMostrar.stream()
                        .map(PermisoDTO::modulo)
                        .distinct()
                        .sorted()
                        .toList();
                cbFiltroModulo.getItems().clear();
                cbFiltroModulo.getItems().add("Todos");
                cbFiltroModulo.getItems().addAll(modulosUnicos);
                cbFiltroModulo.getSelectionModel().selectFirst();
            });
        }).exceptionally(ex -> {
            Platform.runLater(()->{
                Throwable causa = ConfiguradorExcepciones.obtenerCausaRaiz(ex);
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error Crítico",
                        "No se pudieron cargar los permisos",
                        "Hubo un fallo al conectar con la Base de Datos: " + causa.getMessage() + "\n" +
                                "Comunicate con el Administrador y Revisa tu conexión."
                );
                cerrarModal();
            });
            return null;
        });
    }

    private void aplicarFiltros() {
        String textoBusqueda = txtBuscar.getText() != null ? txtBuscar.getText().toLowerCase() : "";
        String moduloSeleccionado = cbFiltroModulo.getValue();
        listaFiltrada.setPredicate(permiso -> {
            boolean coincideModulo = true;
            if (moduloSeleccionado != null && !moduloSeleccionado.equals("Todos")) {
                coincideModulo = permiso.modulo().equals(moduloSeleccionado);
            }
            boolean coincideTexto = true;
            if (!textoBusqueda.isEmpty()) {
                String nombreMinusculas = permiso.nombre().toLowerCase();
                String descMinusculas = permiso.descripcion() != null ? permiso.descripcion().toLowerCase() : "";
                coincideTexto = nombreMinusculas.contains(textoBusqueda) || descMinusculas.contains(textoBusqueda);
            }
            return coincideModulo && coincideTexto;
        });
    }


    @FXML
    private void accionAnadirPermiso(ActionEvent event) {
        PermisoDTO permisoSeleccionado = tablaPermisos.getSelectionModel().getSelectedItem();
        if (permisoSeleccionado == null){
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Atención", null,
                    "Por favor, selecciona un Permiso de la Tabla para Agregarlo al Rol."
            );
            return;
        }
        if (!GestorAlertas.mostrarConfirmacion(getVentana(), "Confirmar", null,
                "¿Estás Seguro de Agregar este Permiso al Rol?")) {
            return;
        }
        this.permisosDelRol.add(permisoSeleccionado);
        if (this.notificadorCambios != null) {
            this.notificadorCambios.run();
        }
        cerrarModal();
    }


    @FXML
    private void accionCancelar(ActionEvent event) {
        cerrarModal();
    }

    private void cerrarModal(){
        Window ventana = getVentana();
        if (ventana != null){
            ventana.hide();
        }
    }

}//===================================================================================================================//

