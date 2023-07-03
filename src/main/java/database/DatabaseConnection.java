/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package database;

import constants.ServerConfig;
import server.ServerProperties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

/**
 * All OdinMS servers maintain a Database Connection. This class therefore
 * "singletonices" the connection per process.
 *
 * @author Frz
 */
public class DatabaseConnection {

    public static final int CLOSE_CURRENT_RESULT = 1;
    /**
     * The constant indicating that the current <code>ResultSet</code> object
     * should not be closed when calling <code>getMoreResults</code>.
     *
     * @since 1.4
     */
    public static final int KEEP_CURRENT_RESULT = 2;
    /**
     * The constant indicating that all <code>ResultSet</code> objects that have
     * previously been kept open should be closed when calling
     * <code>getMoreResults</code>.
     *
     * @since 1.4
     */
    public static final int CLOSE_ALL_RESULTS = 3;
    /**
     * The constant indicating that a batch statement executed successfully but
     * that no count of the number of rows it affected is available.
     *
     * @since 1.4
     */
    public static final int SUCCESS_NO_INFO = -2;
    /**
     * The constant indicating that an error occured while executing a batch
     * statement.
     *
     * @since 1.4
     */
    public static final int EXECUTE_FAILED = -3;
    /**
     * The constant indicating that generated keys should be made available for
     * retrieval.
     *
     * @since 1.4
     */
    public static final int RETURN_GENERATED_KEYS = 1;
    /**
     * The constant indicating that generated keys should not be made available
     * for retrieval.
     *
     * @since 1.4
     */
    public static final int NO_GENERATED_KEYS = 2;
    private static final ThreadLocal<Connection> con = new ThreadLocalConnection();

    public static Connection getConnection() {
        Connection conn = con.get();
        try {
            if(conn == null || conn.isClosed()){
                if(conn != null)
                    conn.close();
                Properties props = new Properties();
                String database = ServerConfig.DB_NAME;
                String host = ServerConfig.DB_IP;
                String dbUser = ServerConfig.DB_USER;
                String dbPass = ServerConfig.DB_PASSWORD;
                String dbUrl = "jdbc:mysql://" + host + ":3306/" + database;//+ "?autoReconnect=true&characterEncoding=UTF8&connectTimeout=120000000";
                props.put("user", dbUser);
                props.put("password", dbPass);
                props.put("autoReconnect", "true");
                props.put("characterEncoding", "UTF8");
                props.put("connectTimeout", "2000000");
                props.put("serverTimezone", "Asia/Taipei");
                props.put("zeroDateTimeBehavior", "convertToNull");
                conn = DriverManager.getConnection(dbUrl, props);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            con.set(conn);
        }
        return conn;
    }

    public static void closeAll() throws SQLException {
        for (final Connection con : ThreadLocalConnection.allConnections) {
            if (con != null) {
                con.close();
            }
        }
    }

    private static final class ThreadLocalConnection extends ThreadLocal<Connection> {

        public static final Collection<Connection> allConnections = new LinkedList<>();

        @Override
        protected final Connection initialValue() {
            try {
                Properties props = new Properties();
                String database = ServerConfig.DB_NAME;
                String host = ServerConfig.DB_IP;
                String dbUser = ServerConfig.DB_USER;
                String dbPass = ServerConfig.DB_PASSWORD;
                String dbUrl = "jdbc:mysql://" + host + ":3306/" + database;//+ "?autoReconnect=true&characterEncoding=UTF8&connectTimeout=120000000";
                props.put("user", dbUser);
                props.put("password", dbPass);
                props.put("autoReconnect", "true");
                props.put("characterEncoding", "UTF8");
                props.put("connectTimeout", "2000000");
                props.put("serverTimezone", "Asia/Taipei");
                props.put("zeroDateTimeBehavior", "convertToNull");
                Connection con = DriverManager.getConnection(dbUrl, props);
                allConnections.add(con);
                return con;
            } catch (SQLException e) {
                System.err.println("ERROR" + e);
                return null;
            }
        }
    }

    //測試
    public enum DataBaseStatus {
        未初始化,
        連接成功,
        連接失敗
    }
    
  public static DataBaseStatus TestConnection() {
        //init();
        Connection localConnection = null;
        DataBaseStatus ret;
        try {
            localConnection = con.get();
            ret = DataBaseStatus.連接成功;
        } catch (Exception e) {
            //Start.showMessage("連接數據庫失敗", "錯誤", 1);
            //System.exit(0);
            ret = DataBaseStatus.連接失敗;
        } finally {
            try {
                if (localConnection != null) {
                    localConnection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }
}
