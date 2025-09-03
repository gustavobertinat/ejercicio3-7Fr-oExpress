import java.util.List;

public class Main {
    public static void main(String[] args) {
        FrioExpressSistema sistema = new FrioExpressSistema();

        // 8 productos
        sistema.registrarProducto(new Producto("P001", "Carne vacuna", Categoria.CARNES, -18, 120, 100, 5200));
        sistema.registrarProducto(new Producto("P002", "Pechuga pollo", Categoria.CARNES, -18, 90, 80, 4100));
        sistema.registrarProducto(new Producto("P003", "Filete merluza", Categoria.PESCADOS, -18, 60, 50, 6300));
        sistema.registrarProducto(new Producto("P004", "Camarones", Categoria.PESCADOS, -18, 40, 40, 9800));
        sistema.registrarProducto(new Producto("P005", "Choclo", Categoria.VEGETALES, -18, 30, 25, 2100));
        sistema.registrarProducto(new Producto("P006", "Arvejas", Categoria.VEGETALES, -18, 26, 25, 2000));
        sistema.registrarProducto(new Producto("P007", "Helado vainilla", Categoria.HELADOS, -18, 70, 60, 3500));
        sistema.registrarProducto(new Producto("P008", "Helado chocolate", Categoria.HELADOS, -18, 65, 60, 3600));

        // 4 clientes
        sistema.registrarCliente(new Cliente("76.111.111-1", "Restaurante La Parrilla", "Av. Central 123", 600000, 0));
        sistema.registrarCliente(new Cliente("77.222.222-2", "Supermercado El Ahorro", "Calle 9 #45", 800000, 200000));
        sistema.registrarCliente(new Cliente("78.333.333-3", "Cafetería Dulce", "Pasaje Norte 77", 150000, 0));
        sistema.registrarCliente(new Cliente("79.444.444-4", "Sushi Express", "Las Flores 321", 300000, 250000));

        // Caso 1: Pedido OK (ajustado para no alcanzar stock mínimo)
        try {
            Pedido pedido1 = new Pedido("PED-001", "76.111.111-1");
            pedido1.agregarItem(new PedidoItem("P001", 10, 5200));
            pedido1.agregarItem(new PedidoItem("P005", 4, 2100)); // antes 5
            sistema.procesarPedido(pedido1);
            System.out.println("OK Caso 1: PED-001 procesado. Total: " + pedido1.total());
        } catch (Exception e) {
            System.out.println("Error Caso 1: " + e.getMessage());
        }

        // Caso 2: Excede stock
        try {
            Pedido pedido2 = new Pedido("PED-002", "77.222.222-2");
            pedido2.agregarItem(new PedidoItem("P004", 1000, 9800));
            sistema.procesarPedido(pedido2);
            System.out.println("ERROR Caso 2: debería fallar por stock.");
        } catch (StockInsuficienteException ex) {
            System.out.println("OK Caso 2: " + ex.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado Caso 2: " + e.getMessage());
        }

        // Caso 3: Excede crédito
        try {
            Pedido pedido3 = new Pedido("PED-003", "78.333.333-3");
            pedido3.agregarItem(new PedidoItem("P003", 50, 6300)); // 315000 > 150000
            sistema.procesarPedido(pedido3);
            System.out.println("ERROR Caso 3: debería fallar por crédito.");
        } catch (LimiteCreditoExcedidoException ex) {
            System.out.println("OK Caso 3: " + ex.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado Caso 3: " + e.getMessage());
        }

        // Caso 4: Ruptura cadena de frío (>30 min forzado)
        try {
            sistema.registrarIncidenteTemperaturaForzadoRuptura("P007", -5);
            System.out.println("ERROR Caso 4: debería lanzar ruptura cadena de frío.");
        } catch (CadenaFrioRotaException ex) {
            System.out.println("OK Caso 4 (forzado): " + ex.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado Caso 4: " + e.getMessage());
        }

        // Caso 5: Reporte bajo stock mínimo
        List<Producto> bajoMinimo = sistema.consultarBajoStockMinimo();
        System.out.println("Caso 5: Productos bajo stock mínimo:");
        for (Producto p : bajoMinimo) System.out.println(" - " + p);

        // Caso 6: Código inexistente en pedido
        try {
            Pedido pedido4 = new Pedido("PED-004", "76.111.111-1");
            pedido4.agregarItem(new PedidoItem("P999", 5, 1000));
            sistema.procesarPedido(pedido4);
            System.out.println("ERROR Caso 6: debería fallar por código inexistente.");
        } catch (ProductoNoCongeladoException ex) {
            System.out.println("OK Caso 6: " + ex.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado Caso 6: " + e.getMessage());
        }

        // Pendientes y despacho
        System.out.println("Pedidos pendientes:");
        for (Pedido p : sistema.listarPedidosPendientes()) {
            System.out.println(p.getId() + " - " + p.getEstado());
        }
        boolean despachado = sistema.despacharSiguientePedidoConTryCatch();
        System.out.println("Despachado? " + despachado);

        // Iteración de productos
        System.out.println("Inventario:");
        for (Producto prod : sistema) System.out.println(" * " + prod);
    }
}