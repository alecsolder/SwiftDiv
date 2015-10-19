package valec.boilermake;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalyzeResult;
import com.microsoft.projectoxford.vision.contract.OCR;
import com.microsoft.projectoxford.vision.contract.Region;
import com.microsoft.projectoxford.vision.contract.Line;
import com.microsoft.projectoxford.vision.contract.Word;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import com.squareup.picasso.*;
import org.json.JSONObject;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URL;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private ProgressDialog progress;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


//        TextView url = new TextView(this);
//        tot = (TextView)findViewById(R.id.tot_cost);
//        tot.setText("Step One: blast egg");
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment(sectionNumber);
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        private int mSectionNumber;

        public PlaceholderFragment(int sectionNumber) {
            mSectionNumber = sectionNumber;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView;
            if (mSectionNumber == 1) {
                rootView = inflater.inflate(R.layout.tab1, container, false);
            } else if (mSectionNumber == 2) {
                rootView = inflater.inflate(R.layout.tab2, container, false);
            } else {
                rootView = inflater.inflate(R.layout.tab3, container, false);
            }
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    //Above this is all setting up views. Now to actions!

    //This is the function that runs when you press camera button
    static final int REQUEST_TAKE_PHOTO = 1;

    public void openCamera(View view) throws IOException, VisionServiceException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Context context = getApplicationContext();
                CharSequence text = "PIC ERROR!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                //GetORCFromURL(photoFile);
            }
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM);
        File image;
        image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        Context context = getApplicationContext();
        Fimage = image;
        return image;
    }

    public File Fimage;
    public String total;
    public void GetORCFromURL(View view) throws FileNotFoundException,
            VisionServiceException, IOException {
        String Key = "7919d32e97c54372a3c3e22a2a947070";
        VisionServiceClient vsc = new VisionServiceRestClient(Key);
        String imgURL = Fimage.getAbsolutePath(); //Your local img path
        InputStream stream = new FileInputStream(imgURL);
        boolean detectOrientation = true;
        String langCode = "en";
        Log.i("MainActivity", "Starting");
        OCR result = vsc.recognizeText(stream, langCode, detectOrientation);
        List<Region> regions = result.regions;
        Log.i("MainActivity", "Regions: " + regions.size());
        StringBuffer text = new StringBuffer();
        //int count = 0;
        //String[] prices = new String[100];
        //float[] vals = new float[100];
        //boolean something = false;
        float max = 0.0f;
        for (Region r : regions) {
            for (Line l : r.lines) {
                for (Word w : l.words) {
                    if (w.text.contains(".")){
                        float f = Float.valueOf(w.text.trim()).floatValue();
                        if(f > max){
                            max = f;
                        }
                        Log.i("MainActivity", "FloatAmount: " + f);
                        Log.i("MainActivity", "FinalMAx: " + max);
                        Log.i("MainActivity", "StrAmount: "+ w.text);
                    }
                }
            }
        }

        Log.i("MainActivity", "After for loop");
        total = ""+max;
//        Context context = getApplicationContext();
//        CharSequence outp = "I DID OCR AND GOT TO THE END OF THIS!";
//        int duration = Toast.LENGTH_SHORT;
//
//        Toast toast = Toast.makeText(context, outp, duration);
//        toast.show();
    }

    public String getKey(){
        SharedPreferences prefs = getSharedPreferences("PrefFile", MODE_PRIVATE);
        String key = prefs.getString("key", null);
        return key;
    }
    public String getCode(){
        SharedPreferences prefs = getSharedPreferences("PrefFile", MODE_PRIVATE);
        String code = prefs.getString("code", null);
        return code;
    }
    public void printKey(View view){
        Log.i("MainActivity", "Key " + getKey());
    }

    public void authSlice(View view){
        Intent intent = new Intent(this, SliceActivity.class);
        startActivityForResult(intent, 0);
    }

    public void EDivIt(View view){
        Intent intent = new Intent(this, DivActivity.class);
        intent.putExtra("totalCost", orderTotal);
        startActivity(intent);
    }
    public void PDivIt(View view){
        Intent intent = new Intent(this, DivActivity.class);
        intent.putExtra("totalCost", total);
        startActivity(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == 1){
           Log.i("MainActivity", "Starting GetKeyTask");
            GetKeyTask getKeyTask = new GetKeyTask(this);
            getKeyTask.execute();
        }
    }

    public void runOCR(View view) {
        GetImageTask myTask = new GetImageTask(view);
        myTask.execute();
    }
    public int purchaseNum = 0;
    public void getPurchases(View view){
        Log.i("MainActivity", "PurchaseNum " + purchaseNum);
        Log.i("MainActivity", "Starting GetPurchasesTask");
        GetPurchasesTask GetPurchasesTask = new GetPurchasesTask(this);
        GetPurchasesTask.execute();

        setRecentPurchase();
    }
    public void incPurchase(View view){
        purchaseNum = purchaseNum + 1;
        Log.i("MainActivity", "PurchaseNum " + purchaseNum);
        Log.i("MainActivity", "Starting GetPurchasesTask");
        GetPurchasesTask GetPurchasesTask = new GetPurchasesTask(this);
        GetPurchasesTask.execute();
        setRecentPurchase();
    }
    public void decPurchase(View view){
        if (purchaseNum > 0){
            purchaseNum = purchaseNum - 1;
        }
        Log.i("MainActivity", "PurchaseNum " + purchaseNum);
        Log.i("MainActivity", "Starting GetPurchasesTask");
        GetPurchasesTask GetPurchasesTask = new GetPurchasesTask(this);
        GetPurchasesTask.execute();
        setRecentPurchase();
    }
    public void setRecentPurchase(){
        TextView tot = new TextView(this);
        tot = (TextView)findViewById(R.id.tot_cost);
        tot.setText("$"+orderTotal);
        TextView desc = new TextView(this);
        desc = (TextView)findViewById(R.id.item_desc);
        desc.setText(description);

        ImageView iv = new ImageView(this);
        iv = (ImageView)findViewById(R.id.item_pic);
        //Context context = getApplicationContext();
        imageURL = imageURL.replace("\"", "");
        Log.i("MainActivity", "Image URL: " + imageURL);

        Picasso.with(this)
                .load(imageURL)
                .placeholder(R.drawable.slice)
                .error(R.drawable.camera)
                .into(iv);


    }
    public String description = "temp desc";
    public String orderTotal = "temp tot";
    public String imageURL = "temp URL";



    private class GetImageTask extends AsyncTask<Void, Void, Boolean> {
        View v;

        public GetImageTask(View v) {
            this.v = v;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(MainActivity.this);
            progress.setTitle("Getting text");
            progress.setIndeterminate(true);
            progress.setMessage("Analyzing");
            progress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                GetORCFromURL(v);
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage());
            }
            return true;
        }

        @Override
        public void onPostExecute(Boolean success) {
            progress.dismiss();
        }
    }
    private class GetKeyTask extends AsyncTask<Void, Void, Boolean> {
        Activity activity;

        public GetKeyTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(activity);
            progress.setTitle("Getting Key");
            progress.setIndeterminate(true);
            progress.setMessage("Working");
            progress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpClient httpClient = new HttpClient();
            //HttpConnectionManager connManager = httpClient.get
            PostMethod httpPost = new PostMethod("https://api.slice.com/oauth/token");
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("client_id", "bb228a3c");
//                jsonObject.put("client_secret", "8b90abd5f2ecb07a2cbbaf263aafb91e");
//                jsonObject.put("grant_type", "authorization_code");
//                jsonObject.put("code", getKey());
//                jsonObject.put("redirect_uri", "https://developers.google.com/oauthplayground");
//            } catch (Exception e){
//                e.printStackTrace();
//            }
            String temp = "code="+ getKey()+ "&redirect_uri=https%3A%2F%2Fdevelopers.google.com%2Foauthplayground&client_id=bb228a3c&client_secret=8b90abd5f2ecb07a2cbbaf263aafb91e&grant_type=authorization_code";
            //Log.i("MainActivity", "JSON Object: " + jsonObject.toString());
            httpPost.setRequestEntity(new StringRequestEntity(temp));
            httpPost.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            //"auth", "bearer" + key
            String responseBody;
            try {
                int responseCode = httpClient.executeMethod(httpPost);
                Log.i("MainActivity", "Response code is: " + responseCode);
                responseBody = httpPost.getResponseBodyAsString();
                Log.i("MainActivity", "Response Body is: " + responseBody);
                String key = responseBody.substring(responseBody.indexOf("\": \"")+4,
                        responseBody.indexOf("\", \""));
                //String key = responseBody.substring(18,49);
                SharedPreferences.Editor editor = getSharedPreferences("PrefFile", MODE_PRIVATE).edit();
                editor.putString("code", key);
                Log.i("SliceActivity", "Code IS " + key);
                editor.commit();
                //json object to string
            } catch (IOException e) {
                e.printStackTrace();
            }

//            httpClient.getState().setCredentials(
//                    new AuthScope("api.slice.com", 443, null),
//                    new UsernamePasswordCredentials()
//            );
            return true;
        }

        @Override
        public void onPostExecute(Boolean success) {

            progress.dismiss();
        }
    }
    private class GetPurchasesTask extends AsyncTask<Void, Void, Boolean> {
        Activity activity;

        public GetPurchasesTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progress = new ProgressDialog(activity);
//            progress.setTitle("Getting Order History");
//            progress.setIndeterminate(true);
//            progress.setMessage("Working");
//            progress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpClient httpClient = new HttpClient();
            //HttpConnectionManager connManager = httpClient.get
            //String temp = "https://api.slice.com/api/v1/orders?expand=items&orderBy=\"purchaseDate desc\"";
            String temp = "https://api.slice.com/api/v1/orders?expand=items&orderBy=purchaseDate";

            GetMethod httpGet = new GetMethod(temp);
            Log.i("MainActivity", "HERE IT IS: " + temp);

            //Log.i("MainActivity", "JSON Object: " + jsonObject.toString());
            httpGet.setRequestHeader("Authorization", "Bearer " + getCode());


            //"auth", "bearer" + key
            String responseBody;
            try {
                int responseCode = httpClient.executeMethod(httpGet);
                Log.i("MainActivity", "Response code is: " + responseCode);
                responseBody = httpGet.getResponseBodyAsString();
                Log.i("MainActivity", "Response Body is: " + responseBody);
                int tStart = responseBody.indexOf("orderTotal") + "orderTotal".length()+2;
                int tFinish= responseBody.indexOf(",", tStart);
                orderTotal = responseBody.substring(tStart, tFinish - 2)+"."+
                        responseBody.substring(tFinish - 2, tFinish);
                int uStart = responseBody.indexOf("imageUrl") + "imageUrl".length()+2;
                int uFinish = responseBody.indexOf(",", uStart);
                imageURL = responseBody.substring(uStart, uFinish);
                int dStart = responseBody.indexOf("description") + "description".length()+3;
                int dFinish = responseBody.indexOf(",", dStart)-1;
                description = responseBody.substring(dStart, dFinish);
                for(int i = 0; i < purchaseNum; i++){
                    int start = responseBody.indexOf("orderTotal", tFinish) + "orderTotal".length()+2;
                    int finish= responseBody.indexOf(",", start);
                    orderTotal = responseBody.substring(start, finish - 2)+"."+
                            responseBody.substring(finish - 2, finish);
                    tFinish = finish;
                    start = responseBody.indexOf("imageUrl",uFinish) + "imageUrl".length()+2;
                    finish = responseBody.indexOf(",", start);
                    imageURL = responseBody.substring(start, finish);
                    uFinish = finish;
                    start = responseBody.indexOf("description", dFinish) + "description".length()+3;
                    finish = responseBody.indexOf(",", start)-1;
                    description = responseBody.substring(start, finish);
                    dFinish = finish;
                }
                Log.i("MainActivity", "Order total is: " + orderTotal);
                Log.i("MainActivity", "imageURL is: " + imageURL);
                Log.i("MainActivity", "description is: " + description);

                //json object to string
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }
        @Override
        public void onPostExecute(Boolean success) {

            //progress.dismiss();
            setRecentPurchase();
        }
    }


}



/*Context context = getApplicationContext();
CharSequence text = "Hello toast!";
int duration = Toast.LENGTH_SHORT;

Toast toast = Toast.makeText(context, text, duration);
toast.show();*/