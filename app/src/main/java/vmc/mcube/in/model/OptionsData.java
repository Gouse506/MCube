package vmc.mcube.in.model;

/**
 * Created by Surendra Singh on 07-05-2015.
 */
public class OptionsData {

    private String OptionId;
    private String OptionName;
    private boolean IsChecked;

    public OptionsData() {
       ;
    }

    public OptionsData(String optionId, String optionName, boolean isChecked) {
        OptionId = optionId;
        OptionName = optionName;
        IsChecked = isChecked;
    }

    public OptionsData(String optionId, String optionName) {
        OptionId = optionId;
        OptionName = optionName;
    }

    public String getOptionId() {
        return OptionId;
    }

    public void setOptionId(String optionId) {
        OptionId = optionId;
    }

    public String getOptionName() {
        return OptionName;
    }

    public void setOptionName(String optionName) {
        OptionName = optionName;
    }

    public boolean isChecked() {
        return IsChecked;
    }

    public void setChecked(boolean isChecked) {
        IsChecked = isChecked;
    }
}

