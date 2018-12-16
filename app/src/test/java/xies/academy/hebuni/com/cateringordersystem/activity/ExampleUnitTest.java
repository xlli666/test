package xies.academy.hebuni.com.cateringordersystem.activity;

import android.net.Uri;

import org.junit.Test;

import xies.academy.hebuni.com.cateringordersystem.util.InfoUtil;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void TestMD5(){
        String after = InfoUtil.EncodeMD5("123");
        System.out.println("after = " + after);
    }
    @Test
    public void StringAndBitmapTest() {
        Uri uri = Uri.parse("android.resource://com.example.myapp/" + R.mipmap.h1);
        System.out.println(uri);
        /*Context context = new MockContext();
        BitmapDrawable bitmap = (BitmapDrawable) context.getResources().getDrawable(R.mipmap.a1, null);
        System.out.println(StringAndBitmap.bitmapToString(bitmap.getBitmap()));*/
    }
}