//
// Created by Android Studio on 3/29/16.
//

#include <jni.h>
#include "SRTcpClient.c"

jstring Java_com_securide_custmer_FirstActivity_getString(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env, "Hello from C World");
}

jint Java_com_securide_custmer_connection_core_JNIConnectionManager_setupNativeTcpSocket(
        JNIEnv *env, jobject thiz) {
    unsigned long serverAddr; /* ip address of the server */
    static int socketId;
    int portNum;
    portNum = 14001;
    ((unsigned char *) &serverAddr)[0] = 50;
    ((unsigned char *) &serverAddr)[1] = 255;
    ((unsigned char *) &serverAddr)[2] = 26;
    ((unsigned char *) &serverAddr)[3] = 100;
    socketId = setupTcpSocket(portNum, serverAddr);
//    processUserRequest(socketId,);
    return socketId;
}

jobject Java_com_securide_custmer_connection_core_JNIConnectionManager_getCabAvailability(
        JNIEnv *env, jobject thiz, int cabType, jobject source, jobject destination,
        jobject result) {

    jclass cls, resultCls,retCls;
    jfieldID fid_lattitudeID;
    jfieldID fid_longitudeID;
    jstring jLattitude;
    jstring jLongitude;

    jstring opCode;

    jfieldID fid_opCodeID;
    jmethodID setOpCodeMethodId;
    jobject objRet;


    cls = (*env)->GetObjectClass(env, source);
    resultCls = (*env)->GetObjectClass(env, result);
//    retCls = (*env)->FindClass (env,"com/securide/custmer/model/TaxiTrip");
//    objRet = (*env)->AllocObject(env,retCls);

    fid_lattitudeID = (*env)->GetFieldID(env, cls, "lattitude", "Ljava/lang/String;");
    fid_longitudeID = (*env)->GetFieldID(env, cls, "longitude", "Ljava/lang/String;");
    jLattitude = (*env)->GetObjectField(env, source, fid_lattitudeID);
    jLongitude = (*env)->GetObjectField(env, source, fid_lattitudeID);

//    fid_opCodeID = (*env)->GetFieldID(env, resultCls, "longitude", "Ljava/lang/String;");
    setOpCodeMethodId = (*env)->GetMethodID(env, resultCls, "setOpCode", "(Ljava/lang/String;)V");
    opCode = (*env)->NewStringUTF(env, "1");
    (*env)->CallVoidMethod(env, result, setOpCodeMethodId, opCode);


    return result;
}