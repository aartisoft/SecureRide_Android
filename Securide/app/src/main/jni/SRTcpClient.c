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

    s = socket(AF_INET,SOCK_STREAM,0);
    if(s < 0)
    {
        conprintf("I cannot get a socket on Client\n");
    }
    sockIn.sin_family = AF_INET;
    sockIn.sin_addr.s_addr = serverIpAddr;
    sockIn.sin_port = portNumber;
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
                conprintf("connect error: errno %d\n", errno);
                close(s);
                rtFd=-1;
            break;
	    }
     }
     else
     {
         conprintf("create socket. RetCode %d\n", retCode);
     }
    if (rtFd != -1)
    {
        rtFd=s;
    }
    return(rtFd);
}
/*****************************************************
* setupTcpSocket()
*
*****************************************************/
int setupTcpSocket(int portNum, unsigned long servIPaddr)
{
    int tcpFd;

    tcpFd= createTcpSocket(portNum, servIPaddr);
    if(tcpFd < 0)
    {
      conprintf("CANNOT CREATE TCP socket. port %d\n", portNum);
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
 void sendAndRcvMessage(int socketId, unsigned  char *msgSent, unsigned  char *msgRecv , short msgSize)
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
            conprintf("waiting for Server\n");
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
TAXITRIP_t processUserRequest(int socketId, const char* firstName, const char* middleName, const char* lastName, const char* pickupStretName, const short pickupBuildingNo, const char* pickupCity, const char* pickupLandmark, int pickupPinCode, const char* dropStreetName, const short dropBuildingNo, const char* dropCity, const char* dropLandmark, int dropPinCode, unsigned short pickupLat, unsigned short pickupLong, unsigned short dropLat, unsigned short dropLong, unsigned short cabType)
{
    unsigned char msgSent[TAXIMSG_SIZE]; /* buffer for sending meesage to server*/
    unsigned char msgRecv[TAXIMSG_SIZE];/*buffer to receiving meesage from server*/
    TAXITRIP_t * tripSnt; /* pointer to the meesage sending buffer */
    TAXITRIP_t  *tripRcv;  /* pointer to the meesage receive buffer */

    tripSnt = (TAXITRIP_t *)&msgSent;
    tripRcv = (TAXITRIP_t *)&msgRecv;
    //tripSnt->tripAddress.passangerFirstName = (unsigned char *)malloc(MAX_NAME_LENGTH);

    tripSnt->header.opCode = TAXI_REQUEST;
    tripSnt->driverNumber = 0; /*will be filled in by server and
                                 reply back*/
    tripSnt->appType = CUSTOMER_APP;
    tripSnt->taxiType = cabType;

    /* passanger information*/
    strcpy(tripSnt->tripAddress.passangerFirstName, firstName);
    strcpy(tripSnt->tripAddress.passangerLastName,lastName);
    strcpy(tripSnt->tripAddress.passangerMiddleName,middleName);

    /* where the passangeris going*/
    strcpy(tripSnt->tripAddress.destStreetName,dropStreetName);
    tripSnt->tripAddress.destBuildingNumber=dropBuildingNo;
    strcpy(tripSnt->tripAddress.destCity,dropCity);
    tripSnt->tripAddress.destZipCode=dropPinCode;
    strcpy(tripSnt->tripAddress.destLandMarkDesc, dropLandmark);

    /* where the passanger is beng picked up*/
    strcpy(tripSnt->tripAddress.pickupStreetName,pickupStretName);
    tripSnt->tripAddress.pickupBuildingNumber=pickupBuildingNo;
    strcpy(tripSnt->tripAddress.pickupCity,pickupCity);
    tripSnt->tripAddress.pickupZipCode=pickupPinCode;
    strcpy(tripSnt->tripAddress.pickupLandMarkDesc, pickupLandmark);


    /* Now setup the GPS cordinates of where the customer is */
    tripSnt->customerGPS.latitudeDegrees = pickupLat;
    tripSnt->customerGPS.latitudeMinutes= pickupLong;
    tripSnt->customerGPS.NorS = 'N';
    tripSnt->customerGPS.longitudeDegrees= dropLat;
    tripSnt->customerGPS.longitudeMinutes= dropLong;
    tripSnt->customerGPS.EorW = 'E';

    /*Now this function will send the message to the server and wait
    for response from the server. It will send the message content
    that contained in the msgSent buffer and when the response from
    the server is received this function will copy it to the msgrecv
    buffer.
    This function sendAndRcvMessage does not retunr until the
    message is received from the server */

    sendAndRcvMessage(socketId,msgSent,msgRecv ,TAXIMSG_SIZE);

    /* print the content of the message received */
    conprintf("\n\nSERVER RESPONSE: opCode %d\n",
     tripRcv->header.opCode);
    conprintf("taxi number:-> %s\n",tripRcv->taxiNumber);
    conprintf("driver number:-> %d\n",tripRcv->driverNumber);
    conprintf("taxi Driver name: %s\n",tripRcv->driverName);
    conprintf("taxi Driver name: %s\n",tripRcv->tripAddress.passangerFirstName);

    conprintf("estimated trip time: %d hour %d minutes\n",
            tripRcv->tripTime.estimatedHours,
    tripRcv->tripTime.estimatedMinutes);
    conprintf("estimated trip Cost: %s \n",
    tripRcv->estimatedCost);
    conprintf("estimated driver Arrival time: %d hour %d minutes\n",
            tripRcv->driverArrivalTime.estimatedHours,
    tripRcv->driverArrivalTime.estimatedMinutes);

    return *tripRcv;
}

TAXITRIP_t checkForAvailableCab(int socketId, int taxiType, const char* pickupStretName, const short pickupBuildingNo, const char* pickupCity, const char* pickupLandmark, int pickupPinCode, unsigned short pickupLat, unsigned short pickupLong, unsigned short dropLat, unsigned short dropLong)
{
    unsigned char msgSent[TAXIMSG_SIZE]; /* buffer for sending meesage to server*/
    unsigned char msgRecv[TAXIMSG_SIZE];/*buffer to receiving meesage from server*/
    TAXITRIP_t * tripSnt; /* pointer to the meesage sending buffer */
    TAXITRIP_t  *tripRcv;  /* pointer to the meesage receive buffer */

    tripSnt = (TAXITRIP_t *)&msgSent;
    tripRcv = (TAXITRIP_t *)&msgRecv;
    tripSnt->header.opCode = GET_CAB_AVAILABILITY;
    tripSnt->appType = CUSTOMER_APP;
    tripSnt->taxiType = taxiType;/*options here are LEMOSINE, REGULAR_CAB, or SPECIAL_ASSIST_CAB */

    /* where the passanger is beng picked up*/
     strcpy(tripSnt->tripAddress.pickupStreetName,pickupStretName);
     tripSnt->tripAddress.pickupBuildingNumber=pickupBuildingNo;
     strcpy(tripSnt->tripAddress.pickupCity,pickupCity);
     tripSnt->tripAddress.pickupZipCode=pickupPinCode;
     strcpy(tripSnt->tripAddress.pickupLandMarkDesc, pickupLandmark);

    /* Now setup the GPS cordinates of where the customer is */
    tripSnt->customerGPS.latitudeDegrees= pickupLat;
    tripSnt->customerGPS.latitudeMinutes= pickupLong;
    tripSnt->customerGPS.NorS = 'N';
    tripSnt->customerGPS.longitudeDegrees= dropLat;
    tripSnt->customerGPS.longitudeMinutes= dropLong;
    tripSnt->customerGPS.EorW = 'E';
    //  tripSnt->estimatedCost = (char *)malloc(6 * sizeof(char));
    /* other parameters are NOT required*/
    /* now send out a message and get the response back */
    sendAndRcvMessage(socketId,msgSent,msgRecv ,TAXIMSG_SIZE);
    /* check the  response from the server. There will be three
     options of response: cab is available, a different cab type
     available, cab not available. The corresponding opCodes
     returned are; CAB_AVAILABLE,DIFFERENT_CAB_AVAILABLE,
     NO_DRIVER_VAILABLE repsectivly. In the case
     different type of cab is available, the cab type
     that is available is returned in the taxiType
     field */

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
    return *tripRcv;
}

CUSTOMER_REG_t processSaveUserInfoRequest(int socketId, const char* firstName, const char* middleName, const char* lastName, const char* emailId, short phoneArea,  short phoneNo1,  short phoneNo2)
{
    unsigned char msgSent[TAXIMSG_SIZE]; /* buffer for sending meesage to server*/
    unsigned char msgRecv[TAXIMSG_SIZE];/*buffer to receiving meesage from server*/
    CUSTOMER_REG_t *customerRegSnt;
    CUSTOMER_REG_t *customerRegRcv;
    customerRegSnt=(CUSTOMER_REG_t *)&msgSent;
    customerRegRcv=(CUSTOMER_REG_t *)&msgRecv;
    customerRegSnt->header.opCode = REGISTER_CUSTOMER;
    customerRegSnt->appType = CUSTOMER_APP;
    strcpy(customerRegSnt->customerFirstName,firstName);
    strcpy(customerRegSnt->customerMiddleName, middleName);
    strcpy(customerRegSnt->customerLastName, lastName);
    strcpy(customerRegSnt->customerEmail, emailId);
    /* follwing three fields contain a 10 digit phone number.
     the first three are contained in the area code and the next
     three digits are contained on the part1PhoneNumber and
     the remaining digits are contained in Part2PhoneNumber.
     in the follwoing example the full phone number 6063924220*/
    customerRegSnt->customerPhoneArea= phoneArea;
    customerRegSnt->customerPart1PhoneNumber= phoneNo1;
    customerRegSnt->customerPart2PhoneNumber= phoneNo2;
    sendAndRcvMessage(socketId,msgSent,msgRecv,TAXIMSG_SIZE);
    /* now print and check we got the confirmation from Server*/
    if(customerRegRcv->header.opCode== SERVER_CONFIRMED)
    {
        conprintf("Server has confirmed the registeration\n");
    }
    else
    {
        conprintf("register customer: %d\n",
                  customerRegRcv->header.opCode);
    }

    return *customerRegRcv;
}

TAXITRIP_t processUserAcceptOrRejectCabRequest(int socketId, unsigned short taxiType, int requestType, const char* taxiNumber, const char* driverName, unsigned short driverNumber)
{
    unsigned char msgSent[TAXIMSG_SIZE]; /* buffer for sending meesage to server*/
    unsigned char msgRecv[TAXIMSG_SIZE];/*buffer to receiving meesage from server*/
    TAXITRIP_t * tripSnt; /* pointer to the meesage sending buffer */
    TAXITRIP_t  *tripRcv;  /* pointer to the meesage receive buffer */

    tripSnt = (TAXITRIP_t *)&msgSent;
    tripRcv = (TAXITRIP_t *)&msgRecv;

    tripSnt->header.opCode = requestType;
    tripSnt->appType = CUSTOMER_APP;
    tripSnt->taxiType = taxiType;/*options here are LEMOSINE, REGULAR_CAB, or SPECIAL_ASSIST_CAB */

    strcpy(tripSnt->taxiNumber,taxiNumber);
   strcpy(tripSnt->driverName, driverName);
    tripSnt->driverNumber=driverNumber;
    /* other fields are not required. */
    /* now send out a message and get the response back */
    sendAndRcvMessage(socketId,msgSent,msgRecv ,TAXIMSG_SIZE);
    /* check the received response is confirmation */

    if (tripRcv->header.opCode == SERVER_CONFIRMED)
    {
        conprintf("Server has confirmed the taxi accept\n");
    }
    else
    {
        conprintf("Server has sent opcode %d for the taxi accept\n",
                  tripRcv->header.opCode);
    }
    return *tripRcv;
}

/********************************************
*
* This function never returns it get the GPS coorindates from the server
*  and then prints ths th response to the screen. In real the android cutomer
*  App the cordinates received will be displayed on the map
*
********************************************/
GPSCORD_t fetchDriverLocationDetails(int socketId)
{
int numBytes;
char msgBuff[TAXIMSG_SIZE];
int count;
TAXITRIP_t  *msgP;
GPSCORD_t  *driverLocation;

      msgP=(TAXITRIP_t *)&msgBuff[0];
      driverLocation= (GPSCORD_t *) &msgP->driverGPS;
      count=0;
      while(count < 2)
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
    return *driverLocation;
}
/****************************************************************
* This is the main entyr point where the program starts execution.
*****************************************************************/
int main()
{
unsigned char msgSent[TAXIMSG_SIZE]; /* buffer for sending meesage to server*/
unsigned char msgRecv[TAXIMSG_SIZE]; /*buffer to receiving meesage from server*/;
unsigned long serverAddr; /* ip address of the server */
 static int socketId;
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

    if (socketId > 0) {
        return socketId;
    }
   /* setu the tcp socket and get the handle (socket discriptor) */
   socketId = setupTcpSocket(portNum,serverAddr);
   if (socketId < 0)
   {
      conprintf("CANNOT SETUP TECP SOCKET\n");
   }
   else
   {
       return socketId;
/* this bottom codes is for compiling the GPS coordinate update thread*/
#ifdef GPS_BUILD
      /* send taxi request and then just recieve gps updates */
      processUserRequest(socketId,TAXI_REQUEST);
     recvAsynGPSCord(socketId);
//#else
//   /* This code sends different commands to the server*/
//      /* send taxi request message */
      processUserRequest(socketId,TAXI_REQUEST);
//      /* wait 20 seconds and then send TAXI ACCEPTED message */
      sleep(20);
      processUserRequest(socketId,TAXI_JOB_ACCEPTED);
      /* wait 20 seconds and then send TAXI REJECTED message */
      sleep(20);
      processUserRequest(socketId,TAXI_JOB_REJECTED);
      /* wait 20 seconds and then send GET CAB AVAILABILITY message */
      sleep(20);
      processUserRequest(socketId,GET_CAB_AVAILABILITY);
      /* wait 20 seconds and then send REGISTER CUSTOMER MESSAGE */
      sleep(20);
      processUserRequest(socketId,REGISTER_CUSTOMER);

#endif
   }
     printf("out of client loop\n");
    return socketId;
}

