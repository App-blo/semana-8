package controlador;

import dao.EntregaDAO;
import dao.PedidoDAO;
import dao.RepartidorDAO;
import modelo.Entrega;
import modelo.Pedido;
import modelo.Repartidor;
import java.sql.SQLException;
import java.util.List;

/**
 * Clase que conecta las ventanas con los DAO. Las SQLException se propagan
 * hacia la vista, que es la que le muestra el mensaje al usuario
 */
public class ControladorSpeedFast {

    private RepartidorDAO repartidorDAO; /* Acceso a la tabla repartidores */
    private PedidoDAO pedidoDAO;         /* Acceso a la tabla pedidos */
    private EntregaDAO entregaDAO;       /* Acceso a la tabla entregas */

    /**
     * Constructor de la clase ControladorSpeedFast
     */
    public ControladorSpeedFast() {
        this.repartidorDAO = new RepartidorDAO(); /* Inicializar DAO de repartidores */
        this.pedidoDAO     = new PedidoDAO();     /* Inicializar DAO de pedidos */
        this.entregaDAO    = new EntregaDAO();    /* Inicializar DAO de entregas */
    }

    /* ── Repartidores ── */

    public void registrarRepartidor(Repartidor repartidor) throws SQLException {
        repartidorDAO.create(repartidor);
    }

    public List<Repartidor> listarRepartidores() throws SQLException {
        return repartidorDAO.readAll();
    }

    public boolean actualizarRepartidor(Repartidor repartidor) throws SQLException {
        return repartidorDAO.update(repartidor);
    }

    public boolean eliminarRepartidor(int id) throws SQLException {
        return repartidorDAO.delete(id);
    }

    /* ── Pedidos ── */

    public void registrarPedido(Pedido pedido) throws SQLException {
        pedidoDAO.create(pedido);
    }

    public List<Pedido> listarPedidos() throws SQLException {
        return pedidoDAO.readAll();
    }

    /**
     * Lista los pedidos filtrando por tipo y/o estado, null significa sin filtro
     */
    public List<Pedido> listarPedidos(String tipo, String estado) throws SQLException {
        return pedidoDAO.readFiltrado(tipo, estado);
    }

    public boolean actualizarPedido(Pedido pedido) throws SQLException {
        return pedidoDAO.update(pedido);
    }

    public boolean eliminarPedido(int id) throws SQLException {
        return pedidoDAO.delete(id);
    }

    /* ── Entregas ── */

    public void registrarEntrega(Entrega entrega) throws SQLException {
        entregaDAO.create(entrega);
    }

    /**
     * Lista las entregas filtrando por pedido y/o repartidor, null significa sin filtro
     */
    public List<Entrega> listarEntregas(Integer idPedido, Integer idRepartidor) throws SQLException {
        return entregaDAO.readFiltrado(idPedido, idRepartidor);
    }

    public boolean actualizarEntrega(Entrega entrega) throws SQLException {
        return entregaDAO.update(entrega);
    }

    public boolean eliminarEntrega(int id) throws SQLException {
        return entregaDAO.delete(id);
    }
}
