package handling.Poker;

import client.MapleCharacter;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class PokerPlayer {
    private final WeakReference<MapleCharacter> chr;
    private Card cd_one;
    private Card cd_two;
    private int bet = 0;
    private int bet_this_move = 0;
    private boolean isFolded = false;
    private int total_bet = 0;
    private int Player_Move_type = 0;
    private boolean allin = false;
    /*
    0-什麼都沒做
    1-Check
    2-Call
    3-bet
    4-fold
     */
    private Card typeMax;
    private Card sCardMax;
    private Card PairCard2;
    private int type = 0;
    /*
    1-散牌
    2-一對
    3-兩隊
    4-三條
    5-順子
    6-同花
    7-葫蘆
    8-鐵支
    9-同花順
    10-皇家同花順
     */

    public int getBet_this_round() {
        return bet_this_move;
    }

    public void setBet_this_round(int bet_this_round) {
        total_bet += bet_this_round;
        this.bet_this_move = bet_this_round;
    }

    public Card getTypeMax() {
        return typeMax;
    }

    public void setTypeMax(Card typeMax) {
        this.typeMax = typeMax;
    }

    public Card getsCardMax() {
        return sCardMax;
    }

    public void setsCardMax(Card sCardMax) {
        this.sCardMax = sCardMax;
    }

    public Card getPairCard2() {
        return PairCard2;
    }

    public void setPairCard2(Card pairCard2) {
        PairCard2 = pairCard2;
    }

    public int getChips() {
        int[] chips = {4001547, 4001548, 4001549, 4001550, 4001551};
        int counter = 0;
        for(int i : chips){
            int number = Objects.requireNonNull(chr.get()).getItemQuantity(i, false);

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
                counter += number * base;
            }
        }
        return counter;
    }
    public void TakeAllChips(int temp) {
        int number_t = Objects.requireNonNull(chr.get()).getItemQuantity(4001551, false);
        if(temp >= 1000 && number_t > 0) {
            short can_take = temp/1000 > number_t?(short) number_t:(short)(temp/1000);
            Objects.requireNonNull(chr.get()).gainItem(4001551, (short) -can_take);
            temp -= (can_take * 1000);
        }

        int number_h = Objects.requireNonNull(chr.get()).getItemQuantity(4001550, false);
        if(temp >= 100 && number_h > 0) {
            short can_take = temp/100 > number_h?(short) number_h:(short)(temp/100);
            Objects.requireNonNull(chr.get()).gainItem(4001550, (short) -can_take);
            temp -= (can_take * 100);
        }

        int number_te = Objects.requireNonNull(chr.get()).getItemQuantity(4001549, false);
        if(temp >= 10 && number_te > 0) {
            short can_take = temp/10 > number_te?(short) number_te:(short)(temp/10);
            Objects.requireNonNull(chr.get()).gainItem(4001549, (short) -can_take);
            temp -= (can_take * 10);
        }

        int number_f = Objects.requireNonNull(chr.get()).getItemQuantity(4001548, false);
        if(temp >= 5 && number_f > 0) {
            short can_take = temp/5 > number_f?(short) number_f:(short)(temp/5);
            Objects.requireNonNull(chr.get()).gainItem(4001548, (short) -can_take);
            temp -= (can_take * 5);
        }

        int number_o = Objects.requireNonNull(chr.get()).getItemQuantity(4001547, false);
        if(temp >= 1 && number_o > 0) {
            short can_take = temp > number_o?(short) number_o:(short)(temp);
            Objects.requireNonNull(chr.get()).gainItem(4001547, (short) -can_take);
            temp -= (can_take);
        }
    }

    public void TakeChips(final int num) {
        int temp = getChips();
        TakeAllChips(temp);
        if(temp - num > 0)
            giveChips(temp - num);
    }

    public void giveChips(final int num){
        int temp = num;
        Objects.requireNonNull(chr.get()).gainItem(4001551, (short) (temp/1000));
        temp -= ((temp/1000)*1000);
        Objects.requireNonNull(chr.get()).gainItem(4001550, (short) (temp/100));
        temp -= ((temp/100)*100);
        Objects.requireNonNull(chr.get()).gainItem(4001549, (short) (temp/10));
        temp -= ((temp/10)*10);
        Objects.requireNonNull(chr.get()).gainItem(4001548, (short) (temp/5));
        temp -= ((temp/5)*5);
        Objects.requireNonNull(chr.get()).gainItem(4001547, (short) (temp));
    }

    public PokerPlayer(MapleCharacter chr) {
        this.chr = new WeakReference<>(chr);
    }

    public Card getCd_one() {
        return cd_one;
    }

    public void setCd_one(Card cd_one) {
        this.cd_one = cd_one;
    }

    public Card getCd_two() {
        return cd_two;
    }

    public void setCd_two(Card cd_two) {
        this.cd_two = cd_two;
    }

    public int getPlayer_Move_type() {
        return Player_Move_type;
    }

    public void setPlayer_Move_type(int player_Move_type) {
        Player_Move_type = player_Move_type;
    }

    public boolean isFolded() {
        return isFolded;
    }

    public void setFolded(boolean folded) {
        isFolded = folded;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public int getTotal_bet() {
        return total_bet;
    }

    public void setTotal_bet(int total_bet) {
        this.total_bet = total_bet;
    }

    public boolean isAllin() {
        return allin;
    }

    public void setAllin(boolean allin) {
        this.allin = allin;
    }

    public void init(){
        isFolded = false;
        bet = 0;
        type = 0;
        total_bet = 0;
        bet_this_move = 0;
        allin = false;
    }

    public WeakReference<MapleCharacter> getChr() {
        return chr;
    }

    private String Card_One_String(){
        if(cd_one != null)
            return cd_one.getCardString();
        else
            return "還沒發牌";
    }

    private String Card_Two_String(){
        if(cd_two != null)
            return cd_two.getCardString();
        else
            return "還沒發牌";
    }


    private String Card_Hand_String(){
        return Card_One_String() + " / " + Card_Two_String();
    }

    public String Card_Type_String(){
        switch (type){
            case 1:
                return "散牌";
            case 2:
                return "一對";
            case 3:
                return "兩隊";
            case 4:
                return "三條";
            case 5:
                return "順子";
            case 6:
                return "同花";
            case 7:
                return "葫蘆";
            case 8:
                return "鐵支";
            case 9:
                return "同花順";
            case 10:
                return "皇家同花順";
        }
        return "無法判定";
    }


    public void sendToPlayer(String notice){
        if(getChr().get() != null){
            Objects.requireNonNull(getChr().get()).dropMessage(notice);
        }
    }

    public void sendToPlayer(int type, String notice){
        if(getChr().get() != null){
            Objects.requireNonNull(getChr().get()).dropMessage(type, notice);
        }
    }

}
