/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.wztosql;

import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ServerConfig;
import constants.ServerConstants;
import database.DatabaseConnection;
import handling.login.LoginServer;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.MapleItemInformationProvider;
import server.ServerProperties;
import server.cashshop.CashItemFactory;
import server.cashshop.CashItemInfo;
import server.life.MapleMonsterInformationProvider;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 *
 * @author Flower
 */
public class CashShopDumper {

    private static final MapleDataProvider data = MapleDataProviderFactory.getDataProvider(new File("D:\\Source\\Maplestory\\piggy\\wz\\Etc.wz\\"));

    public static final CashItemInfo.CashModInfo getModInfo(int sn) {
        CashItemInfo.CashModInfo ret = null;

        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM cashshop_modified_items WHERE serial = ?")) {
            ps.setInt(1, sn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ret = new CashItemInfo.CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));

                }
            }

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return ret;
    }

    public static void main(String[] args) {
        ServerProperties.load();
        ServerConstants.SERVER_IP = ServerConfig.WORLD_INTERFACE;
        LoginServer.PORT = ServerConfig.LOGIN_PORT;

        MapleItemInformationProvider.getInstance().runEtc();
        MapleItemInformationProvider.getInstance().runItems();

        CashItemInfo.CashModInfo m = getModInfo(20000393);
        CashItemFactory.getInstance().initialize();
        Collection<CashItemInfo.CashModInfo> list = CashItemFactory.getInstance().getAllModInfo();
        Connection con = DatabaseConnection.getConnection();

        final List<Integer> itemids = new ArrayList<Integer>();
        List<Integer> qq = new ArrayList<Integer>();

        Map<Integer, List<String>> dics = new HashMap<>();

        for (MapleData field : data.getData("Commodity.img").getChildren()) {
            try {
                final int itemId = MapleDataTool.getIntConvert("ItemId", field, 0);
                final int sn = MapleDataTool.getIntConvert("SN", field, 0);
                final int count = MapleDataTool.getIntConvert("Count", field, 0);
                final int price = MapleDataTool.getIntConvert("Price", field, 0);
                final int priority = MapleDataTool.getIntConvert("Priority", field, 0);
                final int period = MapleDataTool.getIntConvert("Period", field, 0);
                final int gender = MapleDataTool.getIntConvert("Gender", field, -1);
                final int meso = MapleDataTool.getIntConvert("Meso", field, 0);
                //if(qq.contains(itemId))
                //    continue;
                if (itemId == 0) {
                    continue;
                }

                int cat = itemId / 10000;
                if (dics.get(cat) == null) {
                    dics.put(cat, new ArrayList());
                }
                boolean check = false;
                if (meso > 0) {
                    check = true;
                }
                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                    if (!MapleItemInformationProvider.getInstance().isCash(itemId)) {
                        check = true;
                    }
                }
                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                    if (period > 0) {
                        check = true;
                    }
                }

                if (check) {
                    System.out.println(MapleItemInformationProvider.getInstance().getName(itemId));
                    continue;
                }

                int isShow = 1;
                for (int i : GameConstants.cashBlock) {
                    if (itemId == i) {
                        isShow = 0;
                        break;
                    }
                }

                PreparedStatement ps = con.prepareStatement("INSERT INTO cashshop_modified_items (serial, showup,itemid,priority,period,gender,count,meso,discount_price,mark, unk_1, unk_2, unk_3, name) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                ps.setInt(1, sn);
                ps.setInt(2, isShow);
                ps.setInt(3, itemId);
                ps.setInt(4, 0);
                ps.setInt(5, period);
                ps.setInt(6, gender);
                ps.setInt(7, count > 1 ? count : 0);
                ps.setInt(8, meso);
                ps.setInt(9, price);

                qq.add(itemId);
                ps.setInt(10, 0);
                ps.setInt(11, 0);
                ps.setInt(12, 0);
                ps.setInt(13, 0);
                ps.setString(14, MapleItemInformationProvider.getInstance().getName(itemId));

                String sql = ps.toString().split(":")[1].trim() + ";";
                ps.executeUpdate();
                dics.get(cat).add("-- " + MapleItemInformationProvider.getInstance().getName(itemId) + "\n" + sql);
                ps.close();

            } catch (SQLException ex) {
                System.out.println(ex);
            }

        }

        for (Integer key : dics.keySet()) {

            File fout = new File("cashshopItems/" + key.toString() + ".sql");
            List<String> l = dics.get(key);
            FileOutputStream fos = null;
            try {
                if (!fout.exists()) {
                    fout.createNewFile();
                }
                fos = new FileOutputStream(fout);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
                for (int i = 0; i < l.size(); i++) {
                    bw.write(l.get(i));
                    bw.newLine();
                }

                bw.close();

            } catch (FileNotFoundException ex) {
                System.out.println(ex);
            } catch (IOException ex) {
                System.out.println(ex);
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }

        }

    }
}
