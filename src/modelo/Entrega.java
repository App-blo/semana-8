package modelo;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Clase que representa una entrega, relaciona un pedido con un repartidor
 */
public class Entrega {

    private int id;                  /* Identificador de la entrega, lo genera la base de datos */
    private int idPedido;            /* Pedido que se entrega */
    private int idRepartidor;        /* Repartidor que realiza la entrega */
    private LocalDate fecha;         /* Fecha de la entrega */
    private LocalTime hora;          /* Hora de la entrega */
    private String direccionPedido;  /* Dirección del pedido, solo para mostrar en la tabla */
    private String nombreRepartidor; /* Nombre del repartidor, solo para mostrar en la tabla */

    /**
     * Constructor para una entrega nueva, aún sin id
     */
    public Entrega(int idPedido, int idRepartidor, LocalDate fecha, LocalTime hora) {
        this.id           = 0;            /* Aún no se guarda, la BD asigna el id */
        this.idPedido     = idPedido;     /* Definir pedido */
        this.idRepartidor = idRepartidor; /* Definir repartidor */
        this.fecha        = fecha;        /* Definir fecha */
        this.hora         = hora;         /* Definir hora */
    }

    /**
     * Constructor con todos los parametros, se usa al leer una entrega desde la base de datos
     */
    public Entrega(int id, int idPedido, int idRepartidor, LocalDate fecha, LocalTime hora) {
        this.id           = id;           /* Definir identificador de la entrega */
        this.idPedido     = idPedido;     /* Definir pedido */
        this.idRepartidor = idRepartidor; /* Definir repartidor */
        this.fecha        = fecha;        /* Definir fecha */
        this.hora         = hora;         /* Definir hora */
    }

    /**
     * Metodo get para devolver los datos de la entrega
     */
    public int getId() {
        return id; /* Devolver identificador de la entrega */
    }

    public int getIdPedido() {
        return idPedido; /* Devolver id del pedido */
    }

    public int getIdRepartidor() {
        return idRepartidor; /* Devolver id del repartidor */
    }

    public LocalDate getFecha() {
        return fecha; /* Devolver fecha */
    }

    public LocalTime getHora() {
        return hora; /* Devolver hora */
    }

    public String getDireccionPedido() {
        return direccionPedido; /* Devolver dirección del pedido */
    }

    public String getNombreRepartidor() {
        return nombreRepartidor; /* Devolver nombre del repartidor */
    }

    /**
     * Metodo set para modificar los datos de la entrega
     */
    public void setId(int id) {
        this.id = id; /* Definir id generado por la base de datos */
    }

    public void setDireccionPedido(String direccionPedido) {
        this.direccionPedido = direccionPedido; /* Definir dirección para mostrar */
    }

    public void setNombreRepartidor(String nombreRepartidor) {
        this.nombreRepartidor = nombreRepartidor; /* Definir nombre para mostrar */
    }
}
