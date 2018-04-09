package zb.capstone;

import android.Manifest;
import android.accounts.Account;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private final static int REQUEST_CODE = 100;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    private String URL_UPDATE_LOCATIONS = "compsci02.snc.edu/cs460/2018/berrzg/project_files/update_locations.php";
    private String URL_TEST = "http://compsci02.snc.edu/cs460/2018/berrzg/project_files/test.txt";
    private String URL_G = "https://www.google.com";
    private GoogleApiClient gac;//this is our api client
    private Location myloc;
    private FusedLocationProviderClient gpsflc;
    private double mylat;
    private double mylng;
    private Account myaccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("whereami", "in on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        gpsflc = LocationServices.getFusedLocationProviderClient(this);

        //Update Location Button
        FloatingActionButton fab = findViewById(R.id.fab_locate);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onConnected(Bundle.EMPTY);
                new sendData().execute(Double.toString(mylat), Double.toString(mylng));
                //new addNewLocation().execute(ADD PARAMETERS); //starts async thread params: userid, username, lat, lng
            }
        });

        //Activity Drawer: contains menu items
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Moved this to a separate function, keep things neat and tidy in here
        GooglePlayServiceBuilder();
        new receiveData().execute(URL_TEST);
        //GetAccount();
    }

    @Override
    protected void onStart() {
        Log.i("whereami", "in on start");
        super.onStart();
        if (gac != null) {
            gac.connect(); //attempt to establish connection. if success, check onConnected
        }
    }

    //somehow get location updates going again
    @Override
    protected void onResume() {
        Log.i("whereami", "in on resume");
        super.onResume();
        onConnected(Bundle.EMPTY);
    }

    protected void onPause() {
        Log.i("whereami", "in on pause");
        super.onPause();
        FusedLocationProviderApi flpa = LocationServices.FusedLocationApi;
        flpa.removeLocationUpdates(gac, this);
    }

    protected void onStop() {
        Log.i("whereami", "in on stop");
        super.onStop();
    }

    protected void onRestart() {
        Log.i("whereami", "in on restart");
        super.onRestart();
    }

    protected void onDestroy() {
        Log.i("whereami", "in on destroy");
        super.onDestroy();
    }

    //Everytime we exceed the minimum threshold for distance travelled, our function will be called
    public void onLocationChanged(Location location) {
        float accuracy = location.getAccuracy();
        Log.i("location_changed", "accuracy " + accuracy);
        mylat = location.getLatitude();
        mylng = location.getLongitude();
        Log.i("location_changed", "latitude = " + mylat + "; longitude = " + mylng);
        makeBundle();
        //send coordinates to php file to handle
        //new sendData().execute(Double.toString(mylat), Double.toString(mylng));
    }

    //uses AccountManager API to grab user's Google Account data
    protected void GetAccount() {
        Log.i("whereami", "in get account");
        //AccountManager am = AccountManager.get(this);
        //Account[] accounts = am.getAccountsByType("com.google");
        //dialog if multiple accounts
        //choose account
        //parse account name as string for username
        //call User constructor?
        //Log.i("whataccount", Integer.toString(accounts.length));

        ////////////////////////////Google Sign-In////////////////////////////////////////////////////
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        //GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        //Google Sign In Client
    }

    protected void GooglePlayServiceBuilder() {
        //Create Google API Client builder
        GoogleApiClient.Builder gpsbldr = new GoogleApiClient.Builder(this);
        gpsbldr.addConnectionCallbacks(this);
        gpsbldr.addOnConnectionFailedListener(this);
        gpsbldr.addApi(LocationServices.API);
        gac = gpsbldr.build();
    }

    public void makeBundle() {
        Bundle mycoordsbundle = new Bundle();
        mycoordsbundle.putString("mylat", String.valueOf(mylat));
        mycoordsbundle.putString("mylng", String.valueOf(mylng));
        MapFragment mapFragment = new MapFragment();
        mapFragment.setArguments(mycoordsbundle);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.mainLayout, mapFragment).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_groups) {
            // Handle the camera action
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_chat) {

        } else if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("gps", "connected");
        FusedLocationProviderApi flpa = LocationServices.FusedLocationApi;
        LocationRequest request = new LocationRequest();
        request.setInterval(30000); //30 seconds, arbitrarily chosen but should allow for significant distance travelled between requests.
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); //highest available accuracy
        request.setSmallestDisplacement(20);//20meters for testing, minimum distance travelled before checking update, even if more than 30 seconds

        //again, request and obtain permission if not first had
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        }
        if (gac.isConnected())//in case connection was lost when app was in background
            flpa.requestLocationUpdates(gac, request, this);
        else
            gac.connect();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("gps", "connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i("gps", "connection failed");
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(this, REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                Log.i("gps", "problem with google play services");
                Toast.makeText(this, "Problem with Google Play services, exiting", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            gac.connect(); //problem from onConnectionFailed resolved (GooglePlayServices?), try to connect again
        }
    }

    public class receiveData extends AsyncTask<String, String, String>
    {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader br = null;

            try{
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream is = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));

                String line = "";
                while((line = br.readLine()) != null)
                    return line;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally{
                if (connection != null)
                    connection.disconnect();
                try{
                    if (br != null)
                        br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return "error receiving data";
        }

        @Override
        protected void onPostExecute(String line) {
            super.onPostExecute(line);
            if(!Objects.equals(line, "error receiving data"))
            {
                String result = line;
                Log.i("testconnect", result);
            }
        }
    }

    public class sendData extends AsyncTask<String, String, String>
    {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            //String php = URL_UPDATE_LOCATIONS + "?lat="+params[0]+"&lng="+params[1];
            String php = "http://compsci02.snc.edu/cs460/2018/berrzg/project_files/update_locations.php?lat="+params[0]+"&lng="+params[1];
            Log.i("testSend", php);
            try{
                URL url = new URL(php);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                InputStream is = connection.getInputStream();
                return "successfully sent and wrote data";
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally{
                if(connection != null)
                    connection.disconnect();
            }
            return "error sending data to php";
        }

        @Override
        protected  void onPostExecute(String s)
        {
            super.onPostExecute(s);
        }
    }
}
