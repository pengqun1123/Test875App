package com.id_card.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.baselibrary.base.BaseApplication;
import com.baselibrary.callBack.CardInfoListener;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.dao.db.DbCallBack;
import com.baselibrary.dao.db.IdCardDao;
import com.baselibrary.pojo.IdCard;
import com.baselibrary.pojo.User;
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
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by wangyu on 2019/9/17.
 */

public class IdController {

    private static  volatile IdController instance;

    private boolean bopen = false;

    private static final int VID = 1024;    //IDR VID
    private static final int PID = 50010;     //IDR PID

    private final String idSerialName = "/dev/ttyUSB0";
    private final int idBaudrate = 115200;
    private Context mContext;
    private IDCardReader idCardReader;
    private Bitmap bitmap;
    private HandlerThread handlerThread;
    private Handler mHandler;

    private int GET_CardInfo=0x001;

    private  File ImageFile;

    Disposable mDisposable;

    private IdController(Context context) {
        this.mContext=context;
        startIDCardReader();
        initHandler();
    }

    public static IdController getInstance(Context context){
           if (instance==null){
               synchronized (IdController.class){
                 if (instance==null){
                     instance=new IdController(context);
                 }
               }
           }
           return instance;
       }

       //获取身份证信息
       private void getCardInfo(ObservableEmitter<IdCard> emitter){
           IdCard idCard =null;
           try {
               if (idCardReader == null) {
                   startIDCardReader();
               }
               if (!bopen) {
                   try {
                       idCardReader.open(0);
                   } catch (IDCardReaderException e) {
                       e.printStackTrace();
                   }
                   bopen=true;
               }

               long nTickStart = System.currentTimeMillis();
               boolean card = idCardReader.findCard(2);
               Log.d("===c",card+"");
               if (card) {
                   idCardReader.selectCard(0);
                   int ret = idCardReader.readCardEx(0, 1);
                   long nTickRead = System.currentTimeMillis();
                   if (1 == ret) {
                       IDCardInfo idCardInfo = idCardReader.getLastIDCardInfo();
                       idCard = new IdCard();
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
                       emitter.onNext(idCard);

                   } else if (2 == ret) {
                       idCard = new IdCard();
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
                       emitter.onNext(idCard);
                   }
               }
           } catch (Exception e) {
               if (e instanceof IDCardReaderException){
                   IDCardReaderException  exception=(IDCardReaderException) e;
                   LogHelper.d("获取SAM模块失败, 错误码：" + exception.getErrorCode() + "\n错误信息：" + exception.getMessage() + "\n 内部错误码=" + exception.getInternalErrorCode());
               }
              e.printStackTrace();
            }

       }


       //验证
       public void  verify_IdCard(CardInfoListener cardInfoListener){

         process_IdCard(cardInfoListener,2);
       }

       //type用来区分是注册还是验证 1:表示注册 2表示验证
       private void process_IdCard(CardInfoListener cardInfoListener,int type){

           if (mHandler.hasMessages(GET_CardInfo)){
               mHandler.removeMessages(GET_CardInfo);
           }
           Observable.create(new ObservableOnSubscribe<IdCard>() {
               @Override
               public void subscribe(ObservableEmitter<IdCard> emitter) throws Exception {
                   getCardInfo(emitter);
               }
           }) .subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<IdCard>() {
               @Override
               public void onSubscribe(Disposable d) {
                   mDisposable=d;
               }

               @Override
               public void onNext(IdCard idCard) {
                   if (idCard!=null){
                       DBUtil dbUtil = BaseApplication.getDbUtil();
                       dbUtil.setDbCallBack(new DbCallBack<IdCard>() {

                           @Override
                           public void onSuccess(IdCard result) {

                           }

                           @Override
                           public void onSuccess(List<IdCard>result) {
                               try {
                                   ImageFile= bitmapToFile(bitmap);
                                   cardInfoListener.onGetCardInfo(result.get(0));
                               } catch (Exception e) {
                                   e.printStackTrace();
                               }

                           }

                           @Override
                           public void onFailed() {
                               cardInfoListener.onGetCardInfo(null);
                           }

                           @Override
                           public void onNotification(boolean result) {
                             cardInfoListener.onRegisterResult(result,idCard);
                           }
                       });
                       if (type==1) {
                           //注册
                           User user = new User();
                           dbUtil.insertAsyncSingle(idCard);
                       }else {
                           String id = idCard.getId();
                           dbUtil.queryAsync(IdCard.class, IdCardDao.Properties.Id.eq(id));
                       }
                   }
               }

               @Override
               public void onError(Throwable e) {
                 mDisposable.dispose();
               }

               @Override
               public void onComplete() {
                   mDisposable.dispose();
               }
           });

           Message obtain = Message.obtain();
           obtain.obj=cardInfoListener;
           obtain.what=GET_CardInfo;
           obtain.getData().putInt("type",type);
           mHandler.sendMessageDelayed(obtain,1000);
       }

       public void stopScanIdCard(){
           if (mHandler!=null){
               mHandler.removeMessages(GET_CardInfo);
           }
       }


       public void register_IdCard(CardInfoListener cardInfoListener){

          process_IdCard(cardInfoListener,1);

       }

       private File bitmapToFile(Bitmap bitmap) throws Exception{

               String path = Environment.getExternalStorageDirectory() + "/idcard/";
               File file = new File(path,"idcard_temp.jpg");
               if (!file.exists()){
                   file.getParentFile().mkdirs();
                   file.createNewFile();
               }
               FileOutputStream out =new FileOutputStream(file);
               bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
               out.flush();
               out.close();
               //     instance.stopScanIdCard();
               Log.d("===","保存成功！");
           return file;
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
            Log.d("===find",e.getMessage());
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

    private void initHandler() {
        handlerThread = new HandlerThread("card");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper(), mCallback);
    }

    Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what==GET_CardInfo){
                int type = message.getData().getInt("type");
                CardInfoListener cardInfoListener= (CardInfoListener) message.obj;
                if (type==1){
                    register_IdCard(cardInfoListener);
                }else if (type==2){
                    verify_IdCard(cardInfoListener);
                }

            }
            return false;
        }
    };

       public void queryAll(){
           List<IdCard> idCards = BaseApplication.getDbUtil().queryAll(IdCard.class);

       }

    public File getFile() {
        return ImageFile;
    }
}
