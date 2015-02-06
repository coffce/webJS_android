package com.imatlas;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.imatlas.jsb.JavascriptBridge;

import org.json.JSONException;
import org.json.JSONObject;

public class WebActivity extends Activity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        String url = intent.getStringExtra("url");
        setContentView(R.layout.web_activity_main);
        final WebView webView = (WebView)findViewById(R.id.webView1);
        webView.setWebChromeClient(new WebChromeClient());
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        
        final JavascriptBridge jsb = new JavascriptBridge(webView);
        
        //添加个 messagebox 方法给js
        jsb.addJavaMethod("messagebox", new JavascriptBridge.Function() {
            
            @Override
            public Object execute(JSONObject params) {
                Toast.makeText(getApplicationContext(), params.toString(), Toast.LENGTH_LONG).show();
                //解析数据格式：{"status":'1',"text":"你好, messagebox!"}
                try {
					String status=params.getString("status");
					if(status.equals("0")){
						//关闭当前webview窗口
//						onDestroy();
						WebActivity.this.finish();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                return "";
            }
        });
        
        final ProgressBar pbar=(ProgressBar)findViewById(R.id.progress);
        
        
        webView.setWebViewClient(new WebViewClient() {  
        	  
            @Override  
            public void onPageFinished(WebView view, String url) {  
            	pbar.setVisibility(View.GONE);
            }  
  
            @Override  
            public void onReceivedError(WebView view, int errorCode,  
                    String description, String failingUrl) {  
              //加载网页出错时的处理
            }  
  
            @Override  
            public boolean shouldOverrideUrlLoading(WebView view, String url) {  
            	pbar.setVisibility(View.VISIBLE);
                view.loadUrl(url);  
                return true;  
            }  
  
        });  
        
        webView.loadUrl(url);

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
