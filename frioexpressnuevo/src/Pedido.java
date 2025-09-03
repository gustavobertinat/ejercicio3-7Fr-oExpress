import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pedido {
    public enum Estado { PENDIENTE, PROCESADO, DESPACHADO }

    private final String id;
    private final String rutCliente;
    private final LocalDateTime fechaCreacion;
    private final List<PedidoItem> items;
    private Estado estado;

    public Pedido(String id, String rutCliente) {
        this.id = id;
        this.rutCliente = rutCliente;
        this.fechaCreacion = LocalDateTime.now();
        this.items = new ArrayList<>();
        this.estado = Estado.PENDIENTE;
    }

    public void agregarItem(PedidoItem item) { this.items.add(item); }
    public double total() { return items.stream().mapToDouble(PedidoItem::subtotal).sum(); }

    public String getId() { return id; }
    public String getRutCliente() { return rutCliente; }
    public List<PedidoItem> getItems() { return Collections.unmodifiableList(items); }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
}