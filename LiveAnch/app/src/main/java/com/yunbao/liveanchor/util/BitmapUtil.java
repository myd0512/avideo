package com.yunbao.liveanchor.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.lzy.okgo.utils.IOUtils;
import com.yunbao.liveanchor.AppConfig;
import com.yunbao.liveanchor.AppContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Random;

/**
 * Created by cxf on 2018/6/22.
 */

public class BitmapUtil {

    private static BitmapUtil sInstance;
    private Resources mResources;
    private BitmapFactory.Options mOptions;

    private BitmapUtil() {
        mResources = AppContext.sInstance.getResources();
        mOptions = new BitmapFactory.Options();
        mOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        mOptions.inDither=true;
        mOptions.inSampleSize = 1;
    }

    public static BitmapUtil getInstance() {
        if (sInstance == null) {
            synchronized (BitmapUtil.class) {
                if (sInstance == null) {
                    sInstance = new BitmapUtil();
                }
            }
        }
        return sInstance;
    }


    public Bitmap decodeBitmap(int imgRes) {
        Bitmap bitmap = null;
        try {
            byte[] bytes = IOUtils.toByteArray(mResources.openRawResource(imgRes));
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, mOptions);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new SoftReference<>(bitmap).get();
    }

    /**
     * 把Bitmap保存成图片文件
     *
     * @param bitmap
     */
    public String saveBitmap(Bitmap bitmap) {
        String path = null;
        File dir = new File(AppConfig.CAMERA_IMAGE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File imageFile = new File(dir, DateFormatUtil.getCurTimeString() + ".jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            path = imageFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return path;
    }


    /**
     * 获取十六进制的颜色代码.例如  "#6E36B4" , For HTML
     */
    public static String getRandColorCode(){
        String r,g,b;
         Random random = new Random();
         r = Integer.toHexString(random.nextInt(256)).toUpperCase();
         g = Integer.toHexString(random.nextInt(256)).toUpperCase();
         b = Integer.toHexString(random.nextInt(256)).toUpperCase();
         r = r.length()==1 ? "0" + r : r ;
         g = g.length()==1 ? "0" + g : g ;
         b = b.length()==1 ? "0" + b : b ;
         return "#"+r+g+b;
    }
}
