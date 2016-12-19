package vmc.mcube.in.utils;

import android.app.Dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import vmc.mcube.in.R;
import vmc.mcube.in.model.PaymentData;

import static android.R.attr.animation;

/**
 * Created by gousebabjan on 16/12/16.
 */

public class SuccessPaymentDialog extends DialogFragment {

   private TextView amount,desc;
    private ImageView chec_img;
    private  Button btn_ok;

    private PaymentData paymentData;


    public SuccessPaymentDialog() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(
                R.layout.pay_alert_dialog, container);
        Bundle bundle = getArguments();
        if(bundle!=null){
            Gson gson = new Gson();
            String paymentDataObj = getArguments().getString("PAYMENT_DATA");
            paymentData = gson.fromJson(paymentDataObj, PaymentData.class);
        }
        //---get the EditText and Button views
        amount = (TextView) view.findViewById(R.id.tv_payment);
        desc = (TextView) view.findViewById(R.id.tv_desc);
        chec_img = (ImageView) view.findViewById(R.id.img_check);
        btn_ok = (Button) view.findViewById(R.id.btn_ok);
      //  final Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        chec_img.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate) );
        //---event handler for the button
        btn_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dismiss();
            }
        });

        amount.setText("Rs "+paymentData.getC_Amt());
        desc.setText("Your transaction ID for Rs "+paymentData.getC_Amt()+" is "+paymentData.getC_TId()
                +". Please note your Transaction ID for future refrence.");



        return view;
    }



}
