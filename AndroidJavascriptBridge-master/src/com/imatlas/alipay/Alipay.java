package com.imatlas.alipay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

/**
 * 调用支付宝SDK 工具类进行支付
 * @author Administrator
 *
 */
public class Alipay {
	private final static String TAG="Alipay";
	// 商户PID
	public static final String PARTNER = "2088111582256863";
	// 商户收款账号
	public static final String SELLER = "hndojo@foxmail.com ";
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANYtMTIMu/w/PkrUkvUA7ZfUV3JlJ9vs2cLNyDUHcX/G8EIAWdR8BBfggPb0XZy7c1FGti5YW1wg1TncgeoEtJ90X7c1sTWg+mKfdyn+zG0Nupl6YUnp/xXY8vbVtIXnRY+GZGpbE77vjEYtINnZw7HtuI+zeIyMQR17rgjaFgv7AgMBAAECgYEAo2hjin4wxyoxisJZRVjp7ddyyLLpEGDLXFfJEryVGhpcoN7Hqtjb/qOpgcG70qMqPq6mvQFo4weh572X2Uaxzzl2vmapPJ8yxy7juta8m36pMmIzG55r+Zrw4MB5L8+Kf6kQ7GHGqmXUPl6fyo5wd+4sir3/lzLH6xEzCDjatXECQQDwvyKrTjtsPqEGS/4FQjpQwUOm2lQBLb5Yqc3OYNrZzfHPG58vRUuLtlXI86kqcjEXmEZU9T4Bg/ubKVJp/HwFAkEA478YS+LSg944gytY5Lm/6kfWO9xpMGze7N0qNaFtGW+NJojVuXx9/uP5+zrqWRCnb7ex2eiUmW1I55VNwlbn/wJAHP2nWyGsnjhcg7ma+V8kTp03XFxmSU4M4fhwxC19rUTTnbEh1jtuAYEPi4dsE6R0rPa1YX223c/a+LBdME4rUQJAekcYkW3oHzsJ1otIgA2nF++X+63coE5j5oLcbc0NGZIGFAF11tAbieX3SscYGdfSPi4/hELWvgWqFgTYqhYlGwJBAM8oeAqn/rHG4HR5EfYE8QykbQzAEDClqF2nf7BAqwKPzVeYLHRvUIpZIc+PG6SwriThj4/aWSwytP6jOy2KDq0=";
	// 支付宝公钥
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

	private static final int SDK_PAY_FLAG = 3;
	
	private static final int SDK_CHECK_FLAG = 2;
	
	private static Activity activity;
	private static Alipay instance = new Alipay();  
	     private Alipay (){}
	     public static Alipay getInstance(Activity activity1) {  
	    	 activity = activity1;
	    	 return instance;  
	     }  
	
//	private Handler mHandler = new Handler() {
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case SDK_PAY_FLAG: {
//				PayResult payResult = new PayResult((String) msg.obj);
//
//				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
//				String resultInfo = payResult.getResult();
//
//				String resultStatus = payResult.getResultStatus();
//
//				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
//				if (TextUtils.equals(resultStatus, "9000")) {
//					Log.i(TAG, "支付成功");
//				} else {
//					// 判断resultStatus 为非“9000”则代表可能支付失败
//					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
//					if (TextUtils.equals(resultStatus, "8000")) {
//						Log.i(TAG, "支付结果确认中");
//
//					} else {
//						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
//						Log.i(TAG, "支付失败");
//
//					}
//				}
//				break;
//			}
//			case SDK_CHECK_FLAG: 
//				Log.i(TAG,"检查结果为："+msg.obj);
//				break;
//			default:
//				break;
//			}
//		};
//	};


	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay(String subject, String body, String price,String orderId,final Handler mHandler) {
		// 订单
		String orderInfo = getOrderInfo(subject, body, price,orderId);

		// 对订单做RSA 签名
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(activity);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

//	/**
//	 * check whether the device has authentication alipay account.
//	 * 查询终端设备是否存在支付宝认证账户
//	 */
//	public void check() {
//		Runnable checkRunnable = new Runnable() {
//
//			@Override
//			public void run() {
//				// 构造PayTask 对象
//				PayTask payTask = new PayTask(activity);
//				// 调用查询接口，获取查询结果
//				boolean isExist = payTask.checkAccountIfExist();
//
//				Message msg = new Message();
//				msg.what = SDK_CHECK_FLAG;
//				msg.obj = isExist;
//				mHandler.sendMessage(msg);
//			}
//		};
//
//		Thread checkThread = new Thread(checkRunnable);
//		checkThread.start();
//
//	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(activity);
		String version = payTask.getVersion();
		Log.i(TAG,"版本号为："+version);
	}

	/**
	 * create the order info. 创建订单信息  
	 * orderId 网站商品订单号
	 * subject 商品名称
	 * body 商品详情
	 * price 商品价格
	 */
	public String getOrderInfo(String subject, String body, String price,String orderId) {
		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + orderId+ "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm"
				+ "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}


	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}

}
