package xies.academy.hebuni.com.cateringordersystem.activity;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.mock.MockContext;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import xies.academy.hebuni.com.cateringordersystem.util.InfoUtil;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        Uri uri = Uri.parse("android.resource://com.example.myApp/" + R.mipmap.h1);
        String u = uri.toString();
        Log.v("URI",u);

        BitmapDrawable bitmap = (BitmapDrawable) appContext.getResources().getDrawable(R.mipmap.user_def, null);
        String string = InfoUtil.bitmapToString(bitmap.getBitmap());
        int strLen=string.length();
        Log.v(strLen+"",string);

        assertEquals("xies.academy.hebuni.com.cateringordersystem.activity", appContext.getPackageName());
    }
    @Test
    public void his() {
        Context context = new MockContext();
        BitmapDrawable bitmap = (BitmapDrawable) context.getResources().getDrawable(R.mipmap.h1, null);
        System.out.println(InfoUtil.bitmapToString(bitmap.getBitmap()));
    }
}
