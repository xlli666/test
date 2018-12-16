package xies.academy.hebuni.com.cateringordersystem.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {
    public static void SHOW(Context context, String message, boolean isLong) {
        if (message == null)
            message = "";
        Toast toast;
        if (isLong) {
            toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        } else {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
