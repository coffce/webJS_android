package com.imatlas.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Utils {
	
	/**
	 * @param urlStr 从网站后台获取订单号url访问地址,
	 * 此部分需要放在子线程当中执行，可能会有延时，
	 * @return orderId (int) 返回订单号
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws JSONException 
	 * @throws InterruptedException 
	 */
	public static int getOrder(String urlStr) throws ClientProtocolException, IOException, JSONException, InterruptedException{
//		String data=getStringFromUrl(urlStr);
//		if(data.equals("")){
//			//如果取到的数据为空，则休眠200ms后重试，第一次重试
//			Thread.sleep(200);
//			data=getStringFromUrl(urlStr);
//			if(data.equals("")){
//				//第二次重试
//				Thread.sleep(200);
//				data=getStringFromUrl(urlStr);
//			}
//			//再次检测其取回的值是否为空，为空的话，返回0，则表示获取订单失败
//			if(data.equals(""))
//				return 0;
//		}
//		JSONObject jobject=new JSONObject(data);
//		int orderId=jobject.getInt("orderId");
//		return orderId;
		return 10001;
	}
	
	
	/**
	 * 从网上获取内容get方式,其返回的为json字符串
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String getStringFromUrl(String url)	throws ClientProtocolException, IOException {
//		Log.i("TAG","url="+url);
		String rev = "";
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			// 添加http头信息
			HttpResponse response = httpclient.execute(get);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
			BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
				String line = null;
				while ((line = br.readLine()) != null) {
					rev += line;
				}
			}else{
				return rev;
			}
		} catch (Exception e) {
			Log.i("TAG","网络连接出现错误了");
		}
//		Log.i("TAG", "rev="+rev);
		return rev;
	}
	
	/**
	 * 
	 * @param order int类型的订单号，进行短信支付，暂时考虑使用GET_URL_ORER移动MM的进行支付
	 * @return
	 */
	public static String smsPay(int order){
		return null;
	}
	
	/**
	 * 获取手机为移动、联通、还是电信号段
	 * @param context 上下文对象 为0则取不到IMSI码
	 * @return 1-移动 2-联通 3-电信
	 */
	public static int getOperators(Context context){
		try {
			TelephonyManager tm = (TelephonyManager) context	.getSystemService(Activity.TELEPHONY_SERVICE);
			String imsi = tm.getSubscriberId();
			if (imsi != null) {
				if (imsi.startsWith("46000")|| imsi.startsWith("46002")	|| imsi.startsWith("46007")) {
					// 移动
					return 1;
				} else if (imsi.startsWith("46001")|| imsi.startsWith("46006")) {
					// 联通
					return 2;
			} else if (imsi.startsWith("46003")|| imsi.startsWith("46005")) {
				// 电信
				return 3;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
