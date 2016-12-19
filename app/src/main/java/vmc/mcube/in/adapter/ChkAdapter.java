package vmc.mcube.in.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import java.util.List;
import vmc.mcube.in.R;
import vmc.mcube.in.model.OptionsData;



public class ChkAdapter extends ArrayAdapter<OptionsData> {
	CheckBox ChkOption;
	int positon;
	public ChkAdapter(Context context, int resource,
					  List<OptionsData> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		positon=position;
		LayoutInflater inflater=LayoutInflater.from(getContext());
		convertView=inflater.inflate(R.layout.chk_option_item,parent,false);
		ChkOption=(CheckBox)convertView.findViewById(R.id.chk_option);
		final OptionsData obj=getItem(position);
        ChkOption.setText(obj.getOptionName());
		ChkOption.setChecked(obj.isChecked());
		ChkOption.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub

                if (isChecked) {
					obj.setChecked(true);

                    Log.d("IsChecked:::",isChecked+"");
                } else {
                    //obj.setChecked(false);
					obj.setChecked(false);
                    Log.d("IsChecked:::",isChecked+"");
                }
            }
        });
		
		return convertView;
	}
	
	
	public Boolean isChecked(){
		Boolean checked=false;
		checked = ChkOption.isChecked();
			return checked;
		}



}
