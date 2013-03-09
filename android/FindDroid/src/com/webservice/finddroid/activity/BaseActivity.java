package com.webservice.finddroid.activity;

import android.webkit.WebView;
import cn.jpush.android.api.InstrumentedActivity;

/** 
 * <p>Description: [抽象基类]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public abstract class BaseActivity extends InstrumentedActivity {
    /**
     * 展现的WEB页面
     */
    protected WebView mWebView;
    
    public abstract void setTitle(String pageTitle);
    
    public abstract void setBackButtonName(String backButtonName);
    
    public abstract void onPageFinished(String url);
    
    @Override
    protected void onDestroy() {
        if(null != mWebView){
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }
}
