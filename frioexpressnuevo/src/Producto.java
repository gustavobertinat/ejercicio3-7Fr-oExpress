public class Producto {
    private final String codigo;
    private final String nombre;
    private final Categoria categoria;
    private final double temperaturaRequeridaCelsius;
    private double stockActualKg;
    private final double stockMinimoKg;
    private final double precioPorKg;

    public Producto(String codigo, String nombre, Categoria categoria, double temperaturaRequeridaCelsius,
                    double stockActualKg, double stockMinimoKg, double precioPorKg) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.temperaturaRequeridaCelsius = temperaturaRequeridaCelsius;
        this.stockActualKg = stockActualKg;
        this.stockMinimoKg = stockMinimoKg;
        this.precioPorKg = precioPorKg;
    }

    public void ingresarMercaderia(double kilos) {
        if (kilos <= 0) return;
        this.stockActualKg += kilos;
    }

    public void retirarStock(double kilos) throws StockInsuficienteException {
        if (kilos <= 0) return;
        if (kilos > stockActualKg) {
            throw new StockInsuficienteException("Stock insuficiente para producto " + codigo
                    + ": solicitado " + kilos + " kg, disponible " + stockActualKg + " kg");
        }
        this.stockActualKg -= kilos;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public Categoria getCategoria() { return categoria; }
    public double getTemperaturaRequeridaCelsius() { return temperaturaRequeridaCelsius; }
    public double getStockActualKg() { return stockActualKg; }
    public double getStockMinimoKg() { return stockMinimoKg; }
    public double getPrecioPorKg() { return precioPorKg; }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " (" + categoria + ") "
                + stockActualKg + "kg (min " + stockMinimoKg + "), $" + precioPorKg + "/kg";
    }
}