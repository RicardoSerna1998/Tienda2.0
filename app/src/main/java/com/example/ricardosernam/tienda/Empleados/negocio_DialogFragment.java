package com.example.ricardosernam.tienda.Empleados;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ricardosernam.tienda.DatabaseHelper;
import com.example.ricardosernam.tienda.R;
import com.example.ricardosernam.tienda.provider.ContractParaProductos;

import java.text.DecimalFormat;

@SuppressLint("ValidFragment")
public class negocio_DialogFragment extends android.support.v4.app.DialogFragment {
    private LinearLayout botones;
    private Cursor negocio;
    private SQLiteDatabase db;
    private Button aceptar, cancelar,escanear;
    private TextView  unidad;
    private EditText nombre, direccion, telefono;
    private static ContentValues values;

    private int tipo;

    @SuppressLint("ValidFragment")
    public negocio_DialogFragment() {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.dialog_fragment_negocio, container);
        this.getDialog().setTitle("Modificar");///cambiamos titulo del DialogFragment
        nombre = rootView.findViewById(R.id.ETnombreNegocio);
        direccion = rootView.findViewById(R.id.ETdireccion);
        telefono = rootView.findViewById(R.id.ETtelefono);


        botones = rootView.findViewById(R.id.LLBotones);
        aceptar = rootView.findViewById(R.id.BtnAceptarPago);
        cancelar = rootView.findViewById(R.id.BtnCancelarPago);
        final DecimalFormat df = new DecimalFormat("#.00");

        values = new ContentValues();

        DatabaseHelper admin = new DatabaseHelper(getContext(), ContractParaProductos.DATABASE_NAME, null, ContractParaProductos.DATABASE_VERSION);
        db = admin.getWritableDatabase();

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        negocio = db.rawQuery("select nombre_negocio, direccion, telefono from informacion", null);

        if(negocio.moveToFirst()){
            nombre.setText(negocio.getString(0));
            direccion.setText(negocio.getString(1));
            telefono.setText(negocio.getString(2));
        }
        //cantidad.setShowSoftInputOnFocus(false);

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validar()) {   /////si  ya se pago todo bien
                    values.put("nombre_negocio", nombre.getText().toString());
                    values.put("direccion", direccion.getText().toString());
                    values.put("telefono", telefono.getText().toString());

                    db.update("informacion", values, "_id='" + 1 + "'", null);

                        Empleados.relleno(getContext());
                        dismiss();
                        Toast.makeText(getContext(), "Información  modificada", Toast.LENGTH_LONG).show();
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

        if(((TextUtils.isEmpty(nombre.getText().toString().trim())))) {  /// es vacio
            validado=false;
            nombre.setError("Ingresa un nombre");
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
