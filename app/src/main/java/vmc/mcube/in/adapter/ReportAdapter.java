package vmc.mcube.in.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import vmc.mcube.in.R;
import vmc.mcube.in.activity.ClickToConnect;
import vmc.mcube.in.activity.DetailActivity;
import vmc.mcube.in.activity.HomeActivity;
import vmc.mcube.in.activity.LocationActivity;
import vmc.mcube.in.model.Data;
import vmc.mcube.in.utils.ConnectivityReceiver;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.utils.Utils;

import static android.content.DialogInterface.BUTTON_POSITIVE;
import static vmc.mcube.in.utils.Constants.position;

/**
 * Created by mukesh on 7/7/15.
 */
public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.DataViewHolder> implements Tag {

    private Context context;
    private LayoutInflater inflator;
    private XClickedListner xClickedListner;
    private ArrayList<Data> DataArrayList;
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm aa");
    private int previousPosition = 0;
    private RelativeLayout mroot;
    public Fragment fragment;
    public String TYPE;
    public static boolean isHistory;
    private static AlertDialog alertDialog;

    public ReportAdapter(Context context, ArrayList<Data> DataArrayList, RelativeLayout mroot, Fragment fragment, String type, boolean isHistory) {

        this.context = context;
        this.DataArrayList = DataArrayList;
        this.mroot = mroot;
        this.fragment = fragment;
        TYPE = type;
        this.isHistory = isHistory;
    }

    public void setClickedListner(XClickedListner xClickedListner) {
        this.xClickedListner = xClickedListner;
    }


    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.followup_item, parent, false);
        return new DataViewHolder(itemView, xClickedListner, DataArrayList);
    }

    @Override
    public void onBindViewHolder(final DataViewHolder holder, int position) {
        try {
            final Data ci = DataArrayList.get(position);

            holder.callerNameTextView.setText(Utils.isEmpty(ci.getCallerName()) ? UNKNOWN : ci.getCallerName());
            holder.callFromTextView.setText(Utils.isEmpty(ci.getCallFrom()) ? UNKNOWN : ci.getCallFrom());
//           if(TYPE.equals("followup")|| isHistory){
//               holder.overflow.setVisibility(View.GONE);
//           }
            holder.overflow.setOnClickListener(new OnOverflowSelectedListener(context, holder.getAdapterPosition(), DataArrayList, mroot, fragment, TYPE, isHistory));
            holder.overflowlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.overflow.performClick();
                }
            });
            holder.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //((HomeActivity) context).playAudio(ci.getAudioLink());
                    ((HomeActivity) context).playAudio(ci);
                }
            });
            try {
                holder.dateTextView.setText(sdfDate.format(ci.getCallTime()));
                holder.timeTextView.setText(sdfTime.format(ci.getCallTime()));
            } catch (Exception e) {

            }

            if (ci.getAudioLink() == null || ci.getStatus().matches("MISSED") || ci.getAudioLink().equals(UNKNOWN)) {
                holder.play.setVisibility(View.INVISIBLE);
            } else {
                holder.play.setVisibility(View.VISIBLE);
            }

            if (ci.getStatus().matches("INCOMING|OUTGOING|MISSED") && ci.getGroupName().equals("")) {
                holder.groupNameLabel.setText("Employee");
                holder.fstatusLabel.setText("Type");
                holder.groupNameTextView.setText(Utils.isEmpty(ci.getEmpName()) ? UNKNOWN : ci.getEmpName());
                if (Utils.isEmpty(ci.getEmpName())) {
                    holder.statusTextView.setText(Utils.isEmpty(ci.getStatus()) ? UNKNOWN : ci.getStatus());
                } else {
                    holder.statusTextView.setText(Utils.isEmpty(ci.getStatus()) ? UNKNOWN : ci.getStatus());
                }
                if (ci.getStatus() != null && ci.getStatus().matches("INCOMING|OUTGOING")) {

                    if (ci.getSeen() != null && ci.getSeen().equals("1")) {
                        //if api 17  drwle with red image
                        if (Build.VERSION.SDK_INT < 18) {
                            holder.play.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play_seen));
                        } else
                            holder.play.getDrawable().setColorFilter(ContextCompat.getColor(context, R.color.red), PorterDuff.Mode.SRC_ATOP);

                    } else {
                        holder.play.getBackground().setColorFilter(fetchAccentColor(), PorterDuff.Mode.SRC_ATOP);
                        holder.review.setVisibility(View.GONE);
                    }


                    if (!ci.getReview().equals("0")) {
                        holder.review.setVisibility(View.VISIBLE);
                    } else {
                        holder.review.setVisibility(View.GONE);
                    }
                    holder.review.setText("Reviews : " + ci.getReview());
