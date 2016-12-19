package vmc.mcube.in.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import vmc.mcube.in.model.Data;
import vmc.mcube.in.model.OptionsData;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.utils.Utils;


/**
 * Created by mukesh on 14/3/16.
 */
public class MDatabase implements Tag {
    private SiteHelper mHelper;
    private SQLiteDatabase mDatabase;
    private DateFormat dateFormat;

    public MDatabase(Context context) {
        mHelper = new SiteHelper(context);
        mDatabase = mHelper.getWritableDatabase();
    }

    public void deleteData(String table) {
        Log.d("TEST21", table + " called");
        mDatabase.delete(table.equals(TRACK) ? SiteHelper.TABLE_TRACK : table.equals(IVRS) ? SiteHelper.TABLE_IVRS : table.equals(X) ? SiteHelper.TABLE_MCUBEX : table.equals(LEAD) ? SiteHelper.TABLE_LEAD : table.equals(FOLLOWUP) ? SiteHelper.TABLE_FOLLOWUP : SiteHelper.TABLE_MTRACKER, null, null);
    }

    public void deleteMenu(String table) {

        mDatabase.delete(table.equals(TRACK) ? SiteHelper.TABLE_TRACK_MENU : table.equals(IVRS) ? SiteHelper.TABLE_IVRS_MENU : table.equals(X) ? SiteHelper.TABLE_MCUBEX_MENU : table.equals(LEAD) ? SiteHelper.TABLE_LEAD_MENU : table.equals(FOLLOWUP) ? SiteHelper.TABLE_FOLLOWUP_MENU : SiteHelper.TABLE_MTRACKER_MENU, null, null);
    }

    public void deleteData() {
        mDatabase.delete(SiteHelper.TABLE_FOLLOWUP, null, null);
        mDatabase.delete(SiteHelper.TABLE_FOLLOWUP_MENU, null, null);
        mDatabase.delete(SiteHelper.TABLE_IVRS, null, null);
        mDatabase.delete(SiteHelper.TABLE_IVRS_MENU, null, null);
        mDatabase.delete(SiteHelper.TABLE_LEAD, null, null);
        mDatabase.delete(SiteHelper.TABLE_LEAD_MENU, null, null);
        mDatabase.delete(SiteHelper.TABLE_MCUBEX, null, null);
        mDatabase.delete(SiteHelper.TABLE_MCUBEX_MENU, null, null);
        mDatabase.delete(SiteHelper.TABLE_TRACK, null, null);
        mDatabase.delete(SiteHelper.TABLE_TRACK_MENU, null, null);
        mDatabase.delete(SiteHelper.TABLE_MTRACKER, null, null);
        mDatabase.delete(SiteHelper.TABLE_MTRACKER_MENU, null, null);

    }

    public void insertData(String table, ArrayList<Data> datalist, boolean clearPrevious) {
        if (clearPrevious) {
            deleteData(table);
        }
        String sql = "INSERT INTO " + (table.equals(TRACK) ? SiteHelper.TABLE_TRACK : table.equals(IVRS) ? SiteHelper.TABLE_IVRS :
                table.equals(X) ? SiteHelper.TABLE_MCUBEX : table.equals(LEAD) ? SiteHelper.TABLE_LEAD : table.equals(FOLLOWUP) ? SiteHelper.TABLE_FOLLOWUP : SiteHelper.TABLE_MTRACKER) + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);";
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        Log.d("TEST21", "TABLE NAME" + table);
        Log.d("TEST21", sql);

