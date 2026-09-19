# Retail Management System

## Languages (Idiomas):

- [🇺🇸 English](#-english)
- [🇪🇸🇨🇴 Español](#--español)

---

---

---

# 🇺🇸 English

# Retail Management System

## Description

The system is a desktop application designed to manage retail store operations and the unified sales process for products and services. It enables the management of global configurations, product and service catalogs, multiple isolated inventories, and business rules (taxes, discounts, and expiration policies), as well as comprehensive management of users, roles, and permissions under a strict access control model. Geared toward centralized store operations, it is designed to meet real-world business requirements, ensure accounting consistency, and maintain data integrity.

The project prioritizes a rich domain model that is decoupled from the infrastructure. The choice of the retail domain reflects the diverse, complex scenarios it allows the system to address: rigorous stock control, high-precision financial calculations using `BigDecimal`, polymorphic handling of billable items, invoice processing via atomic transactions, and a defense-in-depth security scheme.

The project began as a console-based exercise focused on mastering Object-Oriented Programming and evolved into a full-fledged layered architecture with a JavaFX graphical user interface. Developed intentionally without frameworks like Spring Boot, the project aims to explore low-level Java fundamentals: native persistence using JDBC and HikariCP, manual dependency injection implementation, transaction abstraction, design patterns for extensibility (Strategy and Mappers), cryptographic resilience (Argon2), and a decoupled structure ready for automated test suites using JUnit 5 and Mockito.

---

## 🚧 Project status

**Current Version:** 1.1.1

| Module           | Status             |
|------------------|--------------------|
| Store Management | ✅ Completed        |
| Point of Sale    | ✅ Completed        |
| User management  | ✅ Completed        |
| Login            | ✅ Completed        |
| Documentation    | 🚧 In Development  |
| Testing          | 🚧 In Development  |
| Version 1.1.1    | ✅ Completed        |

### Next Goal

Implement **automated testing** across the entire project.

---

## Features

### 🏪 Store Management
- Global store configuration.
- Management of general business information.

### 📦 Product Management
- Registration, lookup, updating, and deactivation (soft delete) of products to preserve sales history.
- Support for multiple product types with specific behaviors.
- Association of specific attributes based on product type.
- Control of product availability status.

### 🛠️ Service Management
- Independent service management.
- Handling of products and services as billable items within the sales process.
- Control of service availability status.

### 📚 Inventory Management
- Management of multiple inventories.
- Association of any product type with an inventory.
- Stock control per inventory.

### 💰 Commercial Management
- Management of taxes and discounts.
- Price management using `BigDecimal` to ensure precision in monetary calculations.
- Configuration of expiration policies.
- Activation and deactivation of commercial settings to avoid breaking past references.

### 🧾 Invoicing
- Invoice generation with a record of full sales details.
- Automatic application of configured taxes and discounts.
- Precise calculation of subtotals and totals without using floating-point data.
- Sales processing with atomicity guarantees (Unit of Work): if an error occurs during invoicing, inventory changes are automatically rolled back to prevent inconsistencies.

### 🖥️ Point of Sale
- Sales process via an interactive graphical interface.
- Unified shopping cart management, supporting the simultaneous and seamless addition of products and services.
- Real-time polymorphic validation: strict stock verification for physical items and expiration validation for perishable products before finalizing the sale.
- Automatic invoice generation and inventory update in a synchronized workflow.

### 👤 User Management
- Centralized registration of new users by administrators.
- Management and updating of existing accounts. - User activation and deactivation (logical deletion) while maintaining audit trails.
- Flexible assignment of multiple roles to a single user.
- Secure password resetting via temporary credential issuance.

### 🔐 Authentication
- Login using email and password.
- Advanced cryptographic credential protection using the Argon2 algorithm.
- Active defense against user enumeration attacks by executing a hashing operation with a decoy string to normalize response times.
- Detection of initial access and mandatory temporary password change workflow.
- Preventive temporary account lockout after multiple failed attempts to mitigate brute-force attacks, with a configurable penalty period.

### 👥 Role Management
- Creation and editing of custom roles.
- Role activation and deactivation for secure decommissioning.
- Modular assignment and removal of permissions associated with each role.

### 🛡️ Permission Management
- Centralized administration of existing permissions, mapped uniquely to actions defined in the system code.
- Permission activation and deactivation.
- Dual-validation security scheme: Restrictive access control at the UI level, mandatorily backed by cryptographic or session re-validation within backend orchestrators prior to execution.

---

## Screenshots

### 1. Main Menu

<p>
    <img src="docs/images/spanish/screenshots/Menu_Principal.png" alt="Main Menu" width="1605">
</p>

Entry point of the application. Provides centralized navigation to the main modules of the system, including store management and the point-of-sale module.

### 2. Store Management

<p>
    <img src="docs/images/spanish/screenshots/Gestion_Tienda.png" alt="Store Management" width="1611">
</p>

Central administration panel that provides access to the different management modules of the system. It allows independent management of configurations, inventories, services, taxes, discounts, and expiration policies from a single interface.

### 3. Inventory Management

<p>
    <img src="docs/images/spanish/screenshots/Gestion_Inventarios.png" alt="Inventory Management" width="1603">
</p>

Allows the management of the store's inventories by displaying their maximum capacity, occupied stock, and available space. From this module, users can create new inventories, modify existing ones, and manage the products stored in each inventory.

### 4. Product Management

<p>
    <img src="docs/images/spanish/screenshots/Gestion_Productos.png" alt="Product Management" width="1603">
</p>

Allows the management of the products stored in an inventory through both a general view and specialized views for each product type. Includes search, availability filters, stock management, inventory transfers, and logical activation/deactivation without compromising historical data.

### 5. Service Management

<p>
    <img src="docs/images/spanish/screenshots/Gestion_Servicios.png" alt="Service Management" width="1607">
</p>

Allows the management of the store's service catalog, including base prices, taxes, discounts, and availability status. Unlike products, services are not stored in inventories or managed through stock, while still participating in the same billing process.

### 6. Commercial configurations

<p>
    <img src="docs/images/spanish/screenshots/Configuraciones_Comerciales.png" alt="Commercial configurations" width="1605">
</p>

Module responsible for managing the business rules used throughout the system. It allows administrators to configure taxes, discounts, and expiration policies that are automatically applied during product and service management as well as the sales process, ensuring consistent calculations and business rules.

### 7. Configuration Management

<p>
    <img src="docs/images/spanish/screenshots/Gestion_Configuraciones.png" alt="Configuration Management" width="1605">
</p>

Central panel for managing general store and system settings. It allows you to manage the store name and access role and permission management, centralizing the configuration options available for application operation and access control.


### 8. POS Control Panel (Point of Sale)

<p>
    <img src="docs/images/spanish/screenshots/Panel_De_Control_POS.png" alt="POS Control Panel" width="1607">
</p>

Main screen of the point of sale module. It displays a summary of the day's activity, including sales made, the number of invoices issued, and the value of the last sale. It also allows quick access to create a new sale and viewing the history of recorded sales.

### 9. Customer Service (New Sale)

<p>
    <img src="docs/images/spanish/screenshots/Atencion_Al_Cliente_Nueva_Venta.png" alt="Customer Service" width="1602">
</p>

Main screen of the sales process. It allows you to add products using their code, manage the quantities of each item, remove products from the cart, and view the subtotal, taxes, and total amount due in real time. From this interface, you can also cancel the sale or finalize the purchase to generate the transaction.

### 10. Collection Report

<p>
    <img src="docs/images/spanish/screenshots/Reporte_Recaudo.png" alt="Reporte Recaudo" width="1606">
</p>

This collection report generation window is accessible from the Point of Sale module. It allows you to select a date range to calculate issued invoices, the subtotal collected, the taxes generated, and the total sales for the selected period.

### 11. Invoice

<p>
    <img src="docs/images/spanish/screenshots/Factura_Generada.png" alt="Factura Generada" width="1606">
</p>

Upon completion of a sale, the system automatically generates a receipt detailing the products and services sold, quantities, prices, taxes, subtotal, and total of the transaction. This invoice summarizes the transaction and confirms the successful completion of the sale.

### 12. Login

<p>
    <img src="docs/images/spanish/screenshots/Login.png" alt="Login" width="740">
</p>

Application authentication screen. Allows users to log in using their email address and password, validating their credentials before granting access to the system. Includes options to display the password during login and logout.

### 13. Permission Management

<p>
    <img src="docs/images/spanish/screenshots/Gestion_Permisos.png" alt="Permission Management" width="1607">
</p>

Allows you to view and manage the permissions available in the system. It includes search by name, filters by status and module, as well as the activation and deactivation of permissions. Each permission displays its associated module and a description of the action it authorizes, facilitating the administration and control of access to the application's functionalities.

### 14. Role Management

<p>
    <img src="docs/images/spanish/screenshots/Gestion_Roles.png" alt="Role Management" width="1606">
</p>

This allows you to manage system roles from a centralized view. It includes searching and viewing existing roles, creating new roles, modifying their information, and managing the permissions associated with each role. It also displays the availability status of roles, allowing you to control their participation in the system by activating or deactivating them.

### 15. User Management

<p>
    <img src="docs/images/spanish/screenshots/Gestion_Usuarios.png" alt="User Management" width="1606">
</p>

This allows you to manage users registered in the system from a centralized view. It includes searching by identifier, name, or email address, registering new users, editing information, activating and deactivating accounts, and managing assigned roles. It also allows users to reset their passwords using a temporary password.

---

## Design Decisions

### 1. Clean Architecture and Strict Domain Isolation

The application enforces rigid boundaries between the user interface, orchestration (application services), the domain, and the infrastructure. The presentation layer never interacts directly with business entities; data transfer flows exclusively through DTOs constructed by Assembler classes. This isolation ensures that UI changes do not pollute business rules and that the domain model remains unconstrained by display requirements.

### 2. Centralized Transactional Infrastructure (Unit of Work Pattern)

A custom Transaction Manager was implemented to manage the connection lifecycle using `ThreadLocal`. This abstraction allows for the execution of complex logical blocks while guaranteeing ACID properties, without coupling the service layer to the JDBC API. The manager handles advanced propagation scenarios (such as independent transactional scopes for security auditing), defensive resource control, and the prevention of connection pool pollution by strictly resetting the `autoCommit` state.

### 3. Low-Level, Agnostic Persistence and Dedicated Mappers

Commercial ORMs were rejected in favor of raw JDBC to maintain absolute control over performance, SQL execution, and in-memory mapping. To prevent repositories from assuming multiple responsibilities, object hydration (translating `ResultSet` data into Entities) was delegated to specialized Mapper components. This keeps repositories focused exclusively on executing DML commands and structural queries.

### 4. Polymorphic Persistence (Strategy Pattern and OCP Compliance)

Saving and retrieving products of different natures (e.g., Clothing, Perishables) was addressed by applying the Strategy Pattern within the `ProductRepository`. Instead of using conditional control structures—which degrade code quality with every new product type—the repository dynamically delegates SQL execution to the appropriate strategy. This ensures adherence to the Open/Closed Principle (OCP), allowing for the integration of future product families without altering a single line of existing persistence logic.

### 5. Segregated Domain Model (Inventory Items vs. Services)

The sales catalog semantically separates physical goods from services. Although both implement the `ItemFacturable` contract to converge polymorphically within the shopping cart, they exhibit distinct behaviors: products are subject to a capability interface (`Inventariable`) for strict stock control, whereas services operate without inventory constraints. This respects the Interface Segregation Principle (ISP) and avoids injecting null validations or dummy methods into the services.

### 6. High Cohesion and Decoupling in the Application Layer

The service layer adheres to the Single Responsibility Principle (SRP), avoiding "God Objects." Disparate functions that previously coexisted in monolithic classes have been segregated into narrowly scoped services (e.g., splitting authentication logic into a standalone `LoginService`, leaving `UserService` focused on CRUD administration). Services act as thin orchestrators that delegate actual business rules to domain entities.

### 7. Immutable Financial Precision (BigDecimal)

The system completely eliminates the use of primitive floating-point types (`float`, `double`) for handling monetary values. All arithmetic operations involving base prices, tax application, discounts, and invoice totalization are performed using `BigDecimal`. This architectural guideline is non-negotiable to prevent discrepancies caused by binary representation errors, ensuring accounting accuracy at the transactional level.

### 8. RBAC Authorization and Defense in Depth (Double Validation)

Access control is based on a Role-Based Access Control (RBAC) scheme, where permissions are not created dynamically but map directly to pre-existing code operations. A Zero Trust approach is employed for UI-backend communication: although the view hides components based on user role, each orchestrator cryptographically or session-validates that the user holds the precise authorization required before executing any system mutation.

### 9. Cryptographic Resilience and Anti-Enumeration in Authentication

The authentication mechanism goes beyond simple hashing. It employs Argon2 to withstand hardware-based brute-force attacks (ASIC/GPU). Countermeasures against user enumeration attacks have been implemented: when a non-existent email is entered, the system performs a hashing operation using a decoy string to normalize response times. Furthermore, temporary passwords are not stored as such; instead, they mandate a password renewal process upon the first login.

### 10. Historical Immutability and Multi-Inventory Isolation

The relational model safeguards the historical integrity of business operations. Key entities—such as products, tax rates, and discounts—operate under a logical deletion model (Activation/Inactivation), preventing the breakage of foreign key references in previously issued invoices. Additionally, the stock architecture natively supports multiple inventories, isolating available quantities by physical warehouse without duplicating the base product definition.

---

## Technologies Used

| Technology  | Use                                                   |
|-------------|-------------------------------------------------------|
| Java        | Business Logic                                        |
| JavaFX      | Desktop Graphical Interface                           |
| FXML        | Definition of Interface Views                         |
| CSS         | Graphical Interface Styles                            |
| JDBC        | Data Persistence                                      |
| HikariCP    | JDBC Connection Pool                                  |
| Argon2      | Password Encoder                                      |
| MySQL       | Relational Database                                   |
| Maven       | Dependency Management and Project Build               |
| JUnit 5     | Framework for automated unit testing                  |
| Mockito     | Object simulation (mocks) for test isolation          |

---

## Installation and Execution

### Requirements

- Java 26 or higher
- Maven
- MySQL
- Git

### 1. Clone the repository

Download a local copy of the project by running:

```bash
git clone https://github.com/carrascalpjosedanieldev/retail-management-system.git 
cd retail-management-system
```

### 2. Configure the database

A copy of the project's database is included in the **database/** folder.

Import the SQL file into MySQL. The script will automatically create the **"retail_management_system"** database if it doesn't already exist.

### 3. Configure the connection

Rename the file:

```text
application.properties.example
```

to

```text
application.properties
```

and configure the following information:

- Database URL (By default, it's the name of the database created by the script)
- Username
- Password

For your convenience, the instructions are also included in ```application.properties.example```

### 4. Run the project

Open the project with your preferred Maven-compatible IDE (IntelliJ IDEA), wait for Maven to download the dependencies, and run the application's main class.

---

## Architecture

### General Description

The system is organized using a layered architecture, where each component has a clearly defined responsibility. The graphical interface handles user interaction and communicates with the application via orchestrators. These act as entry points for business operations and implement a defense-in-depth (Zero Trust) security scheme: they reinforce the interface's visual restrictions by strictly revalidating user permissions before allowing any execution.

Once authorization is granted, the orchestrators coordinate the use of domain services and transform the resulting entities into DTOs, delivering only the necessary information to the presentation layer. The core business logic is encapsulated within these services, while data access is completely decoupled through the use of ports (interfaces) and native implementations using pure JDBC for MySQL.

This organization separates presentation, access control, business logic, and persistence, facilitating code maintenance and allowing components within a layer to be modified or replaced without directly affecting the others.

### Layered Architecture

Based on the separation of responsibilities and decoupling between presentation, use case coordination, business logic, and persistence. The graphical interface interacts with the application through orchestrators, services encapsulate business rules, and persistence is abstracted through ports and implementations specific to MySQL.

<p>
  <img src="docs/images/english/diagrams/Layered_Architecture_Diagram.png" alt="Layered Architecture Diagram" width="404">
</p>

**Figure 1.** General system architecture organized by layers. Each layer depends only on the one immediately below it, promoting decoupling between the user interface, permission validations, business logic, and persistence infrastructure.

|                                                                                                              Request Flow                                                                                                              |                                                                                                                Response Flow                                                                                                                 |
|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
|                                                                                   <img src="docs/images/english/diagrams/Request_Flow_Diagram.png">                                                                                    |                                                                                      <img src="docs/images/english/diagrams/Response_Flow_Diagram.png">                                                                                      |
| **Figure 2.** Request flow from user interaction to the execution of a database operation. Each layer performs only its corresponding responsibilities, maintaining decoupling between the interface, business logic, and persistence. | **Figure 3.** Response flow from persistence to the user interface. Before reaching the presentation layer, domain entities are transformed into DTOs using assemblers to prevent the interface from directly manipulating the domain model. |

### Package Structure

**Source Code**

```text
src/
main/
java/
RetailManagementSystem
│
├── application
│   ├── dto
│   │   ├── commercial
│   │   ├── queries
│   │   ├── management
│   │   ├── security
│   │   └── sales
│   ├── assemblers
│   ├── factories
│   ├── orchestrators
│   ├── ports
│   └── services
│
├── domain
│   ├── entities
│   │   ├── commercial
│   │   ├── management
│   │   ├── security
│   │   └── sales
│   ├── enums
│   ├── exceptions
│   │   ├── authenticationAndSecurity
│   │   ├── conflicts
│   │   ├── resourceNotFound
│   │   └── businessRules
│   └── ports
│       ├── repositories
│       └── transactions
│
├── infrastructure
│   ├── configuration
│   ├── injection
│   ├── persistence
│   │   ├── exceptions
│   │   └── mysql
│   │       ├── connections
│   │       ├── strategies
│   │       ├── mappers
│   │       └── repositories
│   └── security
│
├── view
│ ├── configuration
│ ├── controllers
│ │ ├── manageStore
│ │ ├── manageUsers
│ │ ├── login
│ │ ├── mainMenu
│ │ └── pointOfSale
│ ├── exceptions
│ └── utilities
│
├── App
└── Executable
```

**Resources**
```text
src/
main/
resources
│
├── css
│ ├── alerts
│ ├── manageStore
│ ├── manageUsers
│ ├── login
│ ├── mainMenu
│ └── pointOfSale
│
├── view
│ ├── manageStore
│ ├── manageUsers
│ ├── login
│ ├── mainMenu
│ └── pointOfSale
│
├── application.example.properties
└── application.properties
```

The project structure is organized following a layered architecture. Each package groups components with a specific responsibility, promoting separation of responsibilities and decoupling between the user interface, business logic, domain, and infrastructure.

| Package          | Responsibility and Key Components                                                                                                                                                                                                                                                                                                                            |
|------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `application`    | **Orchestration and Use Cases:** Coordinates application flow using Orchestrators and Application Services.<br><br>**Data Transfer:** Contains DTOs, Assemblers, and Factories to communicate between the UI and the backend without exposing domain entities.                                                                                               |
| `domain`         | **Business Core:** Models system logic using pure Entities, Value Objects, and Enumerations.<br><br>**Abstractions:** Defines business Exceptions and Ports (interfaces) that establish data access contracts.                                                                                                                                               |
| `infrastructure` | **Persistence & Transactions:** JDBC/MySQL implementation with HikariCP pooling, `TransactionManager`, and dedicated Mappers.<br><br>**Security & Patterns:** Polymorphic repositories based on the Strategy pattern (OCP), Argon2 cryptographic hashing, and dependency injection.                                                                          |
| `view`           | **Controllers and Navigation:** JavaFX controllers, centralized screen flow management via `ViewLoader`, and `ViewRoutes` definition.<br><br>**Utilities & Visual Resilience:** `AlertManager` for standardized feedback, `NumberFormatter` for monetary values, UI exceptions, and `ExceptionConfigurator` for early failure detection during view startup. |

---

## 🗺️ Roadmap

The following features are planned for future versions of the system:

- 🔐 Implement automated testing.
- 💻 Expand the system catalog with a new category of technology products.
- 🌐 Add support for multiple languages (Spanish and English).
- 📄 Enable the export and printing of invoices and reports in PDF and Excel formats.
- 📊 Incorporate indicators and statistics for the control panel.
- ⚙️ Complete the system configuration module with advanced customization options.

---

## Author

Jose Daniel Carrascal Pineda

Systems Engineering Student

2026

---

## License

This project is distributed under the MIT license.


---

---

---

# 🇪🇦 🇨🇴 Español

# Sistema de gestión para tiendas minoristas

## Descripción

El sistema es una aplicación de escritorio diseñada para gestionar la operación de una tienda minorista y el proceso de venta unificado de productos y servicios. Permite administrar configuraciones globales, catálogos de productos y servicios, múltiples inventarios aislados, reglas comerciales (impuestos, descuentos y políticas de vencimiento), así como la gestión completa de usuarios, roles y permisos bajo un modelo de control de acceso estricto. Está orientada a la operación de una tienda centralizada, diseñada para responder a requisitos reales de negocio, consistencia contable e integridad de datos.

El proyecto prioriza un modelado del dominio rico y desacoplado de la infraestructura. La elección del dominio minorista responde a la diversidad de escenarios complejos que permite abordar: control riguroso de stock, cálculo financiero de alta precisión con BigDecimal, manejo polimórfico de ítems facturables, procesamiento de facturas bajo transacciones atómicas y un esquema de seguridad de defensa en profundidad.

El proyecto nació como un ejercicio de consola enfocado en dominar la Programación Orientada a Objetos y evolucionó progresivamente hasta convertirse en una arquitectura por capas completa con interfaz gráfica en JavaFX. Desarrollado intencionalmente sin frameworks como Spring Boot, el proyecto busca comprender los fundamentos de bajo nivel en Java: persistencia nativa con JDBC y HikariCP, implementación manual de inyección de dependencias, abstracción transaccional, patrones de diseño para extensibilidad (Estrategia y Mapeadores), resiliencia criptográfica (Argon2) y una estructura desacoplada lista para suites de pruebas automatizadas con JUnit 5 y Mockito.

---

## 🚧 Estado del proyecto

**Versión actual:** 1.1.1

| Módulo               | Estado            |
|----------------------|-------------------|
| Gestión de la tienda | ✅ Finalizado      |
| Punto de venta       | ✅ Finalizado      |
| Gestion de usuarios  | ✅ Finalizado      |
| Login                | ✅ Finalizado      |
| Documentación        | 🚧 En desarrollo  |
| Testing              | 🚧 En desarrollo  |
| Versión 1.1.1        | ✅ Finalizado      |

### Proximo Objetivo

Implementar **Testing Automatizado** en todo el proyecto.

---

## Características

### 🏪 Gestión de la Tienda
- Configuración global de la tienda.
- Administración de información general del negocio.

### 📦 Gestión de Productos
- Registro, consulta, actualización e inactivación (borrado lógico) de productos para preservar el historial de ventas.
- Soporte para múltiples tipos de productos con comportamientos específicos.
- Asociación de características propias según el tipo de producto.
- Control del estado de disponibilidad de los productos.

### 🛠️ Gestión de Servicios
- Administración independiente de servicios.
- Tratamiento de productos y servicios como ítems facturables dentro del proceso de venta.
- Control del estado de disponibilidad de los servicios.

### 📚 Gestión de Inventarios
- Administración de múltiples inventarios.
- Asociación de cualquier tipo de producto a un inventario.
- Control del stock por inventario.

### 💰 Gestión Comercial
- Administración de impuestos y descuentos.
- Gestión de precios utilizando `BigDecimal` para garantizar precisión en cálculos monetarios.
- Configuración de políticas de vencimiento.
- Activación e inactivación de configuraciones comerciales para evitar romper referencias pasadas.

### 🧾 Facturación
- Generación de facturas con registro del detalle completo de la venta. 
- Aplicación automática de impuestos y descuentos configurados.
- Cálculo preciso de subtotales y totales sin utilizar datos de punto flotante.
- Procesamiento de ventas con garantía de atomicidad: si ocurre un error durante la facturación, se revierten automáticamente los cambios en el inventario para evitar inconsistencias.

### 🖥️ Punto de Venta
- Proceso de venta desde una interfaz gráfica interactiva. 
- Gestión unificada de carrito de compras, soportando la adición simultánea y transparente de productos y servicios. 
- Validación polimórfica en tiempo real: verificación estricta de stock para ítems físicos y validación de caducidad para productos perecederos antes de concretar la venta. 
- Generación automática de la factura y actualización del inventario en un flujo sincronizado.

### 👤 Gestión de Usuarios
- Registro centralizado de nuevos usuarios por parte de administradores. 
- Administración y actualización de cuentas existentes. 
- Activación e inactivación (borrado lógico) de usuarios sin pérdida de trazabilidad. 
- Asignación flexible de múltiples roles a un mismo usuario. 
- Restablecimiento seguro de contraseñas delegando credenciales temporales.

### 🔐 Autenticación
- Inicio de sesión mediante correo electrónico y contraseña. 
- Protección criptográfica avanzada de credenciales mediante el algoritmo Argon2. 
- Defensa activa contra ataques de enumeración de usuarios (User Enumeration) mediante la ejecución de un cómputo de hashing con cadena señuelo para unificar los tiempos de respuesta. 
- Detección del primer acceso y flujo obligatorio de cambio de contraseña temporal. 
- Bloqueo temporal preventivo de cuentas tras múltiples intentos fallidos para mitigar ataques de fuerza bruta, con tiempo de penalización configurable.

### 👥 Gestión de Roles
- Creación y edición de roles personalizados. 
- Activación e inactivación de roles para retiro seguro de operación. 
- Asignación modular y remoción de permisos asociados a cada rol.

### 🛡️ Gestión de Permisos
- Administración centralizada de permisos existentes, los cuales mapean unívocamente a acciones definidas en el código del sistema. 
- Activación e inactivación de permisos. 
- Esquema de seguridad de doble validación: Control de acceso restrictivo en la interfaz visual, respaldado obligatoriamente por una revalidación criptográfica o de sesión en los orquestadores del backend antes de cualquier ejecución.

---

## Screenshots

### 1. Menu Principal

<p>
  <img src="docs/images/spanish/screenshots/Menu_Principal.png" alt="Menu Principal" width="1605">
</p>

Punto de entrada de la aplicación. Centraliza la navegación hacia los diferentes módulos del sistema y proporciona acceso a las funcionalidades principales de gestión y ventas.

### 2. Gestion de la Tienda

<p>
  <img src="docs/images/spanish/screenshots/Gestion_Tienda.png" alt="Gestion de la Tienda" width="1611">
</p>

Panel central de administración desde el que se accede a los diferentes módulos de gestión del sistema. Permite administrar configuraciones, inventarios, servicios, impuestos, descuentos y políticas de vencimiento desde una única interfaz.

### 3. Gestion de Inventarios

<p>
  <img src="docs/images/spanish/screenshots/Gestion_Inventarios.png" alt="Gestion de Inventarios" width="1603">
</p>

Permite administrar los inventarios de la tienda, consultar su capacidad máxima, el stock ocupado y el espacio disponible. Desde este módulo es posible crear nuevos inventarios, modificar su información y acceder a la gestión de los productos almacenados en cada uno.

### 4. Gestion de Productos

<p>
  <img src="docs/images/spanish/screenshots/Gestion_Productos.png" alt="Gestion de Productos" width="1603">
</p>

Permite administrar los productos de un inventario mediante una vista general y vistas especializadas por tipo de producto. Incluye búsqueda, filtros por disponibilidad, control de stock, transferencia entre inventarios y cambio de estado sin eliminar el historial de la información.

### 5. Gestion de Servicios

<p>
  <img src="docs/images/spanish/screenshots/Gestion_Servicios.png" alt="Gestion de Servicios" width="1607">
</p>

Permite administrar el catálogo de servicios ofrecidos por la tienda. Incluye la configuración de precios, impuestos, descuentos y estado de disponibilidad, manteniendo un proceso de facturación consistente con el de los productos.

### 6. Configuraciones comerciales

<p>
  <img src="docs/images/spanish/screenshots/Configuraciones_Comerciales.png" alt="Configuraciones comerciales" width="1605">
</p>

Módulo encargado de administrar las reglas comerciales utilizadas por el sistema. Permite gestionar impuestos, descuentos y políticas de vencimiento que se aplican automáticamente durante la administración de productos, servicios y el proceso de venta, garantizando la consistencia de los cálculos y las reglas de negocio.

### 7. Gestion Configuraciones

<p>
  <img src="docs/images/spanish/screenshots/Gestion_Configuraciones.png" alt="Gestion de Configuraciones" width="1605">
</p>

Panel central para administrar las configuraciones generales de la tienda y del sistema. Permite gestionar el nombre de la tienda, acceder a la administración de roles y permisos, y modificar las políticas de bloqueo, centralizando las opciones de configuración disponibles para la operación y el control de acceso de la aplicación.

### 8. Panel de control POS (Punto de Venta)

<p>
  <img src="docs/images/spanish/screenshots/Panel_De_Control_POS.png" alt="Panel de Control POS" width="1607">
</p>

Pantalla principal del módulo de punto de venta. Presenta un resumen de la actividad del día, incluyendo las ventas realizadas, el número de facturas emitidas y el valor de la última venta. Además, permite acceder rápidamente a la creación de una nueva venta y consultar el historial de ventas registradas.

### 9. Atención al Cliente (Nueva Venta)

<p>
  <img src="docs/images/spanish/screenshots/Atencion_Al_Cliente_Nueva_Venta.png" alt="Atención al cliente" width="1602">
</p>

Pantalla principal del proceso de venta. Permite agregar productos mediante su código, administrar las cantidades de cada artículo, eliminar productos del carrito y visualizar en tiempo real el subtotal, los impuestos y el total a pagar. Desde esta interfaz también es posible cancelar la venta o finalizar la compra para generar la transacción.

### 10. Reporte de Recaudo

<p>
  <img src="docs/images/spanish/screenshots/Reporte_Recaudo.png" alt="Reporte Recaudo" width="1606">
</p>

Ventana de generación de reportes de recaudo accesible desde el módulo Punto de Venta. Permite seleccionar un rango de fechas para calcular las facturas emitidas, el subtotal recaudado, los impuestos generados y el total de ventas correspondiente al período seleccionado.

### 11. Factura

<p>
  <img src="docs/images/spanish/screenshots/Factura_Generada.png" alt="Factura Generada" width="1606">
</p>

Al finalizar una venta, el sistema genera automáticamente un comprobante con el detalle de los productos y servicios vendidos, cantidades, precios, impuestos, subtotal y total de la transacción. Esta factura resume la operación realizada y permite confirmar el cierre exitoso de la venta.

### 12. Login

<p>
  <img src="docs/images/spanish/screenshots/Login.png" alt="Login" width="740">
</p>

Pantalla de autenticación de la aplicación. Permite a los usuarios ingresar mediante su correo electrónico y contraseña, validando sus credenciales antes de proporcionar acceso al sistema. Incluye opciones para visualizar la contraseña durante su ingreso y salir de la aplicación.

### 13.Gestion de Permisos

<p>
  <img src="docs/images/spanish/screenshots/Gestion_Permisos.png" alt="Gestion de Permisos" width="1607">
</p>

Permite consultar y administrar los permisos disponibles en el sistema. Incluye búsqueda por nombre, filtros por estado y módulo, así como la activación e inactivación de permisos. Cada permiso muestra su módulo asociado y una descripción de la acción que autoriza, facilitando la administración y el control de acceso a las funcionalidades de la aplicación.

### 14. Gestion de Roles

<p>
  <img src="docs/images/spanish/screenshots/Gestion_Roles.png" alt="Gestion de Roles" width="1606">
</p>

Permite administrar los roles del sistema desde una vista centralizada. Incluye la búsqueda y consulta de los roles existentes, la creación de nuevos roles, la modificación de su información y la administración de los permisos asociados a cada uno. También muestra el estado de disponibilidad de los roles, permitiendo controlar su participación en el sistema mediante su activación o inactivación.

### 15. Gestion de Usuarios

<p>
  <img src="docs/images/spanish/screenshots/Gestion_Usuarios.png" alt="Gestion de Usuarios" width="1606">
</p>

Permite administrar los usuarios registrados en el sistema desde una vista centralizada. Incluye búsqueda por identificador, nombre o correo electrónico, registro de nuevos usuarios, edición de información, activación e inactivación de cuentas y gestión de los roles asignados. También permite restablecer la contraseña de un usuario mediante una contraseña temporal.

---

## Decisiones de Diseño

Las siguientes decisiones de diseño reflejan los principios que guiaron el desarrollo del proyecto. En cada caso se buscó construir un modelo de dominio coherente con el funcionamiento de una tienda minorista, priorizando la claridad del diseño, la mantenibilidad del código y la integridad de la información antes que la rapidez de implementación.

### 1. Arquitectura Limpia y Aislamiento Estricto del Dominio
   
La aplicación impone fronteras rígidas entre la interfaz de usuario, la orquestación (servicios de aplicación), el dominio y la infraestructura. La capa de presentación jamás interactúa directamente con las entidades del negocio; la transferencia de datos fluye exclusivamente a través de DTO construidos por clases Ensambladoras. Este aislamiento garantiza que los cambios en la UI no contaminen las reglas de negocio y que el modelo de dominio no se vea condicionado por los requerimientos de visualización.

### 2. Infraestructura Transaccional Centralizada (Patrón Unit of Work)
   
Se implementó un GestorTransaccional personalizado que administra el ciclo de vida de las conexiones mediante ThreadLocal. Esta abstracción permite ejecutar bloques lógicos complejos garantizando propiedades ACID sin acoplar la capa de servicios a la API de JDBC. El gestor maneja escenarios avanzados de propagación (burbujas transaccionales independientes para auditoría de seguridad), control defensivo de los recursos y prevención de polución de conexiones en el pool mediante el restablecimiento estricto del estado autoCommit.

### 3. Persistencia Agnóstica de Bajo Nivel y Mapeadores Dedicados
   
Se descartó el uso de ORM`s comerciales a favor de JDBC puro para mantener un control absoluto sobre el rendimiento, la ejecución SQL y el mapeo en memoria. Para evitar que los repositorios asuman múltiples responsabilidades, la hidratación de objetos (traducción de ResultSet a Entidades) se delegó a componentes Mapeadores especializados. Esto mantiene los repositorios enfocados exclusivamente en la ejecución de comandos DML y consultas estructurales.

### 4. Persistencia Polimórfica (Patrón Estrategia y Cumplimiento OCP)
   
El guardado y recuperación de productos con distintas naturalezas (Ropa, Perecederos) se resolvió aplicando el Patrón Estrategia en el RepositorioProducto. En lugar de utilizar estructuras de control condicionales que degradan el código con cada nuevo tipo de producto, el repositorio delega dinámicamente la ejecución SQL a la estrategia correspondiente. Esto garantiza el Principio de Abierto/Cerrado (OCP), permitiendo la integración de futuras familias de productos sin alterar una sola línea de la lógica de persistencia existente.

### 5. Modelo de Dominio Segregado (Inventariables vs. Servicios)

El catálogo de ventas separa semánticamente los bienes físicos de los servicios. Aunque ambos implementan el contrato ItemFacturable para converger polimórficamente en el carrito de compras, mantienen un comportamiento dispar: los productos están sujetos a una interfaz de capacidad (Inventariable) para el control estricto de stock, mientras que los servicios operan sin restricciones de existencias. Esto respeta el Principio de Segregación de Interfaces (ISP) y evita la inyección de validaciones nulas o métodos fantasma en los servicios.

### 6. Alta Cohesión y Desacoplamiento en la Capa de Aplicación

La capa de servicios sigue el Principio de Responsabilidad Única (SRP), evitando los "God Objects". Funciones dispares que antes convivían en clases monolíticas fueron segregadas en servicios de alcance estrecho (por ejemplo, la bifurcación de la lógica de autenticación hacia un ServicioLogin autónomo, dejando al ServicioUsuario enfocado en la administración CRUD). Los servicios actúan como orquestadores delgados que delegan las verdaderas reglas de negocio a las entidades del dominio.

### 7. Precisión Financiera Inmutable (BigDecimal)
   
El sistema erradica por completo el uso de tipos primitivos de punto flotante (float, double) para el manejo de valores monetarios. Toda operación aritmética relacionada con precios base, aplicación de catálogos de impuestos, descuentos y totalización de facturas se ejecuta mediante BigDecimal. Esta directriz arquitectónica es innegociable para evitar desviaciones por errores de representación binaria, garantizando exactitud contable a nivel transaccional.

### 8. Autorización RBAC y Defensa en Profundidad (Doble Validación)
   
El control de acceso se basa en un esquema de Roles y Permisos (RBAC), donde los permisos no se crean dinámicamente, sino que mapean directamente a operaciones de código preexistentes. Se emplea un enfoque de confianza cero (Zero Trust) en la comunicación UI-Backend: aunque la vista oculta componentes según el rol, cada Orquestador reválida criptográficamente o por sesión que el usuario posea la autorización exacta antes de ejecutar cualquier mutación en el sistema.

### 9. Resiliencia Criptográfica y Anti-Enumeración en Autenticación
   
El mecanismo de autenticación va más allá del simple hashing. Utiliza Argon2 para resistir ataques de fuerza bruta por hardware (ASIC/GPU). Se implementaron contramedidas para ataques de enumeración (User Enumeration): cuando se ingresa un correo inexistente, el sistema ejecuta un cómputo de hashing con una cadena señuelo para unificar los tiempos de respuesta. Además, las contraseñas temporales no se almacenan como tales, sino que fuerzan un flujo de renovación obligatorio en el primer ingreso.

### 10. Inmutabilidad del Historial y Aislamiento de Múltiples Inventarios
    
El modelo relacional protege la integridad histórica de las operaciones comerciales. Entidades clave como productos, tasas de impuestos y descuentos operan bajo un modelo de borrado lógico (Activación/Inactivación), impidiendo que se rompan las claves foráneas de facturas emitidas en el pasado. Además, la arquitectura de stock soporta multi-inventario nativo, aislando las cantidades disponibles por almacén físico sin replicar la definición base del producto.

---

## Tecnologías utilizadas

| Tecnología | Uso                                                        |
|------------|------------------------------------------------------------|
| Java       | Lógica de negocio                                          |
| JavaFX     | Interfaz gráfica de escritorio                             |
| FXML       | Definición de las vistas de la interfaz                    |
| CSS        | Estilos de la interfaz gráfica                             |
| JDBC       | Persistencia de datos                                      |
| HikariCP   | Pool de conexiones JDBC                                    |
| Argon2     | Codificador de contraseñas                                 |
| MySQL      | Base de datos relacional                                   |
| Maven      | Gestión de dependencias y construcción del proyecto        |
| JUnit 5    | Framework para pruebas unitarias automatizadas             |
| Mockito    | Simulación de objetos (mocks) para aislamiento en pruebas  |

---

## Instalación y Ejecución

### Requisitos

- Java 26 o superior
- Maven
- MySQL
- Git

### 1. Clonar el repositorio

Descarga una copia local del proyecto ejecutando:

```bash
git clone https://github.com/carrascalpjosedanieldev/retail-management-system.git
cd retail-management-system
```

### 2. Configurar la base de datos

En la carpeta **database/** se incluye una copia de la base de datos del proyecto.

Importa el archivo SQL en MySQL. El script crea automáticamente la base de datos **"retail_management_system"** si no existe.

### 3. Configurar la conexión

Renombra el archivo:

```text
application.properties.example
```

por

```text
application.properties
```

y configura los siguientes datos:

- URL de la base de datos (Por defecto está con el nombre de la base de datos que el script crea)
- Usuario
- Contraseña

Para más comodidad las instrucciones también están en ```application.properties.example```

### 4. Ejecutar el proyecto

Abre el proyecto con tu IDE preferido compatible con Maven (IntelliJ IDEA), espera a que Maven descargue las dependencias y ejecuta la clase principal de la aplicación.

---

## Arquitectura

### Descripción General

El sistema está organizado siguiendo una arquitectura por capas, donde cada componente posee una responsabilidad claramente definida. La interfaz gráfica se encarga de la interacción con el usuario y se comunica con la aplicación mediante orquestadores. Estos actúan como punto de entrada a las operaciones de negocio e implementan un esquema de seguridad de defensa en profundidad (Zero Trust): respaldan las restricciones visuales de la interfaz revalidando de forma estricta los permisos del usuario antes de permitir cualquier ejecución.

Una vez superada la barrera de autorización, los orquestadores coordinan el uso de los servicios de dominio y transforman las entidades resultantes en DTO, entregando a la capa de presentación únicamente la información necesaria. La verdadera lógica del negocio reside encapsulada dentro de estos servicios, mientras que el acceso a los datos se encuentra completamente desacoplado mediante el uso de puertos (interfaces) e implementaciones nativas en JDBC puro para MySQL.

Esta organización permite separar la presentación, el control de acceso, la lógica de negocio y la persistencia, favoreciendo el mantenimiento del código y permitiendo modificar o reemplazar componentes de una capa sin afectar directamente a las demás.

### Arquitectura por capas

Basada en la separación de responsabilidades y el desacoplamiento entre la presentación, la coordinación de los casos de uso, la lógica de negocio y la persistencia. La interfaz gráfica interactúa con la aplicación mediante orquestadores, los servicios encapsulan las reglas de negocio y la persistencia se abstrae mediante puertos e implementaciones específicas para MySQL.

<p>
  <img src="docs/images/spanish/diagramas/Diagrama_Arquitectura_de_Capas.png" alt="Arquitectura por capas" width="404">
</p>

**Figura 1.** Arquitectura general del sistema organizada por capas. Cada capa depende únicamente de la inmediatamente inferior, favoreciendo el desacoplamiento entre la interfaz de usuario, las validaciones de permisos, la lógica de negocio y la infraestructura de persistencia.

|                                                                                                                                      Flujo de una petición                                                                                                                                      |                                                                                                                                Flujo de una respuesta                                                                                                                                 |
|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
|                                                                                                              <img src="docs/images/spanish/diagramas/Diagrama_Flujo_Peticion.png">                                                                                                              |                                                                                                        <img src="docs/images/spanish/diagramas/Diagrama_Flujo_Respuesta.png">                                                                                                         |
| **Figura 2.** Flujo de una petición desde la interacción del usuario hasta la ejecución de una operación en la base de datos. Cada capa realiza únicamente las responsabilidades que le corresponden, manteniendo el desacoplamiento entre la interfaz, la lógica de negocio y la persistencia. | **Figura 3.** Flujo de una respuesta desde la persistencia hasta la interfaz de usuario. Antes de llegar a la capa de presentación, las entidades del dominio son transformadas en DTO mediante ensambladores para evitar que la interfaz manipule directamente el modelo de dominio. |

### Estructura de paquetes

**Código Fuente**
```text
src/
main/
java/
RetailManagementSystem
│
├── aplicacion 
│   ├── dto
│   │   ├── comercial
│   │   ├── consultas
│   │   ├── gestion
│   │   ├── seguridad
│   │   └── ventas
│   ├── ensambladores
│   ├── fabricas
│   ├── orquestadores
│   ├── puertos
│   └── servicios
│
├── dominio
│   ├── entidades
│   │   ├── comercial
│   │   ├── gestion
│   │   ├── seguridad
│   │   └── ventas
│   ├── enums
│   ├── excepciones
│   │   ├── autenticacionYSeguridad
│   │   ├── conflictos
│   │   ├── recursosNoEncontrados
│   │   └── reglasDeNegocio
│   └── puertos
│       ├── repositorios
│       └── transacciones
│
├── infraestructura
│   ├── configuracion
│   ├── inyeccion
│   ├── persistencia
│   │   ├── excepciones
│   │   └── mysql
│   │       ├── conexiones
│   │       ├── estrategias
│   │       ├── mappers
│   │       └── repositorios
│   └── seguridad
│
├── vista
│   ├── configuracion
│   ├── controladores
│   │   ├── gestionarTienda
│   │   ├── gestionarUsuarios
│   │   ├── login
│   │   ├── menuPrincipal
│   │   └── puntoDeVenta
│   ├── excepciones
│   └── utilidades
│
├── App
└── Ejecutable
```

**Recursos**
```text
src/
main/
resources
│
├── css
│   ├── alertas
│   ├── gestionarTienda
│   ├── gestionarUsuarios
│   ├── login
│   ├── menuPrincipal
│   └── puntoDeVenta
│
├── vista
│   ├── gestionarTienda
│   ├── gestionarUsuarios
│   ├── login
│   ├── menuPrincipal
│   └── puntoDeVenta
│
├── application.example.properties
└── application.properties

```

La estructura del proyecto está organizada siguiendo una arquitectura por capas. Cada paquete agrupa componentes con una responsabilidad específica, favoreciendo la separación de responsabilidades y el desacoplamiento entre la interfaz de usuario, la lógica de negocio, el dominio y la infraestructura.

| Paquete           | Responsabilidad y Componentes Clave                                                                                                                                                                                                                                                                                                                                                                                   |
|-------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `aplicacion`      | **Orquestación y Casos de Uso:** Coordina el flujo de la aplicación mediante Orquestadores y Servicios de aplicación.<br><br>**Transferencia de Datos:** Contiene DTOs, Ensambladores y Fábricas para comunicar la UI con el backend sin exponer las entidades del dominio.                                                                                                                                           |
| `dominio`         | **Núcleo del Negocio:** Modela la lógica del sistema a través de Entidades, Valores y Enumeraciones puras.<br><br>**Abstracciones:** Define las Excepciones del negocio y los Puertos (interfaces) que determinan los contratos de acceso a datos.                                                                                                                                                                    |
| `infraestructura` | **Persistencia & Transacciones:** Implementación JDBC/MySQL con pool HikariCP, `GestorTransaccional` y Mapeadores dedicados.<br><br>**Seguridad & Patrones:** Repositorios polimórficos basados en el patrón Estrategia (OCP), hashing criptográfico Argon2 e inyección de dependencias.                                                                                                                              |
| `vista`           | **Controladores y Navegación:** Controladores JavaFX, gestión centralizada del flujo de pantallas con `CargadorVistas` y definición de `RutasVistas`.<br><br>**Utilidades & Resiliencia Visual:** `GestorAlertas` para retroalimentación estandarizada, `FormateadorNumeros` para valores monetarios, excepciones de UI y `ConfiguradorExcepciones` para la captura temprana de fallos en el arranque de la vista.    |

---

## 🗺️ Roadmap

Las siguientes funcionalidades están planificadas para futuras versiones del sistema:

- 🔐 Implementar testing automatizado.
- 💻 Ampliar el catálogo del sistema con una nueva categoría de productos tecnológicos.
- 🌐 Agregar soporte para múltiples idiomas (Español e Inglés).
- 📄 Permitir la exportación e impresión de facturas y reportes en formatos PDF y Excel.
- 📊 Incorporar indicadores y estadísticas para el panel de control.
- ⚙️ Completar el módulo de configuración del sistema con opciones avanzadas de personalización.

---

## Autor

Jose Daniel Carrascal Pineda

Estudiante de Ing. de Sistemas

2026

---

## Licencia

Este proyecto se distribuye bajo la licencia MIT.

