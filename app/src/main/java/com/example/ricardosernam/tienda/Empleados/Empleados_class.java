package com.example.ricardosernam.tienda.Empleados;

/**
 * Created by Ricardo Serna M on 28/02/2018.
 */

public class Empleados_class {
    public String nombre;
    public String puesto, codigo;
    public int activo;



    public Empleados_class(String nombre, String puesto, int activo, String codigo) {
        this.nombre=nombre;
        this.puesto=puesto;
        this.activo=activo;
        this.codigo=codigo;


    }
    public String getNombre() {
        return nombre;
    }
    public String getCodigo() {
        return codigo;
    }

    public String getPuesto() {
        return puesto;
    }

    public int getActivo() {
        return activo;
    }
}
