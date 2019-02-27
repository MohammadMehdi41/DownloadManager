package com.example.admin.download;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private long reqid;
    private BroadcastReceiver receiver;
    private String title;
    private String path;
    String AUTHORITY = ".fileprovider";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button downloadButton = (Button)findViewById(R.id.button2);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        });

}

    public void viewDownloads(View view) {
        Intent i = new Intent();
        i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        startActivity(i);
    }


    public void downloadMusic(View view){
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse("https://storage.tarafdari.com/contents/user308486/content-sound/pink_floyd_-_hey_you.mp3"));
        reqid = downloadManager.enqueue(request);
    }



    public void installApk(File fileApk, String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + path)), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {


        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    String fileName = Calendar.getInstance().getTimeInMillis() + "bazaar.apk";
                    Log.i(TAG, "onRequestPermissionsResult: " + fileName);
                    String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
                    destination += fileName;
                    final Uri uri = Uri.parse("file://" + destination);


                    DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    DownloadManager.Request dr = new DownloadManager.Request(Uri.parse("http://dl.downloadhi.ir/barname-mobile/android/97/9/bazaar-7-19-2(DownloadHi.iR).apk"));
                    dr.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);




                    dr.setTitle("title");
                    //dr.setDescription(getString(R.string.download_desc));
                    dr.setDestinationUri(uri);

                    final boolean[] cancelled = {false};

                    File file = new File(destination);

                    Uri fileUri = Uri.fromFile(file);
                    if (Build.VERSION.SDK_INT >= 24) {
                        fileUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + AUTHORITY,
                                file);
                    }
                    downloadManager.enqueue(dr);

                    final Uri finalFileUri = fileUri;
                    BroadcastReceiver receiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            String action = intent.getAction();
                            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                                intent1.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                                if (Build.VERSION.SDK_INT >= 24) {
                                    intent1.setData(finalFileUri);
                                } else {
                                    intent1.setDataAndType(finalFileUri, "application/vnd.android" + ".package-archive");
                                }
                                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                startActivity(intent1);

//                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
//                    long downloadId = intent.getLongExtra(
//                            DownloadManager.EXTRA_DOWNLOAD_ID, 0);
//
//                    DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//                    DownloadManager.Query query = new DownloadManager.Query();
//                    query.setFilterById(downloadId);
//                    Cursor c = downloadManager.query(query);
//                    if (c.moveToFirst()) {
//                        String fileTitle =   c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));
//
//                        String localPath =   c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
//                        Log.i(TAG, "onReceive: "+ localPath);
//                        title = fileTitle;
//                        path = localPath;
//                        File file = new File(Uri.parse(path).getPath());
//                        Log.i(TAG, "onReceive: " + Uri.parse(path).getPath());
//
//
//                        Log.i(TAG, "onReceive: "+"file://"+"downloads"+ Uri.parse(path).getPath());
//
//                        Intent intentInstall = new Intent(Intent.ACTION_VIEW);
//                        intentInstall.setDataAndType(Uri.parse("file://"+"downloads"+ Uri.parse(path).getPath()+".apk"), "application/vnd.android.package-archive");
//                        intentInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intentInstall);
//
//                    }
                            }
                        }
                    };


                    registerReceiver(receiver,new IntentFilter(
                            DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
