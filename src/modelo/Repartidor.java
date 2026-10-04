package modelo;

/**
 * Clase que representa un repartidor guardado en la tabla repartidores
 */
public class Repartidor {

    private int id;        /* Identificador del repartidor, lo genera la base de datos */
    private String nombre; /* Nombre del repartidor */

    /**
     * Constructor para un repartidor nuevo, aún sin id
     */
    public Repartidor(String nombre) {
        this.id     = 0;      /* Aún no se guarda, la BD asigna el id */
        this.nombre = nombre; /* Definir nombre del repartidor */
    }

    /**
     * Constructor con todos los parametros, se usa al leer un repartidor desde la base de datos
     */
    public Repartidor(int id, String nombre) {
        this.id     = id;     /* Definir identificador del repartidor */
        this.nombre = nombre; /* Definir nombre del repartidor */
    }

    /**
     * Metodo get para devolver los datos del repartidor
     */
    public int getId() {
        return id; /* Devolver identificador del repartidor */
    }

    public String getNombre() {
        return nombre; /* Devolver nombre del repartidor */
    }

    /**
     * Metodo set para modificar los datos del repartidor
     */
    public void setId(int id) {
        this.id = id; /* Definir id generado por la base de datos */
    }

    public void setNombre(String nombre) {
        this.nombre = nombre; /* Definir nuevo nombre */
    }

    /**
     * Sobreescritura del metodo To String, es el texto que se ve en los JComboBox
     */
    @Override
    public String toString() {
        return id + " - " + nombre;
    }
}
