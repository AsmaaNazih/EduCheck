package com.example.educheck.Controleur;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.educheck.Modele.AcademicBackground;
import com.example.educheck.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{
    private ArrayList<AcademicBackground> academicBackgrounds;

    public RecyclerAdapter(ArrayList academicBackgrounds){
        this.academicBackgrounds = academicBackgrounds;
    }
    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView itemImage;
        public TextView itemTitle;
        public TextView itemDetail;

        public ViewHolder(View itemView) {
            super(itemView);
            itemImage =
                    (ImageView)itemView.findViewById(R.id.item_image);
            itemTitle =
                    (TextView)itemView.findViewById(R.id.item_title);
            itemDetail =
                    (TextView)itemView.findViewById(R.id.item_detail);
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_parcour_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.itemTitle.setText(academicBackgrounds.get(i).getName());
        viewHolder.itemDetail.setText(academicBackgrounds.get(i).getDetails());
        viewHolder.itemImage.setImageResource(academicBackgrounds.get(i).getImage());
    }
    @Override
    public int getItemCount() {
        return academicBackgrounds.size();
    }
}

