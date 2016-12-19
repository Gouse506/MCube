package vmc.mcube.in.adapter;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import vmc.mcube.in.R;
import vmc.mcube.in.model.TrackDetailsData;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.utils.Utils;

/**
 * Created by gousebabjan on 1/8/16.
 */
public class ReportDetailsAdapter extends RecyclerView.Adapter<ReportDetailsAdapter.DetailsViewHolder> implements Tag {
    private Context context;
    private int layoutResource;
    private TrackDetailsData obj;
    ArrayList<TrackDetailsData> trackDataArrayList;


    public ReportDetailsAdapter(Context context, int resource, ArrayList<TrackDetailsData> list) {

        this.context = context;
        layoutResource = resource;
        trackDataArrayList = list;

    }

    @Override
    public DetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_details_list_item, parent, false);
        return new DetailsViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final DetailsViewHolder holder, int position) {

        final TrackDetailsData obj = trackDataArrayList.get(position);
        holder.ref = position;

        if (obj.getLabel() != null && obj.getLabel() != "") {
            holder.txtLabel.setVisibility(View.VISIBLE);
            holder.txtLabel.setText(obj.getLabel());
        }

        if (obj.getType().equalsIgnoreCase(LABEL)) {

            holder.txtValue.setVisibility(View.VISIBLE);
            holder.txtValue.setText(obj.getValue());
            holder.lvOptions.setVisibility(View.GONE);
            holder.lvChoice.setVisibility(View.GONE);
            holder.etMultiLine.setVisibility(View.GONE);
            holder.etSingleLine.setVisibility(View.GONE);
            holder.spDropDown.setVisibility(View.GONE);

        }
        if (obj.getType().equalsIgnoreCase(HIDDEN)) {

            holder.txtLabel.setVisibility(View.GONE);
            holder.txtValue.setVisibility(View.GONE);
            holder.lvChoice.setVisibility(View.GONE);
            holder.lvOptions.setVisibility(View.GONE);
            holder.etMultiLine.setVisibility(View.GONE);
            holder.etSingleLine.setVisibility(View.GONE);
            holder.spDropDown.setVisibility(View.GONE);

        } else if (obj.getType().equalsIgnoreCase(TEXT)) {

            holder.etSingleLine.setVisibility(View.VISIBLE);
            holder.txtValue.setVisibility(View.GONE);
            holder.etSingleLine.setText(obj.getValue());
            holder.etMultiLine.setVisibility(View.GONE);
            holder.lvOptions.setVisibility(View.GONE);
            holder.lvChoice.setVisibility(View.GONE);
            holder.spDropDown.setVisibility(View.GONE);
            holder.txtLabel.setVisibility(View.VISIBLE);
            holder.etSingleLine.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    trackDataArrayList.get(holder.ref).setValue(s.toString());

                }

                @Override
                public void afterTextChanged(Editable s) {


                }
            });


        } else if (obj.getType().equalsIgnoreCase(TEXTAREA)) {

            holder.etMultiLine.setVisibility(View.VISIBLE);
            holder.etSingleLine.setVisibility(View.GONE);
            holder.txtValue.setVisibility(View.GONE);
            holder.lvOptions.setVisibility(View.GONE);
            holder.etMultiLine.setText(obj.getValue());
            holder.lvChoice.setVisibility(View.GONE);
            holder.spDropDown.setVisibility(View.GONE);
            holder.txtLabel.setVisibility(View.VISIBLE);

            holder.etMultiLine.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    trackDataArrayList.get(holder.ref).setValue(s.toString());

                }

                @Override
                public void afterTextChanged(Editable s) {


                }
            });


        } else if (obj.getType().equalsIgnoreCase(DROPDOWN) || obj.getType().equalsIgnoreCase(RADIO)) {

            holder.spDropDown.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adp = new ArrayAdapter<String>
                    (context, android.R.layout.simple_spinner_dropdown_item, obj.getOptions());

            holder.spDropDown.setAdapter(adp);
            Log.d("Assigned to", obj.getValue());
            holder.etMultiLine.setVisibility(View.GONE);
            holder.etSingleLine.setVisibility(View.GONE);
            holder.txtValue.setVisibility(View.GONE);
            holder.lvChoice.setVisibility(View.GONE);
            holder.txtLabel.setVisibility(View.VISIBLE);
            holder.lvOptions.setVisibility(View.GONE);


            if (!trackDataArrayList.get(holder.ref).getValue().isEmpty() && trackDataArrayList.get(holder.ref).getValue() != null &&
                    !trackDataArrayList.get(holder.ref).getValue().equals("")) {
                Log.d("DEFAULTEEE  ", trackDataArrayList.get(holder.ref).getValue());

                holder.spDropDown.setSelection(adp.getPosition(trackDataArrayList.get(holder.ref).getValue()));


            }


            holder.spDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                    if(trackDataArrayList.get(holder.ref).getOptionsList().size()>position)
                        trackDataArrayList.get(holder.ref).setValue(trackDataArrayList.get(holder.ref).getOptionsList().get(position).getOptionName());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        }
        else if (obj.getType().equalsIgnoreCase(CHECKBOX)){

            ChkAdapter adp=new ChkAdapter(context,R.layout.chk_option_item,obj.getOptionsList());
            holder.txtLabel.setVisibility(View.VISIBLE);
            holder.txtValue.setVisibility(View.GONE);
            holder.etMultiLine.setVisibility(View.GONE);
            holder.etSingleLine.setVisibility(View.GONE);
            holder.spDropDown.setVisibility(View.GONE);
            holder.lvOptions.setVisibility(View.VISIBLE);
            holder.lvChoice.setVisibility(View.GONE);
            holder.lvOptions.setAdapter(adp);
            Utils.setListViewHeightBasedOnItems(holder.lvOptions);


        }


        else if (obj.getType().equalsIgnoreCase(DATETIME)){

        }

    }

    @Override
    public int getItemCount() {
        return trackDataArrayList.size();
    }

    public class DetailsViewHolder extends RecyclerView.ViewHolder {

        TextView txtLabel;
        TextView txtValue;
        EditText etSingleLine;
        EditText etMultiLine;
        Spinner spDropDown;
        Spinner spDropDownRadio;
        CheckBox chkBox;
        RadioButton radioButton;
        ListView lvOptions;
        ListView lvChoice;
        int ref;

        public DetailsViewHolder(View itemView) {
            super(itemView);
            txtLabel = (TextView) itemView.findViewById(R.id.txtLabel);
            txtValue = (TextView) itemView.findViewById(R.id.txtValue);
            etSingleLine = (EditText) itemView.findViewById(R.id.etSingleLine);
            // etSingleLine.requestFocusFromTouch();
            etMultiLine = (EditText) itemView.findViewById(R.id.etMultiLine);
            etMultiLine.requestFocusFromTouch();
            spDropDown = (Spinner) itemView.findViewById(R.id.spDropdown);
            lvOptions = (ListView) itemView.findViewById(R.id.lvOptions);
            lvChoice = (ListView) itemView.findViewById(R.id.lvChoice);

        }
    }
}
