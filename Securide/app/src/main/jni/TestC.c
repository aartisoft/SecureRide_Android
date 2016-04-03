//
// Created by Android Studio on 3/29/16.
//

#include <jni.h>
#include "SRTcpClient.c"

jstring Java_com_securide_custmer_FirstActivity_getString(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env, "raam Hello from C World tgrgvgtgg");
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

jstring
Java_com_securide_custmer_connection_core_JNIConnectionManager_getTaxiDetails( JNIEnv* env,  jobject thiz )
{
    unsigned char msgSent[TAXIMSG_SIZE]; /* buffer for sending meesage to server*/
    unsigned char msgRecv[TAXIMSG_SIZE]; /*buffer to receiving meesage from server*/;
    unsigned long serverAddr; /* ip address of the server */
    int socketId;
    int portNum;
    TAXITRIP_t  *tripSnt;  /* pointer to the message send buffer */
    TAXITRIP_t  *tripRcv;  /* pointer to the meesage receive buffer */

    tripSnt = (TAXITRIP_t *)&msgSent;
    tripRcv = (TAXITRIP_t *)&msgRecv;

    /* set the Port number and ip address of the server */
    portNum=14001;
    ((unsigned char *)&serverAddr)[0]=50;
    ((unsigned char *)&serverAddr)[1]=255;
    ((unsigned char *)&serverAddr)[2]=26;
    ((unsigned char *)&serverAddr)[3]=100;

    /* setu the tcp socket and get the handle (socket discriptor) */
    socketId= setupTcpSocket(portNum,serverAddr);
    if (socketId <0)
    {
        conprintf("CANNOT SETUP TECP SOCKET\n");
    }
    else
    {
        /* This code is for sednig the taxi request message to the server
           and getting the response back from the server and then printing it*/
        /* setup the message */
        tripSnt->header.opCode = TAXI_REQUEST;
        tripSnt->taxiNumber = 0; /* will be filled in by server and reply back*/
        tripSnt->driverNumber = 0; /*will be filled in by server *nd reply back/

      /* setup trip addresses */

        /* passanger information*/
        strcpy(tripSnt->tripAddress.passangerFirstName,"Rajvir");
        strcpy(tripSnt->tripAddress.passangerLastName,"Sangha");
        strcpy(tripSnt->tripAddress.passangerMiddleName,"Singh");

        /* where the passangeris going*/
        strcpy(tripSnt->tripAddress.destStreetName,"Marillo Ave");
        tripSnt->tripAddress.destBuildingNumber=3636;
        strcpy(tripSnt->tripAddress.destCity,"San Jose");
        tripSnt->tripAddress.destZipCode=95135;
        strcpy(tripSnt->tripAddress.destLandMarkDesc,
               "close to evergreen store");

        /* where the passanger is beng picked up*/
        strcpy(tripSnt->tripAddress.pickupStreetName,"Alum Rock");
        tripSnt->tripAddress.pickupBuildingNumber=303;
        strcpy(tripSnt->tripAddress.pickupCity,"San Jose");
        tripSnt->tripAddress.pickupZipCode=95134;
        strcpy(tripSnt->tripAddress.pickupLandMarkDesc,
               "close to  AutoZone automobile parts store");

        /*Now this function will send the message to the server and wait for
         response from the server. It will send the message content that
         contained in the msgSent buffer and when the response from the server
         is received this function will copy it to the msgrecv buffer.
         This function sendAndRcvMessage does not return until the
         message is received from the server */

        sendAndRcvMessage(socketId,msgSent, msgRecv ,TAXIMSG_SIZE);

        char* result;
        asprintf(&result, "%d;%d;%s;%d;%d;%d;%d;%d",
                 tripRcv->taxiNumber,
                 tripRcv->driverNumber,
                 tripRcv->driverName,
                 tripRcv->tripTime.estimatedHours,
                 tripRcv->tripTime.estimatedMinutes),
                tripRcv->driverArrivalTime.estimatedHours,
                tripRcv->driverArrivalTime.estimatedMinutes,
                tripRcv->taxiPayment.dollarAmount;
        return (*env)->NewStringUTF(env,  result);
    }
}

jstring
Java_com_securide_custmer_connection_core_JNIConnectionManager_getTaxiGpsMapDetails( JNIEnv* env, jobject thiz, jint socketId)
{
    unsigned char msgSent[TAXIMSG_SIZE]; /* buffer for sending meesage to server*/
    unsigned char msgRecv[TAXIMSG_SIZE]; /*buffer to receiving meesage from server*/;
    TAXITRIP_t  *tripSnt;  /* pointer to the message send buffer */
    TAXITRIP_t  *tripRcv;  /* pointer to the meesage receive buffer */

    tripSnt = (TAXITRIP_t *)&msgSent;
    tripRcv = (TAXITRIP_t *)&msgRecv;

    if(socketId < 0) {
        /* set the Port number and ip address of the server */
        unsigned long serverAddr; /* ip address of the server */
        int portNum=14001;
        ((unsigned char *)&serverAddr)[0]=50;
        ((unsigned char *)&serverAddr)[1]=255;
        ((unsigned char *)&serverAddr)[2]=26;
        ((unsigned char *)&serverAddr)[3]=100;

        /* setu the tcp socket and get the handle (socket discriptor) */
        socketId = setupTcpSocket(portNum, serverAddr);
    }
    if (socketId <0)
    {
        conprintf("CANNOT SETUP TECP SOCKET\n");
        return (*env)->NewStringUTF(env,  "CANNOT SETUP TECP SOCKET");
    }
    else
    {
        int numBytes;
        char msgBuff[TAXIMSG_SIZE];
        GPSUPDATE_t *msgP;

        msgP=(GPSUPDATE_t *)&msgBuff[0];

        numBytes = read(socketId,msgBuff,TAXIMSG_SIZE);

        char* result;
        asprintf(&result, "%d;%d;%d;%d;%d",
                 socketId,
                 msgP->gpsUpdate.longitudeDegrees,
                 msgP->gpsUpdate.longitudeMinutes,
                 msgP->gpsUpdate.latitudeDegrees,
                 msgP->gpsUpdate.latitudeMinutes);

        return (*env)->NewStringUTF(env,  result);
    }
}