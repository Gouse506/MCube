package vmc.mcube.in.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import vmc.mcube.in.R;
import vmc.mcube.in.activity.HomeActivity;
import vmc.mcube.in.activity.PaymentActivity;
import vmc.mcube.in.model.PaymentData;
import vmc.mcube.in.utils.Utils;

import static com.payu.india.Payu.PayuConstants.PAYU_REQUEST_CODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MakePayment extends Fragment {

    EditText et_Name, et_Mail, et_PhoneNumber, et_PayAmt, et_InvoiceNum;
    Button btn_PayNow;
    String cust_Name, cust_Email, cust_PhoneNumber, cust_PayAmt, cust_InvoiceNum;


    public MakePayment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_payment, container, false);

        et_Name= (EditText) view.findViewById(R.id.user_name);
        et_Mail= (EditText) view.findViewById(R.id.user_email);
        et_PhoneNumber= (EditText) view.findViewById(R.id.user_phone);
        et_PayAmt= (EditText) view.findViewById(R.id.user_payAmt);
        et_InvoiceNum= (EditText) view.findViewById(R.id.user_Invoice);
        btn_PayNow= (Button) view.findViewById(R.id.btn_payNow);
        btn_PayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    Intent intent = new Intent(getActivity(), PaymentActivity.class);
                    intent.putExtra("FIRST_NAME",cust_Name);
                    intent.putExtra("PHONE_NUMBER",cust_PhoneNumber);
                    intent.putExtra("EMAIL_ADDRESS",cust_Email);
                    intent.putExtra("PAY_AMT",cust_PayAmt);
                    intent.putExtra("PRODUCT_INFO",cust_InvoiceNum);
//                    PaymentData paymentData=new PaymentData();
//                    paymentData.setC_name(cust_Name);
//                    paymentData.setC_phone(cust_PhoneNumber);
//                    paymentData.setC_mail(cust_Email);
//                    paymentData.setC_Amt(cust_PayAmt);
//                    paymentData.setC_Invoice(cust_InvoiceNum);
//
//                    Gson gson = new Gson();
//                    String TrackInfo = gson.toJson(paymentData);
//                    intent.putExtra("PAYMENT_DATA", TrackInfo);
                    getActivity().startActivityForResult(intent,PAYU_REQUEST_CODE);

                }
            }
        });

//        TranslateAnimation animation = new TranslateAnimation(-150.0f, 150.0f,
//                0.0f, 0.0f);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
//        animation.setDuration(5000);  // animation duration
//        animation.setRepeatCount(Animation.INFINITE);  // animation repeat count
//        animation.setRepeatMode(2);   // repeat animation (left to right, right to left )
//        //animation.setFillAfter(true);
//        findViewById(R.id.tv_mcube).startAnimation(animation);
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

    public boolean validate() {
        boolean valid = true;
        cust_Name = et_Name.getText().toString();
        cust_Email = et_Mail.getText().toString();
        cust_PhoneNumber = et_PhoneNumber.getText().toString();
        cust_PayAmt = et_PayAmt.getText().toString();
        cust_InvoiceNum = et_InvoiceNum.getText().toString();
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.error);
        drawable.setBounds(new Rect(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()));
        if (cust_Name.isEmpty() || cust_Name.length() < 4) {
            et_Name.setError("At least 4 characters", drawable);
            valid = false;
        } else {
            et_Name.setError(null);
        }
        if (cust_PhoneNumber.isEmpty() || cust_PhoneNumber.length() < 10) {
            et_PhoneNumber.setError("At least 10 digit", drawable);
            valid = false;
        } else {
            et_PhoneNumber.setError(null);
        }
        if (cust_PayAmt.isEmpty()) {
            et_PayAmt.setError("Enter amount to pay", drawable);
            valid = false;
        } else {
            et_PayAmt.setError(null);
        }

        if (cust_Email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(cust_Email).matches()) {
            et_Mail.setError("Enter a valid email address", drawable);
            valid = false;
        } else {
            et_Mail.setError(null);
        }
        if (cust_InvoiceNum.isEmpty()) {
            et_InvoiceNum.setError("Enter invoice number", drawable);
            valid = false;
        } else {
            et_InvoiceNum.setError(null);
        }


        return valid;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


}
