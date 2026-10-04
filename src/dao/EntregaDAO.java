package dao;

import modelo.Entrega;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase EntregaDAO con las operaciones CRUD de la tabla entregas.
 * Los metodos vuelven a lanzar la SQLException para que la vista le muestre
 * un mensaje claro al usuario
 */
public class EntregaDAO {

    /**
     * Inserta una entrega y le asigna el id generado por la base de datos
     */
    public void create(Entrega entrega) throws SQLException {

        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet clavesGeneradas = null;

        try {
            conexion = ConexionDB.conectar();

            String sql = "INSERT INTO entregas (id_pedido, id_repartidor, fecha, hora) VALUES (?, ?, ?, ?)";
            sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            sentencia.setInt(1, entrega.getIdPedido());
            sentencia.setInt(2, entrega.getIdRepartidor());
            sentencia.setDate(3, Date.valueOf(entrega.getFecha()));
            sentencia.setTime(4, Time.valueOf(entrega.getHora()));
            sentencia.executeUpdate();

            /* Recuperar el id que generó AUTO_INCREMENT */
            clavesGeneradas = sentencia.getGeneratedKeys();
            if (clavesGeneradas.next()) {
                entrega.setId(clavesGeneradas.getInt(1));
            }

        } catch (SQLException e) {
            System.out.println("Error al crear la entrega: " + e.getMessage());
            throw e;
        } finally {
            ConexionDB.cerrar(clavesGeneradas, sentencia, conexion);
        }
    }

    /**
     * Lee todas las entregas de la base de datos
     */
    public List<Entrega> readAll() throws SQLException {
        return readFiltrado(null, null);
    }

    /**
     * Lee las entregas aplicando filtros opcionales, trae también la dirección
     * del pedido y el nombre del repartidor para mostrarlos en la tabla
     * @param idPedido      Id del pedido, null para no filtrar por pedido
     * @param idRepartidor  Id del repartidor, null para no filtrar por repartidor
     */
    public List<Entrega> readFiltrado(Integer idPedido, Integer idRepartidor) throws SQLException {

        List<Entrega> entregas = new ArrayList<>();
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            conexion = ConexionDB.conectar();

            /* Armar la consulta agregando solo los filtros que se pidieron */
            String sql = "SELECT e.id, e.id_pedido, e.id_repartidor, e.fecha, e.hora, p.direccion, r.nombre "
                    + "FROM entregas e "
                    + "INNER JOIN pedidos p ON e.id_pedido = p.id "
                    + "INNER JOIN repartidores r ON e.id_repartidor = r.id "
                    + "WHERE 1 = 1";
            if (idPedido != null) {
                sql += " AND e.id_pedido = ?";
            }
            if (idRepartidor != null) {
                sql += " AND e.id_repartidor = ?";
            }
            sql += " ORDER BY e.id";

            sentencia = conexion.prepareStatement(sql);

            int posicion = 1;
            if (idPedido != null) {
                sentencia.setInt(posicion++, idPedido);
            }
            if (idRepartidor != null) {
                sentencia.setInt(posicion++, idRepartidor);
            }

            resultado = sentencia.executeQuery();

            /* Crear un objeto Entrega por cada fila del resultado */
            while (resultado.next()) {
                Entrega entrega = new Entrega(
                        resultado.getInt("id"),
                        resultado.getInt("id_pedido"),
                        resultado.getInt("id_repartidor"),
                        resultado.getDate("fecha").toLocalDate(),
                        resultado.getTime("hora").toLocalTime());
                entrega.setDireccionPedido(resultado.getString("direccion"));
                entrega.setNombreRepartidor(resultado.getString("nombre"));

                entregas.add(entrega);
            }

        } catch (SQLException e) {
            System.out.println("Error al leer las entregas: " + e.getMessage());
            throw e;
        } finally {
            ConexionDB.cerrar(resultado, sentencia, conexion);
        }

        return entregas;
    }

    /**
     * Actualiza pedido, repartidor, fecha y hora de una entrega existente
     * @return true si se actualizó una fila, false si el id no existe
     */
    public boolean update(Entrega entrega) throws SQLException {

        Connection conexion = null;
        PreparedStatement sentencia = null;

        try {
            conexion = ConexionDB.conectar();

            String sql = "UPDATE entregas SET id_pedido = ?, id_repartidor = ?, fecha = ?, hora = ? WHERE id = ?";
            sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, entrega.getIdPedido());
            sentencia.setInt(2, entrega.getIdRepartidor());
            sentencia.setDate(3, Date.valueOf(entrega.getFecha()));
            sentencia.setTime(4, Time.valueOf(entrega.getHora()));
            sentencia.setInt(5, entrega.getId());

            return sentencia.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar la entrega: " + e.getMessage());
            throw e;
        } finally {
            ConexionDB.cerrar(sentencia, conexion);
        }
    }

    /**
     * Elimina una entrega por su id
     * @return true si se eliminó una fila, false si el id no existe
     */
    public boolean delete(int id) throws SQLException {

        Connection conexion = null;
        PreparedStatement sentencia = null;

        try {
            conexion = ConexionDB.conectar();
            sentencia = conexion.prepareStatement("DELETE FROM entregas WHERE id = ?");
            sentencia.setInt(1, id);

            return sentencia.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar la entrega: " + e.getMessage());
            throw e;
        } finally {
            ConexionDB.cerrar(sentencia, conexion);
        }
    }
}
