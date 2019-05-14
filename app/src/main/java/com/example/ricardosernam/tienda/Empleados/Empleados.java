package com.example.ricardosernam.tienda.Empleados;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

;import com.example.ricardosernam.tienda.DatabaseHelper;
import com.example.ricardosernam.tienda._____interfazes.actualizado;
import com.example.ricardosernam.tienda.provider.ContractParaProductos;
import com.example.ricardosernam.tienda.R;
import com.example.ricardosernam.tienda.sync.SyncAdapter;
import com.example.ricardosernam.tienda.utils.Constantes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.LENGTH_LONG;

public class Empleados extends Fragment {     /////Fragment de categoria ventas
    public static Cursor empleados, informacion, estado, empleadosActivos, ipMode, onlineMode, datosAun, filaProducto, existentes;
    public static RecyclerView recycler;
    public static RecyclerView.Adapter adapter;
    public static RecyclerView.LayoutManager lManager;
    public static android.support.v4.app.FragmentManager fm;
    public static SQLiteDatabase db;
    public static TextView nombre, datos;
    public static Button establecer, imprimir;
    public static Button sync;
    public static EditText ip;
    public static CheckBox online;
    public ContentValues values=new ContentValues();
    private static ArrayList<Empleados_class> itemsEmpleados = new ArrayList<>();  ///Arraylist que contiene los cardviews seleccionados de productos

