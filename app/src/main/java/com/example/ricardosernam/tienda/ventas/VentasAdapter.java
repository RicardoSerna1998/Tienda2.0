package com.example.ricardosernam.tienda.ventas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ricardosernam.tienda.R;
import com.example.ricardosernam.tienda.ventas.Productos.modificarProducto_DialogFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.example.ricardosernam.tienda.provider.ContractParaProductos.rojo;

public class VentasAdapter extends RecyclerView.Adapter <VentasAdapter.Productos_ventasViewHolder>{  ///adaptador para el Fragmet Ventas
    private ArrayList<Productos_class> itemsProductos;
    private FragmentManager fm;
    private Context context;
    private int permisos;

    public VentasAdapter(ArrayList<Productos_class> itemsProductos, FragmentManager fm, Context context, int permisos) {  ///recibe el arrayProductos como parametro y la interface
        this.itemsProductos=itemsProductos;
        this.fm=fm;
        this.context=context;
        this.permisos=permisos;
    }
    public  class Productos_ventasViewHolder extends RecyclerView.ViewHolder{    ////clase donde van los elementos del cardview
        // Campos respectivos de un item
        public TextView producto, precio, existentes;
        public Button modificar, eliminar;
        public LinearLayout botones;
        public Productos_ventasViewHolder(final View v) {   ////lo que se programe aqui es para cuando se le de clic a un item del recycler
            super(v);
            producto = v.findViewById(R.id.TVproducto);  ////Textview donde se coloca el nombre del producto
            precio=v.findViewById(R.id.TVprecio);
            existentes=v.findViewById(R.id.TVexistentes);
            modificar=v.findViewById(R.id.BtnmodificarProducto);
            eliminar=v.findViewById(R.id.BtnEliminarProducto);
            botones=v.findViewById(R.id.LLBotonesProductos);
        }
    }

    @Override
    public int getItemCount() {
        return itemsProductos.size();
    }

    @Override
    public Productos_ventasViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tarjetas_productos_ventas, viewGroup, false);
        return new Productos_ventasViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final Productos_ventasViewHolder holder, final int position) {
        holder.producto.setText(itemsProductos.get(position).getNombre());
        holder.precio.setText("$"+String.valueOf(itemsProductos.get(position).getPrecio()));
        DecimalFormat df = new DecimalFormat("#.00");

        if (permisos==1){  ///hay permisos para modificar e eliminar
            holder.botones.setVisibility(View.VISIBLE);

            /*holder.modificar.setVisibility(View.VISIBLE);
            holder.eliminar.setVisibility(View.VISIBLE);*/
        }else{
            holder.botones.setVisibility(View.GONE);

           /* holder.modificar.setVisibility(View.GONE);
            holder.eliminar.setVisibility(View.GONE);*/
        }
        if(itemsProductos.get(position).getExistentes()<=0) { ////0 son gramos
            holder.existentes.setTextColor(ColorStateList.valueOf(rojo));
        }
        if(itemsProductos.get(position).getCodigo_barras().equals("null")) { ////0 son gramos
            holder.existentes.setText(String.valueOf(df.format(itemsProductos.get(position).getExistentes())) +" Gramos");
        }
        else{ //1 es piezas
            holder.existentes.setText(String.valueOf(df.format(itemsProductos.get(position).getExistentes()) +" Pieza(s)"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemsProductos.get(position).getCodigo_barras().equals("null")) { ////0 son gramos
                    new cantidad_producto_DialogFragment(itemsProductos.get(position).getNombre(), itemsProductos.get(position).getPrecio(), 0).show(fm, "Producto_ventas");
                }
                else{ //1 es piezas
                    new cantidad_producto_DialogFragment(itemsProductos.get(position).getNombre(), itemsProductos.get(position).getPrecio(), 1).show(fm, "Producto_ventas");
                }
                }
        });
        ///para modificar producto
        holder.modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemsProductos.get(position).getCodigo_barras().equals("null")) { ////0 son gramos
                    new modificarProducto_DialogFragment(itemsProductos.get(position).getNombre(), itemsProductos.get(position).getPrecio(), itemsProductos.get(position).getExistentes(), 0).show(fm, "Modificar_producto");
                }
                else{ //1 es piezas
                    new modificarProducto_DialogFragment(itemsProductos.get(position).getNombre(), itemsProductos.get(position).getPrecio(), itemsProductos.get(position).getExistentes(), 1).show(fm, "Modificar_producto");
                }
            }
        });

        ///para eliminar producto
        holder.eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder aceptarVenta = new AlertDialog.Builder(context);
                aceptarVenta .setTitle("Cuidado");
                aceptarVenta .setMessage("¿Seguro que quieres eliminar esta venta?");
                aceptarVenta .setCancelable(false);
                aceptarVenta .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface aceptarVenta , int id) {
                        Ventas.actualizar_disponibles(context, itemsProductos.get(position).getNombre());
                        aceptarVenta.dismiss();
                    }

                });
                aceptarVenta .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface aceptarVenta, int id) {
                        aceptarVenta .dismiss();
                    }
                });
                aceptarVenta .show();
            }
        });

        }
    }
