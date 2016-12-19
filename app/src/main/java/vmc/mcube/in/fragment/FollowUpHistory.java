package vmc.mcube.in.fragment;



import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import vmc.mcube.in.R;
import vmc.mcube.in.activity.DetailActivity;
import vmc.mcube.in.adapter.ReportAdapter;
import vmc.mcube.in.downloads.FollowupHistoryDownload;
import vmc.mcube.in.model.Data;
import vmc.mcube.in.model.OptionsData;
import vmc.mcube.in.model.Params;
import vmc.mcube.in.utils.ConnectivityReceiver;
import vmc.mcube.in.utils.Constants;
import vmc.mcube.in.utils.EndlessScrollListener;
import vmc.mcube.in.utils.SingleTon;
import vmc.mcube.in.utils.SpinnerCallBack;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.model.UserData;
import vmc.mcube.in.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowUpHistory extends Fragment implements Tag, SwipeRefreshLayout.OnRefreshListener,
        ReportAdapter.XClickedListner,FollowupHistoryDownload.FollowupHistoryFinish {
    private static final String TAG = "FollowUpHistory";
    int totalCount = 0;
    ArrayAdapter<String> spinneradapter;
    LinearLayoutManager mLayoutManager;
    int count = 0;
    private SpinnerCallBack spinnerCallBack;
    private String recordLimit;
    private String titles[] = {"FollowUp", "Ivrs", "Lead", "MCubeX", "Report", "Settings"};
    private ArrayList<OptionsData> optionslist;
    private ReportAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout mprogressLayout, retrylayout;
    private RecyclerView recyclerView;
    private UserData userData;
    private RelativeLayout rootlayout;
    private String gid = "0";
    private ArrayList<Data> followUpDataArrayList;
    private ArrayList<String> filterGroup;
    private ArrayList<Data> filterArray;
    private Spinner sp;
    private boolean loading = false;
    private LinearLayout pdloadmore;
    private int offset = 0;
    private TextView tvrefresh;
    private RequestQueue requestQueue;
    private SingleTon volleySingleton;
    private String TrackInfo = null;
    private String GroupName, CallId, TYPE,DataId;
    private Data mTrackData;
    private Boolean FirstLoaded=false;
    private ProgressBar progressBar;
    public FollowUpHistory() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_follow_up, container, false);
        Bundle args = getArguments();
        if (args != null) {
            TrackInfo = getArguments().getString("FollowupHistory");
        }
        if(((DetailActivity)getActivity()).mTrackData!=null)
        mTrackData= ((DetailActivity)getActivity()).mTrackData;

        tvrefresh = (TextView) view.findViewById(R.id.tvrefresh);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.SwipefollowUp);
        mprogressLayout = (LinearLayout) view.findViewById(R.id.mprogressLayout);
        retrylayout = (LinearLayout) view.findViewById(R.id.retryLayout);
        rootlayout = (RelativeLayout) view.findViewById(R.id.fragment_followup);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        pdloadmore = (LinearLayout) view.findViewById(R.id.loadmorepd1);
        progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
        mLayoutManager = new LinearLayoutManager(getActivity());
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);
        recordLimit = Utils.getFromPrefs(getActivity(), "recordLimit", "10");
        filterArray = new ArrayList<Data>();
        filterGroup = new ArrayList<String>();
        followUpDataArrayList = new ArrayList<Data>();
        spinneradapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_drop_title, filterGroup);
        if (Integer.valueOf(Build.VERSION.SDK_INT) != 21) {
            spinneradapter.setDropDownViewResource(R.layout.layout_drop_list);
        }

        volleySingleton = SingleTon.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        getActivity().setTitle("History");
        recyclerView.addOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore() {
                if (!loading) {
                    DownloadMore();
                }
            }

            @Override
            public void onLoadUp() {
                if (pdloadmore.getVisibility() == View.VISIBLE) {
                    pdloadmore.setVisibility(View.GONE);
                }
            }
        });
        tvrefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadFollowUp();
            }
        });
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (((DetailActivity)getActivity()).userData != null) {
            DownloadFollowUp();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    public void onPrepareOptionsMenu(Menu menu) {

        menu.clear();
//        MenuItem item = menu.add(getResources().getString(R.string.signout));
//        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                Utils.isLogout1(getActivity());
//                getActivity().finish();
//                return true;
//
//            }
//        });

      //  MenuItem spinner = menu.add("Search");

//        spinner.setActionView(getActivity().getLayoutInflater().inflate(R.layout.spinner_layout, null));
//        //  spinner.setActionView(new Spinner(((AppCompatActivity) getActivity()).getSupportActionBar().getThemedContext()));
//        spinner.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//        spinner.setVisible(false);
//        sp = (Spinner) spinner.getActionView();
//        sp.setAdapter(spinneradapter);
//        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                if (count == 0) {
//                    count++;
//                } else {
//                    Log.d("FILTER1", followUpDataArrayList.size() + " " + sp.getSelectedItem().toString());
//                    getFilteredArray(optionslist, sp.getSelectedItem().toString());
//                }
//
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


        super.onPrepareOptionsMenu(menu);
    }


    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onRefresh() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);

        }
        Constants.Anim = true;
        if (ConnectivityReceiver.isConnected()) {
        DownloadFollowUp();
        }else {
            Snackbar snack = Snackbar.make(rootlayout, "No Internet Connection", Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DownloadFollowUp();
                        }
                    })
                    .setActionTextColor(ContextCompat.getColor(getActivity(),R.color.accent));
            snack.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary));
            TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();
        }
    }

    protected void DownloadFollowUp() {
        if (ConnectivityReceiver.isConnected()) {

            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            if (mprogressLayout.getVisibility()==View.GONE) {
                mprogressLayout.setVisibility(View.VISIBLE);
            }
            if (retrylayout.getVisibility() == View.VISIBLE) {
                retrylayout.setVisibility(View.GONE);
            }
            if (recyclerView.getVisibility() == View.VISIBLE) {
                recyclerView.setVisibility(View.GONE);
            }
            if (pdloadmore.getVisibility() == View.VISIBLE) {
                pdloadmore.setVisibility(View.GONE);
            }
            loading = true;
            Params params = new Params();
            params.setAuthKey(((DetailActivity) getActivity()).userData.getAUTHKEY());
            params.setDataid(mTrackData.getDataId());
            params.setLimit(Utils.getFromPrefs(getActivity(), "recordLimit", "10"));
            params.setOffset(offset);
            params.setType(mTrackData.getType());
            params.setMore(false);
            followUpDataArrayList.clear();
            new FollowupHistoryDownload(this, params).execute();

        }else {
            noInternetConncetion(false);


        }

    }

    protected void DownloadMore() {
        if (ConnectivityReceiver.isConnected()) {
            if (pdloadmore.getVisibility() == View.GONE) {
                pdloadmore.setVisibility(View.VISIBLE);
            }
            loading = true;
            offset = followUpDataArrayList.size();

            Params params = new Params();
            params.setAuthKey(((DetailActivity) getActivity()).userData.getAUTHKEY());
            params.setDataid(mTrackData.getDataId());
            params.setLimit(Utils.getFromPrefs(getActivity(), "recordLimit", "10"));
            params.setOffset(offset);
            params.setType(mTrackData.getType());
            params.setMore(true);
            new FollowupHistoryDownload(this, params).execute();


        } else {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            if (getActivity() != null) {
                offset = offset - Integer.parseInt(Utils.getFromPrefs(getActivity(), "recordLimit", "10"));
                Snackbar snack = Snackbar.make(rootlayout, "No Internet Connection", Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DownloadMore();

                            }
                        })
                        .setActionTextColor(ContextCompat.getColor(getActivity(),R.color.accent));
                snack.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary));
                TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.WHITE);
                snack.show();
            }

        }


    }

    protected void getGroupName(ArrayList<OptionsData> ivrsData) {
        for (int i = 0; i < ivrsData.size(); i++) {
            filterGroup.add(ivrsData.get(i).getOptionName());
        }
        Set<String> hs = new HashSet<>();
        hs.addAll(filterGroup);
        filterGroup.clear();
        filterGroup.addAll(hs);
        Collections.sort(filterGroup);
        Utils.sortArray(filterGroup);
        for (int i = 0; i < filterGroup.size(); i++) {
            if (filterGroup.get(i).equalsIgnoreCase("ALL")) {
                String temp = filterGroup.get(0);
                filterGroup.set(0, filterGroup.get(i));
                filterGroup.set(i, temp);
            }
        }


    }

    protected void getFilteredArray(ArrayList<OptionsData> option, String text) {
        if (option != null) {
            for (int k = 0; k < option.size(); k++) {

                if (option.get(k).getOptionName().equals(text)) {

                    gid = option.get(k).getOptionId();
                    DownloadFollowUp();
                    break;
                }
            }
        }
    }



    @Override
    public void OnItemClick(Data followUpData, int position) {

    }

    @Override
    public void onFollowupHistoryDownLoadFinished(ArrayList<Data> data, final boolean isMore,boolean isOnline) {

        if (recyclerView.getVisibility() == View.GONE) {
            recyclerView.setVisibility(View.VISIBLE);
        }
        if (mprogressLayout.getVisibility() == View.VISIBLE) {
            mprogressLayout.setVisibility(View.GONE);
        }
        if (retrylayout.getVisibility() == View.VISIBLE) {
            retrylayout.setVisibility(View.GONE);
        }
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        loading = false;

        if (optionslist != null && optionslist.size() > 0) {
            getGroupName(optionslist);
        }
        spinneradapter.notifyDataSetChanged();
      //  if (isOnline) {
            if (data != null && data.size() > 0 && getActivity() != null) {
                followUpDataArrayList.addAll(data);
                if (!isMore) {
                    adapter = new ReportAdapter(getActivity(), data, rootlayout, FollowUpHistory.this, TYPE, true);
                    adapter.setClickedListner(FollowUpHistory.this);
                    FirstLoaded = true;
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();

                }

            } else {
                if (!isMore) {
                    if (recyclerView.getVisibility() == View.VISIBLE) {
                        recyclerView.setVisibility(View.GONE);
                    }
                    if (retrylayout.getVisibility() == View.GONE) {
                        retrylayout.setVisibility(View.VISIBLE);
                    }
                    if (pdloadmore.getVisibility() == View.VISIBLE) {
                        pdloadmore.setVisibility(View.GONE);
                    }
                }
                if (getActivity() != null && getView() != null) {
                    try {
                        Snackbar snack = Snackbar.make(getView(), "No Data Available", Snackbar.LENGTH_SHORT)
                                .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (!isMore) {
                                            DownloadFollowUp();
                                        } else {

                                            DownloadMore();
                                        }

                                    }
                                })
                                .setActionTextColor(ContextCompat.getColor(getActivity(),R.color.accent));
                        snack.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary));
                        TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.WHITE);
                        snack.show();
                    } catch (Exception e) {

                    }
                }
            }
//        } else {
//            noInternetConncetion(isMore);
//        }
    }
    private void noInternetConncetion(final boolean isMore) {

        if (isMore) {
            if (pdloadmore.getVisibility() == View.VISIBLE) {
                pdloadmore.setVisibility(View.GONE);
            }
        } else {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }

            if (followUpDataArrayList.size() == 0) {
                if (retrylayout.getVisibility() == View.GONE) {
                    retrylayout.setVisibility(View.VISIBLE);
                }
                if (recyclerView.getVisibility() == View.VISIBLE) {
                    recyclerView.setVisibility(View.GONE);
                }
            }
        }
        try {
            if (getActivity() != null && Constants.position == 0) {
                Snackbar snack = Snackbar.make(rootlayout, "No Internet Connection", Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isMore) {
                                    DownloadMore();
                                } else {
                                    DownloadFollowUp();
                                }

                            }
                        })
                        .setActionTextColor(ContextCompat.getColor(getActivity(),R.color.accent));
                snack.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary));
                TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.WHITE);
                snack.show();
            }

        } catch (Exception e) {

        }
    }

}












