package server.HttpServer;
import database.DatabaseConnection;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static server.MapleStatInfo.s;

public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> { // 1

    private AsciiString contentType = HttpHeaderValues.TEXT_PLAIN;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        System.out.println("class:" + msg.getClass().getName());
        Connection con = DatabaseConnection.getConnection();
        FullHttpResponse response = null;
        try (PreparedStatement ps = con.prepareStatement("SELECT url from paybill_bills where url = ?")){
            ps.setString(1, msg.uri().substring(1));
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");

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
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                        HttpResponseStatus.OK,
                        Unpooled.wrappedBuffer("無此紀錄".getBytes())); // 2
            }
        }catch (SQLException ex){
            ex.printStackTrace();
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer("Error".getBytes())); // 2
        }


        HttpHeaders heads = response.headers();
        //heads.add(HttpHeaderNames.CONTENT_TYPE, contentType + "; charset=UTF-8");
        heads.add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes()); // 3
        heads.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

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
        System.out.println("channelReadComplete");
        super.channelReadComplete(ctx);
        ctx.flush(); // 4
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exceptionCaught");
        if(null != cause) cause.printStackTrace();
        if(null != ctx) ctx.close();
    }
}

