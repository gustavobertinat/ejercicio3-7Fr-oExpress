import java.time.Duration;
import java.time.LocalDateTime;

public class AlertaTemperatura {
    private final String codigoProducto;
    private final double temperaturaRegistrada;
    private final LocalDateTime inicioIncidente;
    private LocalDateTime finIncidente;

    public AlertaTemperatura(String codigoProducto, double temperaturaRegistrada) {
        this.codigoProducto = codigoProducto;
        this.temperaturaRegistrada = temperaturaRegistrada;
        this.inicioIncidente = LocalDateTime.now();
    }

    public AlertaTemperatura(String codigoProducto, double temperaturaRegistrada, LocalDateTime inicioIncidente) {
        this.codigoProducto = codigoProducto;
        this.temperaturaRegistrada = temperaturaRegistrada;
        this.inicioIncidente = inicioIncidente;
    }

    public void cerrarIncidente() { this.finIncidente = LocalDateTime.now(); }

    public boolean excedeTreintaMinutos() {
        LocalDateTime fin = finIncidente == null ? LocalDateTime.now() : finIncidente;
        return Duration.between(inicioIncidente, fin).toMinutes() > 30;
    }

    public String getCodigoProducto() { return codigoProducto; }
    public double getTemperaturaRegistrada() { return temperaturaRegistrada; }
}