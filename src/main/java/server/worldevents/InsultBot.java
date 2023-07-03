/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package server.worldevents;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * @author Eric
 */
public class InsultBot {

    public static String getInsult() {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("http://www.randominsults.net/").openConnection();
            StringBuilder sb = new StringBuilder();
            con.connect();
            InputStream input = con.getInputStream();
            byte[] buf = new byte[2048];
            int read;
            while ((read = input.read(buf)) > 0) {
                sb.append(new String(buf, 0, read));
            }
            final String find = "<strong><i>";
            int firstPost = sb.indexOf(find);
            StringBuilder send = new StringBuilder();
            for (int i = firstPost + find.length(); i < sb.length(); i++) {
                char ch = sb.charAt(i);
                if (sb.charAt(i) == '<' && sb.charAt(i + 1) == '/' && sb.charAt(i + 2) == 'i')
                    break;
                send.append(ch);
            }
            String sendTxt = send.toString();
            sendTxt = sendTxt.replaceAll("\\<.*?>", "");
            sendTxt = fixHTML(sendTxt);
            return sendTxt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Error Occured!";
    }

    public static String fixHTML(String in) {
        in = in.replaceAll(Pattern.quote("&quot;"), "\"");
        in = in.replaceAll(Pattern.quote("&amp;"), "&");
        return in;
    }

}
