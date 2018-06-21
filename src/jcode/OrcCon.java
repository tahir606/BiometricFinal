package jcode;

import objects.FingerPrint;

import java.sql.*;

public class OrcCon {

    private String ipAddress = "";
    private String user = "";
    private String pass = "";

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
        user = "NOOR15";
        pass = "NOOR";
        try {
            con = DriverManager.getConnection(ipAddress, user, pass);
            return con;

        } catch (SQLException ex) {
            System.out.println("Not Connected");
            ex.printStackTrace();
            return null;
        }
    }

    public void insertFingerPrint(FingerPrint fp) {

        String query = "INSERT INTO EMP_PRINTS (EP_ID, EP_ISO, EP_OWNER)" +
                " VALUES ((SELECT NVL(MAX(EP_ID),0)+1 FROM EMP_PRINTS),?,?) ";

        try {
            PreparedStatement statement = static_con.prepareStatement(query);
            statement.setBytes(1, fp.getISO19794());
            statement.setString(2, fp.getOwner());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public byte[] getFingerPrint(int id) {
        String query = "SELECT EP_ISO FROM EMP_PRINTS WHERE EP_ID = ? ";

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

}
