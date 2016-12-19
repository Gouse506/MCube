package vmc.mcube.in.parsing;

import android.text.TextUtils;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


import vmc.mcube.in.model.Data;
import vmc.mcube.in.model.NewFollowUpData;

import vmc.mcube.in.model.OptionsData;

import vmc.mcube.in.model.RateData;
import vmc.mcube.in.model.TrackDetailsData;
import vmc.mcube.in.model.LoginData;
import vmc.mcube.in.utils.Tag;

/**
 * Created by mukesh on 2/7/15.
 */
public class Parser implements Tag {

    public synchronized static LoginData ParseLoginResponse(JSONObject response) throws JSONException {
        LoginData loginData = new LoginData();
         if(response!=null) {
             if (response.has(AUTHKEY))
                 loginData.setAuthKey(response.getString(AUTHKEY));
             if (response.has(BUSINESS_NAME))
                 loginData.setBusinessName(response.getString(BUSINESS_NAME));
             if (response.has(EMP_CONTACT))
                 loginData.setEmpContact(response.getString(EMP_CONTACT));
             if (response.has(EMP_EMAIL))
                 loginData.setEmpEmail(response.getString(EMP_EMAIL));
             if (response.has(EMP_NAME))
                 loginData.setEmpName(response.getString(EMP_NAME));


             if (response.has(IVRS))
                 loginData.setIvrs(!response.getString(IVRS).equals("0"));
             if (response.has(TRACK))
                 loginData.setTrack(!response.getString(TRACK).equals("0"));
             if (response.has(LEAD))
                 loginData.setLead(!response.getString(LEAD).equals("0"));
             if (response.has(MTRACKER))
                 loginData.setMtracker(!response.getString(MTRACKER).equals("0"));
             if (response.has(PBX))
                 loginData.setX(!response.getString(PBX).equals("0"));

             if (response.has(MESSAGE))
                 loginData.setMessage(response.getString(MESSAGE));

             return loginData;
         }return null;
    }


    public synchronized static ArrayList<TrackDetailsData> ParseTrackDetailsData(JSONObject response) throws JSONException {

        ArrayList<TrackDetailsData> ivrsDetailsArrayList = new ArrayList<TrackDetailsData>();
        ArrayList<OptionsData> OptionsArrayList;
        ArrayList<String> OptionStringArrayList;
        if (response != null) {
            Log.d("TEST22", response.toString());
            SimpleDateFormat sdf = new SimpleDateFormat(SIMPLEDATEFORMAT);
            if (response.has(FIELDS)) {
               // ivrsDetailsArrayList.clear();
                JSONArray fieldsArray = response.getJSONArray(FIELDS);
                // noOfRecords = fieldsArray.length();
                Log.d("DATA", response.toString());
                for (int i = 0; i < fieldsArray.length(); i++) {
                    JSONObject field = (JSONObject) fieldsArray.get(i);
                    String Name = field.getString(NAME);
                    String Label = field.getString(LABEL);
                    String Type = field.getString(TYPE);
                    String Value = field.getString(Tag.VALUE);

                    TrackDetailsData mTrackData = new TrackDetailsData();

                    mTrackData.setName(Name);
                    mTrackData.setLabel(Label);
                    mTrackData.setType(Type);
                    mTrackData.setValue(Value);

                    if (Type != null && Type.equalsIgnoreCase(DROPDOWN) ||
                            Type != null && Type.equalsIgnoreCase(CHECKBOX) ||
                            Type != null && Type.equalsIgnoreCase(RADIO)) {

                        JSONObject Options = field.getJSONObject(OPTIONS);

                        OptionsArrayList = new ArrayList<OptionsData>();
                        OptionStringArrayList = new ArrayList<String>();
                        Iterator keys = Options.keys();

                        while (keys.hasNext()) {
                            String OptionId = (String) keys.next();
                            if (OptionId.equals(Value)) {
                                mTrackData.setValue(Options.getString(OptionId));
                            }
                            String OptionName = Options.getString(OptionId);

                            OptionsData mOptionsData = new OptionsData(OptionId, OptionName);
                            if (Type.equalsIgnoreCase(CHECKBOX) && !Value.equalsIgnoreCase("")) {
                                String[] value = Value.replaceAll("[\\[\\](){}]", "").replace("\"", "").split(",");
                                for (int k = 0; k < value.length; k++) {
                                    if (OptionId.equals(value[k])) {
                                        mOptionsData.setChecked(true);
                                    }
                                }
                            }
                            OptionStringArrayList.add(OptionName);
                            OptionsArrayList.add(mOptionsData);

                        }

                    } else {
                        OptionsArrayList = new ArrayList<OptionsData>();
                        OptionStringArrayList = new ArrayList<String>();

                    }

                    mTrackData.setOptionsList(OptionsArrayList);
                    mTrackData.setOptions(OptionStringArrayList);

//                            if (Name.equalsIgnoreCase(CALLFROM)) {
//                                ContactNo = Value;
//                            } else if (Name.equalsIgnoreCase(CALLEREMAIL)) {
//                                EmailId = Value;
//                            }
                    ivrsDetailsArrayList.add(mTrackData);
                }


            }
            return ivrsDetailsArrayList;

        }    return null;
    }


