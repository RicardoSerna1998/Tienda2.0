package com.example.ricardosernam.tienda.web;

/**
 * Esta clase representa un gasto individual de cada registro de la base de datos
 */
public class Inventario_detalle {
    public String idinventario;
    public String idproducto;
    public Double inventario_inicial;
    public Double inventario_final;




    public Inventario_detalle(String idinventario, String idproducto, Double inventario_inicial, Double inventario_final) {
        this.idinventario = idinventario;
        this.idproducto = idproducto;
        this.inventario_final = inventario_inicial;
        this.inventario_inicial = inventario_final;
    }
}
