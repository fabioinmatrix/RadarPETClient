package com.example.radarpetclient.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.radarpetclient.model.Registro;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RegistroHttp {
    public static final String URL_JSON = "https://k2drones.com.br/JSON/register.json";

    private static HttpURLConnection connectar(String urlFile) throws IOException {
        final int SECONDS = 1000;
        URL url = new URL(urlFile);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(10 * SECONDS);
        connection.setConnectTimeout(15 * SECONDS);
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.setDoOutput(false);
        connection.connect();
        return connection;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    public static List<Registro> loadRegistrosJson() {
        try {
            HttpURLConnection connection = connectar(URL_JSON);
            int response = connection.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                JSONObject json = new JSONObject(bytesToString(inputStream));
                return readJsonRegistros(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Registro> readJsonRegistros(JSONObject json) throws JSONException {
        List<Registro> listaDeRegistros = new ArrayList<Registro>();

        try {
            String categoriaAtual;
            JSONArray jsonPetfinder = json.getJSONArray("petfinder");
            for (int i = 0; i < jsonPetfinder.length(); i++){
                JSONObject jsonCategoria = jsonPetfinder.getJSONObject(i);
                categoriaAtual = jsonCategoria.getString("category");

                JSONArray jsonRegistros = jsonCategoria.getJSONArray("register");
                for (int j = 0; j < jsonRegistros.length(); j++){
                    JSONObject jsonRegistro = jsonRegistros.getJSONObject(j);

                    Registro registros = new Registro(
                            jsonRegistro.getString("imei"),
                            jsonRegistro.getString("timeline"),
                            jsonRegistro.getString("latitude"),
                            jsonRegistro.getString("longitude"),
                            jsonRegistro.getString("uuid"));
                    listaDeRegistros.add(registros);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listaDeRegistros;
    }

    private static String bytesToString(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream bigBuffer = new ByteArrayOutputStream();
        int byteRead;
        while ((byteRead = inputStream.read(buffer)) != -1) {
            bigBuffer.write(buffer, 0, byteRead);
        }
        return new String(bigBuffer.toByteArray(), "UTF-8");
    }
}
