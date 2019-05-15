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
import com.example.ricardosernam.tienda.ventas.Ventas;

import java.text.DecimalFormat;

@SuppressLint("ValidFragment")
public class nuevoEmpleado_DialogFragment extends android.support.v4.app.DialogFragment {
    private LinearLayout botones;
    private Cursor productoElegido;
    private SQLiteDatabase db;
    private Button aceptar, cancelar,escanear;
    private TextView  unidad;
    private EditText nombre, codigo;
    private Spinner puesto;
    private static ContentValues values;

    private int tipo;

    @SuppressLint("ValidFragment")
    public nuevoEmpleado_DialogFragment() {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.dialog_fragment_nuevo_empleado, container);
        this.getDialog().setTitle("Modificar");///cambiamos titulo del DialogFragment
        puesto = rootView.findViewById(R.id.SPNpuestoEmpleado);
        nombre = rootView.findViewById(R.id.ETnombreEmpleado);
        codigo = rootView.findViewById(R.id.ETcodigoEmpleado);


        botones = rootView.findViewById(R.id.LLBotones);
        aceptar = rootView.findViewById(R.id.BtnAceptarPago);
        cancelar = rootView.findViewById(R.id.BtnCancelarPago);
        final DecimalFormat df = new DecimalFormat("#.00");

        values = new ContentValues();

        DatabaseHelper admin = new DatabaseHelper(getContext(), ContractParaProductos.DATABASE_NAME, null, ContractParaProductos.DATABASE_VERSION);
        db = admin.getWritableDatabase();

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //cantidad.setShowSoftInputOnFocus(false);

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validar()) {   /////si  ya se pago todo bien
                    values.put("activo", 0);
                    values.put("nombre_empleado", nombre.getText().toString());
                    values.put("codigo", codigo.getText().toString());


                    if (puesto.getSelectedItemId() == 0) {
                        values.put("tipo_empleado", "Admin.");
                    }
                    else if(puesto.getSelectedItemId()==1){
                        values.put("tipo_empleado", "Cajero");
                    }
                    else{
                        values.put("tipo_empleado", "Otro");
                    }

                        db.insertOrThrow("empleados", null, values);
                        Empleados.relleno(getContext());

                        dismiss();
                        Toast.makeText(getContext(), "Empleado Agregado", Toast.LENGTH_LONG).show();
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
        else if(((TextUtils.isEmpty(codigo.getText().toString().trim()))) || codigo.getText().length()<5) {  /// es vacio
            validado=false;
            codigo.setError("Necesitas contraseña mayor a 4 caracteres");
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
