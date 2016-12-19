package vmc.mcube.in.downloads;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.RequestQueue;
import org.json.JSONObject;
import java.util.ArrayList;
import vmc.mcube.in.activity.HomeActivity;
import vmc.mcube.in.model.NewFollowUpData;
import vmc.mcube.in.model.UserData;
import vmc.mcube.in.parsing.Parser;
import vmc.mcube.in.parsing.Requestor;
import vmc.mcube.in.utils.ConnectivityReceiver;
import vmc.mcube.in.utils.SingleTon;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.utils.Utils;

/**
 * Created by gousebabjan on 29/7/16.
 */
public class AddFollowupDownload  extends AsyncTask<Void, Void, ArrayList<NewFollowUpData>> implements Tag {

    private RequestQueue requestQueue;
    private SingleTon volleySingleton;
    private AddFollowupFinish downloadFininshed;
    private UserData userData;
    private boolean isOnline;

    public interface AddFollowupFinish {

        void onAddFollowupFinished(ArrayList<NewFollowUpData> result);
    }

    public AddFollowupDownload(AddFollowupFinish downloadFininshed, Context context) {
        this.downloadFininshed = downloadFininshed;
        volleySingleton = SingleTon.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        userData= Utils.GetUserData(context);

    }

    @Override
    protected ArrayList<NewFollowUpData> doInBackground(Void... params) {
        isOnline = ConnectivityReceiver.isOnline();
        JSONObject response = null;
        ArrayList<NewFollowUpData> followupDataList = new ArrayList<NewFollowUpData>();
       // if (isOnline){
            try {

                response = Requestor.getFollowUps(requestQueue,BASE_URL + TRACK_FOLLOWUP_URL, userData.getAUTHKEY());
                followupDataList = Parser.ParseNewFollowUpData(response);

            } catch (Exception e) {
               // Log.d("ERROR in Downl", e.getMessage().toString());
                // errorHandle.exceptionHandle(e);

            }
    //}
        return followupDataList;
    }


    @Override
    protected void onPostExecute(ArrayList<NewFollowUpData> data) {

        if (data != null && data.size() > 0) {
            downloadFininshed.onAddFollowupFinished(data);
        }


    }
}
