package com.example.matheus.volleyinsertdata;

//import com.android.volley.R;
import com.example.matheus.volleyinsertdata.AppControlador;
import com.example.matheus.volleyinsertdata.ClasseCategoria;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ClasseCategoria> movieItems;
    ImageLoader imageLoader = AppControlador.getInstance().getImageLoader();

    public CustomListAdapter(Activity activity, List<ClasseCategoria> movieItems) {
        this.activity = activity;
        this.movieItems = movieItems;
    }

    @Override
    public int getCount() {
        return movieItems.size();
    }

    @Override
    public Object getItem(int location) {
        return movieItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.categoria_item, null);

        if (imageLoader == null)
            imageLoader = AppControlador.getInstance().getImageLoader();
        NetworkImageView img_categoria = (NetworkImageView) convertView.findViewById(R.id.img_categoria);
        TextView desc_categoria = (TextView) convertView.findViewById(R.id.desc_categoria);


        // getting movie data for the row
        ClasseCategoria m = movieItems.get(position);

        //  image
        img_categoria.setImageUrl(m.getImg_categoria(), imageLoader);

        // desc
        desc_categoria.setText(m.getDesc_categoria());

        return convertView;
    }

}