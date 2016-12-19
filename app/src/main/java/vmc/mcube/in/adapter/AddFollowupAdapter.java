package vmc.mcube.in.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import vmc.mcube.in.R;
import vmc.mcube.in.model.NewFollowUpData;
import vmc.mcube.in.utils.CustomDateTimePicker;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.utils.Utils;

/**
 * Created by gousebabjan on 1/8/16.
 */
public class AddFollowupAdapter extends RecyclerView.Adapter<AddFollowupAdapter.FollowupViewHolder> implements Tag {
    ArrayList<NewFollowUpData> NewFollowUpDataArratList;
    private int layoutResource;
    private NewFollowUpData obj;
    private Context context;

    public AddFollowupAdapter(Context context, int resource, ArrayList<NewFollowUpData> list) {
        this.context = context;
        layoutResource = resource;
        NewFollowUpDataArratList = list;
    }

    @Override
    public FollowupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_details_list_item, parent, false);
        return new FollowupViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final FollowupViewHolder holder, int position) {

        final NewFollowUpData obj = NewFollowUpDataArratList.get(position);
        holder.ref = position;

        if (obj.getLabel() != null && obj.getLabel() != "") {
            holder.txtLabel.setVisibility(View.VISIBLE);
            holder.txtLabel.setText(obj.getLabel());

        }

        if (obj.getType().equalsIgnoreCase(LABEL)) {

            holder.txtValue.setVisibility(View.VISIBLE);
            holder.txtValue.setText(obj.getValue());
            holder.lvOptions.setVisibility(View.GONE);
            holder.etMultiLine.setVisibility(View.GONE);
            holder.txtDateTime.setVisibility(View.GONE);
            holder.etSingleLine.setVisibility(View.GONE);
            holder.spDropDown.setVisibility(View.GONE);

        }
        if (obj.getType().equalsIgnoreCase(HIDDEN)) {

            holder.txtLabel.setVisibility(View.GONE);
            holder.txtValue.setVisibility(View.GONE);
            holder.lvOptions.setVisibility(View.GONE);
            holder.etMultiLine.setVisibility(View.GONE);
            holder.etSingleLine.setVisibility(View.GONE);
            holder.spDropDown.setVisibility(View.GONE);
            holder.txtDateTime.setVisibility(View.GONE);

        }
        if (obj.getType().equalsIgnoreCase(DATETIME)) {

            holder.txtLabel.setVisibility(View.VISIBLE);
            holder.txtValue.setVisibility(View.GONE);
            holder.etMultiLine.setVisibility(View.GONE);
            holder.lvOptions.setVisibility(View.GONE);
            holder.etSingleLine.setVisibility(View.GONE);
            holder.spDropDown.setVisibility(View.GONE);
            holder.txtDateTime.setVisibility(View.VISIBLE);
            if (obj.getValue().equalsIgnoreCase("")) {
                holder.txtDateTime.setText("Pick Date/Time");
            } else {
                holder.txtDateTime.setText(obj.getValue());
            }
            holder.txtDateTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CustomDateTimePicker custom = new CustomDateTimePicker((Activity) context, new CustomDateTimePicker.ICustomDateTimeListener() {

                        @Override
                        public void onSet(Dialog dialog, Calendar calendarSelected,
                                          Date dateSelected, int year, String monthFullName,
                                          String monthShortName, int monthNumber, int date,
                                          String weekDayFullName, String weekDayShortName,
                                          int hour24, int hour12, int min, int sec,
                                          String AM_PM) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateTimeFormat1);
                            String reminderDateTime = simpleDateFormat.format(calendarSelected.getTime());
                            Log.d("create date & time", "**/" + reminderDateTime);
                            holder.txtDateTime.setText(reminderDateTime);
                            NewFollowUpDataArratList.get(holder.ref).setValue(reminderDateTime);
                            Log.d("date & time", "**/" +year+monthNumber+weekDayShortName+hour24+min+sec);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                    custom.showDialog();
                }
            });

        } else if (obj.getType().equalsIgnoreCase(TEXT)) {

            holder.etSingleLine.setVisibility(View.VISIBLE);
            holder.txtValue.setVisibility(View.GONE);
            holder.etSingleLine.setText(obj.getValue());
            holder.etMultiLine.setVisibility(View.GONE);
            holder.spDropDown.setVisibility(View.GONE);
            holder.lvOptions.setVisibility(View.GONE);
            holder.txtLabel.setVisibility(View.VISIBLE);
            holder.txtDateTime.setVisibility(View.GONE);
            holder.etSingleLine.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    NewFollowUpDataArratList.get(holder.ref).setValue(s.toString());

                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            });


        } else if (obj.getType().equalsIgnoreCase(TEXTAREA)) {

            holder.etMultiLine.setVisibility(View.VISIBLE);
            holder.etSingleLine.setVisibility(View.GONE);
            holder.lvOptions.setVisibility(View.GONE);
            holder.txtValue.setVisibility(View.GONE);
            holder.txtDateTime.setVisibility(View.GONE);
            holder.etMultiLine.setText(obj.getValue());
            holder.spDropDown.setVisibility(View.GONE);
            holder.txtLabel.setVisibility(View.VISIBLE);

            holder.etMultiLine.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    NewFollowUpDataArratList.get(holder.ref).setValue(s.toString());
                }
                @Override
                public void afterTextChanged(Editable s) {

                }
            });


        } else if (obj.getType().equalsIgnoreCase(DROPDOWN) || obj.getType().equalsIgnoreCase(RADIO)) {

            holder.spDropDown.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adp = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, obj.getOptions());
            holder.spDropDown.setAdapter(adp);
            Log.d("Assigned to", obj.getValue());
            holder.etMultiLine.setVisibility(View.GONE);
            holder.etSingleLine.setVisibility(View.GONE);
            holder.txtValue.setVisibility(View.GONE);
            holder.txtDateTime.setVisibility(View.GONE);
            holder.lvOptions.setVisibility(View.GONE);
            holder.txtLabel.setVisibility(View.VISIBLE);
            if (!NewFollowUpDataArratList.get(holder.ref).getValue().isEmpty() &&
                    NewFollowUpDataArratList.get(holder.ref).getValue() != null &&
                    !NewFollowUpDataArratList.get(holder.ref).getValue().equals("")) {
                holder.spDropDown.setSelection(adp.getPosition(NewFollowUpDataArratList.get(holder.ref).getValue()));
            }


            holder.spDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    NewFollowUpDataArratList.get(holder.ref).setValue(NewFollowUpDataArratList.get(holder.ref).getOptionsList().get(position).getOptionName());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {


                }
            });


        } else if (obj.getType() == CHECKBOX) {

            ChkAdapter adp = new ChkAdapter(context, R.layout.chk_option_item, obj.getOptionsList());

            holder.txtLabel.setVisibility(View.VISIBLE);
            holder.txtValue.setVisibility(View.GONE);
            holder.etMultiLine.setVisibility(View.GONE);
            holder.etSingleLine.setVisibility(View.GONE);
            holder.spDropDown.setVisibility(View.GONE);
            holder.lvOptions.setVisibility(View.VISIBLE);
            holder.lvOptions.setAdapter(adp);
            holder.txtDateTime.setVisibility(View.GONE);
            Utils.setListViewHeightBasedOnItems(holder.lvOptions);

        }

    }

    @Override
    public int getItemCount() {
        return  NewFollowUpDataArratList.size();
    }

    public class FollowupViewHolder extends RecyclerView.ViewHolder {

        TextView txtLabel;
        TextView txtDateTime;
        TextView txtValue;
        EditText etSingleLine;
        EditText etMultiLine;
        Spinner spDropDown;
        ListView lvOptions;
        int ref;

        public FollowupViewHolder(View convertView) {
            super(convertView);

            txtLabel = (TextView) convertView.findViewById(R.id.txtLabel);
            txtDateTime = (TextView) convertView.findViewById(R.id.txtDateTime);
            txtValue = (TextView) convertView.findViewById(R.id.txtValue);
            etSingleLine = (EditText) convertView.findViewById(R.id.etSingleLine);
            etSingleLine.requestFocusFromTouch();
            etMultiLine = (EditText) convertView.findViewById(R.id.etMultiLine);
            etMultiLine.requestFocusFromTouch();
            lvOptions = (ListView) convertView.findViewById(R.id.lvOptions);
            spDropDown = (Spinner) convertView.findViewById(R.id.spDropdown);
        }
    }
}
