package jcode;

import SecuGen.FDxSDKPro.jni.*;

public class BiometricHelper {

    private static JSGFPLib sgfplib;
    private long err;

    public BiometricHelper() {
        if (sgfplib == null) {
            System.out.println("Instantiating JSGFPLib Object");
            sgfplib = new JSGFPLib();
            if ((sgfplib != null) && (sgfplib.jniLoadStatus != SGFDxErrorCode.SGFDX_ERROR_JNI_DLLLOAD_FAILED)) {
                System.out.println(sgfplib);
            } else {
                System.out.println("An error occurred while loading JSGFPLIB.DLL JNI Wrapper");
                return;
            }

            // Init()
            err = sgfplib.Init(SGFDxDeviceName.SG_DEV_AUTO);
            System.out.println("Init returned : [" + err + "]");

            // GetLastError()
            err = sgfplib.GetLastError();
            System.out.println("Last error returned : [" + err + "]");

            // GetMinexVersion()
            int[] extractorVersion = new int[1];
            int[] matcherVersion = new int[1];
            err = sgfplib.GetMinexVersion(extractorVersion, matcherVersion);
            System.out.println("GetMinexVersion returned : [" + err + "]");
            System.out.println("Extractor version : [" + extractorVersion[0] + "]");
            System.out.println("Matcher version : [" + matcherVersion[0] + "]");

            // OpenDevice()
            err = sgfplib.OpenDevice(SGPPPortAddr.AUTO_DETECT);
            System.out.println("OpenDevice returned : [" + err + "]");

            // GetError()
            err = sgfplib.GetLastError();
            System.out.println("GetLastError returned : [" + err + "]");

            // GetDeviceInfo()
            SGDeviceInfoParam deviceInfo = new SGDeviceInfoParam();
            err = sgfplib.GetDeviceInfo(deviceInfo);
            System.out.println("GetDeviceInfo returned : [" + err + "]");
            System.out.println("\tdeviceInfo.DeviceSN:    [" + new String(deviceInfo.deviceSN()).trim() + "]");
            System.out.println("\tdeviceInfo.Brightness:  [" + deviceInfo.brightness + "]");
            System.out.println("\tdeviceInfo.ComPort:     [" + deviceInfo.comPort + "]");
            System.out.println("\tdeviceInfo.ComSpeed:    [" + deviceInfo.comSpeed + "]");
            System.out.println("\tdeviceInfo.Contrast:    [" + deviceInfo.contrast + "]");
            System.out.println("\tdeviceInfo.DeviceID:    [" + deviceInfo.deviceID + "]");
            System.out.println("\tdeviceInfo.FWVersion:   [" + deviceInfo.FWVersion + "]");
            System.out.println("\tdeviceInfo.Gain:        [" + deviceInfo.gain + "]");
            System.out.println("\tdeviceInfo.ImageDPI:    [" + deviceInfo.imageDPI + "]");
            System.out.println("\tdeviceInfo.ImageHeight: [" + deviceInfo.imageHeight + "]");
            System.out.println("\tdeviceInfo.ImageWidth:  [" + deviceInfo.imageWidth + "]");

            setLed(true);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            setLed(false);
        }
    }

    public void setLed(boolean b) {
        err = sgfplib.SetLedOn(b);
    }

    public byte[] scanForPrint() {
        int[] quality = new int[1];
        int[] maxSize = new int[1];
        int[] size = new int[1];
        SGFingerInfo fingerInfo = new SGFingerInfo();
        fingerInfo.FingerNumber = SGFingerPosition.SG_FINGPOS_LI;
        fingerInfo.ImageQuality = quality[0];
        fingerInfo.ImpressionType = SGImpressionType.SG_IMPTYPE_LP;
        fingerInfo.ViewNumber = 1;



//////////////////////////////////////////////////////////////////////////////
// Finger 1
        ///////////////////////////////////////////////
        // getImage() - 1st Capture
        System.out.println("Call SetLedOn(true)");
        err =sgfplib.SetLedOn(true);
        System.out.println("SetLedOn returned : [" + err + "]");
        System.out.print("Capture 1. Please place [" + finger + "] on sensor with LEDs on and press <ENTER> ");
        imageBuffer1 = new byte[deviceInfo.imageHeight*deviceInfo.imageWidth];
        try
        {
            System.in.read(kbBuffer);
            System.out.println("Call GetImage()");
            err = sgfplib.GetImage(imageBuffer1);
            System.out.println("GetImage returned : [" + err + "]");
            if (err == SGFDxErrorCode.SGFDX_ERROR_NONE)
            {
                err = sgfplib.GetImageQuality(deviceInfo.imageWidth, deviceInfo.imageHeight, imageBuffer1, quality);
                System.out.println("GetImageQuality returned : [" + err + "]");
                System.out.println("Image Quality is : [" + quality[0] + "]");
                fout = new FileOutputStream(finger + "1.raw");
                fp = new PrintStream(fout);
                fp.write(imageBuffer1,0, imageBuffer1.length);
                fp.close();
                fout.close();
                fp = null;
                fout = null;
            }
            else
            {
                System.out.println("ERROR: Fingerprint image capture failed for sample1.");
                return; //Cannot continue test if image not captured
            }
        }
        catch (IOException e)
        {
            System.out.println("Exception reading keyboard : " + e);
        }
    }

}
