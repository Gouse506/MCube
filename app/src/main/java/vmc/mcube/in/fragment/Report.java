package vmc.mcube.in.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import vmc.mcube.in.R;
import vmc.mcube.in.activity.ClickToConnect;
import vmc.mcube.in.activity.DetailActivity;
import vmc.mcube.in.activity.HomeActivity;
import vmc.mcube.in.activity.MyApplication;
import vmc.mcube.in.adapter.ReportAdapter;
import vmc.mcube.in.downloads.ReportDownload;
import vmc.mcube.in.model.Data;
import vmc.mcube.in.model.OptionsData;
import vmc.mcube.in.model.Params;
import vmc.mcube.in.utils.ConnectivityReceiver;
import vmc.mcube.in.utils.Constants;
import vmc.mcube.in.utils.EndlessScrollListener;
import vmc.mcube.in.utils.SingleTon;
import vmc.mcube.in.utils.SpinnerCallBack;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.utils.Utils;


public class Report extends Fragment implements SwipeRefreshLayout.OnRefreshListener, Tag, ReportAdapter.XClickedListner, AdapterView.OnItemSelectedListener, ReportDownload.ReportFinish {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    int totalCount = 0;
    int count = 0;
    private Boolean FirstLoaded = false;
    private String gid = "0";
    private SpinnerCallBack spinnerCallBack;
    // TODO: Rename and change types of parameters
    private String TYPE;
    private String mParam2;
    private ArrayList<OptionsData> optionslist;
    private String recordLimit;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout mprogressLayout;
    private RecyclerView recyclerView;
    private ReportAdapter adapter;
    private RelativeLayout rootlayout;
    private ArrayList<Data> TrackDataArrayList;
    private ArrayList<String> filterGroup;
    private ArrayList<Data> filterArray;
    private Spinner sp;
    private ArrayAdapter<String> spinneradapter;
    private LinearLayout retrylayout;
    private int offset = 0;
    private LinearLayout pdloadmore;
    private boolean loading = false;
    private boolean isViewShown;
    private TextView tvrefresh;
    private RequestQueue requestQueue;
    private SingleTon volleySingleton;
    private Bundle bundle;
    private int pos = 0;
    View view;
    private ReportDownload.ErrorHandle errorHandle;
    private FloatingActionButton floatingButtoncall;
    private EditText result;

    private FloatingActionButton fbcall;

    public Report() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Report newInstance(String param1) {
        Report fragment = new Report();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        if (getArguments() != null) {
            TYPE = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_x, container, false);
        fbcall = ((HomeActivity) getActivity()).floatingButtoncall;
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.SwipefollowUp);
        mprogressLayout = (LinearLayout) view.findViewById(R.id.mprogressLayout);
        retrylayout = (LinearLayout) view.findViewById(R.id.retryLayout);
        rootlayout = (RelativeLayout) view.findViewById(R.id.fragment_track);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeRefreshLayout.setOnRefreshListener(this);
        recordLimit = Utils.getFromPrefs(getActivity(), "recordLimit", "10");
        TrackDataArrayList = new ArrayList<Data>();
        optionslist = new ArrayList<OptionsData>();
        pdloadmore = (LinearLayout) view.findViewById(R.id.loadmorepd1);
        filterGroup = new ArrayList<String>();
        filterArray = new ArrayList<Data>();
        tvrefresh = (TextView) view.findViewById(R.id.tvrefresh);
        volleySingleton = SingleTon.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        TYPE = ((HomeActivity) getActivity()).TYPE;
        Log.d("TYPE", TYPE);
        if (TYPE == null) {
            TYPE = TRACK;
        }

//        TrackDataArrayList = MyApplication.getWritableDatabase().getData(TYPE);
//        optionslist = MyApplication.getWritableDatabase().getMenuList(TYPE);
        spinneradapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_drop_title, filterGroup);
        if (Build.VERSION.SDK_INT != 21) {
            spinneradapter.setDropDownViewResource(R.layout.layout_drop_list);
        }
        tvrefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadTrack();
            }
        });
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
        swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);
       // FetchData(TYPE);