        mDatabase.beginTransaction();
        for (int i = 0; i < datalist.size(); i++) {
            Data visitData = datalist.get(i);
            statement.clearBindings();
            statement.bindString(2, visitData.getCallId());
            statement.bindString(3, visitData.getCallFrom());
            statement.bindString(4, visitData.getDataId() == null ? UNKNOWN : visitData.getDataId());
            statement.bindString(5, visitData.getCallerName());
            statement.bindString(6, visitData.getGroupName() == null ? UNKNOWN : visitData.getGroupName());
            statement.bindString(7, visitData.getEmpName() == null ? UNKNOWN : visitData.getEmpName());
            statement.bindString(8, visitData.getCallTimeString());
            statement.bindString(9, visitData.getStatus());
            statement.bindString(10, Utils.isEmpty(visitData.getAudioLink()) ? UNKNOWN : visitData.getAudioLink());
            statement.bindString(11, Utils.isEmpty(visitData.getLocation()) ? "0.0,0.0" : visitData.getLocation());
            statement.bindString(12, visitData.getSeen());
            statement.bindString(13, visitData.getReview() == null ? "0" : visitData.getReview());


            statement.execute();
        }
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public ArrayList<Data> getData(String table) {
        ArrayList<Data> dataList = new ArrayList<>();

        String[] columns = {SiteHelper.COLUMN_UID,
                SiteHelper.COLUMN_CALLID,
                SiteHelper.COLUMN_CALLFROM,
                SiteHelper.COLUMN_dataid,
                SiteHelper.COLUMN_CALLERNAME,
                SiteHelper.COLUMN_GROUPNAME,
                SiteHelper.COLUMN_EMPNAME,
                SiteHelper.COLUMN_CALLTIME,
                SiteHelper.COLUMN_STATUS,
                SiteHelper.COLUMN_AUDIO,
                SiteHelper.COLUMN_LOCATION,
                SiteHelper.COLUMN_SEEN,
                SiteHelper.COLUMN_REVIEW

        };

        Cursor cursor = mDatabase.query((table.equals(TRACK) ? SiteHelper.TABLE_TRACK :
                table.equals(FOLLOWUP) ? SiteHelper.TABLE_FOLLOWUP :
                        table.equals(LEAD) ? SiteHelper.TABLE_LEAD :
                                table.equals(X) ? SiteHelper.TABLE_MCUBEX :
                                        table.equals(IVRS) ? SiteHelper.TABLE_IVRS : SiteHelper.TABLE_MTRACKER), columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                Data visitData = new Data("", "");
                visitData.setCallId(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_CALLID)));
                visitData.setCallFrom(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_CALLFROM)));
                visitData.setDataId(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_dataid)));
                visitData.setCallerName(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_CALLERNAME)));
                visitData.setGroupName(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_GROUPNAME)));
                visitData.setEmpName(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_EMPNAME)));
                visitData.setCallTimeString((cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_CALLTIME))));
                visitData.setAudioLink((cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_AUDIO))));
                visitData.setLocation((cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_LOCATION))));
                visitData.setSeen(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_SEEN)));
                visitData.setReview(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_REVIEW)));
                Date callTime = null;
                SimpleDateFormat sdf = new SimpleDateFormat(DateTimeFormat);
                try {
                    callTime = sdf.parse(visitData.getCallTimeString());

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                visitData.setCallTime(callTime);


                visitData.setStatus(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_STATUS)));


                dataList.add(visitData);
            }
            while (cursor.moveToNext());
        }

        return dataList;
    }


    public void insertMENU(String table, ArrayList<OptionsData> listMovies, boolean clearPrevious) {

        if (clearPrevious) {
            deleteMenu(table);
        }
        String sql = "INSERT INTO " + (table.equals(TRACK) ? SiteHelper.TABLE_TRACK_MENU : table.equals(IVRS) ? SiteHelper.TABLE_IVRS_MENU :
                table.equals(X) ? SiteHelper.TABLE_MCUBEX_MENU : table.equals(LEAD) ? SiteHelper.TABLE_LEAD_MENU : table.equals(FOLLOWUP) ? SiteHelper.TABLE_FOLLOWUP_MENU : SiteHelper.TABLE_MTRACKER_MENU) + " VALUES (?,?,?,?);";
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        for (int i = 0; i < listMovies.size(); i++) {
            OptionsData visitData = listMovies.get(i);
            statement.clearBindings();
            statement.bindString(2, visitData.getOptionId());
            statement.bindString(3, visitData.getOptionName());
            statement.bindString(4, visitData.isChecked() ? "0" : "1");

            statement.execute();
        }
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();


    }

    public ArrayList<OptionsData> getMenuList(String table) {
        ArrayList<OptionsData> listFolloupData = new ArrayList<>();
        try {
            Cursor cursor = mDatabase.query((table.equals(TRACK) ? SiteHelper.TABLE_TRACK_MENU :
                    table.equals(FOLLOWUP) ? SiteHelper.TABLE_FOLLOWUP_MENU :
                            table.equals(LEAD) ? SiteHelper.TABLE_LEAD_MENU :
                                    table.equals(X) ? SiteHelper.TABLE_MCUBEX_MENU :
                                            table.equals(IVRS) ? SiteHelper.TABLE_IVRS_MENU : SiteHelper.TABLE_MTRACKER_MENU), null, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    OptionsData visitData = new OptionsData();
                    visitData.setOptionId(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_OPTIONID)));
                    visitData.setOptionName(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_OPTIONNAME)));
                    //visitData.setDataId(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_dataid)));
                    visitData.setChecked(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_ISCHECKED)).equals("1"));

                    listFolloupData.add(visitData);
                }
                while (cursor.moveToNext());
            }

            //  return listFolloupData;
        } catch (Exception e) {

        }
        return listFolloupData;
    }


    private static class SiteHelper extends SQLiteOpenHelper {
        public static final String TABLE_FOLLOWUP = "followup";
        public static final String TABLE_IVRS = "ivrs";
        public static final String TABLE_MCUBEX = "mcubex";
        public static final String TABLE_LEAD = "lead";
        public static final String TABLE_TRACK = "track";
        public static final String TABLE_MTRACKER = "mtracker";

        public static final String TABLE_FOLLOWUP_MENU = "followup_menu";
        public static final String TABLE_IVRS_MENU = "ivrs_menu";
        public static final String TABLE_MCUBEX_MENU = "mcubex_menu";
        public static final String TABLE_LEAD_MENU = "lead_menu";
        public static final String TABLE_TRACK_MENU = "track_menu";
        public static final String TABLE_MTRACKER_MENU = "mtracker_menu";


        public static final String COLUMN_UID = "_id";
        public static final String COLUMN_CALLID = "callid";
        public static final String COLUMN_CALLFROM = "callfrom";
        public static final String COLUMN_dataid = "dataid";
        public static final String COLUMN_CALLERNAME = "callername";
        public static final String COLUMN_GROUPNAME = "groupname";
        public static final String COLUMN_EMPNAME = "empname";
        public static final String COLUMN_CALLTIME = "calltime";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_AUDIO = "audio";
        public static final String COLUMN_LOCATION = "location";


        public static final String COLUMN_OPTIONID = "optionid";
        public static final String COLUMN_OPTIONNAME = "optionname";
        public static final String COLUMN_ISCHECKED = "ischecked";

        public static final String COLUMN_SEEN = SEEN;
        public static final String COLUMN_REVIEW = REVIEW;
        public final String TABLE_NAMES[] = {TABLE_TRACK, TABLE_MTRACKER, TABLE_FOLLOWUP, TABLE_IVRS, TABLE_LEAD, TABLE_MCUBEX};
        public final String MENU_TABLE[] = {TABLE_TRACK_MENU, TABLE_MTRACKER_MENU, TABLE_FOLLOWUP_MENU, TABLE_IVRS_MENU, TABLE_LEAD_MENU, TABLE_MCUBEX_MENU};

        private static final String CREATE_TABLE_TRACK = "CREATE TABLE " + TABLE_TRACK + " (" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_CALLID + " TEXT," +
                COLUMN_CALLFROM + " TEXT," +
                COLUMN_dataid + " TEXT," +
                COLUMN_CALLERNAME + " TEXT," +
                COLUMN_GROUPNAME + " TEXT," +
                COLUMN_EMPNAME + " TEXT," +
                COLUMN_CALLTIME + " TEXT," +
                COLUMN_STATUS + " TEXT," +
                COLUMN_AUDIO + " TEXT," +
                COLUMN_LOCATION + " TEXT," +
                COLUMN_SEEN + " TEXT," +
                COLUMN_REVIEW + " TEXT" +
                ");";


        private static final String CREATE_TABLE_TRACK_MENU = "CREATE TABLE " + TABLE_TRACK_MENU + " (" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_OPTIONID + " TEXT," +
                COLUMN_OPTIONNAME + " TEXT," +
                COLUMN_ISCHECKED + " TEXT" +
                ");";


        public String getTableName(String tableName) {

            return "CREATE TABLE " + tableName + " (" +
                    COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_CALLID + " TEXT," +
                    COLUMN_CALLFROM + " TEXT," +
                    COLUMN_dataid + " TEXT," +
                    COLUMN_CALLERNAME + " TEXT," +
                    COLUMN_GROUPNAME + " TEXT," +
                    COLUMN_EMPNAME + " TEXT," +
                    COLUMN_CALLTIME + " TEXT," +
                    COLUMN_STATUS + " TEXT," +
                    COLUMN_AUDIO + " TEXT," +
                    COLUMN_LOCATION + " TEXT," +
                    COLUMN_SEEN + " TEXT," +
                    COLUMN_REVIEW + " TEXT" +

                    ");";
        }

        public String getMenuTableName(String tableName) {

            return "CREATE TABLE " + tableName + " (" +
                    COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_OPTIONID + " TEXT," +
                    COLUMN_OPTIONNAME + " TEXT," +
                    COLUMN_ISCHECKED + " TEXT" +
                    ");";
        }


        private static final String DB_NAME = "MCube.db";
        private static final int DB_VERSION = 39;

        private Context mContext;

        public SiteHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("TABLE", "on create called");
            try {
                for (int i = 0; i < TABLE_NAMES.length; i++) {
                    db.execSQL(getTableName(TABLE_NAMES[i]));
                }
                for (int j = 0; j < MENU_TABLE.length; j++) {
                    db.execSQL(getMenuTableName(MENU_TABLE[j]));
                }
                //db.execSQL(CREATE_TABLE_TRACK);
               //db.execSQL(CREATE_TABLE_MCUBEX_MENU);

                Log.d("TABLE", "onCreate Called");
            } catch (SQLiteException exception) {
                Log.d("TABLE", exception.getMessage().toString());
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                // L.m("upgrade table box office executed");
                Log.d("TABLE", "onUpgrade Called");

                for (int i = 0; i < TABLE_NAMES.length; i++) {
                    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAMES[i]);
                }
                for (int j = 0; j < MENU_TABLE.length; j++) {
                    db.execSQL("DROP TABLE IF EXISTS " + MENU_TABLE[j]);
                }


//               db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLLOWUP);
//               db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLLOWUP_MENU);


                onCreate(db);
            } catch (SQLiteException exception) {
                //  L.t(mContext, exception + "");
                Log.d("TABLE", exception.getMessage().toString());
            }
        }

    }
}
