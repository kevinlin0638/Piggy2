package handling.Poker;
import client.MapleCharacter;
import constants.GameConstants;
import constants.ServerConstants;
import server.Randomizer;
import server.maps.MapleMap;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.types.ArrayMap;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class PokerGame {
    private ArrayList<PokerPlayer> winners = new ArrayList<>();
    private ArrayList<PokerPlayer> players = new ArrayList<>();
    private int MainPot;
    private final int MinPot;
    private final int blind;
    private long timeLeft;
    private ArrayList<Card> PotCard = new ArrayList<>();
    private MapleMap GameMap;
    private boolean isStart = false;
    private byte flag = 0;
    private int state = 0;
    private int turn_index = 0;
    private int current_bet = 0;

    public PokerGame(int minPot, int blind, MapleMap gameMap, MapleCharacter Creator) {
        this.MinPot = minPot;
        this.blind = blind;
        GameMap = gameMap;
        players.add(new PokerPlayer(Creator));
        timeLeft = System.currentTimeMillis() + 60000;
    }

    private boolean checkMapPlayer(){
        int count = 0;
        for(MapleCharacter chr : GameMap.getCharacters()){
            if(chr.getPg() != null && chr.getPg().equals(this))
                count++;
        }
        return count > 1;
    }

    private void CheckPlayersCardType(PokerPlayer pp){
        ArrayList<Card> allCard = new ArrayList<>(PotCard);
        allCard.add(pp.getCd_one());
        allCard.add(pp.getCd_two());
        ArrayList<Integer> CardIndex = new ArrayList<>();
        for (Card cd : allCard){
            CardIndex.add(cd.getNumber() - 1 + ( cd.getSuit() - 1) * 13);
        }
        Map<Short, Integer> map = new HashMap<>();
        Map<Short, Integer> Suitmap = new HashMap<>();
        for (Card cd : allCard){
            map.put(cd.getNumber(), map.getOrDefault(cd.getNumber(), 0) + 1);
        }
        for (Card cd : allCard){
            Suitmap.put(cd.getSuit(), Suitmap.getOrDefault(cd.getSuit(), 0) + 1);
        }


        //預設散牌
        pp.setType(1);
        pp.setsCardMax(getBiggestSCard(pp, -1));
        //查看對子
        int p1 = -1;
        int p2 = -1;
        for(Map.Entry<Short, Integer> et : map.entrySet()){
            if(et.getValue() == 2){
                if(et.getKey() > p1) {
                    p2 = p1;
                    p1 = et.getKey();
                }
            }
        }
        Card biggest;
        if (p1 != -1) { // 表示有一對
            biggest =findTypeBiggest(allCard, p1);
            pp.setTypeMax(biggest);
            pp.setsCardMax(getBiggestSCard(pp, p1));
            pp.setType(2);
        }
        if(p2 != -1){ // 表示兩對 p1 大對
            biggest =findTypeBiggest(allCard, p2);
            pp.setPairCard2(biggest);
            pp.setType(3);
        }
        int temp_t = -1;
        for(Map.Entry<Short, Integer> et : map.entrySet()){
            if(et.getValue() == 3){
                if(et.getKey() > temp_t) {
                    temp_t = et.getKey();
                }
            }
        }
        if(temp_t != -1){
            biggest = findTypeBiggest(allCard, temp_t);
            pp.setTypeMax(biggest);
            pp.setType(4);
        }

        int temp_soonz = -1;
        for(Map.Entry<Short, Integer> et : map.entrySet()){
            if(et.getKey() <= 10){
                boolean have_soonz = true;
                for(int cc = 1;cc < 5;cc++){
                    if(map.getOrDefault((short)((et.getKey() + cc > 13)?1 : et.getKey() + cc), 0) == 0){
                        have_soonz = false;
                        break;
                    }
                }
                if(have_soonz && et.getKey() > temp_soonz)
                    temp_soonz = et.getKey();
            }
        }
        if(temp_soonz != -1){
            biggest = findTypeBiggest(allCard, ((temp_soonz + 4> 13)?1 : temp_soonz + 4));
            pp.setTypeMax(biggest);
            pp.setType(5);
        }

        short temp_same_suit = -1;
        for(Map.Entry<Short, Integer> et : Suitmap.entrySet()){
            if(et.getValue() >= 5)
            {
                temp_same_suit = et.getKey();
            }
        }
        if(temp_same_suit != -1){
            biggest = findTypeBiggest(allCard, temp_same_suit);
            pp.setTypeMax(biggest);
            pp.setType(6);
        }

        if(temp_t != -1){
            biggest = findTypeBiggest(allCard, temp_t);
            if(p1 != -1 &&  p1 != temp_t){
                pp.setTypeMax(biggest);
                pp.setType(7);
            }else if(p2 != -1 && p2 != temp_t){
                pp.setTypeMax(biggest);
                pp.setType(7);
            }
        }

        int temp_f = -1;
        for(Map.Entry<Short, Integer> et : map.entrySet()){
            if(et.getValue() == 4){
                if(et.getKey() > temp_f) {
                    temp_f = et.getKey();
                }
            }
        }
        if(temp_f != -1){
            biggest = findTypeBiggest(allCard, temp_f);
            pp.setTypeMax(biggest);
            pp.setType(8);
        }


        int temp_ton_hwa = -1;
        if(temp_same_suit > 0 &&  temp_soonz > 0){
            for(Map.Entry<Short, Integer> et : map.entrySet()){
                if(et.getKey() <= 10){
                    boolean have_soonz = true;
                    for(int cc = 1;cc < 5;cc++){
                        if(map.getOrDefault((short)((et.getKey() + cc > 13)?1 : et.getKey() + cc), 0) == 0){
                            have_soonz = false;
                            break;
                        }
                    }
                    if(have_soonz){
                        temp_soonz =  et.getKey();
                        int same = 1;
                        for(int cc = 1;cc < 5;cc++){
                            for (Card cd : allCard){
                                if(cd.getNumber() == (temp_soonz  + cc > 13?  1 : temp_soonz  + cc))
                                    if(cd.getSuit() == temp_same_suit) {
                                        same += 1;
                                    }
                            }
                        }
                        if(same == 5){
                            if(temp_soonz > temp_ton_hwa)
                                temp_ton_hwa = temp_soonz;
                        }
                    }
                }
            }
        }

        if(temp_ton_hwa > 0){
            biggest = findTypeBiggest(allCard, ((temp_ton_hwa + 4> 13)?1 : temp_ton_hwa + 4));
            pp.setTypeMax(biggest);
            pp.setType(9);

            if(temp_ton_hwa == 10)
                pp.setType(10);
        }


    }

    private Card findTypeBiggest(ArrayList<Card> allCard, int p2) {
        Card biggest = new Card((short) 5,(short) p2);
        for(Card cd : allCard){
            if(cd.getNumber() == p2 && cd.getSuit() < biggest.getSuit())
                biggest = cd;
        }
        return biggest;
    }

    private Card findTypeBiggest(ArrayList<Card> allCard, short p2) {
        Card biggest = new Card((short) 5,(short) -1);
        for(Card cd : allCard){
            if(cd.getSuit() == p2 && cd.getNumber() > biggest.getNumber())
                biggest = cd;
        }
        return biggest;
    }


    private Card getBiggestSCard(PokerPlayer pp, int filter1, int filter2){ //找到最大散牌
        Card temp = new Card((short) 5, (short)-1);
        for(Card cd : PotCard){
            temp = getCardTempTwoFilter(filter1, filter2, temp, cd);
        }
        Card cd =  pp.getCd_one();
        temp = getCardTempTwoFilter(filter1, filter2, temp, cd);
        cd = pp.getCd_two();
        temp = getCardTempTwoFilter(filter1, filter2, temp, cd);
        return temp;
    }

    private Card getCardTempTwoFilter(int filter1, int filter2, Card temp, Card cd) {
        if(cd.getNumber() != filter1 && cd.getNumber() != filter2 && (cd.getNumber() > temp.getNumber() || (cd.getNumber() == temp.getNumber() && cd.getSuit() < temp.getSuit()))){
            temp = cd;
        }
        return temp;
    }

    private Card getBiggestSCard(PokerPlayer pp, int filter1){ //找到最大散牌
        Card temp = new Card((short) 5, (short)-1);
        for(Card cd : PotCard){
            temp = getCardTempTwoFilter(filter1, -1, temp, cd);
        }
        Card cd =  pp.getCd_one();
        temp = getCardTempTwoFilter(filter1, -1, temp, cd);
        cd = pp.getCd_two();
        temp = getCardTempTwoFilter(filter1, -1, temp, cd);
        return temp;
    }

    public void join_game(MapleCharacter chr){
        for (PokerPlayer pp : players){
            if(pp.getChr().get() == chr){
                return;
            }
        }

        for (PokerPlayer pp : players){
            if(pp.getChr().get() == null){
                players.remove(pp);
                join_game(chr);
                return;
            }
        }
        chr.setPg(this);
        players.add(new PokerPlayer(chr));
    }

    public void exit_game(MapleCharacter chr){
        for (PokerPlayer pp : players){
            if(pp.getChr().get() == null){
                players.remove(pp);
                exit_game(chr);
                return;
            }
        }
        for (PokerPlayer pp : players){
            if(pp.getChr().get() == chr){
                players.remove(pp);
                break;
            }
        }
    }


    public int getTurn_index() {
        return turn_index;
    }

    private void setTurn_index(int turn_index) {
        this.turn_index = turn_index;
    }

    public int getCurrent_bet() {
        return current_bet;
    }

    public void setCurrent_bet(int current_bet) {
        this.current_bet = current_bet;
    }

    public boolean canStart(){
        return checkMapPlayer();
    }

    public void setClock(int second){
        timeLeft = System.currentTimeMillis() + (second * 1000);
        GameMap.broadcastMessage(CField.getClock(second));
    }

    public long getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(long timeLeft) {
        this.timeLeft = timeLeft;
    }

    public ArrayList<PokerPlayer> getPlayers() {
        return players;
    }

    public int getMinPot() {
        return MinPot;
    }

    public int getBlind() {
        return blind;
    }

    public ArrayList<Card> getPotCard() {
        return PotCard;
    }


    public int getMainPot() {
        return MainPot;
    }

    public void addMainPot(int point){
        MainPot += point;
    }

    public void setMainPot(int mainPot) {
        MainPot = mainPot;
    }

    public MapleMap getGameMap() {
        return GameMap;
    }

    public boolean isStart() {
        return isStart;
    }

    public void StartPoker() {
        flag = 0;

        for (PokerPlayer pp : players){
            if(pp.getChr().get() != null){
                send_chip(Objects.requireNonNull(pp.getChr().get()));
            }
        }
        try {
            while (canStart()) {
                setStart(true);
                newRow();
                for (PokerPlayer pp : players){
                    if(pp.getChr().get() != null){
                        Objects.requireNonNull(pp.getChr().get()).getClient().sendPacket(CWvsContext.clearMidMsg());
                    }
                }
                sendToPlayers( "此局結束，下局 20秒 後開始");
                sendToPlayers( -7,"此局結束，下局 20秒 後開始");
                setClock(20);

                setStart(false);
                TimeUnit.SECONDS.sleep(20);
                if(players.size() == 0)
                    break;
                ArrayList<PokerPlayer> rp = new ArrayList<>();
                for (PokerPlayer pp : players){
                    if(pp.getChr().get() != null){
                        if(pp.getChips() < blind) {
                            Objects.requireNonNull(pp.getChr().get()).setPg(null);
                            rp.add(pp);
                        }
                    }
                }
                for(PokerPlayer pp : rp){
                    players.remove(pp);
                }
                flag += 1;
                flag %= players.size();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void newRow() throws InterruptedException {
        state = 0;// 初始化狀態
        turn_index = 0;
        current_bet = blind;
        setMainPot(0);
        ArrayList<PokerPlayer> RowPlayer = new ArrayList<>();
        StringBuilder s = new StringBuilder("出牌順序");
        for(int i = (flag + 2) % players.size(); i < players.size();i++){
            setRowPlayer(RowPlayer, s, i);
        }
        for(int i = 0; i < (flag + 2) % players.size(); i++){
            setRowPlayer(RowPlayer, s, i);
        }
        sendToPlayers(s.toString());

        draw_all_card();
//        if(ServerConstants.DEBUG){
//            sendToPlayers("共用牌");
//            StringBuilder ss = new StringBuilder();
//            for (Card cd : PotCard){
//                ss.append(cd.getCardString()).append(", ");
//            }
//            sendToPlayers(ss.toString());
//            for (PokerPlayer pk : RowPlayer){
//                sendToPlayers(Objects.requireNonNull(pk.getChr().get()).getName() + "的手牌");
//                sendToPlayers(pk.getCd_one().getCardString());
//                sendToPlayers(pk.getCd_two().getCardString());
//            }
//        }
        for (PokerPlayer pk : RowPlayer){
            pk.init();
            Objects.requireNonNull(pk.getChr().get()).dropMessage("您的牌是 " + pk.getCd_one().getCardString() + " " + pk.getCd_two().getCardString());
        }
        sendToPlayers("大盲玩家: " + Objects.requireNonNull(RowPlayer.get(RowPlayer.size() - 1).getChr().get()).getName() + " 小盲玩家: " + Objects.requireNonNull(RowPlayer.get(RowPlayer.size() - 2).getChr().get()).getName());
        RowPlayer.get(RowPlayer.size() - 1).setBet(blind);
        RowPlayer.get(RowPlayer.size() - 2).setBet(blind/2);
        RowPlayer.get(RowPlayer.size() - 1).TakeChips(blind);
        RowPlayer.get(RowPlayer.size() - 2).TakeChips(blind/2);
        addMainPot(blind + blind/2);
        for (int i = 0;i < 4; i++) {
            betting(RowPlayer);
            if(check_winner(RowPlayer))
                break;
        }


    }

    private boolean check_winner(ArrayList<PokerPlayer> rowPlayer){
        if(state <= 3){
            int left_p = 0;
            int temp_winner = -1;
            for (PokerPlayer pk : rowPlayer){
                if(!pk.isFolded()) {
                    left_p++;
                    temp_winner = rowPlayer.indexOf(pk);
                }
            }
            if(left_p <= 1){
                rowPlayer.get(temp_winner).giveChips(getMainPot());
                sendToPlayers("本局勝利者:" + Objects.requireNonNull(rowPlayer.get(temp_winner).getChr().get()).getName() + "-" + getMainPot() + "萬楓點 ");
                return true;
            }
        }else{
            for (PokerPlayer pk : rowPlayer){
                if(!pk.isFolded()) {
                    CheckPlayersCardType(pk);
                }
            }
            ArrayList<PokerPlayer> winners  = new ArrayList<>();
            ArrayList<PokerPlayer> others  = new ArrayList<>();
            for (PokerPlayer pk : rowPlayer){
                if(!pk.isFolded()) {
                    if(winners.size() == 0)
                        winners.add(pk);
                    else if(winners.get(0).getType() < pk.getType()) {
                        winners.clear();
                        winners.add(pk);
                    }else if(winners.get(0).getType() == pk.getType()){
                        if(winners.get(0).getTypeMax().getNumber() < pk.getTypeMax().getNumber()){
                            winners.clear();
                            winners.add(pk);
                        }else if(winners.get(0).getTypeMax().getSuit() > pk.getTypeMax().getSuit()){
                            winners.clear();
                            winners.add(pk);
                        }else{
                            winners.add(pk);
                        }
                    }else{
                        others.add(pk);
                    }
                }
            }

            int most_win = 0;
            int main_pot = getMainPot();
            int o_counter = 0;
            for(PokerPlayer pk : winners){
                if(pk.getTotal_bet() > most_win)
                    most_win =  pk.getTotal_bet();
            }

            StringBuilder s = new StringBuilder("可回收點數玩家:");
            for(PokerPlayer pk : others){
                if(pk.getTotal_bet() > most_win){
                    s.append(Objects.requireNonNull(pk.getChr().get()).getName()).append("-").append(pk.getTotal_bet() - most_win).append("萬楓點 ");
                    pk.giveChips(pk.getTotal_bet() - most_win);
                    main_pot -= (pk.getTotal_bet() - most_win);
                    o_counter++;
                }

            }
            if(o_counter > 0)
                sendToPlayers(-6, s.toString());

            s = new StringBuilder("本局勝利者:");
            for(PokerPlayer pk : winners){
                s.append(Objects.requireNonNull(pk.getChr().get()).getName()).append("-").append(main_pot / winners.size()).append("萬楓點 ");
                pk.giveChips(main_pot / winners.size());
            }
            sendToPlayers(-6, s.toString());

            sendToPlayers(-6, "----------------------------------------------");
            s = new StringBuilder();
            s.append(getPotCard().get(0).getCardString()).append(" ");
            s.append(getPotCard().get(1).getCardString()).append(" ");
            s.append(getPotCard().get(2).getCardString()).append(" ");
            s.append(getPotCard().get(3).getCardString()).append(" ");
            s.append(getPotCard().get(4).getCardString()).append(" ");
            sendToPlayers(-6, "牌桌: " + s.toString());
            sendToPlayers(-6, "各玩家牌型如下");


            for (PokerPlayer pk : rowPlayer){
                if(!pk.isFolded()) {
                    sendToPlayers(-6,  Objects.requireNonNull(pk.getChr().get()).getName() + ":" + pk.getCd_one().getCardString() + " " + pk.getCd_two().getCardString() + " - " + pk.Card_Type_String());
                }
            }
        }
        return false;
    }

    private void betting(ArrayList<PokerPlayer> rowPlayer) throws InterruptedException {

        sendToPlayers(-7, "發牌中");
        setClock(5);
        TimeUnit.SECONDS.sleep(5);
        int end_index = rowPlayer.size();
        boolean first = true;
        while(true){
            boolean can_next = true;


            for (PokerPlayer pk : rowPlayer){
                if(pk.isFolded()){
                    continue;
                }

                int left_p = 0;
                for (PokerPlayer pkk : rowPlayer){
                    if(!pkk.isFolded()) {
                        left_p++;
                    }
                }
                if(left_p <= 1) {
                    can_next = true;
                    break;
                }
                if(can_next && end_index == rowPlayer.indexOf(pk) && !first)
                    break;

                if(pk.isAllin()){
                    pk.sendToPlayer("因你已經 ALL IN 將自動輪到下一位");
                    continue;
                }


                ShowBoardString();

                pk.setPlayer_Move_type(0);
                pk.setBet_this_round(0);
                setTurn_index(players.indexOf(pk));
                pk.sendToPlayer("輪到你了，使用 @check-過牌/@fold-棄牌/@call-跟注/@bet <籌碼量>-下注/@Allin-全下");
                pk.sendToPlayer(-6, "您的牌是 " + pk.getCd_one().getCardString() + " " + pk.getCd_two().getCardString() + " 剩餘籌碼:" + pk.getChips() + "萬");
                setClock(45);
                synchronized (this) {
                    TimeUnit.SECONDS.timedWait(this, 45);
                }
                if(deal_player_move(pk)) {
                    can_next = false;
                    end_index = rowPlayer.indexOf(pk);
                }
            }
            if(can_next){
                break;
            }
            first = false;
        }
        nextState(rowPlayer);
    }

    private void nextState(ArrayList<PokerPlayer> rowPlayer){
        state += 1;
        current_bet = 0;
        for (PokerPlayer pk : rowPlayer){
            pk.setBet(0);
        }
    }

    private boolean deal_player_move(PokerPlayer player){
        switch (player.getPlayer_Move_type()){
            case 0:
                if(player.getBet() < current_bet){
                   player.setFolded(true);
                   player.sendToPlayer( "由於您沒有動作，系統自動棄牌");
                    sendToPlayers(Objects.requireNonNull(player.getChr().get()).getName() + " 選擇棄牌");
                }else{
                    player.sendToPlayer( "由於您沒有動作，系統自動過牌");
                    sendToPlayers(Objects.requireNonNull(player.getChr().get()).getName() + " 選擇過牌");
                }
                return false;
            case 1:
                player.sendToPlayer("您選擇過牌");
                return false;
            case 2:
                player.TakeChips(player.getBet_this_round());
                player.sendToPlayer("您選擇跟注: " + player.getBet() +" 收取籌碼: " + player.getBet_this_round() + " 剩餘籌碼: " + player.getChips());
                sendToPlayers(Objects.requireNonNull(player.getChr().get()).getName() + " 選擇跟注");
                addMainPot(player.getBet_this_round());
                return false;
            case 3:
                if(player.getBet() == current_bet){
                    player.TakeChips(player.getBet_this_round());
                    player.sendToPlayer("您選擇跟注: " + player.getBet() +" 收取籌碼: " + player.getBet_this_round() + " 剩餘籌碼: " + player.getChips());
                    sendToPlayers(Objects.requireNonNull(player.getChr().get()).getName() + " 選擇跟注");
                    addMainPot(player.getBet_this_round());
                    return false;
                }else if(player.getBet_this_round() == player.getChips()){
                    player.TakeChips(player.getBet_this_round());
                    player.sendToPlayer("您選擇 All In: " + player.getBet() +" 收取籌碼: " + player.getBet_this_round() + " 剩餘籌碼: " + player.getChips());
                    sendToPlayers(Objects.requireNonNull(player.getChr().get()).getName() + " 選擇 ALLIN");
                    player.setAllin(true);
                    addMainPot(player.getBet_this_round());
                    if(current_bet < player.getBet())
                        current_bet = player.getBet();
                    return true;
                }else{
                    player.TakeChips(player.getBet_this_round());
                    player.sendToPlayer("您選擇下注: " + player.getBet() +" 收取籌碼: " + player.getBet_this_round() + " 剩餘籌碼: " + player.getChips());
                    sendToPlayers(Objects.requireNonNull(player.getChr().get()).getName() + " 選擇下注 " + player.getBet());
                    addMainPot(player.getBet_this_round());
                    if(current_bet < player.getBet())
                        current_bet = player.getBet();
                    return true;
                }
            default:
                return false;
        }
    }

    private void ShowBoardString(){
        for (PokerPlayer pp : players){
            if(pp.getChr().get() != null){
                Objects.requireNonNull(pp.getChr().get()).getClient().sendPacket(CWvsContext.clearMidMsg());
            }
        }
        sendToPlayers(-8, "現在輪到 " + Objects.requireNonNull(players.get(turn_index).getChr().get()).getName());
        sendToPlayers(-8, "目前彩池 " + getMainPot() + "萬 楓點");
        StringBuilder s;
        switch (state){
            case 1:
                s = new StringBuilder();
                s.append(getPotCard().get(0).getCardString()).append(" ");
                s.append(getPotCard().get(1).getCardString()).append(" ");
                s.append(getPotCard().get(2).getCardString()).append(" ");
                sendToPlayers(-8, "牌桌: " + s.toString());
                break;
            case 2:
                s = new StringBuilder();
                s.append(getPotCard().get(0).getCardString()).append(" ");
                s.append(getPotCard().get(1).getCardString()).append(" ");
                s.append(getPotCard().get(2).getCardString()).append(" ");
                s.append(getPotCard().get(3).getCardString()).append(" ");
                sendToPlayers(-8, "牌桌: " + s.toString());
                break;
            case 3:
                s = new StringBuilder();
                s.append(getPotCard().get(0).getCardString()).append(" ");
                s.append(getPotCard().get(1).getCardString()).append(" ");
                s.append(getPotCard().get(2).getCardString()).append(" ");
                s.append(getPotCard().get(3).getCardString()).append(" ");
                s.append(getPotCard().get(4).getCardString()).append(" ");
                sendToPlayers(-8, "牌桌: " + s.toString());
                break;
        }
        s = new StringBuilder();
        for (PokerPlayer pp : players){
            if(!pp.isFolded()) {
                s.append(Objects.requireNonNull(pp.getChr().get()).getName()).append(":").append(pp.getBet()).append(" ");
            }
        }
        sendToPlayers(-8, "注: " + s.toString());
    }

    private void draw_all_card(){
        ArrayList<Integer> ran = new ArrayList<>();
        for(int i = 0; i < (players.size() * 2 + 5); i++){
            while(true){
                boolean isConflict = false;
                int ra = Randomizer.nextInt(52);
                for (int k : ran) {
                    if(k == ra) {
                        isConflict = true;
                        break;
                    }
                }
                if(isConflict)
                    continue;
                ran.add(ra);
                break;
            }
        }
        int counter;
        PotCard.clear();
        for(counter = 0; counter < 5;counter++){
            int nm = ran.get(counter);
            PotCard.add(new Card((short) (nm / 13 + 1), (short) (nm % 13 + 1)));
        }
        for (PokerPlayer pp : players){
            int nm = ran.get(counter);
            pp.setCd_one(new Card((short) (nm / 13 + 1), (short) (nm % 13 + 1)));
            counter++;

            nm = ran.get(counter);
            pp.setCd_two(new Card((short) (nm / 13 + 1), (short) (nm % 13 + 1)));
            counter++;
        }
    }

    private void setRowPlayer(ArrayList<PokerPlayer> rowPlayer, StringBuilder s, int i) {
        if(players.get(i).getChr().get() != null){
            s.append(" -> ").append(Objects.requireNonNull(players.get(i).getChr().get()).getName());
            rowPlayer.add(players.get(i));
        }
    }

    private boolean send_chip(MapleCharacter chr){
        chr.modifyCSPoints(2, -blind * 100);
        switch (blind){
            case 2:
                chr.gainItem(4001547, (short) 25);
                chr.gainItem(4001548, (short) 15);
                chr.gainItem(4001549, (short) 10);
                break;
            case 6:
                chr.gainItem(4001547, (short) 50);
                chr.gainItem(4001548, (short) 30);
                chr.gainItem(4001549, (short) 20);
                chr.gainItem(4001550, (short) 2);
                break;
            case 10:
                chr.gainItem(4001547, (short) 100);
                chr.gainItem(4001548, (short) 60);
                chr.gainItem(4001549, (short) 30);
                chr.gainItem(4001550, (short) 3);
                break;
            case 30:
                chr.gainItem(4001547, (short) 100);
                chr.gainItem(4001548, (short) 80);
                chr.gainItem(4001549, (short) 50);
                chr.gainItem(4001550, (short) 10);
                chr.gainItem(4001551, (short) 1);
                break;
            case 50:
                chr.gainItem(4001547, (short) 100);
                chr.gainItem(4001548, (short) 80);
                chr.gainItem(4001549, (short) 50);
                chr.gainItem(4001550, (short) 10);
                chr.gainItem(4001551, (short) 3);
                break;
            case 100:
                chr.gainItem(4001547, (short) 100);
                chr.gainItem(4001548, (short) 80);
                chr.gainItem(4001549, (short) 50);
                chr.gainItem(4001550, (short) 40);
                chr.gainItem(4001551, (short) 5);
                break;
        }
        return true;
    }

    public int check_Nuts(MapleCharacter chr) {
        return 0;
    }

    public void exchange_Chip(MapleCharacter chr){
        int[] chips = {4001547, 4001548, 4001549, 4001550, 4001551};
        for(int i : chips){
            int number = chr.getItemQuantity(i, false);

            if(number > 0) {
                int base;
                switch (i) {
                    case 4001547:
                        base = 1;
                        break;
                    case 4001548:
                        base = 5;
                        break;
                    case 4001549:
                        base = 10;
                        break;
                    case 4001550:
                        base = 100;
                        break;
                    case 4001551:
                        base = 1000;
                        break;
                    default:
                        base = 0;
                        break;
                }
                chr.gainItem(i, (short) -number);
                chr.modifyCSPoints(2, number * base);
            }
        }
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public void sendToPlayers(String notice){
        for (PokerPlayer pp : players){
            if(pp.getChr().get() == null){
                players.remove(pp);
                sendToPlayers(notice);
                return;
            }
        }

        for (PokerPlayer pp : players){
            if(pp.getChr().get() != null){
                Objects.requireNonNull(pp.getChr().get()).dropMessage(notice);
            }
        }
    }

    public void sendToPlayers(int type, String notice){
        for (PokerPlayer pp : players){
            if(pp.getChr().get() == null){
                players.remove(pp);
                sendToPlayers(type, notice);
                return;
            }
        }

        for (PokerPlayer pp : players){
            if(pp.getChr().get() != null){
                Objects.requireNonNull(pp.getChr().get()).dropMessage(type, notice);
            }
        }
    }
}
