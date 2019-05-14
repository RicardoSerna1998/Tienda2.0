package com.example.ricardosernam.tienda.ventas.Historial;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ricardosernam.tienda.DatabaseHelper;
import com.example.ricardosernam.tienda.provider.ContractParaProductos;
import com.example.ricardosernam.tienda.R;
import com.example.ricardosernam.tienda.ventas.Historial.Productos_historial.Productos_historialAdapter;
import com.example.ricardosernam.tienda.ventas.Historial.Productos_historial.Productos_historial_class;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Historial extends Fragment {
    private static Cursor fila, filaEmpleado, fila2, filaProducto;
    private static SQLiteDatabase db;
    private static RecyclerView recycler;
    private static RecyclerView.Adapter adapter;
    private static android.support.v4.app.FragmentManager fm;
    private static RecyclerView.LayoutManager lManager;
    public static ArrayList<Historial_class> itemsHistorial= new ArrayList <>(); ///Arraylist que contiene los productos///
    public static ArrayList<Productos_historial_class> itemsProductosHistorial= new ArrayList <>(); ///Arraylist que contiene los productos///
    public static TextView totalDia;
    public static float total;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_historial, container, false);
        DatabaseHelper admin=new DatabaseHelper(getContext(), ContractParaProductos.DATABASE_NAME, null, ContractParaProductos.DATABASE_VERSION);
        db=admin.getWritableDatabase();
        recycler = view.findViewById(R.id.RVhistorial); ///declaramos el recycler
        totalDia = view.findViewById(R.id.TVtotalDelDia); ///declaramos el recycler
        total=0;


        fm=getActivity().getSupportFragmentManager();

        rellenado_total();
        return view;
    }
    public void rellenado_total(){  ////volvemos a llenar el racycler despues de actualizar, o de una busqueda
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        String fechaActual=String.valueOf(df.format("yyyy-MM-dd", new java.util.Date()));

        itemsHistorial.clear();
        fm=getFragmentManager();
        fila=db.rawQuery("select id_empleado, fecha, _id from ventas where STRFTIME('%Y-%m-%d', fecha)='"+fechaActual+"' order by fecha desc" ,null);

        if(fila.moveToFirst()) {///si hay un elemento

            filaEmpleado=db.rawQuery("select nombre_empleado from empleados where idRemota='"+fila.getInt(0)+"'" ,null);

            if(filaEmpleado.moveToFirst()) {///si hay un elemento
                itemsHistorial.add(new Historial_class(filaEmpleado.getString(0), fila.getString(1), fila.getInt(2)));
                //rellenado_items(fila.getInt(2));
            }

            while (fila.moveToNext()) {
                filaEmpleado=db.rawQuery("select nombre_empleado from empleados where idRemota='"+fila.getInt(0)+"'" ,null);
                while (filaEmpleado.moveToNext()) {
                    itemsHistorial.add(new Historial_class(filaEmpleado.getString(0), fila.getString(1), fila.getInt(2)));
                    //rellenado_items(fila.getInt(2));
                    }
            }
        }
        adapter = new HistorialAdapter(getContext(), itemsHistorial);///llamamos al adaptador y le enviamos el array como parametro
        lManager = new LinearLayoutManager(this.getActivity());  //declaramos el layoutmanager
        recycler.setLayoutManager(lManager);
        recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public static void rellenado_items(int idVenta, RecyclerView recycler, Context context){  ////volvemos a llenar el racycler despues de actualizar, o de una busqueda
        itemsProductosHistorial.clear();
        fila2=db.rawQuery("select id_producto, cantidad, precio from venta_detalles where local=1 and idRemota='"+idVenta+"'" ,null);

        if(fila2.moveToFirst()) {///si hay un elemento
            filaProducto=db.rawQuery("select nombre_producto, codigo_barras from inventario where idRemota='"+fila2.getInt(0)+"'" ,null);
            if(filaProducto.moveToFirst()) {///si hay un elemento}
                if(filaProducto.getString(1).equals("null")){   ///gramos
                    float subtotal=(fila2.getFloat(1)/1000)* fila2.getFloat(2);
                    total=total+subtotal;
                    itemsProductosHistorial.add(new Productos_historial_class(filaProducto.getString(0), fila2.getFloat(1), fila2.getFloat(2), subtotal));
                }
                else{  ///pieza
                    float subtotal=fila2.getFloat(1)* fila2.getFloat(2);
                    total=total+subtotal;
                    itemsProductosHistorial.add(new Productos_historial_class(filaProducto.getString(0), fila2.getFloat(1), fila2.getFloat(2), subtotal));
                }
        }
            while (fila2.moveToNext()) {
                filaProducto=db.rawQuery("select nombre_producto, codigo_barras from inventario where idRemota='"+fila2.getInt(0)+"'" ,null);
                while (filaProducto.moveToNext()) {
                    if(filaProducto.getString(1).equals("null")){   ///gramos
                        float subtotal=(fila2.getFloat(1)/1000)* fila2.getFloat(2);
                        total=total+subtotal;
                        itemsProductosHistorial.add(new Productos_historial_class(filaProducto.getString(0), fila2.getFloat(1), fila2.getFloat(2), subtotal));
                    }
                    else{  ///pieza
                        float subtotal=fila2.getFloat(1)* fila2.getFloat(2);
                        total=total+subtotal;
                        itemsProductosHistorial.add(new Productos_historial_class(filaProducto.getString(0), fila2.getFloat(1), fila2.getFloat(2), subtotal));
                    }
                }
            }
        }
        adapter = new Productos_historialAdapter(itemsProductosHistorial);///llamamos al adaptador y le enviamos el array como parametro
        lManager = new LinearLayoutManager(context);  //declaramos el layoutmanager
        recycler.setLayoutManager(lManager);
        recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        calcularTotalporDia();
    }

    public static void calcularTotalporVenta(TextView total){  ///total por venta
        float suma=0;
        for(int i=0; i<itemsProductosHistorial.size(); i++){
            suma=suma+itemsProductosHistorial.get(i).getSubTotal();
            total.setText("Total: $"+String.valueOf(suma));
        }
    }
    public static void calcularTotalporDia(){  ///total por venta
        totalDia.setText("Total del dia: $"+String.valueOf(total));
    }
}
