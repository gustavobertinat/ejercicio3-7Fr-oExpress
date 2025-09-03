import java.time.LocalDateTime;
import java.util.*;

public class FrioExpressSistema implements Iterable<Producto> {
    private final TreeMap<String, Producto> codigoAProducto = new TreeMap<>();
    private final Map<Categoria, List<Producto>> productosPorCategoria = new HashMap<>();
    private final Map<String, Cliente> rutACliente = new HashMap<>();
    private final Queue<Pedido> pedidosPendientes = new LinkedList<>();
    private final List<AlertaTemperatura> alertasTemperatura = new ArrayList<>();

    public void registrarProducto(Producto producto) {
        codigoAProducto.put(producto.getCodigo(), producto);
        productosPorCategoria.computeIfAbsent(producto.getCategoria(), c -> new ArrayList<>()).add(producto);
    }

    public void registrarCliente(Cliente cliente) { rutACliente.put(cliente.getRut(), cliente); }

    public Producto buscarProductoPorCodigo(String codigo) throws ProductoNoCongeladoException {
        Producto producto = codigoAProducto.get(codigo);
        if (producto == null) throw new ProductoNoCongeladoException("Producto con código " + codigo + " no existe");
        return producto;
    }

    public List<Producto> buscarPorCategoria(Categoria categoria) {
        return productosPorCategoria.getOrDefault(categoria, Collections.emptyList());
    }

    public void registrarIngreso(String codigoProducto, double kilos) throws ProductoNoCongeladoException {
        Producto producto = buscarProductoPorCodigo(codigoProducto);
        producto.ingresarMercaderia(kilos);
    }

    public void procesarPedido(Pedido pedido)
            throws LimiteCreditoExcedidoException, StockInsuficienteException,
            ProductoNoCongeladoException, StockMinimoAlcanzadoException {
        Cliente cliente = Optional.ofNullable(rutACliente.get(pedido.getRutCliente()))
                .orElseThrow(() -> new IllegalArgumentException("Cliente no registrado: " + pedido.getRutCliente()));

        for (PedidoItem item : pedido.getItems()) {
            Producto producto = buscarProductoPorCodigo(item.getCodigoProducto());
            if (item.getKilos() > producto.getStockActualKg()) {
                throw new StockInsuficienteException("Stock insuficiente para producto " + producto.getCodigo());
            }
        }

        double total = pedido.total();
        if (cliente.superaCreditoCon(total)) {
            throw new LimiteCreditoExcedidoException("Límite de crédito excedido para cliente " + cliente.getRut());
        }

        for (PedidoItem item : pedido.getItems()) {
            Producto producto = buscarProductoPorCodigo(item.getCodigoProducto());
            producto.retirarStock(item.getKilos());
            if (producto.getStockActualKg() <= producto.getStockMinimoKg()) {
                throw new StockMinimoAlcanzadoException("Producto " + producto.getCodigo() + " alcanzó stock mínimo");
            }
        }
        cliente.cargarDeuda(total);
        pedido.setEstado(Pedido.Estado.PROCESADO);
        pedidosPendientes.add(pedido);
    }

    public void registrarIncidenteTemperatura(String codigoProducto, double temperaturaRegistrada)
            throws ProductoNoCongeladoException, CadenaFrioRotaException {
        Producto producto = buscarProductoPorCodigo(codigoProducto);
        AlertaTemperatura alerta = new AlertaTemperatura(producto.getCodigo(), temperaturaRegistrada);
        alertasTemperatura.add(alerta);
        alerta.cerrarIncidente();
        if (alerta.excedeTreintaMinutos()) {
            throw new CadenaFrioRotaException("Cadena de frío rota para producto " + producto.getCodigo());
        }
    }

    public void registrarIncidenteTemperaturaForzadoRuptura(String codigoProducto, double temperaturaRegistrada)
            throws ProductoNoCongeladoException, CadenaFrioRotaException {
        Producto producto = buscarProductoPorCodigo(codigoProducto);
        LocalDateTime inicioHace31 = LocalDateTime.now().minusMinutes(31);
        AlertaTemperatura alerta = new AlertaTemperatura(producto.getCodigo(), temperaturaRegistrada, inicioHace31);
        alertasTemperatura.add(alerta);
        alerta.cerrarIncidente();
        if (alerta.excedeTreintaMinutos()) {
            throw new CadenaFrioRotaException("Cadena de frío rota para producto " + producto.getCodigo());
        }
    }

    public List<Producto> consultarBajoStockMinimo() {
        List<Producto> bajoMinimo = new ArrayList<>();
        for (Producto p : codigoAProducto.values()) {
            if (p.getStockActualKg() < p.getStockMinimoKg()) bajoMinimo.add(p);
        }
        return bajoMinimo;
    }

    public List<Pedido> listarPedidosPendientes() { return new ArrayList<>(pedidosPendientes); }

    public boolean despacharSiguientePedidoConTryCatch() {
        Pedido pedido = pedidosPendientes.poll();
        if (pedido == null) return false;
        try {
            pedido.setEstado(Pedido.Estado.DESPACHADO);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Iterator<Producto> iterator() { return codigoAProducto.values().iterator(); }
}