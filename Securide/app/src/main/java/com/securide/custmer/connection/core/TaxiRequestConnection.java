package com.securide.custmer.connection.core;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by pradeep.kumar on 3/17/16.
 */
public class TaxiRequestConnection extends SecurideConnection {



    /**
     * Sends the taxi request across the server
     * @param licence
     * @param driverFirstName
     * @param driversSecondName
     * @param driverLastName
     * @param customerFirst
     * @param customerSecond
     * @param customerLast
     * @param destStreetName
     * @param destCityName
     * @param destLandMark
     * @param destBuildingNumber
     * @param destZipCode
     * @param pickupStreetName
     * @param pickupCityName
     * @param pickupLandMark
     * @param pickupBuildingNumber
     * @param pickupZipCode
     * @param gps
     * */
    public void sendTaxiRequest(String licence, String driverFirstName, String driversSecondName,
                                 String driverLastName, String customerFirst, String customerSecond, String customerLast,
                                 String destStreetName, String destCityName, String destLandMark,
                                 String destBuildingNumber, String destZipCode,
                                 String pickupStreetName, String pickupCityName,
                                 String pickupLandMark, String pickupBuildingNumber,
                                 String pickupZipCode, int gps[]) throws IOException {

        Socket socket = getSocket();
        int count = 0;
        int byteCount;
        int appType = 256;
        int taxiType = 3469;
        int driver = 325;

        OutputStream os = socket.getOutputStream();
        BufferedOutputStream out = new BufferedOutputStream(os);
        Log.d("SecurideConnection", "Going to SEND");

        try {
            out.write(ConnectionConstants.TAXI_REQUEST); /* TAXI_REQUIST opCode =1 */
            out.write(0);
            writeShort(appType, out);/* this write two bytes */
            writeShort(taxiType, out);
            writeShort(driver, out);
            count = 8;

            Log.d("SecurideConnection", "BEFORE CAB INFO count is:  " + count);
            byteCount = writeCabInfo(licence, driverFirstName, driversSecondName, driverLastName, out);
            count = count + byteCount;
            Log.d("SecurideConnection", "AFTER CAB info count is: " + count);
            byteCount = writeCustomerName(customerFirst, customerSecond, customerLast, out);
            count = count + byteCount;
            Log.d("SecurideConnection", "AFTER Customer name info count is: " + count);
            byteCount = writeCustomerDestination(destStreetName, destCityName, destLandMark, destBuildingNumber, destZipCode, out);
            count = count + byteCount;
            Log.d("SecurideConnection", " AFTER Customer Dest info count is: " + count);
            byteCount = writeCustomerPickupLocation(pickupStreetName,
                    pickupCityName, pickupLandMark, pickupBuildingNumber,
                    pickupZipCode, out);
            count = count + byteCount;
            Log.d("SecurideConnection", "AFTER Customer Pickup info count is: " + count);

			/*
             * the next 34 bytes are for the server's response. Just zero them
			 * out
			 */

            byteCount = 0;
            while (byteCount < 34) {
                out.write(84);
                byteCount = byteCount + 1;
            }
            count = count + byteCount - 1;
            Log.d("SecurideConnection", "count BEFORE GPS: " + count);
            byteCount = writeGPSCoordinates(gps, out);
            count = count + byteCount;
            Log.d("SecurideConnection", "count AFTER GPS: " + count);

            while (count < 772) {
                out.write(84);
                count = count + 1;
            }
            Log.d("SecurideConnection", "number of bytes: " + count);
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.flush();
    }


    private int writeCabInfo(String lis, String first, String second,
                             String last, BufferedOutputStream out) throws IOException {
        int countFirst;
        int countSecond;
        int countLast;
        int countLis;
        int totalCount;
        int numPads;

        countLis = writeString(lis, 20, out);
        totalCount = countLis;
        Log.d("SecurideConnection", "after license Count: " + totalCount);

        countFirst = writeString(first, 0, out);
        out.write(32); /* add space between first and middle name */
        totalCount = totalCount + countFirst + 1;
        Log.d("SecurideConnection", "after first NAME count: " + totalCount);

        countSecond = writeString(second, 0, out);
        out.write(32);
        totalCount = totalCount + countSecond + 1;
        Log.d("SecurideConnection", "after Second NAME count: " + totalCount);

        countLast = writeString(last, 0, out);
        numPads = writePadding(30 - (countFirst + countSecond + countLast + 2),
                out);
        totalCount = totalCount + countLast + numPads;
        Log.d("SecurideConnection", "numbr PaD NAME count: " + numPads);
        Log.d("SecurideConnection", "totalCounti at the END: " + totalCount);
        return (totalCount);
    }

    /**********************************************
     * This function write 90 bytes to the socket buffer
     **************************************************/
    private int writeCustomerName(String custFirst, String custSecond,
                                  String custLast, BufferedOutputStream out) throws IOException {
        int countFirst;
        int countSecond;
        int countLast;
        int totalCount;

        Log.d("SecurideConnection", "CUSTOMER NAME: " + custFirst + " " + custSecond
                + " " + custLast);
        countFirst = writeString(custFirst, 30, out);
        countSecond = writeString(custSecond, 30, out);
        countLast = writeString(custLast, 30, out);
        totalCount = countFirst + countSecond + countLast;
        return (totalCount);
    }

    /**********************************************************
     * This method write cutomer's destination 254 bytes to the socket buffer
     **********************************************************/
    private int writeCustomerDestination(String destStreetName,
                                         String destCityName, String destLandMark,
                                         String destBuildingNumber, String destZipCode,
                                         BufferedOutputStream out) throws IOException {
        int countStreetName;
        int countBuildingNumber;
        int countCityName;
        int countLandMark;
        int totalCount;
        int numBytes;

        countStreetName = writeString(destStreetName, 30, out);
        countBuildingNumber = writeString(destBuildingNumber, 12, out);
        countCityName = writeString(destCityName, 30, out);
        numBytes = writeString(destZipCode, 12, out);
        countLandMark = writeString(destLandMark, 200, out);
        totalCount = countStreetName + countBuildingNumber + countCityName;
        Log.d("SecurideConnection", "INSIDE writeDest (1)count is: " + totalCount);
        totalCount = totalCount + countLandMark + numBytes;
        Log.d("SecurideConnection", "INSIDE writeDest (2)count is: " + totalCount);
        Log.d("SecurideConnection", "INSIDE writeDest count is: " + totalCount
                + "other " + countStreetName + " " + countBuildingNumber + " "
                + countCityName + " " + countLandMark + " " + numBytes);
        return (totalCount);
    }


    /**********************************************************
     * This method write cutomer's pickup location 254 bytes to the socket
     * buffer
     **********************************************************/
    private int writeCustomerPickupLocation(String pickupStreetName,
                                            String pickupCity, String pickupLandMark,
                                            String pickupBuildingNumber, String pickupZipCode,
                                            BufferedOutputStream out) throws IOException {
        int totalNumBytes;
        int byteCount1;
        int byteCount2;
        int byteCount3;
        int byteCount4;
        int byteCount5;

        byteCount1 = writeString(pickupStreetName, 30, out);
        byteCount2 = writeString(pickupBuildingNumber, 12, out);
        byteCount3 = writeString(pickupCity, 30, out);
        byteCount4 = writeString(pickupZipCode, 12, out);
        byteCount5 = writeString(pickupLandMark, 200, out);
        totalNumBytes = byteCount1 + byteCount2 + byteCount3 + byteCount4 + byteCount5;
        return (totalNumBytes);
    }

    private int writeGPSCoordinates(int gps[], BufferedOutputStream out)
            throws IOException {
        byte[] outPut = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        byte[] convOut = {0, 0};
        int count;

		/* longitude degrees */
        convertShortToByte(gps[0], convOut);
        outPut[0] = convOut[0];
        outPut[1] = convOut[1];

		/* longitude minutes */
        convertShortToByte(gps[1], convOut);
        outPut[2] = convOut[0];
        outPut[3] = convOut[1];

		/* longitude North or South */
        convertShortToByte(gps[2], convOut);
        outPut[4] = convOut[0];
		/* it is only one byte so we do not have to copy the second byte */

		/* latitude degrees */
        convertShortToByte(gps[3], convOut);
        outPut[6] = convOut[0];
        outPut[7] = convOut[1];

		/* latitude minutes */
        convertShortToByte(gps[4], convOut);
        outPut[8] = convOut[0];
        outPut[9] = convOut[1];

		/* latitude East or West */
        convertShortToByte(gps[5], convOut);
        outPut[10] = convOut[0];
		/* it is only one byte so we do not have to copy the second byte */

		/* write it out to the socket buffer */
        count = 0;
        while (count < 12) {
            out.write(outPut[count]);
            count = count + 1;
        }
        return (count);
    }
}
