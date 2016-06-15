package com.george.assignment4threaddatabase;




import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.george.assignment4threaddatabase.Database.Person;
import com.george.assignment4threaddatabase.Database.sqlDatabase;
import com.george.assignment4threaddatabase.ViewPager.ViewActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class MainActivity extends AppCompatActivity {

    private EditText ET_URL;
    sqlDatabase db = new sqlDatabase(this);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ET_URL = (EditText) findViewById(R.id.URLinputText);




        Button button1 = (Button) findViewById(R.id.Search);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Person> list;
                list = db.getAllPerson();
                if(list.size() != 0){
                Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
                startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(), "Empty", Toast.LENGTH_SHORT).show();
                }
            }

        });
        Button button2 = (Button) findViewById(R.id.Populate);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //String serverURL = "http://www.eecg.utoronto.ca/~jayar/PeopleList.txt";
                new LongOperation().execute(ET_URL.getText().toString());

            }
        });

        Button button3 = (Button) findViewById(R.id.Clear);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Person> list;
                list = db.getAllPerson();

                for (int i = 0; i < list.size(); i++) {

                    deletePicture(list.get(i));
                    db.deletePerson(list.get(i));
                }

            }
        });






    }




    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    private class LongOperation  extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);
        private String baseURL ="";



        TextView uiUpdate = (TextView) findViewById(R.id.output);

        protected void onPreExecute() {

            uiUpdate.setText("Output : ");
            Dialog.setMessage("Downloading source..");
            Dialog.show();
        }


        protected Void doInBackground(String... urls) {
            try {


                if(urls[0].length() < 15){
                    Error = "Input incorrect too short";
                    return null;
                }

                if(Objects.equals(urls[0].substring(0, 11), "http://www.") == false){

                   Error = "Input does not start with \"http://www.\"";
                    return null;
                }
                if(Objects.equals(urls[0].substring(urls[0].length()-4,urls[0].length()), ".txt") == false){
                    Error = "Input does not end with \".txt\"";
                    Error = urls[0].substring(urls[0].length()-4,urls[0].length());
                    return null;
                }


                HttpGet httpget = new HttpGet(urls[0]);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                Content = Client.execute(httpget, responseHandler);
                Log.d("databaser", urls[0]);


                baseURL = urls[0].substring(0 ,1 + urls[0].lastIndexOf("/"));
                Log.d("databaser", baseURL);
                updateDatabase(Content);


            } catch (ClientProtocolException e) {
                Error = e.getMessage();
                cancel(true);
            } catch (IOException e) {
                Error = e.getMessage();
                cancel(true);
            }

            return null;
        }

        protected void onPostExecute(Void unused) {

            Dialog.dismiss();

            if (Error != null) {

                uiUpdate.setText("Output : " + Error);


            } else {

                uiUpdate.setText("Output : Finished Populating");


            }
        }


        private void updateDatabase(String Content) {



            Scanner scanner = new Scanner(Content);
            String scannedLine = "";
            int count = 1;
            String name = "";
            String picture = "";
            String description = "";




            while (scanner.hasNextLine()) {
                scannedLine = scanner.nextLine();

                if (count % 3 == 1) {
                    name = scannedLine;

                } else if (count % 3 == 2) {
                    description = scannedLine;

                } else if (count % 3 == 0) {

                    if(scannedLine.length()<4){
                        Error = "Format of picture url is incorrect";
                        return;
                    }
                    if(Objects.equals(scannedLine.substring(scannedLine.length()-4,scannedLine.length()), ".jpg") == false){
                        Error = "Input does not end with \".jpg\"";
                        return;
                    }


                    picture = downloadPicture(scannedLine, name);

                    if(name == null || description == null || picture ==null){
                        Error = "Format of txt is incorrect";
                        return;
                    }




                    db.createPerson(new Person(name, picture, description));
                    name=null;
                    description=null;
                    picture=null;

                }



                count = count + 1;
            }

            scanner.close();
        }



        private String downloadPicture(String scannedLine, String name) {
            FileOutputStream outStream = null;

            try {


                final Bitmap downloadBitmap = downloadBitmap(baseURL + scannedLine);

                Log.d("databaser", baseURL + scannedLine);

                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/george998579943DossierPic");
                dir.mkdirs();


                String fileName = name + ".jpg";
                File outFile = new File(dir, fileName);

                Log.d("databaser", dir.getAbsolutePath());
                Log.d("databaser", fileName);

                outStream = new FileOutputStream(outFile);


                downloadBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);


                outStream.flush();
                outStream.close();
                refreshGallery(outFile);

                return (dir.getAbsolutePath() + "/" + fileName);


            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }

            return "";
        }


        private Bitmap downloadBitmap(String url) throws IOException {
            HttpUriRequest request = new HttpGet(url.toString());
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);

            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                byte[] bytes = EntityUtils.toByteArray(entity);

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,
                        bytes.length);

                return bitmap;
            } else {
                throw new IOException("Download failed, HTTP response code "
                        + statusCode + " - " + statusLine.getReasonPhrase());
            }
        }




    }


    private void deletePicture(Person deletePerson){

        Intent i = getIntent();


        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/george998579943DossierPic");

        String filename = deletePerson.getName() + ".jpg";
        File myFile = new File(dir, filename);

        myFile.delete();
        refreshGallery(myFile);

    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }


}
