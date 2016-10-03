package com.orocab.customer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MapActivity_cstmr extends AppCompatActivity implements OnMapReadyCallback, OnMapLoadedCallback,
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, OnCameraChangeListener,
        com.google.android.gms.location.LocationListener,ResultCallback<LocationSettingsResult>,
        GoogleMap.OnMyLocationChangeListener
{

    private GoogleMap mMap;
    private GoogleMap mGoogleMap;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location getmLastLocation;
    protected LocationSettingsRequest mLocationSettingsRequest;

    Location mLastLocation;
    Marker mCurrLocationMarker;
    Context context;
    Double Lat,Long;

    String myJson;
    boolean isMapReady = false;
    boolean isDataLoaded = false;

    protected static final String TAG = "MainActivity";

    /**
     * Constant used in the location settings dialog.
     */
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    protected final static String KEY_LOCATION = "location";
    protected final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";
    protected Boolean mRequestingLocationUpdates;
    protected String mLastUpdateTime;

    Location location = null;
    LocationManager locationManager;
    LocationListener locationListener;
    ConnectionDetector cd;
    String Url,IPAddress,APIKEY;

    private static final String TAG_Lat = "latitude";
    private static final String TAG_Long = "longitude";
    private static final String TAG_minu = "minute";
    private static final String TAG_Vehicle = "vehicleId";
    private static final String TAG_templateId = "templateId";
    private static final String TAG_templateName = "templateName";

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String uid = "uId";
    String Usersessionid;
    ArrayList<String> Tempstring = new ArrayList<String>();
    ArrayList<String> TempId = new ArrayList<String>();
    ArrayList<String> selectedStrings = new ArrayList<String>();

    HashMap<String, String> map_name_value = new HashMap<String, String>();


    Toolbar toolbar;

    Place place_pick, place_drop;
    TextView DriverName,Vehiclename,Vehiclenumber,DriverNumber,Cancelnumber;
    LinearLayout Calldriver,CancelTrip,DriverLayout,Bookinglayout;
    ProgressDialog progressDialog;
    ProgressDialog progressDialog1 = null;
    String Drivername,DriverMobile,vehicleno,vehiclename,Crno,Bookingid;
    SharedPreferences sharedpreferences;
    View mapView;
    SharedPreferences.Editor editor;
    RadioButton radiobut;
    Button Cancel,Cancelnot;
    LinearLayout Sharemyride;
    EditText shareMobileno;
    Button shareSubmit;
    ImageButton Closeshareride;
    public static FloatingActionButton FAB;

    String vehicleId;
    public static boolean Tripstatus=false;


  //  [{"templateId":1,"comSubCatId":4,"templateName":" Cab is late","status":1}

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_cstmr);

        if (Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }



        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back_arrow));

        if (getSupportActionBar() != null)
        {
            toolbar.setNavigationOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onBackPressed();
                }
            });
        }



        FAB = (FloatingActionButton)findViewById(R.id.sosbutton);
        FAB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapActivity_cstmr.this);
                alertDialog.setTitle("SOS Enable");
                alertDialog.setMessage("Are you Sure want to use SOS feature");
                //alertDialog.setIcon(R.drawable.);
                alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        sosbuttonenabled();
                    }
                });

                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });


        DriverName = (TextView) findViewById(R.id.Drivername);
        Vehiclename = (TextView) findViewById(R.id.vehiclename);
        Vehiclenumber = (TextView) findViewById(R.id.Vehiclenumber);
        DriverNumber = (TextView) findViewById(R.id.getdrivernumber);
        Cancelnumber = (TextView) findViewById(R.id.getCrno);
        DriverLayout = (LinearLayout) findViewById(R.id.driverlayoutdetails);
        Bookinglayout = (LinearLayout) findViewById(R.id.BookingLayout);
        Calldriver = (LinearLayout) findViewById(R.id.calldriver);
        CancelTrip = (LinearLayout) findViewById(R.id.Cancel);

        Sharemyride = (LinearLayout)findViewById(R.id.sharedirection);
        Sharemyride.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LayoutInflater layoutInflater = (LayoutInflater) MapActivity_cstmr.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //ERRORS HERE!!
                View popupView = layoutInflater.inflate(R.layout.sharedir, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                shareMobileno = (EditText)popupView.findViewById(R.id.sharemob);
                shareSubmit = (Button)popupView.findViewById(R.id.Subshareride);
                Closeshareride = (ImageButton)popupView.findViewById(R.id.Closeride);
                Closeshareride.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        popupWindow.dismiss();
                    }
                });
                shareSubmit.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        popupWindow.dismiss();
                        progressDialog1 = ProgressDialog.show(MapActivity_cstmr.this, "", "Please wait...", true);
                        new Thread(new Runnable()
                        {
                            public void run()
                            {
                                shareride();
                            }
                        }).start();
                    }
                });

                popupWindow.setFocusable(true);
                popupWindow.update();
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
            }
        });
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

       // Log.i("oncreate","oncreate");
       /* Drivername = getIntent().getStringExtra("Drivername");
        DriverMobile = getIntent().getStringExtra("DriverMobile");
        vehicleno = getIntent().getStringExtra("vehicleno");
        vehiclename = getIntent().getStringExtra("vehiclename");
        Crno = getIntent().getStringExtra("Crno");*/

        String intent_value = getIntent().getStringExtra("intent");
        Bookingid = getIntent().getStringExtra("BookingId");

        if(Bookingid != null)
        {
            editor = sharedpreferences.edit();
            editor.putString("BookingId", Bookingid);
            editor.apply();
        }
       // Log.i("Bookingid",Bookingid);


        SharedPreferences shared_BookId = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        Bookingid = (shared_BookId.getString("BookingId", ""));
        Log.i("Bookingidshared", Bookingid);

        Calldriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mobnumber = "tel:" + DriverNumber.getText().toString();
                Uri number = Uri.parse(mobnumber);
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
            }
        });

        CancelTrip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Tripstatus = false;
                progressDialog = ProgressDialog.show(MapActivity_cstmr.this, "", "Cancelling trip ..please wait", true);
                new Thread(new Runnable()
                {
                    public void run()
                    {
                       // Tripcancellation();
                        getcancelpopup();
                    }
                }).start();
            }
        });



        Log.i("Tripstatus",""+Tripstatus);
        if(Tripstatus)
        {
            FAB.setVisibility(View.VISIBLE);
            CancelTrip.setVisibility(View.INVISIBLE);
        }

        cd = new ConnectionDetector(getApplicationContext());
        Url = cd.geturl();
        IPAddress = cd.getLocalIpAddress();
        APIKEY = cd.getAPIKEY();
        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        Usersessionid = (shared.getString(uid, ""));
        Log.i("Usersessionid", Usersessionid);

        if(Bookingid!=null)
        {
            Log.i("Bookingid",Bookingid);
            driverdetails();
        }

        if (savedInstanceState == null)
        {
            Log.i("savedInstanceState", "saved");
            setUpMapIfNeeded();
        }

        locationListener = new MyLocationListener();
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10, locationListener);

       /* FloatingActionButton FAB = (FloatingActionButton) findViewById(R.id.myLocationButton);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (location != null) {
                    Log.i("updateLocationUI", "12345");
                    //  mLatitudeTextView.setText(String.format("%s: %f", mLatitudeLabel,
                    Lat = location.getLatitude();
                    Log.i("Latitude", "" + location.getLatitude());
                    // mLongitudeTextView.setText(String.format("%s: %f", mLongitudeLabel,
                    Long = location.getLongitude();
                    Log.i("Longitude", "" + location.getLongitude());

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.i("latLng", "" + latLng);

                    if (mCurrLocationMarker!=null){
                        mCurrLocationMarker.remove();
                    }

                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.pick_drop_loc_icon);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Current Position");
                    markerOptions.snippet("Latitude:" + Lat + ",Longitude:" + Long);
                    markerOptions.draggable(true);
                    markerOptions.icon(icon);
//                    mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                    mGoogleMap.animateCamera(cameraUpdate);
//                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                }

                sendclientLocation();
            }
        });

        getLocation();*/

    }




    public  void sosbuttonenabled()
    {
        try
        {
            //  String url = "http://jaidevcoolcab.cabsaas.in/sandbox/index/signUp/?firstName=sandip&lastName=tathe&email=rohit@info.com&mobile=9226919759&password=sandip&confirmpassword=sandip&otp=&ApiKey=S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47&UserIPAddress=127.0.0.1&UserID=1212&UserAgent=Mozilla&responsetype=2";
            String urlnew =""+Url+"/sos/?";
            //  String APIKEY ="PVU1ZE-ZE4TPC-5IXWAJ-P2E6ZE-QONPEC-4IUGWD";
            String uri = Uri.parse(urlnew)
                    .buildUpon()
                    .appendQueryParameter("bookingId", Bookingid)
                    .appendQueryParameter("uId", Usersessionid)
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype", "2")
                    .build().toString();

            Log.i("uri", uri);

            // String
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
            // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            myJson = response;
           /* try
            {
                JSONArray json = new JSONArray(myJson);
                Log.i("json", "" + json);
                JSONObject jsonObject = json.getJSONObject(0);
              //  responsecode = jsonObject.getString("responseCode");

            }
            catch (JSONException j)
            {
                j.printStackTrace();
            }*/

            runOnUiThread(new Runnable()
            {
                public void run()
                {
                    try
                    {


                        JSONArray json = new JSONArray(myJson);
                        Log.i("json", "" + json);
                        JSONObject jsonObject = json.getJSONObject(0);
                        FAB.setBackgroundColor(getResources().getColor(R.color.RedTextColor));
                        //code for rating

                    }
                    catch (JSONException j)
                    {
                        j.printStackTrace();
                    }

                }
            });
        }
        catch (IOException e)
        {
            Log.i("Error", "" + e.toString());
        }
    }


    public void shareride()
    {
        try
        {
            //  String url = "http://jaidevcoolcab.cabsaas.in/sandbox/index/signUp/?firstName=sandip&lastName=tathe&email=rohit@info.com&mobile=9226919759&password=sandip&confirmpassword=sandip&otp=&ApiKey=S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47&UserIPAddress=127.0.0.1&UserID=1212&UserAgent=Mozilla&responsetype=2";
            String urlnew =""+Url+"/shareRides/?";
            //  String APIKEY ="PVU1ZE-ZE4TPC-5IXWAJ-P2E6ZE-QONPEC-4IUGWD";
            String uri = Uri.parse(urlnew)
                    .buildUpon()
                    .appendQueryParameter("bookingId", Bookingid)
                    .appendQueryParameter("mobile", shareMobileno.getText().toString())
                    .appendQueryParameter("vehicleId", vehicleId)
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype", "2")
                    .build().toString();

            Log.i("uri", uri);

            // String
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
            // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            myJson = response;

           try
            {
                JSONArray json = new JSONArray(myJson);
                Log.i("json", "" + json);
                JSONObject jsonObject = json.getJSONObject(0);

                String responseMessage = jsonObject.getString("responseMessage");

                Log.i("responseMessage", "" + responseMessage);

                final String ResMsg = responseMessage.substring(2, responseMessage.length() - 2);
//                Toast.makeText(getApplicationContext(), ResMsg, Toast.LENGTH_LONG).show();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog1.dismiss();
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapActivity_cstmr.this);
//                        alertDialog.setTitle("Confirmed");
                        alertDialog.setMessage(ResMsg);
                        //alertDialog.setIcon(R.drawable.);
                        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        alertDialog.show();
                    }
                });

            }
            catch (JSONException j)
            {
                j.printStackTrace();
            }

           /* runOnUiThread(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        JSONArray json = new JSONArray(myJson);
                        Log.i("json", "" + json);
                        JSONObject jsonObject = json.getJSONObject(0);
                        Toast.makeText(getApplicationContext(),"Sucessfully Share Ride",Toast.LENGTH_LONG).show();
                    }
                    catch (JSONException j)
                    {
                        j.printStackTrace();
                    }



                }
            });*/
        }
        catch (IOException e) {
            Log.i("Error", "" + e.toString());
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.i("onresume", "onresume");
        //driverdetails();
    }

    public void driverdetails()
    {
        try
        {
            //  String url = "http://jaidevcoolcab.cabsaas.in/sandbox/index/signUp/?firstName=sandip&lastName=tathe&email=rohit@info.com&mobile=9226919759&password=sandip&confirmpassword=sandip&otp=&ApiKey=S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47&UserIPAddress=127.0.0.1&UserID=1212&UserAgent=Mozilla&responsetype=2";
            String urlnew =""+Url+"/driverDetails/?";
            //  String APIKEY ="PVU1ZE-ZE4TPC-5IXWAJ-P2E6ZE-QONPEC-4IUGWD";
            String uri = Uri.parse(urlnew)
                    .buildUpon()
                    .appendQueryParameter("bookingId", Bookingid)
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype", "2")
                    .build().toString();

            Log.i("uri", uri);

            // String
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
            // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            myJson = response;
           /* try
            {
                JSONArray json = new JSONArray(myJson);
                Log.i("json", "" + json);
                JSONObject jsonObject = json.getJSONObject(0);
              //  responsecode = jsonObject.getString("responseCode");

            }
            catch (JSONException j)
            {
                j.printStackTrace();
            }*/

            runOnUiThread(new Runnable()
            {
                public void run()
                {
                    try
                        {
                            JSONArray json = new JSONArray(myJson);
                            Log.i("json", "" + json);
                            JSONObject jsonObject = json.getJSONObject(0);
                            final String Drivername = jsonObject.getString("driverName");
                            final String DriverMobile = jsonObject.getString("mobile");
                            final String vehiclename = jsonObject.getString("vehicleName");
                            final String vehiclenumber = jsonObject.getString("vehicleNumber");
                            final String Crnno = jsonObject.getString("crnNumber");
                            vehicleId = jsonObject.getString("vehicleId");
                            DriverName.setText(Drivername);
                            DriverNumber.setText(DriverMobile);
                            Vehiclename.setText(vehiclename);
                            Vehiclenumber.setText(vehiclenumber);
                            Cancelnumber.setText(Crnno);

                            if (Drivername != null)
                            {
                                sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                                editor = sharedpreferences.edit();
                                //code for rating review
                                editor.putString("Drivername", Drivername);
                                editor.putString("vehiclename", vehiclename);
                                editor.putString("vehiclenumber", vehiclenumber);
                                editor.putString("vehicleId", vehicleId);
                                editor.putString("crnNumber", Crnno);
                                editor.apply();
                            }
                        }
                        catch (JSONException j)
                        {
                            j.printStackTrace();
                        }
                }
            });
        }
        catch (IOException e)
        {
            Log.i("Error", "" + e.toString());
        }
    }


    private void placeAutocomplete_Pick()
    {
        int var1 = -1;
        Log.i("ED_Click", "ED_Click");
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(MapActivity_cstmr.this);
            startActivityForResult(intent, 1);
        }
        catch (GooglePlayServicesRepairableException var3) {
            var1 = var3.getConnectionStatusCode();
            Log.e("Places", "Could not open autocomplete activity", var3);
        }
        catch (GooglePlayServicesNotAvailableException var4) {
            var1 = var4.errorCode;
            Log.e("Places", "Could not open autocomplete activity", var4);
        }

        if (var1 != -1) {
            GoogleApiAvailability var5 = GoogleApiAvailability.getInstance();
            var5.showErrorDialogFragment(MapActivity_cstmr.this, var1, 2);
        }

    }

    private void placeAutocomplete_Drop()
    {
        int var1 = -1;
        Log.i("ED_Click", "ED_Click");
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(MapActivity_cstmr.this);
            startActivityForResult(intent, 2);
        }
        catch (GooglePlayServicesRepairableException var3) {
            var1 = var3.getConnectionStatusCode();
            Log.e("Places", "Could not open autocomplete activity", var3);
        }
        catch (GooglePlayServicesNotAvailableException var4) {
            var1 = var4.errorCode;
            Log.e("Places", "Could not open autocomplete activity", var4);
        }

        if (var1 != -1) {
            GoogleApiAvailability var5 = GoogleApiAvailability.getInstance();
            var5.showErrorDialogFragment(MapActivity_cstmr.this, var1, 2);
        }

    }


    protected void startLocationUpdates()
    {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);

    }


    private void setUpMapIfNeeded()
    {
        if (mGoogleMap == null)
        {
            Log.i("mapnull", "mapnull");
            SupportMapFragment mapFragment1 = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapView = mapFragment1.getView();
            mapFragment1.getMapAsync(this);


            if (mGoogleMap != null)
            {
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
    }

    public void TurnGpsOn()
    {
        if (mGoogleApiClient == null)
        {
            Log.i("mGoogleApiClient",""+mGoogleApiClient);
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()) .addApi(LocationServices.API) .build();
            mGoogleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder() .addLocationRequest(locationRequest);
            builder.setAlwaysShow(true); // this is the key ingredient

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            if (isMapReady)
                            {
                                mGoogleMap.setMyLocationEnabled(true);
//                                startLocationUpdates();
                            }
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(MapActivity_cstmr.this, REQUEST_CHECK_SETTINGS);
                                // finish();
                                // moveTaskToBack(true);
                                Log.i("Disable GPS ", "Disable GPS");
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                                Log.i("IntentSender ", "SendIntent");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Log.i("SETTINGS_CHANGE", "SETTINGSGPS");
                            break;
                    }
                }
            });
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {

        Log.i("updateValuesFromBundle", "updateValuesFromBundle");

        if (savedInstanceState != null)
        {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES))
            {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(KEY_REQUESTING_LOCATION_UPDATES);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION))
            {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                getmLastLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }
            updateUI();
        }
    }

    protected void createLocationRequest() {
        Log.i("createLocationRequest","createLocationRequest");
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    protected void buildLocationSettingsRequest()
    {
        //Log.i("buildLocationSettingsRequest","buildLocationSettingsRequest");

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
        builder.setAlwaysShow(true);

    }



    @Override
    public void onResult(LocationSettingsResult locationSettingsResult)
    {
        Log.i("locationSettingsResult","locationSettingsResult");
        final Status status = locationSettingsResult.getStatus();

        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied." + "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().

                    status.startResolutionForResult(MapActivity_cstmr.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here." + "not created.");
                break;
        }
    }

    public  void getcancelpopup()
    {

        try {
            String urlnew = ""+Url+"/cancelledComment/?";
            String uri = Uri.parse(urlnew)
                    .buildUpon()
                    .appendQueryParameter("comSubCatId", "4")
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype", "2")
                    .build().toString();

            Log.i("uri", uri);

            // String
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
            // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            myJson = response;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    showCancelType();
                   /* try
                    {
                        if (myJson != null)
                        {

                            *//*JSONArray json = new JSONArray(myJson);
                            Log.i("json", "" + json);
                            JSONObject jsonObject = json.getJSONObject(0);
                            String str = "4";
                            final String Message = jsonObject.getString("responseMessage");
                            final String responsecode = jsonObject.getString("responseCode");
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapActivity_cstmr.this);
                            alertDialog.setTitle("Cancelled");
                            alertDialog.setMessage(Message);
                            //alertDialog.setIcon(R.drawable.);
                            alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {

                                    Intent in = new Intent(getApplicationContext(),MapActivity.class);
                                    startActivity(in);
                                    finish();
                                }
                            });
                            alertDialog.show();*//*
                        }
                        else {
                            // Toast.makeText(getApplicationContext(), "Issues in Booking", Toast.LENGTH_LONG).show();
                        }
                    }
                    catch (JSONException j) {
                        j.printStackTrace();
                    }
*/

                }
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }




    public void showCancelType()
    {
        try
        {
        //   LayoutInflater layoutInflater = (LayoutInflater) MapActivity_cstmr.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //ERRORS HERE!!
        //    View popupView = layoutInflater.inflate(R.layout.cancelpopup, null);
         //   final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
          //  TextView txt_DropLocation = (TextView)popupView.findViewById(R.id.txt_drop_location);
            JSONArray json = new JSONArray(myJson);
            Log.i("json_tyavel_type_inv", "" + json);
            for (int i = 0; i < json.length(); i++)
            {

                JSONObject jsonObject = json.getJSONObject(i);
                final String travel_type = jsonObject.getString(TAG_templateName);
                Log.i("travel_type", travel_type);

                final int ty_id   = jsonObject.getInt(TAG_templateId);
                Log.i("ty_id", "" + ty_id);

                Tempstring.add(travel_type);
                TempId.add(String.valueOf(ty_id));

                map_name_value.put(jsonObject.getString(TAG_templateName), jsonObject.getString(TAG_templateId));







                // custom dialog
             /*   final Dialog dialog = new Dialog(getApplicationContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.cancelpopup);
                List<String> stringList=new ArrayList<>();  // here is list
                for(int j=0;j<json.length();j++)
                {
                    stringList.add(travel_type + (j + 1));
                    Log.i("Tag","Radiojson");
                }
              //  RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);

                for(int k=0;i<stringList.size();k++)
                {
                    RadioButton rb=new RadioButton(getApplicationContext()); // dynamically creating RadioButton and adding to RadioGroup.
                    rb.setText(stringList.get(k));
                    rg.addView(rb);
                    Log.i("Tag", "Radiojson");
                }

                dialog.show();*/

            }

            LayoutInflater layoutInflater = (LayoutInflater) MapActivity_cstmr.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //ERRORS HERE!!
            View popupView = layoutInflater.inflate(R.layout.cancelpopup, null);
            final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            Cancel = (Button)popupView.findViewById(R.id.cancelconfirm);
            Cancelnot = (Button)popupView.findViewById(R.id.cancelnotconfirm);
            Cancel.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    if(!selectedStrings.isEmpty())
                    {
                        popupWindow.dismiss();
                        Tripcancellation();
                    }

                    else
                    {

                        Toast.makeText(getApplicationContext(),"Please Select Reason",Toast.LENGTH_LONG).show();
                    }


                }
            });
            Cancelnot.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    popupWindow.dismiss();

                }
            });
            RadioGroup rg = (RadioGroup)popupView.findViewById(R.id.radio_group);
            for(int j=0;j<json.length();j++)
           {
               RadioButton rb = new RadioButton(getApplicationContext()); // dynamically creating RadioButton and adding to RadioGroup.
              /* ColorStateList colorStateList = new ColorStateList(
                       new int[][]{
                               new int[]{android.R.attr.state_enabled} //enabled
                       },
                       new int[] {getResources().getColor(R.color.TitleColor) }
               );
               //not working below version 21
               rb.setButtonTintList(colorStateList);//set the color tint list
              */
               rb.setId(Integer.parseInt(TempId.get(j)));
               rb.setText(Tempstring.get(j));
              // rb.setButtonDrawable(R.drawable.radioine);
               // rb.setBackgroundColor(getResources().getColor(R.color.TitleColor));
               //    rb.setButtonTintList(getResources().getColor(R.color.GoldenTextColor));

            //   rb.setButtonTintMode(getResources().getColor(R.color.BorderColor));

               rb.setTextColor(getResources().getColor(R.color.TitleColor));
               rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
               {
                   @Override
                   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                   {
                       if (((RadioButton) buttonView).isChecked())
                       {
                           selectedStrings.add(String.valueOf(buttonView.getId()));
                           Log.i("array_list_id1",""+selectedStrings);
                       }
                       else
                       {
                           selectedStrings.remove(String.valueOf(buttonView.getId()));
                           Log.i("array_list_id23",""+selectedStrings);
                       }

                   }
               });
               rg.addView(rb);
           }

            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        }
        catch (Exception e)
        {
            Log.e("Fail 1", e.toString());
//            Toast.makeText(getApplicationContext(), "Invalid IP Address", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i("onActivityResult", "onActivityResult");
        Log.i("requestCode", "" + requestCode);

        switch (requestCode)
        {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
//                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i("ResultCancelled","Cancelled");
                        //  Intent in = new Intent(getApplicationContext(),)
//                        TurnGpsOn();
                        // MapActivity.this.finish();

                        Log.i(TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }


       /* if (requestCode == 1)
        {
            Log.i("case_pick", "case_pick");
            if (resultCode == RESULT_OK) {
                place_pick = PlaceAutocomplete.getPlace(this, data);
                Log.i("place_pick", "" + place_pick);

                ed_auto_pick.setText(place_pick.getAddress().toString());
                Log.i("location_name11", ed_auto_pick.getText().toString());
                txtPick.setText(place_pick.getAddress().toString());
                LatLng latLong = place_pick.getLatLng();
                Log.i("latLong", "" + latLong);
                pick_lat = latLong.latitude;
                Log.i("pick_lat", "" + pick_lat);
                pick_long = latLong.longitude;
                Log.i("pick_long", "" + pick_long);

                pick_LatLong = new LatLng(pick_lat, pick_long);
                Log.i("pick_LatLong", "" + pick_LatLong);

                CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(latLong, 15);
                mGoogleMap.moveCamera(cameraPosition);
                mGoogleMap.animateCamera(cameraPosition);
            }
            else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("status", status.getStatusMessage());

            }
            else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        if (requestCode == 2)
        {
            Log.i("case_drop", "case_drop");
            if (resultCode == RESULT_OK)
            {
                place_drop = PlaceAutocomplete.getPlace(this, data);
                Log.i("place_drop", "" + place_drop);

                ed_auto_drop.setText(place_drop.getAddress().toString());
                Log.i("location_name11", ed_auto_drop.getText().toString());
                txtDrop.setText(place_drop.getAddress().toString());
                LatLng latLong1 = place_drop.getLatLng();
                Log.i("latLong1", "" + latLong1);
                drop_lat = latLong1.latitude;
                Log.i("drop_lat", "" + drop_lat);
                drop_long = latLong1.longitude;
                Log.i("drop_long", "" + drop_long);
                drop_LatLong = new LatLng(drop_lat, drop_long);
                Log.i("drop_LatLong", "" + drop_LatLong);

                CameraUpdate cameraPosition1 = CameraUpdateFactory.newLatLngZoom(latLong1, 15);
                mGoogleMap.moveCamera(cameraPosition1);
                mGoogleMap.animateCamera(cameraPosition1);
            }
            else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("status", status.getStatusMessage());

            }
            else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
//        Log.i("address", ""+ place_drop);*/
    }


    private void updateUI()
    {
        setButtonsEnabledState();
        updateLocationUI();
    }

    private void setButtonsEnabledState()
    {
        if (mRequestingLocationUpdates)
        {
            // mStartUpdatesButton.setEnabled(false);
            // mStopUpdatesButton.setEnabled(true);
        } else {
            // mStartUpdatesButton.setEnabled(true);
            // mStopUpdatesButton.setEnabled(false);
        }
    }

    public void Tripcancellation()
    {
        String crno = Cancelnumber.getText().toString();

        try {
            //  String url = "http://jaidevcoolcab.cabsaas.in/sandbox/index/signUp/?firstName=sandip&lastName=tathe&email=rohit@info.com&mobile=9226919759&password=sandip&confirmpassword=sandip&otp=&ApiKey=S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47&UserIPAddress=127.0.0.1&UserID=1212&UserAgent=Mozilla&responsetype=2";
            String urlnew = ""+Url+"/bookingCancellation/?";
            //  String APIKEY ="PVU1ZE-ZE4TPC-5IXWAJ-P2E6ZE-QONPEC-4IUGWD";
            String uri = Uri.parse(urlnew)
                    .buildUpon()
                    .appendQueryParameter("crnNumber", crno)
                    .appendQueryParameter("tempId",String.valueOf(selectedStrings))
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype", "2")
                    .build().toString();

            Log.i("uri", uri);

            // String
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
            // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            myJson = response;
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                   // progressDialog.dismiss();
                    try
                    {
                        if (myJson != null)
                        {
                            JSONArray json = new JSONArray(response);
                            Log.i("json", "" + json);
                            JSONObject jsonObject = json.getJSONObject(0);
                            String str = "4";
                            final String Message = jsonObject.getString("responseMessage");
                            final String responsecode = jsonObject.getString("responseCode");
                            final String Status_res_massge1 = Message.substring(2, Message.length() - 2);
                            if (responsecode.equals("1"))
                            {

                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapActivity_cstmr.this);
                                alertDialog.setTitle("Cancelled");
                                alertDialog.setMessage(Status_res_massge1);
                                alertDialog.setCancelable(false);
                                alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {

                                        Intent in = new Intent(getApplicationContext(), MapActivity.class);
                                        startActivity(in);
                                        finish();
                                    }
                                });
                                alertDialog.show();
                            }

                                     else
                                 {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapActivity_cstmr.this);
                                alertDialog.setTitle("Not Cancelled");
                                alertDialog.setMessage(Status_res_massge1);
                                alertDialog.setCancelable(false);
                                alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {


                                    }
                                });
                                alertDialog.show();


                        }}
                    }
                    catch (JSONException j) {
                        j.printStackTrace();
                    }


                }
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    public void getLocation()
    {
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (location != null)
        {
            Log.i("updateLocationUI", "startLocationUpdates");
            Lat = location.getLatitude();
            Log.i("Latitude", "" + location.getLatitude());
            Long = location.getLongitude();
            Log.i("Longitude", "" + location.getLongitude());

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            Log.i("latLng", "" + latLng);
        }
//        sendclientLocation();
    }




    private void updateLocationUI()
    {
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (location != null)
        {
            Log.i("updateLocationUI","12345");
            Lat = location.getLatitude();
            Log.i("Latitude",""+location.getLatitude());
            Long = location.getLongitude();
            Log.i("Longitude",""+location.getLongitude());

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            Log.i("latLng",""+latLng);
            mGoogleMap.setMyLocationEnabled(true);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.cab_icon);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.snippet("Latitude:"+Lat+",Longitude:"+Long);
            markerOptions.draggable(true);
            markerOptions.icon(icon);
//            mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
//            mCurrLocationMarker.isDraggable();
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

           // ed_auto_drop.setEnabled(false);
        }
    }

    protected void stopLocationUpdates()
    {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        LocationServices.FusedLocationApi.removeLocationUpdates
                (
                        mGoogleApiClient,
                        this
                ).setResultCallback(new ResultCallback<Status>()
        {
            @Override
            public void onResult(Status status)
            {
                mRequestingLocationUpdates = false;
                setButtonsEnabledState();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        isMapReady = true;

        Log.i("Ready", "Google Map");
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setOnMyLocationChangeListener(this);

     /*   mGoogleMap.setOnMapClickListener(this);
        mGoogleMap.setOnMapLongClickListener(this);
        mGoogleMap.setOnCameraChangeListener(this);*/

        LatLng pos = mGoogleMap.getCameraPosition().target;
        Log.i("pos145",""+pos);
        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
            {
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            }

        }
        else
        {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
//        mGoogleMap.setMyLocationEnabled(true);
    }

    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnected(Bundle connectionHint)
    {
        Log.i(TAG, "Connected to GoogleApiClient");

        if (mLastLocation == null)
        {
            Log.i("mLastLocation",""+mLastLocation);
            try
            {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }
            catch (SecurityException e)
            {
                Log.i("SecurityException", ""+e.toString());
            }

            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateLocationUI();
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
//        updateUI();
//        mGoogleMap.setOnCameraChangeListener(this);
        Log.i("onLocationChange","onLocationChange");

        if (mCurrLocationMarker != null)
        {
            mCurrLocationMarker.remove();

        }
        mGoogleMap.clear();
//        mGoogleMap.setOnCameraChangeListener(this);

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (location != null)
        {
            Log.i("updateLocationUI", "startLocationUpdates");
            Lat = location.getLatitude();
            Log.i("Latitude", "" + location.getLatitude());
            Long = location.getLongitude();
            Log.i("Longitude", "" + location.getLongitude());

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            Log.i("latLng", "" + latLng);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.cab_icon);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.snippet("Latitude:"+Lat+",Longitude:"+Long);
            markerOptions.draggable(true);
            markerOptions.icon(icon);
//            mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
//            mCurrLocationMarker.isDraggable();

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }

    @Override
    public void onConnectionSuspended(int cause)
    {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result)
    {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION))
            {
                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            else
            {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_LOCATION:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED)
                    {

                        if (mGoogleApiClient == null)
                        {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(false);
                    }

                } else
                {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition)
    {
        mGoogleMap.setOnMapLoadedCallback(this);
    }

    @Override
    public void onMapLoaded()
    {
        Log.i("onMapLoad", "OnMapLoad");
        Log.i("place_onMapLoad", ""+place_pick);
        Log.i("place_onMapLoad", ""+place_drop);

    }

    @Override
    public void onMyLocationChange(Location location) {

        double lat = location.getLatitude();
        Log.i("Lat_LocationChange", ""+lat);
        double lng = location.getLongitude();
        Log.i("Lat_LocationChange", ""+lng);

        LatLng latLng = new LatLng(lat, lng);
        Log.i("latLng_LocationChange", ""+latLng);

        Location prevLoc = new Location("service Provider");
        prevLoc.setLatitude(lat);
        prevLoc.setLongitude(lng);
        Location newLoc = new Location("service Provider");
        newLoc.setLatitude(lat);
        newLoc.setLongitude(lng);

        float bearing = prevLoc.bearingTo(newLoc) ;

        if (mCurrLocationMarker != null)
        {
            mCurrLocationMarker.remove();
        }

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.cab_icon);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(icon);
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.rotation(bearing);
        markerOptions.flat(true);
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        mGoogleMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), Booking.class);
        Tripstatus = false;
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.i("onpausefalse","onpausefalse");
//        Tripstatus = false;
        finish();

    }
}