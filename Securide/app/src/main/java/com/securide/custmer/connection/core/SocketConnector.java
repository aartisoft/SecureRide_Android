package com.securide.custmer.connection.core;

import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Created by pradeep.kumar on 3/11/16.
 */
public class SocketConnector {

    static {
        System.loadLibrary("securide");
    }

    public static native  void init();
    public static native int setupTcpSocket(int port, long ip);
    public static native void processUserRequest(int socketId, int reQuestType);

    /*public static void openSocket(String ip, int port) {
        try {
            Socket s = new Socket(ip, port);
            DataOutputStream dOut = new DataOutputStream(s.getOutputStream());
            dOut.writeUTF("Hello Server");
            dOut.flush();
            dOut.close();
            s.close();
        } catch (Exception e) {
            Log.d("SecurideConnection".e);
        }
    }*/
}

