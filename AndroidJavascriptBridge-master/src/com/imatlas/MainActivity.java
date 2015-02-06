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

import com.imatlas.util.JSONUtil;
import com.imatlas.util.Utils;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

public class MainActivity extends Activity {
	//设置取到订单号的标记为1
	public static final int GET_URL_ORER=1;
	//短信支付后，不管结果是否失败，定义其线程标记为2
	public static final int SMS_PAY=2;
	//转至web支付，定义其返回值的标记为：3
	public static final int WEB_PAY=3;
	public static MainActivity mActivity=null;
	private static final String TAG="MainActivity";
	
	// 用来接收：短信支付，或者web支付传过来的信息，短信支付这块，由于网络连接这部分，需要对其进行处理
	public  Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what){
			case GET_URL_ORER:
				
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
			if (mode.equals("0")) {
				// 根据其urlStr 取订单号，并进行短信支付
				new Thread(new Runnable() {
					@Override
					public void run() {
						//在此获取订单，由于是网络请求这部分，所以需要放在子线程当中才行
						try {
							int order=Utils.getOrder(urlStr);
							if(order!=0){
								Message msg = myHandler.obtainMessage(); 
								//将其order 订单给赋值 给msg.arg1 
								msg.arg1=order;
								msg.what=GET_URL_ORER;
								myHandler.sendMessage(msg);
							}else{
								Log.i(TAG,"在获取订单的时候出现问题了。。。");
							}
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
				startActivity(intent);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 当支付完成时，成功与否都通过lua层,C++层，以json格式传递过去
	public native int add(String params);

	// 模拟网络访问地址：
	private final String URL = "http://192.168.1.159:8081/phpdemo1/?";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
			}
		});
		
		mActivity= this;
	}

}
