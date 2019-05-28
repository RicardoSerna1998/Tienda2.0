package com.example.ricardosernam.tienda.ventas.Productos;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
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
import com.example.ricardosernam.tienda.ventas.Ventas;

import java.text.DecimalFormat;

@SuppressLint("ValidFragment")
public class nuevoProducto_DialogFragment extends android.support.v4.app.DialogFragment {
    private LinearLayout botones;
    public static Cursor productoElegido;
    private SQLiteDatabase db;
    private Button aceptar, cancelar,escanear;
    private TextView  unidad;
    private EditText precioNuevo, existente, nombre, codigo;
    private static ContentValues values;

    private int tipo;

    @SuppressLint("ValidFragment")
    public nuevoProducto_DialogFragment() {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.dialog_fragment_nuevo_producto, container);
        this.getDialog().setTitle("Modificar");///cambiamos titulo del DialogFragment
        precioNuevo = rootView.findViewById(R.id.ETprecioProducto);
        existente = rootView.findViewById(R.id.ETexistentesProducto);
        nombre = rootView.findViewById(R.id.ETnombreProducto);
        codigo = rootView.findViewById(R.id.ETcodigoProducto);


        botones = rootView.findViewById(R.id.LLBotones);
        aceptar = rootView.findViewById(R.id.BtnAceptarPago);
        escanear = rootView.findViewById(R.id.BtnescanearProducto);
        cancelar = rootView.findViewById(R.id.BtnCancelarPago);
        final DecimalFormat df = new DecimalFormat("#.00");
        unidad = rootView.findViewById(R.id.TVunidad);

        values = new ContentValues();

        DatabaseHelper admin = new DatabaseHelper(getContext(), ContractParaProductos.DATABASE_NAME, null, ContractParaProductos.DATABASE_VERSION);
        db = admin.getWritableDatabase();

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //cantidad.setShowSoftInputOnFocus(false);

        escanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validar()){   /////si  ya se pago todo bien
                        values.put("disponible", 1);
                        values.put("nombre_producto", nombre.getText().toString());
                        values.put("precio", Float.parseFloat(precioNuevo.getText().toString()));
                        values.put("existente", Float.parseFloat(existente.getText().toString()));
                        values.put("existente2", Float.parseFloat(existente.getText().toString()));

                    if(((TextUtils.isEmpty(codigo.getText())))) {  /// es vacio
                        values.put("codigo_barras", "null");
                    }
                    {
                        values.put("codigo_barras", existente.getText().toString());
                    }


                        db.insertOrThrow("inventario", null, values);
                        Ventas.rellenado_total(getContext());

                        dismiss();
                        Toast.makeText(getContext(), "Producto Agregado", Toast.LENGTH_LONG).show();
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
        productoElegido = db.rawQuery("select * from inventario where nombre_producto='"+nombre.getText().toString() +"'", null);

        if(((TextUtils.isEmpty(nombre.getText().toString().trim())))) {  /// es vacio
            validado=false;
            nombre.setError("Ingresa un nombre");
        }
        else if(((TextUtils.isEmpty(precioNuevo.getText().toString().trim())))) {  /// es vacio
            validado=false;
            precioNuevo.setError("Ingresa una cantidad");
        }
        else if((Float.parseFloat(precioNuevo.getText().toString().trim())==0)) {  /// es vacio
            validado=false;
            precioNuevo.setError("Ingresa una cantidad valida");
        }
        else if(((TextUtils.isEmpty(existente.getText().toString().trim())))) {  /// es vacio
            validado=false;
            existente.setError("Ingresa una cantidad");
        }
        else if((Float.parseFloat(existente.getText().toString().trim())==0)) {  /// es vacio
            validado=false;
            existente.setError("Ingresa una cantidad valida");
        }
        else if(productoElegido.moveToFirst()) {  /// es vacio
            validado=false;
            Toast.makeText(getContext(), "Producto existente", Toast.LENGTH_LONG).show();
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
