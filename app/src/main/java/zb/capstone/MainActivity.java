package zb.capstone;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{
    private final static int REQUEST_CODE = 100;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    private GoogleApiClient gac;//this is our api client
    private Location myloc;
    private FusedLocationProviderClient gpsflc;
    private double mylat;
    private double mylng;

    //Everytime we exceed the minimum threshold for distance travelled, our function will be called
    public void onLocationChanged(Location location )
    {
        float accuracy = location.getAccuracy();
        Log.w("location_changed","accuracy " + accuracy);
        mylat = location.getLatitude();
        mylng = location.getLongitude();
        Log.i("location_changed", "latitude = " + mylat + "; longitude = " + mylng );
        makeBundle();
    }

    protected void onPause()
    {
        super.onPause();
        FusedLocationProviderApi flpa = LocationServices.FusedLocationApi;
        flpa.removeLocationUpdates(gac, this);
        Log.i("onPause", "in on pause");
    }

    //somehow get location updates going again
    @Override
    protected void onResume() {

        super.onResume();
        Log.i("onresume", "in on resume");
        //call onConnect
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("oncreate", "in on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        gpsflc = LocationServices.getFusedLocationProviderClient(this);

        FloatingActionButton fab = findViewById(R.id.fab_locate);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onConnected(Bundle.EMPTY);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Moved this to a separate function, keep things neat and tidy in here
        GooglePlayServiceBuilder();
        GetAccount();

    }

    //uses AccountManager API to grab user's Google Account data
    protected void GetAccount()
    {
        AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccountsByType("com.google");
        //dialog if multiple accounts
            //choose account
            //parse account name as string for username
            //call User constructor?
    }

    protected void GooglePlayServiceBuilder()
    {
        //Create Google API Client builder
        GoogleApiClient.Builder gpsbldr = new GoogleApiClient.Builder(this);
        gpsbldr.addConnectionCallbacks(this);
        gpsbldr.addOnConnectionFailedListener(this);
        gpsbldr.addApi(LocationServices.API);
        gac = gpsbldr.build();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if(gac != null)
        {
            gac.connect(); //attempt to establish connection. if success, check onConnected
        }
    }

    public void makeBundle()
    {
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
                != PackageManager.PERMISSION_GRANTED)
        {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        }
        if(gac.isConnected())//in case connection was lost when app was in background
            flpa.requestLocationUpdates(gac, request, this);
        else
            gac.connect();

        //displayLocation(); //uses getLastLocation to get gps coordinates
        //writeToFile(String.valueOf(0), String.valueOf(mylat), String.valueOf(mylng));
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("gps", "connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i("gps", "connection failed");
        if (result.hasResolution())
        {
            try
            {
                result.startResolutionForResult(this, REQUEST_CODE);
            } catch (IntentSender.SendIntentException e)
            {
                Log.i("gps", "problem with google play services");
                Toast.makeText(this, "Problem with Google Play services, exiting", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            gac.connect(); //problem from onConnectionFailed resolved (GooglePlayServices?), try to connect again
        }
    }

    //This will be reutilized as the SendDataToServer function, still researching/testing on other machine
    private void writeToFile(String usrId, String latText, String lngTxt)
    {
        //try
        {
            //FileOutputStream fout = new FileOutputStream("");
            //byte stream[]=appendText.getBytes();
            //fout.write(stream);
            //fout.close();
            //String appendText = usrId + "/n" + latText + "/n" +lngTxt;
            //String appendText = "test";
            //File outfile = new File("output.txt");

            //if(!outfile.exists())
            //{
            //    outfile.createNewFile();
            //}

            //FileWriter fw = new FileWriter(outfile, true);
            //BufferedWriter bw = new BufferedWriter(fw);
            //bw.write(appendText);
            //bw.close();

        }
        //catch (IOException e)
        {
            //Log.i("file", "unable to write to file");
            //e.printStackTrace();
            //Toast.makeText(this, "Problem writing to file", Toast.LENGTH_LONG).show();
        }
    }
}
