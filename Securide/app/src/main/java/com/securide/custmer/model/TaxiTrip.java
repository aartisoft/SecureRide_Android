package com.securide.custmer.model;

import java.io.Serializable;

/**
 * Created by android_studio on 3/31/16.
 */
public class TaxiTrip implements Serializable {
    String opCode;

    public String getOpCode() {
        return opCode;
    }

    public void setOpCode(String opCode) {
        this.opCode = opCode;
    }
}
