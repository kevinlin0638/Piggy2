package handling;

import io.netty.channel.ChannelHandlerContext;
import tools.types.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionTracker {

    private final List<String> blockIPs = new ArrayList<>();

    private final Map<String, Pair<Long, Byte>> tracker = new ConcurrentHashMap<>();


    public SessionTracker() {

    }

    public boolean trackSession(ChannelHandlerContext ctx) {

        final String host = ctx.channel().remoteAddress().toString().split(":")[0];

        if (blockIPs.contains(host)) {
            return false;
        } else {
            final Pair<Long, Byte> track = tracker.get(host);
            byte count = 1;

            if (track != null) {
                count = track.right;
                final long difference = System.currentTimeMillis() - track.left;
                if (difference < 2000) { // Less than 2 sec
                    count++;
                } else if (difference > 20000) { // Over 20 sec
                    count = 1;
                }
                if (count >= 10) {
                    blockIPs.add(host);
                    tracker.remove(host); // Cleanup
                    return false;
                }
            }
            tracker.put(host, new Pair<>(System.currentTimeMillis(), count));
            return true;
        }
    }

}
