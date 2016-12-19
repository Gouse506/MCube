package vmc.mcube.in.model;

/**
 * Created by gousebabjan on 21/7/16.
 */
public class Params {

    private String type;
    private int offset;
    private String limit;
    private String authKey;
    private String gid;
    private String dataid;
    private Boolean isOnline;

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    public Params() {
    }

    private boolean isMore;
    private String groupName;
    private String callId;
    private boolean isSync;


    public Params(String type, int offset, String limit, String authKey, String gid, boolean isMore, boolean isSync) {
        this.type = type;
        this.offset = offset;
        this.limit = limit;
        this.authKey = authKey;
        this.gid = gid;
        this.isMore = isMore;
        this.isSync = isSync;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }



    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getDataid() {
        return dataid;
    }

    public void setDataid(String dataid) {
        this.dataid = dataid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }
}
