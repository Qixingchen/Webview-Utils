package moe.xing.webviewutils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URI;
import java.util.List;

import moe.xing.baseutils.network.cookies.MyCookiesManager;
import moe.xing.baseutils.utils.LogHelper;
import moe.xing.baseutils.view.BaseActivity;
import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created by Hehanbo on 2016/6/2 0002.
 * <p>
 * webview
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class WebViewActivity extends AppCompatActivity {
    public static final String URL_LOAD = "URL_LOAD";

    private WebView mWebView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(moe.xing.baseutils.R.layout.activity_webview);
        mWebView = (WebView) findViewById(moe.xing.baseutils.R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        Intent intent = getIntent();
        String uri = intent.getStringExtra(URL_LOAD);
        if (TextUtils.isEmpty(uri)) {
            LogHelper.Toast("网址不存在");
            finish();
            return;
        }

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        List<Cookie> cookies = new MyCookiesManager().loadForRequest(HttpUrl.get(URI.create(uri)));

        cookieManager.removeAllCookie();

        for (Cookie cookie : cookies) {

            cookieManager.setCookie(cookie.domain(), cookie.toString());
            Log.d("CookieUrl", cookie.toString());

        }

        mWebView.loadUrl(uri);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(view.getTitle());
                }

            }
        });
    }
}
