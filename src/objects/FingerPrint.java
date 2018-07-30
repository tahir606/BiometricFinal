package objects;

public class FingerPrint {

    private int code;
    private String owner;
    private byte[] ISO19794_one, ISO19794_two;

    public FingerPrint() {
    }

    public FingerPrint(byte[] ISO19794_one, byte[] ISO19794_two) {
        this.ISO19794_one = ISO19794_one;
        this.ISO19794_two = ISO19794_two;
    }

    @Override
    public String toString() {
        return "\n" +
                "Owner = " + owner;
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

    public byte[] getISO19794_one() {
        return ISO19794_one;
    }

    public void setISO19794_one(byte[] ISO19794_one) {
        this.ISO19794_one = ISO19794_one;
    }

    public byte[] getISO19794_two() {
        return ISO19794_two;
    }

    public void setISO19794_two(byte[] ISO19794_two) {
        this.ISO19794_two = ISO19794_two;
    }
}
