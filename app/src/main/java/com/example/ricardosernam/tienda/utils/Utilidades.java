package com.example.ricardosernam.tienda.utils;

import android.database.Cursor;
import android.os.Build;
import android.util.Log;


import com.example.ricardosernam.tienda.provider.ContractParaProductos;

import org.json.JSONException;
import org.json.JSONObject;

public class Utilidades {
    private static final int COLUMNA_ID_REMOTA_INVENTARIO_DETALLE = 4;
    private static final int COLUMNA_ID_PRODUCTO_INVENTARIO_DETALLE = 1;
    private static final int COLUMNA_EXISTENTE_INICIAL = 2;
    private static final int COLUMNA_EXISTENTE_FINAL = 3;

    private static final int COLUMNA_ID_EMPLEADO_VENTA = 1;
    private static final int COLUMNA_FECHA_VENTA = 2;
    private static final int COLUMNA_FECHA_FIN = 3;



    private static final int COLUMNA_ID_REMOTA_VENTA_DETALLE = 4;
    private static final int COLUMNA_CANTIDAD = 1;
    private static final int COLUMNA_ID_PRODUCTO_VENTA_DETALLE = 3;
    private static final int COLUMNA_PRECIO = 2;


    /**
     * Determina si la aplicación corre en versiones superiores o iguales
     * a Android LOLLIPOP
     *
     * @return booleano de confirmación
     */
    public static boolean materialDesign() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }


    /**
     * Copia los datos de un gasto almacenados en un cursor hacia un
     * JSONObject
     *
     * @param c cursor
     * @return objeto jason*/

    public static JSONObject deCursorAJSONObject(Cursor c, String url) {
        JSONObject jObject = new JSONObject();

        if (url.equals(Constantes.UPDATE_URL_EMPLEADOS)) {
            Integer activo;
            activo = c.getInt(0);
            try {
                jObject.put(ContractParaProductos.Columnas.ACTIVO, activo);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (url.equals(Constantes.INSERT_URL_TURNO)) {
            int idempleado;
            String hora_incio;
            String hora_fin;

            idempleado = c.getInt(COLUMNA_ID_EMPLEADO_VENTA);
            hora_incio = c.getString(COLUMNA_FECHA_VENTA);
            hora_fin = c.getString(COLUMNA_FECHA_FIN);

            try {
                jObject.put(ContractParaProductos.Columnas.ID_EMPLEADO, idempleado);
                jObject.put(ContractParaProductos.Columnas.HORA_INICIO, hora_incio);
                jObject.put(ContractParaProductos.Columnas.HORA_FIN, hora_fin);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (url.equals(Constantes.UPDATE_URL_TURNO)) {
           String hora_fin;
            hora_fin = c.getString(2);
            try {
                jObject.put(ContractParaProductos.Columnas.HORA_FIN, hora_fin);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (url.equals(Constantes.UPDATE_URL_INVENTARIO)) {
            Double existenteFinal;
            Double precio;
            //existenteFinal=c.getDouble(0)-c.getDouble(1);
            existenteFinal=c.getDouble(1);
            precio=c.getDouble(3);

            try {
                jObject.put(ContractParaProductos.Columnas.EXISTENTES, existenteFinal);
                jObject.put(ContractParaProductos.Columnas.PRECIO, precio);

            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (url.equals(Constantes.INSERT_URL_VENTA)) {
            int idempleado;
            String fecha;



            idempleado = c.getInt(COLUMNA_ID_EMPLEADO_VENTA);
            fecha = c.getString(COLUMNA_FECHA_VENTA);

            try {
                jObject.put(ContractParaProductos.Columnas.ID_EMPLEADO, idempleado);
                jObject.put(ContractParaProductos.Columnas.FECHA, fecha);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (url.equals(Constantes.INSERT_URL_VENTA_DETALLE)) {
            int idventa;
            int cantidad;
            int idproducto;
            Double precio;

            idventa = c.getInt(0);
            idproducto = c.getInt(1);
            precio = c.getDouble(2);
            cantidad = c.getInt(3);



            try {
                jObject.put("id_iventa", idventa);
                jObject.put(ContractParaProductos.Columnas.ID_PRODUCTO, idproducto);
                jObject.put(ContractParaProductos.Columnas.PRECIO, precio);
                jObject.put(ContractParaProductos.Columnas.CANTIDAD, cantidad);
                } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.i("Cursor a JSONObject", String.valueOf(jObject));    ////mostramos que valores se han insertado
        return jObject;
    }
}


