package com.id_card.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baselibrary.pojo.IdCard;
import com.id_card.R;
import com.id_card.service.IdCardService;
import com.zkteco.android.IDReader.IDPhotoHelper;
import com.zkteco.android.IDReader.WLTService;
import com.zkteco.android.biometric.core.device.ParameterHelper;
import com.zkteco.android.biometric.core.device.TransportType;
import com.zkteco.android.biometric.core.utils.LogHelper;
import com.zkteco.android.biometric.core.utils.ToolUtils;
import com.zkteco.android.biometric.module.idcard.IDCardReader;
import com.zkteco.android.biometric.module.idcard.IDCardReaderFactory;
import com.zkteco.android.biometric.module.idcard.exception.IDCardReaderException;
import com.zkteco.android.biometric.module.idcard.meta.IDCardInfo;
import com.zkteco.android.biometric.module.idcard.meta.IDPRPCardInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created By pq
 * on 2019/9/14
 */
public class IDCardActivity extends AppCompatActivity {

    private static final int VID = 1024;    //IDR VID
    private static final int PID = 50010;     //IDR PID

    private final String idSerialName = "/dev/ttyUSB0";
    private final int idBaudrate = 115200;

    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    /**
     * 所需的所有权限信息
     */
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE

    };
    private IDCardReader idCardReader = null;
    private TextView textView = null;
    private ImageView imageView = null;
    private boolean bopen = false;
    private IdCardService instance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.id_card_activity_id_card);
        textView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView)findViewById(R.id.imageView);
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        }else {
            instance = IdCardService.getInstance(this);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            boolean isAllGranted = true;
            for (int grantResult : grantResults) {
                isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            if (isAllGranted) {
               startIDCardReader();
            } else {
                Toast.makeText(this, R.string.id_card_permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startIDCardReader() {
        // Define output log level
        LogHelper.setLevel(Log.VERBOSE);
        // Start fingerprint sensor
        Map idrparams = new HashMap();



       /* idrparams.put(ParameterHelper.PARAM_KEY_VID, VID);
        idrparams.put(ParameterHelper.PARAM_KEY_PID, PID);
        idCardReader = IDCardReaderFactory.createIDCardReader(this, TransportType.USB, idrparams);*/


        idrparams.put(ParameterHelper.PARAM_SERIAL_SERIALNAME, idSerialName);
        idrparams.put(ParameterHelper.PARAM_SERIAL_BAUDRATE, idBaudrate);
        idCardReader = IDCardReaderFactory.createIDCardReader(this, TransportType.SERIALPORT, idrparams);
        byte [] dataVersion=new byte[50];
        try {
            boolean versionNum = idCardReader.Get_VersionNum(0, dataVersion);
            if (versionNum){
                Log.d("===",String.valueOf(dataVersion));
            }
        } catch (IDCardReaderException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Destroy fingerprint sensor when it's not used
        IDCardReaderFactory.destroy(idCardReader);
    }

    public void OnBnOpen(View view)
    {
        try {
            if (bopen) return;
            idCardReader.open(0);
            bopen = true;
            textView.setText("连接设备成功");
        }
        catch (IDCardReaderException e)
        {
            textView.setText("关闭设备成功");
            LogHelper.d("连接设备失败, 错误码：" + e.getErrorCode() + "\n错误信息：" + e.getMessage() + "\n 内部错误码=" + e.getInternalErrorCode());
        }
    }

    private boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    public void OnBnClose(View view)
    {
        try {
            if (bopen)
            {
                idCardReader.close(0);
                bopen = false;
            }
            textView.setText("关闭设备成功");
        }
        catch (IDCardReaderException e)
        {
            textView.setText("关闭设备失败");
            LogHelper.d("关闭设备失败, 错误码：" + e.getErrorCode() + "\n错误信息：" + e.getMessage() + "\n 内部错误码=" + e.getInternalErrorCode());
        }
    }

    public void OnBnGetSamID(View view)
    {
        try {
            if (!bopen) {
                textView.setText("请先连接设备");
            }
            String samid = idCardReader.getSAMID(0);
            textView.setText("获取SAM编号成功："+ samid);
        }
        catch (IDCardReaderException e)
        {
            textView.setText("获取SAM编号失败");
            LogHelper.d("获取SAM模块失败, 错误码：" + e.getErrorCode() + "\n错误信息：" + e.getMessage() + "\n 内部错误码=" + e.getInternalErrorCode());
        }
    }

    public void OnBnUID(View view)
    {
        String uCardNum = instance.getUCardNum();
        if (uCardNum!=null){
            textView.setText(uCardNum);
        }else{
            textView.setText("获取身份证物理卡号失败");
        }
//        try {
//            if (!bopen) {
//                textView.setText("请先连接设备");
//            }
//            byte mode = (byte)0x52;//寻卡模式 0x52一次可以操作多张卡，0x26一次只对一张卡操作
//            byte halt = (byte)0x01;//0x00不需要执行halt命令，0x01读写器执行halt命令
//            byte[] physicalCardNum = new byte[1024];//读出的数据
//            boolean ret=  idCardReader.MF_GET_NIDCardNum(index, mode, halt, physicalCardNum);
//            if (ret) {
//                byte[] NIDCardNum = new byte[8];
//                System.arraycopy(physicalCardNum, 0, NIDCardNum, 0, 8);
//                textView.setText("UID:"+ ToolUtils.bytesToHexString(NIDCardNum));
//            }
//        }
//        catch (IDCardReaderException e)
//        {
//            textView.setText("获取身份证物理卡号失败");
//            LogHelper.d("获取身份证物理卡号失败, 错误码：" + e.getErrorCode() + "\n错误信息：" + e.getMessage() + "\n 内部错误码=" + e.getInternalErrorCode());
//        }
    }

    private void showMessage(String string)
    {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage(string)
                .setPositiveButton( "确定" ,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialoginterface, int i){
                            }
                        }).show();
    }

    private  int index = 0;
    public   String printHexString( byte[] b) {
        String a = "";
        for (int i = 0; i < 1024; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }

            a = a+hex;
        }

        return a;
    }

    public void Authenticate()
    {
        try {
            idCardReader.findCard(0);
            idCardReader.selectCard(0);
        } catch (IDCardReaderException e) {
            e.printStackTrace();
        }
    }

    public void OnBnRead(View view)
    {
        IdCard cardInfo = instance.getCardInfo();
        if (cardInfo!=null) {
            textView.setText("姓名：" + cardInfo.getName() + "，民族：" + cardInfo.getNation() + "，,身份证号：" + cardInfo.getId());
            imageView.setImageBitmap(instance.getBitmap());
        }else {
            textView.setText("获取信息失败!");
        }
//        try {
//            if (!bopen) {
//                textView.setText("请先连接设备");
//            }
//
//            long nTickStart = System.currentTimeMillis();
//            Authenticate();
//            int ret = idCardReader.readCardEx(0, 1);
////            new Thread(new Runnable() {
////                @Override
////                public void run() {
////
////                }
////            });
//
//            long nTickRead = System.currentTimeMillis();
//            if (1 == ret)
//            {
//                IDCardInfo idCardInfo = idCardReader.getLastIDCardInfo();
//                textView.setText("timeSet:"+ (nTickRead-nTickStart) + "姓名：" + idCardInfo.getName() + "，民族：" + idCardInfo.getNation() + "，住址：" + idCardInfo.getAddress() + ",身份证号：" + idCardInfo.getId());
//                if (idCardInfo.getPhotolength() > 0){
//                    byte[] buf = new byte[WLTService.imgLength];
//                    nTickStart = System.currentTimeMillis();
//                    if(1 == WLTService.wlt2Bmp(idCardInfo.getPhoto(), buf))
//                    {
//                        System.out.println("timeSet decode photo, use time:" + (System.currentTimeMillis() - nTickStart));
//                        Bitmap bitmap = IDPhotoHelper.Bgr2Bitmap(buf);
//                        int byteCount = bitmap.getByteCount();
//                        Log.d("===byte",byteCount+"");
//                        imageView.setImageBitmap(bitmap);
//                        try {
//                            String s = Environment.getExternalStorageDirectory() + "/idcard/";
//                            File file = new File(s,"ttt.jpg");
//                            if (!file.exists()){
//                                file.getParentFile().mkdirs();
//                                file.createNewFile();
//                            }
//                            FileOutputStream out =new FileOutputStream(file);
//                            out.write(buf);
//                           // bitmap.compress(Bitmap.CompressFormat.JPEG,100,buf);
//                            out.flush();
//                            out.close();
//                            Log.d("===","保存成功！");
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//            else if (2 == ret)
//            {
//                IDPRPCardInfo idprpCardInfo = idCardReader.getLastPRPIDCardInfo();
//                textView.setText("timeSet:"+ (nTickRead-nTickStart) + "英文名：" + idprpCardInfo.getEnName() + "，中文名：" + idprpCardInfo.getCnName() +
//                        "，国家：" + idprpCardInfo.getCountry() +  ",证件号：" + idprpCardInfo.getId());
//                if (idprpCardInfo.getPhotolength() > 0){
//                    byte[] buf = new byte[WLTService.imgLength];
//                    nTickStart = System.currentTimeMillis();
//                    if(1 == WLTService.wlt2Bmp(idprpCardInfo.getPhoto(), buf))
//                    {
//                        System.out.println("timeSet decode photo, use time:" + (System.currentTimeMillis() - nTickStart));
//                        imageView.setImageBitmap(IDPhotoHelper.Bgr2Bitmap(buf));
//                    }
//                }
//            }
//        }
//        catch (IDCardReaderException e)
//        {
//            textView.setText("读卡失败");
//            LogHelper.d("读卡失败, 错误码：" + e.getErrorCode() + "\n错误信息：" + e.getMessage() + "\n 内部错误码=" + e.getInternalErrorCode());
//        }
    }
}
