package vmc.mcube.in.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import com.rampo.updatechecker.UpdateChecker;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import vmc.mcube.in.R;
import vmc.mcube.in.fragment.DownloadFile;
import vmc.mcube.in.fragment.MakePayment;
import vmc.mcube.in.fragment.RateDialogFragment;
import vmc.mcube.in.fragment.Report;
import vmc.mcube.in.fragment.Settings;
import vmc.mcube.in.gcm.GCMClientManager;
import vmc.mcube.in.model.Data;
import vmc.mcube.in.model.PaymentData;
import vmc.mcube.in.model.UserData;
import vmc.mcube.in.parsing.Requestor;
import vmc.mcube.in.utils.ConnectivityReceiver;
import vmc.mcube.in.utils.Constants;
import vmc.mcube.in.utils.RefreshCallBack;
import vmc.mcube.in.utils.SingleTon;
import vmc.mcube.in.utils.SpinnerCallBack;
import vmc.mcube.in.utils.SuccessPaymentDialog;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.utils.Utils;

import static android.content.DialogInterface.BUTTON_POSITIVE;
import static com.payu.india.Payu.PayuConstants.PAYU_REQUEST_CODE;

public class HomeActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, NavigationView.OnNavigationItemSelectedListener,
        Tag, RefreshCallBack, SpinnerCallBack, DownloadFile.DownloadFileTask {
    private static final String FIRST_TIME = "first_item";
    private static final String FRAGMENT_KEY = "report_key";
    public static String Tag = "";
    private int position = 100;
   // public static String BASE_URL;
    public UserData userData;
    public Data data;
    public static String recordLimit;
    private NavigationView mDrawer;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private int mSelectedId;
    private boolean mUserSawDrawer = false;
    private String titles[] = {"Track", "Ivrs", "MCubeX", "Lead", "MTracker", "FollowUp", "Settings","MakePayment"};
    private Toolbar mToolbar;
    private TextView clientName, clientEmail;
    private ArrayList<String> filterGroup;
    private boolean doubleBackToExitPressedOnce;
    private Vibrator v;
    private String PROJECT_NUMBER = "305656196217";
    private RequestQueue requestQueue;
    private SingleTon volleySingleton;
    public String TYPE, pushMessage;;
    private DownloadFile downloadFragment;
    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    private static final String TAG_TASK_FRAGMENT1 = "task_fragment1";
    private AlertDialog alertDialog;
    private ProgressDialog mProgressDialog;
    private int completed = 0;
    private boolean isRestored = false;
    private Fragment ReportFragment;
    private RateDialogFragment ratingDialogFragment;
    private boolean fileShare = false;
    private Fragment myFrag;
    private boolean isOnline;
    public FloatingActionButton floatingButtoncall;
    private ProgressDialog progressdialog;
    private boolean isLogout;
    SharedPreferences sharedPrefs;
    private SuccessPaymentDialog successDialogFragment;
    private PaymentData paymentData;
    private String paymentDataObj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utils.tabletSize(HomeActivity.this) < 6.0)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
       // overridePendingTransition(0, 0);
        setContentView(R.layout.activity_home);
        new UpdateChecker(this).start();
        setUpReview();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        if (savedInstanceState != null) {
            fileShare = savedInstanceState.getBoolean("SHARE");
            TYPE = savedInstanceState.getString("type");
            position = savedInstanceState.getInt("selectedIndex");

        } else {
            deleteFiles();
        }
        if (TYPE == null) {
            TYPE = TRACK;
        }
        Utils.callConnectAlert(this,"Your last transaction is failed.",false);
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        Bundle bundle = getIntent().getExtras();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setTitle(titles[Constants.position]);
        userData = Utils.GetUserData(HomeActivity.this);
      //  BASE_URL = userData.getSERVER();
        filterGroup = new ArrayList<String>();
        recordLimit = Utils.getFromPrefs(HomeActivity.this, "recordLimit", "10");
        mDrawer = (NavigationView) findViewById(R.id.main_drawer);
        mDrawer.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,
                R.string.drawer_close);
        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        mDrawer.setItemIconTintList(null);
        View header = mDrawer.getHeaderView(0);
        clientName = (TextView) header.findViewById(R.id.tvName);
        clientEmail = (TextView) header.findViewById(R.id.tvEmail);
        clientEmail.setText(userData.EMP_EMAIL);
        volleySingleton = SingleTon.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        //ReportFragment = (Report) getSupportFragmentManager().findFragmentByTag(TAG_TASK_FRAGMENT);
        //myFrag = (Report) getSupportFragmentManager().findFragmentByTag(TAG_TASK_FRAGMENT);
        myFrag=Report.newInstance(TYPE);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_layout_id, myFrag).commit();
        downloadFragment = (DownloadFile) getSupportFragmentManager().findFragmentByTag(TAG_TASK_FRAGMENT1);
        SelectModule();
        ShowOrHideModule(mDrawer.getMenu());
        clientName.setText("Hi " + userData.EMP_NAME);
        if (!didUserSeeDrawer()) {
            showDrawer();
            markDrawerSeen();
        } else {
            hideDrawer();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utils.checkAndRequestPermissions(HomeActivity.this);

        }
        floatingButtoncall= (FloatingActionButton) findViewById(R.id.fab_call);
        Utils.showFabWithAnimation(floatingButtoncall);
        floatingButtoncall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ClickToConnect.class));
            }
        });
        GCMClientManager pushClientManager = new GCMClientManager(this, PROJECT_NUMBER);
        pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
            @Override
            public void onSuccess(String registrationId, boolean isNewRegistration) {
                Log.d("Registration id", registrationId);
                MyApplication.getInstance().gcmKey=registrationId;
                onRegisterGcm(registrationId, userData.getAUTHKEY());
            }

            @Override
            public void onFailure(String ex) {
                super.onFailure(ex);
            }
        });
        onNavigationItemSelected(mDrawer.getMenu().getItem(position));

        if (myFrag == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(Report.newInstance(TYPE), TAG_TASK_FRAGMENT).commit();

       }
        if (ConnectivityReceiver.isConnected()) {
            new CheckStatus().execute();
        }

    }


    private void ShowOrHideModule(Menu menu) {
        for (int menuItemIndex = 0; menuItemIndex < menu.size(); menuItemIndex++) {
            MenuItem menuItem = menu.getItem(menuItemIndex);

            if (Utils.getFromPrefsBoolean(HomeActivity.this, TRACK, false)) {
                if (menuItem.getItemId() == R.id.track) {
                    menuItem.setVisible(true);

                }
            } else {
                if (menuItem.getItemId() == R.id.track) {
                    menuItem.setVisible(false);
                }
            }

            if (Utils.getFromPrefsBoolean(HomeActivity.this, MTRACKER, false)) {
                if (menuItem.getItemId() == R.id.mtracker) {
                    menuItem.setVisible(true);

                }
            } else {
                if (menuItem.getItemId() == R.id.mtracker) {
                    menuItem.setVisible(false);
                }
            }
            if (Utils.getFromPrefsBoolean(HomeActivity.this, LEAD, false)) {
                if (menuItem.getItemId() == R.id.lead) {
                    menuItem.setVisible(true);

                }
            } else {
                if (menuItem.getItemId() == R.id.lead) {
                    menuItem.setVisible(false);
                }
            }
            if (Utils.getFromPrefsBoolean(HomeActivity.this, X, false)) {
                if (menuItem.getItemId() == R.id.x) {
                    menuItem.setVisible(true);

                }
            } else {
                if (menuItem.getItemId() == R.id.x) {
                    menuItem.setVisible(false);
                }
            }

            if (Utils.getFromPrefsBoolean(HomeActivity.this, IVRS, false)) {
                if (menuItem.getItemId() == R.id.ivrs) {
                    menuItem.setVisible(true);

                }
            } else {
                if (menuItem.getItemId() == R.id.ivrs) {
                    menuItem.setVisible(false);
                }
            }


        }
    }

    private void SelectModule() {

        if (Utils.getFromPrefsBoolean(HomeActivity.this, TRACK, false)) {
            position = 0;
            this.TYPE = TRACK;

        } else if (Utils.getFromPrefsBoolean(HomeActivity.this, IVRS, false)) {
            position = 1;
            this.TYPE = IVRS;

        } else if (Utils.getFromPrefsBoolean(HomeActivity.this, X, false)) {
            position = 2;
            this.TYPE = X;

        } else if (Utils.getFromPrefsBoolean(HomeActivity.this, LEAD, false)) {
            position = 3;
            this.TYPE = LEAD;

        } else if (Utils.getFromPrefsBoolean(HomeActivity.this, MTRACKER, false)) {
            position = 4;
            this.TYPE = MTRACKER;

        }

    }


    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

    public void onRegisterGcm(final String regid, final String authkey) {

        if (ConnectivityReceiver.isConnected()) {
            new RegisterGcm(regid, authkey).execute();
        } else {
            //  Toast.makeText(HomeActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    public void onRatingsClick(Data callData) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ratingDialogFragment = new RateDialogFragment();
        ratingDialogFragment.setCancelable(true);
        ratingDialogFragment.setCallid(callData);
        ratingDialogFragment.show(fragmentManager, "Rating Dialog");
    }


    class RegisterGcm extends AsyncTask<Void, Void, String> {
        JSONObject response = null;
        String regid, code, authkey,msg;

        public RegisterGcm(String regid, String authkey) {
            this.regid = regid;
            this.authkey = authkey;


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                response = Requestor.requestREG_GCM(requestQueue, BASE_URL+GCM_URL, regid, authkey);
                Log.d("REFER", response.toString());

                if (response.has(CODE)) {
                    code = response.getString(CODE);

                }  if (response.has(MESSAGE)) {
                    msg = response.getString(MESSAGE);

                }


            } catch (Exception e) {

            }
            return code;
        }

        @Override
        protected void onPostExecute(String data) {

            if (code != null) {
               // Utils.callConnectAlert(HomeActivity.this,msg,true);
                //Toast.makeText(HomeActivity.this, data, Toast.LENGTH_SHORT).show();
            }


        }


    }


    class CheckStatus extends AsyncTask<Void, Void, String> {
        private String msg,code;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(Void... params) {
            JSONObject response = null;
            try {
                response = Requestor.getStatus(requestQueue, BASE_URL+CHECK_STATUS_URL,(Utils.GetUserData(HomeActivity.this).getAUTHKEY()),MyApplication.getInstance().gcmKey);
                Log.d("TEST", response.toString());
                if (response != null) {
                    if (response.has(CODE)) {
                        code = response.getString(CODE);
                    }
                    if (response.has(MESSAGE)) {
                        msg = response.getString(MESSAGE);
                    }
                }
            } catch (Exception e) {
            }

            return code;
        }


        @Override
        protected void onPostExecute(String code) {
            super.onPostExecute(code);

            if (code != null && code.equals("202")) {
                Utils.logoutByGCM(HomeActivity.this,msg);
                Utils.callConnectAlert(HomeActivity.this,msg,true);
            }

        }

    }






    private void markDrawerSeen() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserSawDrawer = true;
        sharedPreferences.edit().putBoolean(FIRST_TIME, mUserSawDrawer).apply();
    }

    private void navigate(int mSelectedId) {

        if (mSelectedId == R.id.track) {
            setSelection(0);
            position = 0;
        }
        if (mSelectedId == R.id.ivrs) {
            setSelection(1);
            position = 1;
        }
        if (mSelectedId == R.id.x) {
            setSelection(2);
            position = 2;
        }
        if (mSelectedId == R.id.lead) {
            setSelection(3);
            position = 3;
        }
        if (mSelectedId == R.id.mtracker) {
            setSelection(4);
            position = 4;
        }

        if (mSelectedId == R.id.followup) {
            setSelection(5);
            position = 5;
        }
        if (mSelectedId == R.id.settings) {
            setSelection(6);
            position = 6;
        }

        if (mSelectedId == R.id.payU) {
            setSelection(7);
            position = 7;
        }

        if (mSelectedId == R.id.logout) {
            // hideDrawer();
            Utils.isLogout1(HomeActivity.this);
        }
        invalidateOptionsMenu();
    }

    private void setSelection(int item) {
        // hideDrawer();
        displayView(item);
    }

    public void playAudio(Data callData) {
        if (callData.getStatus() != null && callData.getStatus().matches("INCOMING|OUTGOING")) {
            new MarkSeen(callData).execute();
        } else {
            playaudio(callData);
        }

    }

    private void playaudio(Data callData) {
        if (callData.getAudioLink() != null && callData.getAudioLink().length() > 7) {
            Log.d("AUDIO", callData.getAudioLink());
            Uri myUri = Uri.parse(STREAM_TRACKER + callData.getAudioLink());
            //Uri myUri = Uri.parse(callData.getAudioLink());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(myUri, "audio/*");
            startActivity(intent);
        }
    }

    private void SelectType(int position) {

        switch (position) {
            case 0:
                TYPE = TRACK;
                break;
            case 1:
                TYPE = IVRS;
                break;
            case 2:
                TYPE = X;
                break;
            case 3:
                TYPE = LEAD;
                break;
            case 4:
                TYPE = MTRACKER;
                break;
            case 5:
                TYPE = FOLLOWUP;
                break;


        }

    }

    private void displayView(int position) {
        SelectType(position);
        // Fragment myFragment = null;
        setTitle(position);
        myFrag = Report.newInstance(TYPE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
       // getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        switch (position) {
            case 6:
                myFrag = Settings.newInstance("", "");
                floatingButtoncall.hide();
                break;
            case 7:
                myFrag = new MakePayment();
                // transaction.setCustomAnimations(R.anim.entryanim, R.anim.exitanim, 0, 0);
                transaction.setCustomAnimations(R.anim.fragment_enter_bottom, R.anim.fragment_exit_bottom,
                        R.anim.fragment_enter_top, R.anim.fragment_exit_top);
                floatingButtoncall.hide();
                break;

            default:
               floatingButtoncall.show();
                break;

        }


        if (myFrag != null && myFrag instanceof Report) {
            try {
                transaction.replace(R.id.frame_layout_id, myFrag).commit();

            } catch (Exception e) {
            }
            // Can not perform this action after onSaveInstanceState
            // if you are trying to perform a transaction after your Activity is gone in background.
            // To avoid this you should use commitAllowingStateLoss()
            // getSupportFragmentManager().beginTransaction().commit();
        } else {
            transaction.replace(R.id.frame_layout_id, myFrag).addToBackStack(null).commit();

        }

    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        Log.d("BACKSTACK", getSupportFragmentManager().getBackStackEntryCount() + "");
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
               // getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                //getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            else {
                if (doubleBackToExitPressedOnce) {
                    getSupportFragmentManager().popBackStack();
                    super.onBackPressed();
                    return;
                }
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Press again to exit.", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        }



    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void setSelectionDelay(final int selectedId) {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d("TEST", "Handler started");
                navigate(selectedId);
            }
        };
        handler.sendEmptyMessageDelayed(0, 300);

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);
        menuItem.setChecked(true);
        mSelectedId = menuItem.getItemId();
        Log.d("NAVIGATION", "" + mSelectedId);
        hideDrawer();
        setSelectionDelay(menuItem.getItemId());
        return true;
    }


    private boolean didUserSeeDrawer() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserSawDrawer = sharedPreferences.getBoolean(FIRST_TIME, false);
        return mUserSawDrawer;
    }

    private void showDrawer() {
        this.mDrawerLayout.openDrawer(GravityCompat.START);

    }

    private void hideDrawer() {
        this.mDrawerLayout.closeDrawer(GravityCompat.START);
    }


    @Override
    public void onRefresh() {
        supportInvalidateOptionsMenu();
    }


    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
        this.unregisterReceiver(mMessageReceiver);
    }

    public void setTitle(int position) {
        mToolbar.setTitle(titles[position]);

    }

    public void setTitlee(String title) {
        mToolbar.setTitle(title);

    }

    @Override
    public void ResetSpinner() {
    }

    private void clearBackStack() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        while (fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStackImmediate();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
        if (!Utils.isLogin(HomeActivity.this)) {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            HomeActivity.this.startActivity(intent);
    }
        this.registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }


    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (!isConnected) {
            message = "Sorry! Not connected to internet";
            color = Color.WHITE;
        } else {
            message = "Connected to internet";
            color = Color.WHITE;
        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.main_content), message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.primary));
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        snackbar.show();

    }

    public void onShareFile(final String url) {
        if (ConnectivityReceiver.isConnected()) {
            // downloadFragment = (DownloadFile)getSupportFragmentManager().findFragmentByTag(TAG_TASK_FRAGMENT1);
            downloadFragment = new DownloadFile();
            Bundle bundle = new Bundle();
            bundle.putString("FILE", url);
            downloadFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(downloadFragment, TAG_TASK_FRAGMENT1).commit();
        } else {
            Snackbar snack = Snackbar.make(floatingButtoncall, "No Internet Connection", Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            onShareFile(url);

                        }
                    })
                    .setActionTextColor(ContextCompat.getColor(HomeActivity.this, R.color.accent));
            View view = snack.getView();
            view.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.primary));
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();
        }

    }


    private void initProgress() {
        mProgressDialog = new ProgressDialog(HomeActivity.this) {
            @Override
            public void onBackPressed() {
                mProgressDialog.dismiss();
                showDowanlodAlert();
            }
        };
        mProgressDialog.setMessage("Loading");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Downloading file..");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgress(0);
        mProgressDialog.setMax(100);
        mProgressDialog.show();

    }

    @Override
    public void ondownloadFilePreExecute() {
        fileShare = true;
        initProgress();
    }

    @Override
    public void ondownloadFileProgressUpdate(int percent) {
        if (mProgressDialog == null) {
            initProgress();
        }
        completed = percent;
        mProgressDialog.setProgress(percent);
    }


    @Override
    public void ondownloadFileCancelled() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }

        Log.d("SHARE", "Download Cancelled");


    }

    @Override
    public void ondownloadFilePostExecute(File file) {
        Log.d("PATH", file.getAbsolutePath());
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }

        if (file.exists()) {
            fileShare = true;
            Uri uri = Uri.parse("file://" + file);
            Intent share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.setType("audio/*");
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            this.startActivityForResult(Intent.createChooser(share, "Share MCube Record "), SHARE_CALL);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult()", Integer.toString(resultCode));
        Log.d("onActivityResult()", Integer.toString(requestCode));

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {

            case SHARE_CALL:
                Log.d("onActivityResult()", "SHARE SUCCESS");
                fileShare = false;
                deleteFiles();
                break;

            case PAYU_REQUEST_CODE:

                if(resultCode == Activity.RESULT_OK){
                        if(data!=null){
                            Gson gson = new Gson();
                            paymentDataObj = data.getStringExtra("PAYMENT_DATA");
                            paymentData = gson.fromJson(paymentDataObj, PaymentData.class);



//                     Log.d("RESPONSE_HASH",data.getStringExtra("transaction_id"));
//                            String msg="Transaction success,please note your Transaction ID "+"("+data.getStringExtra("transaction_id")+") "+"for future reference.";
                           // Utils.callConnectAlert(this,msg,false);
                            showSuccessDialog();
                        }
                    }
                    else if(resultCode == Activity.RESULT_CANCELED){
                     Utils.callConnectAlert(this,"Payment Failed"+"\n"+" Try again",false);
                       // Toast.makeText(this, "Payment Failed TID: "+data.getStringExtra("transaction_id"), Toast.LENGTH_LONG).show();
                    }



                break;
        }

    }



    private void showSuccessDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        successDialogFragment = new SuccessPaymentDialog();
        successDialogFragment.setCancelable(false);
        //successDialogFragment.setTargetFragment(this, 0);
        //successDialogFragment.show(fragmentManager, "Input Dialog");
        Bundle bundle=new Bundle();
        bundle.putString("PAYMENT_DATA", paymentDataObj);
        successDialogFragment.setArguments(bundle);
        //bundle.putParcelable("PAYMENT_DATA",paymentData);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(successDialogFragment, null);
        ft.commitAllowingStateLoss();

    }


    public void deleteFiles() {
        File folder = null;
        folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "data" + File.separator + "mcubeShare");

        List<File> files = Utils.getListFiles(folder);
        for (int i = 0; i < files.size(); i++) {
            files.get(i).delete();
            Log.d("SHARE", files.get(i).getName() + " Deleted..");
        }
    }


    public void showDowanlodAlert() {
        alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
        alertDialog.setTitle("MCube");
        alertDialog.setIcon(R.mipmap.logo);
        alertDialog.setMessage("Cancel Downloading ?");
        alertDialog.setButton(BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (downloadFragment != null) {
                            downloadFragment.onCancelTask();
                        }
                        deleteFiles();
                        mProgressDialog = null;


                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {

                        dialog.dismiss();
                        if ((mProgressDialog != null && !mProgressDialog.isShowing()) && completed < 100) {
                            mProgressDialog.show();
                        }
                    }
                });


        alertDialog.show();


    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save the state of the drop down menu
        savedInstanceState.putString("type", TYPE);
        savedInstanceState.putBoolean("SHARE", fileShare);
        savedInstanceState.putInt("selectedIndex", position);
        // ReportFragment = (Report) getSupportFragmentManager().findFragmentByTag(TAG_TASK_FRAGMENT);
        try {
            if (myFrag != null) {
                getSupportFragmentManager().putFragment(savedInstanceState, FRAGMENT_KEY, myFrag);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    class MarkSeen extends AsyncTask<Void, Void, String> {
        private String msg;
        private Data callData;
        private String code;

        public MarkSeen(Data callData) {
            this.callData = callData;
        }


        @Override
        protected String doInBackground(Void... params) {
            JSONObject response = null;
            isOnline = ConnectivityReceiver.isOnline();
            // if (isOnline) {
            try {
                response = Requestor.recordSeen(requestQueue, SET_SEEN_URL, (Utils.GetUserData(HomeActivity.this).getAUTHKEY()), callData.getCallId());
                Log.d("TEST", response.toString());
                if (response != null) {
                    if (response.has(CODE)) {
                        code = response.getString(CODE);
                    }
                    if (response.has(MESSAGE)) {
                        msg = response.getString(MESSAGE);
                    }
                }
            } catch (Exception e) {
            }
            // }
            return code;
        }


        @Override
        protected void onPostExecute(String code) {
            super.onPostExecute(code);
            playaudio(callData);
            if (code != null && msg != null) {
                Log.d("AUDIO", code + "" + msg);
            }

        }

    }


    public void clickToCall(final String phoneNumber, final String callid,final String type){
        if (ConnectivityReceiver.isConnected()) {
            new ClickToCall(callid,type).execute();

        } else {
            Snackbar snack = Snackbar.make(findViewById(R.id.fab_call), "No Internet Connection", Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            clickToCall(phoneNumber,callid,type);

                        }
                    })
                    .setActionTextColor(ContextCompat.getColor(HomeActivity.this, R.color.accent));
            View view = snack.getView();
            view.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.primary));
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();
        }

    }




    class ClickToCall extends AsyncTask<Void, Void, String> {
        private String msg,code;
        private String callId,type;

        public ClickToCall(String callId,String type){
            this.callId=callId;
            this.type=type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressdialog = new ProgressDialog(HomeActivity.this);
            progressdialog.setMessage("Your call is connecting..");
            progressdialog.setCancelable(false);
            progressdialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            JSONObject response = null;
            try {
                response = Requestor.clickToCall(requestQueue, BASE_URL+CLICK_TO_CALL_URL, (Utils.GetUserData(HomeActivity.this).getAUTHKEY()),callId,type);
                Log.d("TEST", response.toString());
                if (response != null) {
                    if (response.has(CODE)) {
                        code = response.getString(CODE);
                    }
                    if (response.has(MESSAGE)) {
                        msg = response.getString(MESSAGE);
                    }
                }
            } catch (Exception e) {
            }

            return code;
        }


        @Override
        protected void onPostExecute(String code) {
            super.onPostExecute(code);
            progressdialog.dismiss();
            if (code != null && msg != null) {
           // Toast.makeText(HomeActivity.this,msg,Toast.LENGTH_SHORT).show();
               Utils.callConnectAlert(HomeActivity.this,msg,false);
            }

        }

    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void setUpReview() {

        AppRate.with(this)
                // .setInstallDays(0) // default 10, 0 means install day.
                //  .setLaunchTimes(2) // default 10
                .setRemindInterval(1) // default 1
                .setDebug(false) // default false
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        Log.d(HomeActivity.class.getName(), Integer.toString(which));
                    }
                })
                .monitor();

        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);

    }


    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
             pushMessage = intent.getStringExtra("message");
            Utils.callConnectAlert(HomeActivity.this,pushMessage,true);

            //do other stuff here
        }
    };



}
