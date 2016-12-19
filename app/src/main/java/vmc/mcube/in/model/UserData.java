package vmc.mcube.in.model;

/**
 * Created by mukesh on 7/7/15.
 */
public class UserData {

    public String AUTHKEY;
    public String BUSINESS_NAME;
    public String EMP_CONTACT;
    public String EMP_EMAIL;
    public String EMP_NAME;

    public String getSERVER() {
        return SERVER;
    }

    public void setSERVER(String SERVER) {
        this.SERVER = SERVER;
    }

    public String SERVER;

    public String getAUTHKEY() {
        return AUTHKEY;
    }

    public void setAUTHKEY(String AUTHKEY) {
        this.AUTHKEY = AUTHKEY;
    }

    public String getBUSINESS_NAME() {
        return BUSINESS_NAME;
    }

    public void setBUSINESS_NAME(String BUSINESS_NAME) {
        this.BUSINESS_NAME = BUSINESS_NAME;
    }

    public String getEMP_CONTACT() {
        return EMP_CONTACT;
    }

    public void setEMP_CONTACT(String EMP_CONTACT) {
        this.EMP_CONTACT = EMP_CONTACT;
    }

    public String getEMP_EMAIL() {
        return EMP_EMAIL;
    }

    public void setEMP_EMAIL(String EMP_EMAIL) {
        this.EMP_EMAIL = EMP_EMAIL;
    }

    public String getEMP_NAME() {
        return EMP_NAME;
    }

    public void setEMP_NAME(String EMP_NAME) {
        this.EMP_NAME = EMP_NAME;
    }
}
