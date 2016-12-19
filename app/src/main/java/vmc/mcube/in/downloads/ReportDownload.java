package vmc.mcube.in.downloads;

import android.os.AsyncTask;
import android.util.Log;
import com.android.volley.RequestQueue;
import org.json.JSONObject;
import java.util.ArrayList;
import vmc.mcube.in.activity.HomeActivity;
import vmc.mcube.in.activity.MyApplication;
import vmc.mcube.in.model.Data;
import vmc.mcube.in.model.OptionsData;
import vmc.mcube.in.model.Params;
import vmc.mcube.in.parsing.Parser;
import vmc.mcube.in.parsing.Requestor;
import vmc.mcube.in.utils.ConnectivityReceiver;
import vmc.mcube.in.utils.SingleTon;
import vmc.mcube.in.utils.Tag;

/**
 * Created by gousebabjan on 21/7/16.
 */


public class ReportDownload extends AsyncTask<Void, Void, ArrayList<Data>> implements Tag {
    private RequestQueue requestQueue;
    private SingleTon volleySingleton;
    private Params param;
    private boolean isOnline;
    private ReportFinish downloadFininshed;
    private ArrayList<OptionsData> optionslist;
    private ErrorHandle errorHandle;


    public interface ReportFinish {
        void onReportDownLoadFinished(ArrayList<Data> result, boolean isMore, ArrayList<OptionsData> optionslist, boolean isOnline);

    }
    public interface ErrorHandle {
       void exceptionHandle(Exception e);
    }

    public ReportDownload(Params params) {
        this.param = params;
        volleySingleton = SingleTon.getInstance();
        requestQueue = volleySingleton.getRequestQueue();

    }


    public ReportDownload(ReportFinish downloadFininshed, Params param,ErrorHandle errorHandle) {
        this.downloadFininshed = downloadFininshed;
        volleySingleton = SingleTon.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        this.param = param;
        this.errorHandle=errorHandle;
    }

    @Override
    protected ArrayList<Data> doInBackground(Void... params) {

        JSONObject response = null;
        ArrayList<Data> TrackDataList = new ArrayList<Data>();
        optionslist = new ArrayList<OptionsData>();
        isOnline = ConnectivityReceiver.isOnline();
       // if (isOnline) {
            try {
                response = Requestor.getReport(requestQueue, BASE_URL + GET_LIST_URL,
                        param.getAuthKey(), param.getLimit(), param.getType(), param.getGid(), param.isMore() ? param.getOffset() : 0);
                TrackDataList = Parser.ParseXData(response);
                Log.d("REPORT", response.toString() +param.getGid());
                optionslist = Parser.ParseMenuOptions(response);
            } catch (Exception e) {
                // Log.d("ERROR in Downl", e.getMessage().toString());
                //errorHandle.exceptionHandle(e);
            }

       // }

        return TrackDataList;
    }


    @Override
    protected void onPostExecute(ArrayList<Data> data) {

        if (data != null && data.size() > 0) {

            MyApplication.getWritableDatabase().insertData(param.getType().equals(TRACK) ? TRACK :
                    param.getType().equals(IVRS) ? IVRS :
                            param.getType().equals(X) ? X :
                                    param.getType().equals(LEAD) ? LEAD :
                                            param.getType().equals(MTRACKER) ? MTRACKER :
                                                    FOLLOWUP, data, !param.isMore());
        }


        if (data != null && data.size() > 0 && param.isSync()) {

            Log.d("TEST21", " " + param.getType());
            MyApplication.getWritableDatabase().insertData(param.getType().equals(TRACK) ? TRACK :
                    param.getType().equals(IVRS) ? IVRS :
                            param.getType().equals(X) ? X :
                                    param.getType().equals(LEAD) ? LEAD :
                                            param.getType().equals(MTRACKER) ? MTRACKER :
                                                    FOLLOWUP, data, true);
        }

        if (optionslist != null && optionslist.size() > 0) {
            MyApplication.getWritableDatabase().insertMENU(param.getType().equals(TRACK) ? TRACK :
                    param.getType().equals(IVRS) ? IVRS :
                            param.getType().equals(X) ? X :
                                    param.getType().equals(LEAD) ? LEAD :
                                            param.getType().equals(MTRACKER) ? MTRACKER :
                                                    FOLLOWUP, optionslist, true);

        }

        if (downloadFininshed != null)
            downloadFininshed.onReportDownLoadFinished(data, param.isMore(), optionslist, isOnline);
    }





}
