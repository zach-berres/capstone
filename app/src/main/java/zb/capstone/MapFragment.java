package zb.capstone;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap map;
    String fromMainLat;
    String fromMainLng;
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
        fromMainLat = getArguments().getString("mylat");
        fromMainLng = getArguments().getString("mylng");
        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Log.i("zach", "map ready");
        map = googleMap;

        //LatLng depere = new LatLng(44.444117, -88.066470);
        Log.i("gps", "newlatitude = " + fromMainLat + "; newlongitude = " + fromMainLng );
        LatLng myloc = new LatLng(Double.parseDouble(fromMainLat), Double.parseDouble(fromMainLng));
        MarkerOptions option = new MarkerOptions();
        option.position(myloc).title("DePere");
        map.addMarker(option);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myloc, 18.0f));

    }
}
