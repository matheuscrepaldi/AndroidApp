package com.example.matheus.volleyinsertdata;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import me.drakeet.materialdialog.MaterialDialog;

public class MenuPrincipal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener {

    private FragmentManager fragmentManager;

    private double latG, lgtG;
    private Mapa mapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_menu_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //resultados do login do facebook
        Bundle inBundle = getIntent().getExtras();
        String name = inBundle.get("name").toString();
        String surname = inBundle.get("surname").toString();
        String imageUrl = inBundle.get("imageUrl").toString();
        String token = inBundle.get("token").toString();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        chamaLocalizacaoAtual();

        mapa = new Mapa();
        mapa.setLatG(latG);
        mapa.setLgtG(lgtG);
        fragmentTransaction.add(R.id.fragmento, mapa, "MapaActivity");
        fragmentTransaction.commitAllowingStateLoss();

        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_menu_principal);

        TextView user_name = (TextView) headerLayout.findViewById(R.id.user_name);
        new DownloadImage((ImageView) headerLayout.findViewById(R.id.imageView)).execute(imageUrl);

        user_name.setText(name + " " + surname);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    public void logout(){
        LoginManager.getInstance().logOut();
        Intent login = new Intent(MenuPrincipal.this, MainActivity.class);
        startActivity(login);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Intent intent = new Intent(this, DenunciaActivity.class);
            Bundle bundle = new Bundle();
            bundle.putDouble("latitude", latG);
            bundle.putDouble("longitude", lgtG);
            intent.putExtras(bundle);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage){

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //=================================AÇÕES DO MAPA==========================================
    MaterialDialog mMaterialDialog;
    public static final String TAG = "LOG";
    public static final int REQUEST_PERMISSIONS_CODE = 128;


    /**
     * metodo responsavel por abrir o mapa, incluindo nos extras a latitude e longitude atual do usuário
     */
    public void abrirMapa() {
        Intent intent = new Intent(this, Mapa.class);
        intent.putExtra("latitude", latG);
        intent.putExtra("longitude", lgtG);
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
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
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
            latG = latitude;
            lgtG = longitude;
            //mapa.marcaLocalizacaoAtual(true);
            //abrirMapa();
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

                        ActivityCompat.requestPermissions(MenuPrincipal.this, permissions, REQUEST_PERMISSIONS_CODE);
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
