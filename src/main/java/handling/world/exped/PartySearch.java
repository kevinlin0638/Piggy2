package handling.world.exped;

import handling.world.World;
import server.Timer.EtcTimer;

import java.util.concurrent.ScheduledFuture;

public final class PartySearch {
    private String name; //max 40
    private int partyId;
    private PartySearchType pst;
    private ScheduledFuture<?> removal;

    public PartySearch(String name, int partyId, PartySearchType pst) {
        this.name = name;
        this.partyId = partyId;
        this.pst = pst;
        scheduleRemoval();
    }

    public PartySearchType getType() {
        return pst;
    }

    public int getId() {
        return partyId;
    }

    public String getName() {
        return name;
    }

    public void scheduleRemoval() {
        cancelRemoval();
        removal = EtcTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                World.Party.removeSearch(PartySearch.this, "The Party Listing was removed because it has expired.");
            }
        }, pst.timeLimit * 60000);
    }

    public void cancelRemoval() {
        if (removal != null) {
            removal.cancel(false);
            removal = null;
        }
    }
}
