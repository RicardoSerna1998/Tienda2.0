package com.example.ricardosernam.tienda.ventas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ricardosernam.tienda.Carrito.Carrito;
import com.example.ricardosernam.tienda.DatabaseHelper;
import com.example.ricardosernam.tienda.provider.ContractParaProductos;
import com.example.ricardosernam.tienda.R;
import com.example.ricardosernam.tienda.ventas.Historial.Historial;

import java.util.ArrayList;

public class Ventas extends Fragment implements KeyListener {
    private SearchView nombreCodigo;
    private static Cursor fila, filaBusqueda, datoEscaneado, ventas;
    private static SQLiteDatabase db;
    private static RecyclerView recycler;
    private static RecyclerView.Adapter adapter;
    private static android.support.v4.app.FragmentManager fm;
    private static RecyclerView.LayoutManager lManager;
    private Button escanear, carrito, historial;
    private android.support.v7.app.ActionBar actionBar;

    private static ArrayList<Productos_class> itemsProductos= new ArrayList <>(); ///Arraylist que contiene los productos///

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_ventas, container, false);
        DatabaseHelper admin=new DatabaseHelper(getContext(), ContractParaProductos.DATABASE_NAME, null, ContractParaProductos.DATABASE_VERSION);
        db=admin.getWritableDatabase();
        recycler = view.findViewById(R.id.RVproductosVenta); ///declaramos el recycler
        //escanear= view.findViewById(R.id.BtnEscanearProducto);
        carrito= view.findViewById(R.id.BtnCarrito);
        historial= view.findViewById(R.id.BtnHistorial);
        actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        nombreCodigo=view.findViewById(R.id.ETnombreProducto);

        fm=getFragmentManager();

        carrito.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if(ContractParaProductos.itemsProductosVenta.isEmpty()){
                    Toast.makeText(getContext(), "No hay productos comprados aun", Toast.LENGTH_SHORT).show();
                }
                else{
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    getFragmentManager().beginTransaction().replace(R.id.LLprincipal, new Carrito(), "Empleados").addToBackStack("Empleados").commit(); ///cambio de fragment
                }
            }
        });

        historial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.text.format.DateFormat df = new android.text.format.DateFormat();
                String fechaActual=String.valueOf(df.format("yyyy-MM-dd", new java.util.Date()));
                ventas = db.rawQuery("select * from ventas where STRFTIME('%Y-%m-%d', fecha)='"+fechaActual+"'", null);
                if (ventas.moveToFirst()) {
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    getFragmentManager().beginTransaction().replace(R.id.LLprincipal, new Historial(), "Historial").addToBackStack("Historial").commit(); ///cambio de fragment
                }else{
                    Toast.makeText(getContext(), "No hay ventas realizadas el día de hoy", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ///buscador
        nombreCodigo.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(!(TextUtils.isEmpty(newText))) {   ///el campo tiene algo
                    if ((TextUtils.isDigitsOnly(newText))) {  ///si el campo tiene tan solo numeros es un codigo
                        datoEscaneado=db.rawQuery("select nombre_producto, precio from inventario where codigo_barras='"+newText+"'" ,null);
                        if(datoEscaneado.moveToFirst()) {
                            new cantidad_producto_DialogFragment(datoEscaneado.getString(0), datoEscaneado.getFloat(1), 1).show(fm, "Producto_ventas");
                            nombreCodigo.clearFocus();   ///deshabilitar buscador
                            nombreCodigo.setQuery("", false);
                        }
                    }
                    else {   ///es un nombre de producto
                        filaBusqueda = db.rawQuery("select nombre_producto, precio, codigo_barras, existente2 from inventario where nombre_producto like ?", new String[]{"%" + newText + "%"});
                        if (filaBusqueda.moveToFirst()) { ///si hay un elemento
                            itemsProductos.clear();
                            itemsProductos.add(new Productos_class(filaBusqueda.getString(0), filaBusqueda.getFloat(1), filaBusqueda.getString(2), filaBusqueda.getFloat(3)));
                            while (filaBusqueda.moveToNext()) {
                                itemsProductos.add(new Productos_class(filaBusqueda.getString(0), filaBusqueda.getFloat(1), filaBusqueda.getString(2), filaBusqueda.getFloat(3)));
                            }
                        } else { ///El producto no existe
                            Toast.makeText(getContext(), "Producto inexistente", Toast.LENGTH_SHORT).show();
                        }
                    }
                }  ////esta vacio
                else {
                    rellenado_total(getContext());
                }
                adapter.notifyDataSetChanged();
                return false;
            }

        });
        rellenado_total(getContext());
        return view;
    }
    public static void rellenado_total(Context context){  ////volvemos a llenar el racycler despues de actualizar, o de una busqueda
        fila=db.rawQuery("select nombre_producto, precio, codigo_barras, existente2 from inventario order by codigo_barras" ,null);

        if(fila.moveToFirst()) {///si hay un elemento
            itemsProductos.clear();
            itemsProductos.add(new Productos_class(fila.getString(0), fila.getFloat(1), fila.getString(2), fila.getFloat(3)));
            while (fila.moveToNext()) {
                itemsProductos.add(new Productos_class(fila.getString(0), fila.getFloat(1), fila.getString(2), fila.getFloat(3)));
            }
        }
        adapter = new VentasAdapter(itemsProductos, fm, context);///llamamos al adaptador y le enviamos el array como parametro
        //lManager = new LinearLayoutManager(this.getActivity());  //declaramos el layoutmanager
        lManager = new GridLayoutManager(context, 3);  //declaramos el layoutmanager
        recycler.setLayoutManager(lManager);
        recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    /*@Override
    public boolean dispatchKeyEvent(KeyEvent event) {


        try
        {
            if(event.getAction()== KeyEvent.ACTION_UP)
            {

                try {

                    char pressedKey = (char) event.getUnicodeChar();
                    Barcode += "" + pressedKey;

                    //pass data to fragment
                    WMSSCANFragment WMSSCANFragment = (WMSSCANFragment)getSupportFragmentManager().findFragmentByTag(mFragmentList.get(2).getTag());
                    WMSSCANFragment.receivedKeyDownEvent(Barcode);


                }catch (Exception error) {
                    error.printStackTrace();
                }

                //Toast.makeText(getApplicationContext(), event.getAction() + " " + event.getKeyCode() + " - " + (char) event.getUnicodeChar(), Toast.LENGTH_SHORT).show();

            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return true;
    }*/

    @Override
    public int getInputType() {
        return 0;
    }

    @Override
    public boolean onKeyDown(View view, Editable text, int keyCode, KeyEvent event) {
        String barcode=null;
        if(event.getAction()==KeyEvent.ACTION_DOWN){
            //Log.i(TAG,"dispatchKeyEvent: "+e.toString());
            char pressedKey = (char) event.getUnicodeChar();
            barcode += pressedKey;
        }
        if (event.getAction()==KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            Toast.makeText(getContext(),
                    "barcode--->>>" + barcode, Toast.LENGTH_LONG)
                    .show();

            barcode="";
        }
        return false;
    }

    @Override
    public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyOther(View view, Editable text, KeyEvent event) {
        return false;
    }

    @Override
    public void clearMetaKeyState(View view, Editable content, int states) {

    }


    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2 && data != null) {
            //obtener resultados
            datoEscaneado=db.rawQuery("select nombre_producto, precio from inventario where codigo_barras='"+data.getStringExtra("BARCODE")+"'" ,null);
            if(datoEscaneado.moveToFirst()) {
                new cantidad_producto_DialogFragment(datoEscaneado.getString(0), datoEscaneado.getFloat(1), 1).show(fm, "Producto_ventas");
            }
            else{
                Toast.makeText(getContext(), "Este producto no esta registrado", Toast.LENGTH_SHORT).show();
            }
        }
    }*/
}
