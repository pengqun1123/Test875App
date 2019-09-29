package com.face.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.face.R;

/**
 * 主流程界面
 * <p>
 * 其他示例的入口
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn1c1;
    private Button btnSingleCamera;
    private Button btnDualCamera;
    private Button btnRegister;
    private Button btnDirectCameraDemo;
    private Button btnHalmet;
    private Button btnV3Rec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_activity_main);
        btn1c1 = (Button) findViewById(R.id.btn_1c1);
        btnSingleCamera = (Button) findViewById(R.id.btn_single_camera);
        btnDualCamera = (Button) findViewById(R.id.btn_dual_camera);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnDirectCameraDemo = (Button) findViewById(R.id.btn_direct_demo);
        btnHalmet = (Button) findViewById(R.id.btn_safety_halmet);
        btnV3Rec = (Button) findViewById(R.id.btn_v3_rec);
        btn1c1.setOnClickListener(this);
        btnSingleCamera.setOnClickListener(this);
        btnDualCamera.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnDirectCameraDemo.setOnClickListener(this);
        btnHalmet.setOnClickListener(this);
        btnV3Rec.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_1c1:
                startActivity(new Intent(MainActivity.this, CompareTwoActivity.class));
                break;
            case R.id.btn_single_camera:
                startActivity(new Intent(MainActivity.this, SingleCameraRecActivity.class));
                break;
            case R.id.btn_dual_camera:
             //   startActivity(new Intent(MainActivity.this, DualCameraRecActivity.class));
                break;
            case R.id.btn_register:
                startActivity(new Intent(MainActivity.this, UserListActivity.class));
                break;
            case R.id.btn_direct_demo:
            //    startActivity(new Intent(MainActivity.this, DirectCameraDetectActivity.class));
                break;
            case R.id.btn_safety_halmet:
            //    startActivity(new Intent(MainActivity.this, SafetyHelmetDetectActivity.class));
                break;
            case R.id.btn_v3_rec:
                startActivity(new Intent(MainActivity.this, V3FaceRecActivity.class));
                break;
            default:
                break;
        }
    }
}
