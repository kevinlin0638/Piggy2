package server.HttpServer;
import client.MapleCharacter;
import client.MapleClient;
import com.sun.net.httpserver.Headers;
import database.DatabaseConnection;
import handling.world.World;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import org.json.*;
import tools.data.MaplePacketLittleEndianWriter;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

import static constants.ServerConstants.DonateRate;
import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static server.MapleStatInfo.s;

public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> { // 1

    private AsciiString contentType = HttpHeaderValues.TEXT_PLAIN;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        if(msg.method() == HttpMethod.POST && msg.uri().substring(1).equalsIgnoreCase("getbill")){
            try {
                Connection con = DatabaseConnection.getConnection();

                Map<String, Object> parameters = new HashMap<String, Object>();
                InputStreamReader isr = new InputStreamReader(new ByteBufInputStream(msg.content()), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
                String query = br.readLine();
                parseQuery(query, parameters);


                if(Integer.valueOf((String) parameters.get("RtnCode"))== 1) {
                    try {
                        PreparedStatement ps = con.prepareStatement("SELECT * FROM paybill_bills WHERE TradeNo = ? AND accountID = ? AND money = ?", Statement.RETURN_GENERATED_KEYS);
                        ps.setString(1, (String) parameters.get("MerchantTradeNo"));
                        ps.setString(2, (String) parameters.get("CustomField1"));
                        int fuck = Integer.valueOf((String) parameters.get("TradeAmt"));
                        ps.setInt(3, fuck);
                        ResultSet rs = ps.executeQuery();
                        //ps.close();
                        if (rs.next()) {
                            boolean isSent = false;
                            if (rs.getInt("isSent") == 1) {
                                isSent = true;
                            }

                            ps = con.prepareStatement("UPDATE paybill_bills SET isSent = ? WHERE BillID = ? AND isSent != 1");
                            ps.setInt(1, 0);
                            ps.setInt(2, rs.getInt("BillID"));
                            ps.executeUpdate();
                            //ps.close();

                            if (!isSent) {
                                ps = con.prepareStatement("INSERT INTO paybill_paylog (id, account, money, dps, paytime) VALUES (DEFAULT,?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                                ps.setString(1, rs.getString("accountID"));
                                ps.setInt(2, rs.getInt("money"));
                                ps.setInt(3, (int) Math.floor(rs.getInt("money") * DonateRate));
                                ps.setDate(4, rs.getDate("Date"));
                                ps.executeUpdate();
                                System.out.println("帳號 : " + rs.getString("account") + "斗內金額 : " + rs.getInt("money") + "\r\n自" + rs.getDate("Date").toString() + " 已付款 並存入帳號");
                                ps.close();


                                for (MapleCharacter cl : World.getAllCharacters()) {
                                    if (cl.getClient().getAccountName().equalsIgnoreCase(rs.getString("account"))) {
                                        cl.dropMessage("帳號 : " + rs.getString("account") + " 成功獲得 " + (int) Math.floor(rs.getInt("money") * DonateRate) + " 點贊助點.");
                                        cl.gainPoints((int) Math.floor(rs.getInt("money") * DonateRate));
                                        for (MapleClient cll : World.pending_clients) {
                                            if (cll.getAccountName().equalsIgnoreCase(rs.getString("account"))) {
                                                final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
                                                mplew.writeShort(666);
                                                String sb = "帳號 : " + rs.getString("account") + " 成功獲得 " + (int) Math.floor(rs.getInt("money") * DonateRate) + " 點贊助點.";
                                                mplew.write(sb.getBytes(StandardCharsets.UTF_8));
                                                cll.sendPacket(mplew.getPacket());
                                            }
                                        }
                                        ps = con.prepareStatement("UPDATE paybill_bills SET isSent = ? WHERE BillID = ? AND isSent != 1");
                                        ps.setInt(1, 1);
                                        ps.setInt(2, rs.getInt("BillID"));
                                        ps.executeUpdate();
                                        ps.close();
                                    }
                                }
                            }
                        }
                        rs.close();


                    } catch (SQLException ex) {//130.211.243.179
                        ex.printStackTrace();
                    }
                }


                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer("1|OK"
                        .getBytes()));
                response.headers().set(CONTENT_TYPE, "text/plain");
                response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
                response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                ctx.write(response);
                ctx.flush();
                return;

            } catch (NumberFormatException e) {
                return;
            }
        }
        else if(msg.method() == HttpMethod.POST && msg.uri().substring(1).equalsIgnoreCase("likeparse")) {


            String jsonPayload = msg.content().toString(CharsetUtil.UTF_8);
            JSONObject obj = new JSONObject(msg.content().toString(CharsetUtil.UTF_8));
//            System.out.println(jsonPayload);

            JSONArray jr = obj.getJSONArray("Data");
            for(Object jo : jr){
                JSONObject data = (JSONObject) jo;
                String FBName = data.getString("fbname");
                String event = data.getString("title");
                for(MapleCharacter cl : World.getAllCharacters()){
                    if(cl.getClient().getEmail().equals(FBName)){
                        if(cl.getEventCount(event, 1) <= 0){
                            cl.setEventCount(event, 1);
                            cl.modifyCSPoints(2, 1000000, true);
                            cl.dropMessage(1, "獲得FB楓點補助 100 萬");
                        }
                    }
                }
            }


            FullHttpResponse rs = new DefaultFullHttpResponse(HTTP_1_1,
                    OK,
                    Unpooled.wrappedBuffer("無此紀錄".getBytes())); // 2

            HttpHeaders heads = rs.headers();
            //heads.add(HttpHeaderNames.CONTENT_TYPE, contentType + "; charset=UTF-8");
            heads.add(CONTENT_LENGTH, rs.content().readableBytes()); // 3
            heads.add(CONNECTION, HttpHeaderValues.KEEP_ALIVE);

            ctx.write(rs);
            return;
        }
        Connection con = DatabaseConnection.getConnection();
        FullHttpResponse response = null;
        try (PreparedStatement ps = con.prepareStatement("SELECT url from paybill_bills where url = ?")){
            ps.setString(1, msg.uri().substring(1));
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                response = new DefaultFullHttpResponse(HTTP_1_1, OK);
                response.headers().set(CONTENT_TYPE, "text/html;charset=UTF-8");

                StringBuilder sb = new StringBuilder();
                FileReader fr = new FileReader("C:/Bills/" + msg.uri().substring(1) + ".html ");
                BufferedReader br = new BufferedReader(fr);
                while (br.ready()) {
                    sb.append(br.readLine());
                }
                fr.close();

                ByteBuf buffer = Unpooled.copiedBuffer(sb.toString(), CharsetUtil.UTF_8);
                response.content().writeBytes(buffer);
                buffer.release();
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                return;
            }else{
                response = new DefaultFullHttpResponse(HTTP_1_1,
                        OK,
                        Unpooled.wrappedBuffer("無此紀錄".getBytes())); // 2
            }
        }catch (SQLException ex){
            ex.printStackTrace();
            response = new DefaultFullHttpResponse(HTTP_1_1,
                    OK,
                    Unpooled.wrappedBuffer("Error".getBytes())); // 2
        }


        HttpHeaders heads = response.headers();
        //heads.add(HttpHeaderNames.CONTENT_TYPE, contentType + "; charset=UTF-8");
        heads.add(CONTENT_LENGTH, response.content().readableBytes()); // 3
        heads.add(CONNECTION, HttpHeaderValues.KEEP_ALIVE);

        ctx.write(response);
    }

