package com.microsoft.projectoxford.visionsample;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.LanguageCodes;
import com.microsoft.projectoxford.vision.contract.Line;
import com.microsoft.projectoxford.vision.contract.OCR;
import com.microsoft.projectoxford.vision.contract.Region;
import com.microsoft.projectoxford.vision.contract.Word;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

public class RecognizeActivity extends ActionBarActivity {
    private String getSubscriptKey() {
        Log.d("ERROR", "TODO: Please set correct subscript key.");
        throw new UnsupportedOperationException("TODO: Please set correct subscript key.");
    }

    private VisionServiceClient client = new VisionServiceRestClient(this.getSubscriptKey());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize);

        EditText mEdit = (EditText) findViewById(R.id.editTextUrl);
        mEdit.setText("http://www.idautomation.com/ocr-a-and-ocr-b-fonts/new_sizes_ocr.png");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recognize, menu);
        return true;
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

    public void DoUrl(View v) throws VisionServiceException {
        new doRequest().execute();
    }

    private String process() throws VisionServiceException {
        String result = "";
        Gson gson = new Gson();
        EditText mEdit = (EditText) findViewById(R.id.editTextUrl);
        String url = mEdit.getText().toString();
        OCR ocr = this.client.recognizeText(url, LanguageCodes.AutoDetect, true);
        result = gson.toJson(ocr);
        Log.d("result", result);
        return result;
    }

    private class doRequest extends AsyncTask<String, String, String> {
        // Store error message
        private Exception e = null;

        public doRequest() {
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                return process();
            } catch (Exception e) {
                this.e = e;    // Store error
            }

            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            // Display based on error existence
            TextView v = (TextView) findViewById(R.id.editTextResult);

            if (e != null) {
                v.setText("Error: " + e.getMessage());
                this.e = null;
            } else {
                Gson gson = new Gson();
                OCR r = gson.fromJson(data, OCR.class);

                String result = "";
                for (Region reg : r.regions) {
                    for (Line line : reg.lines) {
                        for (Word word : line.words) {
                            result += word.text;
                        }
                        result += "\n";
                    }
                    result += "\n\n";
                }

                v.setText(result);
            }
        }
    }
}
