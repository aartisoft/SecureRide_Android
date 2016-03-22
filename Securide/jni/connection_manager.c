#include<jni.h>
#include <string.h> /* for strncpy */
#include "tclient2-2.h"


jint Java_com_securide_custmer_connection_core_SocketConnector_setupTcpSocket(JNIEnv env, jobject obj, jint portNum, jlong servIPaddr) {
    int ret = setupTcpSocket(portNum, servIPaddr);
    return ret;
}

void Java_com_securide_custmer_connection_core_SocketConnector_processUserRequest(JNIEnv env, jobject obj, int socketId, int reQuestType){
    int ret = processUserRequest(socketId, reQuestType);
}

void Java_com_securide_custmer_connection_core_SocketConnector_init(JNIEnv env, jobject obj) {
    main();
}
