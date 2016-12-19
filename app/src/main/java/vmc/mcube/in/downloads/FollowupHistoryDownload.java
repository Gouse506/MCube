package vmc.mcube.in.downloads;

import android.os.AsyncTask;
import android.util.Log;
import com.android.volley.RequestQueue;
import org.json.JSONObject;
import java.util.ArrayList;
import vmc.mcube.in.activity.HomeActivity;
import vmc.mcube.in.model.Data;
import vmc.mcube.in.model.Params;
import vmc.mcube.in.parsing.Parser;
import vmc.mcube.in.parsing.Requestor;
import vmc.mcube.in.utils.ConnectivityReceiver;
import vmc.mcube.in.utils.SingleTon;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.utils.Utils;

/**
 * Created by gousebabjan on 1/9/16.
 */
public class FollowupHistoryDownload  extends AsyncTask<Void, Void, ArrayList<Data>> implements Tag {
    private RequestQueue requestQueue;
    private SingleTon volleySingleton;
    private Params param;
    private FollowupHistoryFinish  downloadFininshed;
    private boolean isOnline;


    public interface FollowupHistoryFinish {

        void onFollowupHistoryDownLoadFinished(ArrayList<Data> result, boolean isMore, boolean isOnline);

    }


    public FollowupHistoryDownload (FollowupHistoryFinish downloadFininshed, Params param) {
        this.downloadFininshed = downloadFininshed;
        volleySingleton = SingleTon.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        this.param = param;
    }



    @Override
    protected ArrayList<Data> doInBackground(Void... params) {
        isOnline = ConnectivityReceiver.isOnline();
        JSONObject response = null;
        ArrayList<Data> FollowUpDataList=new ArrayList<Data>();
       // if(isOnline) {
            try {
                response = Requestor.getFollowUpHistory(requestQueue,BASE_URL + GET_FOLLOWUP_HISTORY,
                        param.getAuthKey(), param.getLimit(),
                        param.getType(), param.isMore() ? param.getOffset() : 0, param.getDataid());
                if (response != null)
                    FollowUpDataList = Parser.ParseXData(response);
                Log.d("history", response.toString());
            } catch (Exception e) {
            }
        //}
        return FollowUpDataList;


    }


    @Override
    protected void onPostExecute(ArrayList<Data> datas) {
        //super.onPostExecute(datas);
        if (downloadFininshed != null)
            downloadFininshed.onFollowupHistoryDownLoadFinished(datas,param.isMore(),isOnline);
    }
}
