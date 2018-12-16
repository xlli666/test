package xies.academy.hebuni.com.cateringordersystem.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

import xies.academy.hebuni.com.cateringordersystem.bean.UpdateBean;

public class InfoUtil {
    /**
     * MD5加密
     *
     * @param before 加密前字符串
     * @return 加密后字符串
     */
    public static String EncodeMD5(String before) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(before.getBytes());
            byte[] tempArr = digest.digest();
            int len = tempArr.length;
            StringBuilder strBul = new StringBuilder(len << 1);
            for (byte temp : tempArr) {
                strBul.append(Character.forDigit((temp & 0xf0) >> 4, 16));
                strBul.append(Character.forDigit(temp & 0x0f, 16));
            }
            return strBul.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 字符串转Bitmap
     *
     * @param string 字符串
     * @return 转换结果
     */
    public static Bitmap stringToBitmap(String string) {
        if (string != null && "".equals(string)) {
            byte[] bytes = Base64.decode(string, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } else {
            return null;
        }
    }

    /**
     * Bitmap转字符串
     *
     * @param bitmap Bitmap
     * @return 转换结果
     */
    public static String bitmapToString(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytes = stream.toByteArray();
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } else {
            return "";
        }
    }

    /**
     * 存储Bitmap 返回存储路径
     * @param bitmap Bitmap数据
     * @return 存储路径
     */
    public static String bitmapFilePath(Bitmap bitmap) {
        File file = new File(Environment.getExternalStorageDirectory(),System.currentTimeMillis() + ".jpg");
        try {
            OutputStream os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    /**
     * 当前时间获取
     *
     * @return 时间字符串(YYYYMMDDHHMMSS)
     */
    public static String getCurrTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmddHHmmss", Locale.CHINA);
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    /**
     * 总价格计算
     *
     * @param jsonArray 购物车数据
     * @return 计算结果
     */
    public static String totalPrice(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return "0.0";
        }
        double totalPrice = 0.0;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            String cartPrice = String.format(Locale.CHINA, "%.2f", jsonObject.optDouble("CATE_NUM") * jsonObject.optDouble("PRICE"));
            totalPrice += Double.valueOf(cartPrice);
        }
        return String.format(Locale.CHINA, "%.2f", totalPrice);
    }

    /**
     * 二维码图像数据生成
     *
     * @param qrContent 二维码内容
     * @return 图像信息
     */
    public static Bitmap createQRImg(String qrContent) {
        int QR_WIDTH = 200, QR_HEIGHT = 200;
        if (qrContent == null || "".equals(qrContent) || qrContent.length() < 1) {
            return null;
        }
        Hashtable<EncodeHintType, String> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(qrContent, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取版本Name
     *
     * @param context 上下文
     * @return 版本Name
     */
    public static String getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();// 拿到包的管理器
        try {
            // 封装了所有的功能清单中的数据
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取版本Code
     *
     * @param context 上下文
     * @return 版本Code
     */
    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();// 拿到包的管理器
        try {
            // 封装了所有的功能清单中的数据
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取服务数据
     *
     * @return APP信息
     */
    public static UpdateBean getServiceInfo() {
        UpdateBean bean = new UpdateBean();
        try {
            URL url = new URL(ParamUtil.SV_URL + "upgradeApp.xml");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            // 打开输入流
            con.setDoInput(true);
            con.setRequestMethod("GET");
            con.setConnectTimeout(3000);
            if (con.getResponseCode() == 200) {
                bean = XmlParseUtil.getUpdataInfo(con.getInputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }
//    private static Dialog loadingDialog;
//    public static Dialog showProgress(String text, int isCloseSelf, Context context) {
//            LayoutInflater inflater = LayoutInflater.from(context);
//        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
//        v.setFocusableInTouchMode(true);
//        TextView tipTextView = v.findViewById(R.id.tipTextView);// 提示文字
//        if (text != null && !text.equals("")) {
//            tipTextView.setText(text);// 设置加载文字
//        }
//        if (loadingDialog == null) {
//            loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
//            loadingDialog.setCanceledOnTouchOutside(false);// 点击对话框外边界不消失
//            if (isCloseSelf == 0) {
//                loadingDialog.setCancelable(false);// 不可以用“返回键”取消
//            } else {
//                loadingDialog.setCancelable(true);// 可以用“返回键”取消
//            }
//            loadingDialog.setContentView(v, new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
//            loadingDialog.show();
//        } else {
//            loadingDialog.show();
//            v.setOnKeyListener(new View.OnKeyListener() {
//
//                @Override
//                public boolean onKey(View v, int keyCode, KeyEvent event) {
//                    System.out
//                            .println("===========================loadingDialog==========================");
//                    loadingDialog.dismiss();
//                    return false;
//                }
//            });
//        }
//        return loadingDialog;
//    }

}
