package dao;

import modelo.EstadoPedido;
import modelo.Pedido;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase PedidoDAO con las operaciones CRUD de la tabla pedidos.
 * Los metodos vuelven a lanzar la SQLException para que la vista le muestre
 * un mensaje claro al usuario
 */
public class PedidoDAO {

    /**
     * Inserta un pedido y le asigna el id generado por la base de datos
     */
    public void create(Pedido pedido) throws SQLException {

        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet clavesGeneradas = null;

        try {
            conexion = ConexionDB.conectar();

            String sql = "INSERT INTO pedidos (direccion, tipo, estado) VALUES (?, ?, ?)";
            sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            sentencia.setString(1, pedido.getDireccionEntrega());
            sentencia.setString(2, pedido.getTipoPedido());
            sentencia.setString(3, pedido.getEstado().name());
            sentencia.executeUpdate();

            /* Recuperar el id que generó AUTO_INCREMENT */
            clavesGeneradas = sentencia.getGeneratedKeys();
            if (clavesGeneradas.next()) {
                pedido.setIdPedido(clavesGeneradas.getInt(1));
            }

        } catch (SQLException e) {
            System.out.println("Error al crear el pedido: " + e.getMessage());
            throw e;
        } finally {
            ConexionDB.cerrar(clavesGeneradas, sentencia, conexion);
        }
    }

    /**
     * Lee todos los pedidos de la base de datos
     */
    public List<Pedido> readAll() throws SQLException {
        return readFiltrado(null, null);
    }

    /**
     * Lee los pedidos aplicando filtros opcionales
     * @param tipo   Tipo de pedido, null para no filtrar por tipo
     * @param estado Estado del pedido, null para no filtrar por estado
     */
    public List<Pedido> readFiltrado(String tipo, String estado) throws SQLException {

        List<Pedido> pedidos = new ArrayList<>();
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            conexion = ConexionDB.conectar();

            /* Armar la consulta agregando solo los filtros que se pidieron */
            String sql = "SELECT id, direccion, tipo, estado FROM pedidos WHERE 1 = 1";
            if (tipo != null) {
                sql += " AND tipo = ?";
            }
            if (estado != null) {
                sql += " AND estado = ?";
            }
            sql += " ORDER BY id";

            sentencia = conexion.prepareStatement(sql);

            int posicion = 1;
            if (tipo != null) {
                sentencia.setString(posicion++, tipo);
            }
            if (estado != null) {
                sentencia.setString(posicion++, estado);
            }

            resultado = sentencia.executeQuery();

            /* Crear un objeto Pedido por cada fila de la tabla */
            while (resultado.next()) {
                int id = resultado.getInt("id");
                String direccion = resultado.getString("direccion");
                String tipoPedido = resultado.getString("tipo");
                EstadoPedido estadoPedido = EstadoPedido.valueOf(resultado.getString("estado"));

                pedidos.add(new Pedido(id, direccion, tipoPedido, estadoPedido));
            }

        } catch (SQLException e) {
            System.out.println("Error al leer los pedidos: " + e.getMessage());
            throw e;
        } finally {
            ConexionDB.cerrar(resultado, sentencia, conexion);
        }

        return pedidos;
    }

    /**
     * Actualiza dirección, tipo y estado de un pedido existente
     * @return true si se actualizó una fila, false si el id no existe
     */
    public boolean update(Pedido pedido) throws SQLException {

        Connection conexion = null;
        PreparedStatement sentencia = null;

        try {
            conexion = ConexionDB.conectar();
            sentencia = conexion.prepareStatement("UPDATE pedidos SET direccion = ?, tipo = ?, estado = ? WHERE id = ?");
            sentencia.setString(1, pedido.getDireccionEntrega());
            sentencia.setString(2, pedido.getTipoPedido());
            sentencia.setString(3, pedido.getEstado().name());
            sentencia.setInt(4, pedido.getIdPedido());

            return sentencia.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar el pedido: " + e.getMessage());
            throw e;
        } finally {
            ConexionDB.cerrar(sentencia, conexion);
        }
    }

    /**
     * Elimina un pedido por su id
     * @return true si se eliminó una fila, false si el id no existe
     */
    public boolean delete(int id) throws SQLException {

        Connection conexion = null;
        PreparedStatement sentencia = null;

        try {
            conexion = ConexionDB.conectar();
            sentencia = conexion.prepareStatement("DELETE FROM pedidos WHERE id = ?");
            sentencia.setInt(1, id);

            return sentencia.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar el pedido: " + e.getMessage());
            throw e;
        } finally {
            ConexionDB.cerrar(sentencia, conexion);
        }
    }
}