//        adapter = new ReportAdapter(getActivity(), TrackDataArrayList, rootlayout, Report.this, TYPE, false);
//        adapter.setClickedListner(Report.this);
//        adapter.notifyDataSetChanged();
//        recyclerView.setAdapter(adapter);

        return view;

    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } else {
            // writeToLog("Software Keyboard was not shown");
        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("type", TYPE);

    }


    @SuppressLint("NewApi")
    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        menu.clear();

        MenuItem spinner = menu.add("Search");
        spinner.setActionView(getActivity().getLayoutInflater().inflate(R.layout.spinner_layout, null));
        spinner.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        sp = (Spinner) spinner.getActionView();
        sp.setAdapter(spinneradapter);
        sp.setOnItemSelectedListener(this);


        super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TYPE = ((HomeActivity) getActivity()).TYPE;
        if (savedInstanceState != null) {
            TYPE = savedInstanceState.getString("type");
        }
        if (TYPE.equals("x")) {
            ((HomeActivity) getActivity()).setTitlee("MCubeX");
        } else if (TYPE.equals("mtracker")) {
            ((HomeActivity) getActivity()).setTitlee(TYPE.toUpperCase().charAt(0) + "" + TYPE.toUpperCase().charAt(1) + TYPE.substring(2));
        } else {
            ((HomeActivity) getActivity()).setTitlee(TYPE.toUpperCase().charAt(0) + TYPE.substring(1));
        }
        FetchData(TYPE);

    }

    public void FetchData(String Type) {
        TYPE = Type;
        if (retrylayout.getVisibility() == View.VISIBLE) {
            retrylayout.setVisibility(View.GONE);
        }
        if (recyclerView.getVisibility() == View.GONE) {
            recyclerView.setVisibility(View.VISIBLE);
        }
      //  if (!TYPE.equals(FOLLOWUP)) {
            TrackDataArrayList = MyApplication.getWritableDatabase().getData(TYPE);
            optionslist = MyApplication.getWritableDatabase().getMenuList(TYPE);
       // }
        if (optionslist != null && optionslist.size() > 0) {
            getGroupName(optionslist);
        }
        spinneradapter.notifyDataSetChanged();
        if (TrackDataArrayList != null && TrackDataArrayList.size() <= 0) {
            DownloadTrack();
        } else {
            adapter = new ReportAdapter(getActivity(), TrackDataArrayList, rootlayout, Report.this, TYPE, false);
            adapter.setClickedListner(Report.this);
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            FirstLoaded = true;

        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof Activity) {
            spinnerCallBack = (HomeActivity) context;
            spinnerCallBack.ResetSpinner();
        }
        super.onAttach(context);


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
        if (!ConnectivityReceiver.isConnected()) {
            if (TrackDataArrayList != null && TrackDataArrayList.size() > 0) {
                FetchData(TYPE);
            }
            Snackbar snack = Snackbar.make(rootlayout, "No Internet Connection", Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DownloadTrack();
                        }
                    })
                    .setActionTextColor(ContextCompat.getColor(getActivity(), R.color.accent));
            snack.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary));
            TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();
        } else {
            DownloadTrack();
        }


    }

    @Override
    public void OnItemClick(Data trackData, int position) {
        if (ConnectivityReceiver.isConnected()) {
            //Data Parcel = new Data(TrackDataArrayList.get(position).getCallId(), TrackDataArrayList.get(position).getGroupName());
            Data Parcel = trackData;
            Parcel.setType(TYPE);
            // Parcel.setDataId(TrackDataArrayList.get(position).getDataId());
            //Parcel.setEmpName(TrackDataArrayList.get(position).getEmpName());
            Gson gson = new Gson();
            String TrackInfo = gson.toJson(Parcel);
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra("DATA", TrackInfo);
            //startActivity(intent);
            startActivityForResult(intent, 0);
        } else {
            Snackbar snack = Snackbar.make(rootlayout, "No Internet Connection", Snackbar.LENGTH_SHORT)
                    .setActionTextColor(ContextCompat.getColor(getActivity(), R.color.accent));
            snack.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary));
            TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            snack.show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (count == 0) {
            count++;
        } else {
            if (FirstLoaded && getActivity() != null) {
                getFilteredArray(optionslist, sp.getSelectedItem().toString());
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    protected void DownloadTrack() {
        if (ConnectivityReceiver.isConnected()) {

            if (mprogressLayout.getVisibility() == View.GONE) {
                mprogressLayout.setVisibility(View.VISIBLE);
            }

            if (recyclerView.getVisibility() == View.VISIBLE) {
                recyclerView.setVisibility(View.GONE);
            }
            if (retrylayout.getVisibility() == View.VISIBLE) {
                retrylayout.setVisibility(View.GONE);
            }
            if (pdloadmore.getVisibility() == View.VISIBLE) {
                pdloadmore.setVisibility(View.GONE);
            }
            loading = true;
            Params params = new Params();
            params.setAuthKey(((HomeActivity) getActivity()).userData.getAUTHKEY());
            params.setGid(gid);
            params.setLimit(Utils.getFromPrefs(getActivity(), "recordLimit", "10"));
            params.setOffset(offset);
            params.setType(TYPE);
            params.setMore(false);
            TrackDataArrayList.clear();
            new ReportDownload(this, params, errorHandle).execute();


        } else {
            noInternetConncetion(false);


        }


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

            if (TrackDataArrayList.size() == 0) {
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
                                    DownloadTrack();
                                }

                            }
                        })
                        .setActionTextColor(ContextCompat.getColor(getActivity(), R.color.accent));
                snack.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary));
                TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.WHITE);
                snack.show();
            }
            if (TrackDataArrayList != null && TrackDataArrayList.size() > 0) {
                FetchData(TYPE);
            }
        } catch (Exception e) {

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

    protected void DownloadMore() {
        if (ConnectivityReceiver.isConnected()) {

            if (pdloadmore.getVisibility() == View.GONE) {
                pdloadmore.setVisibility(View.VISIBLE);
            }
            loading = true;
            offset = TrackDataArrayList.size();

            Params params = new Params();
            params.setAuthKey(((HomeActivity) getActivity()).userData.getAUTHKEY());
            params.setGid(gid);
            params.setLimit(Utils.getFromPrefs(getActivity(), "recordLimit", "10"));
            params.setOffset(offset);
            params.setType(TYPE);
            params.setMore(true);
            new ReportDownload(this, params, errorHandle).execute();

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
                        .setActionTextColor(ContextCompat.getColor(getActivity(), R.color.accent));
                snack.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary));
                TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.WHITE);
                snack.show();
            }

        }


    }

    protected void getFilteredArray(ArrayList<OptionsData> option, String text) {
        if (option != null) {
            for (int k = 0; k < option.size(); k++) {
                if (option.get(k).getOptionName().equals(text)) {
                    gid = option.get(k).getOptionId();
                    DownloadTrack();
                    break;
                }
            }
        }
    }


    @Override
    public void onReportDownLoadFinished(ArrayList<Data> data, final boolean isMore, ArrayList<OptionsData> optionslist, boolean isOnline) {

        if (pdloadmore.getVisibility() == View.VISIBLE) {
            pdloadmore.setVisibility(View.GONE);
        }
        if (recyclerView.getVisibility() == View.GONE) {
            recyclerView.setVisibility(View.VISIBLE);
        }

        if (mprogressLayout.getVisibility() == View.VISIBLE) {
            mprogressLayout.setVisibility(View.GONE);
        }

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }

        loading = false;

        if (optionslist != null && optionslist.size() > 0) {
            getGroupName(optionslist);
        }
        spinneradapter.notifyDataSetChanged();

        // if (isOnline) {

        if (data != null && data.size() > 0 && getActivity() != null) {
            TrackDataArrayList.addAll(data);

            if (!isMore) {
                adapter = new ReportAdapter(getActivity(), TrackDataArrayList, rootlayout, Report.this, TYPE, false);
                adapter.setClickedListner(Report.this);
                FirstLoaded = true;
                recyclerView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
                //adapter.notifyItemRangeChanged(0, adapter.getItemCount());

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
            if (getActivity() != null && getView() != null && isMore) {
                try {
                    Snackbar snack = Snackbar.make(rootlayout, "No Data Available", Snackbar.LENGTH_SHORT)
                            .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!isMore) {
                                        DownloadTrack();
                                    } else {

                                        DownloadMore();
                                    }

                                }
                            })
                            .setActionTextColor(ContextCompat.getColor(getActivity(), R.color.accent));
                    snack.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary));
                    TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.WHITE);
                    snack.show();
                } catch (Exception e) {

                }
            }
        }

    }


}


