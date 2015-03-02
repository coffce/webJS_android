package com.imatlas;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.imatlas.alipay.Alipay;
import com.imatlas.alipay.PayResult;
import com.imatlas.util.JSONUtil;
import com.imatlas.util.Utils;
import com.imatlas.util.YiXunPay;

public class MainActivity extends Activity {
	//设置取到订单号的标记为1 sms取订单
	public static final int GET_URL_ORER=1;
	// 支付宝、微信钱包获取订单状态
	public static final int GET_URL_ORDE_WEB =2;
	//短信支付成功
	public static final int SMS_PAY_SUCCES = 101;
	//短信支付失败
	public static final int SMS_PAY_FAILUEW=102;
	//支付宝SDK支付结果返回
	public static final int SDK_PAY_FLAG=3;
	//web支付的返回结果
	public static final int WEB_PAY=10;
	public static MainActivity mActivity=null;
	private static final String TAG="MainActivity";
	//由于往下传递的时候，需要传递订单号，所以布局一个全局变量
	public static int orderId=0;
	//请求值，打开WebActivity 请求值为1
	private static final int REQUSET=1;
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
					try {
						YiXunPay.pay(MainActivity.this, "05", orderId, myHandler);
					} catch (IOException e) {
						Log.i(TAG,"支付出现异常了。");
						e.printStackTrace();
					}
				}
				break;
			case GET_URL_ORDE_WEB:
				if(orderId==0){
					String str=JSONUtil.payTojsonString(0, 8);
					Log.i(TAG,str+ " web激活SDK支付 获取订单失败");
					mActivity.paymentCompletion(str);
				}else{
					//此部分调用支付宝支付。
					Map<String,String> hm=(Map<String,String>)msg.obj;
					Alipay pay=Alipay.getInstance(MainActivity.this);
					pay.pay(hm.get("subject"), hm.get("subject"), hm.get("price"), String.valueOf(orderId), myHandler);
				}
				break;
			case SDK_PAY_FLAG:
				PayResult payResult = new PayResult((String) msg.obj);
				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();
				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					Log.i(TAG, "支付成功");
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Log.i(TAG, "支付结果确认中");

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Log.i(TAG, "支付失败");
					}
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
				getOrder(urlStr,0,null);

			} else {
				// 根据其urlStr 取订单号，并转webView进行支付
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, WebActivity.class);
				intent.putExtra("url",urlStr );
				Log.i(TAG,urlStr);
				intent.putExtra("url", urlStr);
				startActivityForResult(intent, REQUSET);  
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void getOrder(String urlStr,String subject, String  price){
		  Message msg = new Message();
		  Map<String,String> hm=new HashMap<String,String>();
		  hm.put("subject",subject);
		  hm.put("price", price);
		  msg.obj = hm;
		  getOrder(urlStr,1,msg);
	}

	/**
	 * 取订单，这里分两种情况，一种是sms发起的取订单。另外一种是web激活支付宝/微信钱包进行支付，取订单
	 * mode =0 则是sms支付，获取订单 mode=1则是支付宝方式，获取订单
	 * @param urlStr
	 */
	private void getOrder(final String urlStr,final int mode,final Message msg) {
		// 根据其urlStr 取订单号，并进行短信支付
		new Thread(new Runnable() {
			@Override
			public void run() {
				//在此获取订单，由于是网络请求这部分，所以需要放在子线程当中才行
				try {
					orderId=Utils.getOrder(urlStr);
					if(mode==0)
						myHandler.sendEmptyMessage(GET_URL_ORER);
					else{
						msg.what=GET_URL_ORDE_WEB;
						myHandler.sendMessage(msg);
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
	}

	// 当支付完成时，成功与否都通过lua层,C++层，以json格式传递过去
	public native int paymentCompletion(String params);
	
	// 模拟网络访问地址：
	private final String URL = "http://192.168.1.180:8081/phpdemo1/?";
//	private final String URL = "http://192.168.1.180:8082/phpdemo1/?";

	
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
				String jsonStr= "{\"mode\":1,\"gameid\":10001,\"channel\":10005,\"price\":100,\"goodsId\":1006}";
				mActivity.pay(jsonStr);
			}
		});
		YiXunPay.setContext(this);
		YiXunPay.initPaySDK();
		mActivity= this;
	}
	
	
	  @Override  
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
	        // TODO Auto-generated method stub  
	        super.onActivityResult(requestCode, resultCode, data);  
	        //requestCode标示请求的标示   resultCode表示有数据   
	        if (requestCode == REQUSET ) {  
	        	
	           switch(resultCode){
	           //1 - web请求，采用支付宝支付
	           case 1:
	        	   Log.i(TAG,"成功通过web发起支付宝 --- 成功 ");
	        	   String result =data.getStringExtra("result");
		        	String subject="";
		        	String price="";
					try {
						subject=JSONUtil.jsonStringToSTring(result, "subject");
						price = JSONUtil.jsonStringToSTring(result, "price");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.i(TAG,"解析json数据出现异常了。");
					}
	        	   //在此处获取订单 url=取订单网址
	        	   getOrder("url====", subject, price);
	        	   break;
	           case 2:
	        	   break;
	           case 4:
	        	   //用户自己关闭了网页，支付失败
	        	   break;
	           case 12:
	        	   //用户访问支付网页出错。支付失败
	        	   break;
	           default:
	        	   break;
	           }
	        }  
	    }  
}
