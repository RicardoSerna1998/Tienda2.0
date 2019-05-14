package com.example.ricardosernam.tienda.ventas;

public class Productos_class {  ///clase para obtener productos para cobrar
    public String nombre;
    public String codigo_barras;
    public Float precio, existentes;

    public Productos_class(String nombre, Float precio, String codigo_barras, Float existentes) {   ///se manda desde el arrayProductos
        this.nombre = nombre;
        this.precio = precio;
        this.codigo_barras=codigo_barras;
        this.existentes = existentes;
        }

    public String getNombre() {
        return nombre;
    }
    public Float getPrecio() {
        return precio;
    }
    public String getCodigo_barras() {
        return codigo_barras;
    }
    public Float getExistentes() {
        return existentes;
    }
}
