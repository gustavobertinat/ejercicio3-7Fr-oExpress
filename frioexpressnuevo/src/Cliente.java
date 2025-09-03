public class Cliente {
    private final String rut;
    private final String razonSocial;
    private final String direccionEntrega;
    private final double limiteCredito;
    private double deudaActual;

    public Cliente(String rut, String razonSocial, String direccionEntrega, double limiteCredito, double deudaActual) {
        this.rut = rut;
        this.razonSocial = razonSocial;
        this.direccionEntrega = direccionEntrega;
        this.limiteCredito = limiteCredito;
        this.deudaActual = deudaActual;
    }

    public boolean superaCreditoCon(double monto) { return deudaActual + monto > limiteCredito; }
    public void cargarDeuda(double monto) { this.deudaActual += monto; }

    public String getRut() { return rut; }
    public String getRazonSocial() { return razonSocial; }
    public String getDireccionEntrega() { return direccionEntrega; }
    public double getLimiteCredito() { return limiteCredito; }
    public double getDeudaActual() { return deudaActual; }
}