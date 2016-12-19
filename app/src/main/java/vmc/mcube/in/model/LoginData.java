package vmc.mcube.in.model;

/**
 * Created by mukesh on 7/7/15.
 */
public class LoginData {
    public String authKey;
    public String businessName;
    public String code;
    public String msg;
    public String empContact;
    public String empEmail;
    public String empName;
    private Boolean isMtracker;
    private Boolean isIvrs;

    public Boolean getMtracker() {
        return isMtracker;
    }

    public void setMtracker(Boolean mtracker) {
        isMtracker = mtracker;
    }

    public Boolean getIvrs() {
        return isIvrs;
    }

    public void setIvrs(Boolean ivrs) {
        isIvrs = ivrs;
    }

    public Boolean getLead() {
        return isLead;
    }

    public void setLead(Boolean lead) {
        isLead = lead;
    }

    public Boolean getTrack() {
        return isTrack;
    }

    public void setTrack(Boolean track) {
        isTrack = track;
    }


    public Boolean getX() {
        return isX;
    }

    public void setX(Boolean x) {
        isX = x;
    }

    private Boolean isLead;
    private Boolean isTrack;
    private Boolean isX;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String message;

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getEmpContact() {
        return empContact;
    }

    public void setEmpContact(String empContact) {
        this.empContact = empContact;
    }

    public String getEmpEmail() {
        return empEmail;
    }

    public void setEmpEmail(String empEmail) {
        this.empEmail = empEmail;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getAuthKey() {

        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }
}
