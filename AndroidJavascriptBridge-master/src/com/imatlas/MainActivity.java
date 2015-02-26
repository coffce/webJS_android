package com.imatlas;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.imatlas.operator.IAPListener;
import com.imatlas.operator.MobileSMSPay;
import com.imatlas.util.JSONUtil;
import com.imatlas.util.Utils;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

public class MainActivity extends Activity {
	//设置取到订单号的标记为1
	public static final int GET_URL_ORER=1;
	//短信支付成功
	public static final int SMS_PAY_SUCCES = 101;
	//短信支付失败
	public static final int SMS_PAY_FAILUEW=102;
	//web支付的返回结果
	public static final int WEB_PAY=3;
	public static MainActivity mActivity=null;
	private static final String TAG="MainActivity";
	//由于往下传递的时候，需要传递订单号，所以布局一个全局变量
	public static int orderId=0;
	// 用来接收：短信支付，或者web支付传过来的信息，短信支付这块，由于网络连接这部分，需要对其进行处理
	public  Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_URL_ORER:
				if(orderId==0){
					String str=JSONUtil.payTojsonString(0, 5);
					Log.i(TAG,str+ " 取订单失败,sms支付失败");
					mActivity.paymentCompletion(str);
				}else{
					//此部分调用短信的支付。
					MobileSMSPay.order(MainActivity.this, new IAPListener(myHandler),String.valueOf(orderId));
				}
				break;
			case SMS_PAY_SUCCES:
				Log.i(TAG,JSONUtil.payTojsonString(orderId, 1)+"  sms支付成功");
				break;
			case SMS_PAY_FAILUEW:
				Log.i(TAG,JSONUtil.payTojsonString(orderId, 2)+"  sms支付失败");
				break;
			case WEB_PAY:
				break;
			}
			super.handleMessage(msg);
		}
	};
	// 定义c++层，或者lua层调用java层方法，传值上来，用的是json格式的字符串
	public void pay(String params) {
		// 对json格式进行解析，遍历出，然后根据不同的type值来返回不同的string字符串来进行不同的处理
		try {
			String mode = JSONUtil.jsonStringToString(params, 0);
			//URL + 获取的get形式的字符串 去服务器请求 订单
			final String urlStr = URL+JSONUtil.jsonStringToString(params, 1);
			Log.i(TAG,"mode="+mode);
			if (mode.equals("0")) {
				// 根据其urlStr 取订单号，并进行短信支付
				new Thread(new Runnable() {
					@Override
					public void run() {
						//在此获取订单，由于是网络请求这部分，所以需要放在子线程当中才行
						try {
							orderId=Utils.getOrder(urlStr);
							myHandler.sendEmptyMessage(GET_URL_ORER);
						} catch (ClientProtocolException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}).start();

			} else {
				// 根据其urlStr 取订单号，并转webView进行支付
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, WebActivity.class);
				intent.putExtra("url",urlStr );
				Log.i(TAG,urlStr);
				intent.putExtra("url", urlStr);
				startActivity(intent);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 当支付完成时，成功与否都通过lua层,C++层，以json格式传递过去
	public native int paymentCompletion(String params);
	
	// 模拟网络访问地址：
	private final String URL = "http://192.168.1.180:8081/phpdemo1/?";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//取到为某个运营商再初始化其SDK
		int operators=Utils.getOperators(this);
		if (operators==1){
			//移动
			/** 当前手机号段为移动，初始化移动支付SDK */
			MobileSMSPay.initMoblieSms(this, new IAPListener(myHandler));
		}else if(operators==2){
			//联通
		}else if(operators==3){
			//电信
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TextView tv = (TextView) findViewById(R.id.tv1);
		Button btn = (Button) findViewById(R.id.btn1);
		tv.setText(URL);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Intent intent = new Intent();
//				intent.setClass(MainActivity.this, WebActivity.class);
//				intent.putExtra("url", URL);
//				startActivity(intent);
				String jsonStr= "{\"mode\":0,\"gameid\":10001,\"channel\":10005,\"price\":600,\"goodsId\":1006}";
				mActivity.pay(jsonStr);
			}
		});
		mActivity= this;
	}
}
