package com.example.matheus.volleyinsertdata;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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
import java.util.Map;

import me.drakeet.materialdialog.MaterialDialog;

public class DenunciaActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText data, categoria;
    private int yy;
    private int mm;
    private int dd;
    private Button buttonChoose;
    private Button buttonCamera;
    private Button buttonUpload;
    private ImageView imageView2;
    private EditText editTextName;
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    private int OPEN_CAMERA_REQUEST = 2;
    private String UPLOAD_URL ="http://192.168.0.5/tcc/ws/volleyDenunciaImg.php";
    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";
    public static final String TAG = "LOG";
    MaterialDialog mMaterialDialog;
    public static final int REQUEST_PERMISSIONS_CODE = 128;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denuncia);

        data = (EditText) findViewById(R.id.data);
        categoria = (EditText) findViewById(R.id.categoria);
        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonCamera = (Button) findViewById(R.id.buttonCamera);
        //buttonUpload = (Button) findViewById(R.id.buttonUpload);
       // editTextName = (EditText) findViewById(R.id.editText);
        imageView2  = (ImageView) findViewById(R.id.imageView2);
        buttonChoose.setOnClickListener(this);
        buttonCamera.setOnClickListener(this);

        //exibe data atual no campo de data
        final Calendar c = Calendar.getInstance();
        yy = c.get(Calendar.YEAR);
        mm = c.get(Calendar.MONTH);
        dd = c.get(Calendar.DAY_OF_MONTH);

        data.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(dd).append("/").append(mm + 1).append("/")
                .append(yy));


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

    private void showFileChooser() {
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


   // @Override
    public void onClick(View v) {

        if(v == buttonChoose){
            //showFileChooser();
            chamaPermissao();

        }

        if(v == buttonCamera){
            chamaPermissaoCamera();
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
                imageView2.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == OPEN_CAMERA_REQUEST && resultCode == RESULT_OK && intentResultado != null && intentResultado.getData() != null) {
            Bundle extras = intentResultado.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView2.setImageBitmap(imageBitmap);
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
}
