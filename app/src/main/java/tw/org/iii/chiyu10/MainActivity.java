package tw.org.iii.chiyu10;

// API Guides -> Data Storage ->
// 1. 偏好設定
// 2. 內部儲存
// 3. 外部儲存 需要去 Ｍanifest 開權限

// 4. 關於權限 permission 官網有這兩篇文章
//    https://developer.android.com/guide/topics/security/permissions.html
//    https://developer.android.com/training/permissions/requesting.html

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    // 兩個核心物件
    private SharedPreferences sp;
    // 這是一個內部類別
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 這兩個物件 是所有 context 都有的
        // 所以不能 new 出來 只能用 get 取得指向
        // 只有 MODE_PRIVATE 是可以用的
        sp = getSharedPreferences("gamedate123456",MODE_PRIVATE);
        editor = sp.edit();

        // 外部儲存 的處理程序
        exSpace();

        // check permission 這只有在 Android 6.0 以上才有辦法處理 (4.0 會無視)
        myPermission();

    }

    // 這裡檢查＆丟出 權限詢問 給使用者
    // 檢查 => ContextCompat.checkSelfPermission
    // 丟出 => ActivityCompat.requestPermissions
    private void myPermission(){
        // 檢查權限是否有開
        if(ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
                    Log.v("chiyu","ASK");
                    // 如果沒有 則跳到詢問的
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE},
                            666);
        } else {AppRootInit();}
    }
    // 使用者的回答 藉由 onRequestPermissionsResult 回傳＆處理
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int grantResult : grantResults){
            if(grantResult == PackageManager.PERMISSION_GRANTED){
                Log.v("chiyu","OK");
                AppRootInit();
            } else if(grantResult == PackageManager.PERMISSION_DENIED){
                Log.v("chiyu","Denied");
            }
        }
    }

    //
    File sdroot;
    private void exSpace() {
        // 測試手機有無外部儲存空間可以存取
        String state = Environment.getExternalStorageState();
        Log.v("chiyu",state);

        if(state.equals("mounted")){
        // 取得根路徑
        sdroot = Environment.getExternalStorageDirectory();
        Log.v("chiyu",sdroot.toString() + "/n");
        Log.v("chiyu",sdroot.getAbsolutePath() + "/n");
        } else if(state.equals("removed")) {
            Log.v("chiyu","No ExternalStorage");
        }
    }
    File approot;
    private void AppRootInit() {
        approot = new File(sdroot, "Android/data/" + getPackageName());
        if (!approot.exists()) {approot.mkdirs();}
    }


    // ===== 偏好設定 =====
    // save
    public void test1(View v){
        editor.putInt("stage", 3);
        editor.putString("user","chiyu");
        editor.commit();
        Toast.makeText(this, "Done",Toast.LENGTH_SHORT).show();
    }
    // read
    public void test2(View v){
        int stage = sp.getInt("stage",0);
        String name = sp.getString("user","Guest");

        TextView tv = (TextView)findViewById(R.id.myTextView);
        tv.setText("Stage : " + stage + "\nName : " + name);
    }


    // ===== 內存 =====
    // 內存 write
    public void test3(View v) {
        try {
            Log.v("chiyu","this is save");

            // 存取在 這個 package 目錄下的 FileSystem
            // MODE_PRIVATE => 複寫
            // MODE_APPEND => 加上去
            FileOutputStream fout = openFileOutput("chiyu.data",MODE_PRIVATE);
            fout.write("Hello, Chiyu!\nHello, World\n1234567\n7654321\nabcdefg\n".getBytes());
            fout.flush();
            fout.close();

            Log.v("chiyu","Save OK");
        } catch (Exception e) {
            Log.v("chiyu",e.toString());
        }
    }
    // 內存 read
    public void test4(View v) {

        TextView tv = (TextView)findViewById(R.id.myTextView);
        tv.setText("");

        String line;

        try {
            BufferedReader myReader =
                    new BufferedReader(
                            new InputStreamReader(
                                    openFileInput("chiyu.data")));
            while ((line = myReader.readLine()) != null)  {
                tv.append(line + "\n");
            }

            myReader.close();

        } catch (Exception e) {
            Log.v("chiyu",e.toString());
        }
    }


    // ===== 外存 =====
    // 外存 write (儲存在其他的路徑)
    public void test5(View v) {
        try {
            FileOutputStream fout = new FileOutputStream(new File(sdroot, "pifile1.txt"));
            fout.write("Hello, chiyu!".getBytes());
            fout.flush();
            fout.close();
            Log.v("chiyu","Ex sdroot Write OK");

        } catch (Exception e) {
            Log.v("chiyu",e.toString());
        }
    }
    // 外存 write (儲存在與ＡＰＰ相依的路徑)
    public void test6(View v) {
        try {
            FileOutputStream fout = new FileOutputStream(new File(approot, "pifile2.txt"));
            fout.write("Hello, chiyu!".getBytes());
            fout.flush();
            fout.close();
            Log.v("chiyu","Ex spproot Write OK");

        } catch (Exception e) {
            Log.v("chiyu",e.toString());
        }
    }
    //
    public void test7(View v) {
        TextView tv = (TextView)findViewById(R.id.myTextView);
        tv.setText("");

        try {
            BufferedReader reader =
                    new BufferedReader(
                            new FileReader(
                                    new File(approot, "pifile2.txt")));

            String line;
            while ((line = reader.readLine()) != null){
                tv.append(line + "\n");
            }
            reader.close();

        } catch (Exception e) {
            Log.v("chiyu",e.toString());
        }
    }

}
