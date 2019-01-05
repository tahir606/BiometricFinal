package jcode;

import javafx.scene.control.Label;

public class BiometricMain {

    private int deviceNo;

    private BiometricHelper bioHelper;
    private BioZKTHelper bioZKT;

    public BiometricMain(int deviceNo) {
        this.deviceNo = deviceNo;
        switch (deviceNo) {
            case 1: {
                bioHelper = new BiometricHelper();
            }
            case 2: {
                bioZKT = new BioZKTHelper();
            }
            default:
                return;
        }
    }

    public boolean open() {
        switch (deviceNo) {
            case 1: {
                return bioHelper.openDevice();
            }
            case 2: {
                return bioZKT.openDevice();
            }
            default:
                return false;
        }
    }

    public byte[] scanAndReturnISO(Label label) {
        switch (deviceNo) {
            case 1: {
                return bioHelper.scanAndReturnISO();
            }
            case 2: {
                return bioZKT.scanAndReturnISO(label);
            }
            default:
                return null;
        }
    }

    public boolean matchPrints(byte[] p1, byte[] p2) {
        switch (deviceNo) {
            case 1: {
                return bioHelper.matchPrints(p1, p2);
            }
            case 2: {
                boolean boo = bioZKT.matchPrints(p1, p2);
                return boo;
            }
            default:
                return false;
        }
    }

    public byte[] scanForPrint(Label label) {
        switch (deviceNo) {
            case 1: {
                return bioHelper.scanForPrint();
            }
            case 2: {
                return bioZKT.scanForPrint(label);
            }
            default:
                return null;
        }
    }

}
