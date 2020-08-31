package main.java;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;

import com.html5test.webview.R;


public class MainActivity extends Activity {
  private long pageStartTime = 0;
  private WebView webView;
  private ImageButton goBtn;
  private EditText editTextURL;
  private static String TAG="WEBVIEW-BROWSER";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    LOG("onCreate()...");

    webView = (WebView) findViewById(R.id.wv);
    goBtn = (ImageButton) findViewById(R.id.go);
    editTextURL = (EditText) findViewById(R.id.et);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      WebView.setWebContentsDebuggingEnabled(true);
      LOG("debug enabled");
    }

//    webView.setWebViewClient(new WebViewClient());
    //EnterpriseBrowser also uses a WebChromeClient
    webView.setWebChromeClient(new WebChromeClient());

    WebSettings settings = webView.getSettings();
    settings.setJavaScriptEnabled(true);
    settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    settings.setAppCacheEnabled(false);
    settings.setDomStorageEnabled(true);
    settings.setSupportZoom(true);
    settings.setBuiltInZoomControls(true);
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      settings.setAllowUniversalAccessFromFileURLs(true);
    }
    LOG("Settings: "+settings.getUserAgentString());

    webView.setWebViewClient(new WebViewClient() {
      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        LOG("WebViewClient onPageStarted: "+url);
        super.onPageStarted(view, url, favicon);
        editTextURL.setText(url);
      }

      @Override
      public void onPageFinished(WebView view, String url){
        LOG("onPageFinished: "+url);
      }
      @Override
      public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error){
        LOG("onReceivedError: "+request.getUrl() +", " +request.getMethod() +", error="+error.getDescription().toString());

      }
      @Override
      public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse){
        LOG("onReceivedHttpError: "+request.toString()+", error="+errorResponse.toString());

      }
      @Override
      public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request){
        LOG("shouldInterceptRequest: "+request.getMethod()+", "+request.getUrl()+", " +request.getRequestHeaders().toString());
        return super.shouldInterceptRequest(view, request);
      }

      @Override
      public void onLoadResource(WebView view, String url){
        LOG("onLoadResource: "+url);

      }
    });



    LOG("loadUrl https://html5test.com");
    webView.loadUrl("https://html5test.com");

    // setup events
    goBtn.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          goBtn.setColorFilter(getResources().getColor(android.R.color.holo_blue_dark));
          return false;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
          goBtn.setColorFilter(null);
          return false;
        }
        return false;
      }
    });

    goBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        handleLoadUrl(true);
      }
    });

    editTextURL.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
          handleLoadUrl(false);
        }
      }
  });
  }

  @Override
  public void onBackPressed() {
      if (webView.canGoBack() == true) {
          webView.goBack();
      } else {
          MainActivity.super.onBackPressed();
      }
  }

  private void handleLoadUrl(boolean forceReload) {
    LOG("handleLoadURL");
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(editTextURL.getWindowToken(), 0);

    String url = editTextURL.getText().toString();
    if (url.startsWith("http://")) {
    } else if (url.startsWith("https://")) {
      } else {
        url = String.format("http://%s", url);
      }

    if (!url.equals(webView.getUrl()) || forceReload) {
      LOG("loadURL: "+url);
      webView.loadUrl(url);
    }
  }
  private void LOG(String msg){
      Log.d(TAG, msg);
  }
}