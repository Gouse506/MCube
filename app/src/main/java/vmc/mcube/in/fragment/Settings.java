package vmc.mcube.in.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import vmc.mcube.in.R;
import vmc.mcube.in.activity.HomeActivity;
import vmc.mcube.in.model.UserData;
import vmc.mcube.in.utils.Utils;


public class Settings extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView empNameTextView;
    private TextView businessNameTextView;
    private TextView mobileNumberTextView;
    private TextView emailTextView;
    private EditText recordLimitEditText;
    private Button signOut;
    private UserData userData;
    private Button saveButton;
    private String recordLimit;
    Context context;

    // TODO: Rename and change types and number of parameters
    public static Settings newInstance(String param1, String param2) {
        Settings fragment = new Settings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public Settings() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            context = getActivity();


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        empNameTextView = (TextView) view.findViewById(R.id.empNameTextView);
        businessNameTextView = (TextView) view.findViewById(R.id.businessNameTextView);
        mobileNumberTextView = (TextView) view.findViewById(R.id.mobileNumberTextView);
        emailTextView = (TextView) view.findViewById(R.id.emailTextView);
        recordLimitEditText = (EditText) view.findViewById(R.id.recordLimitEditText);
        saveButton = (Button) view.findViewById(R.id.saveButton);
        signOut = (Button) view.findViewById(R.id.logout);
        recordLimit = HomeActivity.recordLimit;
        ((HomeActivity)getActivity()).userData = Utils.GetUserData(getActivity());
        if (((HomeActivity)getActivity()).userData != null) {
            empNameTextView.setText(((HomeActivity)getActivity()).userData.EMP_NAME);
            mobileNumberTextView.setText(((HomeActivity)getActivity()).userData.EMP_CONTACT);
            businessNameTextView.setText(((HomeActivity)getActivity()).userData.BUSINESS_NAME);
            emailTextView.setText(((HomeActivity)getActivity()).userData.EMP_EMAIL);
            recordLimitEditText.setText("" + recordLimit);
        }
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String limit = recordLimitEditText.getText().toString() == null ? recordLimit : recordLimitEditText.getText().toString();
                int recordLmt;
                try {
                    recordLmt = Integer.parseInt(recordLimitEditText.getText().toString());
                } catch (Exception e) {
                    recordLmt = 10;
                    recordLimitEditText.setText("10");
                }
                if (recordLmt <= 0) {
                    Toast.makeText(context, "Record Limit should be greater than 0.", Toast.LENGTH_SHORT).show();
                } else {
                    Utils.saveToPrefs(getActivity(), "recordLimit", "" + recordLmt);
                    Toast.makeText(context, "Settings saved successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.isLogout1(getActivity());
               // getActivity().finish();
            }
        });


        return view;
    }




    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuItem item = menu.add("Logout");
        item.setIcon(R.drawable.logout);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Utils.isLogout1(getActivity());
                return true;

            }
        });
        super.onPrepareOptionsMenu(menu);
    }


}
