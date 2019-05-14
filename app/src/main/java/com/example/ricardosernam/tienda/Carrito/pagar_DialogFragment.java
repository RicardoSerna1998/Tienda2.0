package com.example.ricardosernam.tienda.Carrito;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ricardosernam.tienda.DatabaseHelper;
import com.example.ricardosernam.tienda.Empleados.Empleados;
import com.example.ricardosernam.tienda.MainActivity;
import com.example.ricardosernam.tienda.provider.ContractParaProductos;
import com.example.ricardosernam.tienda.R;
import com.example.ricardosernam.tienda.sync.SyncAdapter;
import com.example.ricardosernam.tienda.utils.Constantes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.UUID;

import static android.widget.Toast.LENGTH_LONG;
import static com.example.ricardosernam.tienda.Carrito.Carrito.aceptar_cancelar;

@SuppressLint("ValidFragment")
public class pagar_DialogFragment extends android.support.v4.app.DialogFragment {
    private Button aceptar,cancelar, sumar, restar;
    private SQLiteDatabase db;
    private android.support.v4.app.FragmentManager fm;
    private Cursor empleado, venta, existente, informacion, filaProducto;
    private ContentValues values, values2, values3;
    private TextView total,cambio, deuda, abono;
    private EditText cantidad;
    private float totalPagar;
    private static DecimalFormat df;
    private CheckBox imprimir;   ///10
    private LinearLayout totalAPagar;

    private static int[]  pagos = {0,5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 60, 70, 80,90, 100, 150, 200, 250,300,350,400,450, 500, 600, 700,800, 900, 1000,1500, 2000};

    ///////////////////////////////////////////////////////////BLUETOOTH //////////////////////////////////////////////7
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


     @SuppressLint("ValidFragment")
     public pagar_DialogFragment(float totalPagar){
         this.totalPagar=totalPagar;
     }

