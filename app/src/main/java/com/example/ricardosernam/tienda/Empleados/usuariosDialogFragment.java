package com.example.ricardosernam.tienda.Empleados;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ricardosernam.tienda.DatabaseHelper;
import com.example.ricardosernam.tienda.MainActivity;
import com.example.ricardosernam.tienda._____interfazes.actualizado;
import com.example.ricardosernam.tienda.provider.ContractParaProductos;
import com.example.ricardosernam.tienda.R;
import com.example.ricardosernam.tienda.sync.SyncAdapter;
import com.example.ricardosernam.tienda.utils.Constantes;
import com.example.ricardosernam.tienda.ventas.Ventas;

import java.text.SimpleDateFormat;

import static com.example.ricardosernam.tienda.Empleados.Empleados.recycler;
import static com.example.ricardosernam.tienda.Empleados.Empleados.relleno;

@SuppressLint("ValidFragment")
public class usuariosDialogFragment extends android.support.v4.app.DialogFragment {     //clase que me crea el dialogFragment con productos
    public static String usuario, sesion, codigo, puesto, formattedDate;
    public TextView usuarioSeleccionado;
    public Button aceptar, cancelar;
    public EditText contrasena;
    private static ContentValues values, values2;
    private static SQLiteDatabase db;
    private actualizado Interfaz;
    private Cursor activos, empladosActualizados, seleccionados;
    public static android.support.v4.app.FragmentManager fm;


    public usuariosDialogFragment(String usuario, String sesion, String codigo, String puesto) {
        this.usuario = usuario;
        this.sesion = sesion;
        this.codigo = codigo;
        this.puesto = puesto;
    }
    public usuariosDialogFragment(String usuario, String sesion) {
        this.usuario = usuario;
        this.sesion = sesion;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Interfaz = (actualizado) getParentFragment();  ///interfaz para notificar el nuevo producto agregado
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement Callback interface");
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.dialog_fragment_sesion_usuarios, container);
        usuarioSeleccionado = rootView.findViewById(R.id.TVusuario);
        aceptar = rootView.findViewById(R.id.BtnAceptarSesion);
        cancelar = rootView.findViewById(R.id.BtnCancelarSesion);
        contrasena = rootView.findViewById(R.id.ETcodigo);
        ///activo= rootView.findViewById(R.id.RBactivo);


        values = new ContentValues();
        values2 = new ContentValues();

        fm = getFragmentManager();
        DatabaseHelper admin = new DatabaseHelper(getContext(), ContractParaProductos.DATABASE_NAME, null, ContractParaProductos.DATABASE_VERSION);
        db = admin.getWritableDatabase();
        empladosActualizados = db.rawQuery("select nombre_empleado, tipo_empleado, activo, codigo from empleados ORDER by tipo_empleado, activo desc", null);

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                java.util.Calendar c = java.util.Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                formattedDate = df.format(c.getTime());
                seleccionados = db.rawQuery("select codigo from empleados where nombre_empleado='"+usuario+"'", null);

