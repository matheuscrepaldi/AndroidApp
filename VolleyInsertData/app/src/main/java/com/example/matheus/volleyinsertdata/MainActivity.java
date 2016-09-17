package com.example.matheus.volleyinsertdata;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.appevents.internal.Constants;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import me.drakeet.materialdialog.MaterialDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String LOGIN_URL = "http://192.168.0.14/tcc/ws/volleyLogin.php";

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textView;
    private LoginButton loginButton; //botao do facebook
    private CallbackManager callbackManager; //callback do facebook
    private TextView info;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_main);

        loginButton = (LoginButton) findViewById(R.id.login_button); //pega botao do facebook
        info = (TextView) findViewById(R.id.info);

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        buttonLogin = (Button) findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(this);

        textView = (TextView) findViewById(R.id.linkRegistrar);
        textView.setOnClickListener(this);
    }

    /*public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actionbar, menu);
        return true;
    }*/


    private void userLogin() {
        username = editTextUsername.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("Login efetuado")) {
                            openProfile();
                        } else {
                            Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put(KEY_USERNAME, username);
                map.put(KEY_PASSWORD, password);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void openProfile() {

        //intent.putExtra(KEY_USERNAME, username);
        //Intent intent = new Intent(this, Mapa.class);
        //startActivity(intent);
        chamaLocalizacaoAtual();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {

        //se clicar para logar
        if (v == buttonLogin) {
            userLogin();
        }

        //se clicar para registrar usuario
        if (v == textView) {
            Intent intent = new Intent(this, RegisterActivity.class);
            //intent.putExtra(KEY_USERNAME, username);
            startActivity(intent);

        }

        //se clicar no botao do facebook
        if (v == loginButton) {

            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {


                    info.setText(
                            "User ID: "
                                    + loginResult.getAccessToken().getUserId()
                                    + "\n" +
                                    "Auth Token: "
                                    + loginResult.getAccessToken().getToken()
                    );

                }

                @Override
                public void onCancel() {

                    info.setText("Login attempt canceled.");
                }

                @Override
                public void onError(FacebookException e) {

                    info.setText("Login attempt failed.");
                }
            });
        }
    }

    //=================================AÇÕES DO MAPA==========================================
    MaterialDialog mMaterialDialog;
    public static final String TAG = "LOG";
    public static final int REQUEST_PERMISSIONS_CODE = 128;


    /**
     * metodo responsavel por abrir o mapa, incluindo nos extras a latitude e longitude atual do usuário
     */
    public void abrirMapa(double latitude, double longitude) {
        Intent intent = new Intent(this, Mapa.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        startActivity(intent);
    }

    /**
     * metodo responsavel por testar a permissão e exibir aviso de permissão
     * e se o usuario autorizar, chama o metodo getMinhasCoordenadasAtuais() que pega
     * latitude e longitude atual do usuário
     */
    public void chamaLocalizacaoAtual() {
        Log.i(TAG, "chamaLocalizacaoAtual()");

        /** verificaa se tem permissão concedida ao FINE_LOCATION */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            /** se ja pediu autorização uma vez, mostra um alerta explicando porque se faz necessaria esta permissão
             * e pede novamente se podemos acessar o FINE_LOCATION*/
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                callDialog("É preciso a permission ACCESS_FINE_LOCATION para apresentação dos clientes.", new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
            } else {
                /** se ainda nao perguntou nenhuma vez, pede ao usuário se temos permissão para acessar FINE_LOCATION */
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_CODE);
            }
        } else {
            /** caso ja tenhamos permissão, vai direto para o metodo que pega as coordenadas atuais */
            getMinhasCoordenadasAtuais();
        }
    }

    /**
     * metodo responsavel por chamar o getMinhasCoordenadasAtuais() caso o usuário permitir acesso a localização
     * quando executamos o metodo chamaLocalizacaoAtual(), quando exibimos o pedido de permissão, e aceitamos (ou não)
     * o metodo onRequestPermissionsResult é executado, e dentro dele verificamos se a permissão
     * concedida é ao FINE_LOCATION, e se sim, chamamos o metodo getMinhasCoordenadasAtuais()
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.i(TAG, "test");
        switch (requestCode) {
            case REQUEST_PERMISSIONS_CODE:
                for (int i = 0; i < permissions.length; i++) {

                    if (permissions[i].equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION)
                            && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        /** se a permissão concedida é FINE_LOCATION, chama o metodo responsavel */
                        getMinhasCoordenadasAtuais();
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * metodo responsavel por pegar as coordenadas atuais do usuário
     */
    private void getMinhasCoordenadasAtuais() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Location location = null;
        double latitude = 0;
        double longitude = 0;

        /** verifica se possui conexão com internet ou o gps está ligado
         * se nenhum dos dois tiver, não abre o mapa, neste ponto é possivel
         * e recomendado mostrar um aviso ao usuário, sobre a necessidade
         * de estar com internet ou GPS ligado, para saber a localização*/
        if (!isGPSEnabled && !isNetworkEnabled) {
            Log.i(TAG, "Não existem fontes de dados para pegar a localização.");
        } else {
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 2000, 0, this);
                Log.d(TAG, "Localização pera Internet");
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }

            if (isGPSEnabled) {
                if (location == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, (LocationListener) this);
                    Log.d(TAG, "Localização pelo GPS");
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }

            /** depois de pegar a localização, chama o metodo abrirMapa, pasasndo a latitude e longitude que acabamos de obter*/
            //aqui
            Log.i(TAG, "Lat: " + latitude + " | Long: " + longitude);
            abrirMapa(latitude, longitude);
        }

    }


    public void onLocationChanged(Location location) {
    }


    public void onStatusChanged(String provider, int status, Bundle extras) {
    }


    public void onProviderEnabled(String provider) {
    }


    public void onProviderDisabled(String provider) {
    }

    /**
     * metodo responsavel por chamar a mensagem de aviso sobre a permissão
     * recebendo como parametro o conteudo, e um array de permissões
     */
    private void callDialog(String message, final String[] permissions) {
        mMaterialDialog = new MaterialDialog(this)
                .setTitle("Permissão")
                .setMessage(message)
                .setPositiveButton("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ActivityCompat.requestPermissions(MainActivity.this, permissions, REQUEST_PERMISSIONS_CODE);
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
    //===============================FIM MAPA ================================================
}
