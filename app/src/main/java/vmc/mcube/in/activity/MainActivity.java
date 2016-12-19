package vmc.mcube.in.activity;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import vmc.mcube.in.R;
import vmc.mcube.in.gcm.GCMClientManager;
import vmc.mcube.in.parsing.Parser;
import vmc.mcube.in.parsing.Requestor;
import vmc.mcube.in.utils.ConnectivityReceiver;
import vmc.mcube.in.utils.Constants;
import vmc.mcube.in.model.LoginData;
import vmc.mcube.in.utils.SingleTon;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.utils.Utils;


public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, View.OnClickListener, Tag {
    public static final String DEAFULT = "n/a";
    private TextInputLayout mInputLayoutEmail, mInputLayoutPassword, mInputLayoutServer;
    private CoordinatorLayout mroot;
    private ProgressDialog mProgressDialog;
    private LoginData loginData;
    private CheckBox checkBox;
    private EditText inputEmail, inputPassword;
    private View.OnClickListener mSnackBarListner;
    private Button btnLogin;
    private RequestQueue requestQueue;
    private SingleTon volleySingleton;
    private String PROJECT_NUMBER = "305656196217";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utils.tabletSize(MainActivity.this) < 6.0)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("MCube");
        toolbar.setTitleTextColor(0xFFFFFFFF);
        volleySingleton = SingleTon.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        mInputLayoutEmail = (TextInputLayout) findViewById(R.id.inputLayoutEmail);
        mInputLayoutPassword = (TextInputLayout) findViewById(R.id.inputLayoutPassword);
        checkBox = (CheckBox) findViewById(R.id.checkBox1);
        mroot = (CoordinatorLayout) findViewById(R.id.rootLayout);
        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputPassword = (EditText) findViewById(R.id.inputPassword);
        btnLogin = (Button) findViewById(R.id.submit);
        btnLogin.setOnClickListener(this);
        load();
        cancelNotification(this, NOTIFICATION_ID);
        mSnackBarListner = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
        inputEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Drawable drawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.error);
                drawable.setBounds(new Rect(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()));
                String email = inputEmail.getText().toString().trim();
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (email.isEmpty() || (email.length() < 8 && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
                        inputEmail.setError("Enter a valid email address.", drawable);
                    }
                    return false;
                }
                return false;
            }
        });
        inputPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Drawable drawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.error);
                drawable.setBounds(new Rect(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()));

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (validations()) {
                        hideKeyboard();
                    }
                    return true;
                }
                return false;
            }
        });
        inputPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (inputPassword.getRight() - inputPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (inputPassword.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                            // show password
                            inputPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                            inputPassword.clearFocus();
                            inputPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pass, 0, R.drawable.ic_show, 0);
                        } else {
                            // hide password
                            inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            inputPassword.clearFocus();
                            inputPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pass, 0, R.drawable.ic_hide, 0);

                        }
                        return false;
                    }
                }
                return false;
            }
        });
        logoAnimation();

        GCMClientManager pushClientManager = new GCMClientManager(this, PROJECT_NUMBER);
        pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
            @Override
            public void onSuccess(String registrationId, boolean isNewRegistration) {
                Log.d("Registration id", registrationId);
                MyApplication.getInstance().gcmKey=registrationId;

            }

            @Override
            public void onFailure(String ex) {
                super.onFailure(ex);
            }
        });


        if (ConnectivityReceiver.isConnected()) {
            if(!Utils.getFromPrefsBoolean(MainActivity.this,SETGCM,false))
            new UpdateGCM().execute();
        }

    }

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

    public void logoAnimation() {
        TranslateAnimation translation;
        translation = new TranslateAnimation(0f, 0F, 100f, 0f);
        translation.setStartOffset(500);
        translation.setDuration(2000);
        translation.setFillAfter(true);
        translation.setInterpolator(new BounceInterpolator());
        findViewById(R.id.logo).startAnimation(translation);


    }

    public boolean validations() {
        boolean valid = true;
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        Drawable drawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.error);
        drawable.setBounds(new Rect(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()));
        if (email.isEmpty() || (email.length() < 8 && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            inputEmail.setError("Enter a valid email address", drawable);
            valid = false;
        } else {
            inputEmail.setError(null);
        }
        if (password.isEmpty()) {
            inputPassword.setError("Password must not be empty.", drawable);
            valid = false;
        } else {
            inputPassword.setError(null);
        }
        return valid;
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
            View view = this.getCurrentFocus();
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } else {
            // writeToLog("Software Keyboard was not shown");
        }
    }

    public void save() {
        if (((CheckBox) findViewById(R.id.checkBox1)).isChecked()) {
            SharedPreferences pref = getSharedPreferences("Mydata", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("name", inputEmail.getText().toString());
            editor.putString("password", inputPassword.getText().toString());
            editor.commit();

        } else {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            prefs.edit().clear().commit();
            String email = inputEmail.getText().toString();
            SharedPreferences pref = getSharedPreferences("Mydata", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("email", email);
            editor.putString("password", "");
            editor.commit();

        }
    }

    public void load() {
        SharedPreferences pref = getSharedPreferences("Mydata", Context.MODE_PRIVATE);
        String name = pref.getString("name", DEAFULT);
        String password = pref.getString("password", DEAFULT);
        String serverName = pref.getString("server", DEAFULT);
        if (!name.equals(DEAFULT) || !password.equals(DEAFULT) || !serverName.equals(DEAFULT)) {
            inputEmail.setText(name);
            inputPassword.setText(password);

        }


    }


    @Override
    public void onClick(View v) {
        inputPassword.clearFocus();
        inputPassword.clearFocus();
        hideKeyboard();
        if (validations())
            Login();
    }

    protected void Login() {
        String email = inputEmail.getText().toString().replace(" ", "");
        String password = inputPassword.getText().toString();
        if (ConnectivityReceiver.isConnected()) {
            new StartLogin(password, email).execute();
        } else {
            Snackbar snack = Snackbar.make(mroot, "No Internet Connection", Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Login();

                        }
                    })
                    .setActionTextColor(ContextCompat.getColor(MainActivity.this, R.color.accent));
            View view = snack.getView();
            view.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.primary));
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();
        }
    }

    protected void showProgress(String msg) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            dismissProgress();
        mProgressDialog = new ProgressDialog(this, R.style.StyledDialog);
        mProgressDialog.setMessage("Login Please Wait..!!");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.progress));
        mProgressDialog.show();
    }

    protected void dismissProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    class StartLogin extends AsyncTask<Void, Void, LoginData> {
        String message = "No Response from server";
        String code = "N";
        JSONObject response = null;
        String password, email;

        public StartLogin(String password, String email) {
            this.password = password;
            this.email = email;
        }

        @Override
        protected void onPreExecute() {
           // showProgress("Login Please Wait..");
            btnLogin.setText("Authenticating..");
            btnLogin.setEnabled(false);
            super.onPreExecute();
        }


        @Override
        protected LoginData doInBackground(Void... params) {
            // TODO Auto-generated method stub
            loginData = new LoginData();

            try {
                response = Requestor.login(requestQueue, BASE_URL + AUTHENTICATION_URL, email, password);
                //response = Requestor.login(requestQueue, TEST_URL + AUTHENTICATION_URL, email, password);
                Log.d("RESPONSE", response.toString());
            } catch (Exception e) {
            }
            if (response != null) {
                System.out.println(email + "  " + password + " ");
                System.out.println(response);

                try {
                    code = response.getString(CODE);
                    message = response.getString(MESSAGE);
                    if (code.equalsIgnoreCase(SUCCESS)) {

                        loginData = Parser.ParseLoginResponse(response);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return loginData;
        }

        @Override
        protected void onPostExecute(LoginData data) {
            //dismissProgress();
            btnLogin.setText("Success");
            btnLogin.setEnabled(true);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            prefs.edit().clear().commit();
            if (!code.equals("N") && code.equalsIgnoreCase(SUCCESS)) {
                save();
                Utils.saveToPrefs(MainActivity.this, AUTHKEY, data.getAuthKey());
                Utils.saveToPrefs(MainActivity.this, BUSINESS_NAME, data.getBusinessName());
                Utils.saveToPrefs(MainActivity.this, EMP_CONTACT, data.getEmpContact());
                Utils.saveToPrefs(MainActivity.this, EMP_EMAIL, data.getEmpEmail());
                Utils.saveToPrefs(MainActivity.this, EMP_NAME, data.getEmpName());
                Utils.saveToPrefs(MainActivity.this, MESSAGE, data.getMessage());

                Utils.saveToPrefs(MainActivity.this, MTRACKER, data.getMtracker());
                Utils.saveToPrefs(MainActivity.this, TRACK, data.getTrack());
                Utils.saveToPrefs(MainActivity.this, IVRS, data.getIvrs());
                Utils.saveToPrefs(MainActivity.this, LEAD, data.getLead());
                Utils.saveToPrefs(MainActivity.this, X, data.getX());

                Constants.Anim = true;
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
               // overridePendingTransition(R.anim.flipfadein, R.anim.flipfadeout);
            } else {
                btnLogin.setText("Login");
                Snackbar snack = Snackbar.make(mroot, message, Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Login();

                            }
                        })
                        .setActionTextColor(ContextCompat.getColor(MainActivity.this, R.color.accent));
                View view = snack.getView();
                view.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.primary));
                TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.WHITE);
                snack.show();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);


    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }


    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (!isConnected) {
            message = "Sorry! Not connected to internet";
            color = Color.WHITE;
        } else {
            message = "Connected to internet";
            color = Color.WHITE;
        }
        Snackbar snackbar = Snackbar
                .make(mroot, message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.primary));
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        snackbar.show();
    }


    class UpdateGCM extends AsyncTask<Void, Void, String> {
        private String msg,code;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(Void... params) {
            JSONObject response = null;
            try {
                response = Requestor.updateGCM(requestQueue, BASE_URL+SET_GCM_URL,MyApplication.getInstance().gcmKey);
                Log.d("TEST", response.toString());
                if (response != null) {
                    if (response.has(CODE)) {
                        code = response.getString(CODE);
                    }
                    if (response.has(MESSAGE)) {
                        msg = response.getString(MESSAGE);
                    }
                }
            } catch (Exception e) {
            }

            return code;
        }


        @Override
        protected void onPostExecute(String code) {
            super.onPostExecute(code);

            if (code != null && code.equals("400")) {
                Utils.saveToPrefs(MainActivity.this,SETGCM,true);
//                 Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
//                Utils.callConnectAlert(MainActivity.this,msg,false);
            }

        }

    }
}