                if (seleccionados.moveToFirst()) {
                    if (contrasena.getText().toString().equals(seleccionados.getString(0))) {  //codigo correcto
                        if (sesion.equals("Iniciar Sesión")) {
                            if (puesto.equals("Admin.") || puesto.equals("Cajero")) {
                                activos = db.rawQuery("select * from empleados where tipo_empleado='Admin.' and activo=1 or tipo_empleado='Cajero' and activo=1", null);
                                if (activos.moveToFirst()) {  ///hay un cajero/admin activo
                                    Toast.makeText(getContext(), "Cerrar sesión de otro cajero", Toast.LENGTH_LONG).show();
                                } else {  ///
                                    values.put("activo", 1);
                                    db.update("empleados", values, "nombre_empleado='" + usuario + "'", null);
                                    MainActivity.empleadoActivo.setText("Cajer@: " + usuario);

                                    insertarTurno(getContext());
                                    relleno(getContext());
                                    getDialog().dismiss();
                                    getFragmentManager().beginTransaction().replace(R.id.LLprincipal, new Ventas(puesto), "Ventas").addToBackStack("Ventas").commit(); ///cambio de fragment
                                }
                            } else {///otro puesto que no implica caja
                                values.put("activo", 1);
                                db.update("empleados", values, "nombre_empleado='" + usuario + "'", null);
                                relleno(getContext());
                                insertarTurno(getContext());
                                getDialog().dismiss();
                            }
                            ////insertamos incio de turno
                        }
                        ///////////////////// Cerrar sesión
                        else if (sesion.equals("Cerrar Sesión")) {
                            if (puesto.equals("Admin.") || puesto.equals("Cajero")) {
                                MainActivity.empleadoActivo.setText("");
                            }
                            values.put("activo", 0);
                            db.update("empleados", values, "nombre_empleado='" + usuario + "'", null);
                            actualizarTurno(getContext());
                            relleno(getContext());
                            getDialog().dismiss();
                        }
                         else if (sesion.equals("Agregar")) {   ////Agregar Empleado
                            getDialog().dismiss();
                            new nuevoEmpleado_DialogFragment().show(fm, "Modificar_producto");
                         }
                        else if (sesion.equals("Modificar")) {   ////Modificar Empleado
                            getDialog().dismiss();
                            new modificarEmpleado_DialogFragment(codigo, puesto).show(fm, "Modificar_producto");
                        }
                        else if (sesion.equals("Negocio")) {   ////Datos del negocio
                            getDialog().dismiss();
                            new negocio_DialogFragment().show(fm, "Modificar_Info");
                        }
                        else if (sesion.equals("Eliminar")) {   ///Eliminar emppleado
                            getDialog().dismiss();
                            final AlertDialog.Builder aceptarVenta = new AlertDialog.Builder(getContext());
                            aceptarVenta.setTitle("Cuidado");
                            aceptarVenta.setMessage("¿Seguro que quieres eliminar esta empleado?");
                            aceptarVenta.setCancelable(false);
                            aceptarVenta.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface aceptarVenta, int id) {
                                    Empleados.actualizar_disponibles(getContext(), codigo);
                                    aceptarVenta.dismiss();
                                }

                            });
                            aceptarVenta.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface aceptarVenta, int id) {
                                    aceptarVenta.dismiss();
                                }
                            });
                            aceptarVenta.show();

                        }

                        ///SyncAdapter.sincronizarAhora(getContext(), true, 0, Constantes.UPDATE_URL_EMPLEADOS);   ///actualizamos el inventario disponible a cero
                    } else {  //codigo incorrecto
                        contrasena.setError("Contraseña invalida");
                    }
                }
            }
        });
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        usuarioSeleccionado.setText(usuario);
        this.getDialog().setTitle(sesion);///cambiamos titulo del DialogFragment
        return rootView;
    }

    public static void insertarTurno(Context context) {
        Cursor empleado = db.rawQuery("select idRemota from empleados where nombre_empleado='" + usuario + "'", null);

        if (empleado.moveToFirst()) {
            values2.put("idRemota", empleado.getString(0));
        }
        values2.put("hora_inicio", formattedDate);
        values2.put(ContractParaProductos.Columnas.PENDIENTE_INSERCION, 1);
        db.insertOrThrow("turnos", null, values2);
        if(Empleados.online.isChecked()){
            SyncAdapter.sincronizarAhora(context, false, 0, Constantes.INSERT_URL_TURNO);
            //SyncAdapter.sincronizarAhora(context, true, 0, Constantes.UPDATE_URL_TURNO);  descomentar en online
        }
    }

    public static void actualizarTurno(Context context) {
       String id_empleado = null;
        Cursor empleado = db.rawQuery("select idRemota from empleados where nombre_empleado='" + usuario + "'", null);

        if (empleado.moveToFirst()) {
            id_empleado=empleado.getString(0);
        }
        values2.put("hora_fin", formattedDate);
        values2.put(ContractParaProductos.Columnas.PENDIENTE_INSERCION, 1);
        db.update("turnos", values2, "idRemota='" + id_empleado + "' and hora_fin IS NULL", null);
        if(Empleados.online.isChecked()){
            SyncAdapter.sincronizarAhora(context, false, 0, Constantes.INSERT_URL_TURNO);
            //SyncAdapter.sincronizarAhora(context, true, 0, Constantes.UPDATE_URL_TURNO);  descomentar en online
        }
    }
}
