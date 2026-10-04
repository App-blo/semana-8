# SpeedFast - Gestión de Pedidos con CRUD y JDBC

## Autor del proyecto
- **Nombre completo:** Pablo Ibacache
- **Carrera:** Analista Programador
- **Sede:** Campus Virtual

---

## Descripción general del sistema
Aplicación de escritorio en Java Swing para la empresa de reparto SpeedFast, conectada a una base de datos MySQL mediante JDBC. Permite gestionar repartidores, pedidos y entregas con operaciones CRUD completas (crear, leer, actualizar y eliminar). Cada entidad tiene su propia clase DAO que usa `PreparedStatement` y `ResultSet`, y las ventanas validan los datos antes de ejecutar cada operación y muestran mensajes claros cuando ocurre un error de base de datos. El proyecto sigue la estructura modelo, dao, controlador y vista.

---

## Estructura general del proyecto

```plaintext
semana 8/
├── lib/
│   └── mysql-connector-j-8.4.0.jar  # Conector JDBC de MySQL
├── sql/
│   └── speedfast_db.sql             # Script de creación de la base de datos y las tablas
├── src/
│   ├── modelo/
│   │   ├── Pedido.java              # Id, dirección, tipo y estado del pedido
│   │   ├── EstadoPedido.java        # Enum: PENDIENTE, EN_REPARTO, ENTREGADO
│   │   ├── Repartidor.java          # Id y nombre del repartidor
│   │   └── Entrega.java             # Relación entre pedido y repartidor, con fecha y hora
│   ├── dao/
│   │   ├── ConexionDB.java          # Conexión JDBC con DriverManager y cierre de recursos
│   │   ├── RepartidorDAO.java       # create, readAll, update y delete
│   │   ├── PedidoDAO.java           # create, readAll, readFiltrado, update y delete
│   │   └── EntregaDAO.java          # create, readAll, readFiltrado, update y delete
│   ├── controlador/
│   │   └── ControladorSpeedFast.java    # Conecta las ventanas con los DAO
│   ├── vista/
│   │   ├── VentanaPrincipal.java        # Menú principal con acceso a las ventanas de gestión
│   │   ├── VentanaRepartidores.java     # CRUD de repartidores con JTable
│   │   ├── VentanaPedidos.java          # CRUD de pedidos con JTable y filtros por tipo y estado
│   │   ├── VentanaEntregas.java         # CRUD de entregas con JComboBox y filtros por pedido y repartidor
│   │   ├── Mensajes.java                # Mensajes de error, advertencia y confirmación reutilizables
│   │   └── ModeloTablaSoloLectura.java  # DefaultTableModel con celdas no editables
│   └── main/
│       └── Main.java                # Punto de entrada, abre VentanaPrincipal
└── README.md
```

---

## Instrucciones para clonar y ejecutar el proyecto

1. Clona el repositorio desde GitHub:

```bash
git clone https://github.com/App-blo/semana-8
```

2. Abre el proyecto en **IntelliJ IDEA** y marca la carpeta `src/` como **Sources Root**.
3. Crea la base de datos ejecutando el script `sql/speedfast_db.sql` en MySQL Workbench o por consola.
4. Agrega el conector JDBC de MySQL al proyecto: **File > Project Structure > Libraries > + > Java** y selecciona `lib/mysql-connector-j-8.4.0.jar`.
5. En `src/dao/ConexionDB.java`, reemplaza `tu_contraseña` por la contraseña de tu usuario `root` de MySQL.
6. Ejecuta la clase `Main` ubicada en `src/main/Main.java`.
7. Desde la ventana principal puedes abrir:
   - **Gestión de Repartidores:** registrar, editar, eliminar y listar repartidores. Al seleccionar una fila de la tabla se cargan sus datos en el formulario.
   - **Gestión de Pedidos:** registrar, editar, eliminar y listar pedidos (dirección, tipo y estado). La tabla se puede filtrar por tipo y por estado.
   - **Gestión de Entregas:** registrar, editar, eliminar y listar entregas eligiendo el pedido y el repartidor desde combos cargados de la base de datos (se muestran como "id - texto"), con fecha y hora. La tabla se puede filtrar por pedido y por repartidor. Los combos se refrescan cada vez que la ventana vuelve a estar activa, así se ven los cambios hechos en las otras ventanas.

No se puede eliminar un pedido o un repartidor que tenga entregas asociadas: la aplicación avisa con un mensaje y no borra el registro.

---

**Repositorio GitHub:** https://github.com/App-blo/semana-8
**Fecha de entrega:** 04/10/2026

---

Duoc UC | Escuela de Informática y Telecomunicaciones | Semana 8
