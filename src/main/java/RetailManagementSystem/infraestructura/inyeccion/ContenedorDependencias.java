package RetailManagementSystem.infraestructura.inyeccion;

import RetailManagementSystem.aplicacion.ensambladores.comercial.EnsambladorDTOProducto;
import RetailManagementSystem.aplicacion.ensambladores.comercial.EnsambladorDTOServicio;
import RetailManagementSystem.aplicacion.ensambladores.comercial.estrategias.EstrategiaEnsambladoDTOPerecedero;
import RetailManagementSystem.aplicacion.ensambladores.comercial.estrategias.EstrategiaEnsambladoDTOProducto;
import RetailManagementSystem.aplicacion.ensambladores.comercial.estrategias.EstrategiaEnsambladoDTORopa;
import RetailManagementSystem.aplicacion.ensambladores.gestion.EnsambladorDTODescuento;
import RetailManagementSystem.aplicacion.ensambladores.gestion.EnsambladorDTOImpuesto;
import RetailManagementSystem.aplicacion.ensambladores.gestion.EnsambladorDTOInventario;
import RetailManagementSystem.aplicacion.ensambladores.gestion.EnsambladorDTOPoliticaVencimiento;
import RetailManagementSystem.aplicacion.ensambladores.seguridad.EnsambladorDTOPermiso;
import RetailManagementSystem.aplicacion.ensambladores.seguridad.EnsambladorDTORol;
import RetailManagementSystem.aplicacion.ensambladores.seguridad.EnsambladorDTOUsuario;
import RetailManagementSystem.aplicacion.ensambladores.ventas.EnsambladorDTOCarrito;
import RetailManagementSystem.aplicacion.ensambladores.ventas.EnsambladorDTOFactura;
import RetailManagementSystem.aplicacion.fabricas.FabricaProductos;
import RetailManagementSystem.aplicacion.orquestadores.*;
import RetailManagementSystem.aplicacion.puertos.CodificadorContrasenas;
import RetailManagementSystem.aplicacion.puertos.ProveedorConfiguracion;
import RetailManagementSystem.dominio.financiero.calculos.MatematicaFinanciera;
import RetailManagementSystem.dominio.financiero.estrategias.EstrategiaCalculoPrecios;
import RetailManagementSystem.dominio.financiero.estrategias.EstrategiaCalculoPreciosProductoPerecedero;
import RetailManagementSystem.dominio.financiero.estrategias.EstrategiaCalculoPreciosProductoRopa;
import RetailManagementSystem.dominio.financiero.estrategias.EstrategiaCalculoPreciosServicio;
import RetailManagementSystem.aplicacion.servicios.gestion.ServicioProductos;
import RetailManagementSystem.aplicacion.servicios.gestion.ServicioServicios;
import RetailManagementSystem.aplicacion.servicios.gestion.*;
import RetailManagementSystem.aplicacion.servicios.seguridad.ServicioLogin;
import RetailManagementSystem.aplicacion.servicios.seguridad.ServicioPermiso;
import RetailManagementSystem.aplicacion.servicios.seguridad.ServicioRol;
import RetailManagementSystem.aplicacion.servicios.seguridad.ServicioUsuario;
import RetailManagementSystem.aplicacion.servicios.ventas.ServicioCarrito;
import RetailManagementSystem.aplicacion.servicios.ventas.ServicioFacturas;
import RetailManagementSystem.dominio.entidades.comercial.*;
import RetailManagementSystem.dominio.enums.TipoProducto;
import RetailManagementSystem.dominio.financiero.calculos.CalculadoraPrecios;
import RetailManagementSystem.dominio.puertos.repositorios.*;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;
import RetailManagementSystem.infraestructura.configuracion.ProveedorConfiguracionImpl;
import RetailManagementSystem.infraestructura.persistencia.mysql.estrategias.EstrategiaPersistenciaPerecedero;
import RetailManagementSystem.infraestructura.persistencia.mysql.estrategias.EstrategiaPersistenciaProducto;
import RetailManagementSystem.infraestructura.persistencia.mysql.estrategias.EstrategiaPersistenciaRopa;
import RetailManagementSystem.infraestructura.persistencia.mysql.conexiones.GestorTransaccionalMySQL;
import RetailManagementSystem.infraestructura.persistencia.mysql.mappers.*;
import RetailManagementSystem.infraestructura.persistencia.mysql.repositorios.*;
import RetailManagementSystem.infraestructura.seguridad.Argon2CodificadorAdapter;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarProductos.tabGeneral.crearProducto.estrategias.EstrategiaCreacionDetallePerecedero;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarProductos.tabGeneral.crearProducto.estrategias.EstrategiaCreacionDetalleProducto;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarProductos.tabGeneral.crearProducto.estrategias.EstrategiaCreacionDetalleRopa;

