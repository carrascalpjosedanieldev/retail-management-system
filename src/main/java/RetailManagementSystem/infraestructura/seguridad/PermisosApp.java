package RetailManagementSystem.infraestructura.seguridad;

import RetailManagementSystem.dominio.puertos.RepositorioPermiso;
import RetailManagementSystem.infraestructura.inyeccion.ContenedorDependencias;

import java.util.List;

public class PermisosApp {

    //INICIALIZADOR:

    private static boolean inicializado = false;

    public static void inicializarYValidarSincronizacionPermisos(){
        if (inicializado) return;

        RepositorioPermiso repositorioPermiso = ContenedorDependencias.getRepositorioPermiso();

        List<String> permisosBD = repositorioPermiso.obtenerNombresTodosLosPermisos();

        List<String> permisosCodigo = List.of(
                PermisosApp.PROCESAR_VENTA,
                PermisosApp.VER_HISTORIAL_VENTAS,
                PermisosApp.VER_INVENTARIOS,
                PermisosApp.ADMINISTRAR_INVENTARIOS,
                PermisosApp.VER_PRODUCTOS,
                PermisosApp.ADMINISTRAR_PRODUCTOS,
                PermisosApp.TRASLADAR_PRODUCTOS,
                PermisosApp.VER_SERVICIOS,
                PermisosApp.ADMINISTRAR_SERVICIOS,
                PermisosApp.ADMINISTRAR_IMPUESTOS,
                PermisosApp.ADMINISTRAR_DESCUENTOS,
                PermisosApp.POLITICAS_DE_VENCIMIENTO,
                PermisosApp.EDITAR_PERFIL_DE_TIENDA,
                PermisosApp.GESTIONAR_ROLES,
                PermisosApp.GESTIONAR_PERMISOS,
                PermisosApp.GESTIONAR_USUARIOS
        );

        for (String permiso : permisosCodigo) {
            if (!permisosBD.contains(permiso)) {
                throw new IllegalStateException(
                        "Error Crítico: El Permiso -" + permiso + "- Existe en el Código " +
                                "pero NO en la Base de Datos. Por favor, Sincroniza los Nombres."
                );
            }
        }

        inicializado = true;
    }

    //PERMISOS:

    public static final String PROCESAR_VENTA = "Procesar Venta";

    public static final String VER_HISTORIAL_VENTAS = "Ver Historial de Ventas";

    public static final String VER_INVENTARIOS = "Ver Inventarios";

    public static final String ADMINISTRAR_INVENTARIOS = "Administrar Inventarios";

    public static final String VER_PRODUCTOS = "Ver Productos";

    public static final String ADMINISTRAR_PRODUCTOS = "Administrar Productos";

    public static final String TRASLADAR_PRODUCTOS = "Trasladar Productos";

    public static final String VER_SERVICIOS = "Ver Servicios";

    public static final String ADMINISTRAR_SERVICIOS = "Administrar Servicios";

    public static final String ADMINISTRAR_IMPUESTOS = "Administrar Impuestos";

    public static final String ADMINISTRAR_DESCUENTOS = "Administrar Descuentos";

    public static final String POLITICAS_DE_VENCIMIENTO = "Políticas de Vencimiento";

    public static final String EDITAR_PERFIL_DE_TIENDA = "Editar Perfil de Tienda";

    public static final String GESTIONAR_ROLES = "Gestionar Roles";

    public static final String GESTIONAR_PERMISOS = "Gestionar Permisos";

    public static final String GESTIONAR_USUARIOS = "Gestionar Usuarios";

}//===================================================================================================================//

