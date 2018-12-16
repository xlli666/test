package xies.academy.hebuni.com.cateringordersystem.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DBUtil {

    public static Connection openConnection() {
        Connection conn = null;
        try {
            Class.forName(ParamUtil.DRIVER_NAME);
            conn = DriverManager.getConnection(ParamUtil.DB_URL, ParamUtil.USER, ParamUtil.PWD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static Connection openConnection(String url, String userName, String passWord) {
        Connection conn = null;
        try {
            Class.forName(ParamUtil.DRIVER_NAME);
            conn = DriverManager.getConnection(url, userName, passWord);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void closeConnection(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据库查询
     *
     * @param conn   连接
     * @param sql    查询语句
     * @param params where条件参数
     * @return 查询结果--JSON串
     */
    public static JSONObject query(Connection conn, String sql, String[] params) {
        if (conn == null) {
            return null;
        }
        JSONArray jsonArray = new JSONArray();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
            rs = ps.executeQuery();
            if (rs != null && rs.first()) {
                while (!rs.isAfterLast()) {
                    JSONObject jsonObject = new JSONObject();
                    ResultSetMetaData rltMetaData = rs.getMetaData();
                    for (int i = 0; i < rltMetaData.getColumnCount(); i++) {
                        rltMetaData.getColumnName(i + 1);
                        rs.getString(i + 1);
                        try {
                            jsonObject.put(rltMetaData.getColumnName(i + 1), rs.getString(i + 1));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    jsonArray.put(jsonObject);
                    rs.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        JSONObject result = new JSONObject();
        try {
            if (jsonArray.length() == 0) {
                result.put("isOK", false);
                result.put("results", jsonArray);
            } else {
                result.put("isOK", true);
                result.put("results", jsonArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 数据库添加
     *
     * @param conn   连接
     * @param sql    查询语句
     * @param params 添加的数据
     * @return 查询结果--JSON串
     */
    public static JSONObject insert(Connection conn, String sql, String[] params) {
        if (conn == null) {
            return null;
        }
        PreparedStatement ps = null;
        int rs = -1;

        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
            rs = ps.executeUpdate();
//            ps.addBatch();
//            int[] rs =ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        JSONObject result = new JSONObject();
        try {
            if (rs < 0) {
                result.put("isOK", false);
                result.put("results", rs);
            } else {
                result.put("isOK", true);
                result.put("results", rs);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 数据库更新
     *
     * @param conn   连接
     * @param sql    查询语句
     * @param params 修改的数据及条件
     * @return 查询结果--JSON串
     */
    public static JSONObject update(Connection conn, String sql, String[] params) {
        if (conn == null) {
            return null;
        }
        PreparedStatement ps = null;
        int rs = -1;

        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
            rs = ps.executeUpdate();
//            ps.addBatch();
//            int[] rs =ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        JSONObject result = new JSONObject();
        try {
            if (rs < 0) {
                result.put("isOK", false);
                result.put("results", rs);
            } else {
                result.put("isOK", true);
                result.put("results", rs);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
