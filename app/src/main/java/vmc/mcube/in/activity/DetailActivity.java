package vmc.mcube.in.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import vmc.mcube.in.R;
import vmc.mcube.in.fragment.ReportDetails;
import vmc.mcube.in.model.Data;
import vmc.mcube.in.model.UserData;
import vmc.mcube.in.utils.Utils;

import static android.content.DialogInterface.BUTTON_POSITIVE;


public class DetailActivity extends AppCompatActivity {


    public Data mTrackData;
    public UserData userData;
    private ReportDetails ReportDetailsFragment;
    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utils.tabletSize(DetailActivity.this) < 6.0)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        String data = getIntent().getExtras().getString("DATA");
        Gson gson = new Gson();
        mTrackData = gson.fromJson(data, Data.class);
        toolbar.setTitleTextColor(Color.WHITE);
        userData = Utils.GetUserData(this);
        ReportDetailsFragment = new ReportDetails();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_layout_id, ReportDetailsFragment).commit();

        try {
            if (savedInstanceState != null) {
                //Restore the fragment's instance
                ReportDetailsFragment = (ReportDetails) getSupportFragmentManager().getFragment(savedInstanceState, "DetailFragment");
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout_id, ReportDetailsFragment, TAG_TASK_FRAGMENT).commit();
            } else {
                ReportDetailsFragment = (ReportDetails) getSupportFragmentManager().findFragmentByTag(TAG_TASK_FRAGMENT);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                try {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        Log.d("BackStack", "" + getSupportFragmentManager().getBackStackEntryCount());
                        getSupportFragmentManager().popBackStack();
                        return true;
                    } else {
                        onBackPressed();
                        return true;

                    }
                } catch (Exception e) {

                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        super.onBackPressed();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        if (ReportDetailsFragment != null)
            getSupportFragmentManager().putFragment(outState, "DetailFragment", ReportDetailsFragment);
    }


}
