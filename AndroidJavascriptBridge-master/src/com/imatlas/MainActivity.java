package com.imatlas;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.imatlas.jsb.JavascriptBridge;
import com.imatlas.util.JSONUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {
	
	//定义c++层，或者lua层调用java层方法，传值上来，用的是json格式的字符串
	public void pay(String params){
		//对json格式进行解析，遍历出，将其存为键值对
		try {
			String mode=JSONUtil.jsonStringToString(params, 0);
			String urlStr =JSONUtil.jsonStringToString(params, 1);
			if(mode.equals("0")){
				//根据其urlStr 取订单号，并进行短信支付
			}else{
				//根据其urlStr 取订单号，并转webView进行支付
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//当支付完成时，成功与否都通过lua层,C++层，以json格式传递过去
	 public native int add(String params); 
	
	//模拟网络访问地址：
	private final String URL="http://192.168.1.159:8081/phpdemo1/?price=1000";
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
				Intent intent =new Intent();
				intent.setClass(MainActivity.this,WebActivity.class);
				intent.putExtra("url",URL);
				startActivity(intent);
			}
		});
	}

}
