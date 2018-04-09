package com.training.jeremy_pc.mapway;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by jeremy_pc on 01/03/2018.
 */

public class HttpDataHandler {

    public HttpDataHandler(){


    }

    public String GetHTTPData(String requestURL){

        URL url;
        String reponse = "";
        try {
            url = new URL(requestURL);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

            int responseCode = connection.getResponseCode();

            if(responseCode == HttpsURLConnection.HTTP_OK){
                String line;

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = br.readLine()) !=null){
                    reponse+=line;
                }
            }else{
                reponse = "";
            }

        }catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return reponse;
    }
}
