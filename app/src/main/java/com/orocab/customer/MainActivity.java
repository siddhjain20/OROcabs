package com.orocab.customer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";
    // public static final String LName = "Lastname";
    public static final String Email = "emailKey";
    public static final String Tag_Sign = "responseCode";
    public static final String Tag_Subuserid = "subuserid";
    public static final String Tag_uid = "uId";
    public static final String TagprefernceBalance = "balance";
    public static final String Tagemail = "email";
    public static final String Tagmobile = "mobile";
    public static final String Tagname = "firstName";
    public static final String TagLast = "Lastname";

    int Cashwalletbalance;
    String uId,fname,Lname;
    String LoginStatus,Useremail,Usermobile;


    SharedPreferences sharedpreferences;




    Button SignInButton;
    EditText UserMob,Userpwd;
    TextView SignupLink;
    LinearLayout Passwordlink;
    ProgressDialog dialog;

    UserSessionManager session;
    ConnectionDetector cd;
    String Url,IPAddress,APIKEY;
    ImageView FaceBooklogin;
    int version_code;
    String android_id;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
//    BroadcastReceiver mBroadcastReceiver;
    String token,Device_id,Androidversion;
    Date Start,End;
    Permisions pm ;

    private static final int ALL_PERMISSION_CODE = 27;
    List<String> permissions = new ArrayList<String>();
    String[] params;
    private static final int PERMISSION_REQUEST_CODE = 200;
    boolean phoneStateAccepted = false;
    int currentapiVersion;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cd = new ConnectionDetector(getApplicationContext());
        Url = cd.geturl();
        APIKEY = cd.getAPIKEY();
        IPAddress = cd.getLocalIpAddress();
        pm = new Permisions(getApplicationContext());



        if (cd.isConnectingToInternet(MainActivity.this))
        {

             currentapiVersion = Build.VERSION.SDK_INT;
            //Log.i("currentapiVersion",""+currentapiVersion);
            if (currentapiVersion >= 23)
            {
                // requesting all permisions at same time
                if(checkPermission())
                {
                    //Log.i("Log","permision granted");
                    //Toast.makeText(MainActivity.this, "permision granted", Toast.LENGTH_SHORT).show();
                    deviceData();
                }
                else
                {
                    //Log.i("Log","permision not granted");
                    //request permisions
                    //Toast.makeText(MainActivity.this, "permision  not granted", Toast.LENGTH_SHORT).show();

                    requestPermission();
                }
            }
            else
            {
                deviceData();
            }

                session = new UserSessionManager(getApplicationContext());
                sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

                session = new UserSessionManager(getApplicationContext());
                if (session.isUserLoggedIn())
                {
                    Intent intent1 = new Intent(getApplicationContext(), MapActivity.class);
                    startActivity(intent1);
                    finish();
                }

                FaceBooklogin = (ImageView) findViewById(R.id.facebooklogin);
                FaceBooklogin.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent in = new Intent(getApplicationContext(), Facebook.class);
                        startActivity(in);
                    }
                });


                UserMob = (EditText) findViewById(R.id.Username);
                Userpwd = (EditText) findViewById(R.id.Password);
                Passwordlink = (LinearLayout) findViewById(R.id.passwordlink);
                SignupLink = (TextView) findViewById(R.id.Signuplink);
                SignInButton = (Button) findViewById(R.id.SignIn);
                SignInButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (UserMob.getText().toString().trim().equals("") && Userpwd.getText().toString().trim().equals(""))
                        {
                            UserMob.setError("Enter Mobile or Email");
                            Userpwd.setError("Enter password");
                        }
                        else
                        {

                            dialog = ProgressDialog.show(MainActivity.this, "", "Signing In... Please Wait", true);

                            new Thread(new Runnable()
                            {
                                public void run()
                                {


                                    Sign_in();
                                }
                            }).start();

                        }
                    }
                });

                SignupLink.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent in = new Intent(getApplicationContext(), Signup.class);
                        in.putExtra("token", token);
                        startActivity(in);
                    }
                });
                Passwordlink.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent in = new Intent(getApplicationContext(), Forgotpassword.class);
                        startActivity(in);
                        finish();
                    }
                });



            }

            else
            {
                cd.showNetDisabledAlertToUser(MainActivity.this);
            }


    }

    public void deviceData()
    {




        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        // Log.i("Androidid", "" + android_id);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Device_id = telephonyManager.getDeviceId();
        //  Log.i("mobile_id", "" + Device_id);

        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        int version = Build.VERSION.SDK_INT;
        String versionRelease = Build.VERSION.RELEASE;
        Androidversion = manufacturer + model + version + versionRelease;

        try
        {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            //string version_code = info.versionName;
            version_code = info.versionCode;
            // Log.i("version_code", "" + version_code);
        }
        catch (PackageManager.NameNotFoundException e)
        {

        }

        //Checking play service is available or not
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        Log.i("resultCode", "" + resultCode);
        //if play service is not available
        if (ConnectionResult.SUCCESS != resultCode)
        {
            Log.i("SUCCESS", "SUCCESS");
            //If play service is supported but not installed
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                Log.i("isUserRecoverableError", "isUserRecoverableError");
                //Displaying message that play service is not installed
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());

                //If play service is not supported
                //Displaying an error message
            }

            else
            {
                 Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }

            //If play service is available
        }
        else
        {
            //Starting intent to register device
            Log.i("itent", "itent");
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }
        //// end push

        ///code push notification
        //Initializing our broadcast receiver
        mRegistrationBroadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                //If the broadcast has received with success
                //that means device is registered successfully
