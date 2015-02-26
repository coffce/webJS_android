package com.imatlas.operator;

import java.util.HashMap;

import mm.purchasesdk.OnPurchaseListener;
import mm.purchasesdk.Purchase;
import mm.purchasesdk.PurchaseCode;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class IAPListener implements OnPurchaseListener {
	private final String TAG = "IAPListener";
	private Handler handler;
	public IAPListener(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void onAfterApply() {

	}

	@Override
	public void onAfterDownload() {

	}

	@Override
	public void onBeforeApply() {

	}

	@Override
	public void onBeforeDownload() {

	}

	@Override
	public void onInitFinish(int code) {
		Log.d(TAG, "Init finish, status code = " + code);
		String result = "初始化结果：" + Purchase.getReason(code);
		Log.i(TAG, result);
	}

	@Override
	public void onBillingFinish(int code, HashMap arg1) {
		Log.d(TAG, "billing finish, status code = " + code);
//		String result = "订购结果：订购成功";
//		// 此次订购的orderID
//		String orderID = null;
//		// 商品的paycode
//		String paycode = null;
//		// 商品的有效期(仅租赁类型商品有效)
//		String leftday = null;
//		// 商品的交易 ID，用户可以根据这个交易ID，查询商品是否已经交易
//		String tradeID = null;
//		
//		String ordertype = null;
//		if (code == PurchaseCode.ORDER_OK || (code == PurchaseCode.AUTH_OK) ||(code == PurchaseCode.WEAK_ORDER_OK)) {
//			/**
//			 * 商品购买成功或者已经购买。 此时会返回商品的paycode，orderID,以及剩余时间(租赁类型商品)
//			 */
//			if (arg1 != null) {
//				leftday = (String) arg1.get(OnPurchaseListener.LEFTDAY);
//				if (leftday != null && leftday.trim().length() != 0) {
//					result = result + ",剩余时间 ： " + leftday;
//				}
//				orderID = (String) arg1.get(OnPurchaseListener.ORDERID);
//				if (orderID != null && orderID.trim().length() != 0) {
//					result = result + ",OrderID ： " + orderID;
//				}
//				paycode = (String) arg1.get(OnPurchaseListener.PAYCODE);
//				if (paycode != null && paycode.trim().length() != 0) {
//					result = result + ",Paycode:" + paycode;
//				}
//				tradeID = (String) arg1.get(OnPurchaseListener.TRADEID);
//				if (tradeID != null && tradeID.trim().length() != 0) {
//					result = result + ",tradeID:" + tradeID;
//				}
//				ordertype = (String) arg1.get(OnPurchaseListener.ORDERTYPE);
//				if (tradeID != null && tradeID.trim().length() != 0) {
//					result = result + ",ORDERTYPE:" + ordertype;
//				}
//			}
//		} else {
//			/** * 表示订购失败。 */
//			result = "订购结果：" + Purchase.getReason(code);
//		}
//		Log.i(TAG,result);
		if (code==PurchaseCode.ORDER_OK){
			//此时购买成功
			handler.sendEmptyMessage(101);
		}
		else{
			handler.sendEmptyMessage(102);
		}
	}

	@Override
	public void onQueryFinish(int code, HashMap arg1) {
		
	}

	@Override
	public void onUnsubscribeFinish(int arg0) {
		// TODO Auto-generated method stub
		
	}
}
