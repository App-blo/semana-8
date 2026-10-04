package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase que gestiona la conexión JDBC con la base de datos MySQL de SpeedFast
 */
public class ConexionDB {

    private static final String URL      = "jdbc:mysql://localhost:3306/speedfast_db"; /* Dirección de la base de datos */
    private static final String USER     = "root";                                     /* Usuario de MySQL */
    private static final String PASSWORD = "tu_contraseña";                            /* Cambiar por la contraseña de tu MySQL */

    /**
     * Abre y devuelve una conexión con la base de datos
     */
    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Cierra los recursos JDBC recibidos (ResultSet, Statement, Connection),
     * se llama desde el finally de cada metodo de los DAO
     */
    public static void cerrar(AutoCloseable... recursos) {
        for (AutoCloseable recurso : recursos) {
            try {
                if (recurso != null) {
                    recurso.close(); /* Cerrar el recurso si fue creado */
                }
            } catch (Exception e) {
                System.out.println("Error al cerrar un recurso: " + e.getMessage());
            }
        }
    }
}
