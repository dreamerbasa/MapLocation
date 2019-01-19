package com.google.maplocation;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadUrl {

    public String readUrl(String strurl) throws IOException{

        String data = "";
        InputStream is = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strurl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            //connecting to url
            urlConnection.connect();

            //reading data from url
            is = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            StringBuilder sb = new StringBuilder();
            String line = "";

            while ((line = br.readLine())!=null){

                sb.append(line);

            }

            data = sb.toString();
            Log.d("DownloadUrl",data);
            br.close();

        }catch (Exception e){
            Log.d("Exception",e.toString());
        }
        finally {
            is.close();
            urlConnection.disconnect();
        }
        return data;
    }

}
