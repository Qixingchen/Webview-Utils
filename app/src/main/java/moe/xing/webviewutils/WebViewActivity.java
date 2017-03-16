package moe.xing.webviewutils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URI;
import java.util.List;

import moe.xing.baseutils.Init;
import moe.xing.baseutils.network.cookies.MyCookiesManager;
import moe.xing.baseutils.utils.LogHelper;
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

    /**
     * 获取 UA
     */
    @NonNull
    private static String UA() {
        String BuildVersion = Init.getVersionName();
        String rootBuildVersion = BuildVersion.substring(0, BuildVersion.lastIndexOf("."));
        return " " + Init.getUaName() + "/" + rootBuildVersion +
                "(Android;Build 1;Version " + BuildVersion + ";)";
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setDomStorageEnabled(true);

        String ua = mWebView.getSettings().getUserAgentString();
        mWebView.getSettings().setUserAgentString(ua + UA());

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
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (URLUtil.isNetworkUrl(url)) {
                    return false;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(view.getTitle());
                }
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(title);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
