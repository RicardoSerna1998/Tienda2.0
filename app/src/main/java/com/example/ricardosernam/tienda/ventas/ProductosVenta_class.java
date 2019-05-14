package com.example.ricardosernam.tienda.ventas;

public class ProductosVenta_class {  ///clase para obtener productos para cobrar
    public String nombre;
    public Float precio;
    public Float cantidad;
    public Float subtotal;
    public int tipo, idRemota;


    public ProductosVenta_class(String nombre, Float cantidad, Float precio, int tipo, Float subtotal, int idRemota) {   ///se manda desde el arrayProductos
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.tipo = tipo;
        this.subtotal=subtotal;
        this.idRemota=idRemota;
    }

    public String getNombre() {
        return nombre;
    }

    public Float getCantidad() {
        return cantidad;
    }

    public Float getPrecio() {
        return precio;
    }
    public Float getSubtotal() {
        return subtotal;
    }

    public int getTipo() {
        return tipo;
}
    public int getIdRemota() {
        return idRemota;
    }

}
