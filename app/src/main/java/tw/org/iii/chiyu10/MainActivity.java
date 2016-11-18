package tw.org.iii.chiyu10;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

}
