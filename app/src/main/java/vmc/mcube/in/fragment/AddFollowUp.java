package vmc.mcube.in.fragment;


import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import vmc.mcube.in.R;
import vmc.mcube.in.activity.DetailActivity;
import vmc.mcube.in.activity.HomeActivity;
import vmc.mcube.in.adapter.AddFollowupAdapter;
import vmc.mcube.in.downloads.AddFollowupDownload;
import vmc.mcube.in.model.Data;
import vmc.mcube.in.model.NewFollowUpData;
import vmc.mcube.in.parsing.Requestor;
import vmc.mcube.in.utils.ConnectivityReceiver;
import vmc.mcube.in.utils.SingleTon;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddFollowUp extends Fragment implements Tag, AddFollowupDownload.AddFollowupFinish, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "TrackFollowupFragment";
    private ProgressBar trackDetailsProgressBar;
    private Context context;
    ArrayList<NewFollowUpData> trackDetailsFollowupArrayList = new ArrayList<NewFollowUpData>();
    boolean isListLoaded = false;
    private volatile boolean isLoadingInProgress = false;
    private LinkedHashMap<String, String> filterMap = new LinkedHashMap<String, String>();
    private String CallId = null;
    private String Comment = null;
    private String FollowupDate = null;
    private String Alert = null;
    private String FollowupInfo = null;
    private String GroupName = null;
    private Button filterButton;
    private Button submitButton;
    private TextView opsMessageTextView;
    private TextView retryTextView;
    private LinearLayout retryLayout, mprogressLayout;
    private RelativeLayout root;
    private int fragmentNo = 7;
    private Button PopupButton;
    private ImageView imgMenu;
    private String ContactNo, EmailId;
    private RequestQueue requestQueue;
    private SingleTon volleySingleton;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AddFollowupAdapter addFollowupAdapter;
    private String EmpName, TYPE;
    private Data mTrackData;
    private String FTYPE;
    //private boolean isOnline;

    public AddFollowUp() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_follow_up, container, false);

        FollowupInfo = getArguments().getString("FollowupInfo");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        Gson gson = new Gson();
        mTrackData = ((DetailActivity) getActivity()).mTrackData;
        if (mTrackData != null) {
            GroupName = mTrackData.getGroupName();
            EmpName = mTrackData.getEmpName();
            TYPE = mTrackData.getType();
            Log.d("TEST2", "TYPE" + CallId);
        }
        getType(TYPE);


        opsMessageTextView = (TextView) view.findViewById(R.id.tOpsMessageTextView);
        retryTextView = (TextView) view.findViewById(R.id.tRetryTextView);
        retryLayout = (LinearLayout) view.findViewById(R.id.tRetryLayout);
        root = (RelativeLayout) view.findViewById(R.id.fragment_track_followup);
