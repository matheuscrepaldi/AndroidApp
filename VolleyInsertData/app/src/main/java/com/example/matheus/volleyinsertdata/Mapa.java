package com.example.matheus.volleyinsertdata;

import android.Manifest;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
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
import com.google.android.gms.maps.model.internal.zzf;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mapa extends SupportMapFragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private LatLngBounds LIMITES;
    private Boolean temInfo = false;
    private double latitudesul, latitudenorte, longitudesul, longitudenorte;
    private double latN, latS, lgtN, lgtS;
    private DrawerLayout mDrawerLayout;
    private ListView mListDrawer;
    private ActionBarDrawerToggle mToggle;
    public static final String LOGIN_URL = "http://192.168.0.5/tcc/ws/volleyDenuncia.php";
    //ESTA VARIAVEL ARMAZENA O ENDEREÇO DO SEU WEB SERVICES
    //public static final String END_WEBSERVICE = "http://192.168.0.14/tcc/ws/volleyLogin";
    //"http://localhost....."
    //"http://www.seudominio.........."

    private double latG, lgtG;

    public double getLatG() {
        return latG;
    }

    public void setLatG(double latG) {
        this.latG = latG;
    }

    public double getLgtG() {
        return lgtG;
    }

    public void setLgtG(double lgtG) {
        this.lgtG = lgtG;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_mapa);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        getMapAsync(this);

        /** pega latitude e longidute que são passados como Extras via intent, pela MainActivity */
        Intent intent = getActivity().getIntent();
        latitude = getLatG();
        longitude = getLgtG();

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

        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
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
                    Float mapZoom = mMap.getCameraPosition().zoom;
                    LatLng latlong = mMap.getCameraPosition().target;

                    /** atribui as variaveis os valores de coordenadas dos limites da tela */
                    latitudenorte = LIMITES.northeast.latitude;
                    latitudesul = LIMITES.southwest.latitude;
                    longitudenorte = LIMITES.northeast.longitude;
                    longitudesul = LIMITES.southwest.longitude;

                    mapZoom = mMap.getCameraPosition().zoom;

                    /** chama o metodo da lib volley, passando as coordenadas completas da tela como parametros */
                    selecionarTodos(getActivity().getApplicationContext());

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
    public void selecionarTodos(final Context vContext) {

        /** atribuição dos parametros que serão enviados via POST ao webservices */
        Map<String, String> par = new HashMap<>();

        par.put("latN", String.valueOf(latitudenorte));
        par.put("latS", String.valueOf(latitudesul));
        par.put("lgtN", String.valueOf(longitudenorte));
        par.put("lgtS", String.valueOf(longitudesul));

        /** criação de um novo request */
        CustomRequest jsonObjReq = new CustomRequest(Request.Method.POST,
                LOGIN_URL, par,
                new Response.Listener<JSONArray>() {
                    /** dentro do metodo on response, é que podemos tratar o codigo que deve ser executano, em nosso caso,
                     * estamos limpando os marcadores do mapa, adicionando o marcador na localização atual e adicionando marcadores
                     * referentes a cada registro do banco de dados*/
                    @Override
                    public void onResponse(JSONArray response) {
                        /** limpa os marcadores atuais*/
                        mMap.clear();
                        /** adiciona marcador na localização atual */
                        marcaLocalizacaoAtual(false);
                        /** laço que executa a quantidade de objetos respondidos no Json, pelo web service */
                        for(int i=0;i<response.length();i++) {
                            JSONObject e = new JSONObject();
                            try {
                                e = response.getJSONObject(i);
                                /** o objeto "e" agora é um objeto retornado de nosso web serives, nas linhas abaixo
                                 * é utilizado e.getDouble("campo"), mas poderia ser e.getString, getInt, de acordo com o valor
                                 * esperado, e o campo nada mais é que o indice do nosso Json. */
                                LatLng localizacaoCli = new LatLng(e.getDouble("latitude"), e.getDouble("longitude"));
                                mMap.addMarker(new MarkerOptions().position(localizacaoCli)

                                        .title("este é o titulo")//.title(e.getString("este é o titulo"))
                                        .snippet(e.getString("rua_den") + ", " + e.getString("num_den")));

                            } catch (JSONException e1) {

                            }
                        }
                    }
                }, new Response.ErrorListener() {

            /** se o metodo onResponse nao consegue ser executado, seja por falha de rede, alguma sintaxe errada
             * nossa requisição é direcionada ao metodo onErrorResponse, aqui podemos gerar um Log, ou até mesmo
             * tratar as exceptions */
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("", "Error: " + error.getMessage());
            }
        });

        /** adiciona nossa requisição para a fila de requisições */
        Volley.newRequestQueue(vContext).add(jsonObjReq);

    }
}