package vmc.mcube.in.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;

import vmc.mcube.in.R;
import vmc.mcube.in.activity.HomeActivity;
import vmc.mcube.in.activity.MainActivity;
import vmc.mcube.in.activity.MyApplication;
import vmc.mcube.in.model.UserData;

import static vmc.mcube.in.gcm.PushNotificationService.NOTIFICATION_ID;

/**
 * Created by mukesh on 6/7/15.
 */
public class Utils implements Tag {
    private static AlertDialog alertDialog;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public static ArrayList<String> sortArray(ArrayList<String> al) {
        Comparator<String> nameComparator = new Comparator<String>() {
            @Override
            public int compare(String value1, String value2) {
                if (Character.isDigit(value1.charAt(0)) && !Character.isDigit(value2.charAt(0)))
                    return 1;
                if (Character.isDigit(value2.charAt(0)) && !Character.isDigit(value1.charAt(0)))
                    return -1;
                return value1.compareTo(value2);
            }
        };

        Collections.sort(al, nameComparator);

        System.out.println(al);
        return al;
    }

    //Check if phone is online
    public static boolean onlineStatus(Context activityContext) {
        if (activityContext != null) {
            ConnectivityManager cm = (ConnectivityManager)
                    activityContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] activeNetInfo = cm.getAllNetworkInfo();
            boolean isConnected = false;
            if (activeNetInfo != null) {
                for (NetworkInfo i : activeNetInfo) {
                    if (i.getState() == NetworkInfo.State.CONNECTED && i.isAvailable()) {
                        isConnected = true;
                        break;

                    }
                }
            }
            return isConnected;
        } else {
            return false;
        }
    }

    public static double tabletSize(Context context) {

        double size = 0;
        try {

            // Compute screen size

            DisplayMetrics dm = context.getResources().getDisplayMetrics();

            float screenWidth = dm.widthPixels / dm.xdpi;

            float screenHeight = dm.heightPixels / dm.ydpi;

            size = Math.sqrt(Math.pow(screenWidth, 2) +

                    Math.pow(screenHeight, 2));

        } catch (Throwable t) {

        }

        return size;

    }


    public static boolean onlineStatus1(Context activityContext) {
        ConnectivityManager cm = (ConnectivityManager)
                activityContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] activeNetInfo = cm.getAllNetworkInfo();
        boolean isConnected = false;
        for (NetworkInfo i : activeNetInfo) {
            if (i.getState() == NetworkInfo.State.CONNECTED) {
                isConnected = true;
                break;
            }
        }


        return isConnected;
    }

    public static int getAPILevel() {
        return Build.VERSION.SDK_INT;
    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

    public static int memoryRemaining(Context activityContext) {
        int memClass = ((ActivityManager) activityContext.getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryClass();
        return memClass;
    }


    public static int getBatteryLevel(Context context) {
        int batteryPercentage = 0;
        try {
            IntentFilter ifilter = new IntentFilter(
                    Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);

            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL,
                    -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE,
                    -1);
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS,
                    -1);

            float batteryPct = level / (float) scale;
            batteryPercentage = (int) (batteryPct * 100);
            if (batteryPercentage < 0) {
                batteryPercentage = 0;
            }
            // McubeUtils.infoLog("Battery level remaining : " + batteryPercentage + "%");
            String strStatus = "";
            switch (status) {
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    strStatus = "Unknown Charged";
                    break;
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    strStatus = "Charged Plugged";
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    strStatus = "Charged Unplugged";
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    strStatus = "Not Charging";
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    strStatus = "Charged Completed";
                    break;
            }
            // McubeUtils.infoLog("Battery status  " + strStatus);
        } catch (Exception e) {
            // McubeUtils.errorLog(e.toString());
        }

        return batteryPercentage;
    }

    public static void saveToPrefs(Context context, String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void saveToPrefs(Context context, String key, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }



    public static void logoutByGCM(Context context,String msg) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().clear().commit();
        File sampleDir = Environment.getExternalStorageDirectory();
        List<File> files = getListFiles(new File(sampleDir.getAbsolutePath() + File.separator + "data"+File.separator + "mcubeShare"));
        for (int i = 0; i < files.size(); i++) {
            files.get(i).delete();
        }
        MyApplication.getWritableDatabase().deleteData();

//        Intent intent = new Intent(context, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                Intent.FLAG_ACTIVITY_CLEAR_TASK |
//                Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
       // ((HomeActivity) context).finish();

        sendNotification(context,msg);

    }
    private static void sendNotification(Context context,String msg) {
        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder TSB = TaskStackBuilder.create(context);
        TSB.addParentStack(MainActivity.class);
        TSB.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                TSB.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.largeicon);
        NotificationCompat.Builder nb = new NotificationCompat.Builder(context);
        nb.setContentText(msg);
        nb.setContentTitle("MCube");
        nb.setTicker(msg);
        nb.setSmallIcon(R.drawable.smallicon);
        nb.setLargeIcon(bm);
        nb.setColor(ContextCompat.getColor(context, R.color.accent));
        // nb.addAction(R.drawable.ic_share_24dp, "Share", resultPendingIntent);
        //  nb.setContent(new RemoteViews(new Tex))
        nb.setContentText(msg);
        NotificationCompat.BigTextStyle s = new NotificationCompat.BigTextStyle();
        s.setBigContentTitle("MCube");
        s.bigText(msg);
        nb.setStyle(s);
        nb.setContentIntent(resultPendingIntent);
        nb.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, nb.build());


    }


    public static void isLogout1(final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("MCube");
        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want to logout?");
        alertDialog.setIcon(R.mipmap.logo);
        // On pressing Settings button
        alertDialog.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                MyApplication.getWritableDatabase().deleteData();
                saveToPrefs(context, Tag.AUTHKEY, "n");
                saveToPrefs(context, Tag.BUSINESS_NAME, "n");
                saveToPrefs(context, Tag.EMP_CONTACT, "n");
                saveToPrefs(context, Tag.EMP_EMAIL, "n");
                saveToPrefs(context, Tag.EMP_NAME, "n");
                saveToPrefs(context, Tag.MESSAGE, "n");
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                ((HomeActivity) context).finish();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();


    }

    public static boolean contains(JSONObject jsonObject, String key) {
        return jsonObject != null && jsonObject.has(key) && !jsonObject.isNull(key);
    }

    public static String getFromPrefs(Context context, String key, String defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPrefs.getString(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static Boolean isLogin(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        String authKey = sharedPrefs.getString(Tag.AUTHKEY, "n");
        String empName = sharedPrefs.getString(Tag.EMP_NAME, "n");
        String empEmail = sharedPrefs.getString(Tag.EMP_EMAIL, "n");
        String empContact = sharedPrefs.getString(Tag.EMP_CONTACT, "n");
        String bussinessName = sharedPrefs.getString(Tag.BUSINESS_NAME, "n");

        return !(authKey.equals("n") && empName.equals("n") && empEmail.equals("n") && empContact.equals("n") && bussinessName.equals("n"));

    }

    public static boolean isEmpty(String msg) {
        return msg == null || msg.trim().equals("")
                || msg.isEmpty();
    }


    public static UserData GetUserData(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        UserData userData = new UserData();
        userData.setAUTHKEY(sharedPrefs.getString(Tag.AUTHKEY, "n"));
        userData.setEMP_NAME(sharedPrefs.getString(Tag.EMP_NAME, "n"));
        userData.setEMP_EMAIL(sharedPrefs.getString(Tag.EMP_EMAIL, "n"));
        userData.setEMP_CONTACT(sharedPrefs.getString(Tag.EMP_CONTACT, "n"));
        userData.setBUSINESS_NAME(sharedPrefs.getString(Tag.BUSINESS_NAME, "n"));
        userData.setSERVER(sharedPrefs.getString(Tag.SERVER, "n"));
        return userData;
    }

    public static Boolean getFromPrefsBoolean(Context context, String key, Boolean defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPrefs.getBoolean(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static Calendar getCurrentDate() {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
    }

    public static String ordinal(int i) {
        String[] suffixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + suffixes[i % 10];

        }
    }

    public static int convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int dp = (int) Math.ceil(px / (metrics.densityDpi / 160f));
        return dp;
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static float convertPixelsToSp(float px, Context context) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }

    public static void makeAcall(String number, final Activity mActivity) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mActivity.startActivity(callIntent);

    }
    public static boolean checkAndRequestPermissions(Context context) {
        int readConractsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS);
        int readSmsConractsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS);
        int readPhoneStatePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
        int writeExternalPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (readConractsPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (readSmsConractsPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }

        if (readPhoneStatePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (writeExternalPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) context, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
    private static void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener, Context mActivity) {
        new AlertDialog.Builder(mActivity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public static void sendSms(String number, Activity mActivity) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("sms_body", " ");
        sendIntent.putExtra("address", number);
        sendIntent.setType("vnd.android-dir/mms-sms");
        mActivity.startActivity(sendIntent);

    }

    public static void sendAnEmail(String recipient, Activity mActivity) {
        try {
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            if (recipient != null)
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{recipient});
//	        if (subject != null) {   emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);}
//	        if (message != null)    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
            mActivity.startActivity(emailIntent);

        } catch (ActivityNotFoundException e) {
            // cannot send email for some reason
        }

    }
    public static List<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();

        File[] files = parentDir.listFiles();
        if (files != null && files.length > 0)
            for (File file : files) {
                if (file.isDirectory()) {
                    inFiles.addAll(getListFiles(file));
                } else {
                    if (file.getName().endsWith(".3gp") || file.getName().endsWith(".amr") || file.getName().endsWith(".wav")) {
                        inFiles.add(file);
                    }
                }
            }
        return inFiles;
    }




    public static void showFabWithAnimation(final FloatingActionButton fab) {
        fab.setVisibility(View.INVISIBLE);
        fab.setScaleX(0.0F);
        fab.setScaleY(0.0F);
        fab.setAlpha(0.0F);
        fab.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override public boolean onPreDraw() {
                fab.getViewTreeObserver().removeOnPreDrawListener(this);
                fab.postDelayed(new Runnable() {
                    @Override public void run() {
                        fab.show();
                    }
                }, 400);
                return true;
            }
        });
    }


    public static String stripNumber(String phoneNumber) {
        return phoneNumber.replace("-", "")
                .replace("+", "")
                .replace("(", "")
                .replace(")", "")
                .replace(" ", "");
    }

    public static void  callConnectAlert(final Context context, String msg, final boolean isLogout){
        alertDialog = new AlertDialog.Builder(context).create();
        // alertDialog.setTitle("MCube");
        alertDialog.setTitle(Html.fromHtml("<font  size='30' color='#FF7F27'>MCube</font>"));
        alertDialog.setIcon(R.mipmap.logo);
        alertDialog.setMessage(Html.fromHtml("<font color='#FFFFFF'></font>"+msg));

        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if(isLogout) {
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                    Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                        alertDialog.dismiss();
                    }
                });
       // alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        alertDialog.show();
        //alertDialog.getWindow().setBackgroundDrawableResource(R.color.secondary_text);
    }
}
