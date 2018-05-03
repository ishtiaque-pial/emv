package com.viliyantrbr.nfcemvpayer.service;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.viliyantrbr.nfcemvpayer.object.PaycardObject;
import com.viliyantrbr.nfcemvpayer.util.HexUtil;
import com.viliyantrbr.nfcemvpayer.util.LogUtil;
import com.viliyantrbr.nfcemvpayer.util.PseUtil;

import java.util.Arrays;

public class PaymentService extends HostApduService {
    private static final String TAG = PaymentService.class.getSimpleName();

    private PaycardObject mPaycardObject;

    public PaymentService(@NonNull PaycardObject paycardObject) {
        mPaycardObject = paycardObject;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "\"" + TAG + "\": Service create");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "\"" + TAG + "\": Service destroy");
    }

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        byte[] responseApdu = null;

        if (commandApdu != null) {
            LogUtil.d(TAG, "\"" + TAG + "\": Command APDU: " + Arrays.toString(commandApdu));
            LogUtil.d(TAG, "\"" + TAG + "\": Command APDU Hexadecimal: " + HexUtil.bytesToHexadecimal(commandApdu));

            // PSE (Payment System Environment)
            if (Arrays.equals(commandApdu, mPaycardObject.getCPse()) && Arrays.equals(commandApdu, PseUtil.selectPse(null))) {
                responseApdu = mPaycardObject.getRPse();
            }
            // - PSE (Payment System Environment)

            // PPSE (Proximity Payment System Environment)
            if (Arrays.equals(commandApdu, mPaycardObject.getCPpse()) && Arrays.equals(commandApdu, PseUtil.selectPpse(null))) {
                responseApdu = mPaycardObject.getRPpse();
            }
            // - PPSE (Proximity Payment System Environment)
        }

        if (responseApdu != null) {
            return responseApdu;
        }

        return new byte[0];
    }

    @Override
    public void onDeactivated(int reason) {
        LogUtil.d(TAG, "\"" + TAG + "\": Service deactivated");

        try {
            stopSelf();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
            LogUtil.e(TAG, e.toString());

            e.printStackTrace();
        }
    }
}
