package com.example.ricardosernam.tienda.web;

/**
 * Esta clase representa un gasto individual de cada registro de la base de datos
 */
public class Informacion {
    public String idinformacion;
    public String nombre;
    public String direccion;
    public String telefono;



    public Informacion(String idinformacion, String nombre, String direccion, String telefono) {
        this.idinformacion = idinformacion;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
    }
}
