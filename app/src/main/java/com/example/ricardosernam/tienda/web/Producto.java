package com.example.ricardosernam.tienda.web;

/**
 * Esta clase representa un gasto individual de cada registro de la base de datos
 */
public class Producto {
    public String idproducto;
    public String nombre;
    public double precio;
    public double porcion;
    public String guisado;
    public int disponible;
    public String tipo_producto;


    public Producto(String idproducto, String nombre, double precio, double porcion, String guisado, int disponible, String tipo_producto) {
        this.idproducto = idproducto;
        this.nombre = nombre;
        this.precio = precio;
        this.porcion = porcion;
        this.guisado = guisado;
        this.disponible=disponible;
        this.tipo_producto=tipo_producto;
    }
}
