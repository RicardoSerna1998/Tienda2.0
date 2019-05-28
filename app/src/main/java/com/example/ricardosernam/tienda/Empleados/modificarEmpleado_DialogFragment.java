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
public class modificarEmpleado_DialogFragment extends android.support.v4.app.DialogFragment {
    private LinearLayout botones;
    private Cursor productoElegido;
    private SQLiteDatabase db;
    private Button aceptar, cancelar,sumar;
    private TextView nombre;
    private EditText contrasena, comfirmar;
    private static ContentValues values;
    private String empleado,  codigo;
    private Spinner tipoEmpleado;
    private int tipo;



    @SuppressLint("ValidFragment")
    public modificarEmpleado_DialogFragment(String empleado, String codigo) {
        this.empleado = empleado;
        this.codigo=codigo;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.dialog_fragment_modificar_empleado, container);
        this.getDialog().setTitle("Modificar");///cambiamos titulo del DialogFragment

        nombre = rootView.findViewById(R.id.TVnombreModificar);

        tipoEmpleado = rootView.findViewById(R.id.SPNpuestoModificar);

        contrasena = rootView.findViewById(R.id.ETcodigoModificar);
        comfirmar = rootView.findViewById(R.id.ETcodigoConfirmar);

        contrasena.setText(codigo);
        comfirmar.setText(codigo);

        aceptar = rootView.findViewById(R.id.BtnAceptarPago);
        cancelar = rootView.findViewById(R.id.BtnCancelarPago);


        values = new ContentValues();

        ///si es el usuario principal el que se modificará, no se puede cambiar el puesto
        if(empleado.equals("Administrador")){
            tipoEmpleado.setEnabled(false);
        }


        //cantidad.setSelection(cantidad.getText().length());
        DatabaseHelper admin = new DatabaseHelper(getContext(), ContractParaProductos.DATABASE_NAME, null, ContractParaProductos.DATABASE_VERSION);
        db = admin.getWritableDatabase();

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //cantidad.setShowSoftInputOnFocus(false);

        nombre.setText(empleado);


        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validar()){   /////si  ya se pago todo bien
                    productoElegido= db.rawQuery("select _id from empleados where nombre_empleado='"+empleado+"'", null);
                    if(productoElegido.moveToFirst()){
                        values.put("codigo", contrasena.getText().toString());

                        if (tipoEmpleado.getSelectedItemId() == 0) {
                            values.put("tipo_empleado", "Admin.");
                        }
                        else if(tipoEmpleado.getSelectedItemId()==1){
                            values.put("tipo_empleado", "Cajero");
                        }
                        else{
                            values.put("tipo_empleado", "Otro");
                        }

                        db.update("empleados", values, "_id='" + productoElegido.getString(0) + "'", null);
                        Empleados.relleno(getContext());
                        dismiss();
                        Toast.makeText(getContext(), "Empleado Modificado", Toast.LENGTH_LONG).show();
                    }
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
    public Boolean validar() {
        Boolean validado = true;
        if (((TextUtils.isEmpty(contrasena.getText().toString().trim())))) {  /// es vacio
            validado = false;
            contrasena.setError("Ingresa la contraseña");
        }
        else if(((TextUtils.isEmpty(comfirmar.getText().toString().trim())))) {  /// es vacio
        validado=false;
        comfirmar.setError("Confirma la contraseña");
        }
        else if(!comfirmar.getText().toString().equals(contrasena.getText().toString())) {  /// es vacio
            validado=false;
            comfirmar.setError("Confirma correctamente la contraseña");
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
