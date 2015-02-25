package com.imatlas.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.ehoo.app.OnInitListener;
import com.ehoo.app.OnPayListener;
import com.ehoo.app.Pay;
import com.ehoo.app.PayOption;
import com.ehoo.app.PaySDK;
import com.ehoo.app.ResultBean;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Title: <br>
 * Description: <br>
 * Create Date:2014年12月30日上午10:51:19<br>
 *
 * @anthor xiang
 */
public class YiXunPay {
	private static String TAG ="YiXunPay";
	static Context mContext;
	// 应用编号
	private static final String OPENAPPID = "1236";
	// 商户渠道编号
	public static final String MERID = "1174";
	// 计费类型（预留字段，固定传入”0”）
	public static final String FEETYPE = "0";
	// 手机号
	public static final String PHONE = "";
	public YiXunPay() {
	}
	
	//在主Activity中的onCreate方法中进行初始化(由于此处并不需要阻碍整个游戏的运行，所以此时无需中外开个Activity来进行处理)
	public static void setContext(Context context) {
		mContext = context;
	}

	// PaySDK初始化 ：此初始化只需在应用启动时调用一次即可，无需计费时重复调用
	public static void initPaySDK() {
		Log.i("TAG", "执行到initPaySDK这个方法。");
		PaySDK.setMerID(MERID); // 所属商户NCID
		PaySDK.setOpenAppID(OPENAPPID); // 应用编号
		PaySDK.setFeetype(FEETYPE);
		/* 参数设置完毕后，调用初始化 */
		// 不关注初始化结果
//		 PaySDK.init(mContext);
		// 关注初始化结果
		PaySDK.init(mContext, new OnInitListener() {
			@Override
			public void onInitResult(String result) {
				// TODO Auto-generated method stub
				if (!result.equals("0000")) {
					Log.i(TAG, "传入数据错误，初始化失败。code ="+result);
					//此时，短信初始化失败，直接通过handler返回值给C++层或者Lua层
				}
			}
		});
	}

	/**
	 * 各支付点，调用易迅支付的方法
	 * @param chargePoint   计费点编号
	 *@param 用于返回其支付是否成功 ，可定义两个handler返回值
	 *支付的时候，只需要调用其方法就行：adj:YiXunPay.pay(this,payId, ,handler);
	 * @throws IOException 
	 */
	public static void pay(Context context,String chargePoint,int order,final Handler handler) throws IOException {
		PayOption payOption = new PayOption();
		payOption.setOpenChargePoint(chargePoint);// 计费点编号
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
		payOption.setOrderDate(format.format(new Date()));
//		Log.i(TAG, "orderid="+order);
		payOption.setOrderID(String.valueOf(order));
		payOption.setPhone(PHONE);
//		setVipUIOne(context,payOption,payId);
//		setPayView(context,payOption);
//		Log.i("TAG", "执行一次支付操作");
		Pay pay = new Pay(context);
		pay.setPayOptions(payOption);
		pay.setOnPayListener(new OnPayListener() {
			@Override
			public boolean onPostPayResult(ResultBean result) {
				if (result != null && result.isSuccess()) {
					// 支付成功
					handler.sendEmptyMessage(101);
				} else {
					// 支付失败
					handler.sendEmptyMessage(102);
				}
				// 返回false使用默认支付结果提示
				// return false;
				// 返回true屏蔽默认支付结果提示
				// innerOnPay(PayMainActivity.this, result);
				return true;
			}
		});
		pay.start();
	}
	
}
