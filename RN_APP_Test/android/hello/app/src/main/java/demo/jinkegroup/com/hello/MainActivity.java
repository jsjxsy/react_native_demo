package demo.jinkegroup.com.hello;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.wpt.bsdiff.BSDiff;
import com.wpt.rn.activity.SingleActivity;
import com.wpt.rn.update.HotUpdate;

import java.io.File;

public class MainActivity extends AppCompatActivity {


    private long mDownLoadId;
    private CompleteReceiver localReceiver;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SingleActivity.class);
                startActivity(intent);
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        registerReceiver();

        //动态请求的权限数组
        String[] permissions =new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
//测试
        Log.i("checkSelfPermission", "--------------------------------"+permissions.length);
        for (String permission : permissions) {
            int isGranted = ContextCompat.checkSelfPermission(this, permission);
            if (isGranted == PackageManager.PERMISSION_GRANTED) {
                //已授权

                Log.i("checkSelfPermission","checkSelfPermission  已授权");
            }else if(isGranted == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
                //未授权的
//                Log.i("checkSelfPermission", PermissionUtils.getInstance().getPermissionName(permission)+ ":   未授权");
            }
        }
        Log.i("checkSelfPermission", "--------------------------------"+permissions.length);

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
            String oldApkPath = Environment.getExternalStorageDirectory().toString()
                    + File.separator + "com.weipaitang.wpt" + File.separator + "1.txt";
            String newApkPath = Environment.getExternalStorageDirectory().toString()
                    + File.separator + "com.weipaitang.wpt" + File.separator + "2.txt";
            String patchPath = Environment.getExternalStorageDirectory().toString()
                    + File.separator + "com.weipaitang.wpt" + File.separator + "3.patch";

            int result = BSDiff.patch(oldApkPath, newApkPath, patchPath);
            Log.e("xsy", "result=" + result);
            //checkVersion();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * 注册广播
     */
    private void registerReceiver() {
        localReceiver = new CompleteReceiver();
        registerReceiver(localReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public class CompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long completeId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (completeId == mDownLoadId) {
                HotUpdate.handleZIP(getApplicationContext());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:

                if (grantResults != null && grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "WRITE_CONTACTS Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(localReceiver);
    }

}
