package com.example.ricardosernam.tienda.ventas;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.ricardosernam.tienda.Carrito.Carrito;
import com.example.ricardosernam.tienda.DatabaseHelper;
import com.example.ricardosernam.tienda.provider.ContractParaProductos;
import com.example.ricardosernam.tienda.R;
import com.example.ricardosernam.tienda.ventas.Historial.Historial;
import com.example.ricardosernam.tienda.ventas.Productos.modificarProducto_DialogFragment;
import com.example.ricardosernam.tienda.ventas.Productos.nuevoProducto_DialogFragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static android.widget.Toast.LENGTH_LONG;

@SuppressLint("ValidFragment")
public class Ventas extends Fragment implements KeyListener {
    private SearchView nombreCodigo;
    private static Cursor fila, filaBusqueda, datoEscaneado, ventas, existentes, filaProducto, productoElegido;
    private static SQLiteDatabase db;
    private static TextView productos;
    private static RecyclerView recycler;
    private static RecyclerView.Adapter adapter;
    private static android.support.v4.app.FragmentManager fm;
    private static RecyclerView.LayoutManager lManager;
    private Button carrito, historial, imprimir, nuevo;
    private android.support.v7.app.ActionBar actionBar;
    public static ContentValues values = new ContentValues();
    public String tipo_empleado;
    public static int permisos;

    private static ArrayList<Productos_class> itemsProductos= new ArrayList <>(); ///Arraylist que contiene los productos///


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

    @SuppressLint("ValidFragment")
    public Ventas(String tipo_empleado){
        this.tipo_empleado=tipo_empleado;
    }
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
        imprimir= view.findViewById(R.id.BtnImprimriInventario);
        nuevo= view.findViewById(R.id.BtnAgregarProducto);

        productos= view.findViewById(R.id.TVproductos);


        fm=getFragmentManager();

        //tipo_empleado='Admin.' and activo=1 or tipo_empleado='Cajero'
        if(!tipo_empleado.equals("Admin.")){ //no tiene permisos
           imprimir.setVisibility(View.GONE); ///////ocultar boton de nuevo, imprimir, editar y eliminar producto
           nuevo.setVisibility(View.GONE);
           permisos=0;
        }
        else{  ///tiene permisos
            imprimir.setVisibility(View.VISIBLE); ///////ocultar boton de nuevo, imprimir, editar y eliminar producto
            nuevo.setVisibility(View.VISIBLE);
            permisos=1;
        }
        carrito.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if(ContractParaProductos.itemsProductosVenta.isEmpty()){
                    Toast.makeText(getContext(), "No hay productos comprados aún", Toast.LENGTH_SHORT).show();
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

        nuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new nuevoProducto_DialogFragment().show(fm, "Modificar_producto");

            }
        });
        imprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                existentes=db.rawQuery("select * from inventario where disponible=1" ,null);
                if(existentes.moveToFirst()){
                    findBT();
                }
                else{
                    Toast.makeText(getContext(), "No hay productos aún", LENGTH_LONG).show();
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
    public static void numero_productos(Context context) {
        existentes = db.rawQuery("select * from inventario where disponible=1", null);
        if (existentes.moveToFirst()) {
            productos.setText(String.valueOf(existentes.getCount())+ " productos" );
        } else {
            productos.setText("No hay productos aún");
        }
    }
    public static void actualizar_disponibles(Context context, String producto) {
        productoElegido= db.rawQuery("select _id from inventario where nombre_producto='"+producto+"'", null);
        values.put("disponible", 0);
        if(productoElegido.moveToFirst()){
            db.update("inventario", values, "_id='" + productoElegido.getString(0) + "'", null);
        }
        rellenado_total(context);
    }
    public static void rellenado_total(Context context){  ////volvemos a llenar el racycler despues de actualizar, o de una busqueda
       numero_productos(context);
        fila=db.rawQuery("select nombre_producto, precio, codigo_barras, existente from inventario where disponible=1 order by codigo_barras" ,null);

        if(fila.moveToFirst()) {///si hay un elemento
            itemsProductos.clear();
            itemsProductos.add(new Productos_class(fila.getString(0), fila.getFloat(1), fila.getString(2), fila.getFloat(3)));
            while (fila.moveToNext()) {
                itemsProductos.add(new Productos_class(fila.getString(0), fila.getFloat(1), fila.getString(2), fila.getFloat(3)));
            }
        }
        adapter = new VentasAdapter(itemsProductos, fm, context, permisos);///llamamos al adaptador y le enviamos el array como parametro
        //lManager = new LinearLayoutManager(this.getActivity());  //declaramos el layoutmanager
        lManager = new GridLayoutManager(context, 3);  //declaramos el layoutmanager
        recycler.setLayoutManager(lManager);
        recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

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