//                GCMRegistrationIntentService abc= new GCMRegistrationIntentService();
//                abc();
         //       Log.i("onReceive", "onReceive:");

                if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS))
                {
                    //Getting the registration token from the intent
                    token = intent.getStringExtra("token");
                    Log.i("token", "" + token);

                }
                else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR))
                {
                    //   Toast.makeText(getApplicationContext(), "GCM registration error!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    // Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        };
    }


    public void requestAllPermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.RECEIVE_SMS)
                && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_CONTACTS)
                && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_GSERVICES))
        {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission

            Log.i("Log11","ask for permisions");
        }

        //And finally ask for the permission

        Log.i("Log12","permisions to req :"+ permissions.toString());
        ActivityCompat.requestPermissions(MainActivity.this, permissions.toArray(new String[permissions.size()]),ALL_PERMISSION_CODE);

    }

    public boolean isAllPermAllowed()
    {
        String locationPermission = android.Manifest.permission.ACCESS_FINE_LOCATION;
        String locationpermissioncoarse = android.Manifest.permission.ACCESS_COARSE_LOCATION;
        String ContactsPermission = android.Manifest.permission.READ_CONTACTS;
        String storagePermission = android.Manifest.permission.READ_EXTERNAL_STORAGE;
        String smsPermission = android.Manifest.permission.RECEIVE_SMS;
        String GCMgsf = android.Manifest.permission.WRITE_GSERVICES;
        String Phonestate = android.Manifest.permission.READ_PHONE_STATE;

        //Getting the permission status
        int result1 = ContextCompat.checkSelfPermission(MainActivity.this,  android.Manifest.permission.RECEIVE_SMS);
        int result2 = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int result3 = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_CONTACTS);
        int result4 = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int result5 = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_GSERVICES);
        int result6=  ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_PHONE_STATE);
        int result7 = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);

        Log.i("Log1",permissions.toString());

        if (result1 != PackageManager.PERMISSION_GRANTED) {
            permissions.add(smsPermission);
            Log.i("Log2",permissions.toString());
        }

        if (result2 != PackageManager.PERMISSION_GRANTED)
        {
            permissions.add(locationPermission);
            Log.i("Log3",permissions.toString());
        }

        if (result3 != PackageManager.PERMISSION_GRANTED)
        {
            permissions.add(ContactsPermission);
            Log.i("Log4",permissions.toString());
        }

        if (result4 != PackageManager.PERMISSION_GRANTED)
        {
            permissions.add(storagePermission);
            Log.i("Log5",permissions.toString());
        }

        if (result5 != PackageManager.PERMISSION_GRANTED)
        {
            permissions.add(GCMgsf);
            Log.i("Log6",permissions.toString());
        }

        if (result6 != PackageManager.PERMISSION_GRANTED)
        {
            permissions.add(Phonestate);
            Log.i("Log7",permissions.toString());
        }

        if (result7 != PackageManager.PERMISSION_GRANTED) {
            permissions.add(locationpermissioncoarse);
            Log.i("Log8",permissions.toString());
        }

        if (!permissions.isEmpty())
        {
            params = permissions.toArray(new String[permissions.size()]);
            //   requestPermissions(params, REQUEST_PERMISSIONS);
            requestAllPermission();
            Log.i("Log9","false");
            return false;
        }
        else
        {
            Log.i("Log10","true");
            return true;
            // We already have permission, so handle as normal
        }
    }

    private boolean checkPermission()
    {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),  android.Manifest.permission.RECEIVE_SMS);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_CONTACTS);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int result5 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_GSERVICES);
        int result6=  ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_PHONE_STATE);
        int result7 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION);

        /*Log.i("result1",""+result1);
        Log.i("result2",""+result2);
        Log.i("result3",""+result3);
        Log.i("result4",""+result4);
        Log.i("result5",""+result5);
        Log.i("result6",""+result6);
        Log.i("result7",""+result7);*/


        return result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED
                && result4 == PackageManager.PERMISSION_GRANTED && result5 == PackageManager.PERMISSION_GRANTED && result6 == PackageManager.PERMISSION_GRANTED
                && result7 == PackageManager.PERMISSION_GRANTED;



    }

    public void requestPermission()
    {

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.READ_CONTACTS,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_GSERVICES,
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CALL_PHONE,
                android.Manifest.permission.PROCESS_OUTGOING_CALLS,
                android.Manifest.permission.READ_CALENDAR}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode)
        {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0)
                {
                    boolean smsAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean locationAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean contactsAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean extStrgAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean gserviceAccepted = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    phoneStateAccepted = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                    boolean coarseLocAccepted = grantResults[6] == PackageManager.PERMISSION_GRANTED;
                    boolean writeExtStrgAccepted = grantResults[7] == PackageManager.PERMISSION_GRANTED;
                    boolean callPhoneAccepted = grantResults[8] == PackageManager.PERMISSION_GRANTED;
                    boolean outCallAccepted = grantResults[9] == PackageManager.PERMISSION_GRANTED;
                    boolean readCalenderAccepted = grantResults[10] == PackageManager.PERMISSION_GRANTED;

                    /*Log.i("smsAccepted",""+smsAccepted);
                    Log.i("locationAccepted",""+locationAccepted);
                    Log.i("contactsAccepted",""+contactsAccepted);
                    Log.i("extStrgAccepted",""+extStrgAccepted);
                    Log.i("gserviceAccepted",""+gserviceAccepted);
                    Log.i("phoneStateAccepted",""+phoneStateAccepted);
                    Log.i("coarseLocAccepted",""+coarseLocAccepted);
                    Log.i("writeExtStrgAccepted",""+writeExtStrgAccepted);
                    Log.i("callPhoneAccepted",""+callPhoneAccepted);
                    Log.i("outCallAccepted",""+outCallAccepted);
                    Log.i("readCalenderAccepted",""+readCalenderAccepted);*/

                    if (locationAccepted && smsAccepted && contactsAccepted && extStrgAccepted
                            && gserviceAccepted && phoneStateAccepted && coarseLocAccepted && writeExtStrgAccepted
                            && callPhoneAccepted && outCallAccepted && readCalenderAccepted)
                    {
                        //Log.i("Granted","You can use");
                    }
                    else
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        {
                            //Log.i("Not Granted","You can't use");
                        }
                    }
                }
                break;
        }
    }


    /* @Override
    protected void onStart()
    {

        Log.i("Test","Test");

        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Start = sdf.parse("2016/09/02");
            End = sdf.parse("2016/09/05");
        } catch (ParseException p)
        {
            p.printStackTrace();
        }

        if (End.after(Start))
        {
            Log.i("Test","Test");
            Toast.makeText(getApplicationContext(),"Please Be Patient",Toast.LENGTH_LONG).show();
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            finish();
        }
        super.onStart();
    }
*/
    public void Sign_in()
    {
        String uname = UserMob.getText().toString();
        String upass = Userpwd.getText().toString();

        String responsetype = "2";
        // String APIKEY = "S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47";
        String url=""+Url+"/signIn/?";
        // http://admin.clearcarrental.com/api
        //  String APIKEY="PVU1ZE-ZE4TPC-5IXWAJ-P2E6ZE-QONPEC-4IUGWD";
        // ?email=customeruser@infogird.com&mobile=8888801816&password=Customer123&&ApiKey=S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47&UserIPAddress=127.0.0.1&UserID=1212&UserAgent=Mozilla&responsetype=2";



            /* progressDialog = ProgressDialog.show(ccrlogin.this, "", "Please Wait...",
                     true);
             progressDialog.show();*/

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("username", uname));
        nameValuePairs.add(new BasicNameValuePair("password", upass));


        try
        {
            String uri = Uri.parse(url)
                    .buildUpon()
                    .appendQueryParameter("email",uname)
                    .appendQueryParameter("password",upass)
                    .appendQueryParameter("token",token)
                    .appendQueryParameter("version",String.valueOf(version_code))
                    .appendQueryParameter("androidVersion",Androidversion)
                    .appendQueryParameter("imeiNumber",Device_id)
                    .appendQueryParameter("androidId",android_id)
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserID","1212")
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype",responsetype)
                    .build().toString();

            Log.i("uri", uri);


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            Log.i("response", "" + response);

            runOnUiThread(new Runnable()
            {
                public void run()
                {
                    dialog.dismiss();
                }
            });

//                 progressDialog.dismiss();

                /* final String separated[] = response.split(",");
                 final String Check[] =separated[1].split(":");
                 final String Confirmlogin = Check[1].trim();
                 Log.i("Confirm",Confirmlogin);
                 final String Confirm = "1}]";*/
            final String Str = "1";
            JSONArray json = new JSONArray(response);
            //Log.i("json",""+json);
            JSONObject jsonObject = json.getJSONObject(0);
            String str ="1";
            final String responsecode = jsonObject.getString(Tag_Sign);
            final String Message = jsonObject.getString("responseMessage");
            final String Status_res_massge1 = Message.substring(2, Message.length() - 2);

            //Log.i("responsecode",""+responsecode);

            if(responsecode.compareTo(str)==0)
            {
                final String LoginStatus =jsonObject.getString(Tag_Subuserid);
               // Log.i("LoginStatus",LoginStatus);
                session.createUserLoginSession(uname, upass);
                uId = jsonObject.getString(Tag_uid);
                Cashwalletbalance = jsonObject.getInt(TagprefernceBalance);
                Useremail = jsonObject.getString(Tagemail);
                Usermobile = jsonObject.getString(Tagmobile);
                fname = jsonObject.getString(Tagname);
               // Log.i("fname",fname);
                Lname = jsonObject.getString("lastName");
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(Email, uname);
                editor.putString(Tag_uid, uId);
                editor.putString(Name,fname);
              //  editor.putString(TagLast,Lname);
                editor.putInt(TagprefernceBalance, Cashwalletbalance);
                editor.putString(Tagemail, Useremail);
                editor.putString(Tagmobile,Usermobile);
                editor.putString("versioncode",String.valueOf(version_code));
                editor.apply();
            }

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {

                    if (responsecode.equals("1"))
                    {
                        Intent in = new Intent(getApplicationContext(), MapActivity.class);
                        startActivity(in);
                        finish();

                    }
                    else
                    {
                       // Log.i("LoginStatus", LoginStatus);
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                        alertDialog.setTitle("");
                        alertDialog.setMessage(Status_res_massge1);
                        //   alertDialog.setIcon(R.drawable.fail);
                        alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                            }
                        });
                        alertDialog.show();
                    }

                }
            });


            Log.e("pass 1", "connection success ");
        }
        catch (Exception e)
        {
            Log.e("Fail 1", e.toString());

        }

    }



    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(startMain);

        /*new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1)
                    {
                        MainActivity.super.onBackPressed();
                    }
                }).create().show();*/


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Registering receiver on activity resume
    @Override
    protected void onResume()
    {
        super.onResume();
        Log.w("MainActivity", "onResume");
        Log.i("internet",""+cd.isConnectingToInternet(MainActivity.this));
        if(cd.isConnectingToInternet(MainActivity.this))
        {
           if(currentapiVersion<23)
           {
               Log.i("lolipopresume", "lollipop");

               LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                       new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
               LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                       new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
           }
            if (phoneStateAccepted)
            {
                deviceData();
                Log.i("onResume12315", "onResume12315");

                LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                        new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
                LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                        new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
            }
        }
    }


    //Unregistering receiver on activity paused
    @Override
    protected void onPause()
    {
        super.onPause();
        Log.w("MainActivity", "onPause");
        Log.i("onpauseinternet", "" + cd.isConnectingToInternet(MainActivity.this));
        if (phoneStateAccepted)
        {
            Log.w("onPause", "unregisterReceiver");
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
            finish();
        }
        if(currentapiVersion<23)
        {

            Log.w("onPause", "unregisterReceiver");
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
            finish();
        }
    }
}