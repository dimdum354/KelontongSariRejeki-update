package com.dimdum.kelontongsarirejeki;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by dimzs on 03-Jan-18.
 */

public class GoodsList extends AppCompatActivity {

    GridView gridView;
    ArrayList<Goods> list;
    GoodsListAdapter adapter = null;
    SQLiteHelper sqLiteHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goods_list_array);
        sqLiteHelper = new SQLiteHelper(this, "GoodsDB.sqlite", null,1);

        gridView = (GridView) findViewById(R.id.gridView);
        list = new ArrayList<>();
        adapter = new GoodsListAdapter(this, R.layout.goods_items, list);
        gridView.setAdapter(adapter);

        // get all data from sqlite
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT * FROM GOODS");
        list.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String price = cursor.getString(2);
            byte[] image = cursor.getBlob(3);

            list.add(new Goods(id, name, price, image));
        }
        adapter.notifyDataSetChanged();

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final CharSequence[] items = {"Update", "Delete"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(GoodsList.this);

                dialog.setTitle("Choose an action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item ==0){
                            Cursor c=MainActivity.sqLiteHelper.getData("SELECT id FROM GOODS");
                            ArrayList<Integer> arrID=new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            //Show Dialog at Here
                            showDialogUpdate(GoodsList.this, arrID.get(position));
                        } else {
                            //Delete
                            Cursor c=MainActivity.sqLiteHelper.getData("SELECT id FROM GOODS");
                            ArrayList<Integer> arrID=new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            showDialogDelete(arrID.get(position));
                        }

                    }
                });
                dialog.show();
                return true;
            }
        });
    }
    //Dialog untuk Mengkonfirmasi Delete
    ImageView imageViewGoods;
    private void showDialogUpdate(Activity activity, final int position){
        final Dialog dialog=new Dialog(activity);
        dialog.setContentView(R.layout.update_goods_activity);
        dialog.setTitle("Update");

        imageViewGoods=(ImageView)dialog.findViewById(R.id.imageViewGoods);
        final EditText edtName=(EditText) dialog.findViewById(R.id.edtName);
        final EditText edtPrice=(EditText) dialog.findViewById(R.id.edtPrice);
        Button btnUpdate=(Button) dialog.findViewById(R.id.btnUpdate);

        //Set Width untuk Dialog
        int width= (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);

        //Set Height untuk Dialog
        int height= (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.7);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        imageViewGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Request Photo Library
                ActivityCompat.requestPermissions(
                        GoodsList.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888
                );
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    MainActivity.sqLiteHelper.updateData(
                            edtName.getText().toString().trim(),
                            edtPrice.getText().toString().trim(),
                            MainActivity.imageViewToByte(imageViewGoods),
                            position
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Update Sukses",Toast.LENGTH_SHORT).show();
                }
                catch (Exception error){
                    Log.e("Update Error", error.getMessage());
                }
                updateGoodsList();
            }
        });
    }

    private void showDialogDelete(final int idGoods){
        AlertDialog.Builder dialogDelete=new AlertDialog.Builder(GoodsList.this);

        dialogDelete.setTitle("Peringatan");
        dialogDelete.setMessage("Apakah Anda Yakin Akan Menghapus Barang Ini ?");
        dialogDelete.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    MainActivity.sqLiteHelper.deleteData(idGoods);
                    Toast.makeText(getApplicationContext(), "Hapus Sukses",Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Log.e("Error", e.getMessage());
                }
                updateGoodsList();
            }
        });

        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }

    //Untuk Merefresh Grid View Setelah Update
    private void updateGoodsList(){
        //Hasil Cut
        //Get All Data dari SQLite
        Cursor cursor=MainActivity.sqLiteHelper.getData("SELECT * FROM GOODS");
        list.clear();
        while (cursor.moveToNext()){
            int id=cursor.getInt(0);
            String name=cursor.getString(1);
            String price=cursor.getString(2);
            byte[] image=cursor.getBlob(3);

            list.add(new Goods(id, name, price, image));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==888){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 888);
            }
            else{
                Toast.makeText(getApplicationContext(),"Anda Tidak Punya Ijin Untuk Mengakses Lokasi File",Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 888 && resultCode == RESULT_OK && data !=null){
            Uri uri=data.getData();
            try{
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageViewGoods.setImageBitmap(bitmap);
            } catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
