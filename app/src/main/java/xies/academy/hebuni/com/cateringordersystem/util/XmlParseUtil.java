package xies.academy.hebuni.com.cateringordersystem.util;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;

import xies.academy.hebuni.com.cateringordersystem.bean.UpdateBean;

public class XmlParseUtil {

    /**
     * 解析xml
     *
     * @param inputStream
     * @return
     */
    public static UpdateBean getUpdataInfo(InputStream inputStream) {
        //拿到解析器 初始化 拿到事件类型 如果是制定内容就放到bean中
        XmlPullParser parser = Xml.newPullParser();
        UpdateBean bean = new UpdateBean();
        try {
            parser.setInput(inputStream, "UTF-8");
            int type = parser.getEventType();
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("Version".equals(parser.getName())) {
                            bean.setVersion(parser.nextText());
                        } else if ("des".equals(parser.getName())) {
                            bean.setDes(parser.nextText());
                        } else if ("Url".equals(parser.getName())) {
                            bean.setApkUrl(parser.nextText());
                        }
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }
}
