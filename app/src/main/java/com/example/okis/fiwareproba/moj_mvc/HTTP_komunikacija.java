package com.example.okis.fiwareproba.moj_mvc;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class HTTP_komunikacija {

    private InputStream OpenHttpConnection(String urlString) throws IOException {
        InputStream in = null;
        int response = -1;

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");
        try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");

            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        }
        catch (Exception ex)
        {
            Log.d("Networking", ex.getLocalizedMessage());
            throw new IOException("Error connecting");
        }
        return in;
    }
    //---------------METOD ZA PREUZIMANJE SADRZAJA SA SERVERA ---------------------------------
    public String getMethod(String URL)
    {
        int BUFFER_SIZE = 2000;
        InputStream in = null;
        try {
            in = OpenHttpConnection(URL);
        } catch (IOException e) {
            return "";
        }

        InputStreamReader isr = new InputStreamReader(in);
        int charRead;
        String str = "";
        char[] inputBuffer = new char[BUFFER_SIZE];
        try {
            while ((charRead = isr.read(inputBuffer))>0) {
                //---convert the chars to a String---
                String readString =
                        String.copyValueOf(inputBuffer, 0, charRead);
                str += readString;
                inputBuffer = new char[BUFFER_SIZE];
            }
            in.close();
        } catch (IOException e) {
            return "";
        }

        return str;
    }
    //---------------END METOD ZA PREUZIMANJE SADRZAJA SA SERVERA ---------------------------------

    //---------------METOD ZA SLANJE SADRZAJA NA SERVER---------------------------------

    private String _prepere_OUT_requst(String json_za_slanje, String url_za_slanje,int response_code, String metod){
        JSONObject postData=null;
        try {
            postData=new JSONObject(json_za_slanje);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            URL url = new URL(url_za_slanje);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(metod);

            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            // Send the post body
            if (postData != null) {
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(postData.toString());
                writer.flush();
            }

            int statusCode = urlConnection.getResponseCode();

            if (statusCode !=  response_code) {
                return  "Greska vracen statusCode:"+statusCode;
            }

        } catch (Exception e) {
            Log.d("TAG-GERSKA", e.getLocalizedMessage());
        }
        return null;
    }

    //--------------- POST METOD ZA SLANJE SADRZAJA NA SERVER---------------------------------
    public String postMetod( String json_za_slanje, String url_za_slanje,int response_code){
        return _prepere_OUT_requst(json_za_slanje, url_za_slanje,response_code, "POST");
    }
    //--------------- END POST METOD ZA SLANJE SADRZAJA NA SERVER---------------------------------

    //--------------- PUT METOD ZA SLANJE SADRZAJA NA SERVER ---------------------------------
    public String putMetod( String json_za_slanje, String url_za_slanje,int response_code){
        return _prepere_OUT_requst(json_za_slanje, url_za_slanje,response_code, "PUT");
    }
    //--------------- END PUT METOD ZA SLANJE SADRZAJA NA SERVER---------------------------------
}
