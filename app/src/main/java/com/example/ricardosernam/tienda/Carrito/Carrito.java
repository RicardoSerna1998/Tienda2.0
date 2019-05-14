package com.example.ricardosernam.tienda.Carrito;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ricardosernam.tienda.DatabaseHelper;
import com.example.ricardosernam.tienda.provider.ContractParaProductos;
import com.example.ricardosernam.tienda.R;
import com.example.ricardosernam.tienda.ventas.ProductosVenta_class;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class Carrito extends Fragment {
    private static Cursor datosSeleccionado;
    private static SQLiteDatabase db;
    private static RecyclerView recycler;
    private static RecyclerView.Adapter adapter;
    private static android.support.v4.app.FragmentManager fm;
    private static RecyclerView.LayoutManager lManager;
    public static TextView total;
    private static Button aceptar, eliminar, cerrar;
    private static DecimalFormat df;
    private static ArrayList<ProductosVenta_class> itemsProductosVenta2;


    public Carrito() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_carrito, container, false);
        aceptar=view.findViewById(R.id.BtnAceptarCompra);
        eliminar=view.findViewById(R.id.BtnEliminarCompra);
        recycler = view.findViewById(R.id.RVproductosCarrito); ///declaramos el recycler
        total = view.findViewById(R.id.TVtotal); ///declaramos el recycle
        df = new DecimalFormat("#.00");

        fm=getFragmentManager();


        DatabaseHelper admin=new DatabaseHelper(getContext(), ContractParaProductos.DATABASE_NAME, null, ContractParaProductos.DATABASE_VERSION);
        db=admin.getWritableDatabase();

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarItems()){
                    new pagar_DialogFragment(Float.parseFloat(String.valueOf(total.getText()))).show(fm, "Producto_ventas");
                }
                else{
                    Toast.makeText(getContext(), "Checa las cantidades", Toast.LENGTH_LONG).show();
                }
                }
        });
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder aceptarVenta = new AlertDialog.Builder(getContext());
                aceptarVenta .setTitle("Cuidado");
                aceptarVenta .setMessage("¿Seguro que quieres eliminar esta venta?");
                aceptarVenta .setCancelable(false);
                aceptarVenta .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface aceptarVenta , int id) {
                                aceptar_cancelar(fm);
                                //ContractParaProductos.itemsProductosVenta.clear();
                                aceptarVenta.dismiss();
                        }

                });
                aceptarVenta .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface aceptarVenta, int id) {
                        aceptarVenta .dismiss();
                    }
                });
                aceptarVenta .show();
            }
        });

        rellenado_total(getContext());
        calcularTotal();
        return view;

    }
    public static void aceptar_cancelar(FragmentManager fm){
        //ContractParaProductos.itemsProductosVenta.removeAll(ContractParaProductos.itemsProductosVenta);
        ContractParaProductos.itemsProductosVenta.clear();
        fm.beginTransaction().replace(R.id.LLprincipal, fm.findFragmentByTag("Ventas")).addToBackStack("Ventas").commit(); ///cambio de fragment
    }
    public static void rellenado_total(Context context){  ////volvemos a llenar el racycler despues de actualizar, o de una busqueda
        itemsProductosVenta2=new ArrayList<>(ContractParaProductos.itemsProductosVenta);
        ContractParaProductos.itemsProductosVenta.clear();
        ContractParaProductos.itemsProductosVenta=new ArrayList<>(itemsProductosVenta2);
        adapter = new CarritosAdapter(ContractParaProductos.itemsProductosVenta, context);
        lManager = new LinearLayoutManager(context);  //declaramos el layoutmanager
        recycler.setLayoutManager(lManager);
        recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    public static void actualizar(Float cantidad, String nombre, final int position) {
        datosSeleccionado=db.rawQuery("select precio, codigo_barras, idRemota from inventario where nombre_producto='"+nombre+"'" ,null);
        if(datosSeleccionado.moveToFirst()) {
            if (datosSeleccionado.getString(1).equals("null")) {  ///fruta
                ContractParaProductos.itemsProductosVenta.set(position, new ProductosVenta_class(nombre, cantidad, datosSeleccionado.getFloat(0), 0, (cantidad / 1000) * datosSeleccionado.getFloat(0), datosSeleccionado.getInt(2)));//obtenemos el cardview seleccionado y lo agregamos a items2
            } else {   //pieza
                ContractParaProductos.itemsProductosVenta.set(position, new ProductosVenta_class(nombre, cantidad, datosSeleccionado.getFloat(0), 1, cantidad * datosSeleccionado.getFloat(0), datosSeleccionado.getInt(2)));//obtenemos el cardview seleccionado y lo agregamos a items2
            }
        }
        calcularTotal();
    }

    public static void calcularTotal() {
        float suma = 0;
        if (ContractParaProductos.itemsProductosVenta.isEmpty()) {
            fm.beginTransaction().replace(R.id.LLprincipal, fm.findFragmentByTag("Ventas")).addToBackStack("Ventas").commit(); ///cambio de fragment
        }
        for (int i = 0; i < ContractParaProductos.itemsProductosVenta.size(); i++) {
            suma = suma + ContractParaProductos.itemsProductosVenta.get(i).getSubtotal();
            total.setText(String.valueOf(df.format(suma)));
        }
    }
    public static boolean  validarItems() {
        boolean resultado=true;
        for (int i = 0; i < ContractParaProductos.itemsProductosVenta.size(); i++) {
            if(ContractParaProductos.itemsProductosVenta.get(i).getCantidad()==0){
                resultado=false;
            }
        }
        return resultado;
    }
}
