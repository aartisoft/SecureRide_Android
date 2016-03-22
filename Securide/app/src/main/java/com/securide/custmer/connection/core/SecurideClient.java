package com.securide.custmer.connection.core;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class SecurideClient {
	public static void main() throws IOException {
		Log.d("SecurideConnection","Hello, World.");
		ClientSocket soc = new ClientSocket();
		Log.d("SecurideConnection","Going to connect");
		soc.conServer();
		Log.d("SecurideConnection","End of Hello World");
	}
}

class ClientSocket {
	String ip = "50.255.26.100";
	int port = 22;

	public void conServer() throws IOException {
		Socket s = null;
		int gps[] = { 10, 180, 'N', 90, 60, 'E' };
		String lis = "DL156";
		String first = "Rajvir";
		String second = "Singh";
		String last = "Sangha";
		String custFirst = "John";
		String custSecond = "Singh";
		String custLast = "Ludo";
		String destStreetName = "Alum Rock";
		String destCityName = "San Jose";
		String destBuildingNumber = "2345A";
		String destZipCode = "95135";
		String destLandMark = "Next to AutoZone Parts Store";
		String pickupStreetName = "Capical Expressway";
		String pickupCityName = "San Jose";
		String pickupBuildingNumber = "2556B";
		String pickupZipCode = "95134";
		String pickupLandMark = "Next to Eastrage Mall";
		int driverGPS[] = { 0, 0, 0, 0, 0, 0, 0, 0 };
		byte opCode = 0;
		int taxiType = 0;
		int driverNumber = 0;
		String taxiNumber = "D20";
		String driverName = "jack";
		byte driverArivalTime = 0;
		byte[] tripTime = { 0, 0, 0, 0 };
		String cost = "$20.10";

		try {
			Log.d("SecurideConnection","CONNECTED TO SOCKET");
			s = new Socket(ip, port);
		} catch (UnknownHostException e) {
			System.err.println("connection failed");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException ioe) {
			System.err.println("connection failed");
			ioe.printStackTrace();
			System.exit(1);
		}
		Log.d("SecurideConnection","Connected to host");

		binaryOut(s, lis, first, second, last, custFirst, custSecond, custLast,
				destStreetName, destCityName, destLandMark, destBuildingNumber,
				destZipCode, pickupStreetName, pickupCityName, pickupLandMark,
				pickupBuildingNumber, pickupZipCode, gps);

		Log.d("SecurideConnection","Going to Read the Socket");

		readServer(s, opCode, taxiType, driverNumber, taxiNumber, driverName,
				tripTime, driverArivalTime, cost, driverGPS);
		printServer(opCode, taxiType, driverNumber, taxiNumber, driverName,
				tripTime, driverArivalTime, cost, driverGPS);

		s.close();
	}

	public void sendMessage(Socket s, byte[] message) throws IOException {
		OutputStream os = s.getOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(message);
		oos.flush();
	}

	public void sendByteMessage(Socket s, byte[] mess2, int start, int len)
			throws IOException {
		if (len < 0) {
			throw new IllegalArgumentException("Negative length not allowed");
		}
		if ((start < 0) || (start >= mess2.length)) {
			throw new IndexOutOfBoundsException("Out of bound" + start);
		}
		Log.d("SecurideConnection","start value: " + start + "Length value:" + len);
		OutputStream out = s.getOutputStream();
		DataOutputStream dos = new DataOutputStream(out);
		dos.writeInt(len);
		if (len > 0) {
			dos.write(mess2, start, len);
		}
	}

