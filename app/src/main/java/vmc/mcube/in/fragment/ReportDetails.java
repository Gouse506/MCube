package vmc.mcube.in.fragment;


import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import vmc.mcube.in.R;
import vmc.mcube.in.activity.DetailActivity;
import vmc.mcube.in.activity.HomeActivity;
import vmc.mcube.in.adapter.ReportDetailsAdapter;
import vmc.mcube.in.downloads.ReportDetailsDownload;
import vmc.mcube.in.model.OptionsData;
import vmc.mcube.in.model.Params;
import vmc.mcube.in.model.TrackDetailsData;
import vmc.mcube.in.model.Data;
import vmc.mcube.in.parsing.Requestor;
import vmc.mcube.in.utils.ConnectivityReceiver;
import vmc.mcube.in.utils.SingleTon;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.utils.Utils;

import static android.R.attr.visible;


public class ReportDetails extends Fragment implements Tag, ReportDetailsDownload.ReportDetailsFinish, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private static final String TAG = "TrackDetailsFragment";
    private ProgressBar trackDetailsProgressBar;
    private Context context;

    private Button update, followup, followupList;
    ArrayList<TrackDetailsData> trackDetailsArrayList = new ArrayList<TrackDetailsData>();
    ArrayList<OptionsData> OptionsArrayList = null;
    ArrayList<String> OptionStringArrayList = null;
    int totalCount = 0;
    int startIndex = 0;
    boolean isListLoaded = false;
    private String callid, groupname;
    private ReportDetailsAdapter trackListAdapter;
    private RecyclerView recyclerView;
    private ReportDetailsDownload downLoadFragmentDetails;
    private SwipeRefreshLayout swipeRefreshLayout;
    private volatile boolean isLoadingInProgress = false;
    private LinkedHashMap<String, String> filterMap = new LinkedHashMap<String, String>();
    private String defaultKey = "0";
    private String selectedKey = null;
    private String CallId = null;
    private String ContactNo = null;
    private String EmailId = null;
    private String CallerName = null;
    private String Remarks = null;
    private String CallerBusiness = null;
    private String AssignTo = null;
    private String CallerAddress = null;
    private String Custom = null;
    private String TrackInfo = null;
    private String GroupName = null;
    private Button filterButton;
    private TextView opsMessageTextView;
    private TextView retryTextView;
    private LinearLayout retryLayout,progressLayout;
    private RelativeLayout root;
    // private LinearLayout root;
    private Data mTrackData;
    private String Value;
    private RequestQueue requestQueue;
    private SingleTon volleySingleton;
    private Params params;
    private String TYPE, DataId;
    private boolean isOnline;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_track_details, container, false);
        Bundle args = getArguments();
        if (args != null) {
            TrackInfo = getArguments().getString("TrackInfo");
        }

        volleySingleton = SingleTon.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //Restore the fragment's state here
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTrackData = ((DetailActivity) getActivity()).mTrackData;
        if (mTrackData != null) {
            CallId = mTrackData.getCallId();
            DataId = mTrackData.getDataId();
            GroupName = mTrackData.getGroupName();
            GroupName = mTrackData.getEmpName();
            TYPE = mTrackData.getType();
            ContactNo = mTrackData.getCallFrom();
        }

        opsMessageTextView = (TextView) view.findViewById(R.id.tOpsMessageTextView);
        retryTextView = (TextView) view.findViewById(R.id.tRetryTextView);
        retryLayout = (LinearLayout) view.findViewById(R.id.tRetryLayout);
        root = (RelativeLayout) view.findViewById(R.id.fragment_track_details);
        update = (Button) view.findViewById(R.id.btnUpdate);
        followup = (Button) view.findViewById(R.id.btnfollowUp);
        followupList = (Button) view.findViewById(R.id.btnfollowUpList);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.SwipefollowUp);
        swipeRefreshLayout.setOnRefreshListener(this);
