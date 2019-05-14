package com.example.ricardosernam.tienda.ventas.Historial.Productos_historial;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ricardosernam.tienda.R;
import com.example.ricardosernam.tienda.ventas.Historial.Historial;

import java.util.ArrayList;

public class Productos_historialAdapter extends RecyclerView.Adapter<Productos_historialAdapter.HistorialVentasViewHolder> {

    private ArrayList<Productos_historial_class> itemsProductosHistorial;
    //private interfaz_OnClick Interfaz;

    public Productos_historialAdapter(ArrayList<Productos_historial_class> itemsProductosHistorial) {  ///recibe el arrayCobrar como parametro
        this.itemsProductosHistorial=itemsProductosHistorial;
      //  this.Interfaz=Interfaz;

    }

    public class HistorialVentasViewHolder extends RecyclerView.ViewHolder {
        public TextView producto,cantidad, precio, subtotal;

        public HistorialVentasViewHolder(View itemView) {
            super(itemView);
            producto=itemView.findViewById(R.id.TVproductoHistorial);
            cantidad=itemView.findViewById(R.id.TVcantidadHistorial);
            precio=itemView.findViewById(R.id.TVprecioHistorial);
            subtotal=itemView.findViewById(R.id.TVsubTotalHistorial);

        }

    }

    @Override
    public HistorialVentasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjetas_productos_historial, parent, false);////mencionamos el archivo del layout
        return new HistorialVentasViewHolder(v);
    }
    @Override
    public int getItemCount() {
        return itemsProductosHistorial.size();
    }

    @Override
    public void onBindViewHolder(HistorialVentasViewHolder holder, final int position) {
        holder.producto.setText(itemsProductosHistorial.get(position).getProducto());
        holder.cantidad.setText(String.valueOf(itemsProductosHistorial.get(position).getCantidad()));
        holder.precio.setText("$ "+itemsProductosHistorial.get(position).getPrecio().toString());
        holder.subtotal.setText("$ "+itemsProductosHistorial.get(position).getSubTotal().toString());
    }
}
