package zb.capstone;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap map;
    String fromMainLat;
    String fromMainLng;
    String userCoords[] = new String[10]; //do I need to define a size here or can i initialize as just userCoords[]?
    private String URL_TEST = "http://compsci02.snc.edu/cs460/2018/berrzg/project_files/test.txt";
    //private Callbacks zCallbacks;

    public MapFragment() {
        //Log.i("zach", "in public constructor");
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment)this.getChildFragmentManager().findFragmentById(R.id.zmap);
        mapFragment.getMapAsync(this);
        //fromMainLat = getArguments().getString("mylat");
        //fromMainLng = getArguments().getString("mylng");
        userCoords = getArguments().getStringArray("coords");
        Log.i("coordsfrommain", userCoords[0]);
        String user[];
        user = userCoords[0].split(";");
        fromMainLat = user[1];
        fromMainLng = user[2];
        //new receiveData().execute(URL_TEST);
        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Log.i("zach", "map ready");
        map = googleMap;

        //LatLng depere = new LatLng(44.444117, -88.066470);
        Log.i("gps", "newlatitude = " + fromMainLat + "; newlongitude = " + fromMainLng );

        LatLng myloc = new LatLng(Double.parseDouble(fromMainLat), Double.parseDouble(fromMainLng));
        MarkerOptions option = new MarkerOptions()
                .position(myloc)
                .title(userCoords[0]);
        map.addMarker(option);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myloc, 16));
        //map.animateCamera(CameraUpdateFactory.zoomTo((11)));
        splitUserData(userCoords);

    }

    public void splitUserData(String[] sa)
    {
        String user[];
        int i = 1;
        while((i < sa.length) && (sa[i] != ""))
        {
            user = sa[i].split(";");
            if(Objects.equals(user[3], "0"))//they are NOT incognito
            {
                double currentLatitude = Double.parseDouble(user[1]);
                double currentLongitude = Double.parseDouble(user[2]);

                Log.i("testconnectfrag", currentLatitude + " | " + currentLongitude);

                    LatLng latLng = new LatLng(currentLatitude, currentLongitude);

                    MarkerOptions options = new MarkerOptions()
                            .position(latLng)
                            .title(user[0])
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                map.addMarker(options);
            }
            else
            {
                //Toast.makeText(getActivity(),"User is Incognito!",Toast.LENGTH_LONG).show();
                Log.i("ncog", "User " + user[0] + " is incognito!");
            }
            i++;
        }

        return;
    }
}