    TextView myLabel;
    // will enable user to enter any text to be printed
    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empleados, container, false);
        onViewCreated(view, savedInstanceState);


        fm = getFragmentManager();
        ip = view.findViewById(R.id.ETip);
        nombre= view.findViewById(R.id.TVnombreNegocio);
        datos= view.findViewById(R.id.TVdatosAun);
        online= view.findViewById(R.id.CBonline);


        establecer = view.findViewById(R.id.BtnEstablecer);
        imprimir= view.findViewById(R.id.BtnImprimriInventario);

        sync = view.findViewById(R.id.BtnSync);
        recycler = view.findViewById(R.id.RVempleados); ///declaramos el recycler

        DatabaseHelper admin = new DatabaseHelper(getContext(), ContractParaProductos.DATABASE_NAME, null, ContractParaProductos.DATABASE_VERSION);
        db = admin.getWritableDatabase();

        establecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (establecer.getText().equals(" Establecer IP ")) {  //validamos que no este vacio
                    if (TextUtils.isEmpty(ip.getText().toString().trim())) {
                        Toast.makeText(getContext(), "Ingresa un valor", LENGTH_LONG).show();
                    } else {
                        establecer.setText(" Modificar IP ");
                        ip.setEnabled(false);
                        estado=db.rawQuery("select ip, online from estados" ,null);

                        ///guardamo el estado de la pantalla
                        values.put(ContractParaProductos.Columnas.IP,  String.valueOf(ip.getText()));
                        if(estado.moveToFirst()){
                            db.update("estados", values, null, null);
                        }
                        else{
                            db.insertOrThrow("estados", null, values);
                        }
                        new Constantes("http://" + String.valueOf(ip.getText()));
                    }
                } else {
                    establecer.setText(" Establecer IP ");
                    ip.setEnabled(true);
                }
            }
        });
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                empleadosActivos = db.rawQuery("select * from empleados where activo=1", null);
                if(Empleados.ip.isEnabled()){
                    Toast.makeText(getContext(), "Establece la IP", LENGTH_LONG).show();
                }
                else {
                    ///SyncAdapter.sincronizarAhora(getContext(), false, 0, Constantes.GET_URL_INFORMACION);  descomentar en online
                    //quitar en online
                    SyncAdapter.sincronizarAhora(getContext(), false, 0, Constantes.INSERT_URL_TURNO);

                    //SyncAdapter.sincronizarAhora(getContext(), true, 0, Constantes.INSERT_URL_TURNO);
                }
            }
        });
        imprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                existentes=db.rawQuery("select * from inventario" ,null);
                if(existentes.moveToFirst()){
                    findBT();
                }
                else{
                    Toast.makeText(getContext(), "No hay productos aún", LENGTH_LONG).show();
                }

            }
        });
        online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estado=db.rawQuery("select ip, online from estados" ,null);


                if(online.isChecked()) {    ////si queremos activar el modo online
                    final AlertDialog.Builder aceptarVenta = new AlertDialog.Builder(getContext());
                    aceptarVenta .setTitle("Cuidado");
                    aceptarVenta .setMessage("El modo online requiere conexión a Internet");
                    aceptarVenta .setCancelable(false);
                    aceptarVenta .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface aceptarVenta, int id) {
                            values.put(ContractParaProductos.Columnas.ONLINE, 1);
                            if (estado.moveToFirst()) {
                                db.update("estados", values, null, null);
                            }
                            else{
                                db.insertOrThrow("estados", null, values);

                            }
                        }
                    });
                    aceptarVenta .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface aceptarVenta, int id) {
                            aceptarVenta .dismiss();
                            online.setChecked(false);
                        }
                    });
                    aceptarVenta .show();
                }
                else {    ////si queremos activar el modo offline
                    final AlertDialog.Builder aceptarVenta = new AlertDialog.Builder(getContext());
                    aceptarVenta .setTitle("Cuidado");
                    aceptarVenta .setMessage("Podras sincronizar tus datos hasta que tengas conexión a Internet");
                    aceptarVenta .setCancelable(false);
                    aceptarVenta .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface aceptarVenta, int id) {
                            values.put(ContractParaProductos.Columnas.ONLINE, 0);
                            if (estado.moveToFirst()) {
                                db.update("estados", values, null, null);
                            }
                            else{
                                db.insertOrThrow("estados", null, values);

                            }
                        }
                    });
                    aceptarVenta .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface aceptarVenta, int id) {
                            aceptarVenta .dismiss();
                            online.setChecked(true);
                        }
                    });
                    aceptarVenta .show();
                }

            }});
        relleno(getContext());
        return view;
    }

    public static void relleno(Context context) {    ///llamamos el adapter del recycler
        itemsEmpleados.clear();
        empleados = db.rawQuery("select nombre_empleado, tipo_empleado, activo, codigo from empleados ORDER by tipo_empleado, activo desc", null);
        informacion= db.rawQuery("select nombre_negocio, direccion, telefono from informacion", null);
        ipMode=db.rawQuery("select ip from estados" ,null);
        onlineMode=db.rawQuery("select online from estados" ,null);

        if(ipMode.moveToFirst()) {///si hay un elemento
            ///establecer.setText(" Modificar IP ");
            ip.setText(ipMode.getString(0));
        }
        if(onlineMode.moveToFirst()) {///si hay un elemento
            ///establecer.setText(" Modificar IP ");
            if(onlineMode.getInt(0)==1){
                online.setChecked(true);
            }
            else{
                online.setChecked(false);
            }
        }
        if(informacion.moveToFirst()){
            //if(!informacion.getString(0).isEmpty()){
            nombre.setVisibility(View.VISIBLE);
            nombre.setText(informacion.getString(0)+" "+informacion.getString(1)+" "+informacion.getString(2));
            //}
        }
        else{
            nombre.setVisibility(View.GONE);
            nombre.setText("");
        }

        if (empleados.moveToFirst()) {///si hay un elemento

            itemsEmpleados.add(new Empleados_class(empleados.getString(0), empleados.getString(1), empleados.getInt(2), empleados.getString(3)));
            while (empleados.moveToNext()) {
                itemsEmpleados.add(new Empleados_class(empleados.getString(0), empleados.getString(1), empleados.getInt(2), empleados.getString(3)));
            }
        }
        datosAsincronizar();
        adapter = new Empleados_ventasAdapter(itemsEmpleados, fm);///llamamos al adaptador y le enviamos el array como parametro
        lManager = new LinearLayoutManager(context);  //declaramos el layoutmanager
        recycler.setLayoutManager(lManager);
        recycler.setAdapter(adapter);
    }
    public static void datosAsincronizar (){
        datosAun= db.rawQuery("select * from ventas where pendiente_insercion=1", null);
        if(datosAun.moveToFirst()){
            datos.setText("Hay datos por sincronizar");
        }
        else{
            datos.setText(" ");
        }
    }
    void findBT() {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if(mBluetoothAdapter == null) {
                //Toast.makeText(getContext(), "Enciende la impresora", LENGTH_LONG).show();

            }

            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if(pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
                    if (device.getName().equals("BlueTooth Printer")) {
                        mmDevice = device;
                        openBT();

                        //insertarVenta();
                        break;
                    }
                    else{  ///si no esta vinculado
                        /*myLabel.setText("Revisa tu conexión con la impresora");   ///aquí sino está vinculado
                        cancelar.setEnabled(true);
                        aceptar.setEnabled(true);
                        imprimir.setEnabled(true)*/
                        Toast.makeText(getContext(), "Revisa la conexión con tu impresora", LENGTH_LONG).show();
                    }
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // tries to open a connection to the bluetooth printer device
    void openBT() throws IOException {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * after opening a connection to bluetooth printer device,
     * we have to listen and check if a data were sent to be printed.*/

    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];
            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                //myLabel.setText(data);

                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();
            sendData();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // this will send text data to be printed by the bluetooth printer
    void sendData() throws IOException {
        try {
            String total=null;
            filaProducto= db.rawQuery("select nombre_producto, precio, existente2 from inventario", null);

            if (filaProducto.moveToFirst()) {///si hay un elemento
                total="Productos: "+String.valueOf(filaProducto.getCount());
                total += "\n \n";
                mmOutputStream.write(total.getBytes());   ///// aqui imprime

                String items=String.valueOf(filaProducto.getString(0)+"  $"+filaProducto.getString(1)+"  "+filaProducto.getString(2));
                items += "\n";
                mmOutputStream.write(items.getBytes());

                while (filaProducto.moveToNext()) {
                    items=String.valueOf(filaProducto.getString(0)+"  $"+filaProducto.getString(1)+"  "+filaProducto.getString(2));
                    items += "\n";
                    mmOutputStream.write(items.getBytes());
                }
            }
            String gracias=" ";
            gracias += "\n \n \n \n";

            mmOutputStream.write(gracias.getBytes());   ///// aqui imprime
            closeBT();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
//            cancelar.setEnabled(true);
  //          aceptar.setEnabled(true);
   //         imprimir.setEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


