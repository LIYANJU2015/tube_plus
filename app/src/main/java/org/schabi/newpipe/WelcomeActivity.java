package org.schabi.newpipe;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by liyanju on 2018/4/11.
 */

public class WelcomeActivity  extends AppCompatActivity {

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.switch_service_out);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startMain();
    }

    private void startMain() {
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.switch_service_in, 0);
                finish();
            }
        }, 500);

    }
}
