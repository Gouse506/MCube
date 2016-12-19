package vmc.mcube.in.fragment;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import org.json.JSONObject;
import vmc.mcube.in.R;
import vmc.mcube.in.parsing.Requestor;
import vmc.mcube.in.utils.ConnectivityReceiver;
import vmc.mcube.in.utils.SingleTon;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.utils.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class RateFragment extends Fragment implements View.OnClickListener, Tag {
    private static final int LOLLIPOP = 21;
    private RatingBar ratingBar;
    private TextView rateComment;
    private EditText title, desc;
    private String tile, description;
    private RelativeLayout main;
    private Button submit;
    Float rateValue;
    private RequestQueue requestQueue;
    private SingleTon volleySingleton;
    private String callid;
    //private boolean isOnline;

    public RateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            callid = getArguments().getString(CALLID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rate, container, false);
        ratingBar = (RatingBar) view.findViewById(R.id.rate);
        rateComment = (TextView) view.findViewById(R.id.rateComment);
        title = (EditText) view.findViewById(R.id.title);
        desc = (EditText) view.findViewById(R.id.desc);
        submit = (Button) view.findViewById(R.id.submit);
        main = (RelativeLayout) view.findViewById(R.id.main);
        volleySingleton = SingleTon.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            LayerDrawable stars = (LayerDrawable)ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(fetchAccentColor(), PorterDuff.Mode.SRC_ATOP);
        }
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                setRateComment(rating);

            }
        });
        submit.setOnClickListener(this);
        return view;

    }
    public static RateFragment newInstance(String callData) {
        RateFragment fragment = new RateFragment();
        Bundle args = new Bundle();
        args.putString(CALLID, callData);
        fragment.setArguments(args);
        return fragment;
    }

    public void onClick(View v) {
        hideKeyboard();
        switch (v.getId()) {
            case R.id.submit:
                if (validateRating())
                    startRating();
                break;

        }

    }


    public boolean validateRating() {
        boolean valid = true;
        rateValue = ratingBar.getRating();
        Log.d("RATE VALUE", " " + rateValue);
        tile = title.getText().toString().trim();
        description = desc.getText().toString().trim();
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.error);
        drawable.setBounds(new Rect(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()));
        if (tile.isEmpty() || (tile.length() < 4)) {
            title.setError("Enter title", drawable);
            valid = false;
        } else {
            title.setError(null);
        }

        if (description.isEmpty() && description.length() < 4) {
            desc.setError("Enter description.", drawable);
            valid = false;
        } else {
            desc.setError(null);
        }
        return valid;

    }

    public void setRateComment(float rating) {

        int val = Math.round(rating);
        switch (val) {
            case 1:
                rateComment.setText("Hated it");
                break;
            case 2:
                rateComment.setText("Disliked it");
                break;
            case 3:
                rateComment.setText("It's ok");
                break;
            case 4:
                rateComment.setText("Liked it");
                break;
            case 5:
                rateComment.setText("Loved it");
                break;
            default:
                rateComment.setText("");
                break;
        }


    }

    private int fetchAccentColor() {
        TypedValue typedValue = new TypedValue();

        TypedArray a = getActivity().obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }


    public void startRating() {
        if (ConnectivityReceiver.isConnected()) {
          new SubmitRating().execute();

        } else {

            Snackbar snack = Snackbar.make(main, "No Internet Connection", Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            startRating();

                        }
                    })
                    .setActionTextColor(ContextCompat.getColor(getActivity(), R.color.accent));
            View view = snack.getView();
            view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary));
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);

            snack.show();
        }
    }


    class SubmitRating extends AsyncTask<Void, Void, String> {
       private String code,msg;
        @Override
        protected String doInBackground(Void... params) {
           // isOnline = ConnectivityReceiver.isOnline();
            JSONObject response = null;
           // if(isOnline) {
                try {
                    response = Requestor.submitRateReview(requestQueue, (Utils.GetUserData(getContext()).getAUTHKEY()), SET_RATE_URL, rateValue + "", tile, description, callid);
                    Log.d("TEST", response.toString());
                    Log.d("TEST", (Utils.GetUserData(getContext()).getAUTHKEY()));
                    if (response != null) {
                        if (response.has(CODE)) {
                            code = response.getString(CODE);
                        }
                    }
                } catch (Exception e) {
                }
          //  }
            return code;
        }


        @Override
        protected void onPostExecute(String code) {
            super.onPostExecute(code);
            if(code!=null && code.equals("400")){
                Toast.makeText(getActivity(),"Submitted successfully.",Toast.LENGTH_SHORT).show();
            }

        }
    }



    public void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

}
