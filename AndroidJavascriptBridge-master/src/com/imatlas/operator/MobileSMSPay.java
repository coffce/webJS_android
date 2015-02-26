package com.imatlas.operator;
import mm.purchasesdk.OnPurchaseListener;
import mm.purchasesdk.Purchase;
import android.content.Context;
import android.view.Menu;
import android.widget.Button;
public class MobileSMSPay {
	public static final int ITEM0 = Menu.FIRST;// 系统值
	private final String TAG = "Demo";
	public static Purchase purchase;
	private Button billButton;
	private IAPListener mListener;
	
//	// 计费信息
//	// 计费信息 (现网环境)
	private static final String APPID = "300008895892";
	private static final String APPKEY = "2BAC0464C819EBCB3A39A443E764D6B9";
	// 计费点信息
	private static final String LEASE_PAYCODE = "30000889589201";
	
	/**
	 * 初始化移动MM支付SDK
	 * @param context
	 * @param mListener
	 */
	public static  void initMoblieSms(Context context,IAPListener mListener){
		/** * IAP组件初始化.包括下面3步。 */
//		/**
//		 * step1.实例化PurchaseListener。实例化传入的参数与您实现PurchaseListener接口的对象有关。
//		 * 例如，此Demo代码中使用IAPListener继承PurchaseListener，其构造函数需要Context实例。
//		 */
//		mListener = new IAPListener();
		/**
		 * step2.获取Purchase实例。
		 */
		purchase = Purchase.getInstance();
		/**
		 * step3.向Purhase传入应用信息。APPID，APPKEY。 需要传入参数APPID，APPKEY。 APPID，见开发者文档
		 * APPKEY，见开发者文档
		 */
		try {
			purchase.setAppInfo(APPID, APPKEY);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		/**
		 * step4. IAP组件初始化开始， 参数PurchaseListener，初始化函数需传入step1时实例化的
		 * PurchaseListener。
		 */
		try {
			purchase.init(context, mListener);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * 通过移动MM进行支付
	 * @param context 上下文对象
	 * @param listener 监听返回值
	 * @param orderId 订单号，传订单回调到后面服务器
	 */
	public static void order(Context context, OnPurchaseListener listener,String orderId) {
		try {
//			purchase.order(context, mPaycode, mProductNum, listener);  --helloworld可换成自己后台生成的订单号
			purchase.order(context,LEASE_PAYCODE , 1,orderId,false,listener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
