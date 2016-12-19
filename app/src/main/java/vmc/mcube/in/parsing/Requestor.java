package vmc.mcube.in.parsing;

import android.text.TextUtils;
import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import vmc.mcube.in.activity.MyApplication;
import vmc.mcube.in.model.NewFollowUpData;
import vmc.mcube.in.model.TrackDetailsData;
import vmc.mcube.in.utils.Tag;


/**
 * Created by gousebabjan on 29/6/16.
 */
public class Requestor implements Tag {

    private static final int MY_SOCKET_TIMEOUT_MS =5000 ;

    public static JSONObject getReportDetails(RequestQueue requestQueue, String url, final String authKey, final String type, final String callId, final String groupName) throws Exception {
        JSONObject response = null;
        String resp;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.POST, url, requestFuture, requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("authKey", authKey);
                Log.d("TEST", "authkey" + authKey);
                params.put("type", type);
                Log.d("TEST", "type " + type);
                params.put("callid", callId);
                Log.d("TEST", "callid " + callId);
                if (groupName != null) {
                    params.put("groupname", groupName);
                    Log.d("TEST", "groupname " + groupName);
                }
             return params;
            }
        };

        requestQueue.add(request);

        resp = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        Log.d("TEST", resp.toString());
        response = new JSONObject(resp);
        return response;
    }

    public static JSONObject updateReportDetails(RequestQueue requestQueue, String url, final ArrayList<TrackDetailsData> trackDetailsArrayList, final String authkey, final String typetest, final String groupName) throws Exception {
        JSONObject response = null;
        String resp;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.POST, url, requestFuture, requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("authKey", authkey);
                params.put("type", typetest);
                params.put("groupname", groupName);
                for (int i = 0; i < trackDetailsArrayList.size(); i++) {
                    if (trackDetailsArrayList.get(i).getType().equalsIgnoreCase("checkbox")) {

                        // if (trackDetailsArrayList.get(i).getValues() != null ) {

                        ArrayList<String> val = new ArrayList<String>();
                        for (int m = 0; m < trackDetailsArrayList.get(i).getOptionsList().size(); m++) {
                            if (trackDetailsArrayList.get(i).getOptionsList().get(m).isChecked()) {
                                val.add(trackDetailsArrayList.get(i).getOptionsList().get(m).getOptionId());
                            }
                        }
                        if (val.size() > 0) {
                            String joined = TextUtils.join(",", val);
                            params.put(trackDetailsArrayList.get(i).getName(),
                                    joined);
                            Log.d("TEST2 " + trackDetailsArrayList.get(i).getName(), joined);
                        } else {

                            params.put(trackDetailsArrayList.get(i).getName(),
                                    "null");
                            Log.d("TEST2 " + trackDetailsArrayList.get(i).getName(), "null");
                        }


                        //  }
                    } else if (trackDetailsArrayList.get(i).getType().equalsIgnoreCase("dropdown") ||
                            trackDetailsArrayList.get(i).getType().equalsIgnoreCase("radio")) {
                        for (int k = 0; k < trackDetailsArrayList.get(i).getOptionsList().size(); k++) {
                            if (trackDetailsArrayList.get(i).getOptionsList().get(k).getOptionName().equals(trackDetailsArrayList.get(i).getValue())) {
                                params.put(trackDetailsArrayList.get(i).getName(),
                                        trackDetailsArrayList.get(i).getOptionsList().get(k).getOptionId());
                                Log.d("TEST2 " + trackDetailsArrayList.get(i).getName(),
                                        trackDetailsArrayList.get(i).getOptionsList().get(k).getOptionId());
                            }
                        }
                    } else {
                        params.put(trackDetailsArrayList.get(i).getName(), trackDetailsArrayList.get(i).getValue());
                        Log.d("TEST2 " + trackDetailsArrayList.get(i).getName(), trackDetailsArrayList.get(i).getValue());
                    }
                }

                return params;
            }
        };

        requestQueue.add(request);

        resp = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        response = new JSONObject(resp);
        return response;
    }

    public static JSONObject getFollowUpHistory(RequestQueue requestQueue, final String url, final String authKey, final String recordLimit, final String type2, final int offset, final String CallId) throws Exception {
        JSONObject response = null;
        String resp;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.POST, url, requestFuture, requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                Log.d("TEST", "url " + url);
                params.put("authKey", authKey);
                Log.d("TEST", "authKey " + authKey);
                params.put("callid", CallId);
                Log.d("TEST", "callid " + CallId);
                params.put("ofset", String.valueOf(offset));
                Log.d("TEST", "ofset " + String.valueOf(offset));
                params.put("type", type2);
                Log.d("TEST", "type " + type2);
                params.put("limit", recordLimit);
                Log.d("TEST", "limit " + recordLimit);

                return params;
            }
        };

        requestQueue.add(request);

        resp = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        Log.d("TEST", resp.toString());
        response = new JSONObject(resp);
        return response;
    }

    public static JSONObject addFollowUp(RequestQueue requestQueue, String url, final String authKey, final ArrayList<NewFollowUpData> trackDetailsFollowupArrayList, final String CallId, final String groupName, final String type1) throws Exception {
        JSONObject response = null;
        String resp;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.POST, url, requestFuture, requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("authKey", authKey);
                Log.d("TEST2", "authkey " + authKey);
                params.put("type", "followup");
                Log.d("TEST2", "type" + "followup");
                params.put("callid", CallId);
                Log.d("TEST2", "callid" + CallId);
                params.put("ftype", type1.equals(FOLLOWUP) ? groupName : type1);
                Log.d("TEST2", "ftype" + (type1.equals(FOLLOWUP) ? groupName : type1));
                params.put("groupname", groupName);
                Log.d("TEST2", "groupname" + groupName);
                //  params.put("callid", CallId);
                for (int i = 0; i < trackDetailsFollowupArrayList.size(); i++) {
                    if (trackDetailsFollowupArrayList.get(i).getType().equalsIgnoreCase("checkbox")) {

                    } else if (trackDetailsFollowupArrayList.get(i).getType().equalsIgnoreCase("dropdown") ||
                            trackDetailsFollowupArrayList.get(i).getType().equalsIgnoreCase("radio")) {
                        for (int k = 0; k < trackDetailsFollowupArrayList.get(i).getOptionsList().size(); k++) {
                            if (trackDetailsFollowupArrayList.get(i).getOptionsList().get(k).getOptionName().equals(trackDetailsFollowupArrayList.get(i).getValue())) {
                                params.put(trackDetailsFollowupArrayList.get(i).getName(),
                                        trackDetailsFollowupArrayList.get(i).getOptionsList().get(k).getOptionId());
                                Log.d("TEST2 " + trackDetailsFollowupArrayList.get(i).getName(),
                                        trackDetailsFollowupArrayList.get(i).getOptionsList().get(k).getOptionId());
                            }
                        }
                    } else {
                        params.put(trackDetailsFollowupArrayList.get(i).getName(), trackDetailsFollowupArrayList.get(i).getValue());
                        Log.d("TEST2 " + trackDetailsFollowupArrayList.get(i).getName(), trackDetailsFollowupArrayList.get(i).getValue());
                    }
                }

                return params;
            }
        };

        requestQueue.add(request);

        resp = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        response = new JSONObject(resp);
        return response;
    }

    public static JSONObject getFollowUps(RequestQueue requestQueue, String url, final String authKey) throws Exception {
        JSONObject response = null;
        String resp;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.POST, url, requestFuture, requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("authKey", authKey);
                return params;
            }
        };

        requestQueue.add(request);

        resp = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        response = new JSONObject(resp);
        return response;
    }

    public static JSONObject login(RequestQueue requestQueue, String url, final String Email, final String Password) throws Exception {
        JSONObject response = null;
        String resp;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.POST, url, requestFuture, requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", Email);
                params.put("password", Password);
                return params;
            }
        };

        requestQueue.add(request);
        resp = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        Log.d("TEST",resp.toString());
        response = new JSONObject(resp);
        return response;
    }

    public static JSONObject getReport(RequestQueue requestQueue, String url, final String authKey, final String recordLimit, final String type, final String gid, final int offset) throws Exception {
        JSONObject response = null;
        String resp;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.POST, url, requestFuture, requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("authKey", authKey);
                Log.d("TEST21", "Post Parameters.... ");
                Log.d("TEST21", "authKey " + authKey);
                params.put("type", type);
                Log.d("TEST21", "type " + type);
                params.put("ofset", String.valueOf(offset));
                Log.d("TEST21", "offset " + offset);
                params.put("gid", gid);
                Log.d("TEST21", "gid " + gid);
                params.put("limit", recordLimit);
                Log.d("TEST21", "limit " + recordLimit);

                return params;
            }
        };

        requestQueue.add(request);
        resp = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        Log.d("RESPONSE", resp);
        response = new JSONObject(resp);


        return response;
    }


    public static JSONObject requestREG_GCM(RequestQueue requestQueue, String url, final String regid, final String authkey) throws Exception {
        JSONObject response = null;
        String resp;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.POST, url, requestFuture, requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(GCMKEY, regid);
                params.put(AUTHKEY1, authkey);
                params.put(PLATFORM, "ANDROID");
                return params;
            }
        };

        requestQueue.add(request);

        resp = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        response = new JSONObject(resp);
        return response;
    }


    public static JSONObject clickToCall(RequestQueue requestQueue, String url, final String authkey, final String callid, final String type) throws Exception {
        JSONObject response = null;
        String resp;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.POST, url, requestFuture, requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(AUTHKEY1, authkey);
                params.put(CALLID, callid);
                params.put(TYPE, type);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

        resp = requestFuture.get(30000, TimeUnit.MILLISECONDS);
       // Log.d("Clicktocall",resp.toString());
        response = new JSONObject(resp);
        return response;
    }


    public static JSONObject getStatus(RequestQueue requestQueue, String url,final String authkey,final String gcmkey) throws Exception {
        JSONObject response = null;
        String resp;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.POST, url, requestFuture, requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(GCMKEY, gcmkey);
                params.put(AUTHKEY1, authkey);
                return params;
            }
        };

        requestQueue.add(request);
        resp = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        response = new JSONObject(resp);
        return response;
    }



    public static JSONObject updateGCM(RequestQueue requestQueue, String url,  final String gcmkey) throws Exception {
        JSONObject response = null;
        String resp;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.POST, url, requestFuture, requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(GCMKEY, gcmkey);

                return params;
            }
        };

        requestQueue.add(request);

        resp = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        response = new JSONObject(resp);
        return response;
    }



    public static JSONObject recordSeen(RequestQueue requestQueue, String url, final String authkey, final String callid) throws Exception {
        JSONObject response = null;
        String resp;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.POST, url, requestFuture, requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(AUTHKEY, authkey);
                params.put(CALLID, callid);
                return params;
            }
        };

        requestQueue.add(request);

        resp = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        response = new JSONObject(resp);
        return response;
    }

    public static JSONObject submitRateReview(RequestQueue requestQueue, final String authey, String url, final String rateValue, final String title, final String desc, final String callid) throws Exception {
        JSONObject response = null;
        String resp;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.POST, url, requestFuture, requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(AUTHKEY, authey);
                params.put(RATING, rateValue);
                params.put(RATING_TITLE, title);
                params.put(COMMENT, desc);
                params.put(CALLID, callid);
                return params;
            }
        };

        requestQueue.add(request);

        resp = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        response = new JSONObject(resp);
        return response;
    }

    public static JSONObject getReviews(RequestQueue requestQueue, String url, final String authKey, final String callid) throws Exception {
        JSONObject response = null;
        String resp;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.POST, url, requestFuture, requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(AUTHKEY, authKey);
                params.put(CALLID, callid);

                return params;
            }
        };

        requestQueue.add(request);

        resp = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        response = new JSONObject(resp);
        return response;
    }
//Need to Handle these Exceptions to Show snack
//        } catch (InterruptedException e) {
//            //L.m(e + "");
//        } catch (ExecutionException e) { no internet connection
//            // L.m(e + "");
//        } catch (TimeoutException e) {
//            //L.m(e + "");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

}
