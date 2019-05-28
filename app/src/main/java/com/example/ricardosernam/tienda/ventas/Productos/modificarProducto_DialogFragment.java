package com.example.ricardosernam.tienda.ventas.Productos;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ricardosernam.tienda.DatabaseHelper;
import com.example.ricardosernam.tienda.R;
import com.example.ricardosernam.tienda.provider.ContractParaProductos;
import com.example.ricardosernam.tienda.ventas.ProductosVenta_class;
import com.example.ricardosernam.tienda.ventas.Ventas;

import java.text.DecimalFormat;

@SuppressLint("ValidFragment")
public class modificarProducto_DialogFragment extends android.support.v4.app.DialogFragment {
    private LinearLayout botones;
    private Cursor productoElegido;
    private SQLiteDatabase db;
    private Button aceptar, cancelar,sumar;
    private TextView productoSelecionado, unidad;
    private EditText precioNuevo, existente, nuevosExistente;
    private static ContentValues values;
    private float precio, existentes;
    private String producto;
    private int tipo;

    @SuppressLint("ValidFragment")
    public modificarProducto_DialogFragment(String producto, float precio, float existentes, int tipo) {
        this.producto = producto;
        this.precio = precio;
        this.existentes = existentes;
        this.tipo=tipo;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.dialog_fragment_modificar_producto, container);
        this.getDialog().setTitle("Modificar");///cambiamos titulo del DialogFragment
        productoSelecionado = rootView.findViewById(R.id.TVproductoModificar);
        precioNuevo = rootView.findViewById(R.id.ETprecioModificar);
        botones = rootView.findViewById(R.id.LLBotones);
        aceptar = rootView.findViewById(R.id.BtnAceptarPago);
        sumar = rootView.findViewById(R.id.BtnsumarNuevos);
        cancelar = rootView.findViewById(R.id.BtnCancelarPago);
        final DecimalFormat df = new DecimalFormat("#.00");
        unidad = rootView.findViewById(R.id.TVunidad);
        existente = rootView.findViewById(R.id.ETexistenteModificar);
        nuevosExistente = rootView.findViewById(R.id.ETexistenteSumar);

        //cantidad.setText("0");
        precioNuevo.setText(String.valueOf(precio));
        existente.setText(String.valueOf(existentes));

        values = new ContentValues();

        if (tipo == 1) {   ///pieza
            unidad.setText("Pieza(s)");
        } else {  ///gramos
            unidad.setText("Gramos");
        }
        //cantidad.setSelection(cantidad.getText().length());
        DatabaseHelper admin = new DatabaseHelper(getContext(), ContractParaProductos.DATABASE_NAME, null, ContractParaProductos.DATABASE_VERSION);
        db = admin.getWritableDatabase();

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //cantidad.setShowSoftInputOnFocus(false);

        productoSelecionado.setText(producto);


        sumar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((TextUtils.isEmpty(nuevosExistente.getText())))) {  /// es vacio
                    nuevosExistente.setError("Ingresa una cantidad");
                }
                else if((Float.parseFloat(nuevosExistente.getText().toString())==0)) {  /// es vacio
                    nuevosExistente.setError("Ingresa una cantidad valida");
                }
                else{
                    existente.setText(String.valueOf(Float.parseFloat(existente.getText().toString())+Float.parseFloat(nuevosExistente.getText().toString())));
                    nuevosExistente.setText("");
                }
            }
        });

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validar()){   /////si  ya se pago todo bien
                    productoElegido= db.rawQuery("select _id, existente from inventario where nombre_producto='"+producto+"'", null);
                    if(productoElegido.moveToFirst()){
                        values.put("precio", Float.parseFloat(precioNuevo.getText().toString()));
                        values.put("existente2", Float.parseFloat(existente.getText().toString()));

                        db.update("inventario", values, "_id='" + productoElegido.getString(0) + "'", null);
                        Ventas.rellenado_total(getContext());

                        //repetido(producto);
                        //ContractParaProductos.itemsProductosVenta.add(new ProductosVenta_class(producto, Float.parseFloat(cantidad.getText().toString()),precio, tipo, Float.parseFloat(subtotal.getText().toString()) , productoElegido.getInt(0)));
                        dismiss();
                        Toast.makeText(getContext(), "Producto Modificado", Toast.LENGTH_LONG).show();
                    }
                    //aceptarCompra.actualizar(0, null);
                }
            }
        });
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return rootView;
    }
    public Boolean validar(){
         Boolean validado=true;
        if(((TextUtils.isEmpty(precioNuevo.getText())))) {  /// es vacio
            validado=false;
            precioNuevo.setError("Ingresa una cantidad");
        }
        else if((Float.parseFloat(precioNuevo.getText().toString())==0)) {  /// es vacio
            validado=false;
            precioNuevo.setError("Ingresa una cantidad valida");
        }
        else if(((TextUtils.isEmpty(existente.getText())))) {  /// es vacio
            validado=false;
            existente.setError("Ingresa una cantidad");
        }
        else if((Float.parseFloat(existente.getText().toString())==0)) {  /// es vacio
            validado=false;
            existente.setError("Ingresa una cantidad valida");
        }
        return validado;
    }
    /*public static void repetido(String nombre){
        for(int i=0; i<ContractParaProductos.itemsProductosVenta.size(); i++) {
            if(nombre.equals(ContractParaProductos.itemsProductosVenta.get(i).getNombre())){  ///si se repite
                ContractParaProductos.itemsProductosVenta.remove(i);   ////eliminamos el previamente agregado
            }
        }
    }*/
}
