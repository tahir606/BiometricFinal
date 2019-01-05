package jcode;

import java.io.*;

public class FileHelper {

    private static final String FADD = "settings/",
        DEVICE_NAME = "deviceName.txt",
        YRNO = "yrno.txt";

    public FileHelper() {
    }

    public void writeDeviceName(String device) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(
                    new File(FADD + DEVICE_NAME));

            writer.write(device);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            writer.close();
        }
    }

    public String readDeviceName() {
        String text = "";
        InputStreamReader isReader = null;
        try {
            isReader = new InputStreamReader(
                    new FileInputStream(
                            new File(FADD + DEVICE_NAME)));
            BufferedReader br = new BufferedReader(isReader);

            text = br.readLine();
            return text;
        } catch (FileNotFoundException e) {
            System.out.println(e);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                isReader.close();
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
        return null;
    }

    public void writeYRNO(String yrno) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(
                    new File(FADD + YRNO));

            writer.write(yrno);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            writer.close();
        }
    }

    public String readYRNO() {
        String text = "";
        InputStreamReader isReader = null;
        try {
            isReader = new InputStreamReader(
                    new FileInputStream(
                            new File(FADD + YRNO)));
            BufferedReader br = new BufferedReader(isReader);

            text = br.readLine();
            return text;
        } catch (FileNotFoundException e) {
            System.out.println(e);
            return "";
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "";
        } finally {
            try {
                isReader.close();
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
        return "";
    }


}
