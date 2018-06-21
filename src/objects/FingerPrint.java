package objects;

public class FingerPrint {

    private int code;
    private String owner;
    private byte[] ISO19794;

    public FingerPrint() {
    }

    @Override
    public String toString() {
        return "FingerPrint{" +
                "code=" + code +
                ", owner='" + owner + '\'' +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public byte[] getISO19794() {
        return ISO19794;
    }

    public void setISO19794(byte[] ISO19794) {
        this.ISO19794 = ISO19794;
    }
}
