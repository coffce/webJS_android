package com.imatlas.util;
import android.os.Bundle;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
public class JSONUtil {
	private static String TAG ="JSONUtil";
	/**
	 * 把 bundle 转换成 json 对象, 只取用 String, Boolean, Integer, Long, Double
	 * 
	 * @param bundle
	 * @return
	 * @throws JSONException
	 */
	public static JSONObject bundleToJSON(Bundle bundle) throws JSONException {
		JSONObject json = new JSONObject();
		if (bundle == null || bundle.isEmpty()) {
			return json;
		}
		Set<String> keySet = bundle.keySet();
		for (String key : keySet) {
			Object object = bundle.get(key);
			if (object instanceof String || object instanceof Boolean
					|| object instanceof Integer || object instanceof Long
					|| object instanceof Double) {
				json.put(key, object);
			}
		}
		return json;
	}

	/**
	 * 把 bundle 转换成 json 字符串, 只取用 String, Boolean, Integer, Long, Double
	 * 
	 * @param bundle
	 * @return
	 * @throws JSONException
	 */
	public static String bundleToJSONString(Bundle bundle) throws JSONException {
		JSONObject json = bundleToJSON(bundle);
		return json.toString();
	}

	/**
	 * @param params   json格式的String字符串 [其中 mode=这个值是不会改变的]
	 * @param type 0= 取mode的值  type =1 返回 ?mode=1&gameid=101&*=* 格式的字符串
	 * @return 返回String的对象
	 * @throws JSONException 
	 */
	public static String jsonStringToString(String params,int type) throws JSONException {
		Log.i(TAG, params);
		JSONObject jobject= new JSONObject(params);
		if(type==0){
			return jobject.getString("mode");
		}else{
			String str="";
			Iterator<String> keys=jobject.keys(); 
			while(keys.hasNext()){
				String key =keys.next();
				String value=jobject.getString(key);
				str=str+key+"="+value+"&";
			}
			return str.substring(0,str.length()-1);
		}
	}
	
	//短信支付后的结果传给c++层或者lua层
	public static String payTojsonString(int order,int status){
		return "{\"order\":"+order+",\"status\":"+status+"}";
	}

}
