package jcode;

import objects.Employee;
import objects.FingerPrint;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrcCon {

    private static String ipAddress = "";
    private static String user = "";
    private static String pass = "";

    private static Connection static_con;

    public OrcCon() {
        if (static_con == null)
            static_con = connectDB();
    }

    public Connection connectDB() {

        Connection con;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException ex) {
            System.out.println("Driver Not Found");
        }

        ipAddress = "jdbc:oracle:thin:@" + "192.168.100.110:1521" + ":orcl";
        user = "PKDOT";
        pass = "PK786";
        try {
            con = DriverManager.getConnection(ipAddress, user, pass);
            return con;

        } catch (SQLException ex) {
            System.out.println("Not Connected");
            ex.printStackTrace();
            return null;
        }
    }

    public boolean authenticateLogin(String username, String password) {
//        String query = "SELECT UNAME FROM USRACCOUNT " +
        String query = "SELECT UNAME FROM EMPLOYEE_MASTER " +
                "WHERE UNAME = ? " +
                "AND PASWD = ? ";

        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet set = statement.executeQuery();

            if (!set.isBeforeFirst())
                return false;
            else
                return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void insertFingerPrint(FingerPrint fp, int no) {

        String query = "INSERT INTO EMP_PRINTS (ECODE, EISO" + String.valueOf(no) + ") " +
                " VALUES (?,?) ";

        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            statement.setInt(1, fp.getCode());
            if (no == 1)
                statement.setBytes(2, fp.getISO19794_one());
            else if (no == 2)
                statement.setBytes(2, fp.getISO19794_two());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void updateFingerPrint(FingerPrint fp, int no) {

        String query = "UPDATE EMP_PRINTS SET EISO" + String.valueOf(no) + " = ? " +
                " WHERE ECODE = ? ";

        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            if (no == 1)
                statement.setBytes(1, fp.getISO19794_one());
            else if (no == 2)
                statement.setBytes(1, fp.getISO19794_two());
            statement.setInt(2, fp.getCode());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public byte[] getFingerPrint(int id, int no) {
        String query = "SELECT EP_ISO" + String.valueOf(no) + " FROM EMP_PRINTS WHERE EP_ID = ? ";

        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                return set.getBytes(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean checkForFingerprint(int id) {
        String query = " SELECT EISO1, EISO2 FROM EMP_PRINTS WHERE ECODE = ? ";

        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet set = statement.executeQuery();

            if (!set.next()) {
                return false;
            } else {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<FingerPrint> getAllFingerPrints() {
        String query = "SELECT EP_ID, EP_OWNER, EPISO1, EPISO2  FROM EMP_PRINTS";

        List<FingerPrint> listOfPrints = new ArrayList<>();
        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                FingerPrint print = new FingerPrint();
                print.setCode(set.getInt("EP_ID"));
                print.setOwner(set.getString("EP_OWNER"));
                print.setISO19794_one(set.getBytes("EP_ISO1"));
                print.setISO19794_two(set.getBytes("EP_ISO2"));
                listOfPrints.add(print);
            }
            set.close();
            statement.close();
            return listOfPrints;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Employee> getAllEmployees() {
        //PHONE FOR NOOR
        String query = "SELECT ECODE, ENAME, FNAME, PCELL FROM EMPLOYEE_MASTER WHERE LDATE IS NULL ORDER BY ENAME";

        List<Employee> listOfEmployees = new ArrayList<>();
        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                Employee emp = new Employee();
                emp.setCode(set.getInt("ECODE"));
                emp.setName(set.getString("ENAME"));
                emp.setFatherName(set.getString("FNAME"));
                emp.setPhone(set.getString("PCELL"));
                getEmployeeDetails(emp);
                listOfEmployees.add(emp);
            }
            set.close();
            statement.close();
            return listOfEmployees;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Employee getCompleteEmployeeDetails(int code) {
        String query = "SELECT ECODE, ENAME, FNAME, PHONE FROM EMPLOYEE_MASTER" +
                " WHERE LDATE IS NULL " +
                " AND ECODE = ? " +
                " ORDER BY ENAME";

        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            statement.setInt(1, code);
            ResultSet set = statement.executeQuery();
            Employee emp = null;
            while (set.next()) {
                emp = new Employee();
                emp.setCode(set.getInt("ECODE"));
                emp.setName(set.getString("ENAME"));
                emp.setFatherName(set.getString("FNAME"));
                emp.setPhone(set.getString("PHONE"));
                getEmployeeDetails(emp);
            }
            set.close();
            statement.close();

            return emp;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    //Get thumb prints and photo
    public void getEmployeeDetails(Employee emp) {
        String query = "SELECT (SELECT EISO1 FROM EMP_PRINTS EI WHERE EI.ECODE = ?) AS EISO1, " +
                "(SELECT EISO2 FROM EMP_PRINTS EJ WHERE EJ.ECODE = ?) AS EISO2,(SELECT PHOTT FROM EMPLOYEE_IMAGES EP WHERE EP.ECODE = ?) AS PHOTT FROM DUAL";

        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            statement.setInt(1, emp.getCode());
            statement.setInt(2, emp.getCode());
            statement.setInt(3, emp.getCode());

            ResultSet set = statement.executeQuery();
            while (set.next()) {
                emp.setPrint(new FingerPrint(set.getBytes("EISO1"), set.getBytes("EISO2")));
                Blob blob = set.getBlob("PHOTT");
                if (blob != null) {
                    byte b[] = blob.getBytes(1, (int) blob.length());
                    FileOutputStream output = null;
                    try {
                        output = new FileOutputStream("img/" + emp.getCode());
                        output.write(b);
                        output.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            set.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateSalary(Employee emp, String month, int year) {
//        String query = "UPDATE SAVED_SALARY SET  BIOCHECK = 'Y' " + for noor
        String query = "UPDATE EMPLOYEE_SALARY_MST SET  BIOCHECK = 'Y' " +
                " WHERE ECODE = ? " +
                " AND EDATE = ? ";

        PreparedStatement statement = null;

        try {
            statement = static_con.prepareStatement(query);
            statement.setInt(1, emp.getCode());
            statement.setString(2, "1-" + month + "-" + year);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentYear() {
        String query = "SELECT YRNO " +
                " FROM   OLDINFO " +
                " WHERE  CURN ='Y'";

        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                return set.getString("YRNO");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "";
    }

    public List<String> getVoucherList() {
        String query = "SELECT SRNO, RMARKS " +
                " FROM   VOUCH_LIST " +
                " WHERE  fpver = 'Y'";
        List<String> list = new ArrayList<>();
        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                list.add(set.getString("SRNO") + " - " + set.getString("RMARKS"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public String getVoucherDetails(int srno, String yrno, int voucher) {
        String query = "SELECT FP_DETAIL(" + srno + ",'" + yrno + "'," + voucher + ") FROM DUAL";
        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                return set.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void verifyVoucher(int srno, String yrno, int voucher) {
        try {
            CallableStatement storedProc = static_con.prepareCall("{call FP_VERIFIED(?, ?, ?)}");
            storedProc.setInt(1, srno);
            storedProc.setString(2, yrno);
            storedProc.setInt(3, voucher);
            storedProc.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
