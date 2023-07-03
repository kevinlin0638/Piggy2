package server.worldevents;

import server.Randomizer;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * @author Oliver
 */
public class MLIABot {

    private static int getPage() {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("http://mylifeisaverage.com/").openConnection();

            StringBuilder sb = new StringBuilder();
            con.connect();
            InputStream input = con.getInputStream();
            byte[] buf = new byte[2048];
            int read;
            while ((read = input.read(buf)) > 0) {
                sb.append(new String(buf, 0, read));
            }

            final String find = "<li class=\"last\"><a href=\"";
            int firstPost = sb.indexOf(find);

            StringBuilder send = new StringBuilder();

            for (int i = firstPost + find.length(); i < sb.length(); i++) {
                char ch = sb.charAt(i);

                if (ch == '"') {
                    break;
                }
                send.append(ch);
            }

            int toreturn = Integer.parseInt(send.toString());
            toreturn = Randomizer.nextInt(toreturn) + 1;
            return toreturn;
        } catch (Exception e) {
            System.err.println("[MLIA Bot] There has been an error displaying the FML.");
            e.printStackTrace();
        }

        return 2;
    }

    public static String findMLIA() {
        String fmlmsg = "";
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("http://mylifeisaverage.com/" + getPage()).openConnection();

            StringBuilder sb = new StringBuilder();
            con.connect();
            InputStream input = con.getInputStream();
            byte[] buf = new byte[2048];
            int read;
            while ((read = input.read(buf)) > 0) {
                sb.append(new String(buf, 0, read));
            }

            final String find = "<div class=\"sc\">";
            int random = Randomizer.nextInt(10);
            for (int i = 0; i < random; i++) {
                String gb = sb.substring(sb.indexOf(find) + 1);

                sb = new StringBuilder();
                sb.append(gb);
            }

            int firstPost = sb.indexOf(find);

            StringBuilder send = new StringBuilder();

            for (int i = firstPost + find.length(); i < sb.length(); i++) {
                char ch = sb.charAt(i);

                if (ch == '<') {
                    if (sb.charAt(i + 1) == '/') {
                        if (sb.charAt(i + 2) == 'd') {
                            break;
                        }
                    }
                }
                send.append(ch);
            }
            String sendTxt = send.toString();
            sendTxt = ignore(sendTxt);

            final String find2 = " ";
            sendTxt = sendTxt.substring(sendTxt.indexOf(find2));
            sendTxt = sendTxt.substring(8);

            fmlmsg = "[My Life Is Average] " + sendTxt;
            input.close();
            con.disconnect();
        } catch (Exception e) {
            System.err.println("[MLIA Bot] There has been an error displaying the MLIA.");
            e.printStackTrace();
        }

        return fmlmsg;
    }

    public static String ignore(String in) {
        in = in.replaceAll("\\<.*?>", "");
        // in = in.replaceAll(Pattern.quote("."), ".\r\n");
        in = in.replaceAll(Pattern.quote("&quot;"), "");
        in = in.replaceAll(Pattern.quote("&amp;"), "");
        in = in.replaceAll(Pattern.quote(""), "");
        return in;
    }
}