//        trackDetailsProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
//        trackDetailsProgressBar.setVisibility(View.GONE);
        mprogressLayout = (LinearLayout) view.findViewById(R.id.mprogressLayout);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.SwipefollowUp);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);
        volleySingleton = SingleTon.getInstance();
        requestQueue = volleySingleton.getRequestQueue();

        submitButton = (Button) view.findViewById(R.id.btnSubmit);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        if (!isListLoaded) {
            // trackDetailsProgressBar.setVisibility(View.VISIBLE);
            if (mprogressLayout.getVisibility() == View.GONE) {
                mprogressLayout.setVisibility(View.VISIBLE);
            }

        }
        new AddFollowupDownload(this, getActivity()).execute();
        addFollowupAdapter = new AddFollowupAdapter(getActivity(), R.layout.track_list_item, trackDetailsFollowupArrayList);
        recyclerView.setAdapter(addFollowupAdapter);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UpdateTrackFoloupTask UpdateTrackFoloupTask = null;
                getValues();
                if (!FollowupDate.equals("")) {
                    if (UpdateTrackFoloupTask != null && !UpdateTrackFoloupTask.isCancelled()) {
                        UpdateTrackFoloupTask.cancel(true);
                    }
                    if (ConnectivityReceiver.isConnected()) {
                        UpdateTrackFoloupTask = new UpdateTrackFoloupTask();
                        UpdateTrackFoloupTask.execute(null, null, null);
                    } else {
                        noInternetcon();
                    }

                } else {
                    Toast.makeText(getActivity(), "Pick Date/Time..", Toast.LENGTH_SHORT).show();
                }

            }
        });

        if (addFollowupAdapter != null) {
            recyclerView.setAdapter(addFollowupAdapter);
        } else {
            if (!isListLoaded) {
                trackDetailsProgressBar.setVisibility(View.VISIBLE);
                submitButton.setVisibility(View.INVISIBLE);
            }
            isLoadingInProgress = true;
            new AddFollowupDownload(this, getActivity()).execute();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        super.onPrepareOptionsMenu(menu);

    }


    public void getType(String TYPE) {

        switch (TYPE) {
            case TRACK:
                getActivity().setTitle(TYPE.toUpperCase().charAt(0) + TYPE.substring(1) + " " + "Followup");
                FTYPE = "calltrack";
                break;
            case IVRS:
                getActivity().setTitle(TYPE.toUpperCase().charAt(0) + TYPE.substring(1) + " " + "Followup");
                FTYPE = "ivrs";
                break;
            case X:
                getActivity().setTitle("MCubeX" + " " + "Followup");
                FTYPE = "pbx";
                break;
            case LEAD:
                getActivity().setTitle(TYPE.toUpperCase().charAt(0) + TYPE.substring(1) + " " + "Followup");
                FTYPE = "leads";
                break;
            case MTRACKER:
                getActivity().setTitle(TYPE.toUpperCase().charAt(0) + "" + TYPE.toUpperCase().charAt(1) + TYPE.substring(2) + " " + "Followup");
                FTYPE = "mtracker";
                break;
            case "followup":
                getActivity().setTitle(TYPE.toUpperCase().charAt(0) + TYPE.substring(1) + " " + "Followup");
                FTYPE = "followup";
                break;

        }

    }


    private void getValues() {
        for (int j = 0; j < trackDetailsFollowupArrayList.size(); j++) {

            if (trackDetailsFollowupArrayList.get(j).getName().equalsIgnoreCase("comment")) {

                Comment = trackDetailsFollowupArrayList.get(j).getValue();
                Log.d("UpdatePArams", "Comment" + Comment);

            }

            if (trackDetailsFollowupArrayList.get(j).getName().equalsIgnoreCase("followupdate")) {

                FollowupDate = trackDetailsFollowupArrayList.get(j).getValue();
                Log.d("UpdatePArams", "FollowupDate" + FollowupDate);

            }
            if (trackDetailsFollowupArrayList.get(j).getName().equalsIgnoreCase("alert")) {

                Alert = trackDetailsFollowupArrayList.get(j).getValue();
                Log.d("UpdatePArams", "Alert" + Alert);
            }

        }


    }

    @Override
    public void onAddFollowupFinished(ArrayList<NewFollowUpData> data) {

        isLoadingInProgress = false;
        for (int i = 0; i < trackDetailsFollowupArrayList.size(); i++) {
            if (trackDetailsFollowupArrayList.get(i).getName().equalsIgnoreCase(CALLFROM)) {
                ContactNo = trackDetailsFollowupArrayList.get(i).getValue();
            } else if (trackDetailsFollowupArrayList.get(i).getName().equalsIgnoreCase(CALLEREMAIL)) {
                EmailId = trackDetailsFollowupArrayList.get(i).getValue();
            }
        }

       // trackDetailsProgressBar.setVisibility(View.GONE);
        if (mprogressLayout.getVisibility() == View.VISIBLE) {
            mprogressLayout.setVisibility(View.GONE);
        }
        isListLoaded = true;

        if (data != null) {
            submitButton.setVisibility(View.VISIBLE);

            trackDetailsFollowupArrayList = data;
            addFollowupAdapter = new AddFollowupAdapter(getActivity(), R.layout.track_list_item, trackDetailsFollowupArrayList);
            recyclerView.setAdapter(addFollowupAdapter);
            recyclerView.invalidate();
            addFollowupAdapter.notifyDataSetChanged();

        }
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);

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

    @Override
    public void onRefresh() {
        // isOnline = ConnectivityReceiver.isOnline();
        if (ConnectivityReceiver.isConnected()) {
            new AddFollowupDownload(this, getActivity()).execute();
        } else {
            noInternetcon();
        }

    }

    private class UpdateTrackFoloupTask extends AsyncTask<Void, Void, String> {
        String code;
        String msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // trackDetailsProgressBar.setVisibility(View.VISIBLE);
            if (mprogressLayout.getVisibility() == View.GONE) {
                mprogressLayout.setVisibility(View.VISIBLE);
            }

        }

        @Override
        protected String doInBackground(Void... params) {
            // isOnline = ConnectivityReceiver.isOnline();
            JSONObject response = null;
            //  if(isOnline) {
            try {
                response = Requestor.addFollowUp(requestQueue,BASE_URL + UPDATE_DETAILS_URL, ((DetailActivity) getActivity()).userData.getAUTHKEY(), trackDetailsFollowupArrayList,
                        mTrackData.getCallId(), mTrackData.getGroupName().equals("") ? mTrackData.getEmpName() : mTrackData.getGroupName(), FTYPE);
                Log.d("TEST2 ", response.toString());
                code = response.getString(CODE);
                msg = response.getString(MESSAGE);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // }

            return code;
        }

        @Override
        protected void onPostExecute(String code) {
           // trackDetailsProgressBar.setVisibility(View.GONE);
            if (mprogressLayout.getVisibility() == View.VISIBLE) {
                mprogressLayout.setVisibility(View.GONE);
            }
            super.onPostExecute(code);

            if (code.equalsIgnoreCase("202")) {
                //Toast.makeText(getActivity(), "Updated Successfully.", Toast.LENGTH_SHORT).show();
                Utils.callConnectAlert(getActivity(),"Followup Added.",false);
            } else {
               // Toast.makeText(getActivity(), "Updated Successfully.", Toast.LENGTH_SHORT).show();
                Utils.callConnectAlert(getActivity(),msg,false);
            }
        }

    }


}
