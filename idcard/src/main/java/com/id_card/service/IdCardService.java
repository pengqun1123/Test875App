package com.id_card.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.baselibrary.ARouter.ARouterConstrant;
import com.baselibrary.pojo.IdCard;
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

import org.greenrobot.greendao.annotation.Id;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangyu on 2019/9/17.
 */

public class IdCardService {

    private static  volatile  IdCardService instance;

    private boolean bopen = false;

    private static final int VID = 1024;    //IDR VID
    private static final int PID = 50010;     //IDR PID

    private final String idSerialName = "/dev/ttyUSB0";
    private final int idBaudrate = 115200;
    private Context mContext;
    private IDCardReader idCardReader;
    private Bitmap bitmap;

    private IdCardService(Context context) {
        this.mContext=context;
        startIDCardReader();
    }

    public static IdCardService  getInstance(Context context){
           if (instance==null){
               synchronized (IdCardService.class){
                 if (instance==null){
                     instance=new IdCardService(context);
                 }
               }
           }
           return instance;
       }

       //获取身份证信息
       public IdCard getCardInfo(){
           IdCard idCard =null;
           try {
               if (idCardReader == null) {
                   startIDCardReader();
               }
               if (!bopen) {
                   idCardReader.open(0);
                   bopen=true;
               }
               long nTickStart = System.currentTimeMillis();
               Authenticate();
               int ret = idCardReader.readCardEx(0, 1);
               long nTickRead = System.currentTimeMillis();
               if (1 == ret) {
                   IDCardInfo idCardInfo = idCardReader.getLastIDCardInfo();
                   idCard=new IdCard();
                   idCard.setName(idCardInfo.getName());
                   idCard.setId(idCardInfo.getId());
                   idCard.setNation(idCardInfo.getNation());
                   idCard.setSex(idCardInfo.getSex());
                   idCard.setBirthday(idCardInfo.getBirth());
                   if (idCardInfo.getPhotolength() > 0) {
                       byte[] buf = new byte[WLTService.imgLength];
                       nTickStart = System.currentTimeMillis();
                       if (1 == WLTService.wlt2Bmp(idCardInfo.getPhoto(), buf)) {
                           System.out.println("timeSet decode photo, use time:" + (System.currentTimeMillis() - nTickStart));
                           bitmap = IDPhotoHelper.Bgr2Bitmap(buf);
                       }
                   }
               }else if (2 == ret) {
                   idCard=new IdCard();
                   IDPRPCardInfo idprpCardInfo = idCardReader.getLastPRPIDCardInfo();
                   idCard.setName(idprpCardInfo.getCnName());
                   idCard.setId(idprpCardInfo.getId());
                   idCard.setNation(idprpCardInfo.getCountry());
                   idCard.setSex(idprpCardInfo.getSex());
                   idCard.setBirthday(idprpCardInfo.getBirth());
                   if (idprpCardInfo.getPhotolength() > 0) {
                       byte[] buf = new byte[WLTService.imgLength];
                       nTickStart = System.currentTimeMillis();
                       if (1 == WLTService.wlt2Bmp(idprpCardInfo.getPhoto(), buf)) {
                           System.out.println("timeSet decode photo, use time:" + (System.currentTimeMillis() - nTickStart));
                           bitmap = IDPhotoHelper.Bgr2Bitmap(buf);
                       }
                   }
               }
           } catch (Exception e) {
              e.printStackTrace();
            }
            return idCard;
       }


       //获取身份证物理卡号
       public String getUCardNum(){
           String cardNum=null;
           try {
               if (!bopen) {
                  idCardReader.open(0);
                  bopen=true;
               }
               byte mode = (byte)0x26;//寻卡模式 0x52一次可以操作多张卡，0x26一次只对一张卡操作
               byte halt = (byte)0x01;//0x00不需要执行halt命令，0x01读写器执行halt命令
               byte[] physicalCardNum = new byte[8];//读出的数据
               boolean ret=  idCardReader.MF_GET_NIDCardNum(0, mode, halt, physicalCardNum);
               if (ret) {
                   cardNum= ToolUtils.bytesToHexString(physicalCardNum);
                //   byte[] NIDCardNum = new byte[8];
                 //  System.arraycopy(physicalCardNum, 0, NIDCardNum, 0, 8);
                //   textView.setText("UID:"+ ToolUtils.bytesToHexString(NIDCardNum));
               }
           }
           catch (IDCardReaderException e)
           {
               LogHelper.d("获取身份证物理卡号失败, 错误码：" + e.getErrorCode() + "\n错误信息：" + e.getMessage() + "\n 内部错误码=" + e.getInternalErrorCode());
           }
           return cardNum;
       }


    //获取SAM模块编号
    public String GetSamID(View view)
    {
        String samId=null;
        try {
            if (!bopen) {
                idCardReader.open(0);
                bopen=true;
            }
            String samid = idCardReader.getSAMID(0);
        }
        catch (IDCardReaderException e)
        {
            LogHelper.d("获取SAM模块失败, 错误码：" + e.getErrorCode() + "\n错误信息：" + e.getMessage() + "\n 内部错误码=" + e.getInternalErrorCode());
        }
        return samId;
    }

    //获取身份证照片
       public Bitmap getBitmap(){
           return  bitmap;
       }

    private void Authenticate()
    {
        try {
            idCardReader.findCard(0);
            idCardReader.selectCard(0);
        } catch (IDCardReaderException e) {
            e.printStackTrace();
        }
    }

    //初始化身份证读卡器
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
        idCardReader = IDCardReaderFactory.createIDCardReader(mContext, TransportType.SERIALPORT, idrparams);
    }


}
