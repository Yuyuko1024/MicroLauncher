package org.exthmui.microlauncher.duoqin.activity;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.exthmui.microlauncher.duoqin.R;


/**
 * 此方法将调用系统的KeyguardManager使用锁屏密码进行验证
 * @author Yuyuko1024
 */
public class KeyguardVerificationActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;

    private static final String TAG = "VerificationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (km != null){
            String title = getString(R.string.keyguard_title);
            String desc = getString(R.string.keyguard_description);
            Intent intent = km.createConfirmDeviceCredentialIntent(title,desc);
            if (intent == null){
                Log.d(TAG, "该设备没有密码/PIN保护。");
                openExtraToolbox();
                finish();
            }else{
                startActivityForResult(intent, REQUEST_CODE);
            }
        }else{
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            openExtraToolbox();
        }
        finish();
    }

    private void openExtraToolbox(){
        Intent intent = new Intent();
        intent.setClass(KeyguardVerificationActivity.this,AppList3rdActivity.class);
        intent.putExtra("result",true);
        startActivity(intent);
    }

}
