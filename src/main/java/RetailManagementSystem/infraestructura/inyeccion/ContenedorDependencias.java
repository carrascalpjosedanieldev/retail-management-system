package RetailManagementSystem.infraestructura.inyeccion;

import RetailManagementSystem.aplicacion.ensambladores.*;
import RetailManagementSystem.aplicacion.fabricas.FabricaProductos;
import RetailManagementSystem.aplicacion.orquestadores.*;
import RetailManagementSystem.aplicacion.puertos.CodificadorContrasenas;
import RetailManagementSystem.aplicacion.puertos.ProveedorConfiguracion;
import RetailManagementSystem.aplicacion.servicios.*;
import RetailManagementSystem.dominio.enums.TipoProducto;
import RetailManagementSystem.dominio.puertos.repositorios.*;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;
import RetailManagementSystem.infraestructura.configuracion.ProveedorConfiguracionImpl;
import RetailManagementSystem.infraestructura.persistencia.mysql.estrategias.EstrategiaPersistenciaPerecedero;
import RetailManagementSystem.infraestructura.persistencia.mysql.estrategias.EstrategiaPersistenciaProducto;
import RetailManagementSystem.infraestructura.persistencia.mysql.estrategias.EstrategiaPersistenciaRopa;
import RetailManagementSystem.infraestructura.persistencia.mysql.conexiones.GestorTransaccionalMySQL;
import RetailManagementSystem.infraestructura.persistencia.mysql.mappers.MapeadorDescuentos;
import RetailManagementSystem.infraestructura.persistencia.mysql.mappers.MapeadorImpuestos;
import RetailManagementSystem.infraestructura.persistencia.mysql.mappers.MapeadorProductoBase;
import RetailManagementSystem.infraestructura.persistencia.mysql.repositorios.*;
import RetailManagementSystem.infraestructura.seguridad.Argon2CodificadorAdapter;

import java.util.HashMap;
import java.util.Map;

public class ContenedorDependencias {

    //DEPENDENCIAS:

        //ENSAMBLADORES:

    private static EnsambladorDTOCarrito ensambladorDTOCarrito;
    private static EnsambladorDTODescuento ensambladorDTODescuento;
    private static EnsambladorDTOFactura ensambladorDTOFactura;
    private static EnsambladorDTOImpuesto ensambladorDTOImpuesto;
    private static EnsambladorDTOInventario ensambladorDTOInventario;
    private static EnsambladorDTOPoliticaVencimiento ensambladorDTOPoliticaVencimiento;
    private static EnsambladorDTOProducto ensambladorDTOProducto;
    private static EnsambladorDTOServicio ensambladorDTOServicio;
    private static EnsambladorDTOPermiso ensambladorDTOPermiso;
    private static EnsambladorDTORol ensambladorDTORol;
    private static EnsambladorDTOUsuario ensambladorDTOUsuario;

        //UTILIDADES:

    private static CodificadorContrasenas codificadorContrasenas;
    private static ProveedorConfiguracion proveedorConfiguracion;
    private static Map<TipoProducto, EstrategiaPersistenciaProducto<?>> despachador;
    private static GestorTransaccional gestorTransaccional;

        //MAPEADORES:

    private static MapeadorImpuestos mapeadorImpuestos;
    private static MapeadorDescuentos mapeadorDescuentos;
    private static MapeadorProductoBase mapeadorProductoBase;

        //REPOSITORIOS:

    private static RepositorioConfiguracion repositorioConfiguracion;
    private static RepositorioDescuentos repositorioDescuentos;
    private static RepositorioFacturas repositorioFacturas;
    private static RepositorioImpuestos repositorioImpuestos;
    private static RepositorioInventario repositorioInventario;
    private static RepositorioPoliticaVencimiento repositorioPoliticaVencimiento;
    private static RepositorioProducto repositorioProducto;
    private static RepositorioServicio repositorioServicio;
    private static RepositorioPermiso repositorioPermiso;
    private static RepositorioRol repositorioRol;
    private static RepositorioUsuario repositorioUsuario;

        //SERVICIOS:

    private static ServicioCarrito servicioCarrito;
    private static ServicioConfiguraciones servicioConfiguraciones;
    private static ServicioDescuentos servicioDescuentos;
    private static ServicioFacturas servicioFacturas;
    private static ServicioImpuestos servicioImpuestos;
    private static ServicioInventario servicioInventario;
    private static ServicioPoliticaVencimiento servicioPoliticaVencimiento;
    private static ServicioProductos servicioProductos;
    private static ServicioGestionStock servicioGestionStock;
    private static ServicioServicios servicioServicios;
    private static ServicioPermiso servicioPermiso;
    private static ServicioRol servicioRol;
    private static ServicioUsuario servicioUsuario;

