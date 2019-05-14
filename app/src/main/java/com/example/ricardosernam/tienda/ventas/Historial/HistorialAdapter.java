package com.example.ricardosernam.tienda.ventas.Historial;

import android.annotation.SuppressLint;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ricardosernam.tienda.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.HistorialVentasViewHolder> {

    private ArrayList<Historial_class> itemsHistorial;
    private Context context;

    public HistorialAdapter(Context context, ArrayList<Historial_class> itemsCobrar) {  ///recibe el arrayCobrar como parametro
        this.itemsHistorial=itemsCobrar;
        this.context=context;

    }

    public class HistorialVentasViewHolder extends RecyclerView.ViewHolder {
        public TextView empleado, fecha, total;
        public RecyclerView recycler;

        public HistorialVentasViewHolder(View itemView) {
            super(itemView);
            empleado=itemView.findViewById(R.id.TVempleadoHistorial);
            fecha=itemView.findViewById(R.id.TVfechaHistorial);
            total=itemView.findViewById(R.id.TVtotalHistorial);

            recycler = itemView.findViewById(R.id.RVproductosHistorial); ///declaramos el recycler
        }

    }

    @Override
    public HistorialVentasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjetas_historial, parent, false);////mencionamos el archivo del layout
        return new HistorialVentasViewHolder(v);
    }
    @Override
    public int getItemCount() {
        return itemsHistorial.size();
    }
    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(HistorialVentasViewHolder holder, final int position) {
        String[]  meses = {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre", "Octubre", "Noviembre", "Diciembre"};
        String mes=null;
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).parse(String.valueOf(itemsHistorial.get(position).getFecha()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Calendar cal = Calendar.getInstance();
        GregorianCalendar cal = new GregorianCalendar(TimeZone.getDefault());
        cal.setTime(date);

        for(int i=0; i<meses.length;i++){
            ////      1             0
            if((cal.get(Calendar.MONTH))==i){
                mes=meses[i];
            }
        }
        String año=String.valueOf(cal.get(Calendar.YEAR));
        String dia=String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String hora=String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
        String minuto=String.valueOf(cal.get(Calendar.MINUTE));



        holder.empleado.setText("Cajero: "+itemsHistorial.get(position).getEmpleado());
        holder.fecha.setText("Fecha: "+dia+"/"+mes +"/"+año+" "+hora+":"+minuto);
        //holder.fecha.setText(itemsHistorial.get(position).getFecha());
        Historial.rellenado_items(itemsHistorial.get(position).getIdVenta(), holder.recycler, context);
        Historial.calcularTotalporVenta(holder.total);

    }
}
