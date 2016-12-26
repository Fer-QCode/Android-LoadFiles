package mx.qcode.testfilecamera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 0x5;
    private static final int READ_EXST = 0x4;
    private ImageView imageView;
    private TextView textView;
    private Context context;
    private Camera mCamera;
    private static View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView2);
        Button button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.view = view;
                askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXST);

            }
        });

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.view = view;
                askForPermission(Manifest.permission.CAMERA, MY_PERMISSIONS_REQUEST_CAMERA);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainActivity.view = view;
                askForPermission(Manifest.permission.CAMERA, MY_PERMISSIONS_REQUEST_CAMERA);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Snackbar.make(view, "requestCode:: " + requestCode, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        if(requestCode == MY_PERMISSIONS_REQUEST_CAMERA){
            if(data != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(bitmap);
            } else {
                Snackbar.make(view, "No picture taken... ", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }

        }
        if(requestCode == READ_EXST){

            // Get the Uri of the selec
            // ted file
            if(data != null) {
                Uri uri = data.getData();
                String uriString = uri.toString();
                File xmlFile = new File(uriString); //Here is the XML File
                Log.e(TAG, "isFile:: " + xmlFile.isFile());
                String path = xmlFile.getAbsolutePath();
                Log.e(TAG, "path:: " + path);
                String displayName = null;
                Log.e(TAG, uriString.toString());
                if (uriString.startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = this.getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            //Log.e(TAG, "size:: " + cursor.getString(cursor.getColumnIndex(OpenableColumns.SIZE)));
                        }
                    } finally {
                        cursor.close();
                    }
                } else if (uriString.startsWith("file://")) {
                    displayName = xmlFile.getName();
                }

                textView.setText(displayName);
                Snackbar.make(view, "File Name:: " + displayName, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


                File source = new File("/sdcard/" + "Download" + "/" + displayName);


                try {

                    File xmlFilePath = new File(source.getCanonicalPath().toString());
                    boolean unfile = xmlFilePath.isFile();
                    boolean Esiste = xmlFilePath.exists();
                    Log.e(TAG, xmlFilePath.toString());
                    Log.e(TAG, "unfile:: " + unfile + ", Esiste: " + Esiste);

                    textView.setText(source.getCanonicalPath().toString());
                    textView.setTextSize(10f);


                    Reader pr;
                    String line = "";
                    try {
                        pr = new FileReader(xmlFilePath);
                        int d = pr.read();
                        while (d != -1) {
                            line += (char) d;
                            d = pr.read();
                        }
                        pr.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //System.out.println(getXml(source.getCanonicalPath().toString()));
                    /*** Print XML Detail ***/
                    //System.out.println(line);
                    TextView xmlDetail = (TextView) findViewById(R.id.textView4);
                    xmlDetail.setText(line);
                    xmlDetail.setMaxLines(line.length());
                    xmlDetail.setMovementMethod(new ScrollingMovementMethod());
                    //Log.e(TAG, "XML doc::\n" + doc.get);

                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());

                }
            }else {
                Snackbar.make(view, "No file selected... ", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Function used to fetch an XML file from assets folder
     * @param fileName - XML file name to convert it to String
     * @return - return XML in String form
     */
    private String getXml(String fileName) {
        String xmlString = null;
        AssetManager am = context.getAssets();
        try {
            InputStream is = am.open(fileName);
            int length = is.available();
            byte[] data = new byte[length];
            is.read(data);
            xmlString = new String(data);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return xmlString;
    }

    private void askForPermission(String permission, Integer requestCode) {
        //Log.e(TAG, "permission:: " + permission + ", requestCode:: " + requestCode);
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            Snackbar.make(view, "" + permission + " is already granted.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            switch (requestCode){
                case READ_EXST:
                    openFolder();
                    break;
                case MY_PERMISSIONS_REQUEST_CAMERA:
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, requestCode);
                    }
                    break;

            }
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {
                //Location
                case 1:
                    //askForGPS();
                    break;
                //Call
                case 2:
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + "{This is a telephone number}"));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        startActivity(callIntent);
                    }
                    break;
                //Write external Storage
                case 3:
                    break;
                //Read External Storage
                case 4:
                    /*Intent imageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(imageIntent, 11);*/
                    openFolder();
                    break;
                //Camera
                case 5:
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, 12);
                    }
                    break;
                //Accounts
                case 6:
                    /*AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
                    Account[] list = manager.getAccounts();
                    Toast.makeText(this,""+list[0].name,Toast.LENGTH_SHORT).show();
                    for(int i=0; i<list.length;i++){
                        Log.e("Account "+i,""+list[i].name);
                    }*/
                    break;
            }

            //Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            Snackbar.make(view, "Permission granted", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

        }else{
            Snackbar.make(view, "Permission denied", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            //Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    public void openFolder(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                + "/sdcard/Download");
        intent.setDataAndType(uri, "text/xml");
        //startActivity(Intent.createChooser(intent, "Open folder"));
        startActivityForResult(Intent.createChooser(intent, "Open folder"), READ_EXST);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