        //FABRICAS:

    private static FabricaProductos fabricaProductos;

        //ORQUESTADORES:

    private static OrquestadorDescuentos orquestadorDescuentos;
    private static OrquestadorHistoricoDeVentas orquestadorHistoricoDeVentas;
    private static OrquestadorImpuestos orquestadorImpuestos;
    private static OrquestadorInventarios orquestadorInventarios;
    private static OrquestadorLogin orquestadorLogin;
    private static OrquestadorPermisos orquestadorPermisos;
    private static OrquestadorPoliticaVencimiento orquestadorPoliticaVencimiento;
    private static OrquestadorProductos orquestadorProductos;
    private static OrquestadorGestionStock orquestadorGestionStock;
    private static OrquestadorRoles orquestadorRoles;
    private static OrquestadorServicios orquestadorServicios;
    private static OrquestadorUsuarios orquestadorUsuarios;
    private static OrquestadorVentas orquestadorVentas;

    //BANDERA SEGURIDAD:

    private static boolean inicializado = false;

    //INICIALIZADOR:

    public static void inicializar() {
        if (inicializado) return;

        //INSTANCIACIÓN DE ENSAMBLADORES:

        ensambladorDTOCarrito = new EnsambladorDTOCarrito();
        ensambladorDTODescuento = new EnsambladorDTODescuento();
        ensambladorDTOFactura = new EnsambladorDTOFactura();
        ensambladorDTOImpuesto = new EnsambladorDTOImpuesto();
        ensambladorDTOPoliticaVencimiento = new EnsambladorDTOPoliticaVencimiento();
        ensambladorDTOProducto = new EnsambladorDTOProducto(
                ensambladorDTOImpuesto, ensambladorDTODescuento, ensambladorDTOPoliticaVencimiento
        );
        ensambladorDTOInventario = new EnsambladorDTOInventario();
        ensambladorDTOServicio = new EnsambladorDTOServicio(
                ensambladorDTOImpuesto, ensambladorDTODescuento
        );
        ensambladorDTOPermiso = new EnsambladorDTOPermiso();
        ensambladorDTORol = new EnsambladorDTORol(ensambladorDTOPermiso);
        ensambladorDTOUsuario = new EnsambladorDTOUsuario(ensambladorDTORol);

        //INSTANCIACIÓN DE UTILIDADES:

        codificadorContrasenas = new Argon2CodificadorAdapter();

        proveedorConfiguracion = new ProveedorConfiguracionImpl(repositorioConfiguracion);

        gestorTransaccional = new GestorTransaccionalMySQL();

        //INSTANTIATION DE MAPEADORES:

        mapeadorImpuestos = new MapeadorImpuestos();
        mapeadorDescuentos = new MapeadorDescuentos();
        mapeadorProductoBase = new MapeadorProductoBase();

        //INSTANTIATION DE ESTRATEGIAS:

        despachador = new HashMap<>();
        despachador.put(TipoProducto.ROPA, new EstrategiaPersistenciaRopa());
        despachador.put(TipoProducto.PERECEDERO, new EstrategiaPersistenciaPerecedero());

        //INSTANCIACIÓN DE REPOSITORIOS:

        repositorioConfiguracion = new RepositorioConfiguracionMySQL();
        repositorioDescuentos = new RepositorioDescuentosMySQL(mapeadorDescuentos);
        repositorioFacturas = new RepositorioFacturasMySQL();
        repositorioImpuestos = new RepositorioImpuestosMySQL(mapeadorImpuestos);
        repositorioInventario = new RepositorioInventarioMySQL();
        repositorioPoliticaVencimiento = new RepositorioPoliticaVencimientoMySQL();
        repositorioProducto = new RepositorioProductoMySQL(
                despachador, mapeadorImpuestos, mapeadorDescuentos, mapeadorProductoBase
        );
        repositorioServicio = new RepositorioServicioMySQL();
        repositorioPermiso = new RepositorioPermisoMySQL();
        repositorioRol = new RepositorioRolMySQL();
        repositorioUsuario = new RepositorioUsuarioMySQL();

        //INSTANCIACIÓN DE SERVICIOS:

        servicioProductos = new ServicioProductos(
                repositorioProducto, repositorioImpuestos, repositorioDescuentos, repositorioPoliticaVencimiento,
                gestorTransaccional
        );
        servicioGestionStock = new ServicioGestionStock(
                repositorioProducto, repositorioInventario, gestorTransaccional
        );
        servicioServicios = new ServicioServicios(
                repositorioImpuestos, repositorioDescuentos, repositorioServicio
        );
        servicioCarrito = new ServicioCarrito(servicioProductos, servicioServicios);
        servicioConfiguraciones = new ServicioConfiguraciones(repositorioConfiguracion);
        servicioDescuentos = new ServicioDescuentos(repositorioDescuentos);
        servicioFacturas = new ServicioFacturas(repositorioFacturas);
        servicioImpuestos = new ServicioImpuestos(repositorioImpuestos);
        servicioInventario = new ServicioInventario(repositorioInventario);
        servicioPoliticaVencimiento = new ServicioPoliticaVencimiento(repositorioPoliticaVencimiento);
        servicioPermiso = new ServicioPermiso(repositorioPermiso);
        servicioRol = new ServicioRol(repositorioRol);
        servicioUsuario = new ServicioUsuario(repositorioUsuario, codificadorContrasenas, proveedorConfiguracion);

        //INSTANCIACIÓN DE ORQUESTADORES:

        fabricaProductos = new FabricaProductos(servicioImpuestos, servicioDescuentos, servicioPoliticaVencimiento);

        //INSTANCIACIÓN DE ORQUESTADORES:

        orquestadorDescuentos = new OrquestadorDescuentos(servicioDescuentos, ensambladorDTODescuento);
        orquestadorHistoricoDeVentas = new OrquestadorHistoricoDeVentas(servicioFacturas, ensambladorDTOFactura);
        orquestadorImpuestos = new OrquestadorImpuestos(servicioImpuestos, ensambladorDTOImpuesto);
        orquestadorInventarios = new OrquestadorInventarios(servicioInventario, ensambladorDTOInventario);
        orquestadorLogin = new OrquestadorLogin(servicioUsuario, ensambladorDTOUsuario);
        orquestadorPermisos = new OrquestadorPermisos(ensambladorDTOPermiso, servicioPermiso);
        orquestadorPoliticaVencimiento= new OrquestadorPoliticaVencimiento(
                servicioPoliticaVencimiento, ensambladorDTOPoliticaVencimiento
        );
        orquestadorProductos = new OrquestadorProductos(ensambladorDTOProducto, servicioProductos);
        orquestadorGestionStock = new OrquestadorGestionStock(
                servicioProductos, servicioGestionStock, ensambladorDTOProducto
        );
        orquestadorRoles = new OrquestadorRoles(servicioRol, ensambladorDTORol);
        orquestadorServicios = new OrquestadorServicios(
                servicioServicios, ensambladorDTOServicio
        );
        orquestadorUsuarios = new OrquestadorUsuarios(servicioUsuario, ensambladorDTOUsuario);
        orquestadorVentas = new OrquestadorVentas(
                servicioFacturas, servicioCarrito, servicioProductos, servicioServicios,
                ensambladorDTOFactura, ensambladorDTOCarrito
                );

        inicializado = true;
    }

