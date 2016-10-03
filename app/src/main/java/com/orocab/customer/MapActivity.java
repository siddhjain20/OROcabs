package com.orocab.customer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,ResultCallback<LocationSettingsResult>,PlaceSelectionListener,
        GoogleMap.OnMapClickListener,GoogleMap.OnMapLongClickListener,GoogleMap.OnCameraChangeListener,GoogleMap.OnMapLoadedCallback

{
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawerLayout;
    public static Toolbar toolbar;
    /*private MapView mapView;*/
    private GoogleMap googleMap;
    private GoogleMap mMap;
    private GoogleMap mGoogleMap;

   protected LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    GoogleApiClient getmGoogleApiClient;
    LocationRequest getmLocationRequest;
    Location getmLastLocation;
    protected LocationSettingsRequest mLocationSettingsRequest;

    Location mLastLocation;
    Marker mCurrLocationMarker;
    Context context;
    Double Lat = 0.0, Long = 0.0;
//    RelativeLayout Autoapi;
    String myJson;
    boolean isMapReady = false; boolean isDataLoaded = false;
    JSONArray Vehicle =null;
    JSONArray VehicleTarif =null;
    public static TextView Farepopup,FareEstimate;
    TextView Picuplocation,Droploaction,Estimatecharges1,Estimatecharges2;

    boolean isestimateready=false;
    boolean isgpscheck=false;
    String pickuplocation,Droplocation;
    String searchstatus = "";


    protected static final String TAG = "MainActivity";
    public static boolean touch = false;
    PopupWindow popupWindow;



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

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;
    LocationManagercheck locationmanagercheck ;
    Location location = null;
    Location locationNetwork = null;
    LocationManager locationManager;
    int PLACE_AUTOCOMPLETE_REQUEST_CODEPICK = 1;
    int PLACE_AUTOCOMPLETE_REQUEST_CODEDROP = 3;

    PlaceAutocompleteFragment autocompleteFragment,autocompleteFragment1;
    LocationListener locationListener;


    ConnectionDetector cd;
    String Url,IPAddress,APIKEY;
    UserSessionManager sessionManager;

    private static final String TAG_Lat = "latitude";
    private static final String TAG_Long = "longitude";
    private static final String TAG_minu = "minute";
    private static final String TAG_Vehicle = "vehicleId";
    private static final String TAG_driverVehicleLocationResult = "driverVehicleLocationResult";
    private static final String TAG_driverVehicleTariffResult = "driverVehicleTariffResult";
    private static final String TAG_approximateFareMin="approximateFareMin";
    private static final String TAG_approximateFareMax="approximateFareMax";
    private static final String TAG_approximatedistance="distance";


    // [{"approximateFareMin":49,"approximateFareMax":79,"approximateTravelTime":"11 mins","kmRate":"10.00","distance":"4.6 km","dryRunKm":"2","dryRunRate":"2.00","dryRunDistance":2.6,"dryRunCharge":5.2}]
    String Time,Vehicleid;

    TextView AvailTime;
    public static Button confirm;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String uid = "uId";
    String Usersessionid;
    String city;
    String Drivername,DriverMobile,vehicleno,vehiclename,Crno;

    TextView PopbaseFare,PopupBasekm,popupBasemin;

    TextView DriverName,Vehiclename,Vehiclenumber,DriverNumber,Cancelnumber;
    LinearLayout Calldriver,CancelTrip,DriverLayout;
    ImageButton cancelpopoup;
    Context mcontext;
    String BaseFare="",Basekm="",RideTime="",Kmrate="";
    ImageButton btn_currentLocation,Cancelrideestimate;
    Double Droplat,Droplong;
    String Approximatefaremin,Approximatefaremax;
    int mDay,mMonth,mYear;
    String CurrentDate;

    Place place;
    String BookingId,responsecode;

    public static TextView ed_auto_pick;
    public static TextView ed_auto_drop;
    LinearLayout ed_drop,ed_pick;
    TextView txtDrop, txtPick;
    RelativeLayout Autoapi, drop;
    public static ImageView drop_arrow, pick_arrow;
    LatLng pick_LatLong, drop_LatLong;
    double pick_lat, pick_long,Distance=0.0;
    double drop_lat, drop_long;
    String dropAddress;
    Place place_pick, place_drop;
    public static LinearLayout book_cab_layout;

    Double dryrunrate, dryRunKm = 0.0, dryRun = 0.0, dryRunCharges;

    RelativeLayout Book_Cnfrm;
    RelativeLayout book_dropLayout;
    TextView book_pickLocation, book_dropLocation;
    public static FloatingActionButton FAB;
    ImageView marker_img;
    String bestProvider,Packagename;
    int version_code;
    View mapView;

    boolean mapLoad = false;
    public static boolean mMapIsTouched = false;
    public static ImageView mark_img, cross_img;
    public static LinearLayout BookingLayout;
    public static Handler timeoutHandler;//= new Handler();
    public static Runnable finalizer;
    public static Button Book;
    Date Start,End;

    ProgressDialog progressDialog = null;
    ProgressDialog progressDialogML = null;

 //   LinearLayout get_location;
    LatLng latLng1;

    public static NetworkChange receiver;
    Snackbar snackbar;
    FrameLayout content_frame;
    LatLngBounds.Builder builder;
    LatLngBounds bounds;

    LinearLayout fare_layout;

    boolean onCreate = true;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        cd = new ConnectionDetector(getApplicationContext());
        boolean chk = cd.isConnectingToInternet(MapActivity.this);
        // Log.i("chk", "" + chk);

        if (getSupportActionBar() != null)
        {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        set(navMenuTitles, navMenuIcons);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        timeoutHandler = new Handler();
        try
        {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            version_code = info.versionCode;
            Packagename = info.packageName;
            //   Log.i("version_code", "" + version_code);
        }
        catch (PackageManager.NameNotFoundException e)
        {

        }

        mark_img = (ImageView) findViewById(R.id.marker_img);
        cross_img = (ImageView) findViewById(R.id.marker_cross);
        BookingLayout = (LinearLayout) findViewById(R.id.BookingLayout);
        drop = (RelativeLayout) findViewById(R.id.Autoapi_drop);
        ed_pick = (LinearLayout) findViewById(R.id.pick_loc);
        ed_drop = (LinearLayout) findViewById(R.id.drop_loc);
        txtPick = (TextView) findViewById(R.id.txt_pick);
        ed_auto_pick = (TextView) findViewById(R.id.ed_place_autocomplete_fragment);
        ed_auto_drop = (TextView) findViewById(R.id.ed_drop_place_autocomplete_fragment);
        drop_arrow = (ImageView) findViewById(R.id.drop_arrow);
        pick_arrow = (ImageView) findViewById(R.id.arrow);
        txtDrop = (TextView) findViewById(R.id.txt_drop);
        //book_cab_layout = (LinearLayout) findViewById(R.id.book_cab_layout);
        content_frame = (FrameLayout) findViewById(R.id.content_frame);
        Autoapi = (RelativeLayout) findViewById(R.id.Autoapi);
        Book_Cnfrm = (RelativeLayout) findViewById(R.id.book_cnfrm_layout);
        book_dropLayout = (RelativeLayout) findViewById(R.id.book_dropLayout);
        book_pickLocation = (TextView) findViewById(R.id.pick_location);
        book_dropLocation = (TextView) findViewById(R.id.book_drop_location);
        fare_layout = (LinearLayout) findViewById(R.id.fare_layout);
        AvailTime = (TextView) findViewById(R.id.CabTime);
        Book = (Button) findViewById(R.id.Booknow);
        confirm = (Button) findViewById(R.id.confirmnow);
        FAB = (FloatingActionButton) findViewById(R.id.myLocationButton);
        Farepopup = (TextView) findViewById(R.id.Farepopup);
        FareEstimate = (TextView) findViewById(R.id.Fareestimate);

        Book.setEnabled(true);

        receiver = new NetworkChange()
        {
            @Override
            protected void onNetworkChange()
            {
                if (receiver.isConnected)
                {
                    checkversion();
                    sendclientLocation();
                    BookingLayout.setVisibility(View.VISIBLE);
                    FAB.setVisibility(View.VISIBLE);

                    if (Book.isEnabled())
                    {
                        Log.i("isEnabled","isEnabled");
                        Book.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        Log.i("isDisabled","isDisabled");
                        Book.setVisibility(View.GONE);
                    }

                    if (snackbar != null && snackbar.isShown())
                    {
                        snackbar.dismiss();
                    }
                }
                else
                {
                    BookingLayout.setVisibility(View.GONE);
                    FAB.setVisibility(View.GONE);
                    snackbar = Snackbar.make(content_frame, "Please check your internet connection", Snackbar.LENGTH_INDEFINITE);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.RedTextColor));
                    snackbar.show();
                }
            }
        };

        registerReceiver(receiver, filter);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Url = cd.geturl();
        IPAddress = cd.getLocalIpAddress();
        APIKEY = cd.getAPIKEY();
        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        Usersessionid = (shared.getString(uid, ""));

        sessionManager = new UserSessionManager(getApplicationContext());

        if (cd.isConnectingToInternet(MapActivity.this))
        {
            checkversion();
        }
        else
        {
            BookingLayout.setVisibility(View.GONE);
            snackbar = Snackbar.make(content_frame, "Please check your internet connection and try again", Snackbar.LENGTH_INDEFINITE);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.RedTextColor));
            snackbar.show();
        }
        TurnGpsOn();

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
        bestProvider = lm.getBestProvider(criteria, true);

        mapLoad = false;

        ed_auto_drop.setEnabled(false);

        Autoapi.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (receiver.isConnected)
                {
                    placeAutocomplete_Pick();
                }
            }
        });

        drop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (receiver.isConnected)
                {
                    placeAutocomplete_Drop();
                }
            }
        });

        ed_pick.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (receiver.isConnected)
                {
                    Autoapi.setVisibility(View.VISIBLE);
                    drop.setVisibility(View.GONE);
                    ed_pick.setVisibility(View.GONE);
                    drop_arrow.setVisibility(View.GONE);
                    ed_drop.setVisibility(View.VISIBLE);
                    if (!txtPick.getText().toString().equals(""))
                    {
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(8);
                        mGoogleMap.animateCamera(zoom);
                        //Log.i("pick_LatLong", "" + pick_LatLong);
                        CameraUpdate cameraPosition1 = CameraUpdateFactory.newLatLngZoom(pick_LatLong, 15);
                        mGoogleMap.animateCamera(cameraPosition1);
                    }
                    ed_auto_pick.setEnabled(true);
                    ed_auto_drop.setEnabled(false);
                }
            }
        });

        ed_drop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (receiver.isConnected)
                {
                    if (txtDrop.getText().toString().equals(""))
                    {
                        placeAutocomplete_Drop();
                    }
                    Autoapi.setVisibility(View.GONE);
                    drop.setVisibility(View.VISIBLE);
                    ed_pick.setVisibility(View.VISIBLE);
                    drop_arrow.setVisibility(View.VISIBLE);
                    ed_drop.setVisibility(View.GONE);

                    if (!txtDrop.getText().toString().equals(""))
                    {
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(8);
                        mGoogleMap.animateCamera(zoom);
                        //Log.i("drop_LatLong", "" + drop_LatLong);
                        CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(drop_LatLong, 15);
                        mGoogleMap.animateCamera(cameraPosition);
                    }
                    ed_auto_pick.setEnabled(false);
                    ed_auto_drop.setEnabled(true);
                }
            }
        });

        Book.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (receiver.isConnected)
                {
                    mark_img.setVisibility(View.GONE);
                    Book.setEnabled(false);
                    FareEstimate.setEnabled(true);

                    if (txtDrop.getText().toString().equals(""))
                    {
                        CameraUpdate cameraPosition = CameraUpdateFactory.zoomBy(3);
                        mGoogleMap.animateCamera(cameraPosition);
                    }
                    ed_auto_pick.setEnabled(false);
                    ed_auto_drop.setEnabled(false);
                    FAB.setVisibility(View.GONE);
                    drop_arrow.setVisibility(View.GONE);
                    drop.setVisibility(View.GONE);
                    ed_pick.setVisibility(View.GONE);
                    Autoapi.setVisibility(View.GONE);
                    ed_drop.setVisibility(View.GONE);
                    pick_arrow.setVisibility(View.GONE);
                    String pick_str = ed_auto_pick.getText().toString();
                    Book_Cnfrm.setVisibility(View.VISIBLE);
                    book_dropLayout.setVisibility(View.VISIBLE);
                    book_pickLocation.setText(pick_str);
                    mGoogleMap.clear();

                    BitmapDescriptor icon1 = BitmapDescriptorFactory.fromResource(R.drawable.location_mark_icon);
                    MarkerOptions options1 = new MarkerOptions();
                    options1.position(pick_LatLong);
                    options1.title("Pick Location");
                    options1.snippet("Latitude:" + Lat + ",Longitude:" + Long);
                    options1.icon(icon1);
                    // Crash Happened some time..
                    mGoogleMap.addMarker(options1);

                    if (!txtDrop.getText().toString().equals(""))
                    {
                        String drop_str = txtDrop.getText().toString();
                        book_dropLocation.setText(drop_str);

                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.location_mark_icon_drop);
                        MarkerOptions options = new MarkerOptions();
                        options.position(drop_LatLong);
                        options.title("Drop Location");
                        options.snippet("Latitude:" + Droplat + ",Longitude:" + Droplong);
                        options.icon(icon);
                        mCurrLocationMarker = mGoogleMap.addMarker(options);

                        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);

                        builder = new LatLngBounds.Builder();
                        builder.include(pick_LatLong);
                        builder.include(drop_LatLong);
                        bounds = builder.build();
                        int padding = 60; // offset from edges of the map in pixels
                        mGoogleMap.setPadding(0, 310, 0, 100);
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mGoogleMap.animateCamera(cu);
                    }

                    if (Distance > dryRun && dryRun > 0.0)
                    {
                        alertDialoge();
                    }
                    else
                    {
                        Book.setVisibility(View.GONE);
                        confirm.setVisibility(View.VISIBLE);
                        Farepopup.setVisibility(View.GONE);
                        FareEstimate.setVisibility(View.VISIBLE);
                    }
                }
            }


        });

        book_dropLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setBoundsBias((new LatLngBounds(
                            new LatLng(19.8036125, 75.1932621),
                            new LatLng(19.9365798, 75.4171406))))
                            .build(MapActivity.this);
                    startActivityForResult(intent, 4);
                }
                catch (GooglePlayServicesRepairableException e)
                {
                    // TODO: Handle the error.
                }
                catch (GooglePlayServicesNotAvailableException e)
                {
                    // TODO: Handle the error.
                }
                isestimateready = true;
                Book.setVisibility(View.GONE);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapActivity.this);
                alertDialog.setTitle("Booking");
                alertDialog.setMessage("Are you Sure want to Confirm Booking");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        progressDialog = ProgressDialog.show(MapActivity.this, "", "Confirming trip ..please wait", true);
                        new Thread(new Runnable()
                        {
                            public void run()
                            {
                                Bookingconfirm();
                            }
                        }).start();
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


        FAB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (receiver.isConnected)
                {
                    if (ed_auto_pick.isEnabled() || ed_auto_drop.isEnabled())
                    {
                        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                        if (location != null)
                        {
                            Lat = location.getLatitude();
                            //  Log.i("Latitude", "" + location.getLatitude());
                            Long = location.getLongitude();
                            // Log.i("Longitude", "" + location.getLongitude());
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            //  Log.i("latLng", "" + latLng);
                            if (mCurrLocationMarker != null)
                            {
                                mCurrLocationMarker.remove();
                            }
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                            mGoogleMap.animateCamera(cameraUpdate);
                        }
                    }
                }
            }
        });

        FareEstimate.setEnabled(false);

        fare_layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (FareEstimate.isEnabled())
                {
                    if (book_dropLocation.getText().toString().equals(""))
                    {
                        try
                        {
                            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setBoundsBias((new LatLngBounds(
                                    new LatLng(19.8036125, 75.1932621),
                                    new LatLng(19.9365798, 75.4171406))))
                                    .build(MapActivity.this);
                            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODEDROP);
                        }
                        catch (GooglePlayServicesRepairableException e)
                        {
                            // TODO: Handle the error.
                        }
                        catch (GooglePlayServicesNotAvailableException e)
                        {
                            // TODO: Handle the error.
                        }
                        isestimateready = true;
                        Book.setVisibility(View.GONE);
                    }
                    else
                    {
                        sendrideestimate();
                        LayoutInflater layoutInflater = (LayoutInflater) MapActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //ERRORS HERE!!
                        View popupView = layoutInflater.inflate(R.layout.rideestimate, null);
                        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        popupWindow.setFocusable(true);
                        popupWindow.update();
                        LinearLayout Drop_Layout = (LinearLayout) popupView.findViewById(R.id.Droplocation);
                        TextView txt_DropLocation = (TextView) popupView.findViewById(R.id.txt_drop_location);
                        Picuplocation = (TextView) popupView.findViewById(R.id.pick_add);
                        Droploaction = (TextView) popupView.findViewById(R.id.drop_add);
                        Estimatecharges1 = (TextView) popupView.findViewById(R.id.estimate1);
                        Estimatecharges2 = (TextView) popupView.findViewById(R.id.estimate2);
                        Estimatecharges1.setText(Approximatefaremin + " - "+ Approximatefaremax);
                        // int app = 20 + Integer.parseInt(Approximatefare);
                        //Estimatecharges2.setText(Approximatefaremax);
                        Cancelrideestimate = (ImageButton) popupView.findViewById(R.id.cancelrideestimate);

                        Cancelrideestimate.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                popupWindow.dismiss();
                                Book.setVisibility(View.GONE);
                            }
                        });

                        Drop_Layout.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                try
                                {
                                    popupWindow.dismiss();
                                    Book.setVisibility(View.GONE);
                                    //Log.i("Autcomplete", "Autocm");
                                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setBoundsBias((new LatLngBounds(
                                            new LatLng(19.8036125, 75.1932621),
                                            new LatLng(19.9365798, 75.4171406))))
                                            .build(MapActivity.this);
                                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODEDROP);
                                }
                                catch (GooglePlayServicesRepairableException e)
                                {
                                    // TODO: Handle the error.
                                }
                                catch (GooglePlayServicesNotAvailableException e)
                                {
                                    // TODO: Handle the error.
                                }
                                isestimateready = true;
                                Book.setVisibility(View.GONE);
                            }
                        });
                        Picuplocation.setText(pickuplocation);
                        Droploaction.setText(book_dropLocation.getText().toString());
                        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                        Book.setVisibility(View.GONE);
                    }
                }
                else if (Farepopup.isEnabled())
                {
                    if (receiver.isConnected)
                    {
                        LayoutInflater layoutInflater = (LayoutInflater) MapActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //ERRORS HERE!!
                        View popupView = layoutInflater.inflate(R.layout.farepopupdetails, null);
                        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        popupWindow.setFocusable(true);
                        popupWindow.update();
                        PopbaseFare = (TextView) popupView.findViewById(R.id.BaseFare);
                        PopupBasekm = (TextView) popupView.findViewById(R.id.Basekm);
                        popupBasemin = (TextView) popupView.findViewById(R.id.Basemin);
                        cancelpopoup = (ImageButton) popupView.findViewById(R.id.cancelpopup);

                        if (BaseFare.equals("") && Basekm.equals(""))
                        {
                            PopbaseFare.setText("");
                            PopupBasekm.setText("");
                            popupBasemin.setText("");
                        }
                        else
                        {
                            PopbaseFare.setText(BaseFare + "/" + Basekm);
                            PopupBasekm.setText(Kmrate);
                            popupBasemin.setText(RideTime);
                        }

                        // btnReject = (Button) popupView.findViewById(R.id.reject_booking_popup);
                        // btnCancel = (Button) popupView.findViewById(R.id.cancel_reject_popup);
                        cancelpopoup.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                popupWindow.dismiss();
                            }
                        });
                        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                    }
                }
            }
        });
                /*FareEstimate.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (book_dropLocation.getText().toString().equals(""))
                        {
                            try
                            {
                               // Log.i("Autcomplete", "Autocm");
                                Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setBoundsBias((new LatLngBounds(
                                        new LatLng(19.8036125, 75.1932621),
                                        new LatLng(19.9365798, 75.4171406))))
                                        .build(MapActivity.this);

                                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODEDROP);
                            } catch (GooglePlayServicesRepairableException e) {
                                // TODO: Handle the error.
                            } catch (GooglePlayServicesNotAvailableException e) {
                                // TODO: Handle the error.
                            }

                            isestimateready = true;
                            Book.setVisibility(View.GONE);
                          //  Log.i("isestimateready", "" + isestimateready);
                        }

                        else
                        {
                            sendrideestimate();
                            LayoutInflater layoutInflater = (LayoutInflater) MapActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //ERRORS HERE!!
                            View popupView = layoutInflater.inflate(R.layout.rideestimate, null);
                            final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            popupWindow.setFocusable(true);
                            popupWindow.update();
                            LinearLayout Drop_Layout = (LinearLayout) popupView.findViewById(R.id.Droplocation);
                            TextView txt_DropLocation = (TextView) popupView.findViewById(R.id.txt_drop_location);
                            Picuplocation = (TextView) popupView.findViewById(R.id.pick_add);
                            Droploaction = (TextView) popupView.findViewById(R.id.drop_add);
                            Estimatecharges1 = (TextView) popupView.findViewById(R.id.estimate1);
                            Estimatecharges2 = (TextView) popupView.findViewById(R.id.estimate2);

                            Estimatecharges1.setText(Approximatefaremin + "-");
                            // int app = 20 + Integer.parseInt(Approximatefare);
                            Estimatecharges2.setText(Approximatefaremax);
                            Cancelrideestimate = (ImageButton) popupView.findViewById(R.id.cancelrideestimate);
                            Cancelrideestimate.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    popupWindow.dismiss();
                                    Book.setVisibility(View.GONE);
                                }
                            });

                            Drop_Layout.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    try
                                    {
                                        popupWindow.dismiss();
                                        Book.setVisibility(View.GONE);
                                        //Log.i("Autcomplete", "Autocm");
                                        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setBoundsBias((new LatLngBounds(
                                                new LatLng(19.8036125, 75.1932621),
                                                new LatLng(19.9365798, 75.4171406))))
                                                .build(MapActivity.this);

                                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODEDROP);
                                    } catch (GooglePlayServicesRepairableException e)
                                    {
                                        // TODO: Handle the error.
                                    } catch (GooglePlayServicesNotAvailableException e)
                                    {
                                        // TODO: Handle the error.
                                    }

                                    isestimateready = true;
                                    Book.setVisibility(View.GONE);
                                }
                            });
                            Picuplocation.setText(pickuplocation);
                            Droploaction.setText(book_dropLocation.getText().toString());
                            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                            Book.setVisibility(View.GONE);
                        }
                    }
                });
                Farepopup.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (receiver.isConnected)
                        {
                            LayoutInflater layoutInflater = (LayoutInflater) MapActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //ERRORS HERE!!
                            View popupView = layoutInflater.inflate(R.layout.farepopupdetails, null);
                            final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            popupWindow.setFocusable(true);
                            popupWindow.update();
                            PopbaseFare = (TextView) popupView.findViewById(R.id.BaseFare);
                            PopupBasekm = (TextView) popupView.findViewById(R.id.Basekm);
                            popupBasemin = (TextView) popupView.findViewById(R.id.Basemin);
                            cancelpopoup = (ImageButton) popupView.findViewById(R.id.cancelpopup);
                            PopbaseFare.setText(BaseFare + "/" + Basekm);
                            PopupBasekm.setText(Kmrate);
                            popupBasemin.setText(RideTime);
                            // btnReject = (Button) popupView.findViewById(R.id.reject_booking_popup);
                            // btnCancel = (Button) popupView.findViewById(R.id.cancel_reject_popup);
                            cancelpopoup.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupWindow.dismiss();
                                }
                            });
                            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                        }
                    }
                });*/


        if (savedInstanceState == null)
        {
            setUpMapIfNeeded();
        }


        locationListener = new MyLocationListener();
        LocationManager lmngr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lmngr.requestLocationUpdates(bestProvider, 2000, 10, locationListener);
    }

    private void placeAutocomplete_Pick()
    {
        int var1 = -1;
        LatLngBounds latLngBounds = new LatLngBounds(
                    new LatLng(19.8036125,75.1932621),
                    new LatLng(19.9365798,75.4171406));

        final AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .setTypeFilter(3)
                .build();

        try
        {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setBoundsBias(latLngBounds).setFilter(typeFilter).build(MapActivity.this);
            startActivityForResult(intent, 1);
        }
        catch (GooglePlayServicesRepairableException var3)
        {
            var1 = var3.getConnectionStatusCode();
         //   Log.e("Places", "Could not open autocomplete activity", var3);
        }
        catch (GooglePlayServicesNotAvailableException var4)
        {
            var1 = var4.errorCode;
          //  Log.e("Places", "Could not open autocomplete activity", var4);
        }

        if (var1 != -1)
        {
            GoogleApiAvailability var5 = GoogleApiAvailability.getInstance();
            var5.showErrorDialogFragment(MapActivity.this, var1, 2);
        }

    }

    private void placeAutocomplete_Drop()
    {
        int var1 = -1;
        Log.i("ED_Click", "ED_Click");

        LatLngBounds latLngBounds = new LatLngBounds(
                new LatLng(19.8036125,75.1932621),
                new LatLng(19.9365798,75.4171406));

        final AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .setTypeFilter(3)
                .build();

        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setBoundsBias(latLngBounds).setFilter(typeFilter).build(MapActivity.this);
            startActivityForResult(intent, 2);
        }
        catch (GooglePlayServicesRepairableException var3)
        {
            var1 = var3.getConnectionStatusCode();
          //  Log.e("Places", "Could not open autocomplete activity", var3);
        }
        catch (GooglePlayServicesNotAvailableException var4)
        {
            var1 = var4.errorCode;
          //  Log.e("Places", "Could not open autocomplete activity", var4);
        }

        if (var1 != -1)
        {
            GoogleApiAvailability var5 = GoogleApiAvailability.getInstance();
            var5.showErrorDialogFragment(MapActivity.this, var1, 2);
        }
    }





    public void getLocationFromAddress(String strAddress)
    {

        Geocoder coder = new Geocoder(this);
        try {
            ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(strAddress, 50);
            for(Address add : adresses)
            {
               // if (statement) {//Controls to ensure it is right address such as country etc.
                    double longitude = add.getLongitude();
                    double latitude = add.getLatitude();
                //}
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    protected void startLocationUpdates()
    {
        // Log.i("onstartlocationupdates","HiiStart");

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        // Request location updates

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);

    }

    private void setUpMapIfNeeded()
    {
// Do a null check to confirm that we have not already instantiated the map.
        if (mGoogleMap == null)
        {
            Log.i("mapnull","mapnull");
            SupportMapFragment mapFragment1 = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mGoogleMap = mapFragment1.getMap();
           // mapView = mapFragment1.getView();
            mapFragment1.getMapAsync(this);
            TurnGpsOn();

            // Check if we were successful in obtaining the map.
            if (mGoogleMap != null)
            {
                mGoogleMap.setMyLocationEnabled(true);
                Log.i("mGoogleMap","Map Exist");
            }
        }
    }

    public void TurnGpsOn()
    {
        if (mGoogleApiClient == null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(ActivityRecognition.API)
                    .build();
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
                            //   Log.i("GPSON ", "GPSON");
                            getLocation();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(MapActivity.this, REQUEST_CHECK_SETTINGS);
                                isgpscheck = true;
                                // Log.i("Disable GPS ", "Disable GPS");
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                                // Log.i("IntentSender ", "SendIntent");
                            }

                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //  Log.i("SETTINGS_CHANGE", "SETTINGSGPS");
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
    private void updateValuesFromBundle(Bundle savedInstanceState)
    {

      //  Log.i("updateValuesFromBundle", "updateValuesFromBundle");

        if (savedInstanceState != null)
        {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES))
            {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
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
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING))
            {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }
            updateUI();
        }
    }


    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest()
    {
       // Log.i("createLocationRequest","createLocationRequest");
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

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    protected void buildLocationSettingsRequest()
    {
        //Log.i("buildLocationSettingsRequest","buildLocationSettingsRequest");

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
        builder.setAlwaysShow(true);

    }

    /**
     * Check if the device's location settings are adequate for the app's needs using the
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} method, with the results provided through a {@code PendingResult}.
     */
    protected void checkLocationSettings()
    {

       // Log.i("checkLocationSettings","checkLocationSettings");
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    /**
     * The callback invoked when
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} is called. Examines the
     * {@link com.google.android.gms.location.LocationSettingsResult} object and determines if
     * location settings are adequate. If they are not, begins the process of presenting a location
     * settings dialog to the user.
     */
    @Override
    public void onResult(LocationSettingsResult locationSettingsResult)
    {
       // Log.i("locationSettingsResult","locationSettingsResult");
        final Status status = locationSettingsResult.getStatus();

        switch (status.getStatusCode())
        {
            case LocationSettingsStatusCodes.SUCCESS:
              //  Log.i(TAG, "All location settings are satisfied.");
            //    getLocation();
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
              /*  Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");
*/
                try {

                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().

                    status.startResolutionForResult(MapActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e)
                {
                   // Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        //  Log.i(TAG, "User agreed to make required location settings changes.");
                        getLocation();
                        startLocationUpdates();
//                        ed_auto_drop.setEnabled(false);
                        isgpscheck = false;
                        if (Lat > 0.0 && Long > 0.0)
                        {
                            //  Log.i("sendLocation","sendLocation");
                            sendclientLocation();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        // Log.i("ResultCancelled","Cancelled");
                        if (isgpscheck)
                        {
                            Intent startMain = new Intent(Intent.ACTION_MAIN);
                            startMain.addCategory(Intent.CATEGORY_HOME);
                            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(startMain);
                            finish();
                        }

                        // Log.i(TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }

        if (requestCode == 1) {
            //  Log.i("case_pick", "case_pick");
            if (resultCode == RESULT_OK) {
                place_pick = PlaceAutocomplete.getPlace(this, data);
//                Log.i("place_pick", "" + place_pick);

                if (place_pick != null) {
                    stoplocupdate();
                    ed_auto_pick.setText(place_pick.getAddress().toString());
                    //  Log.i("location_name11", ed_auto_pick.getText().toString());
                    txtPick.setText(place_pick.getAddress().toString());
                    LatLng latLong = place_pick.getLatLng();
                    // Log.i("latLong", "" + latLong);
                    Lat = latLong.latitude;
                    // Log.i("pick_lat", "" + Lat);
                    Long = latLong.longitude;
                    // Log.i("pick_long", "" + Long);

                    pick_LatLong = new LatLng(Lat, Long);
                    // Log.i("pick_LatLong", "" + pick_LatLong);
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    try {
                        addresses = geocoder.getFromLocation(Lat, Long, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        //  Log.i("address",""+addresses);
                        if (addresses != null && addresses.size() > 0)
                        {
                            city = addresses.get(0).getLocality();
                            Log.i("citypickup", city);

                        }
                    } catch (IOException e) {
                        // Log.e("exception", e.toString());
                    }


//                    city = place_pick.getLocale().toString();
//                    Log.i("city", "" + city);
                    pickuplocation = ed_auto_pick.getText().toString();
                    //  Log.i("pickuplocation", "" + pickuplocation);
                    searchstatus = "1";
                    sendclientLocation();

                    CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(latLong, 15);
                    mGoogleMap.moveCamera(cameraPosition);
                    mGoogleMap.animateCamera(cameraPosition);
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                // Log.i("status", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                //  Log.i("resultcode",""+resultCode);
                // The user canceled the operation.
            }
        }

        if (requestCode == 2) {
            //  Log.i("case_drop", "case_drop");
            if (resultCode == RESULT_OK) {
                place_drop = PlaceAutocomplete.getPlace(this, data);
//                Log.i("place_drop", "" + place_drop);

                if (place_drop != null)
                {
                    stoplocupdate();
                    ed_auto_drop.setText(place_drop.getAddress().toString());
                    //  Log.i("location_name11", ed_auto_drop.getText().toString());
                    txtDrop.setText(place_drop.getAddress().toString());
                    LatLng latLong1 = place_drop.getLatLng();
                    //  Log.i("latLong1", "" + latLong1);
                    Droplat = latLong1.latitude;
                    //  Log.i("drop_lat", "" + drop_lat);
                    Droplong = latLong1.longitude;
                    //  Log.i("drop_long", "" + drop_long);
                    drop_LatLong = new LatLng(Droplat, Droplong);
                    //  Log.i("drop_LatLong", "" + drop_LatLong);

                    CameraUpdate cameraPosition1 = CameraUpdateFactory.newLatLngZoom(latLong1, 15);
                    mGoogleMap.moveCamera(cameraPosition1);
                    mGoogleMap.animateCamera(cameraPosition1);
                }
            }
            else if (resultCode == PlaceAutocomplete.RESULT_ERROR)
            {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                // Log.i("status", status.getStatusMessage());

            }
            else if (resultCode == RESULT_CANCELED)
            {
                // The user canceled the operation.
            }
        }

        if (requestCode == 3)
        {
            if (resultCode == RESULT_OK)
            {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Droplocation = place.getAddress().toString();

                if (isestimateready)
                {
                    Book.setVisibility(View.GONE);
                    LatLng Latlng = place.getLatLng();
                    //    Log.i("Latlng", "" + Latlng);
                    Droplat = Latlng.latitude;
                    //    Log.i("walujlat", "" + Droplat);
                    Droplong = Latlng.longitude;
                    //    Log.i("walujlong", "" + Droplong);

                    if (mCurrLocationMarker != null)
                    {
                        mCurrLocationMarker.remove();
                    }

                    /*BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.location_mark_icon_drop);
                    MarkerOptions options = new MarkerOptions();
                    options.position(Latlng);
                    options.title("Drop Location");
                    options.snippet("Latitude:" + Droplat + ",Longitude:" + Droplong);
                    options.icon(icon);
                    mCurrLocationMarker = mGoogleMap.addMarker(options);*/

//                    mGoogleMap.getUiSettings().setZoomControlsEnabled(false);

                    sendrideestimate();
                    Book.setVisibility(View.GONE);

                    LayoutInflater layoutInflater = (LayoutInflater) MapActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //ERRORS HERE!!
                    View popupView = layoutInflater.inflate(R.layout.rideestimate, null);
                    final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    popupWindow.setFocusable(true);
                    popupWindow.update();
                    LinearLayout Drop_Layout = (LinearLayout) popupView.findViewById(R.id.Droplocation);
                    TextView txt_DropLocation = (TextView) popupView.findViewById(R.id.txt_drop_location);
                    Picuplocation = (TextView) popupView.findViewById(R.id.pick_add);
                    Droploaction = (TextView) popupView.findViewById(R.id.drop_add);
                    Estimatecharges1 = (TextView) popupView.findViewById(R.id.estimate1);
                    Estimatecharges2 = (TextView) popupView.findViewById(R.id.estimate2);

                    Estimatecharges1.setText(Approximatefaremin + "-");
                    // int app = 20 + Integer.parseInt(Approximatefare);
                    Estimatecharges2.setText(Approximatefaremax);
                    Cancelrideestimate = (ImageButton) popupView.findViewById(R.id.cancelrideestimate);
                    Cancelrideestimate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            Book.setVisibility(View.GONE);
                        }
                    });

                    Drop_Layout.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v) {
                            try {
                                popupWindow.dismiss();
                                Book.setVisibility(View.GONE);
                                //   Log.i("Autcomplete", "Autocm");
                                Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setBoundsBias((new LatLngBounds(
                                        new LatLng(19.8036125, 75.1932621),
                                        new LatLng(19.9365798, 75.4171406))))
                                        .build(MapActivity.this);

                                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODEDROP);
                            } catch (GooglePlayServicesRepairableException e)
                            {
                                // TODO: Handle the error.
                            } catch (GooglePlayServicesNotAvailableException e)
                            {
                                // TODO: Handle the error.
                            }

                            isestimateready = true;
                            Book.setVisibility(View.GONE);
                        }
                    });
                    Picuplocation.setText(pickuplocation);
                    Droploaction.setText(Droplocation);
                    popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                    Book.setVisibility(View.GONE);
                }

                book_dropLocation.setText(Droplocation);
            }
            else if (resultCode == PlaceAutocomplete.RESULT_ERROR)
            {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                // Log.i("status", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        if (requestCode == 4)
        {
            if (resultCode == RESULT_OK)
            {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Droplocation = place.getAddress().toString();

                if (isestimateready)
                {
                    Book.setVisibility(View.GONE);
                    LatLng Latlng = place.getLatLng();
                    //    Log.i("Latlng", "" + Latlng);
                    Droplat = Latlng.latitude;
                    //    Log.i("walujlat", "" + Droplat);
                    Droplong = Latlng.longitude;
                    //    Log.i("walujlong", "" + Droplong);

                    if (mCurrLocationMarker != null)
                    {
                        mCurrLocationMarker.remove();
                    }

                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.location_mark_icon_drop);
                    MarkerOptions options = new MarkerOptions();
                    options.position(Latlng);
                    options.title("Drop Location");
                    options.snippet("Latitude:" + Droplat + ",Longitude:" + Droplong);
                    options.icon(icon);
                    mCurrLocationMarker = mGoogleMap.addMarker(options);

                    mGoogleMap.getUiSettings().setZoomControlsEnabled(false);

                    if (book_dropLayout.isEnabled())
                    {
                        builder = new LatLngBounds.Builder();
                        builder.include(pick_LatLong);
                        builder.include(Latlng);
                        bounds = builder.build();

                        int padding = 50; // offset from edges of the map in pixels
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mGoogleMap.animateCamera(cu);
                    }

                    book_dropLocation.setText(Droplocation);
                }
                else if (resultCode == PlaceAutocomplete.RESULT_ERROR)
                {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    // TODO: Handle the error.
                    // Log.i("status", status.getStatusMessage());

                }
                else if (resultCode == RESULT_CANCELED)
                {
                    // The user canceled the operation.
                }
            }
        }
    }

    public void sendrideestimate()
    {
        try
        {
            //  String url = "http://jaidevcoolcab.cabsaas.in/sandbox/index/signUp/?firstName=sandip&lastName=tathe&email=rohit@info.com&mobile=9226919759&password=sandip&confirmpassword=sandip&otp=&ApiKey=S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47&UserIPAddress=127.0.0.1&UserID=1212&UserAgent=Mozilla&responsetype=2";
            String urlnew =""+Url+"/rideEstimate/?";
            //  String APIKEY ="PVU1ZE-ZE4TPC-5IXWAJ-P2E6ZE-QONPEC-4IUGWD";
            String uri = Uri.parse(urlnew)
                    .buildUpon()
                    .appendQueryParameter("pickupLat", String.valueOf(Lat))
                    .appendQueryParameter("pickupLong",String.valueOf(Long))
                    .appendQueryParameter("dropLat", String.valueOf(Droplat))
                    .appendQueryParameter("dropLong", String.valueOf(Droplong))
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype", "2")
                    .build().toString();

         //   Log.i("uri", uri);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
            // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
         //   Log.i("response", "" + response);
            myJson = response;

        //    Log.i("myJson",""+myJson);
            try
            {
                JSONArray jsonArray= new JSONArray(myJson);
                JSONObject jsonObj = jsonArray.getJSONObject(0);
                Approximatefaremin = jsonObj.getString(TAG_approximateFareMin);
           //     Log.i("Approximatefare",""+Approximatefaremin);
                Approximatefaremax = jsonObj.getString(TAG_approximateFareMax);

            }
            catch (JSONException e)
            {

            }
        }
        catch (IOException e)
        {
          //  Log.i("Error",""+e.toString());
        }
    }

    /**
     * Handles the Start Updates button and requests start of location updates. Does nothing if
     * updates have already been requested.
     */
    public void startUpdatesButtonHandler(View view) {
        checkLocationSettings();
    }

    /**
     * Handles the Stop Updates button, and requests removal of location updates.
     */
    public void stopUpdatesButtonHandler(View view)
    {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        stopLocationUpdates();
    }

    /**
     * Updates all UI fields.
     */
    private void updateUI()
    {
        setButtonsEnabledState();
        updateLocationUI();
    }

    public void alert()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MapActivity.this);
        dialog.setMessage(getApplicationContext().getResources().getString(R.string.gps_network_not_enabled));
        dialog.setPositiveButton(getApplicationContext().getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
                // TODO Auto-generated method stub
                Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(myIntent);

                //get gps
            }
        });
        dialog.setNegativeButton(getApplicationContext().getString(R.string.Cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                // TODO Auto-generated method stub

            }
        });
        dialog.show();
    }


    /**
     * Disables both buttons when functionality is disabled due to insuffucient location settings.
     * Otherwise ensures that only one button is enabled at any time. The Start Updates button is
     * enabled if the user is not requesting location updates. The Stop Updates button is enabled
     * if the user is requesting location updates.
     */
    private void setButtonsEnabledState()
    {
        if (mRequestingLocationUpdates)
        {
           // mStartUpdatesButton.setEnabled(false);
           // mStopUpdatesButton.setEnabled(true);
        }
        else
        {
           // mStartUpdatesButton.setEnabled(true);
           // mStopUpdatesButton.setEnabled(false);
        }
    }

    /**
     * Sets the value of the UI fields for the location latitude, longitude and last update time.
     */


    public void getLocation()
    {
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
//        location = locationManager.getLastKnownLocation(bestProvider);
        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
       // Log.i("location", "" + location);

        if (location != null)
        {
            Log.i("updateLocationUI", "startLocationUpdates");
            Lat = location.getLatitude();
           Log.i("Latitude", "" + location.getLatitude());
            Long = location.getLongitude();
            Log.i("Longitude", "" + location.getLongitude());

            LatLng latLng1 = new LatLng(Lat, Long);
            Log.i("latLng1", "" + latLng1);

            CameraUpdate cameraPosition1 = CameraUpdateFactory.newLatLngZoom(latLng1, 15);
            mGoogleMap.animateCamera(cameraPosition1);
        }
    }

    private void updateLocationUI()
    {
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null)
        {

            Lat = location.getLatitude();
            // Log.i("Latitude",""+location.getLatitude());
            Long = location.getLongitude();
             // Log.i("Longitude", "" + location.getLongitude());

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
          //  Log.i("latLng", "" + latLng);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.location_mark_icon);
            markerOptions.icon(icon);
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates()
    {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        LocationServices.FusedLocationApi.removeLocationUpdates
                (mGoogleApiClient, this).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = false;
                setButtonsEnabledState();
            }
        });
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.i("onpause", "onpause");

        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (cd.isConnectingToInternet(MapActivity.this))
        {
            if(mGoogleApiClient==null)
            {
                TurnGpsOn();
            }

        }
    }

    @Override
    protected void onResume()
    {
        Log.i("OnResume", "OnResume");
        super.onResume();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        if(mGoogleApiClient==null || mGoogleMap ==null)
        {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        isMapReady = true;

        Log.i("Ready", "Google Map");
        mGoogleMap = googleMap;

        Log.i("Ready", "Google Map_ready");
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setOnMapClickListener(this);
        //mGoogleMap.setOnMapLongClickListener(this);
        mGoogleMap.setOnCameraChangeListener(this);
        Log.i("Ready", "Google Map_load");

        /*if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null)
        {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }
*/
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
          //Log.i("Permission","permission");
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
            {
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                getLocation();
            }
        }
        else
        {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
            getLocation();
        }
    }

    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(ActivityRecognition.API)
                .build();
        mGoogleApiClient.connect();


       /* LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient*/
        //**************************
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint)
    {
        Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
      /*  if (getmLastLocation == null)
        {
            getmLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateLocationUI();
        }*/

        Log.i(TAG, "Connected to GoogleApiClient");

        if (mLastLocation == null)
        {
           // Log.i("mLastLocationconnected",""+mLastLocation);

            if (mGoogleApiClient != null)
            {
                try
                {
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                } catch (SecurityException e)
                {
                    Log.i("SecurityException", "" + e.toString());
                }

            }
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            //getLocation();
        }


    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location)
    {
        Log.i("onLocationChanged","onLocationChanged");

        mLastLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
      //  updateUI();

        if (mCurrLocationMarker != null)
        {
            mCurrLocationMarker.remove();
        }

        if (place_pick == null)
        {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            Lat = location.getLatitude();
            Long = location.getLongitude();
            Log.i("latLng123", "" + latLng);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_mark_icon));
            sendclientLocation();

            CameraUpdate cameraPosition1 = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            mGoogleMap.animateCamera(cameraPosition1);
        }

        if (mGoogleApiClient != null)
        {
            Log.i("remove_location", "remove_location");
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
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

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

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
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
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
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                }
                else
                {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                //    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onStart()
    {
       //Log.i("onStart", "onStart");
        if(cd.isConnectingToInternet(MapActivity.this))
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();
            mGoogleApiClient.connect();

            super.onStart();
        }
        else
        {
          //  cd.showNetDisabledAlertToUser(MapActivity.this);
            super.onStart();
        }
    }

    /*@Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(receiver!=null)
        {
            Log.i("reciever","reciever");
            unregisterReceiver(receiver);
        }
    }*/

    @Override
    protected void onStop()
    {
       // Log.i("onStop", "onStop");
        if(cd.isConnectingToInternet(MapActivity.this))
        {
            //  unregisterReceiver(receiver);
              super.onStop();
        }

        else
        {
            // unregisterReceiver(receiver);
             super.onStop();
        }


    }

    public void stoplocupdate()
    {
        if (mGoogleApiClient != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }


    public void  sendclientLocation()
    {
        try {
            //  String url = "http://jaidevcoolcab.cabsaas.in/sandbox/index/signUp/?firstName=sandip&lastName=tathe&email=rohit@info.com&mobile=9226919759&password=sandip&confirmpassword=sandip&otp=&ApiKey=S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47&UserIPAddress=127.0.0.1&UserID=1212&UserAgent=Mozilla&responsetype=2";
            String urlnew =""+Url+"/vehicleLocation/?";
            // String APIKEY ="PVU1ZE-ZE4TPC-5IXWAJ-P2E6ZE-QONPEC-4IUGWD";
            String uri = Uri.parse(urlnew)
                    .buildUpon()
                    .appendQueryParameter("uId", Usersessionid)
                    .appendQueryParameter("latitude", String.valueOf(Lat))
                    .appendQueryParameter("longitude", String.valueOf(Long))
                    .appendQueryParameter("pCity", city)
                    .appendQueryParameter("searchStatus", searchstatus)
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype", "2")
                    .build().toString();

            Log.i("uri", uri);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            Log.i("response", "" + response);
            myJson = response;
            VehicleSearchResults();
        }
        catch (IOException e)
        {
          //  Log.i("Error", "" + e.toString());
        }
    }

    public  void VehicleSearchResults()
    {
        try
        {
           // JSONArray json = new JSONArray(myJson);
             JSONObject jsonObj = new JSONObject(myJson);
             //Log.i("jsonObj", "" + jsonObj);
             Vehicle = jsonObj.getJSONArray(TAG_driverVehicleLocationResult);
            //Log.i("Vehicle", "" + Vehicle);
             myJson = String.valueOf(Vehicle);
            //Log.i("myJson", "" + myJson);

            if(myJson.equals("[]"))
            {
                AvailTime.setText("No Cabs Found");
                Book.setVisibility(View.GONE);
            }
            else
            {
                if(myJson!=null)
                {
                    try
                    {
                        JSONObject jsonObject1 = Vehicle.getJSONObject(0);

                       // JSONObject jsonObject1 = c.getJSONObject(0);
                        String responsecode = jsonObject1.getString("responseCode");

                        if(responsecode.equals("0"))
                        {
                            final String msg = jsonObject1.getString("responseMessage");
                            final String Status_res_massge1 = msg.substring(2, msg.length() - 2);

                            AvailTime.setText(Status_res_massge1);
                            Book.setVisibility(View.GONE);

                        }
                        if(responsecode.equals("1"))
                        {
                            try
                            {
                                VehicleTarif = jsonObj.getJSONArray(TAG_driverVehicleTariffResult);
                                //Book.setVisibility(View.VISIBLE);
                                mGoogleMap.clear();
                                for (int i = 0; i < Vehicle.length(); i++)
                                {
                                    JSONObject c = Vehicle.getJSONObject(i);
                                    double Lat_vehicle = c.getDouble(TAG_Lat);
                                    //  Log.i("Lat_vehicle", "" + Lat_vehicle);
                                    double Long_vehicle = c.getDouble(TAG_Long);
                                    //  Log.i("Long_vehicle", "" + Long_vehicle);
                                    Distance = c.getDouble(TAG_approximatedistance);
                                    //  Log.i("isMapReady", "" + isMapReady);
                                    if (isMapReady)
                                    {
                                        //   Log.i("mapmarker", "markerplaced");
                                        Marker m1 = mGoogleMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(Lat_vehicle, Long_vehicle))
                                                .anchor(0.5f, 0.5f)
                                                .title("")
                                                .snippet("")
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.cab_icon)));
                                    }

                                    // Log.i("Json", "" + Vehicle);
                                    JSONObject c1 = Vehicle.getJSONObject(0);
                                    // Log.i("Jsonobject", "" + c1);
                                    Time = c1.getString(TAG_minu);
                                    Vehicleid = c1.getString(TAG_Vehicle);
                                    //  Log.i("Vehicleid", "" + Vehicleid);

                                    // Log.i("Time", "" + Time);
                                    if (Time.equals("0"))
                                    {
                                        AvailTime.setText("2 Min");
                                    }
                                    else
                                    {
                                        AvailTime.setText(Time + "Min");
                                    }
                                }
                            }
                            catch (JSONException j)
                            {
                                j.printStackTrace();
                            }


                            for (int i = 0; i < VehicleTarif.length(); i++)
                            {
                                JSONObject c1 = VehicleTarif.getJSONObject(0);
                                Basekm = c1.getString("minKm");
                                //  Log.i("Basekm", "" + Basekm);
                                BaseFare = c1.getString("basicRate");
                                //   Log.i("BaseFare", "" + BaseFare);
                                RideTime = c1.getString("minuteCharge");
                                Kmrate   = c1.getString("kmRate");

                                // Log.i("RideTime", "" + RideTime);
                                dryrunrate = c1.getDouble("dryRunRate");
                                //  Log.i("dryrunrate",""+dryrunrate);
                                dryRunKm = c1.getDouble("dryRunKm");
                                // Log.i("dryRunKm",""+dryRunKm);
                                dryRun = c1.getDouble("dryRun");
                                //  Log.i("dryRun",""+dryRun);
                                dryRunCharges = c1.getDouble("dryRunCharges");
                                //  Log.i("dryRunCharges",""+dryRunCharges);
                            }

                            Book.setVisibility(View.VISIBLE);
                        }
                    }
                    catch (JSONException j)
                    {
                        j.printStackTrace();
                    }
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void Bookingconfirm()
    {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        CurrentDate = mDay + "-" + (mMonth + 1) + "-" + mYear;
       // Log.i("Currentdate", "" + CurrentDate);

        try
        {
            //  String url = "http://jaidevcoolcab.cabsaas.in/sandbox/index/signUp/?firstName=sandip&lastName=tathe&email=rohit@info.com&mobile=9226919759&password=sandip&confirmpassword=sandip&otp=&ApiKey=S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47&UserIPAddress=127.0.0.1&UserID=1212&UserAgent=Mozilla&responsetype=2";
            String urlnew =""+Url+"/bookingConfirm/?";
            String uri = Uri.parse(urlnew)
                    .buildUpon()
                    .appendQueryParameter("uId", Usersessionid)
                    .appendQueryParameter("tDate", CurrentDate)
                    .appendQueryParameter("pCity", city)
                    .appendQueryParameter("CustomerLat", String.valueOf(Lat))
                    .appendQueryParameter("CustomerLong", String.valueOf(Long))
                    .appendQueryParameter("pickUpaddress", pickuplocation)
                    .appendQueryParameter("CustomerDropLat", String.valueOf(Droplat))
                    .appendQueryParameter("CustomerDropLong", String.valueOf(Droplong))
                    .appendQueryParameter("dropAddress", dropAddress)
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
                public void run() {
                    progressDialog.dismiss();

                    try {
                        if (myJson != null) {
                            JSONArray json = new JSONArray(response);
                            //Log.i("json", "" + json);
                            JSONObject jsonObject = json.getJSONObject(0);
                            String str = "4";
                            final String Message = jsonObject.getString("responseMessage");
                            final String responsecode = jsonObject.getString("responseCode");
                            final String Status_res_massge1 = Message.substring(2, Message.length() - 2);
                            if (responsecode.equals("1"))
                            {
                                try
                                {
                                    JSONArray json1 = new JSONArray(myJson);
                                    JSONObject jsonObject1 = json1.getJSONObject(0);
                                    BookingId = jsonObject1.getString("bookingId");
                                    Intent in = new Intent(MapActivity.this, MapActivity_cstmr.class);
                                    in.putExtra("BookingId", BookingId);
                                    in.putExtra("intent", "0");
                                    startActivity(in);
                                    unregisterReceiver(receiver);
                                    finish();

                                }
                                catch (JSONException j)
                                {
                                    j.printStackTrace();
                                }
                            }
                            if (responsecode.equals("0"))
                            {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapActivity.this);
                                alertDialog.setTitle("Not Confirmed");
                                alertDialog.setMessage(Status_res_massge1);
                                alertDialog.setCancelable(false);
                                alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent in = new Intent(getApplicationContext(), MapActivity.class);
                                        startActivity(in);
                                        finish();
                                    }
                                });
                                alertDialog.show();
                            }
                            if (responsecode.equals("3"))
                            {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapActivity.this);
                                alertDialog.setTitle("Blocked");
                                alertDialog.setMessage(Status_res_massge1);
                                alertDialog.setCancelable(false);
                                alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        sessionManager.logoutUser();
                                    }
                                });
                                alertDialog.show();
                            }
                        }
                    } catch (JSONException j) {
                        j.printStackTrace();
                    }
                }
            });
        }
        catch (IOException e)
        {
          //  Log.i("Error", "" + e.toString());
        }
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
                   .appendQueryParameter("bookingId", BookingId)
                   .appendQueryParameter("ApiKey", APIKEY)
                   .appendQueryParameter("UserID", "1212")
                   .appendQueryParameter("UserIPAddress", IPAddress)
                   .appendQueryParameter("UserAgent", "androidApp")
                   .appendQueryParameter("responsetype", "2")
                   .build().toString();

         //  Log.i("uri", uri);

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
            //   Log.i("json", "" + json);
               JSONObject jsonObject = json.getJSONObject(0);
               responsecode = jsonObject.getString("responseCode");

           }
           catch (JSONException j)
           {
               j.printStackTrace();
           }

           runOnUiThread(new Runnable()
           {
               public void run()
               {

                   if (responsecode.equals("1"))
                   {
                       try
                       {
                           progressDialog.dismiss();
//                           Bookinglayout.setVisibility(View.GONE);
                           DriverLayout.setVisibility(View.VISIBLE);
                           JSONArray json = new JSONArray(response);
                         //  Log.i("json", "" + json);
                           JSONObject jsonObject = json.getJSONObject(0);
                           final String Drivername = jsonObject.getString("driverName");
                           final String DriverMobile = jsonObject.getString("mobile");
                           final String vehiclename = jsonObject.getString("vehicleName");
                           final String vehiclenumber = jsonObject.getString("vehicleNumber");
                           final String Crnno = jsonObject.getString("crnNumber");
                           DriverName.setText(Drivername);
                           DriverNumber.setText(DriverMobile);
                           Vehiclename.setText(vehiclename);
                           Vehiclenumber.setText(vehiclenumber);
                           Cancelnumber.setText(Crnno);

                       } catch (JSONException j)
                       {
                           j.printStackTrace();
                       }
                   }
                   else
                   {
                       driverdetails();
                   }
               }
           });
       }
       catch (IOException e)
       {
         //  Log.i("Error", "" + e.toString());
       }
   }

    @Override
    public void onCameraChange(CameraPosition cameraPosition)
    {
        mGoogleMap.setOnMapLoadedCallback(this);
    }

    @Override
    public void onPlaceSelected(Place plac)
    {
        place = plac;
      //  Log.i("Place", "Place: " + plac.getLatLng());
        pickuplocation = plac.getAddress().toString();
     //   Log.i("pickuplocation", pickuplocation);

        autocompleteFragment.setText(pickuplocation);
        ((EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(12.0f);
        LatLng Latlng = plac.getLatLng();
      //  Log.i("Latlng", "" + Latlng);
        Lat = Latlng.latitude;
      //  Log.i("walujlat", "" + Lat);
        Long = Latlng.longitude;
      //  Log.i("walujlong", "" + Long);
        if (mCurrLocationMarker != null)
        {
            mCurrLocationMarker.remove();
        }

        mGoogleMap.clear();
        // mGoogleMap.setMyLocationEnabled(false);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.location_mark_icon);
       // Log.i("iconplaceselected","iconplaceselected");
        MarkerOptions options = new MarkerOptions();
        options.position(Latlng);
        options.title("Position");
        options.draggable(true);
        options.snippet("Latitude:" + Lat + ",Longitude:" + Long);
        options.icon(icon);

//        mGoogleMap.addMarker(options);

        CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(Latlng, 15);
        mGoogleMap.moveCamera(cameraPosition);
        mGoogleMap.animateCamera(cameraPosition);
        stopLocationUpdates();
          //  sendclientLocation();
    }

    @Override
    public void onError(Status status)
    {
       //Toast.makeText(getApplicationContext(),""+status,Toast.LENGTH_LONG).show();

    }

    @Override
    public void onMapClick(LatLng latLng)
    {
      //  Log.i("place_place_pick", ""+place_pick);
      //  Log.i("place_place_drop", ""+place_drop);
        if (place_pick == null)
        {
            if (ed_auto_pick.isEnabled())
            {
                LatLng position = mGoogleMap.getCameraPosition().target;
              //  Log.i("change_position", "" + position);
                Lat = latLng.latitude;
               // Log.i("Lat_onMapClick", "" + Lat);
                Long = latLng.longitude;
               // Log.i("Long_onMapClick", "" + Long);

                DecimalFormat dFormat = new DecimalFormat("#.#######");

                Lat = Double.valueOf(dFormat.format(Lat));
              //  Log.i("lat_six", "" + Lat);
                Long = Double.valueOf(dFormat.format(Long));
             //   Log.i("Long_six", "" + Long);

                pick_LatLong = new LatLng(Lat, Long);
              //  Log.i("pick_LatLong_six", "" + pick_LatLong);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(pick_LatLong));

                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(this, Locale.getDefault());
                try
                {
                    addresses = geocoder.getFromLocation(Lat, Long, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    //  Log.i("address",""+addresses);
                    if (addresses != null && addresses.size() > 0)
                    {
                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                      //  Log.i("address", address);

                        String address11 = addresses.get(0).getAddressLine(1);
                     //   Log.i("address11", address11);

                        String address12 = addresses.get(0).getAddressLine(2);
                      // Log.i("address12", address12);

                        city = addresses.get(0).getLocality();
                      //  Log.i("city", city);

                        String state = addresses.get(0).getAdminArea();
                        //Log.i("state", state);

                        String country = addresses.get(0).getCountryName();
                        //Log.i("country", country);

                        pickuplocation = address + ", " + address11 + ", " + city;
                        ed_auto_pick.setText(pickuplocation);
                        txtPick.setText(pickuplocation);
                    }
                }
                catch (IOException e) {
                   // Log.e("exception", e.toString());
                }
                if (Lat > 0.0&& Long > 0.0)
                {
                    searchstatus ="";
                    sendclientLocation();
                }

               // get_location.setVisibility(View.GONE);
            }
        }
        place_pick = null;

        if (place_drop == null)
        {
            if (ed_auto_drop.isEnabled())
            {
                Book.setVisibility(View.VISIBLE);
                LatLng position = mGoogleMap.getCameraPosition().target;
            //    Log.i("change_position", "" + position);
                Droplat = latLng.latitude;
           //     Log.i("Lat_onMapLoad", "" + Droplat);
                Droplong = latLng.longitude;
            //    Log.i("Long_onMapLoad", "" + Droplong);

                DecimalFormat dFormat = new DecimalFormat("#.#######");

                Droplat = Double.valueOf(dFormat.format(Droplat));
             //   Log.i("lat_six_drop", "" + Droplat);
                Droplong = Double.valueOf(dFormat.format(Droplong));
             //   Log.i("Long_six_drop", "" + Droplong);

                drop_LatLong = new LatLng(Droplat, Droplong);
             //   Log.i("drop_LatLong_six", "" + drop_LatLong);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(drop_LatLong));

                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(this, Locale.getDefault());
                try
                {
                    addresses = geocoder.getFromLocation(Droplat, Droplong, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    //  Log.i("address",""+addresses);
                    if (addresses != null && addresses.size() > 0)
                    {
                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                      //  Log.i("address", address);

                        String address11 = addresses.get(0).getAddressLine(1);
                       // Log.i("address11", address11);

                        String address12 = addresses.get(0).getAddressLine(2);
                      //Log.i("address12", address12);

                        city = addresses.get(0).getLocality();
                      //  Log.i("city", city);

                        String state = addresses.get(0).getAdminArea();
                        //Log.i("state", state);

                        String country = addresses.get(0).getCountryName();
                        //Log.i("country", country);

                        String Pickupaddress = address + ", " + address11 + ", " + city;
                        ed_auto_drop.setText(Pickupaddress);
                        txtDrop.setText(Pickupaddress);
                    }
                }
                catch (IOException e)
                {
                 //   Log.e("exception", e.toString());
                }
//                sendclientLocation();
            }
        }
        place_drop = null;
    }

   @Override
    public void onMapLoaded()
    {
        Log.i("onMapLoad", "OnMapLoad");
      //  Log.i("place_onMapLoad", ""+place_pick);
     //   Log.i("place_onMapLoad", ""+place_drop);


            if (place_pick == null)
            {
                if (ed_auto_pick.isEnabled())
                {
//                    Book.setVisibility(View.VISIBLE);

                    Farepopup.setVisibility(View.VISIBLE);
                    confirm.setVisibility(View.GONE);
                    FareEstimate.setVisibility(View.GONE);

                    LatLng position = mGoogleMap.getCameraPosition().target;
                //    Log.i("change_position", "" + position);
                    Lat = position.latitude;
                //    Log.i("Lat_onMapLoad", "" + Lat);
                    Long = position.longitude;
               //     Log.i("Long_onMapLoad", "" + Long);

                    DecimalFormat dFormat = new DecimalFormat("#.#######");

                    Lat = Double.valueOf(dFormat.format(Lat));
                //    Log.i("lat_six", "" + Lat);
                    Long = Double.valueOf(dFormat.format(Long));
                //    Log.i("Long_six", "" + Long);

                    pick_LatLong = new LatLng(Lat, Long);
                //    Log.i("pick_LatLong_six", "" + pick_LatLong);

                    finalizer = new Runnable()
                    {
                        public void run() {
                            Geocoder geocoder;
                            List<Address> addresses;
                            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            try {
                                addresses = geocoder.getFromLocation(Lat, Long, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                //  Log.i("address",""+addresses);
                                if (addresses != null && addresses.size() > 0)
                                {
                                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                    // Log.i("address", address);

                                    String address11 = addresses.get(0).getAddressLine(1);
                                    //   Log.i("address11", address11);

                                    String address12 = addresses.get(0).getAddressLine(2);
                                    //  Log.i("address12", address12);

                                    city = addresses.get(0).getLocality();
                                    //  Log.i("city", city);

                                    String state = addresses.get(0).getAdminArea();
                                    //Log.i("state", state);

                                    String country = addresses.get(0).getCountryName();
                                    //Log.i("country", country);

                                    pickuplocation = address + ", " + address11 + ", " + city;
                                    ed_auto_pick.setText(pickuplocation);
                                    txtPick.setText(pickuplocation);
                                  //  popupWindow.dismiss();
                                   // get_location.setVisibility(View.GONE);
                                    //Book.setVisibility(View.VISIBLE);
                                }
                            } catch (IOException e)
                            {
                                // Log.e("exception", e.toString());
                            }

                            if (Lat > 0.0 && Long > 0.0)
                            {
                                searchstatus ="";
                                sendclientLocation();
                            }
                        }
                    };

                    if (mMapIsTouched)
                    {
                        timeoutHandler.postDelayed(finalizer, 2000);
                    }
                    else
                    {
                        timeoutHandler.postDelayed(finalizer, 100);
                    }
                }
            }

            place_pick = null;
//        mapLoad = true;

            if (place_drop == null)
            {
                if (ed_auto_drop.isEnabled())
                {
//                    Book.setVisibility(View.VISIBLE);
                    Farepopup.setVisibility(View.VISIBLE);
                    confirm.setVisibility(View.GONE);
                    FareEstimate.setVisibility(View.GONE);

                    LatLng position = mGoogleMap.getCameraPosition().target;
                  //  Log.i("change_position", "" + position);
                    Droplat = position.latitude;
                 //   Log.i("Lat_onMapLoad_drop", "" + Droplat);
                    Droplong = position.longitude;
                 //   Log.i("Long_onMapLoad_drop", "" + Droplong);

                    DecimalFormat dFormat = new DecimalFormat("#.#######");

                    Droplat = Double.valueOf(dFormat.format(Droplat));
                 //   Log.i("lat_six_drop", "" + Droplat);
                    Droplong = Double.valueOf(dFormat.format(Droplong));
                 //   Log.i("Long_six_drop", "" + Droplong);

                    drop_LatLong = new LatLng(Droplat, Droplong);
                  //  Log.i("drop_LatLong_six", "" + drop_LatLong);

                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    try
                    {
                        addresses = geocoder.getFromLocation(Droplat, Droplong, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        //  Log.i("address",""+addresses);
                        if (addresses != null && addresses.size() > 0)
                        {
                            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            //  Log.i("address", address);

                            String address11 = addresses.get(0).getAddressLine(1);
                            //  Log.i("address11", address11);

                            String address12 = addresses.get(0).getAddressLine(2);
                            //   Log.i("address12", address12);

                            city = addresses.get(0).getLocality();
                            //Log.i("city", city);

                            String state = addresses.get(0).getAdminArea();
                            //Log.i("state", state);

                            String country = addresses.get(0).getCountryName();
                            //Log.i("country", country);

                            dropAddress = address + ", " + address11 + ", " + city;
                            ed_auto_drop.setText(dropAddress);
                            txtDrop.setText(dropAddress);
                            //Book.setVisibility(View.VISIBLE);
                        }
                    }
                    catch (IOException e)
                    {
                     //   Log.e("exception", e.toString());
                    }
//                sendclientLocation();
                }
            }
            place_drop = null;
    }


    @Override
    public void onMapLongClick(LatLng latLng)
    {
       // Log.i("latLong986", ""+latLng);
        if (mCurrLocationMarker != null)
        {
            mCurrLocationMarker.remove();
        }

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.pick_drop_loc_icon);
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        options.title("Map Click");
        options.draggable(true);
        options.snippet("Latitude:" + Lat + ",Longitude:" + Long);
        options.icon(icon);
//        mCurrLocationMarker = mGoogleMap.addMarker(options);

       // Log.i("latLong1515", "" + latLng);
    }

    @Override
    public void onBackPressed()
    {
        //checkversion();
        Intent intent = getIntent();
        finish();
        startActivity(intent);
       // return;
    }

    public void alertDialoge()
    {
       // Log.i("Alert", "Alert");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Extra Dry run charges Rs  "+ dryrunrate + "/Km will be applicable after "+ dryRunKm + " Km")
                .setTitle("Confirmed Dry Run")
                .setCancelable(false)
                .setPositiveButton(" Yes ", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Book.setVisibility(View.GONE);
                        confirm.setVisibility(View.VISIBLE);
                        Farepopup.setVisibility(View.GONE);
                        FareEstimate.setVisibility(View.VISIBLE);
                    }
                });

        alertDialogBuilder.setNegativeButton(" No ", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                mark_img.setVisibility(View.VISIBLE);
                Book.setEnabled(true);
                CameraUpdate cameraPosition = CameraUpdateFactory.zoomBy(-3);
                mGoogleMap.animateCamera(cameraPosition);
                ed_auto_pick.setEnabled(true);
                ed_auto_drop.setEnabled(false);
                FAB.setVisibility(View.VISIBLE);
                drop_arrow.setVisibility(View.GONE);
                drop.setVisibility(View.GONE);
                ed_pick.setVisibility(View.GONE);
                Autoapi.setVisibility(View.VISIBLE);
                ed_drop.setVisibility(View.VISIBLE);
                pick_arrow.setVisibility(View.VISIBLE);
                Book_Cnfrm.setVisibility(View.GONE);
                book_dropLayout.setVisibility(View.GONE);

                if (mCurrLocationMarker != null)
                {
                    mCurrLocationMarker.remove();
                }

                dialog.cancel();
            }
        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    public void checkversion()
    {
        try
        {
            String urlnew =""+Url+"/versionCheck/?";
            //  String APIKEY ="PVU1ZE-ZE4TPC-5IXWAJ-P2E6ZE-QONPEC-4IUGWD";
            String uri = Uri.parse(urlnew)
                    .buildUpon()
                    .appendQueryParameter("ptopVersion", String.valueOf(version_code))
                    .appendQueryParameter("uId", Usersessionid)
                    .appendQueryParameter("type", "0")
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype", "2")
                    .build().toString();

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            myJson = response;

            runOnUiThread(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        JSONArray json = new JSONArray(myJson);
                    //    Log.i("json", "" + json);
                        JSONObject jsonObject = json.getJSONObject(0);
                        String resp = jsonObject.getString("responseCode");
                        String respmsg = jsonObject.getString("responseMessage");
                        final String Status_res_massge1 = respmsg.substring(2, respmsg.length() - 2);

                        if(resp.equals("0"))
                        {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapActivity.this);
                            alertDialogBuilder.setMessage(Status_res_massge1)
                                    .setTitle("update app")
                                    .setCancelable(false)
                                    .setPositiveButton(" Yes ", new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id="+Packagename+"&hl=en"));
                                            startActivity(intent);
                                            finish();

                                        }
                                    });

                            alertDialogBuilder.setNegativeButton(" No ", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                                    startMain.addCategory(Intent.CATEGORY_HOME);
                                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(startMain);
                                    finish();
                                }
                            });

                            AlertDialog alert = alertDialogBuilder.create();
                            alert.show();
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
}



