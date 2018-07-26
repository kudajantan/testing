package login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.seseorang.demos.R;


/**
 * Created by Rius on 7/16/2016.
 */
public class SplashScreen extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);

        /*menjalankan splash screen dan menu menggunakan delayed thread*/
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent mainMenu= new Intent(SplashScreen.this,LoginActivity.class);
                startActivity(mainMenu);
                finish();
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        }, 3000);

    }
}