    //GETTERS DE DEPENDENCIAS:

    private static void validarInicializado(){
        if (!inicializado) {
            throw new IllegalStateException("El Contenedor NO ha sido Inicializado.");
        }
    }

    public static EnsambladorDTOCarrito getEnsambladorDTOCarrito() {
        validarInicializado();
        return ensambladorDTOCarrito;
    }

    public static EnsambladorDTODescuento getEnsambladorDTODescuento() {
        validarInicializado();
        return ensambladorDTODescuento;
    }

    public static EnsambladorDTOFactura getEnsambladorDTOFactura() {
        validarInicializado();
        return ensambladorDTOFactura;
    }

    public static EnsambladorDTOImpuesto getEnsambladorDTOImpuesto() {
        validarInicializado();
        return ensambladorDTOImpuesto;
    }

    public static EnsambladorDTOInventario getEnsambladorDTOInventario() {
        validarInicializado();
        return ensambladorDTOInventario;
    }

    public static EnsambladorDTOPoliticaVencimiento getEnsambladorDTOPoliticaVencimiento() {
        validarInicializado();
        return ensambladorDTOPoliticaVencimiento;
    }

    public static EnsambladorDTOProducto getEnsambladorDTOProducto() {
        validarInicializado();
        return ensambladorDTOProducto;
    }

    public static EnsambladorDTOServicio getEnsambladorDTOServicio() {
        validarInicializado();
        return ensambladorDTOServicio;
    }

    public static EnsambladorDTOPermiso getEnsambladorDTOPermiso() {
        validarInicializado();
        return ensambladorDTOPermiso;
    }

    public static EnsambladorDTORol getEnsambladorDTORol() {
        validarInicializado();
        return ensambladorDTORol;
    }

    public static EnsambladorDTOUsuario getEnsambladorDTOUsuario() {
        validarInicializado();
        return ensambladorDTOUsuario;
    }

    public static RepositorioConfiguracion getRepositorioConfiguracion() {
        validarInicializado();
        return repositorioConfiguracion;
    }

    public static RepositorioDescuentos getRepositorioDescuentos() {
        validarInicializado();
        return repositorioDescuentos;
    }

