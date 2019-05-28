package com.example.ricardosernam.tienda;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.ricardosernam.tienda.provider.ContractParaProductos;

import static android.content.ContentValues.TAG;


/**
 * Clase envoltura para el gestor de Bases de datos
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void onCreate(SQLiteDatabase database) {
        productos(database);
    }


    public static void productos(SQLiteDatabase database) {
        String cmd2 = "CREATE TABLE " + ContractParaProductos.INFORMACION + " (" +
                ContractParaProductos.Columnas._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContractParaProductos.Columnas.ID_REMOTA + " INT," +
                ContractParaProductos.Columnas.NOMBRE_NEGOCIO + " TEXT, " +
                ContractParaProductos.Columnas.DIRECCION + " VARCHAR(45), " +
                ContractParaProductos.Columnas.TELEFONO + " VARCHAR(45)," +
                ContractParaProductos.Columnas.ESTADO + " INTEGER NOT NULL DEFAULT "+ ContractParaProductos.ESTADO_OK+"," +
                ContractParaProductos.Columnas.PENDIENTE_INSERCION + " INTEGER NOT NULL DEFAULT 0)";
        database.execSQL(cmd2);

        String cmd0 = "CREATE TABLE " + ContractParaProductos.EMPLEADOS + " (" +
                ContractParaProductos.Columnas._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContractParaProductos.Columnas.ID_REMOTA + " INT," +
                ContractParaProductos.Columnas.DISPONIBLE+ " INT," +
                ContractParaProductos.Columnas.NOMBRE_EMPLEADO + " TEXT, " +
                ContractParaProductos.Columnas.TIPO_EMPLEADO + " TEXT, " +
                ContractParaProductos.Columnas.CODIGO+ " VARCHAR(45), " +
                ContractParaProductos.Columnas.ACTIVO + " INTEGER, " +   //QUITAR
                ContractParaProductos.Columnas.ESTADO + " INTEGER NOT NULL DEFAULT "+ ContractParaProductos.ESTADO_OK+"," +
                ContractParaProductos.Columnas.PENDIENTE_INSERCION + " INTEGER NOT NULL DEFAULT 0)";
        database.execSQL(cmd0);

        String cmd6 = "CREATE TABLE " + ContractParaProductos.TURNOS + " (" +
                ContractParaProductos.Columnas._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContractParaProductos.Columnas.ID_REMOTA + " INT," +
                ContractParaProductos.Columnas.HORA_INICIO + " TEXT, " +
                ContractParaProductos.Columnas.HORA_FIN + " TEXT, " +
                ContractParaProductos.Columnas.ESTADO + " INTEGER NOT NULL DEFAULT "+ ContractParaProductos.ESTADO_OK+"," +
                ContractParaProductos.Columnas.PENDIENTE_INSERCION + " INTEGER NOT NULL DEFAULT 0)";
        database.execSQL(cmd6);

        String cmd = "CREATE TABLE " + ContractParaProductos.INVENTARIO + " (" +
                ContractParaProductos.Columnas._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContractParaProductos.Columnas.ID_REMOTA + " INT," +
                ContractParaProductos.Columnas.DISPONIBLE+ " INT," +
                ContractParaProductos.Columnas.NOMBRE_PRODUCTO + " VARCHAR(45), " +
                ContractParaProductos.Columnas.PRECIO + " DOUBLE, " +
                ContractParaProductos.Columnas.CODIGO_BARRAS + " VARCHAR(45), " +
                ContractParaProductos.Columnas.EXISTENTES + " DOUBLE," +
                ContractParaProductos.Columnas.EXISTENTES2 + " DOUBLE," +
                ContractParaProductos.Columnas.ESTADO + " INTEGER NOT NULL DEFAULT "+ ContractParaProductos.ESTADO_OK+"," +
                ContractParaProductos.Columnas.PENDIENTE_INSERCION + " INTEGER NOT NULL DEFAULT 0)";
        database.execSQL(cmd);

        database.execSQL("INSERT INTO inventario (idRemota, disponible, nombre_producto, precio, codigo_barras, existente) values (1, 1, 'Jitomate', 15.0, 'null', 3000)");
        database.execSQL("INSERT INTO inventario (idRemota, disponible, nombre_producto, precio, codigo_barras, existente) values (2, 1,'Coca', 10.0 , '7501055354672', 20)");
        database.execSQL("INSERT INTO inventario (idRemota, disponible, nombre_producto, precio, codigo_barras, existente) values (3, 0, 'Cebolla', 15.0, 'null', 3000)");
        database.execSQL("INSERT INTO inventario (idRemota, disponible, nombre_producto, precio, codigo_barras, existente) values (4, 1, 'Trojan', 60.0 , '022600972525', 30)");

        database.execSQL("INSERT INTO empleados (idRemota, codigo, nombre_empleado, tipo_empleado, activo, disponible) values (1,'juan', 'Juan', 'Cajero', 0, 1)");
        database.execSQL("INSERT INTO empleados (idRemota, codigo, nombre_empleado, tipo_empleado, activo, disponible) values (2,'manuel', 'Manuel', 'Admin.', 0, 1)");
        database.execSQL("INSERT INTO empleados (idRemota, codigo, nombre_empleado, tipo_empleado, activo, disponible) values (3,'admin', 'Administrador', 'Admin.', 0, 1)");
        database.execSQL("INSERT INTO empleados (idRemota, codigo, nombre_empleado, tipo_empleado, activo, disponible) values (4,'maria', 'María', 'Cerillo',0, 1)");

        database.execSQL("INSERT INTO informacion (nombre_negocio, direccion, telefono) values ('Punto de venta', 'México', '+52 0000000000')");

        String cmd4 = "CREATE TABLE " + ContractParaProductos.VENTAS + " (" +
                ContractParaProductos.Columnas._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContractParaProductos.Columnas.ID_EMPLEADO + " INT, " +
                ContractParaProductos.Columnas.FECHA + " TEXT, " +
                ContractParaProductos.Columnas.ESTADO + " INTEGER NOT NULL DEFAULT "+ ContractParaProductos.ESTADO_OK+"," +
                ContractParaProductos.Columnas.PENDIENTE_INSERCION + " INTEGER NOT NULL DEFAULT 0)";
        database.execSQL(cmd4);

        String cmd5 = "CREATE TABLE " + ContractParaProductos.VENTA_DETALLES + " (" +
                ContractParaProductos.Columnas._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContractParaProductos.Columnas.ID_REMOTA + " INT," +
                ContractParaProductos.Columnas.ID_PRODUCTO+ " INT, " +
                ContractParaProductos.Columnas.PRECIO + " DOUBLE, " +
                ContractParaProductos.Columnas.CANTIDAD + " DOUBLE, " +
                ContractParaProductos.Columnas.LOCAL + " INT, " +
                ContractParaProductos.Columnas.ESTADO + " INTEGER NOT NULL DEFAULT "+ ContractParaProductos.ESTADO_OK+"," +
                ContractParaProductos.Columnas.PENDIENTE_INSERCION + " INTEGER NOT NULL DEFAULT 0)";
        database.execSQL(cmd5);

        String cmd7 = "CREATE TABLE IF NOT EXISTS " + ContractParaProductos.ESTADOS + " (" +
                ContractParaProductos.Columnas._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContractParaProductos.Columnas.ONLINE + " INT, " +
                ContractParaProductos.Columnas.IP + " TEXT)";

        database.execSQL(cmd7);
    }

    //@Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            limpiar(db);
            onCreate(db);
    }

    public static void limpiar(SQLiteDatabase db) {
        db.execSQL("drop table if exists " + ContractParaProductos.VENTA_DETALLES);
        db.execSQL("drop table if exists " + ContractParaProductos.VENTAS);
        db.execSQL("drop table if exists " + ContractParaProductos.INVENTARIO);
        db.execSQL("drop table if exists " + ContractParaProductos.EMPLEADOS);
        db.execSQL("drop table if exists " + ContractParaProductos.INFORMACION);
        productos(db);
    }

}
