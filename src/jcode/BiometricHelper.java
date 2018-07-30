package jcode;

import SecuGen.FDxSDKPro.jni.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class BiometricHelper {

    private static JSGFPLib sgfplib;
    private static SGDeviceInfoParam deviceInfo;
    private static byte kbBuffer[] = new byte[100];
    private static long err;

    public BiometricHelper() {
        if (sgfplib == null) {
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
            if (err != 0) {
                return;
            }
            // GetError()
            err = sgfplib.GetLastError();
            System.out.println("GetLastError returned : [" + err + "]");

            // GetDeviceInfo()
            deviceInfo = new SGDeviceInfoParam();
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

//            setLed(true);
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            setLed(false);
        }
    }

    public long setLed(boolean b) {
        return sgfplib.SetLedOn(b);
    }

    public byte[] scanForPrint() {  //Returns in RAW Format
        int[] quality = new int[1];
        byte[] imageBuffer;

        // getImage()
        err = sgfplib.SetLedOn(true);
        System.out.println("SetLedOn returned : [" + err + "]");
//        System.out.print("Press Enter to Scan: ");
        imageBuffer = new byte[deviceInfo.imageHeight * deviceInfo.imageWidth];
//            System.in.read(kbBuffer);
        System.out.println("Scanning..");
        err = sgfplib.GetImage(imageBuffer);
        System.out.println("GetImage returned : [" + err + "]");
        if (err != 0) {
            sgfplib.SetLedOn(false);
            return null;
        }
        if (err == SGFDxErrorCode.SGFDX_ERROR_NONE) {
            err = sgfplib.GetImageQuality(deviceInfo.imageWidth, deviceInfo.imageHeight, imageBuffer, quality);
            System.out.println("GetImageQuality returned : [" + err + "]");
            System.out.println("Image Quality is : [" + quality[0] + "]");
            return imageBuffer;
        } else {
            System.out.println("ERROR: Fingerprint image capture failed for sample1.");
            return null; //Cannot continue test if image not captured
        }
    }

    public byte[] convertRawtoISO19794(byte[] imageBuffer) {
        if (imageBuffer == null)
            return null;

        int[] maxSize = new int[1];
        byte[] ISOminutiaeBuffer1;
        int[] size = new int[1];

        SGFingerInfo fingerInfo = new SGFingerInfo();
        fingerInfo.FingerNumber = SGFingerPosition.SG_FINGPOS_LI;
//        fingerInfo.ImageQuality = quality[0];
        fingerInfo.ImpressionType = SGImpressionType.SG_IMPTYPE_LP;
        fingerInfo.ViewNumber = 1;

        // Set Template format ISO19794
        err = sgfplib.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_ISO19794);
        System.out.println("SetTemplateFormat returned : [" + err + "]");
        // Get Max Template Size for ISO19794
        err = sgfplib.GetMaxTemplateSize(maxSize);
        System.out.println("GetMaxTemplateSize returned : [" + err + "]");
        System.out.println("Max ISO19794 Template Size is : [" + maxSize[0] + "]");

        // Create ISO19794 Template for Finger
        ISOminutiaeBuffer1 = new byte[maxSize[0]];
        err = sgfplib.CreateTemplate(fingerInfo, imageBuffer, ISOminutiaeBuffer1);
        System.out.println("CreateTemplate returned : [" + err + "]");
        err = sgfplib.GetTemplateSize(ISOminutiaeBuffer1, size);
        System.out.println("GetTemplateSize returned : [" + err + "]");
        System.out.println("ISO19794 Template Size is : [" + size[0] + "]");
        if (err == SGFDxErrorCode.SGFDX_ERROR_NONE) {
            return ISOminutiaeBuffer1;
        }
        return null;
    }

    public byte[] scanAndReturnISO() {
        return convertRawtoISO19794(scanForPrint());
    }

    public boolean matchPrints(byte[] p1, byte[] p2) {
        boolean[] matched = new boolean[1];
        int[] score = new int[1];
        //Match ISO19794 Templates
        System.out.println("--------");
        matched[0] = false;
        score[0] = 0;
        System.out.println("Call SetTemplateFormat(ISO19794)");
        err = sgfplib.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_ISO19794);
        System.out.println("SetTemplateFormat returned : [" + err + "]");
        System.out.println("Call MatchIsoTemplates()");
        err = sgfplib.MatchIsoTemplate(p1, 0, p2, 0, SGFDxSecurityLevel.SL_NORMAL, matched);
        System.out.println("MatchISOTemplates returned : [" + err + "]");
        System.out.println("ISO-1 <> ISO-2 Match Result : [" + matched[0] + "]");
        System.out.println("Call GetIsoMatchingScore()");
        err = sgfplib.GetIsoMatchingScore(p1, 0, p2, 0, score);
        System.out.println("GetIsoMatchingScore returned : [" + err + "]");
        System.out.println("ISO-1  <> ISO-2 Match Score : [" + score[0] + "]");

        return matched[0];
    }


}
