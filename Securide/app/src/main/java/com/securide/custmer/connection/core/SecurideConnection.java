package com.securide.custmer.connection.core;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by pradeep.kumar on 3/17/16.
 */
public abstract class SecurideConnection {

    public Socket getSocket(){
        Socket socket = null;
        try {
            Log.d("SecurideConnection", "CONNECTED TO SOCKET");
            socket = new Socket(ConnectionConstants.SERVER_IP, ConnectionConstants.SERVER_PORT);
        } catch (UnknownHostException e) {
            Log.d("SecurideConnection", "connection failed");
            e.printStackTrace();
        } catch (IOException ioe) {
            Log.d("SecurideConnection", "connection failed");
            ioe.printStackTrace();
        }
        Log.d("SecurideConnection","Connected to host");
        return socket;
    }

    public int writeShort(int inPut, BufferedOutputStream out) throws IOException {
        byte[] outPut = { 0, 2 };
        int count;

        convertShortToByte(inPut, outPut);
        count = 0;
        while (count < 2) {
            out.write(outPut[count]);
            count = count + 1;
        }
        return (count);
    }

    public void convertShortToByte(int inPut, byte[] outPut) {
        outPut[0] = (byte) (inPut);
        outPut[1] = (byte) (inPut >> 8);
        Log.d("SecurideConnection"," inPut " + inPut);
        Log.d("SecurideConnection"," byte0 " + outPut[0] + " byte1 " + outPut[1]);
    }


    public int writeString(String str, int strLength, BufferedOutputStream out) throws IOException {
        int nameLength = 0;
        int countByte;
        int totalCount;
        int numPads;
        char character;
        int asciiVal;

        nameLength = str.length();
        countByte = 0;
        Log.d("SecurideConnection","INSIDE WRITE STRING->" + str + strLength);
        while (countByte < nameLength) {
            character = str.charAt(countByte);
            asciiVal = (int) character;
            out.write(asciiVal);
            countByte = countByte + 1;
        }
        numPads = 0;
        numPads = writePadding(strLength - nameLength, out);
        totalCount = countByte + numPads;
        return (totalCount);
    }

    public int writePadding(int numPads, BufferedOutputStream out) throws IOException {
        int padCount = 0;
        while (padCount < numPads) {
            out.write(0);
            padCount = padCount + 1;
        }
        return (padCount);
    }

}
