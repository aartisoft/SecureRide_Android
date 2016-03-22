/****************************************************************************
 * File Name    : clientsrvint.h
 * Description  : secket client interface for taxi 
 * Date         : Dec 20   2015 
 * Compiler     : gcc (linux) 
 ***************************************************************************/

/****************************************************************************
 *                        standard header files
 ***************************************************************************/
/* Note tht all the include files are not required to be included  for any 
Androd app. They are only required for "C" based linux compiling */

#include  <stdio.h>
#include  <sys/types.h>
#include  <sys/socket.h>
#include  <netinet/in.h>
#include  <pwd.h>
#include  <signal.h> 
#include  <sys/time.h>
#include  <netdb.h>
#include  <errno.h>

#define s32_t int
#define s16_t int
#define s8_t  char
#define u32_t unsigned long
#define u16_t unsigned short

extern int err;

/****************************************************************************
 *                      manifest constants (#defines)  
 ***************************************************************************/

#define MESSAGE_SIZE  200   
#define MAX_NAME_LENGTH 30  /* this is the maximum number of characters for the
                               name field. */ 
#define MAX_LICENSE_LENGTH 20
#define MAX_DESCRIPTION_LENGTH 200 
#define SIZE_OF_CREDIT_CARD_NUM 20 

#define CUSTOMER_APP       0
#define DRIVER_APP         1
#define SERVER_RESPONSE    2

/**********************************different taxi types supported */
#define LEMOSINE            1
#define REGULAR_CAB         2
#define SPECIAL_ASSIST_CAB   3
/* this is the data structure for sending and receiving GPS cordinates updates*/

struct gpsCord
{
unsigned short  longitudeDegrees;
unsigned short  longitudeMinutes;
unsigned char   NorS;
unsigned short  latitudeDegrees;
unsigned short  latitudeMinutes;      
unsigned char   EorW;
} ;

typedef struct gpsCord GPSCORD_t; 

struct taxiPayment
{
unsigned char paymentType;
unsigned short dollarAmount;
unsigned short centAmount;
}; 
typedef struct taxiPayment  TAXIPAYMENT_t; 
struct creditCardInfo 
{
unsigned char creditCardNumber[SIZE_OF_CREDIT_CARD_NUM];
unsigned char creditCardName[MAX_NAME_LENGTH]; 
unsigned char creditCardType; 
unsigned char expryMonth;
unsigned short expryYear;
unsigned short codeOnBack;
}; 
typedef struct creditCardInfo CREDIT_CARD_INFO_t; 
struct intrMessage
{
unsigned char intrMessage[MESSAGE_SIZE];
};
typedef struct intrMessage  INTRMESSAGE_t; 

struct tripTime
{
unsigned char estimatedHours;
unsigned char estimatedMinutes;
unsigned char estimatedSeconds; 
unsigned char actualdHours;
unsigned char actualMinutes;
unsigned char actualSeconds; 
short    estimatedTaxiArrivalTime; /* this is in mimutes */ 
}; 
typedef struct tripTime  TRIPTIME_t; 

struct tripAddress
{
unsigned char passangerFirstName[MAX_NAME_LENGTH];
unsigned char passangerMiddleName[MAX_NAME_LENGTH];
unsigned char passangerLastName[MAX_NAME_LENGTH];
unsigned char destStreetName[MAX_NAME_LENGTH];
unsigned short destBuildingNumber; 
unsigned char destCity[MAX_NAME_LENGTH]; 
int destZipCode; 
unsigned char destLandMarkDesc[MAX_DESCRIPTION_LENGTH]; 
unsigned char pickupStreetName[MAX_NAME_LENGTH];
unsigned short pickupBuildingNumber; 
unsigned char pickupCity[MAX_NAME_LENGTH]; 
int  pickupZipCode; 
unsigned char pickupLandMarkDesc[MAX_DESCRIPTION_LENGTH]; 
};
typedef struct tripAddress  TRIPADDRESS_t; 
/* protocol operation codes. This is the field the defines the message type or 
  command beteen the server and the client*/

/* operation codes used between the server and the customer app communications*/
#define TAXI_REQUEST                1 /* customer is requesting a taxi. With 
                                        this operation code the customer app
                                        requests the server for a taxi. 
                                        The server repsonses with the same
                                        operation code when responding to 
                                        the request */      
#define TAXI_JOB_ACCEPTED           2 /* customer App sends this operation
                                        code to the server once the customer 
                                        has accepted the cab. */ 
#define TAXI_JOB_REJECTED           3 /* customer App sends this operation
                                        code once the server once the customer
                                        cancels the taxi request */ 
