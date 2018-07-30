package objects;

public class Employee {

    int code;
    String name, imgName, fatherName, phone;
    FingerPrint print;

    public Employee() {
    }

    @Override
    public String toString() {
        return code + " - " + name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public FingerPrint getPrint() {
        return print;
    }

    public void setPrint(FingerPrint print) {
        this.print = print;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }
}