import java.util.HashMap;
import java.util.Map;

public class ContenedorDependencias {

    //DEPENDENCIAS:

    private static Map<TipoProducto, EstrategiaCreacionDetalleProducto<?, ?>> estrategiasCreacionDetalle;



    private static OrquestadorConfiguraciones orquestadorConfiguraciones;
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

        //INSTANCIACIÓN DE UTILIDADES FINANCIERAS:

        MatematicaFinanciera matematicaFinanciera = new MatematicaFinanciera();

        Map<Class<? extends ItemFacturable>, EstrategiaCalculoPrecios<?>> estrategiasCalculoPrecios = new HashMap<>();
        estrategiasCalculoPrecios.put(
                ProductoRopa.class, new EstrategiaCalculoPreciosProductoRopa(matematicaFinanciera)
        );
        estrategiasCalculoPrecios.put(
                ProductoPerecedero.class, new EstrategiaCalculoPreciosProductoPerecedero(matematicaFinanciera)
        );
        estrategiasCalculoPrecios.put(
                Servicio.class, new EstrategiaCalculoPreciosServicio(matematicaFinanciera)
        );

        CalculadoraPrecios calculadoraPrecios = new CalculadoraPrecios(matematicaFinanciera, estrategiasCalculoPrecios);

        //ENSAMBLADORES:

        EnsambladorDTOImpuesto ensambladorDTOImpuesto = new EnsambladorDTOImpuesto();
        EnsambladorDTODescuento ensambladorDTODescuento = new EnsambladorDTODescuento();
        EnsambladorDTOPoliticaVencimiento ensambladorDTOPoliticaVencimiento = new EnsambladorDTOPoliticaVencimiento();

        EstrategiaEnsambladoDTORopa estrategiaEnsambladoDTORopa = new EstrategiaEnsambladoDTORopa(
                calculadoraPrecios, ensambladorDTOImpuesto, ensambladorDTODescuento
        );

        EstrategiaEnsambladoDTOPerecedero estrategiaEnsambladoDTOPerecedero = new EstrategiaEnsambladoDTOPerecedero(
                calculadoraPrecios, ensambladorDTOImpuesto, ensambladorDTODescuento, ensambladorDTOPoliticaVencimiento
        );

        Map<TipoProducto, EstrategiaEnsambladoDTOProducto<?>> estrategiasEnsamblado = new HashMap<>();
        estrategiasEnsamblado.put(TipoProducto.ROPA, estrategiaEnsambladoDTORopa);
        estrategiasEnsamblado.put(TipoProducto.PERECEDERO, estrategiaEnsambladoDTOPerecedero);

        EnsambladorDTOCarrito ensambladorDTOCarrito = new EnsambladorDTOCarrito(calculadoraPrecios);
        EnsambladorDTOFactura ensambladorDTOFactura = new EnsambladorDTOFactura();
        EnsambladorDTOProducto ensambladorDTOProducto = new EnsambladorDTOProducto(estrategiasEnsamblado, calculadoraPrecios);
        EnsambladorDTOInventario ensambladorDTOInventario = new EnsambladorDTOInventario();
        EnsambladorDTOServicio ensambladorDTOServicio = new EnsambladorDTOServicio(
                calculadoraPrecios, ensambladorDTOImpuesto, ensambladorDTODescuento
        );
        EnsambladorDTOPermiso ensambladorDTOPermiso = new EnsambladorDTOPermiso();
        EnsambladorDTORol ensambladorDTORol = new EnsambladorDTORol(ensambladorDTOPermiso);
        EnsambladorDTOUsuario ensambladorDTOUsuario = new EnsambladorDTOUsuario(ensambladorDTORol);

        //INSTANTIATION DE MAPEADORES:

