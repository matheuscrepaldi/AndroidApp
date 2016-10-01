package com.example.matheus.volleyinsertdata;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.drakeet.materialdialog.MaterialDialog;

public class DenunciaActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText data, categoria;
    private int yy;
    private int mm;
    private int dd;
    private TextView teste;
    private String yourAddress, yourCity;
    private Button buttonUpload;
    private ImageView imageView1, imageView2, imageView3, imageView4;
    private EditText editTextName;
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    private int OPEN_CAMERA_REQUEST = 2;
    private String UPLOAD_URL ="http://192.168.0.5/tcc/ws/volleyDenunciaImg.php";
    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";
    public static final String TAG = "LOG";
    MaterialDialog mMaterialDialog;
    public AlertDialog dialog, alerta;
    public static final int REQUEST_PERMISSIONS_CODE = 128;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denuncia);

        data = (EditText) findViewById(R.id.data);
        categoria = (EditText) findViewById(R.id.categoria);
        teste = (TextView) findViewById(R.id.teste);
        imageView1  = (ImageView) findViewById(R.id.imageView1);
        imageView2  = (ImageView) findViewById(R.id.imageView2);
        imageView3  = (ImageView) findViewById(R.id.imageView3);
        imageView4  = (ImageView) findViewById(R.id.imageView4);
        imageView1.setOnClickListener(this);
        imageView1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                return true;
            }
        });
        imageView2.setOnClickListener(this);
        imageView3.setOnClickListener(this);
        imageView4.setOnClickListener(this);


        final String[] items = new String[] {"Abrir Galeria","Abrir Câmera"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione a Imagem");

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which == 0){
                    showFileChooser();
                }

                else takePhoto();
            }
        });



        final Calendar c = Calendar.getInstance();
        yy = c.get(Calendar.YEAR);
        mm = c.get(Calendar.MONTH);
        dd = c.get(Calendar.DAY_OF_MONTH);

        data.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(dd).append("/").append(mm + 1).append("/")
                .append(yy));

        dialog = builder.create();

        Bundle bundle = getIntent().getExtras();
        double latitude = bundle.getDouble("latitude");
        double longitude = bundle.getDouble("longitude");

        Geocoder geocoder;
        List<Address> yourAddresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            yourAddresses= geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (yourAddresses.size() > 0)
        {
             yourAddress = yourAddresses.get(0).getAddressLine(0);
            yourCity = yourAddresses.get(0).getAddressLine(1);
            //String yourCountry = yourAddresses.get(0).getAddressLine(2);
        }


        teste.setText("Endereço: " + yourAddress + "Cidade: " + yourCity);
    }


    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage(){
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(DenunciaActivity.this, s , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(DenunciaActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                //Getting Image Name
                String name = editTextName.getText().toString().trim();

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_IMAGE, image);
                params.put(KEY_NAME, name);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    public void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    public void takePhoto()
    {
        //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, OPEN_CAMERA_REQUEST);
    }

    public void verificaImagem(Bitmap bitmap){

        if(imageView2.getVisibility() == View.INVISIBLE){
            imageView1.setImageBitmap(bitmap);
            imageView2.setVisibility(View.VISIBLE);

        }

        else if(imageView3.getVisibility() == View.INVISIBLE){
            imageView2.setImageBitmap(bitmap);
            imageView3.setVisibility(View.VISIBLE);
        }

        else if(imageView4.getVisibility() == View.INVISIBLE){
            imageView3.setImageBitmap(bitmap);
            imageView4.setVisibility(View.VISIBLE);
        }

        else if(imageView4.getVisibility() == View.VISIBLE){
            imageView4.setImageBitmap(bitmap);
        }
    }


   // @Override
    public void onClick(View v) {


        if(v == imageView1){

            dialog.show();
        }

        if(v == imageView2){

            dialog.show();
        }

        if(v == imageView3){

            dialog.show();
        }

        if(v == imageView4){

            dialog.show();
        }
    }

    public void OnLongClick(View v){

       if(v == imageView1){
           exemplo_simples();
       }
    }

    public void abrirCategorias(View view) {

        Intent intent = new Intent(this, CategoriaActivity.class);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intentResultado) {
        super.onActivityResult(requestCode, resultCode, intentResultado);
        if (resultCode == RESULT_OK){
            if (intentResultado.hasExtra("categoria")){
                ClasseCategoria cat = (ClasseCategoria) intentResultado.getSerializableExtra("categoria");
                //denu.setcodcat(cat.getcod);
                categoria.setText(cat.getDesc_categoria());
            }
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && intentResultado != null && intentResultado.getData() != null) {
            Uri filePath = intentResultado.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                //imageView1.setImageBitmap(bitmap);
                //imageView2.setVisibility(View.VISIBLE);
                verificaImagem(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == OPEN_CAMERA_REQUEST && resultCode == RESULT_OK && intentResultado != null && intentResultado.getData() != null) {
            Bundle extras = intentResultado.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //imageView1.setImageBitmap(imageBitmap);
            //imageView2.setVisibility(View.VISIBLE);
            verificaImagem(bitmap);
        }
    }

    public void chamaPermissao() {
        Log.i(TAG, "chamaPermissao()");


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                callDialog("É preciso a permission ACCESS_FINE_LOCATION para apresentação dos clientes.", new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CODE);
            }
        } else {

            showFileChooser();
        }
    }

    public void chamaPermissaoCamera() {
        Log.i(TAG, "chamaPermissaoCamera()");


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                callDialog("É preciso a permission ACCESS_FINE_LOCATION para apresentação dos clientes.", new String[]{Manifest.permission.CAMERA});
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSIONS_CODE);
            }
        } else {

            takePhoto();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.i(TAG, "test");
        switch (requestCode) {
            case REQUEST_PERMISSIONS_CODE:
                for (int i = 0; i < permissions.length; i++) {

                    if (permissions[i].equalsIgnoreCase(Manifest.permission.READ_EXTERNAL_STORAGE)
                            && grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                        showFileChooser();
                    }

                    if (permissions[i].equalsIgnoreCase(Manifest.permission.CAMERA)
                            && grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                        takePhoto();
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void callDialog(String message, final String[] permissions) {
        mMaterialDialog = new MaterialDialog(this)
                .setTitle("Permissão")
                .setMessage(message)
                .setPositiveButton("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ActivityCompat.requestPermissions(DenunciaActivity.this, permissions, REQUEST_PERMISSIONS_CODE);
                        mMaterialDialog.dismiss();
                    }
                })
                .setNegativeButton("Cancelar", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });
        mMaterialDialog.show();
    }

    private void exemplo_simples() {
        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //define o titulo
        builder.setTitle("Imagem");
        //define a mensagem
        builder.setMessage("Deseja excluir essa imagem?");
        //define um botão como positivo
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(DenunciaActivity.this, "sim=" + arg1, Toast.LENGTH_SHORT).show();
            }
        });
        //define um botão como negativo.
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(DenunciaActivity.this, "nao=" + arg1, Toast.LENGTH_SHORT).show();
            }
        });
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe
        alerta.show();
    }
}