#define GPS_POSITION_UPDATE         8 /* server sends this operation code to
                                        customer app when sending gps
                                        coordinates of the taxi to the customer
                                        app. The cusomter App and the driver
                                        app sends this operation code to the
                                        server when they are sending their
                                        location to the server. */ 
#define CUSTOMER_MESSAGE           12 /* Customer App can send a sepcial message                                         to the sever using this operation
                                         code. */ 
#define NO_DRIVER_AVAILABLE       22 /* there is no driver available. */
#define REGISTER_CUSTOMER         25 /* do the customer registration */
#define GET_CAB_AVAILABILITY      26  /* find out if a particular type 
                                        of cab is available in the area.*/
#define GET_CUSTOMER_PAYMENTINFO  27  /*get all the credit cards that customer
                                       has on file*/
#define GET_CUSTOMER_HISTORY      28 /* get the last 15 trips that customer
                                       took info */
#define SERVER_CONFIRMED           29 /* meessage sent by server confirming the
                                         cab accept and cab reject*/
#define CAB_AVAILABLE              30 /* this is response from the server for
                                         if the cab requested is available.*/
#define DIFFERENT_CAB_AVAILABLE    32 /* this is the response from the server
                                         when the type of cab requested is not
                                        available but a different type of cab 
                                        is available. In this cas the server
                                        returns the cab type in the taxiType
                                        field. */  
/* operation codes used between the server and the driver app communications.
   these opeartion codes are not required by the customer app*/ 

#define CREDIT_CARD_PAYMENT         4
#define CREDIT_CARD_ACCEPTED        5
#define CREDIT_CARD_REJECTED        6 
#define CASH_PAYMENT                7
#define ALERT_MESSAGE               9
#define DISPATCHER_MESSAGE         10
#define TAXI_DRIVER_MESSAGE        11
#define TAXI_IN_MAINTANCE          13
#define TAXI_BACK_IN_SERVICE       14
#define TAXI_TEMPORARY_OUT_SERVICE 15
#define USER_MESSAGE               16
#define FILE_TRX                   17  
#define STOP_TRX                   18 
#define FILE_TRX_COMPLETE          19 
#define CON_REJECTED               20  /* server rejected. max connectsion 
                                          that server
                                       can handle are reached. */
#define CUSTOMER_DROPOFF_SUCCESS   23
#define CUSTOMER_DROPOFF_FAIL      24

struct taxiHeader
{
unsigned char opCode;
} ; 
typedef struct taxiHeader TAXI_HEADER_t; 
 
/* this is the structure used to send and receive taxi trip message
   between the server and the app */
struct taxiTrip
{
TAXI_HEADER_t header;
unsigned short appType; /* application type*/
unsigned short taxiType;
char  taxiNumber[MAX_LICENSE_LENGTH];
unsigned short driverNumber;
unsigned char  driverName[MAX_NAME_LENGTH];
TRIPADDRESS_t tripAddress;
TRIPTIME_t    tripTime;
TRIPTIME_t    driverArrivalTime;
TAXIPAYMENT_t taxiPayment;
char estimatedCost[12];
GPSCORD_t customerGPS; 
GPSCORD_t driverGPS; 

}; 
typedef struct taxiTrip TAXITRIP_t;

struct customerRegister {
TAXI_HEADER_t header;
unsigned short appType; /* application type*/
char customerFirstName[MAX_NAME_LENGTH];
char customerMiddleName[MAX_NAME_LENGTH];
char customerLastName[MAX_NAME_LENGTH];
char customerEmail[MAX_NAME_LENGTH];
short  customerPhoneArea;
short  customerPart1PhoneNumber;
short  customerPart2PhoneNumber;
CREDIT_CARD_INFO_t     creditCard;
};
typedef struct customerRegister CUSTOMER_REG_t;

/* This is the structure used to send and receive the gps cordinates
   to and from the server */

struct taxiGPSUpdate
{
TAXI_HEADER_t header;
GPSCORD_t gpsUpdate; 
};
typedef struct taxiGPSUpdate GPSUPDATE_t;

struct paymentProcess
{
TAXI_HEADER_t header;
TAXIPAYMENT_t taxiPayment;
CREDIT_CARD_INFO_t creditCardInfo;
} ; 
typedef struct paymentProcess PAYMENT_PROCESS_t;

struct intermMessage
{
TAXI_HEADER_t header;
INTRMESSAGE_t message; 
}; 
typedef struct intermMessage INTERMMESSAGE_t;

struct taxiProto 
{
unsigned char opCode;
unsigned char fileName[13];
unsigned short numBytesTrx;
unsigned short payLoadSize;
};

typedef struct taxiProto TAXI_PROTO; 

#define PAYLOAD_SIZE  400
#define TAXIMSG_SIZE   sizeof(TAXITRIP_t)

