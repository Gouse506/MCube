package vmc.mcube.in.model;



import java.util.ArrayList;

import vmc.mcube.in.model.OptionsData;

/**
 * Created by Surendra Singh on 07-05-2015.
 */
public class NewFollowUpData {

    private String Label;
    private String Name;
    private ArrayList<String> Options;
    private ArrayList<OptionsData> OptionsList;
    private String Type;
    private String Value;



 /*   public NewFollowUpData(String label, String name, ArrayList<String> options, String type, String value) {
        Label = label;
        Name = name;
        Options = options;
        Type = type;
        Value = value;
    }

    public NewFollowUpData(String label, String name, ArrayList<String> options, ArrayList<OptionsData> optionsList, String type, String value) {
        Label = label;
        Name = name;
        Options = options;
        OptionsList = optionsList;
        Type = type;
        Value = value;
    }
*/
    public ArrayList<OptionsData> getOptionsList() {
        return OptionsList;
    }

    public void setOptionsList(ArrayList<OptionsData> optionsList) {
        OptionsList = optionsList;
    }
    //    public TrackDetailsData( String name,String label, String type, String options, String value) {
//        Label = label;
//        Name = name;
//        Options = options;
//        Type = type;
//        Value = value;
//    }


    public void setLabel(String label) {
        Label = label;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setOptions(ArrayList<String> options) {
        Options = options;
    }

    public void setType(String type) {
        Type = type;
    }

    public void setValue(String value) {
        Value = value;
    }

    public String getLabel() {
        return Label;
    }

    public String getName() {
        return Name;
    }


    public ArrayList<String> getOptions() {
        return Options;
    }

    public String getType() {
        return Type;
    }

    public String getValue() {
        return Value;
    }


}