//        trackDetailsProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
//        trackDetailsProgressBar.setVisibility(View.GONE);
        progressLayout= (LinearLayout) view.findViewById(R.id.mprogressLayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        followupList.setOnClickListener(this);
        followup.setOnClickListener(this);
        update.setOnClickListener(this);
        if (TYPE.equals("x")) {
            getActivity().setTitle("MCubeX" + " " + "Details");
        } else if (TYPE.equals("mtracker")) {
            getActivity().setTitle(TYPE.toUpperCase().charAt(0) + "" + TYPE.toUpperCase().charAt(1) + TYPE.substring(2) + " " + "Details");
        } else {
            getActivity().setTitle(TYPE.toUpperCase().charAt(0) + TYPE.substring(1) + " " + "Details");
        }

        if (TYPE.equals("followup")) {
            update.setVisibility(View.GONE);
            followupList.setVisibility(View.VISIBLE);
            followupList.setText("History");
            followupList.setTextSize(12);
        }
        swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);
        trackListAdapter = new ReportDetailsAdapter(getActivity(), R.layout.track_list_item, trackDetailsArrayList);
        recyclerView.setAdapter(trackListAdapter);


        params = new Params();
        params.setAuthKey(((DetailActivity) getActivity()).userData.getAUTHKEY());
        params.setCallId(mTrackData.getCallId());
        params.setGroupName(mTrackData.getGroupName());
        params.setType(TYPE);

        if (!isListLoaded) {
          //  trackDetailsProgressBar.setVisibility(View.VISIBLE);
            if(progressLayout.getVisibility()==View.GONE){
                progressLayout.setVisibility(View.VISIBLE);
            }
            hideNoDataPresent();
            hideRetry();
        }
        isLoadingInProgress = true;

        downLoadFragmentDetails = new ReportDetailsDownload(this, params);
        downLoadFragmentDetails.execute();
    }

    private void getValues() {
        for (int j = 0; j < trackDetailsArrayList.size(); j++) {
            if (trackDetailsArrayList.get(j).getName().equalsIgnoreCase("callername")) {

                CallerName = trackDetailsArrayList.get(j).getValue();

            }
            if (trackDetailsArrayList.get(j).getName().equalsIgnoreCase("remark")) {

                Remarks = trackDetailsArrayList.get(j).getValue();


            }
            if (trackDetailsArrayList.get(j).getName().equalsIgnoreCase("assignto")) {

                AssignTo = trackDetailsArrayList.get(j).getValue();
            }


            if (trackDetailsArrayList.get(j).getName().equalsIgnoreCase("calleraddress")) {

                CallerAddress = trackDetailsArrayList.get(j).getValue();
            }
            if (trackDetailsArrayList.get(j).getName().equalsIgnoreCase("callerbusiness")) {

                CallerBusiness = trackDetailsArrayList.get(j).getValue();

            }


            if (trackDetailsArrayList.get(j).getType().equalsIgnoreCase("checkbox")) {
                ArrayList<String> values = new ArrayList<String>();
                for (int i = 0; i < trackDetailsArrayList.get(j).getOptionsList().size(); i++) {
                    if (trackDetailsArrayList.get(j).getOptionsList().get(i).isChecked()) {

                        values.add(trackDetailsArrayList.get(j).getOptionsList().get(i).getOptionId());

                        trackDetailsArrayList.get(j).setValues(values);
                    }
                }

            }


            Custom = trackDetailsArrayList.get(j).getName();
            Log.d("UpdatePArams", "Id " + Custom);
            Value = trackDetailsArrayList.get(j).getValue();
            Log.d("UpdatePArams", "Value  " + Value);

        }


    }


    @Override
    public void onReportDetailsDownLoadFinished(ArrayList<TrackDetailsData> data) {

       // trackDetailsProgressBar.setVisibility(View.GONE);
        if(progressLayout.getVisibility()==View.VISIBLE){
            progressLayout.setVisibility(View.GONE);
        }
        isListLoaded = true;
        //isOnline = ConnectivityReceiver.isOnline();
        isLoadingInProgress = false;
        // if (isOnline) {
        if (data != null && getActivity() != null) {
            trackDetailsArrayList = data;
            trackListAdapter = new ReportDetailsAdapter(getActivity(), R.layout.track_list_item, trackDetailsArrayList);
            recyclerView.setAdapter(trackListAdapter);
            recyclerView.invalidate();
            trackListAdapter.notifyDataSetChanged();
        }
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);

        }
        //}


    }


    private void hideNoDataPresent() {
        retryLayout.setVisibility(View.GONE);
    }

    @Override
    public void onRefresh() {
        // isOnline = ConnectivityReceiver.isOnline();
        if (ConnectivityReceiver.isConnected()) {
            downLoadFragmentDetails = new ReportDetailsDownload(this, params);
            downLoadFragmentDetails.execute();
        } else {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);

            }
            noInternetcon();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdate:
                // isOnline = ConnectivityReceiver.isOnline();
                if (ConnectivityReceiver.isConnected()) {
                    Update();
                } else {
                    noInternetcon();
                }
                break;
            case R.id.btnfollowUp:
                //  isOnline = ConnectivityReceiver.isOnline();
                if (ConnectivityReceiver.isConnected()) {
                    Followup();
                } else {
                    noInternetcon();
                }

                break;
            case R.id.btnfollowUpList:
                // isOnline = ConnectivityReceiver.isOnline();
                if (ConnectivityReceiver.isConnected()) {
                    FollowupList();
                } else {
                    noInternetcon();
                }
                break;
        }
    }

    private void noInternetcon() {
        Snackbar snackbar = Snackbar
                .make(root, "No Internet Connection.", Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary));
        TextView tv = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        snackbar.show();
    }

    private void hideRetry() {
        retryLayout.setVisibility(View.GONE);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        if (getActivity() != null) {
            menu.clear();

            MenuItem item1 = menu.add(getResources().getString(R.string.update));
            item1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Update();
                    return true;

                }
            });
            MenuItem item2 = menu.add(getResources().getString(R.string.followup));
            item2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Followup();
                    return true;

                }
            });
