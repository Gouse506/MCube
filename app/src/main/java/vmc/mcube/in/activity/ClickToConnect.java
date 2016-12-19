package vmc.mcube.in.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import org.json.JSONObject;

import vmc.mcube.in.R;
import vmc.mcube.in.fragment.DialpadFragment;
import vmc.mcube.in.fragment.DownloadFile;
import vmc.mcube.in.model.Data;
import vmc.mcube.in.parsing.Requestor;
import vmc.mcube.in.utils.ConnectivityReceiver;
import vmc.mcube.in.utils.SingleTon;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.utils.Utils;

public class ClickToConnect extends AppCompatActivity implements Tag{
    private RequestQueue requestQueue;
    private SingleTon volleySingleton;
    public FloatingActionButton mDialFab;
    DialpadFragment myDialFragment;
    private LinearLayout mprogressLayout;
    private Animation  animationEnter,animationExit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utils.tabletSize(ClickToConnect.this) < 6.0)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_click_to_connect);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ClickToCall");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        volleySingleton = SingleTon.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        onShowDialpad();
        myDialFragment=new DialpadFragment();
        mprogressLayout = (LinearLayout) findViewById(R.id.callmprogressLayout);
         animationEnter = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fragment_enter_top);
        animationExit = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fragment_exit_top);


//        if (savedInstanceState == null) {
//            if (Prefs.isOperatorSelected(this)) {
//                getSupportFragmentManager().beginTransaction()
//                        .add(R.id.container, new RecentContactsFragment())
//                        .commit();
//            } else {
//                getSupportFragmentManager().beginTransaction()
//                        .add(R.id.container, new OperatorsFragment())
//                        .commit();
//            }
//        }

    }

    public void onDigitClick(View view) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment instanceof DialpadFragment) {
            ((DialpadFragment) fragment).onDigitClick((Button) view);
        }
    }



//    public void onOperatorSelected(int id) {
//        Prefs.saveOperatorId(getApplicationContext(), id);
//
//        getSupportFragmentManager().beginTransaction()
//                .setCustomAnimations(R.anim.fragment_enter_right, R.anim.fragment_exit_right)
//                .replace(R.id.container, new RecentContactsFragment(), RecentContactsFragment.TAG)
//                .commit();
//    }

//    public void onChangeOperator() {
//        getSupportFragmentManager().beginTransaction()
//                .setCustomAnimations(R.anim.fragment_enter_left, R.anim.fragment_exit_left)
//                .replace(R.id.container, new OperatorsFragment(), OperatorsFragment.TAG)
//                .commit();
//    }

    public void onShowDialpad() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fragment_enter_bottom, R.anim.fragment_exit_bottom,
                        R.anim.fragment_enter_top, R.anim.fragment_exit_top)
                .replace(R.id.container, new DialpadFragment(), DialpadFragment.TAG)
                .addToBackStack(null)
                .commit();
    }

    public void onHideDialpad() {
        // Pop a back stack because currently transaction Recent contacts -> Dialpad is saved and must be removed.
        getSupportFragmentManager().popBackStack();

        getSupportFragmentManager().beginTransaction()
                //.setCustomAnimations(R.anim.fragment_enter_top, R.anim.fragment_exit_top)
               // .replace(R.id.container, new RecentContactsFragment(), RecentContactsFragment.TAG)
                .commit();
    }

    public void onChangeTheme() {
        recreate();
    }

    public void clickToCall(final String phoneNumber, final String type){
        if (ConnectivityReceiver.isConnected()) {
            new ClickToCall(phoneNumber,type).execute();

        } else {
            Snackbar snack = Snackbar.make(findViewById(R.id.drawer_layout), "No Internet Connection", Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                           clickToCall(phoneNumber,type);

                        }
                    })
                    .setActionTextColor(ContextCompat.getColor(ClickToConnect.this, R.color.accent));
            View view = snack.getView();
            view.setBackgroundColor(ContextCompat.getColor(ClickToConnect.this, R.color.primary));
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();
        }

    }




    class ClickToCall extends AsyncTask<Void, Void, String> {
        private String msg;
        private Data callData;
        private String code;
        private String phoneNumber,type;

        public ClickToCall(String phoneNumber, String type) {
            this.type = type;
            this.phoneNumber = phoneNumber;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mprogressLayout.startAnimation(animationEnter);
            if (mprogressLayout.getVisibility() == View.GONE) {
                mprogressLayout.setVisibility(View.VISIBLE);
            }




        }

        @Override
        protected String doInBackground(Void... params) {
            JSONObject response = null;
            try {
                response = Requestor.clickToCall(requestQueue, BASE_URL+CLICK_TO_CALL_URL, (Utils.GetUserData(ClickToConnect.this).getAUTHKEY()),phoneNumber,type);
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

               // Log.d("Click",e.getMessage().toString());
            }
            // }
            return code;
        }


        @Override
        protected void onPostExecute(String code) {
            super.onPostExecute(code);
            mprogressLayout.startAnimation(animationExit);
            if (mprogressLayout.getVisibility() == View.VISIBLE) {
                mprogressLayout.setVisibility(View.GONE);
            }
            if (code != null && msg != null) {
                //Toast.makeText(ClickToConnect.this,msg,Toast.LENGTH_SHORT).show();
                Utils.callConnectAlert(ClickToConnect.this,msg,false);
            }

        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            getSupportFragmentManager().popBackStack();
                super.onBackPressed();
                return;
            }

    }

}
