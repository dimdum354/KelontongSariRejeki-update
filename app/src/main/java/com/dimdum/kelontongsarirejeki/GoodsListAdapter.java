package com.dimdum.kelontongsarirejeki;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dimzs on 03-Jan-18.
 */

public class GoodsListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Goods> goodssList;

    public GoodsListAdapter(Context context, int layout, ArrayList<Goods> goodssList) {
        this.context = context;
        this.layout = layout;
        this.goodssList = goodssList;
    }

    @Override
    public int getCount() {
        return goodssList.size();
    }

    @Override
    public Object getItem(int position) {
        return goodssList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView txtName, txtPrice;
    }

    @Override
    public View getView(int position, View view, ViewGroup ViewGrup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout,null);

            holder.txtName = (TextView)row.findViewById(R.id.txtName);
            holder.txtPrice = (TextView)row.findViewById(R.id.txtPrice);
            holder.imageView = (ImageView)row.findViewById(R.id.imgGoods);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        Goods goods = goodssList.get(position);

        holder.txtName.setText(goods.getName());
        holder.txtPrice.setText(goods.getPrice());

        byte[] goodsImage = goods.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(goodsImage, 0, goodsImage.length);
        holder.imageView.setImageBitmap(bitmap);

        return row;
    }
}