    public synchronized static ArrayList<NewFollowUpData> ParseNewFollowUpData(JSONObject response) throws JSONException {

        ArrayList<OptionsData> OptionsArrayList = null;
        ArrayList<String> OptionStringArrayList =null;
        ArrayList<NewFollowUpData> trackDetailsFollowupArrayList = new ArrayList<NewFollowUpData>();
        if(response!=null) {
            if (response.has("fields")) {
                JSONArray fieldsArray = response.getJSONArray("fields");

                for (int i = 0; i < fieldsArray.length(); i++) {
                    JSONObject field = (JSONObject) fieldsArray.get(i);
                    String Name = field.getString("name");
                    String Label = field.getString("label");
                    String Type = field.getString("type");
                    String Value = field.getString("value");

                    NewFollowUpData mTrackData = new NewFollowUpData();

                    mTrackData.setName(Name);
                    mTrackData.setLabel(Label);
                    mTrackData.setType(Type);
                    mTrackData.setValue(Value);

                    if (Type != null && Type.equalsIgnoreCase("dropdown") ||
                            Type != null && Type.equalsIgnoreCase("checkbox") ||
                            Type != null && Type.equalsIgnoreCase("radio")) {

                        JSONObject Options = field.getJSONObject("options");

                        OptionsArrayList = new ArrayList<OptionsData>();
                        OptionStringArrayList = new ArrayList<String>();
                        Iterator keys = Options.keys();
                        while (keys.hasNext()) {
                            String OptionId = (String) keys.next();
                            if (OptionId.equals(Value)) {
                                mTrackData.setValue(Options.getString(OptionId));
                            }
                            String OptionName = Options.getString(OptionId);

                            OptionsData mOptionsData = new OptionsData(OptionId, OptionName);
                            if (Type.equalsIgnoreCase(CHECKBOX) && !Value.equalsIgnoreCase("")) {
                                String[] value = Value.replaceAll("[\\[\\](){}]", "").replace("\"", "").split(",");
                                for (int k = 0; k < value.length; k++) {
                                    if (OptionId.equals(value[k])) {
                                        mOptionsData.setChecked(true);
                                    }
                                }
                            }
                            OptionStringArrayList.add(OptionName);
                            OptionsArrayList.add(mOptionsData);

                                   /* while (keys.hasNext()) {
                                        String OptionId = (String) keys.next();
                                        if (OptionId.equals(Value)) {
                                            mTrackData.setValue(Options.getString(OptionId));
                                        }
                                        String OptionName = Options.getString(OptionId);

                                        OptionsData mOptionsData = new OptionsData(OptionId, OptionName);
                                        OptionStringArrayList.add(OptionName);
                                        OptionsArrayList.add(mOptionsData);

                                    }*/
                        }

                    } else {
                        OptionsArrayList = new ArrayList<OptionsData>();
                        OptionStringArrayList = new ArrayList<String>();

                    }

                    mTrackData.setOptionsList(OptionsArrayList);
                    mTrackData.setOptions(OptionStringArrayList);

                    trackDetailsFollowupArrayList.add(mTrackData);
                }


            } else {
                // showNoDataPresent();
            }


            return trackDetailsFollowupArrayList;
        }return null;
    }


