package modelo;

/**
 * Clase que representa un pedido guardado en la tabla pedidos
 */
public class Pedido {

    private int idPedido;            /* Identificador del pedido, lo genera la base de datos */
    private String direccionEntrega; /* Dirección donde se debe entregar el pedido */
    private String tipoPedido;       /* Tipo de pedido: COMIDA, ENCOMIENDA o EXPRESS */
    private EstadoPedido estado;     /* Estado actual del pedido */

    /**
     * Constructor para un pedido nuevo, aún sin id
     */
    public Pedido(String direccionEntrega, String tipoPedido, EstadoPedido estado) {
        this.idPedido         = 0;                /* Aún no se guarda, la BD asigna el id */
        this.direccionEntrega = direccionEntrega; /* Definir dirección de entrega */
        this.tipoPedido       = tipoPedido;       /* Definir tipo de pedido */
        this.estado           = estado;           /* Definir estado del pedido */
    }

    /**
     * Constructor con todos los parametros, se usa al leer un pedido desde la base de datos
     */
    public Pedido(int idPedido, String direccionEntrega, String tipoPedido, EstadoPedido estado) {
        this.idPedido         = idPedido;         /* Definir identificador del pedido */
        this.direccionEntrega = direccionEntrega; /* Definir dirección de entrega */
        this.tipoPedido       = tipoPedido;       /* Definir tipo de pedido */
        this.estado           = estado;           /* Definir estado del pedido */
    }

    /**
     * Metodo get para devolver los datos del pedido
     */
    public int getIdPedido() {
        return idPedido; /* Devolver identificador del pedido */
    }

    public String getDireccionEntrega() {
        return direccionEntrega; /* Devolver dirección de entrega */
    }

    public String getTipoPedido() {
        return tipoPedido; /* Devolver tipo de pedido */
    }

    public EstadoPedido getEstado() {
        return estado; /* Devolver estado del pedido */
    }

    /**
     * Metodo set para modificar los datos del pedido
     */
    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido; /* Definir id generado por la base de datos */
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega; /* Definir nueva dirección */
    }

    public void setTipoPedido(String tipoPedido) {
        this.tipoPedido = tipoPedido; /* Definir nuevo tipo */
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado; /* Definir nuevo estado */
    }

    /**
     * Sobreescritura del metodo To String, es el texto que se ve en los JComboBox
     */
    @Override
    public String toString() {
        return idPedido + " - " + direccionEntrega;
    }
}