        MapeadorImpuestos mapeadorImpuestos = new MapeadorImpuestos();
        MapeadorDescuentos mapeadorDescuentos = new MapeadorDescuentos();
        MapeadorProductoBase mapeadorProductoBase = new MapeadorProductoBase();
        MapeadorPoliticasVencimiento mapeadorPoliticasVencimiento = new MapeadorPoliticasVencimiento();
        MapeadorInventario mapeadorInventario = new MapeadorInventario();
        MapeadorPermisos mapeadorPermisos = new MapeadorPermisos();
        MapeadorRol mapeadorRol = new MapeadorRol();
        MapeadorServicio mapeadorServicio = new MapeadorServicio();
        MapeadorUsuario mapeadorUsuario = new MapeadorUsuario();

        //INSTANTIATION DE ESTRATEGIAS Y GESTOR TRANSACCIONAL:

        GestorTransaccional gestorTransaccional = new GestorTransaccionalMySQL();

        Map<TipoProducto, EstrategiaPersistenciaProducto<?>> despachador = new HashMap<>();
        despachador.put(TipoProducto.ROPA, new EstrategiaPersistenciaRopa());
        despachador.put(TipoProducto.PERECEDERO, new EstrategiaPersistenciaPerecedero(mapeadorPoliticasVencimiento));

        //INSTANCIACIÓN DE REPOSITORIOS:

        RepositorioConfiguracion repositorioConfiguracion = new RepositorioConfiguracionMySQL();
        RepositorioDescuentos repositorioDescuentos = new RepositorioDescuentosMySQL(mapeadorDescuentos);
        RepositorioFacturas repositorioFacturas = new RepositorioFacturasMySQL();
        RepositorioImpuestos repositorioImpuestos = new RepositorioImpuestosMySQL(mapeadorImpuestos);
        RepositorioInventario repositorioInventario = new RepositorioInventarioMySQL(mapeadorInventario);
        RepositorioPoliticaVencimiento repositorioPoliticaVencimiento =
                new RepositorioPoliticaVencimientoMySQL(mapeadorPoliticasVencimiento);
        RepositorioProducto repositorioProducto = new RepositorioProductoMySQL(
                despachador, mapeadorImpuestos, mapeadorDescuentos, mapeadorProductoBase
        );
        RepositorioServicio repositorioServicio = new RepositorioServicioMySQL(
                mapeadorServicio, mapeadorImpuestos, mapeadorDescuentos
        );
        RepositorioPermiso repositorioPermiso = new RepositorioPermisoMySQL(mapeadorPermisos);
        RepositorioRol repositorioRol = new RepositorioRolMySQL(mapeadorRol, mapeadorPermisos);
        RepositorioUsuario repositorioUsuario = new RepositorioUsuarioMySQL(
                mapeadorUsuario, mapeadorRol, mapeadorPermisos
        );

        //PROOVEDOR Y CODIFICADOR:

        ProveedorConfiguracion proveedorConfiguracion = new ProveedorConfiguracionImpl(
                gestorTransaccional, repositorioConfiguracion
        );

        CodificadorContrasenas codificadorContrasenas = new Argon2CodificadorAdapter();

        //INSTANCIACIÓN DE SERVICIOS:

        ServicioProductos servicioProductos = new ServicioProductos(
                repositorioProducto, repositorioImpuestos, repositorioDescuentos, repositorioPoliticaVencimiento,
                gestorTransaccional
        );
        ServicioGestionStock servicioGestionStock = new ServicioGestionStock(
                repositorioProducto, repositorioInventario, gestorTransaccional
        );
        ServicioServicios servicioServicios = new ServicioServicios(
                repositorioImpuestos, repositorioDescuentos, repositorioServicio, gestorTransaccional
        );
        ServicioCarrito servicioCarrito = new ServicioCarrito(calculadoraPrecios, servicioProductos, servicioServicios);
        ServicioConfiguraciones servicioConfiguraciones = new ServicioConfiguraciones(
                repositorioConfiguracion, proveedorConfiguracion, gestorTransaccional
        );
        ServicioDescuentos servicioDescuentos = new ServicioDescuentos(repositorioDescuentos, gestorTransaccional);
        ServicioFacturas servicioFacturas = new ServicioFacturas(repositorioFacturas, gestorTransaccional);
        ServicioImpuestos servicioImpuestos = new ServicioImpuestos(repositorioImpuestos, gestorTransaccional);
        ServicioInventario servicioInventario = new ServicioInventario(repositorioInventario, gestorTransaccional);
        ServicioLogin servicioLogin = new ServicioLogin(
                repositorioUsuario, codificadorContrasenas, proveedorConfiguracion, gestorTransaccional
        );
        ServicioPoliticaVencimiento servicioPoliticaVencimiento = new ServicioPoliticaVencimiento(
                repositorioPoliticaVencimiento, gestorTransaccional
        );
        ServicioPermiso servicioPermiso = new ServicioPermiso(repositorioPermiso, gestorTransaccional);
        ServicioRol servicioRol = new ServicioRol(repositorioRol, gestorTransaccional);
        ServicioUsuario servicioUsuario = new ServicioUsuario(
                repositorioUsuario, codificadorContrasenas, gestorTransaccional
        );