    public static RepositorioFacturas getRepositorioFacturas() {
        validarInicializado();
        return repositorioFacturas;
    }

    public static RepositorioImpuestos getRepositorioImpuestos() {
        validarInicializado();
        return repositorioImpuestos;
    }

    public static RepositorioInventario getRepositorioInventario() {
        validarInicializado();
        return repositorioInventario;
    }

    public static RepositorioPoliticaVencimiento getRepositorioPoliticaVencimiento() {
        validarInicializado();
        return repositorioPoliticaVencimiento;
    }

    public static RepositorioProducto getRepositorioProducto() {
        validarInicializado();
        return repositorioProducto;
    }

    public static RepositorioServicio getRepositorioServicio() {
        validarInicializado();
        return repositorioServicio;
    }

    public static RepositorioPermiso getRepositorioPermiso() {
        validarInicializado();
        return repositorioPermiso;
    }

    public static RepositorioRol getRepositorioRol() {
        validarInicializado();
        return repositorioRol;
    }

    public static RepositorioUsuario getRepositorioUsuario() {
        validarInicializado();
        return repositorioUsuario;
    }

    public static CodificadorContrasenas getCodificadorContrasenas() {
        validarInicializado();
        return codificadorContrasenas;
    }

    public static ProveedorConfiguracion getProveedorConfiguracion() {
        validarInicializado();
        return proveedorConfiguracion;
    }

    public static ServicioCarrito getServicioCarrito() {
        validarInicializado();
        return servicioCarrito;
    }

    public static ServicioConfiguraciones getServicioConfiguraciones() {
        validarInicializado();
        return servicioConfiguraciones;
    }

    public static ServicioDescuentos getServicioDescuentos() {
        validarInicializado();
        return servicioDescuentos;
    }

    public static ServicioFacturas getServicioFacturas() {
        validarInicializado();
        return servicioFacturas;
    }

    public static ServicioImpuestos getServicioImpuestos() {
        validarInicializado();
        return servicioImpuestos;
    }

    public static ServicioInventario getServicioInventario() {
        validarInicializado();
        return servicioInventario;
    }

    public static ServicioPoliticaVencimiento getServicioPoliticaVencimiento() {
        validarInicializado();
        return servicioPoliticaVencimiento;
    }

    public static ServicioProductos getServicioProductos() {
        validarInicializado();
        return servicioProductos;
    }

    public static ServicioServicios getServicioServicios() {
        validarInicializado();
        return servicioServicios;
    }

    public static ServicioPermiso getServicioPermiso() {
        validarInicializado();
        return servicioPermiso;
    }

    public static ServicioRol getServicioRol() {
        validarInicializado();
        return servicioRol;
    }

    public static ServicioUsuario getServicioUsuario() {
        validarInicializado();
        return servicioUsuario;
    }

    public static FabricaProductos getFabricaProductos() {
        validarInicializado();
        return fabricaProductos;
    }

    public static OrquestadorDescuentos getOrquestadorDescuentos() {
        validarInicializado();
        return orquestadorDescuentos;
    }

    public static OrquestadorHistoricoDeVentas getOrquestadorHistoricoDeVentas() {
        validarInicializado();
        return orquestadorHistoricoDeVentas;
    }

    public static OrquestadorImpuestos getOrquestadorImpuestos() {
        validarInicializado();
        return orquestadorImpuestos;
    }

    public static OrquestadorInventarios getOrquestadorInventarios() {
        return orquestadorInventarios;
    }

    public static OrquestadorLogin getOrquestadorLogin() {
        validarInicializado();
        return orquestadorLogin;
    }

    public static OrquestadorPermisos getOrquestadorPermisos() {
        validarInicializado();
        return orquestadorPermisos;
    }

    public static OrquestadorPoliticaVencimiento getOrquestadorPoliticaVencimiento() {
        validarInicializado();
        return orquestadorPoliticaVencimiento;
    }

    public static OrquestadorProductos getOrquestadorProductos() {
        validarInicializado();
        return orquestadorProductos;
    }

    public static OrquestadorGestionStock getOrquestadorGestionStock() {
        validarInicializado();
        return orquestadorGestionStock;
    }

    public static OrquestadorRoles getOrquestadorRoles() {
        validarInicializado();
        return orquestadorRoles;
    }

    public static OrquestadorServicios getOrquestadorServicios() {
        validarInicializado();
        return orquestadorServicios;
    }

    public static OrquestadorUsuarios getOrquestadorUsuarios() {
        validarInicializado();
        return orquestadorUsuarios;
    }

    public static OrquestadorVentas getOrquestadorVentas() {
        validarInicializado();
        return orquestadorVentas;
    }

}//===================================================================================================================//