    @Override
    public View onCreateView (final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView=inflater.inflate(R.layout.dialog_fragment_pagar,container);
        this.getDialog().setTitle("Cobrar");///cambiamos titulo del DialogFragment
        total=rootView.findViewById(R.id.TVtotalCompra);
        cambio=rootView.findViewById(R.id.TVcambio);
        imprimir=rootView.findViewById(R.id.CBimprimir);

        abono=rootView.findViewById(R.id.TVpagoAbono);
        deuda=rootView.findViewById(R.id.TVdeuda);
        cantidad=rootView.findViewById(R.id.ETcantidadPago);
        aceptar=rootView.findViewById(R.id.BtnAceptarPago);
        cancelar=rootView.findViewById(R.id.BtnCancelarPago);
        totalAPagar=rootView.findViewById(R.id.LLtotalApagar);


        //sumar = rootView.findViewById(R.id.BtnSumarRecibido);
        //restar = rootView.findViewById(R.id.BtnRestarRecibido);
        df = new DecimalFormat("#.00");


        ////////Bluetooth
        myLabel =  rootView.findViewById(R.id.label);

        fm=getFragmentManager();

        total.setText("$ "+String.valueOf(totalPagar));
        DatabaseHelper admin=new DatabaseHelper(getContext(), ContractParaProductos.DATABASE_NAME, null, ContractParaProductos.DATABASE_VERSION);
        db=admin.getWritableDatabase();

        totalAPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cantidad.setText(String.valueOf(totalPagar));
                }
        });
        deuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cantidad.setText(String.valueOf(totalPagar));
            }
        });


        cantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!(TextUtils.isEmpty(cantidad.getText())) & !cantidad.getText().toString().equals(".")) {
                    float cantidadCambio= Float.parseFloat(String.valueOf(cantidad.getText()))-totalPagar;///feria
                    if(cantidadCambio>=0) {
                        cambio.setText(String.valueOf(cantidadCambio));
                    }
                    else{
                        cambio.setText("");
                    }
                }
                else{
                    cambio.setText("");
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validar(totalPagar)){   /////si  ya se pago todo bien
                    if(imprimir.isChecked()){
                        ///imprimimos el recibo
                        cancelar.setEnabled(false);
                        aceptar.setEnabled(false);
                        imprimir.setEnabled(false);

                        // try {
                            findBT();
                         //   openBT();
                           // sendData();
                            //loseBT();
                       // }
                       // catch (IOException e) {
                         //   e.printStackTrace();
                       // }
                    }
                    else{
                        insertarVenta();
                        }

                    /*else{
                        insertarVenta();
                    }*/
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
    public Boolean validar(float total){
         Boolean validado=true;
        if(((TextUtils.isEmpty(cantidad.getText())))) {  /// es vacio
            validado=false;
            cantidad.setError("Ingresa una cantidad válida");
        }
        else{  ///hay algo
            if((Float.parseFloat(String.valueOf(cantidad.getText()))<total)){
                validado=false;
                cantidad.setError("Ingresa una cantidad válida");
            }
        }
        return validado;
    }

    /////////////////////////////////////////////////////////////////////BLUETOOTH ///////////////////////
    /////////////////////////////////////////////////////////////////// ABRIR
    // open bluetooth connection
    // this will find a bluetooth printer device
    void insertarVenta(){
        values = new ContentValues();
        /////obtener fecha actual
        java.util.Calendar c = java.util.Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = df.format(c.getTime());

        empleado= db.rawQuery("select idRemota from empleados where tipo_empleado='Admin.' and activo=1 or tipo_empleado='Cajero' and activo=1", null);

        if (empleado.moveToFirst()) {
            values.put("id_empleado", empleado.getString(0));
        }

        values.put("fecha", formattedDate);
        values.put(ContractParaProductos.Columnas.PENDIENTE_INSERCION, 1);
        db.insertOrThrow("ventas", null, values);
        Log.i("Venta", String.valueOf(values));    ////mostramos que valores se han insertado
        com.example.ricardosernam.tienda.Empleados.Empleados.datosAsincronizar();


/////////////////////////////////incersion-modificación ventas-inventario_detalles
        values2 = new ContentValues();
        venta = db.rawQuery("select * from ventas", null);

        for (int i = 0; i < ContractParaProductos.itemsProductosVenta.size(); i++) {
            ////////////////venta detalles/////////////////////////////77
            if (venta.moveToFirst()) {
                venta.moveToLast();
                values2.put("idRemota", venta.getString(0));
                values2.put("id_producto", ContractParaProductos.itemsProductosVenta.get(i).getIdRemota());
                values2.put("cantidad", ContractParaProductos.itemsProductosVenta.get(i).getCantidad());
                values2.put("precio",ContractParaProductos.itemsProductosVenta.get(i).getPrecio());
                values2.put("local",1);
                db.insertOrThrow("venta_detalles", null, values2);
                Log.i("Venta_detalles", String.valueOf(values2));    ////mostramos que valores se han insertado
            }
            //////////////////////////////////////////inventario detalles//////////////////////////////
            values3 = new ContentValues();
            ///obtenemos el guisado donde tenemos que descontar
            //le restamos a existente2
            existente = db.rawQuery("select existente2 from inventario where nombre_producto='" + ContractParaProductos.itemsProductosVenta.get(i).getNombre() + "'", null);
            if (existente.moveToFirst()) {
                float porcion = existente.getFloat(0) - (ContractParaProductos.itemsProductosVenta.get(i).getCantidad());
                values3.put("existente2", porcion);
                db.update("inventario", values3, "idRemota='" + ContractParaProductos.itemsProductosVenta.get(i).getIdRemota() + "'", null);
                Log.i("Inventario", String.valueOf(values3));    ////mostramos que valores se han insertado
            }
        }
        //DESCOMENTAR MODO ONLINE
        if(Empleados.online.isChecked()){
            SyncAdapter.sincronizarAhora(getContext(), false, 0, Constantes.INSERT_URL_TURNO);
            //SyncAdapter.sincronizarAhora(getContext(), true,0, Constantes.UPDATE_URL_INVENTARIO);   ///actualizamos el inventario disponible a cero
        }

        dismiss();
        Toast.makeText(getContext(), "Venta exitosa", Toast.LENGTH_LONG).show();
        MainActivity.bar.setDisplayHomeAsUpEnabled(false);

        aceptar_cancelar(fm);  ///volvemos al fragment
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
                       myLabel.setText("Revisa tu conexión con la impresora");   ///aquí sino está vinculado
                        cancelar.setEnabled(true);
                        aceptar.setEnabled(true);
                        imprimir.setEnabled(true);
                            //Toast.makeText(getContext(), "Revisa la conexión con tu impresora", LENGTH_LONG).show();
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
     * we have to listen and check if a data were sent to be printed.
     */
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
    ////////////////////////////////////////////////////////////////////////////////// ENVIAR
    // send data typed by the user to be printed
/*sendButton.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
            try {
                sendData();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    });*/
    // this will send text data to be printed by the bluetooth printer
    void sendData() throws IOException {
        try {
            String nombre=null;
            String direccion=null;
            String telefono=null;
            String vendedor=null;

            informacion= db.rawQuery("select nombre_negocio, direccion, telefono from informacion", null);
            if(informacion.moveToFirst()){
                nombre=informacion.getString(0);
                nombre += "\n";

                direccion=informacion.getString(1);
                direccion += "\n";

                telefono=informacion.getString(2);
                telefono += "\n \n";
            }

            empleado= db.rawQuery("select nombre_empleado from empleados where tipo_empleado='Admin.' and activo=1 or tipo_empleado='Cajero' and activo=1", null);
            if(empleado.moveToFirst()){
                vendedor="Cajero "+empleado.getString(0);
                vendedor += "\n \n";
            }



            String indice="---------";
            indice += "\n";

            String rayas="--------------------------------";
            rayas += "\n";

            mmOutputStream.write(nombre.getBytes());   ///// aqui imprime
            mmOutputStream.write(direccion.getBytes());   ///// aqui imprime
            mmOutputStream.write(telefono.getBytes());   ///// aqui imprime
            mmOutputStream.write(vendedor.getBytes());   ///// aqui imprime
            mmOutputStream.write(rayas.getBytes());   ///// aqui imprime


            for (int i = 0; i < ContractParaProductos.itemsProductosVenta.size(); i++) {
                ////////////////venta detalles/////////////////////////////
                String nombreProducto=null;
                filaProducto=db.rawQuery("select nombre_producto from inventario where idRemota='"+ContractParaProductos.itemsProductosVenta.get(i).getIdRemota()+"'" ,null);
                if(filaProducto.moveToFirst()) {///si hay un elemento}
                    nombreProducto=filaProducto.getString(0);
                    nombreProducto += "\n";
                }
                String subtotal;
                if(ContractParaProductos.itemsProductosVenta.get(i).getTipo()==0) { ////0 son gramos
                    subtotal=String.valueOf((ContractParaProductos.itemsProductosVenta.get(i).getCantidad()/1000)*ContractParaProductos.itemsProductosVenta.get(i).getPrecio());

                }
                else{ //1 es piezas*/
                    subtotal=String.valueOf(ContractParaProductos.itemsProductosVenta.get(i).getCantidad()*ContractParaProductos.itemsProductosVenta.get(i).getPrecio());

                }
                /*String items=String.valueOf(nombreProducto+" "+ContractParaProductos.itemsProductosVenta.get(i).getCantidad()+" "+ContractParaProductos.itemsProductosVenta.get(i).getPrecio()+" "+subtotal);
                    items += "\n";
                    mmOutputStream.write(items.getBytes());   *////// aqui imprime
                mmOutputStream.write(nombreProducto.getBytes());

                String items=String.valueOf("Cant "+ContractParaProductos.itemsProductosVenta.get(i).getCantidad()+" $"+ContractParaProductos.itemsProductosVenta.get(i).getPrecio()+" Sub. $"+subtotal);
                items += "\n";
                mmOutputStream.write(items.getBytes());
                mmOutputStream.write(indice.getBytes());   ///// aqui imprime



            }
            mmOutputStream.write(rayas.getBytes());   ///// aqui imprime
            String total="                Total $"+totalPagar;
            total += "\n";
            mmOutputStream.write(total.getBytes());   ///// aqui imprime


            String espacio=" ";
            espacio += "\n ";

            mmOutputStream.write(espacio.getBytes());   ///// aqui imprime

            String gracias="     Gracias por su compra";
            gracias += "\n \n \n \n \n";

            mmOutputStream.write(gracias.getBytes());   ///// aqui imprime


            // the text typed by the user
            //String msg = cantidad.getText().toString();
            // tell the user data were sent
            //myLabel.setText("Data sent.");
            closeBT();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////CERRAR
    // close bluetooth connection
/*closeButton.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
            try {
                closeBT();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    });*/

    // close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            insertarVenta();
            cancelar.setEnabled(true);
            aceptar.setEnabled(true);
            imprimir.setEnabled(true);
            } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //----------------------------------------------------------------------------------------------------------------------

}
