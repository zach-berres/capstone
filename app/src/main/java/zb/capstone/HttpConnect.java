//Class HttpConnect modified with permission from adanware.blogspot.com

package zb.capstone;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

public class HttpConnect {

    private URLConnection urlConnection;
    private URL myUrl;
    private String htmlData;

    public boolean connect(String url) throws IOException
    {
        Log.i("testconnect", "in HTTP connect");
        myUrl = new URL(url);
        Log.i("testconnect", url);
        urlConnection = myUrl.openConnection();
        htmlData = convertStreamToString(urlConnection.getInputStream());
        return true;
    }

    public String accessHtmlData(){
        Log.i("testconnect", "in HTTP access");
        if(htmlData != "")
            return htmlData;
        else
            return "ERROR GETTING DATA";
    }


    private String convertStreamToString(InputStream is) {
        Log.i("connect", "in HTTP convert");

        if (is != null) {
            BufferedReader reader;
            StringBuilder data = new StringBuilder();
            try
            {
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                {
                    data.append(inputLine + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally
            {

                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return data.toString();
        } else {
            return "";
        }
    }
}
