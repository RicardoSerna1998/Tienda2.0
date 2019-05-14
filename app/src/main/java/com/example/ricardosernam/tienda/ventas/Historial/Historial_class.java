package com.example.ricardosernam.tienda.ventas.Historial;



public class Historial_class {
    private String empleado, fecha;
    private int idVenta;

    public Historial_class(String empleado, String fecha, int idVenta) {   ///se manda desde el arrayProductos
        this.empleado= empleado;
        this.fecha= fecha;
        this.idVenta=idVenta;

    }

    public String getEmpleado() {
        return empleado;
    }
    public String getFecha() {
        return fecha;
    }
    public int getIdVenta() {
        return idVenta;
    }

}
