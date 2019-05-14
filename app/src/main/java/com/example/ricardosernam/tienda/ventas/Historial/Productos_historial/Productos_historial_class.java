package com.example.ricardosernam.tienda.ventas.Historial.Productos_historial;



public class Productos_historial_class {
    private String producto;
    private Float precio, subTotal;
    private Float cantidad;

    public Productos_historial_class(String producto, Float cantidad, Float precio, Float subTotal) {   ///se manda desde el arrayProductos
        this.producto= producto;
        this.cantidad= cantidad;
        this.precio= precio;
        this.subTotal= subTotal;
    }

    public String getProducto() {
        return producto;
    }
    public Float getPrecio() {
        return precio;
    }
    public Float getSubTotal() {
        return subTotal;
    }
    public Float getCantidad() {
        return cantidad;
    }
}
