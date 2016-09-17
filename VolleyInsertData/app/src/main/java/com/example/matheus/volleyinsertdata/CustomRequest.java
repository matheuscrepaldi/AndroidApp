package com.example.matheus.volleyinsertdata;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by Vainer on 17/09/2016.
 */
    public class CustomRequest extends Request<JSONArray> {

        private Response.Listener<JSONArray> listener;
        private Map<String, String> params;

        public CustomRequest(String url, Map<String, String> params, Response.Listener<JSONArray> responseListener, Response.ErrorListener errorListener) {
            super(Request.Method.GET, url, errorListener);
            this.listener = responseListener;
            this.params = params;
        }

        public CustomRequest(int method, String url, Map<String, String> params, Response.Listener<JSONArray> reponseListener, Response.ErrorListener errorListener) {
            super(method, url, errorListener);
            this.listener = reponseListener;

            this.params = params;
        }


        @Override
        protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
            return params;
        }

        @Override
        protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
            try {
                String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

                return Response.success(new JSONArray(jsonString), HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            } catch (JSONException je) {
                return Response.error(new ParseError(je));
            }
        }

        @Override
        protected void deliverResponse(JSONArray response) {
            listener.onResponse(response);
        }
    }