        //INSTANCIACIÓN DE FABRICAS:

        FabricaProductos fabricaProductos = new FabricaProductos(
                servicioImpuestos, servicioDescuentos, servicioPoliticaVencimiento
        );

        //ESTRATEGIAS VISTA:

        EstrategiaCreacionDetalleRopa creacionDetalleRopa = new EstrategiaCreacionDetalleRopa();
        EstrategiaCreacionDetallePerecedero creacionDetallePerecedero = new EstrategiaCreacionDetallePerecedero();

        estrategiasCreacionDetalle = new HashMap<>();
        estrategiasCreacionDetalle.put(TipoProducto.ROPA, creacionDetalleRopa);
        estrategiasCreacionDetalle.put(TipoProducto.PERECEDERO, creacionDetallePerecedero);

        //INSTANCIACIÓN DE ORQUESTADORES:

        orquestadorConfiguraciones = new OrquestadorConfiguraciones(servicioConfiguraciones);
        orquestadorDescuentos = new OrquestadorDescuentos(servicioDescuentos, ensambladorDTODescuento);
        orquestadorHistoricoDeVentas = new OrquestadorHistoricoDeVentas(servicioFacturas);
        orquestadorImpuestos = new OrquestadorImpuestos(servicioImpuestos, ensambladorDTOImpuesto);
        orquestadorInventarios = new OrquestadorInventarios(servicioInventario, ensambladorDTOInventario);
        orquestadorLogin = new OrquestadorLogin(servicioLogin, ensambladorDTOUsuario);
        orquestadorPermisos = new OrquestadorPermisos(ensambladorDTOPermiso, servicioPermiso);
        orquestadorPoliticaVencimiento= new OrquestadorPoliticaVencimiento(
                servicioPoliticaVencimiento, ensambladorDTOPoliticaVencimiento
        );
        orquestadorProductos = new OrquestadorProductos(ensambladorDTOProducto, servicioProductos);
        orquestadorGestionStock = new OrquestadorGestionStock(
                servicioGestionStock, ensambladorDTOProducto, fabricaProductos
        );
        orquestadorRoles = new OrquestadorRoles(servicioRol, ensambladorDTORol);
        orquestadorServicios = new OrquestadorServicios(
                servicioServicios, ensambladorDTOServicio
        );
        orquestadorUsuarios = new OrquestadorUsuarios(servicioUsuario, ensambladorDTOUsuario);
        orquestadorVentas = new OrquestadorVentas(
                calculadoraPrecios, servicioFacturas, servicioCarrito, ensambladorDTOFactura, ensambladorDTOCarrito
        );

        inicializado = true;
    }

    //GETTERS DE DEPENDENCIAS:

    private static void validarInicializado(){
        if (!inicializado) {
            throw new IllegalStateException("El Contenedor de Dependencias NO ha sido Inicializado.");
        }
    }

    public static Map<TipoProducto, EstrategiaCreacionDetalleProducto<?, ?>> getEstrategiasCreacionDetalle() {
        validarInicializado();
        return estrategiasCreacionDetalle;
    }

    public static OrquestadorConfiguraciones getOrquestadorConfiguraciones() {
        validarInicializado();
        return orquestadorConfiguraciones;
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
        validarInicializado();
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

