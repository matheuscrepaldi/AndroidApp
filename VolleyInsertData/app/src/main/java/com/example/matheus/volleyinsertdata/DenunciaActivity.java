package com.example.matheus.volleyinsertdata;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DenunciaActivity extends AppCompatActivity {

    private EditText data, categoria;
    private int yy;
    private int mm;
    private int dd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denuncia);

        data = (EditText) findViewById(R.id.data);
        categoria = (EditText) findViewById(R.id.categoria);

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
    }
}
