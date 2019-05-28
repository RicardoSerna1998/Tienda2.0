package com.example.ricardosernam.tienda.Empleados;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ricardosernam.tienda.R;
import com.example.ricardosernam.tienda.ventas.Productos.modificarProducto_DialogFragment;
import com.example.ricardosernam.tienda.ventas.Ventas;

import java.util.ArrayList;

import static com.example.ricardosernam.tienda.provider.ContractParaProductos.rojo;
import static com.example.ricardosernam.tienda.provider.ContractParaProductos.verde;

public class Empleados_ventasAdapter extends RecyclerView.Adapter <Empleados_ventasAdapter.Productos_ventasViewHolder>{  ///adaptador para el Fragmet Ventas
    private ArrayList<Empleados_class> itemsEmpleados;
    private FragmentManager fm;
    private Context context;

    public Empleados_ventasAdapter(ArrayList<Empleados_class> itemsEmpleados, FragmentManager fm, Context context) {  ///recibe el arrayProductos como parametro y la interface
        this.itemsEmpleados=itemsEmpleados;
        this.fm=fm;
        this.context=context;
    }
    public static class Productos_ventasViewHolder extends RecyclerView.ViewHolder{    ////clase donde van los elementos del cardview
        // Campos respectivos de un item
        public AppCompatRadioButton activo ;
        public TextView nombre, puesto;
        public Button modificar, eliminar;

        public Productos_ventasViewHolder(final View v) {   ////lo que se programe aqui es para cuando se le de clic a un item del recycler
            super(v);
            nombre = v.findViewById(R.id.TVnombre);  ////Textview donde se coloca el nombre del producto
            puesto=v.findViewById(R.id.TVpuesto);
            activo= (AppCompatRadioButton) v.findViewById(R.id.RBactivo);
            modificar=v.findViewById(R.id.BtnModificarEmpleado);
            eliminar=v.findViewById(R.id.BtnEliminarEmpleado);
        }
    }

    @Override
    public int getItemCount() {
        return itemsEmpleados.size();
    }

    @Override
    public Productos_ventasViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tarjeta_empleados, viewGroup, false);
        return new Productos_ventasViewHolder(v);
    }

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final Productos_ventasViewHolder holder, final int position) {
        if(itemsEmpleados.get(position).getNombre().equals("Administrador")){
            holder.eliminar.setVisibility(View.INVISIBLE);
        }

        holder.nombre.setText(itemsEmpleados.get(position).getNombre());
        holder.puesto.setText(String.valueOf(itemsEmpleados.get(position).getPuesto()));

        if(itemsEmpleados.get(position).getActivo()==1){
            holder.activo.setButtonTintList(ColorStateList.valueOf(verde));
        }
        else{
            holder.activo.setButtonTintList(ColorStateList.valueOf(rojo));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemsEmpleados.get(position).getActivo()==1){
                    new usuariosDialogFragment(itemsEmpleados.get(position).getNombre(), "Cerrar Sesión", itemsEmpleados.get(position).getCodigo(), itemsEmpleados.get(position).getPuesto()).show(fm, "Empleados");
                }
                else{
                    new usuariosDialogFragment(itemsEmpleados.get(position).getNombre(), "Iniciar Sesión", itemsEmpleados.get(position).getCodigo(), itemsEmpleados.get(position).getPuesto()).show(fm, "Empleados");
                }
            }
        });

        ///para modificar producto
        holder.modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ///validamos si no es el superAdmin
                new usuariosDialogFragment("Administrador", "Modificar", itemsEmpleados.get(position).getNombre(), itemsEmpleados.get(position).getCodigo()).show(fm, "Empleados");
            }
        });

        ///para eliminar producto
        holder.eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new usuariosDialogFragment("Administrador", "Eliminar", itemsEmpleados.get(position).getNombre(), itemsEmpleados.get(position).getCodigo()).show(fm, "Empleados");
            }
        });
        }
    }
