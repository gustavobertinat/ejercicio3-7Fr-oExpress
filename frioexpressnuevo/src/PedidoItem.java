public class PedidoItem {
    private final String codigoProducto;
    private final double kilos;
    private final double precioUnitarioKg;

    public PedidoItem(String codigoProducto, double kilos, double precioUnitarioKg) {
        this.codigoProducto = codigoProducto;
        this.kilos = kilos;
        this.precioUnitarioKg = precioUnitarioKg;
    }

    public double subtotal() { return kilos * precioUnitarioKg; }

    public String getCodigoProducto() { return codigoProducto; }
    public double getKilos() { return kilos; }
    public double getPrecioUnitarioKg() { return precioUnitarioKg; }
}