    public synchronized static ArrayList<Data> ParseXData(JSONObject response) throws JSONException {

        ArrayList<Data> DataArrayList = new ArrayList<Data>();

        SimpleDateFormat sdf = new SimpleDateFormat(DateTimeFormat);
        if (response != null) {
            if (response.has(RECORDS)) {
                JSONArray recordsArray = response.getJSONArray(RECORDS);

                for (int i = 0; i < recordsArray.length(); i++) {
                    Data data = new Data("", "");
                    JSONObject record = (JSONObject) recordsArray.get(i);
                    if (record.has(CALLID)) {
                        data.setCallId(record.getString(CALLID));
                    }
                    if (record.has(DATAID)) {
                        data.setDataId(record.getString(DATAID));
                    }
                    if (record.has(CALLFROM)) {
                        data.setCallFrom(record.getString(CALLFROM));
                    }
                    if (record.has(CALLTO)) {
                        data.setCallFrom(record.getString(CALLTO));
                    }
                    if (record.has(GROUPNAME)) {
                        data.setGroupName(record.getString(GROUPNAME));
                    }
                    if (record.has(NAME)) {
                        data.setCallerName(record.getString(NAME));
                    }
                    if (record.has(CALLERNAME)) {
                        data.setCallerName(record.getString(CALLERNAME));
                    }
                    if (record.has(EMP_NAME)) {
                        data.setEmpName(record.getString(EMP_NAME));
                    }
                    if (record.has(AUDIO)) {
                        if (!record.getString(AUDIO).equals(" ")) {
                           // data.setAudioLink(STREAM_MCUBE + record.getString(AUDIO));
                            data.setAudioLink(record.getString(AUDIO));
                        }
                    }
                    if (record.has(STATUS)) {
                        data.setStatus(record.getString(STATUS));
                        if(record.has(EMP_NAME)){
                        if (data.getStatus().equals("0")) {
                            data.setStatus("MISSED");

                        } else if (data.getStatus().equals("1")) {
                            data.setStatus("INCOMING");
                        } else {
                            data.setStatus("OUTGOING");
                        }
                        }
                    }

                    if (record.has(LOCATION)) {
                       data.setLocation((record.getString(LOCATION)));

                    }
                    if (record.has(LISTEN)) {
                        data.setSeen(record.getString(LISTEN));
                    }else{
                        data.setSeen("0");
                    }
                    if (record.has(RATING_COUNT)) {
                        data.setReview(record.getString(RATING_COUNT));
                    }else {
                        data.setReview("0");
                    }

                    if (record.has(STARTTIMESTRING)) {
                        data.setCallTimeString((record.getString(STARTTIMESTRING)));
                    }
                    if (record.has(CALLTIMESTRING)) {
                        data.setCallTimeString((record.getString(CALLTIMESTRING)));
                    }
                    Date callTime = null;
                    try {
                        callTime = sdf.parse(data.getCallTimeString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    data.setCallTime(callTime);
                    DataArrayList.add(data);

                }
            }
            return DataArrayList;
        }


        return null;
    }


    public synchronized static ArrayList<OptionsData> ParseMenuOptions(JSONObject response) throws JSONException {
        ArrayList<OptionsData> optionslist = new ArrayList<OptionsData>();

        if (response != null) {
            JSONArray groupsArray = response.getJSONArray(GROUPS);
            for (int j = 0; j < groupsArray.length(); j++) {
                JSONObject option = (JSONObject) groupsArray.get(j);
                OptionsData optionsData = new OptionsData(option.getString(KEY), option.getString(VAL));
                optionslist.add(optionsData);

            }
            return optionslist;
        }

       return  null;
    }



    public static ArrayList<RateData> ParseReview(JSONObject response) throws JSONException {
        ArrayList<RateData> CallList = new ArrayList<RateData>();
        JSONArray recordsArray = null;
        SimpleDateFormat sdf = new SimpleDateFormat(DateTimeFormat);
        if (response != null) {
            if (response.has(RATING_LIST)) {
                Log.d("RESPONSE", response.toString());
                recordsArray = response.getJSONArray(RATING_LIST);
                for (int i = 0; i < recordsArray.length(); i++) {
                    RateData rateData = new RateData();
                    JSONObject record = (JSONObject) recordsArray.get(i);
                    if (record.has(COMMENT)) {
                        rateData.setDesc(record.getString(COMMENT));
                    }
                    if (record.has(EMPLOYEE)) {
                        rateData.setName(record.getString(EMPLOYEE));
                    }
                    if (record.has(RATING)) {
                        rateData.setRate(record.getString(RATING));
                    }
                    if (record.has(RATING_TITLE)) {
                        rateData.setTitle(record.getString(RATING_TITLE));
                    }

                    if (record.has(DATE)) {
                        Date startTime = null;
                        try {
                            startTime = sdf.parse(record.getString(DATE));

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        rateData.setDate(startTime);

                    }
                    CallList.add(rateData);

                }
            }
            return CallList;
        }
        return null;
    }
}
