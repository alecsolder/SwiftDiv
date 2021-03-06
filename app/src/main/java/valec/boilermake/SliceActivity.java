package valec.boilermake;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
/**
 * Created by AlecSolder on 10/17/2015.
 */
public class SliceActivity extends Activity{
    private WebView sliceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slice);
        sliceView = (WebView)findViewById(R.id.sliceWeb);
        sliceView.getSettings().setJavaScriptEnabled(true);
        sliceView.setWebViewClient(new MyWebviewClient());
        sliceView.loadUrl("https://api.slice.com/oauth/authorize?client_id=bb228a3c&response_type=code&redirect_uri=https://developers.google.com/oauthplayground");

    }

    private class MyWebviewClient extends WebViewClient{
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon){
            Log.i("SliceActivity", "URL is "+url);
            if ( url.startsWith("https://developers.google.com") ) {
                view.stopLoading();
            }
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // MY_PREFS_NAME - a static String variable like:
            //public static final String MY_PREFS_NAME = "MyPrefsFile";

            //Parse this url and get the code value we need and save it to pref
            if (url.startsWith("https://developers.google.com/oauthplayground"))
            {
                view.stopLoading();
                SharedPreferences.Editor editor = getSharedPreferences("PrefFile", MODE_PRIVATE).edit();
                editor.putString("key", url.substring(51));
                Log.i("SliceActivity", "KEY IS " + url.substring(51));
                editor.commit();
                view.loadUrl(url);
                try {
                    URL urlAuth = new URL("https://api.slice.com/oauth/token");
                }
                catch(MalformedURLException ex){
                    Context context = getApplicationContext();
                    CharSequence text = "URL EXCEPTION!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
//                HttpClient httpClient = new e
                Intent intent = new Intent();
                setResult(1, intent);
                SliceActivity.this.finish();
                return true;
            }
            return false;
        }
    }

}