//                    holder.play.setVisibility(View.VISIBLE);
                } else {
                    holder.play.setVisibility(View.INVISIBLE);
                }
//                if (ci.getAudioLink()==null ||ci.getStatus().equals("MISSED") || ci.getAudioLink().equals(UNKNOWN)) {
//                    holder.play.setVisibility(View.GONE);
//                } else {
//                    holder.play.setVisibility(View.VISIBLE);
//                }
            } else {
                holder.groupNameLabel.setText("Group");
                holder.groupNameTextView.setText(Utils.isEmpty(ci.getGroupName()) ? UNKNOWN : ci.getGroupName());
                //TEMPORARILY
                if (((ci.getAudioLink() == null || Utils.isEmpty(ci.getAudioLink()) || ci.getAudioLink().equals(UNKNOWN))) || TYPE.equals(FOLLOWUP) || isHistory) {
                    holder.play.setVisibility(View.INVISIBLE);
                } else {
                    holder.play.setVisibility(View.VISIBLE);
                }
                holder.statusTextView.setText(Utils.isEmpty(ci.getStatus()) ? UNKNOWN : ci.getStatus());

            }


        } catch (Exception e) {
            Log.d("TEST", e.getMessage().toString());
        }

        previousPosition = position;
    }

    @Override
    public int getItemCount() {
        return DataArrayList.size();
    }


    public static class DataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView overflow, play;
        protected TextView callFromTextView, callerNameTextView, review,
                groupNameTextView, dateTextView, timeTextView, statusTextView, groupNameLabel, fstatusLabel;
        private XClickedListner xClickedListner;
        private ArrayList<Data> DataArrayList;
        private LinearLayout overflowlayout;

        public DataViewHolder(View v, XClickedListner xClickedListner, ArrayList<Data> DataArrayList) {
            super(v);
            this.xClickedListner = xClickedListner;
            this.DataArrayList = DataArrayList;
            callFromTextView = (TextView) v.findViewById(R.id.fCallFromTextView);
            callerNameTextView = (TextView) v.findViewById(R.id.fCallerNameTextView);
            groupNameTextView = (TextView) v.findViewById(R.id.fGroupNameTextView);
            dateTextView = (TextView) v.findViewById(R.id.fDateTextView);
            timeTextView = (TextView) v.findViewById(R.id.fTimeTextView);
            statusTextView = (TextView) v.findViewById(R.id.fStatusTextView);
            groupNameLabel = (TextView) v.findViewById(R.id.fGroupNameLabel);
            fstatusLabel = (TextView) v.findViewById(R.id.fStatusLabel);
            review = (TextView) v.findViewById(R.id.review);
            overflow = (ImageView) v.findViewById(R.id.ic_more);
            overflowlayout = (LinearLayout) v.findViewById(R.id.overflow);
            play = (ImageView) v.findViewById(R.id.ivplay);

            v.setClickable(true);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (xClickedListner != null) {
                xClickedListner.OnItemClick(DataArrayList.get(getAdapterPosition()), getAdapterPosition());
            }

        }
    }

    public static class OnOverflowSelectedListener implements View.OnClickListener {
        private Context mContext;
        private int position;
        private ArrayList<Data> reportdata;
        private RelativeLayout mroot;
        private Fragment fragment;
        private boolean isHistory;
        private String type;

        public OnOverflowSelectedListener(Context context, int pos, ArrayList<Data> reportdata, RelativeLayout mroot, Fragment fragment, String type, boolean isHistory) {
            mContext = context;
            this.position = pos;
            this.reportdata = reportdata;
            this.mroot = mroot;
            this.fragment = fragment;
            this.isHistory = isHistory;
            this.type = type;
        }

        @Override
        public void onClick(final View v) {
            PopupMenu popupMenu = new PopupMenu(mContext, v) {
                @Override
                public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.call:
                            showCallAlert(mContext, reportdata.get(position).getCallFrom(), reportdata.get(position).getCallId(), type);
//                            if (!Utils.isEmpty(reportdata.get(position).getCallFrom())) {
//                                Utils.makeAcall(reportdata.get(position).getCallFrom(), (HomeActivity) mContext);
//                            } else {
//                                Toast.makeText(mContext, "Invalid Number", Toast.LENGTH_SHORT).show();
//                            }
//                            if(isHistory){
//                                ((DetailActivity) mContext).showCallAlert(mContext,reportdata.get(position).getCallFrom(),reportdata.get(position).getCallId(),type);
//                            }
//                            else if ((type!=null && type.equals(FOLLOWUP))) {
//                            ((HomeActivity) mContext).showCallAlert(mContext, reportdata.get(position).getCallFrom(), reportdata.get(position).getCallId(), type);
//                            }
//                            else {
//                                showCallAlert(mContext,reportdata.get(position).getCallFrom(),reportdata.get(position).getCallId(),type);
//                            }
                            return true;
                        case R.id.sms:
                            if (!Utils.isEmpty(reportdata.get(position).getCallFrom())) {
                                Utils.sendSms(reportdata.get(position).getCallFrom(), (HomeActivity) mContext);
                            } else {
                                Toast.makeText(mContext, "Invalid Number", Toast.LENGTH_SHORT).show();
                            }
                            return true;

                        case R.id.location:
                            if (ConnectivityReceiver.isConnected()) {
                                Gson gson = new Gson();
                                String TrackInfo = gson.toJson(reportdata.get(position));
                                Intent intent = new Intent(mContext, LocationActivity.class);
                                intent.putExtra("DATA", TrackInfo);
                                mContext.startActivity(intent);
                            } else {
                                Toast.makeText(mContext, "No Internet Connection.", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        case R.id.share:
                            ((HomeActivity) mContext).onShareFile(reportdata.get(position).getAudioLink());
                            return true;
                        case R.id.rate:
                            ((HomeActivity) mContext).onRatingsClick(reportdata.get(position));
                            return true;
                        default:
                            return super.onMenuItemSelected(menu, item);
                    }
                }
            };

            // Force icons to show
            Object menuHelper = null;
            Class[] argTypes;
            try {
                Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
                fMenuHelper.setAccessible(true);
                menuHelper = fMenuHelper.get(popupMenu);
                argTypes = new Class[]{boolean.class};
                menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
            } catch (Exception e) {
                Log.w("t", "error forcing menu icons to show", e);
                popupMenu.show();
                // Try to force some horizontal offset
                try {
                    Field fListPopup = menuHelper.getClass().getDeclaredField("mPopup");
                    fListPopup.setAccessible(true);
                    Object listPopup = fListPopup.get(menuHelper);
                    argTypes = new Class[]{int.class};
                    Class listPopupClass = listPopup.getClass();
                } catch (Exception e1) {

                    Log.w("T", "Unable to force offset", e);
                }
                return;
            }

            if (!isHistory && (reportdata.get(position).getAudioLink() != null && reportdata.get(position).getAudioLink().length() > 7) &&
                    (reportdata.get(position).getLocation() != null && reportdata.get(position).getLocation().length() > 7)) {
                popupMenu.inflate(R.menu.popupmenu_cmsl);
            } else if (!isHistory && (reportdata.get(position).getAudioLink() != null && reportdata.get(position).getAudioLink().length() > 7)) {
                popupMenu.inflate(R.menu.popupmenu_cms);
            } else if (!isHistory && (reportdata.get(position).getLocation() != null && reportdata.get(position).getLocation().length() > 7)) {
                popupMenu.inflate(R.menu.popupmenu_cml);
            } else {
                popupMenu.inflate(R.menu.popupmenu);
            }
            if (popupMenu.getMenu().findItem(R.id.rate) != null)
                popupMenu.getMenu().findItem(R.id.rate).setVisible(false);

//            if ((TYPE!=null && TYPE.equals(FOLLOWUP))|| isHistory) {
//                if (popupMenu.getMenu().findItem(R.id.call) != null)
//                    popupMenu.getMenu().findItem(R.id.call).setVisible(false);
//                if (popupMenu.getMenu().findItem(R.id.sms) != null)
//                    popupMenu.getMenu().findItem(R.id.sms).setVisible(false);
//            }
            popupMenu.show();


        }
    }

    public interface XClickedListner {
        void OnItemClick(Data data, int position);
    }

    public static void showCallAlert(final Context mContext, final String phoneNumber, final String callid, final String type) {
        alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("MCube");
        alertDialog.setIcon(R.mipmap.logo);
        alertDialog.setMessage("Select option to proceed");
        alertDialog.setCancelable(false);

        alertDialog.setButton(BUTTON_POSITIVE, "Click To Call",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // Toast.makeText(mContext, "Click To Call", Toast.LENGTH_SHORT).show();
                        if (!TextUtils.isEmpty(phoneNumber) && phoneNumber.length() >= 10)
                            ((HomeActivity) mContext).clickToCall(phoneNumber, callid, type);


                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Call",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        if (!Utils.isEmpty(phoneNumber)) {
                            Utils.makeAcall((phoneNumber), (HomeActivity) mContext);
                        } else {
                            Toast.makeText(mContext, "Invalid Number", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        alertDialog.dismiss();
                    }
                });


        alertDialog.show();
        if ((type != null && type.equals(FOLLOWUP)) || isHistory) {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        }
    }

    private int fetchAccentColor() {
        TypedValue typedValue = new TypedValue();

        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }
}
