package vmc.mcube.in.fragment;

/**
 * Created by gousebabjan on 21/11/16.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import vmc.mcube.in.R;
import vmc.mcube.in.activity.ClickToConnect;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.utils.Utils;

import static vmc.mcube.in.R.style.DialpadButton;


public class DialpadFragment extends Fragment implements Tag {

    public static final String TAG = "DialpadFragment";
    private EditText mPhoneNumber;
    private ImageView mclogo;
    private FloatingActionButton mDialFab, fab;
    private boolean mIsAnimationRunning = false;
    private ValueAnimator mColorAnimation;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_dialpad, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mDialFab = (FloatingActionButton) view.findViewById(R.id.fab_dial);
        mPhoneNumber = (EditText) view.findViewById(R.id.phone_number);
        mPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        mPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String phoneNumber = editable.toString();
//                if (isNumberValid(phoneNumber)) {
//                    animateHeader(ContextCompat.getColor(getActivity(), R.color.valid), 0, true);
//                } else {
//                    animateHeader(ContextCompat.getColor(getActivity(), R.color.accent), 0, false);
//                }
            }
        });

        ImageButton backSpace = (ImageButton) view.findViewById(R.id.backspace);
        backSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence oldText = mPhoneNumber.getText();
                mPhoneNumber.setText(oldText.subSequence(0, Math.max(0, oldText.length() - 1)));
            }
        });
        backSpace.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mPhoneNumber.setText("");
                return true;
            }
        });


        mDialFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = Utils.stripNumber(mPhoneNumber.getText().toString());
                if (!TextUtils.isEmpty(phoneNumber) && (9 < phoneNumber.length() && phoneNumber.length()<= 11  ))
                {
                    if((phoneNumber.startsWith("0") && phoneNumber.length()==11) || phoneNumber.length()==10) {
                        ((ClickToConnect) getActivity()).clickToCall(phoneNumber, DIALER);
                    }
                } else {

                    Utils.callConnectAlert(getActivity(),"Invalid Number",false);
                }
                // if (isNumberValid(phoneNumber)) {
                // dialSelectedNumber(phoneNumber);

                // Toast.makeText(getActivity(), "Dial" + phoneNumber, Toast.LENGTH_SHORT).show();
//                } else {
//                    animateHeader(getResources().getColor(R.color.invalid), 3, false);
//                    showPhoneNumberError(phoneNumber);
//                }
            }


        });

        Utils.showFabWithAnimation(mDialFab);

    }


//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.dialpad, menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_recent_contacts:
//                mDialFab.hide();
//                ((ClickToConnect) getActivity()).onHideDialpad();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    public void onDigitClick(Button button) {
        button.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
        mPhoneNumber.append(button.getText());

    }


//    private void dialSelectedNumber(String phoneNumber) {
//        String encodedNumber = Uri.encode(Dialer.getRecallNumber(mOperator, phoneNumber));
//        Intent intent = new Intent(Intent.ACTION_DIAL);
//        intent.setData(Uri.parse("tel:" + encodedNumber));
//        if (Utils.isIntentResolvable(getActivity().getApplicationContext(), intent)) {
//            startActivity(intent);
//        } else {
//            Utils.showDialerNotInstalledDialog(getActivity());
//        }
//    }

    private void showPhoneNumberError(String phoneNumber) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Invalid Number")
                .setMessage(Html.fromHtml(getString(0, phoneNumber)))
                .setPositiveButton("close", null)
                .create()
                .show();
    }

    private void animateHeader(final int colorTo, final int repeatCount, boolean cancelPrevious) {
        // final int colorFrom = ((ColorDrawable) mDialFab.getBackground()).getColor();
        final int colorFrom = ContextCompat.getColor(getActivity(), R.color.accent);
        if (colorFrom == colorTo || (mIsAnimationRunning && !cancelPrevious)) return;

        if (mColorAnimation != null) {
            mColorAnimation.cancel();
        }
        mColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        mColorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mDialFab.setBackgroundColor((Integer) animator.getAnimatedValue());
            }
        });
        mColorAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnimationRunning = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimationRunning = false;
            }
        });
        if (repeatCount > 0) {
            mColorAnimation.setRepeatCount(repeatCount);
            mColorAnimation.setRepeatMode(ValueAnimator.REVERSE);
        }
        mColorAnimation.setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
        mColorAnimation.start();
    }


    public static boolean isNumberValid(String phoneNumber) {
        boolean isValid = false;
        phoneNumber = Utils.stripNumber(phoneNumber);

        // 9XXXXXXXXX
        if (phoneNumber.length() >= 10) {
            phoneNumber = phoneNumber.substring(phoneNumber.length() - 10);
            isValid = phoneNumber.startsWith("91");
        }

        isValid &= phoneNumber.matches("[0-9]+");

        return isValid;
    }
}
