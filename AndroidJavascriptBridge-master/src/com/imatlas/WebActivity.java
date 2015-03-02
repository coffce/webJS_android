package com.imatlas;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.imatlas.jsb.JavascriptBridge;
import org.json.JSONException;
import org.json.JSONObject;

public class WebActivity extends Activity {
	private final static String TAG = "WebActivity";
	private final static int LOADURL_ERROR = 404;
	private WebView webView;
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String url = intent.getStringExtra("url");
		// 以下 布局参数
		// 标识当前控件的宽高情况MATCH_PARENT=占据全部父控件，WRAP_CONTENT=仅包裹控件中的内容//还有其他作用比如左右边距，这里我们使用默认的
		RelativeLayout.LayoutParams RP_FF = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		RelativeLayout.LayoutParams RP_WW = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		RP_WW.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		RelativeLayout rl = new RelativeLayout(this);
		rl.setLayoutParams(RP_FF);
		webView = new WebView(this);
		webView.setLayoutParams(RP_FF);
		rl.addView(webView);
		// final ProgressBar pbar=(ProgressBar)findViewById(R.id.progress);
		final ProgressBar pbar = new ProgressBar(this, null,
				android.R.attr.progressBarStyle);
		pbar.setLayoutParams(RP_WW);
		rl.addView(pbar);
		setContentView(rl);
		webView.setWebChromeClient(new WebChromeClient());
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

		final JavascriptBridge jsb = new JavascriptBridge(webView);

		// 添加个 messagebox 方法给js
		jsb.addJavaMethod("messagebox", new JavascriptBridge.Function() {

			@Override
			public Object execute(JSONObject params) {
				Toast.makeText(getApplicationContext(), params.toString(),
						Toast.LENGTH_LONG).show();
				// 解析数据格式：{"status":'1',"text":"你好, messagebox!"} 类似格式
				try {
					String status = params.getString("status");
					Intent intent = new Intent();
					// 返回其
					intent.putExtra("result", params.toString());
					if (status.equals("0")) {
						// 支付失败情况
						setResult(4, intent);
					} else if (status.equals("12")) {
						// web支付失败(访问网页出错)
						setResult(12, intent);
					} else if (status.equals("6")) {
						// web请求的支付宝支付
						setResult(1, intent);
					} else if (status.equals("7")) {
						// web请求的，微信钱包支付
						setResult(2, intent);
					} else {
						// web方式支付成功
						setResult(3, intent);
					}
					// 关闭当前webview窗口
					WebActivity.this.finish();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return "";
			}
		});

		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				pbar.setVisibility(View.GONE);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				view.stopLoading();
				view.clearView();
				// 加载网页出错时的处理 加载时间超过，设置一个错误页面，放置在本地，进行提醒
				Log.i(TAG, "加载网页出错了");
				Log.i(TAG, failingUrl);
				try {
					view.loadUrl("file:///android_asset/error.html#" + URLEncoder.encode(failingUrl,"utf-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.i(TAG, "执行到这里=url="+url);
				 pbar.setVisibility(View.VISIBLE);
				 try {
					view.loadUrl(URLDecoder.decode(url.substring(22),"utf-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				return true;
			}
		});
		 webView.loadUrl(url);
//		try {
//			webView.loadUrl("file:///android_asset/error.html#"+ URLEncoder.encode("http://192.168.1.180:8081/phpdemo1/?goodsId=1006&price=100&channel=10005&gameid=10001&mode=1","utf-8"));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出支付窗口",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