	public void sendTest(Socket s) throws IOException {
		short test2 = 878;
		OutputStream os = s.getOutputStream();
		BufferedOutputStream out = new BufferedOutputStream(os);
		Log.d("SecurideConnection","number of bytes");

		try {
			out.write(test2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.flush();
	}

	public void readServer(Socket s, byte opCode, int taxiType,
			int driverNumber, String taxiNumber, String driverName,
			byte[] tripTime, byte driverArivalTime, String cost,
			int driverGPS[]) throws IOException {
		byte[] inBuff = new byte[1024];
		int numBytes;
		int count = 0;
		byte[] tmpBuff = { 0, 0, 0, 0 };
		int byteCount = 0;
		byte[] tmpByte = new byte[300];
		byte[] tmpByte2 = new byte[300];
		byte[] tmpByte3 = new byte[40];
		InputStream in = s.getInputStream();
		Log.d("SecurideConnection"," going to READ SOCKET");
		try {
			numBytes = in.read(inBuff);
			Log.d("SecurideConnection"," I have read : " + numBytes
					+ " bytes from socket");

			opCode = inBuff[0];
			taxiType = inBuff[4];
			tmpBuff[0] = inBuff[6];
			tmpBuff[1] = inBuff[7];
			driverNumber = inBuff[6];
			/*
			 * convertBytesToInt(tmpBuff,1,driverNumber);
			 */

			count = 0;
			while (count < 20) {
				tmpByte[count] = inBuff[8 + count];
				count = count + 1;
			}
			taxiNumber = new String(tmpByte);
			Log.d("SecurideConnection","TAXI LICENSE: " + taxiNumber);
			count = 0;
			while (count < 30) {
				tmpByte2[count] = inBuff[28 + count];
				count = count + 1;
			}
			driverName = new String(tmpByte2);
			Log.d("SecurideConnection","Driver Name: " + driverName);

			byteCount = 28 + 30 + 658;
			;
			count = 0;
			while (count < 3) {
				tripTime[count] = inBuff[byteCount + count];
				count = count + 1;
			}
			byteCount = byteCount + count + 3;
			driverArivalTime = inBuff[byteCount];
			Log.d("SecurideConnection","driver Arvial time: " + driverArivalTime);
			byteCount = byteCount + count + 2 + 8 + 3;
			count = 0;
			while (count < 12) {
				tmpByte3[count] = inBuff[byteCount + count];
				count = count + 1;
			}
			cost = new String(tmpByte3);
			Log.d("SecurideConnection"," trip cost: " + cost);
			byteCount = byteCount + 12 + 12;
			driverGPS[0] = inBuff[byteCount + 0];
			driverGPS[1] = inBuff[byteCount + 2];
			driverGPS[2] = inBuff[byteCount + 4];
			driverGPS[3] = inBuff[byteCount + 6];
			driverGPS[4] = inBuff[byteCount + 8];
			driverGPS[5] = inBuff[byteCount + 10];
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void printServer(byte opCode, int taxiType, int driverNumber,
			String taxiNumber, String driverName, byte[] tripTime,
			byte driverArivalTime, String cost, int driverGPS[])
			throws IOException {
		Log.d("SecurideConnection","-----------------IN PRINT SERVER------------");
		Log.d("SecurideConnection","opCode " + opCode + " taxiType " + taxiType
				+ " drv " + driverNumber);
		Log.d("SecurideConnection","TAXI LICENSE: " + taxiNumber);
		Log.d("SecurideConnection","Driver Name: " + driverName);
		Log.d("SecurideConnection","tripTime: " + tripTime[0] + " " + tripTime[1]);
		Log.d("SecurideConnection","tripTime: " + tripTime[2]);
		Log.d("SecurideConnection","driver Arvial time: " + driverArivalTime);
		Log.d("SecurideConnection"," cost: " + cost);
		Log.d("SecurideConnection","driver GPS Long: " + driverGPS[0] + ":"
				+ driverGPS[1] + " " + driverGPS[2]);
		Log.d("SecurideConnection","driver GPS Lat: " + driverGPS[3] + ":"
				+ driverGPS[4] + " " + driverGPS[5]);

	}

	public void binaryOut(Socket s, String lis, String first, String second,
			String last, String custFirst, String custSecond, String custLast,
			String destStreetName, String destCityName, String destLandMark,
			String destBuildingNumber, String destZipCode,
			String pickupStreetName, String pickupCityName,
			String pickupLandMark, String pickupBuildingNumber,
			String pickupZipCode, int gps[]) throws IOException {
		int count = 0;
		int byteCount;
		int appType = 256;
		int taxiType = 3469;
		int driver = 325;

		OutputStream os = s.getOutputStream();
		BufferedOutputStream out = new BufferedOutputStream(os);
		Log.d("SecurideConnection","Going to SEND");
		try {
			out.write(1); /* TAXI_REQUIST opCode =1 */
			out.write(0);
			writeShort(appType, out);/* this write two bytes */
			writeShort(taxiType, out);
			writeShort(driver, out);
			count = 8;

			Log.d("SecurideConnection","BEFORE CAB INFO count is:  " + count);
			byteCount = writeCabInfo(lis, first, second, last, out);
			count = count + byteCount;
			Log.d("SecurideConnection","AFTER CAB info count is: " + count);
			byteCount = writeCustomerName(custFirst, custSecond, custLast, out);
			count = count + byteCount;
			Log.d("SecurideConnection","AFTER Customer name info count is: " + count);
			byteCount = writeCustomerDestination(destStreetName, destCityName,
					destLandMark, destBuildingNumber, destZipCode, out);
			count = count + byteCount;
			Log.d("SecurideConnection"," AFTER Customer Dest info count is: " + count);
			byteCount = writeCustomerPickupLocation(pickupStreetName,
					pickupCityName, pickupLandMark, pickupBuildingNumber,
					pickupZipCode, out);
			count = count + byteCount;
			Log.d("SecurideConnection","AFTER Customer Pickup info count is: " + count);

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
			Log.d("SecurideConnection","count BEFORE GPS: " + count);
			byteCount = writeGPS(gps, out);
			count = count + byteCount;
			Log.d("SecurideConnection","count AFTER GPS: " + count);

			while (count < 772) {
				out.write(84);
				count = count + 1;
			}
			Log.d("SecurideConnection","number of bytes: " + count);
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.flush();
	}

	private int writePadding(int numPads, BufferedOutputStream out)
			throws IOException {
		int padCount = 0;
		while (padCount < numPads) {
			out.write(0);
			padCount = padCount + 1;
		}
		return (padCount);
	}

	private int writeString(String str, int strLength, BufferedOutputStream out)
			throws IOException {
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

	private void convertShortToByte(int inPut, byte[] outPut) {
		outPut[0] = (byte) (inPut);
		outPut[1] = (byte) (inPut >> 8);
		Log.d("SecurideConnection"," inPut " + inPut);
		Log.d("SecurideConnection"," byte0 " + outPut[0] + " byte1 " + outPut[1]);
	}

	private int writeGPS(int gps[], BufferedOutputStream out)
			throws IOException {
		byte[] outPut = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		byte[] convOut = { 0, 0 };
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

	private int writeShort(int inPut, BufferedOutputStream out)
			throws IOException {
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
		Log.d("SecurideConnection","after license Count: " + totalCount);

		countFirst = writeString(first, 0, out);
		out.write(32); /* add space between first and middle name */
		totalCount = totalCount + countFirst + 1;
		Log.d("SecurideConnection","after first NAME count: " + totalCount);

		countSecond = writeString(second, 0, out);
		out.write(32);
		totalCount = totalCount + countSecond + 1;
		Log.d("SecurideConnection","after Second NAME count: " + totalCount);

		countLast = writeString(last, 0, out);
		numPads = writePadding(30 - (countFirst + countSecond + countLast + 2),
				out);
		totalCount = totalCount + countLast + numPads;
		Log.d("SecurideConnection","numbr PaD NAME count: " + numPads);
		Log.d("SecurideConnection","totalCounti at the END: " + totalCount);
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

		Log.d("SecurideConnection","CUSTOMER NAME: " + custFirst + " " + custSecond
				+ " " + custLast);
		countFirst = writeString(custFirst, 30, out);
		countSecond = writeString(custSecond, 30, out);
		countLast = writeString(custLast, 30, out);
		totalCount = countFirst + countSecond + countLast;
		return (totalCount);
	}

	/**********************************************************
	 * This method write cutomer's destination 254 bytes to the socket buffer
	 *
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
		Log.d("SecurideConnection","INSIDE writeDest (1)count is: " + totalCount);
		totalCount = totalCount + countLandMark + numBytes;
		Log.d("SecurideConnection","INSIDE writeDest (2)count is: " + totalCount);
		Log.d("SecurideConnection","INSIDE writeDest count is: " + totalCount
				+ "other " + countStreetName + " " + countBuildingNumber + " "
				+ countCityName + " " + countLandMark + " " + numBytes);
		return (totalCount);
	}

	/**********************************************************
	 * This method write customer's pickup location 254 bytes to the socket
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
		totalNumBytes = byteCount1 + byteCount2 + byteCount3 + byteCount4
				+ byteCount5;
		return (totalNumBytes);
	}
}
