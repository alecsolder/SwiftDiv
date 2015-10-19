package valec.boilermake;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by AlecSolder on 10/17/2015.
 */
public class DivActivity extends Activity{
    public String totalCost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finaldiv);
        Intent mIntent = getIntent();
        totalCost = mIntent.getStringExtra("totalCost");
        TextView tot = new TextView(this);
        tot = (TextView)findViewById(R.id.tot_cost);
        tot.setText("Your total cost is $" + totalCost);

        // Get a reference to the AutoCompleteTextView in the layout
        AutoCompleteTextView textView1 = (AutoCompleteTextView) findViewById(R.id.autocomplete_friend1);
        AutoCompleteTextView textView2 = (AutoCompleteTextView) findViewById(R.id.autocomplete_friend2);
        AutoCompleteTextView textView3 = (AutoCompleteTextView) findViewById(R.id.autocomplete_friend3);
        // Get the string array
        String[] countries = getResources().getStringArray(R.array.friends_array);
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countries);
        textView1.setAdapter(adapter);
        textView2.setAdapter(adapter);
        textView3.setAdapter(adapter);

    }

    public void sendIt(View view){
        //this would work if venmo worked!!!!!
        Context context = getApplicationContext();
        CharSequence text = "This would work if venmo was working!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        DivActivity.this.finish();
    }

    public void compute(View view){
        AutoCompleteTextView textView1 = (AutoCompleteTextView) findViewById(R.id.autocomplete_friend1);
        AutoCompleteTextView textView2 = (AutoCompleteTextView) findViewById(R.id.autocomplete_friend2);
        AutoCompleteTextView textView3 = (AutoCompleteTextView) findViewById(R.id.autocomplete_friend3);
        AutoCompleteTextView textViewTest = (AutoCompleteTextView) findViewById(R.id.autocomplete_friendTest);

        Log.i("DivActivity", "TV1 " + textView1.length());
        Log.i("DivActivity", "TV2 " + textView2.length());
        Log.i("DivActivity", "TV3 " + textView3.length());
        int numpeople = 4;
        if(textView1.length() == 0){
            numpeople = numpeople - 1;
        }
        if(textView2.length() == 0){
            numpeople = numpeople - 1;
        }
        if(textView3.length() == 0){
            numpeople = numpeople - 1;
        }
        TextView tot = new TextView(this);
        tot = (TextView)findViewById(R.id.split_cost);
        float f = Float.valueOf(totalCost.trim()).floatValue();
        String s = String.format("%.2f", f/numpeople);
        tot.setText("$" + s);


    }





}
