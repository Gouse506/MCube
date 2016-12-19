package vmc.mcube.in.fragment;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import vmc.mcube.in.R;
import vmc.mcube.in.adapter.Ratings_Adapter;
import vmc.mcube.in.model.RateData;
import vmc.mcube.in.parsing.Parser;
import vmc.mcube.in.parsing.Requestor;
import vmc.mcube.in.utils.ConnectivityReceiver;
import vmc.mcube.in.utils.SingleTon;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.utils.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, Tag {

    private Ratings_Adapter adapter;
    public RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout mprogressLayout, retrylayout;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<RateData> rateDataList;
    public ArrayList<RateData> rateData;
    private String callid;
    private SingleTon volleySingleton;
    private RequestQueue requestQueue;
    private TextView click;
    private ProgressBar progressBar;
    private boolean isOnline;

    public ReviewFragment() {
        // Required empty public constructor
    }

    public static ReviewFragment newInstance(String callData) {
        ReviewFragment fragment = new ReviewFragment();
        Bundle args = new Bundle();
        args.putString(CALLID, callData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            callid = getArguments().getString(CALLID);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_review, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.SwipefollowUp);
        //  mroot = (RelativeLayout) view.findViewById(R.id.fragment_followup);

        mprogressLayout = (LinearLayout) view.findViewById(R.id.mprogressLayout);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        click = (TextView) view.findViewById(R.id.click);
        retrylayout = (LinearLayout) view.findViewById(R.id.retryLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        volleySingleton = SingleTon.getInstance();
        progressBar.getIndeterminateDrawable().setColorFilter(
                Color.RED, PorterDuff.Mode.MULTIPLY);
        requestQueue = volleySingleton.getRequestQueue();
        retrylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadReviews();
            }
        });

        swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);

        DownloadReviews();

        return view;
    }

    @Override
    public void onRefresh() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);

        }
        DownloadReviews();
    }

    protected void DownloadReviews() {
        if (ConnectivityReceiver.isConnected()) {
            new DownloadReviewData().execute();
        } else {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            if (retrylayout.getVisibility() == View.GONE) {
                retrylayout.setVisibility(View.VISIBLE);
            }
            if (recyclerView.getVisibility() == View.VISIBLE) {
                recyclerView.setVisibility(View.GONE);
            }
        }
    }

    class DownloadReviewData extends AsyncTask<Void, Void, ArrayList<RateData>> {
        private String code = "n/a", msg = "n/a";

        @Override
        protected void onPreExecute() {
            if (mprogressLayout.getVisibility() == View.GONE) {
                mprogressLayout.setVisibility(View.VISIBLE);
            }
            if (recyclerView.getVisibility() == View.VISIBLE) {
                recyclerView.setVisibility(View.GONE);
            }

            if (retrylayout.getVisibility() == View.VISIBLE) {
                retrylayout.setVisibility(View.GONE);
            }

            super.onPreExecute();
        }


        @Override
        protected ArrayList<RateData> doInBackground(Void... params) {
            /// TODO Auto-generated method stub
            JSONObject response = null;
            //isOnline = ConnectivityReceiver.isOnline();
            //  if(isOnline) {
            try {

                response = Requestor.getReviews(requestQueue, GET_RATE_URL, Utils.getFromPrefs(getActivity(), AUTHKEY, "N/A"), callid);
                Log.d("TEST", response.toString());
            } catch (Exception e) {
                Log.d("ERROR", e.getMessage().toString());
            }
            // }
            if (response != null) {

                try {

                    if (response.has(CODE)) {
                        code = response.getString(CODE);
                    }
                    if (response.has(MESSAGE)) {
                        msg = response.getString(MESSAGE);
                    }

                    rateDataList = Parser.ParseReview(response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            return rateDataList;
        }

        @Override
        protected void onPostExecute(ArrayList<RateData> data) {

            if (recyclerView.getVisibility() == View.GONE) {
                recyclerView.setVisibility(View.VISIBLE);
            }

            if (mprogressLayout.getVisibility() == View.VISIBLE) {
                mprogressLayout.setVisibility(View.GONE);
            }

            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);

            }
            if (data != null && getActivity() != null && data.size() > 0) {
                adapter = new Ratings_Adapter(getActivity(), data);
                recyclerView.setAdapter(adapter);

            } else if (code.equals("202") || code.equals("401")) {
                if (retrylayout.getVisibility() == View.GONE) {
                    retrylayout.setVisibility(View.VISIBLE);
                }
                if (recyclerView.getVisibility() == View.VISIBLE) {
                    recyclerView.setVisibility(View.GONE);
                }

            } else {

                if (retrylayout.getVisibility() == View.GONE) {
                    retrylayout.setVisibility(View.VISIBLE);
                }

            }
        }

    }
}
