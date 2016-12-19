package vmc.mcube.in.downloads;

import android.os.AsyncTask;
import com.android.volley.RequestQueue;
import org.json.JSONObject;
import java.util.ArrayList;
import vmc.mcube.in.activity.HomeActivity;
import vmc.mcube.in.model.Params;
import vmc.mcube.in.model.TrackDetailsData;
import vmc.mcube.in.parsing.Parser;
import vmc.mcube.in.parsing.Requestor;
import vmc.mcube.in.utils.ConnectivityReceiver;
import vmc.mcube.in.utils.SingleTon;
import vmc.mcube.in.utils.Tag;

/**
 * Created by gousebabjan on 25/7/16.
 */
public class ReportDetailsDownload  extends AsyncTask<Void, Void, ArrayList<TrackDetailsData>> implements Tag {

    private RequestQueue requestQueue;
    private SingleTon volleySingleton;
    private Params param;
    private ReportDetailsFinish downloadFininshed;
    private boolean isOnline;

    public interface ReportDetailsFinish {
        void onReportDetailsDownLoadFinished(ArrayList<TrackDetailsData> result);

    }
    public ReportDetailsDownload(ReportDetailsFinish downloadFininshed, Params param) {
        this.downloadFininshed = downloadFininshed;
        volleySingleton = SingleTon.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        this.param=param;
    }

    @Override
    protected ArrayList<TrackDetailsData> doInBackground(Void... params) {
        JSONObject response = null;
        ArrayList<TrackDetailsData> TrackDataList = new ArrayList<TrackDetailsData>();
        isOnline = ConnectivityReceiver.isOnline();
      //  if(isOnline) {
            try {

                response = Requestor.getReportDetails(requestQueue, BASE_URL + GET_DETAIL, param.getAuthKey(), param.getType(), param.getCallId(), param.getGroupName());
                TrackDataList = Parser.ParseTrackDetailsData(response);

            } catch (Exception e) {
            }
        //}
        return TrackDataList;

    }
    @Override
    protected void onPostExecute(ArrayList<TrackDetailsData> data) {

        if (data != null && data.size() > 0) {
            downloadFininshed.onReportDetailsDownLoadFinished(data);
        }


    }
}
