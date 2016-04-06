#include <stdio.h>

#include <string.h> /* for strncpy */

#include <sys/types.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <netinet/in.h>
#include <net/if.h>
#include <sys/times.h>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include "SRClientStructure.h"

//extern int errno ;

#define conprintf printf


/**************************************************
*
**************************************************/
int createTcpSocket(int portNumber, unsigned long serverIpAddr )
{
    int s;
    int retCode;
    struct sockaddr_in sockIn;
    int rtFd;
    int portNum;

    s = socket(AF_INET,SOCK_STREAM,0);
    if(s < 0)
    {
        conprintf("I cannot get a socket on Client\n");
        exit(1);
    }
    portNum = htons(portNumber); /* convert to netowrk byte ordering */
    sockIn.sin_family = AF_INET;
    sockIn.sin_addr.s_addr = serverIpAddr;
    sockIn.sin_port = portNum;
    conprintf(" conneting on port %d to server %u.%u.%u.%u \n",
              portNumber,
              ((unsigned char *)&serverIpAddr)[0],
              ((unsigned char *)&serverIpAddr)[1],
              ((unsigned char *)&serverIpAddr)[2],
              ((unsigned char *)&serverIpAddr)[3]);

    /* do a connect and it should return */
    conprintf("GOING TO CONNECT\n");
    retCode = connect(s,(struct sockaddr *)&sockIn,sizeof(sockIn));
    if(retCode < 0)
    {
        switch(errno)
        {
            case  EINPROGRESS:
                conprintf("create socket\n");
                rtFd=s;
                break;

            case  ETIMEDOUT:
                conprintf("tyring to connect timed out\n");
                close(s);
                rtFd=-1;
                break;

            case ECONNREFUSED:
                conprintf("server not present\n");
                close(s);
                rtFd=-1;
                break;

            default:
                conprintf("connect error: errno %d\n",
                          errno);
                close(s);
                rtFd=-1;
                break;
        }
    }
    else
    {
        conprintf("create socket. RetCode %d\n", retCode);
    }
    rtFd=s;
    return(rtFd);
}
/*****************************************************
* setupTcpSocket()
*
*****************************************************/
int setupTcpSocket(int portNum, unsigned long servIPaddr)
{
    int tcpFd;

    tcpFd= createTcpSocket(portNum,
                           servIPaddr);
    if(tcpFd <0)
    {
        conprintf("CANNOT CREATE TCP socket. port %d\n",
                  portNum);
        return(-1);
    }
    return(tcpFd);
}
/*****************************************************
* This function sents the messge that is contained in the msfSent
* buffer to the server and then waits for the response from the server.
* It receives the response in the msgRecv buffer from the server.
*
* void sendAndRcvMessage(int socketId, unsigned char *msgSent,
*   unsigned  char *msgRecv )
*
*****************************************************/
void sendAndRcvMessage(int socketId, unsigned char *msgSent,
                       unsigned  char *msgRecv , short msgSize)
{
    int numBytes;

    /* write the message to the socket that is connected
       to the server. */

    numBytes = write(socketId,msgSent,TAXIMSG_SIZE);
    if(numBytes<0)
    {
        conprintf("cannot send on the socket\n");
        return;
    }
    /* Now wait for the response from the server and receiv it
      in the msgRecv Buffer */
    conprintf("waiting for Server. byte written %d\n",
              numBytes);
    numBytes = read(socketId,msgRecv,TAXIMSG_SIZE);
    conprintf(" number of bytes received. %d\n", numBytes);
}
/***********************************************************
* This function sends the RIDE REQUEST  message to the
* server and receives the response.
* void requestRide(int socketId, unsigned char *msgSent,
*   unsigned  char *msgRecv )
*
*****************************************************/
void processUserRequest(int socketId, int reQuestType)
{
    unsigned char msgSent[TAXIMSG_SIZE]; /* buffer for sending meesage to server*/
    unsigned char msgRecv[TAXIMSG_SIZE];/*buffer to receiving meesage from server*/
    TAXITRIP_t  *tripSnt;  /* pointer to the message send buffer */
    TAXITRIP_t  *tripRcv;  /* pointer to the meesage receive buffer */
    CUSTOMER_REG_t *customerRegSnt;
    CUSTOMER_REG_t *customerRegRcv;
    char taxiNumber[MAX_LICENSE_LENGTH]; /* to store the license plate
                                      number received from server
                                      in response to TAXI REQUEST. It will be
                                      used to respond to the server
                                      when the ride is accepted or rejected.*/

    char driverNumber ;        /* to store the driver number whch is received
                              from server in response to TAXI REQUEST.
                              It will be used to respond to the server
                               to identify the driver when the ride is
                               accepted or rejected.*/
    char driverName[MAX_NAME_LENGTH];/*to store the driver name whch is received
                              from server in response to TAXI REQUEST.
                              It will be used to respond to the server
                               to identify the driver when the ride is
                               accepted or rejected.*/


    tripSnt = (TAXITRIP_t *)&msgSent;
    tripRcv = (TAXITRIP_t *)&msgRecv;
    customerRegSnt=(CUSTOMER_REG_t *)&msgSent;
    customerRegRcv=(CUSTOMER_REG_t *)&msgRecv;
    switch(reQuestType)
    {

        case TAXI_REQUEST:
            tripSnt->header.opCode = TAXI_REQUEST;
            tripSnt->driverNumber = 0; /*will be filled in by server and
                                         reply back*/
            tripSnt->appType = CUSTOMER_APP;
            tripSnt->taxiType = SPECIAL_ASSIST_CAB;/*options here are
                                                    LEMOSINE, REGULAR_CAB,
                                                    or SPECIAL_ASSIST_CAB */

            /* setup trip addresses */

            /* passanger information*/
            strcpy(tripSnt->tripAddress.passangerFirstName,"Rajvir");
            strcpy(tripSnt->tripAddress.passangerLastName,"Sangha");
            strcpy(tripSnt->tripAddress.passangerMiddleName,"Singh");

            /* where the passangeris going*/
            strcpy(tripSnt->tripAddress.destStreetName,"Marillo Ave");
            strcpy(tripSnt->tripAddress.destBuildingNumber,"3636");
            strcpy(tripSnt->tripAddress.destCity,"San Jose");
            strcpy(tripSnt->tripAddress.destZipCode,
                   "95135");
            strcpy(tripSnt->tripAddress.destLandMarkDesc,
                   "close to evergreen store");

            /* where the passanger is beng picked up*/
            strcpy(tripSnt->tripAddress.pickupStreetName,"Alum Rock");
            strcpy(tripSnt->tripAddress.pickupBuildingNumber,"303D");
            strcpy(tripSnt->tripAddress.pickupCity,"San Jose");
            strcpy(tripSnt->tripAddress.pickupZipCode,
                   "95134");
            strcpy(tripSnt->tripAddress.pickupLandMarkDesc,
                   "close to  AutoZone automobile parts store");

            /* Now setup the GPS cordinates of where the customer is */
            tripSnt->customerGPS.longitudeDegrees= 10;
            tripSnt->customerGPS.longitudeDegrees= 30;
            tripSnt->customerGPS.NorS = 'N';
            tripSnt->customerGPS.latitudeDegrees= 20;
            tripSnt->customerGPS.longitudeDegrees= 35;
            tripSnt->customerGPS.EorW = 'E';

            /*Now this function will send the message to the server and wait
              for response from the server. It will send the message content
              that contained in the msgSent buffer and when the response from
              the server is received this function will copy it to the msgrecv
              buffer.
              This function sendAndRcvMessage does not retunr until the
              message is received from the server */

            sendAndRcvMessage(socketId,msgSent,
                              msgRecv ,TAXIMSG_SIZE);

            /* print the content of the message received */
            conprintf("\n\nSERVER RESPONSE: opCode %d\n",
                      tripRcv->header.opCode);
            conprintf("taxi number:-> %s\n",tripRcv->taxiNumber);
            conprintf("driver number:-> %d\n",tripRcv->driverNumber);
            conprintf("taxi Driver name: %s\n",tripRcv->driverName);

            conprintf("estimated trip time: %d hour %d minutes\n",
                      tripRcv->tripTime.estimatedHours,
                      tripRcv->tripTime.estimatedMinutes);
            conprintf("estimated trip Cost: %s \n",
                      tripRcv->estimatedCost);
            conprintf("estimated driver Arrival time: %d hour %d minutes\n",
                      tripRcv->driverArrivalTime.estimatedHours,
                      tripRcv->driverArrivalTime.estimatedMinutes);
            /* save the driver number and cab license number to
               identify the driver with when sending the server the
               cab ride accepted or rejected message */
            strcpy(taxiNumber,tripRcv->taxiNumber);
            strcpy(driverName,tripRcv->driverName);
            driverNumber =  tripRcv->driverNumber;
            break;

        case TAXI_JOB_ACCEPTED:
            tripSnt->header.opCode = TAXI_JOB_ACCEPTED;
            tripSnt->appType = CUSTOMER_APP;
            tripSnt->taxiType = SPECIAL_ASSIST_CAB;/*options here are
                                                    LEMOSINE, REGULAR_CAB,
                                                    or SPECIAL_ASSIST_CAB */
            strcpy(tripSnt->taxiNumber,taxiNumber);
            strcpy(tripSnt->driverName, driverName);
            tripSnt->driverNumber=driverNumber;
            /* other fields are not required. */
            /* now send out a message and get the response back */
            sendAndRcvMessage(socketId,msgSent,
                              msgRecv ,TAXIMSG_SIZE);
            /* check the received response is confirmation */

            conprintf("OPCODE FROM SERVER %d\n",
                      tripRcv->header.opCode);
            if (tripRcv->header.opCode == SERVER_CONFIRMED)
            {
                conprintf("Server has confirmed the taxi accept\n");
            }
            else
            {
                conprintf("Server has sent opcode %d for the taxi accept\n",
                          tripRcv->header.opCode);
            }
            break;


        case TAXI_JOB_REJECTED:
            tripSnt->header.opCode = TAXI_JOB_REJECTED;
            tripSnt->appType = CUSTOMER_APP;
            tripSnt->taxiType = SPECIAL_ASSIST_CAB;/*options here are
                                                    LEMOSINE, REGULAR_CAB,
                                                    or SPECIAL_ASSIST_CAB */
            strcpy(tripSnt->taxiNumber,taxiNumber);
            strcpy(tripSnt->driverName, driverName);
            tripSnt->driverNumber=driverNumber;
            /* other fields are not required. */
            /* now send out a message and get the response back */
            sendAndRcvMessage(socketId,msgSent,
                              msgRecv ,TAXIMSG_SIZE);
            /* check the received response is confirmation */

            conprintf("OPCODE FROM SERVER %d\n",
                      tripRcv->header.opCode);
            if (tripRcv->header.opCode == SERVER_CONFIRMED)
            {
                conprintf("Server has confirmed the taxi rejected\n");
            }
            else
            {
                conprintf("Server has sent opcode %d for the taxi rejected\n",
                          tripRcv->header.opCode);
            }

            break;


        case GET_CAB_AVAILABILITY:
            tripSnt->header.opCode = GET_CAB_AVAILABILITY;
            tripSnt->appType = CUSTOMER_APP;
            tripSnt->taxiType = SPECIAL_ASSIST_CAB;/*options here are
                                                    LEMOSINE, REGULAR_CAB,
                                                    or SPECIAL_ASSIST_CAB */
            /* where the passanger is beng picked up*/
            strcpy(tripSnt->tripAddress.pickupStreetName,"Alum Rock");
            strcpy(tripSnt->tripAddress.pickupBuildingNumber,"303D");
            strcpy(tripSnt->tripAddress.pickupCity,"San Jose");
            strcpy(tripSnt->tripAddress.pickupZipCode,
                   "95134");
            strcpy(tripSnt->tripAddress.pickupLandMarkDesc,
                   "close to  AutoZone automobile parts store");

            /* Now setup the GPS cordinates of where the customer is */
            tripSnt->customerGPS.longitudeDegrees= 10;
            tripSnt->customerGPS.longitudeDegrees= 30;
            tripSnt->customerGPS.NorS = 'N';
            tripSnt->customerGPS.latitudeDegrees= 20;
            tripSnt->customerGPS.longitudeDegrees= 35;
            tripSnt->customerGPS.EorW = 'E';
            /* other parameters are NOT required*/
            /* now send out a message and get the response back */
            sendAndRcvMessage(socketId,msgSent,
                              msgRecv ,TAXIMSG_SIZE);
            /* check the  response from the server. There will be three
              options of response: cab is available, a different cab type
              available, cab not available. The corresponding opCodes
              returned are; CAB_AVAILABLE,DIFFERENT_CAB_AVAILABLE,
                            NO_DRIVER_VAILABLE repsectivly. In the case
                            different type of cab is available, the cab type
                            that is available is returned in the taxiType
                            field */

            conprintf("OPCODE FROM SERVER %d\n",
                      tripRcv->header.opCode);
            if (tripRcv->header.opCode == CAB_AVAILABLE)
            {
                conprintf("Cab requested is available.\n");
            }
            if (tripRcv->header.opCode == DIFFERENT_CAB_AVAILABLE)
            {
                conprintf("Different Type of cab is available. cab type is %d\n",
                          tripRcv->taxiType);
            }
            if (tripRcv->header.opCode == NO_DRIVER_AVAILABLE)
            {
                conprintf("No Cab is  available at this time.\n");
                conprintf("Please check back in 10 minutes. My applogies\n");
            }
            break;

        case GET_CUSTOMER_PAYMENTINFO:
            break;

        case REGISTER_CUSTOMER:
            tripSnt->header.opCode = REGISTER_CUSTOMER;
            tripSnt->appType = CUSTOMER_APP;
            strcpy(tripSnt->custInfo.customerFirstName,
                   "Jaswinder");
            strcpy(tripSnt->custInfo.customerMiddleName,
                   "Singh");
            strcpy(tripSnt->custInfo.customerLastName,
                   "Sidhu");
            strcpy(tripSnt->custInfo.customerEmail,
                   "jaswinder@gmail.com");
            strcpy(tripSnt->custInfo.customerPhone,
                   "604-596-1679");
            strcpy(tripSnt->custInfo.paswrd,
                   "JasSAN123");
            tripSnt->custInfo.changePaswrdFlag = 0;
            /* add the credit card info if given.*/
            strcpy(tripSnt->custInfo.credit.creditCardNumber,
                   "2342111123456789");
            strcpy(tripSnt->custInfo.credit.creditCardName,
                   "jagdeesh");
            tripSnt->custInfo.credit.creditCardType =2;
            tripSnt->custInfo.credit.expryMonth =11;
            tripSnt->custInfo.credit.expryYear =2020;
            tripSnt->custInfo.credit.codeOnBack =189;
            /* now send out a message and get the response back */
            sendAndRcvMessage(socketId,msgSent,
                              msgRecv ,TAXIMSG_SIZE);
            /* now check the response from the server*/


            conprintf("OPCODE FROM SERVER %d\n",
                      tripRcv->header.opCode);
            if (tripRcv->header.opCode == CUSTOMER_REGISTER_SUCCESS)
            {
                printf("REGISTRATION SUCCESS\n");
            }
            if (tripRcv->header.opCode == CUSTOMER_REGISTER_FAILURE)
            {
                printf("REGISTRATION FAILURE\n");
            }

            break;


        case GET_CUSTOMER_HISTORY:
            tripSnt->header.opCode = GET_CUSTOMER_HISTORY;
            tripSnt->appType = CUSTOMER_APP;
            strcpy(tripSnt->custInfo.customerFirstName,
                   "Jaswinder");
            strcpy(tripSnt->custInfo.customerMiddleName,
                   "Singh");
            strcpy(tripSnt->custInfo.customerLastName,
                   "Sidhu");
            strcpy(tripSnt->custInfo.paswrd,
                   "JasSAN123");
            tripSnt->custInfo.changePaswrdFlag = 0;
            /* note you do not need to provide the email address
               or the phone number. The server will return those to you*/
            /* now send out a message and get the response back */
            sendAndRcvMessage(socketId,msgSent,
                              msgRecv ,TAXIMSG_SIZE);
            /* now check the response from the server*/
            conprintf("OPCODE FROM SERVER %d\n",
                      tripRcv->header.opCode);
            if ((tripRcv->header.opCode == CUSTOMER_AUTH_SUCCESS)||
                (tripRcv->header.opCode == CUSTOMER_HISTORY_AVAILABLE))
            {
                /*Server will response with the data. This will
                  only be true if the user has provided the right password*/
                printf("GET HISTORY  SUCCESS\n");
                printf(" email address %s phone number %s\n",
                       tripRcv->custInfo.customerEmail,
                       tripRcv->custInfo.customerPhone);
                /* you can print other data as well */
                /* at this momen the server resutnrs the credit card info
                   and does not return the last 5 trips- which will be added
                   to the end of ths command*/

            }
            if (tripRcv->header.opCode == CUSTOMER_AUTH_FAILURE)
            {
                printf("GET HISTORY  FAILURE\n");

            }
            break;

        case CUSTOMER_CHANGE_PASSWORD:
            tripSnt->header.opCode = CUSTOMER_CHANGE_PASSWORD;
            tripSnt->appType = CUSTOMER_APP;
            strcpy(tripSnt->custInfo.customerFirstName,
                   "Jaswinder");
            strcpy(tripSnt->custInfo.customerMiddleName,
                   "Singh");
            strcpy(tripSnt->custInfo.customerLastName,
                   "Sidhu");
            strcpy(tripSnt->custInfo.paswrd,
                   "JasSAN123");
            tripSnt->custInfo.changePaswrdFlag = CHANGE_PASSWORD_SET;
            strcpy(tripSnt->custInfo.newPasswrd,
                   "SINGH123");
            /* note you do not need to provide the email address
           /* now send out a message and get the response back */
            sendAndRcvMessage(socketId,msgSent,
                              msgRecv ,TAXIMSG_SIZE);
            /* now check the response from the server*/

            conprintf("OPCODE FROM SERVER %d\n",
                      tripRcv->header.opCode);
            if (tripRcv->header.opCode == PASSWORD_CHANGE_SUCCESS)
            {
                printf("Password changed successfully\n");
            }
            if (tripRcv->header.opCode == PASSWORD_CHANGE_FAILURE)
            {
                printf("Password changed failed\n");
            }

        case CUSTOMER_MESSAGE:

            break;


        default:
            conprintf("the command cannot be interpretted\n");
            break;
    }
}
/********************************************
*
* This function never returns it get the GPS coorindates from the server
*  and then prints ths th response to the screen. In real the android cutomer
*  App the cordinates received will be displayed on the map
*
********************************************/
void recvAsynGPSCord(int socketId)
{
    int numBytes;
    char msgBuff[TAXIMSG_SIZE];
    int count;
    TAXITRIP_t  *msgP;
    GPSCORD_t  *driverLocation;


    msgP=(TAXITRIP_t *)&msgBuff[0];
    driverLocation= (GPSCORD_t *) &msgP->driverGPS;
    count=0;
    while(1)
    {
        conprintf("waiting for Server. Message num %d\n",
                  count);
        numBytes = read(socketId,msgBuff,TAXIMSG_SIZE);
        conprintf("\n\nMESSAGE opCode %d\n",
                  msgP->header.opCode);
        conprintf("\n\n %d GPS Cordinates: long->%d:%d %c. Lat->%d:%d %c\n",
                  count,
                  driverLocation->longitudeDegrees,
                  driverLocation->longitudeMinutes,
                  driverLocation->NorS,
                  driverLocation->latitudeDegrees,
                  driverLocation->latitudeMinutes,
                  driverLocation->EorW
        );

        count++;
    }
}
/****************************************************************
* This is the main entyr point where the program starts execution.
*****************************************************************/
int main()
{
    unsigned char msgSent[TAXIMSG_SIZE]; /* buffer for sending meesage to server*/
    unsigned char msgRecv[TAXIMSG_SIZE]; /* buffer for sending meesage to server*/
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
/* this bottom codes is for compiling the GPS coordinate update thread*/
#ifdef GPS_BUILD
        /* send taxi request and then just recieve gps updates */
        processUserRequest(socketId,TAXI_REQUEST);
       recvAsynGPSCord(socketId);
#else
        /* This code sends different commands to the server*/
        /* send taxi request message */
        processUserRequest(socketId,TAXI_REQUEST);
        /* wait 15 seconds and then send TAXI ACCEPTED message */
        sleep(15);
        processUserRequest(socketId,TAXI_JOB_ACCEPTED);
        /* wait 15 seconds and then send TAXI REJECTED message */
        sleep(15);
        processUserRequest(socketId,TAXI_JOB_REJECTED);
        /* wait 15 seconds and then send GET CAB AVAILABILITY message */
        printf("Requesting server for Cab availability\n");
        processUserRequest(socketId,GET_CAB_AVAILABILITY);
        /* wait 15 seconds and then send REGISTER CUSTOMER MESSAGE */
        sleep(15);
        printf("Requesting server for Customer registration\n");
        processUserRequest(socketId,REGISTER_CUSTOMER);
        sleep(15);
        printf("Requesting server for Customer History\n");
        processUserRequest(socketId,GET_CUSTOMER_HISTORY);
        sleep(15);
        printf("Requesting server for Customer password change\n");
        processUserRequest(socketId,CUSTOMER_CHANGE_PASSWORD);

#endif
    }
    printf("out of client loop\n");
}
