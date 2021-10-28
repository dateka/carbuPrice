package com.example.zipfiledownload;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    String data1[];
    String data2[];
    String data3[];
    String data4[];


    public MyAdapter(Context ct, String s1[],  String s2[], String s3[], String s4[]) {
        context = ct;
        data1 = s1;
        data2 = s2;
        data3 = s3;
        data4 = s4;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.NomStation.setText(data1[position]);
        holder.PrixGo.setText(data2[position]);
        holder.PrixSp95.setText(data3[position]);
        holder.PrixSp98.setText(data4[position]);
    }

    @Override
    public int getItemCount() {
        return data1.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView NomStation, PrixGo, PrixSp95, PrixSp98;

        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
            NomStation = (TextView) itemView.findViewById(R.id.NomStation);
            PrixGo = (TextView) itemView.findViewById(R.id.PrixGo);
            PrixSp95 = (TextView) itemView.findViewById(R.id.PrixSp95);
            PrixSp98 = (TextView) itemView.findViewById(R.id.PrixSp98);
        }
    }
}
