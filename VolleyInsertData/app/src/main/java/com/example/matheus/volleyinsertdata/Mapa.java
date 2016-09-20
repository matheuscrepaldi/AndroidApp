package com.example.matheus.volleyinsertdata;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class Mapa extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private LatLngBounds LIMITES;
    private Boolean temInfo = false;
    private double latitudesul, latitudenorte, longitudesul, longitudenorte;
    //ESTA VARIAVEL ARMAZENA O ENDEREÇO DO SEU WEB SERVICES
    //public static final String END_WEBSERVICE = "http://192.168.0.14/tcc/ws/volleyLogin";
    //"http://localhost....."
    //"http://www.seudominio.........."

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /** pega latitude e longidute que são passados como Extras via intent, pela MainActivity */
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);

    }


    /**
     * Método que será acionado quando o mapa estiver carregado e pronto
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        Marker marker;

        mMap = googleMap;

        /** atribui ao mapa os botões de zoom */
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        /** atribui ao mapa todos os gestos (incluindo o ZOOM com movimento de "pinça") */
        googleMap.getUiSettings().setAllGesturesEnabled(true);

        googleMap.setMyLocationEnabled(true);
        /** chamada ao metodo que adiciona um marcador na localização atual, passando TRUE como parametro
         * para mostrar o infowindow e mover a camera para a posição que acaba de ser adicionada
         * se o parametro for FALSE, o metodo apenas adiciona o marcador */
        marcaLocalizacaoAtual(true);
        /** seta o zoom, para a visualização atual */
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

        /** //se fosse preciso manipular distancias, pontoA é a localização atual
         * Location pontoA = new Location("pontoA");
         * pontoA.setLatitude(latitude);
         * pontoA.setLongitude(longitude);
         *
         * //o objeto pontoB é um outro ponto no mapa
         *
         * Location pontoB = new Location("pontoB");
         * pontoB.setLatitude(-21.234638);
         * pontoB.setLongitude(-50.406622);
         *
         * //distance é uma variavel do tipo DOUBLE, que recebe o resultado do metodo
         * //distanceTo, chamado a partir do pontoA, passando o pontoB como parametro
         * //isto resulta um calculo de distancia em linha reta, ideal para manipulação
         * //de resultados em um circulo, a divisão por 1000 é necessaria para o valor
         * //de distance ser equivalente a kilometros, e para imprimir o valor arredondado
         * //poderia se usar o codigo Math.ceil(distance) Exemplo:
         * //Toast toast = Toast.makeText(this, Math.ceil(distance)+"", Toast.LENGTH_SHORT);
         * //toast.show();
         * distance = pontoA.distanceTo(pontoB) / 1000;
         */

        /** //para adicionar um circulo no mapa em formato de raio, instancia-se um objeto
         * //do tipo Circle
         * Circle circulo = mMap.addCircle(new CircleOptions()
         * .center(new LatLng(sualatitude, sualongitude)) //substituir os valores
         * .radius(10000) //raio de 10 KM
         * .strokeColor(Color.RED) //cor da borda
         * .fillColor(Color.TRANSPARENT)); //cor do preenchimento
         */

        /**
         * //para adicionar manualmente um marcador no mapa
         * //chama o metodo addMarker, no seu mapa (mMap) passando as opções do marcador como parametro
         * mMap.addMarker(new MarkerOptions()
         * .position(new LatLng(sualatitude, sualongitude))  //substituir os valores
         * .title("Este é um Marcador")
         * .snippet("Esta é a descrição adicional do Marcador"));
         */

        /** listener que verifica se o infoWindow esta aberto para tratar a requisição ao WebServices */
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                /** se estiver aberto atribui true a variavel temInfo */
                temInfo = true;
                return false;
            }
        });

        /** metodo responsavel por pegar os limites da tela
         * este metodo é acionado assim que o usuario termina de movimentar a tela do mapa */
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                /** verifica se tem infoWindow aberto, se o resultado for true atualiza os marcadores e o limite da tela */
                if (!temInfo) {
                    /** LIMITES pega os limites da tela, chamados de BOUNDS */
                    LIMITES = mMap.getProjection().getVisibleRegion().latLngBounds;
                    //Float mapZoom = mMap.getCameraPosition().zoom;
                    //LatLng latlong = mMap.getCameraPosition().target;

                    /** atribui as variaveis os valores de coordenadas dos limites da tela */
                    latitudenorte = LIMITES.northeast.latitude;
                    latitudesul = LIMITES.southwest.latitude;
                    longitudenorte = LIMITES.northeast.longitude;
                    longitudesul = LIMITES.southwest.longitude;

                    //mapZoom = mMap.getCameraPosition().zoom;

                    /** chama o metodo da lib volley, passando as coordenadas completas da tela como parametros */
                    selecionarTodos(getApplicationContext(), latitudenorte, latitudesul, longitudenorte, longitudesul);

                }

                /** seta a variavel de controle do infoWindow como false, para carregar novos clientes ao mover o mapa */
                temInfo = false;

            }
        });

    }

    /**
     * metodo responsavel por atribuir a localização atual
     * o parametro "primeiravez" significa se é a primeira vez que a função esta sendo chamada, ou seja
     * se o mapa acaba de ser aberto, portanto ao passar "true" o metodo se encarrega de exibir o infowindow e mover
     * a camera para a localização atual do usuário, se "false" apenas adiciona o marcador da localização atual
     * este metodo se faz necessario, pois ao atualizar os dados pelo volley, é preciso limpar todos os marcadores
     */
    public void marcaLocalizacaoAtual(Boolean primeiravez) {

        LatLng locAtual = new LatLng(latitude, longitude);

        Marker marker;
        /** adiciona o marcador */
        marker = mMap.addMarker(new MarkerOptions().position(locAtual).title("Voce está aqui!"));
        marker.showInfoWindow();
        if (primeiravez) {
            /** mostra o infowindow e move a camera para o marcador atual */
            mMap.moveCamera(CameraUpdateFactory.newLatLng(locAtual));
        }

    }








/**
 * metodo volley responsavel por fazer a consulta ao banco de dados
 */
    public void selecionarTodos(final Context vContext, double latN, double latS, double lgtN, double lgtS) {

        /** atribuição dos parametros que serão enviados via POST ao webservices */
        Map<String, String> par = new HashMap<>();
        par.put("consultar", "cliente");
        par.put("acao", "selecionarTodosArea");
        par.put("latN", String.valueOf(latN));
        par.put("latS", String.valueOf(latS));
        par.put("lgtN", String.valueOf(lgtN));
        par.put("lgtS", String.valueOf(lgtS));

        /** criação de um novo request */

        /** adiciona nossa requisição para a fila de requisições */
        //Volley.newRequestQueue(vContext).add(jsonObjReq);

    }


}