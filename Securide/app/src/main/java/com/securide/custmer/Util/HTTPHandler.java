package com.securide.custmer.Util;

/* Copyright (C)
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Kevin Irish Antonio <irish.antonio@yahoo.com>, February 2014
 */
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.securide.custmer.R;

public class HTTPHandler {

	public static HTTPHandler httpHandler = null;

	private HTTPHandler() {

	}

	public static HTTPHandler defaultHandler() {
		if (httpHandler == null) {
			httpHandler = new HTTPHandler();
		}
		return httpHandler;
	}

	// public JSONObject doPostOld(String URL, JSONObject jsonObjSend) {
	//
	// // KeyStore keyStore;
	//
	// InputStream is = null;
	// String json = "";
	// JSONObject jObj;
	// Log.i("WS-URL", URL);
	// Log.i("WS_PARAM", jsonObjSend.toString());
	//
	// try {
	//
	// // mPost = new HttpPost(ur);
	// // ByteArrayEntity baEntity = new
	// // ByteArrayEntity(json.toString().getBytes("UTF8"));
	// // baEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
	// // "application/json"));
	// // mPost.setEntity(baEntity);
	//
	// HttpClient httpclient = null;
	//
	// httpclient = new DefaultHttpClient();
	//
	// HttpPost httpPostRequest = new HttpPost(URL);
	//
	// ByteArrayEntity bae = new ByteArrayEntity(jsonObjSend.toString()
	// .getBytes("UTF8"));
	// bae.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
	// "application/json"));
	//
	// // StringEntity se;
	// // se = new StringEntity(jsonObjSend.toString());
	//
	// // Set HTTP parameters
	//
	// httpPostRequest.addHeader("Accept", "application/json");
	// httpPostRequest.setEntity(bae);
	// HttpResponse response = (HttpResponse) httpclient
	// .execute(httpPostRequest);
	//
	// // Get hold of the response entity (-> the data):
	// HttpEntity entity = response.getEntity();
	//
	// is = entity.getContent();
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// try {
	// BufferedReader reader = new BufferedReader(new InputStreamReader(
	// is, "iso-8859-1"), 8);
	// StringBuilder sb = new StringBuilder();
	// String line = null;
	// while ((line = reader.readLine()) != null) {
	// sb.append(line + "\n");
	// }
	// is.close();
	// json = sb.toString();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// try {
	// jObj = new JSONObject(json);
	// Log.i("WS RESPONSE", json);
	// return jObj;
	//
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	//
	// return null;
	// }

	public JSONObject doPost(String URL, JSONObject jsonObjSend) {

		System.out.printf("Url = ",URL);
		System.out.printf("input = ",jsonObjSend);
		
		try {
			URL object = new URL(URL);

			HttpURLConnection con = (HttpURLConnection) object.openConnection();

			con.setDoOutput(true);

			con.setDoInput(true);

			con.setInstanceFollowRedirects(false);

			con.setRequestProperty("Content-Type", "application/json");

			con.setRequestProperty("Accept", "application/json");

			con.setRequestMethod("POST");

			con.setUseCaches(false);

			con.connect();

			OutputStreamWriter wr = new OutputStreamWriter(
					con.getOutputStream());

			wr.write(jsonObjSend.toString());

			wr.flush();

			// display what returns the POST request

			StringBuilder sb = new StringBuilder();

			int HttpResult = con.getResponseCode();

			if (HttpResult == HttpURLConnection.HTTP_OK) {

				BufferedReader br = new BufferedReader(new InputStreamReader(
						con.getInputStream(), "utf-8"));

				String line = null;

				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}

				br.close();

				Log.i("return", "" + sb.toString());
				JSONObject obj = new JSONObject("" + sb.toString());
				return obj;

			} else {
				JSONObject obj = new JSONObject(con.getResponseMessage());
				return obj;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public JSONObject doGet(String url, List<NameValuePair> params) {

		InputStream is = null;
		JSONObject jObj = null;
		String json = "";

		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			if (params != null) {
				String paramString = URLEncodedUtils.format(params, "utf-8");
				url += "?" + paramString;
			}

			HttpGet httpGet = new HttpGet(url);
			Log.i("URL REQUEST - GET", url);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		return jObj;

	}

	public JSONObject doMultipart(String urlTo, String filefield,
			String filepath, String postQuery) throws ParseException,
			IOException {

		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		InputStream inputStream = null;

		String twoHyphens = "--";
		String boundary = "*****" + Long.toString(System.currentTimeMillis())
				+ "*****";
		String lineEnd = "\r\n";

		String result = "";

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;

		String[] q = filepath.split("/");
		int idx = q.length - 1;

		try {
			File file = new File(filepath);
			FileInputStream fileInputStream = new FileInputStream(file);

			URL url = new URL(urlTo);
			connection = (HttpURLConnection) url.openConnection();

			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("User-Agent",
					"Android Multipart HTTP Client 1.0");
			connection.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + boundary);

			outputStream = new DataOutputStream(connection.getOutputStream());
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			outputStream.writeBytes("Content-Disposition: form-data; name=\""
					+ filefield + "\"; filename=\"" + q[idx] + "\"" + lineEnd);
			outputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);
			outputStream.writeBytes("Content-Transfer-Encoding: binary"
					+ lineEnd);
			outputStream.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			while (bytesRead > 0) {
				outputStream.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			outputStream.writeBytes(lineEnd);

			// Upload POST Data
			String[] posts = postQuery.split("&amp;");
			System.out.println("Post value are: " + postQuery);
			int max = posts.length;
			for (int i = 0; i < max; i++) {
				outputStream.writeBytes(twoHyphens + boundary + lineEnd);
				String[] kv = posts[i].split("=");
				outputStream
						.writeBytes("Content-Disposition: form-data; name=\""
								+ kv[0] + "\"" + lineEnd);
				outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
				outputStream.writeBytes(lineEnd);
				outputStream.writeBytes(kv[1]);
				outputStream.writeBytes(lineEnd);
			}

			outputStream.writeBytes(twoHyphens + boundary + twoHyphens
					+ lineEnd);

			inputStream = connection.getInputStream();
			result = this.convertStreamToString(inputStream);

			fileInputStream.close();
			inputStream.close();
			outputStream.flush();
			outputStream.close();

			JSONObject obj = new JSONObject(result);
			return obj;
		} catch (Exception e) {
			Log.e("MultipartRequest", "Multipart Form Upload Error");
			e.printStackTrace();
			return null;
		}
	}

	private String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public class MyHttpClient extends DefaultHttpClient {
		final KeyStore store;

		public MyHttpClient(KeyStore store) {
			this.store = store;
		}

		@Override
		protected ClientConnectionManager createClientConnectionManager() {
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", newSslSocketFactory(), 443));
			return new SingleClientConnManager(getParams(), registry);
		}

		private SSLSocketFactory newSslSocketFactory() {
			try {
				return new SSLSocketFactory(store);
			} catch (Exception e) {
				throw new AssertionError(e);
			}
		}

	}

	public boolean isConnectingToInternet(Context context){
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null)
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED)
					{
						return true;
					}

		}
		return false;
	}
	public  void showDialog(Context context,String message){
		final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setMessage(message);
		dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				paramDialogInterface.dismiss();
			}
		});

		dialog.show();
	}
}
