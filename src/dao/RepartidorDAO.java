package dao;

import modelo.Repartidor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase RepartidorDAO con las operaciones CRUD de la tabla repartidores.
 * Los metodos vuelven a lanzar la SQLException para que la vista le muestre
 * un mensaje claro al usuario
 */
public class RepartidorDAO {

    /**
     * Inserta un repartidor y le asigna el id generado por la base de datos
     */
    public void create(Repartidor repartidor) throws SQLException {

        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet clavesGeneradas = null;

        try {
            conexion = ConexionDB.conectar();
            sentencia = conexion.prepareStatement("INSERT INTO repartidores (nombre) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS);
            sentencia.setString(1, repartidor.getNombre());
            sentencia.executeUpdate();

            /* Recuperar el id que generó AUTO_INCREMENT */
            clavesGeneradas = sentencia.getGeneratedKeys();
            if (clavesGeneradas.next()) {
                repartidor.setId(clavesGeneradas.getInt(1));
            }

        } catch (SQLException e) {
            System.out.println("Error al crear el repartidor: " + e.getMessage());
            throw e;
        } finally {
            ConexionDB.cerrar(clavesGeneradas, sentencia, conexion);
        }
    }

    /**
     * Lee todos los repartidores de la base de datos
     */
    public List<Repartidor> readAll() throws SQLException {

        List<Repartidor> repartidores = new ArrayList<>();
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            conexion = ConexionDB.conectar();
            sentencia = conexion.prepareStatement("SELECT id, nombre FROM repartidores ORDER BY id");
            resultado = sentencia.executeQuery();

            /* Crear un objeto Repartidor por cada fila de la tabla */
            while (resultado.next()) {
                repartidores.add(new Repartidor(resultado.getInt("id"), resultado.getString("nombre")));
            }

        } catch (SQLException e) {
            System.out.println("Error al leer los repartidores: " + e.getMessage());
            throw e;
        } finally {
            ConexionDB.cerrar(resultado, sentencia, conexion);
        }

        return repartidores;
    }

    /**
     * Actualiza el nombre de un repartidor existente
     * @return true si se actualizó una fila, false si el id no existe
     */
    public boolean update(Repartidor repartidor) throws SQLException {

        Connection conexion = null;
        PreparedStatement sentencia = null;

        try {
            conexion = ConexionDB.conectar();
            sentencia = conexion.prepareStatement("UPDATE repartidores SET nombre = ? WHERE id = ?");
            sentencia.setString(1, repartidor.getNombre());
            sentencia.setInt(2, repartidor.getId());

            return sentencia.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar el repartidor: " + e.getMessage());
            throw e;
        } finally {
            ConexionDB.cerrar(sentencia, conexion);
        }
    }

    /**
     * Elimina un repartidor por su id
     * @return true si se eliminó una fila, false si el id no existe
     */
    public boolean delete(int id) throws SQLException {

        Connection conexion = null;
        PreparedStatement sentencia = null;

        try {
            conexion = ConexionDB.conectar();
            sentencia = conexion.prepareStatement("DELETE FROM repartidores WHERE id = ?");
            sentencia.setInt(1, id);

            return sentencia.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar el repartidor: " + e.getMessage());
            throw e;
        } finally {
            ConexionDB.cerrar(sentencia, conexion);
        }
    }
}
