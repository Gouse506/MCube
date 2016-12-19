package vmc.mcube.in.activity;


import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import vmc.mcube.in.R;
import vmc.mcube.in.model.Data;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.utils.Utils;

public class LocationActivity extends AppCompatActivity implements Tag, OnMapReadyCallback {

    GoogleMap googleMap;
    LatLng latLng;
    protected static final String TAG = "MainActivity";
    private Marker markerName;
    private TextView mLocationTextView;
    private Data callData;
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm aa");
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        if (Utils.tabletSize(LocationActivity.this) < 6.0)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.location_activity);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Call Location");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mLocationTextView = (TextView) findViewById(R.id.location_text_view);
        String data = getIntent().getExtras().getString("DATA");
        Gson gson = new Gson();
        callData = gson.fromJson(data, Data.class);
        SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap));
        mapFragment.getMapAsync(this);

    }

    private void updateLocation(LatLng centerLatLng) {
        if (centerLatLng != null) {
            Geocoder geocoder = new Geocoder(LocationActivity.this,
                    Locale.getDefault());

            List<Address> addresses = new ArrayList<Address>();
            try {
                addresses = geocoder.getFromLocation(centerLatLng.latitude,
                        centerLatLng.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addresses != null && addresses.size() > 0) {

                String addressIndex0 = (addresses.get(0).getAddressLine(0) != null) ? addresses
                        .get(0).getAddressLine(0) : null;
                String addressIndex1 = (addresses.get(0).getAddressLine(1) != null) ? addresses
                        .get(0).getAddressLine(1) : null;
                String addressIndex2 = (addresses.get(0).getAddressLine(2) != null) ? addresses
                        .get(0).getAddressLine(2) : null;
                String addressIndex3 = (addresses.get(0).getAddressLine(3) != null) ? addresses
                        .get(0).getAddressLine(3) : null;

                String completeAddress = addressIndex0 + "," + addressIndex1;

                if (addressIndex2 != null) {
                    completeAddress += "," + addressIndex2;
                }
                if (addressIndex3 != null) {
                    completeAddress += "," + addressIndex3;
                }
                if (completeAddress != null) {
                    mLocationTextView.setText(completeAddress);
                }
            }
        }
    }


    private boolean isGooglePlayServicesAvailable() {
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GoogleApiAvailability.getInstance().getErrorDialog(this, status, 0).show();
            return false;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                // Use default InfoWindow frame
                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                // Defines the contents of the InfoWindow
                @Override
                public View getInfoContents(Marker arg0) {

                    // Getting view from the layout file info_window_layout
                    View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);
                    if(Utils.tabletSize(LocationActivity.this)>6.0){
                        v.setLayoutParams(new LinearLayout.LayoutParams(200, 180));
                    }else if(Utils.tabletSize(LocationActivity.this)>5.0){
                        v.setLayoutParams(new LinearLayout.LayoutParams(280, 300));
                    }
                    else{
                        v.setLayoutParams(new LinearLayout.LayoutParams(220, 210));
                    }
                    // Getting the position from the marker
                    LatLng latLng = arg0.getPosition();
                    Log.d("ADDRESS", arg0.getPosition() + "");
                    // Getting reference to the TextView to set latitude
                    TextView tvFrom = (TextView) v.findViewById(R.id.tv1);
                    TextView tvDate = (TextView) v.findViewById(R.id.tv2);
                    TextView tvTime = (TextView) v.findViewById(R.id.tv3);
                    TextView tvType = (TextView) v.findViewById(R.id.tv4);
                    TextView tvName = (TextView) v.findViewById(R.id.tv5);
                    String Name=Utils.isEmpty(callData.getCallerName()) ? UNKNOWN : callData.getCallerName();
                    String name[]=Name.split("[-\\s]");
                    tvName.setText(name[0]);
                    //tvName.setText(Utils.isEmpty(callData.getCallerName()) ? UNKNOWN : callData.getCallerName());
                    tvFrom.setText(callData.getCallFrom());
                    tvDate.setText(sdfDate.format(callData.getCallTime()));
                    tvTime.setText(sdfTime.format(callData.getCallTime()));
                    tvType.setText(callData.getStatus());
                    return v;

                }
            });
            String location[] = callData.getLocation().split(",");
            Double Lat = Double.parseDouble(location[0]);
            Double Long = Double.parseDouble(location[1]);
            latLng = new LatLng(Lat, Long);
            if (markerName != null)
                markerName.remove();
            markerName = googleMap.addMarker(new MarkerOptions().position(latLng).title("Call Location").snippet(""));
            markerName.setDraggable(true);
            markerName.showInfoWindow();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
            updateLocation(latLng);
            TextView locationTv = (TextView) findViewById(R.id.latlongLocation);
            locationTv.setText("LAT:" + Lat + " LONG:" + Long);


        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}