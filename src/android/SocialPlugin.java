package com.wangdahoo.social;
import java.util.HashMap;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import com.google.gson.JsonObject;

public class SocialPlugin extends CordovaPlugin{
	private static final String LOGIN = "login";
	private static final int LOGIN_TYPE_QQ = 0;
	private static final int LOGIN_TYPE_SINA = 1;
	private static final int LOGIN_TYPE_WECHAT = 2;
	private static final int ERROR_CODE_CANCEL = 0;//取消
	private static final int ERROR_CODE_ERROR = 1;//发生错误
	private PlatformActionListener mPlatformActionListener;
    public CallbackContext callbackContext;
	@Override
	public void initialize(final CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		mPlatformActionListener = new PlatformActionListener() {
			@Override
			public void onCancel(Platform arg0, int arg1) {
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("errorCode", ERROR_CODE_CANCEL);
				callbackContext.error(jsonObject.toString());
			}

			@Override
			public void onComplete(Platform platform, int arg1, HashMap<String, Object> arg2) {	
				PlatformDb weiboDb = platform.getDb();
				String userId = weiboDb.getUserId();
				String nickname = weiboDb.get("nickname"); // getAuthedUserName();
				
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("userId", userId);
				jsonObject.addProperty("nickname", nickname);
				callbackContext.success(jsonObject.toString());
			}

			@Override
			public void onError(Platform arg0, int arg1, final Throwable arg2) {
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("errorCode", ERROR_CODE_ERROR);
				jsonObject.addProperty("errorMsg", Log.getStackTraceString(arg2));
				callbackContext.error(jsonObject.toString());
			}  
          };
	}

	@Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext)  throws JSONException{
        this.callbackContext = callbackContext;
		boolean result = false;
        if (LOGIN.equals(action)){
		   int loginType = args.getInt(0);
		   authorize(loginType);
		   result = true;
        }
        return result;
	}
	
	/**
	 * 授权登录
	 * @param type
	 */
	private void authorize(int type){
		String platformName = "";
		switch(type){
		   case LOGIN_TYPE_QQ:
			   platformName = QQ.NAME;			   
			   break;
		   case LOGIN_TYPE_SINA:
			   platformName = SinaWeibo.NAME;
			   break;
		   case LOGIN_TYPE_WECHAT:
			   platformName = Wechat.NAME;
			   break;
		   }
		try{
			Platform platform = ShareSDK.getPlatform(platformName);
			platform.setPlatformActionListener(mPlatformActionListener);
			//platform.authorize();
			platform.SSOSetting(true);
			platform.showUser(null);
		} catch(Exception e){
//			com.wangdahoo.util.T.showShort(cordova.getActivity(), Log.getStackTraceString(e));
		}
	}

}