//        MenuItem item6 = menu.add(getResources().getString(R.string.Listfollowup));
//        item6.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                FollowupList();
//                return true;
//
//            }
//        });
            MenuItem item3 = menu.add(getResources().getString(R.string.sms));
            item3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Utils.sendSms(ContactNo, getActivity());
                    return true;

                }
            });
            MenuItem item5 = menu.add(getResources().getString(R.string.call));
            item5.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Utils.makeAcall(ContactNo, getActivity());
                    return true;

                }
            });

            MenuItem item4 = menu.add(getResources().getString(R.string.email));
            item4.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Utils.sendAnEmail(EmailId, getActivity());
                    return true;

                }
            });
        }
        super.onPrepareOptionsMenu(menu);

    }


    public void Update() {
        UpdateTrackTask updateTrackTask = null;

        if (updateTrackTask != null && !updateTrackTask.isCancelled()) {
            updateTrackTask.cancel(true);
        }
        updateTrackTask = new UpdateTrackTask();
        updateTrackTask.execute(null, null, null);

    }

    public void Followup() {
        if (!isLoadingInProgress) {
            Fragment fr = new AddFollowUp();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            Bundle args = new Bundle();
            args.putString("FollowupInfo", TrackInfo);
            fr.setArguments(args);
            transaction.detach(new ReportDetails()).replace(R.id.fragment_track_details, fr).attach(fr).addToBackStack("back").commit();


        }
    }

    public void FollowupList() {
        if (!isLoadingInProgress) {
            Fragment fr = new FollowUpHistory();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            Bundle args = new Bundle();
            args.putString("FollowupHistory", TrackInfo);
            fr.setArguments(args);
            transaction.detach(new ReportDetails()).replace(R.id.fragment_track_details, fr).attach(fr).addToBackStack("back").commit();

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update:
                Update();
                return true;
            case R.id.followup:
                Followup();
                return true;
            case R.id.call:
                Utils.makeAcall(ContactNo, getActivity());
                return true;
            case R.id.sms:
                Utils.sendSms(ContactNo, getActivity());
                return true;
            case R.id.email:
                Utils.sendAnEmail(EmailId, getActivity());
                return true;
            default:
                break;
        }

        return false;
    }


    private class UpdateTrackTask extends AsyncTask<Void, Void, String> {
        String code;
        String msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // trackDetailsProgressBar.setVisibility(View.VISIBLE);
            if(progressLayout.getVisibility()==View.GONE){
                progressLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            //  isOnline = ConnectivityReceiver.isOnline();

            getValues();
            if (ConnectivityReceiver.isConnected()) {
                try {
                    JSONObject response1 = Requestor.updateReportDetails(requestQueue,BASE_URL + UPDATE_DETAILS_URL, trackDetailsArrayList, ((DetailActivity) getActivity()).userData.getAUTHKEY(), mTrackData.getType(), mTrackData.getGroupName());
                    System.out.println(response1);
                    Log.d("TEST2 ", response1.toString());

                    code = response1.getString(CODE);
                    msg = response1.getString(MESSAGE);
                    Log.d("Code", code + "___" + msg);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                noInternetcon();
            }

            return code;
        }


        @Override
        protected void onPostExecute(String code) {
            super.onPostExecute(code);
           // trackDetailsProgressBar.setVisibility(View.GONE);
            if(progressLayout.getVisibility()==View.VISIBLE){
                progressLayout.setVisibility(View.GONE);
            }
            if (code != null) {

                if (code.equalsIgnoreCase("202") && getActivity() != null) {
                    //Toast.makeText(getActivity(), "Updated successfully.", Toast.LENGTH_SHORT).show();
                    Utils.callConnectAlert(getActivity(),"Details updated.",false);
                } else if (getActivity() != null) {
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
}