//    private static void sendRedirect(ChannelHandlerContext ctx, String newUri){
//        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
////        response.headers().set("LOCATIN", newUri);
//        response.headers().set(HttpHeaderNames.LOCATION, newUri);
//        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
//    }
//    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status){
//        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
//                Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
//        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");
//        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
//    }
//    private static void setContentTypeHeader(HttpResponse response, File file){
//        MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
//        response.headers().set(HttpHeaderNames.CONTENT_TYPE, mimetypesFileTypeMap.getContentType(file.getPath()));
//    }
//    private static void sendListing(ChannelHandlerContext ctx, File dir){
//        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
////        response.headers().set("CONNECT_TYPE", "text/html;charset=UTF-8");
//        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");
//
//        String dirPath = dir.getPath();
//        StringBuilder buf = new StringBuilder();
//
//        buf.append("<!DOCTYPE html>\r\n");
//        buf.append("<html><head><title>");
//        buf.append(dirPath);
//        buf.append("目录:");
//        buf.append("</title></head><body>\r\n");
//
//        buf.append("<h3>");
//        buf.append(dirPath).append(" 目录：");
//        buf.append("</h3>\r\n");
//        buf.append("<ul>");
//        buf.append("<li>链接：<a href=\" ../\")..</a></li>\r\n");
//        for (File f : dir.listFiles()) {
//            if(f.isHidden() || !f.canRead()) {
//                continue;
//            }
//            String name = f.getName();
//            if (!ALLOWED_FILE_NAME.matcher(name).matches()) {
//                continue;
//            }
//
//            buf.append("<li>链接：<a href=\"");
//            buf.append(name);
//            buf.append("\">");
//            buf.append(name);
//            buf.append("</a></li>\r\n");
//        }
//
//        buf.append("</ul></body></html>\r\n");
//
//        ByteBuf buffer = Unpooled.copiedBuffer(buf,CharsetUtil.UTF_8);
//        response.content().writeBytes(buffer);
//        buffer.release();
//        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
//    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.flush(); // 4
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        if(null != cause) cause.printStackTrace();
        if(null != ctx) ctx.close();
    }
    private static void parseQuery(String query, Map<String,
            Object> parameters) throws UnsupportedEncodingException {

        if (query != null) {
            String pairs[] = query.split("[&]");
            for (String pair : pairs) {
                String param[] = pair.split("[=]");
                String key = null;
                String value = null;
                if (param.length > 0) {
                    key = URLDecoder.decode(param[0],
                            System.getProperty("file.encoding"));
                }

                if (param.length > 1) {
                    value = URLDecoder.decode(param[1],
                            System.getProperty("file.encoding"));
                }

                if (parameters.containsKey(key)) {
                    Object obj = parameters.get(key);
                    if (obj instanceof List<?>) {
                        List<String> values = (List<String>) obj;
                        values.add(value);

                    } else if (obj instanceof String) {
                        List<String> values = new ArrayList<String>();
                        values.add((String) obj);
                        values.add(value);
                        parameters.put(key, values);
                    }
                } else {
                    parameters.put(key, value);
                }
            }
        }
    }
}

