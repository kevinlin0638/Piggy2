package handling.Poker;

public class Card {
    private final short suit;
    /* 1 - 黑桃, 2 - 紅心, 3 - 方塊, 4 - 梅花 */
    private final short number;

    public Card(short suit, short number) {
        this.suit = suit;
        this.number = number;
    }


    public short getSuit() {
        return suit;
    }

    public short getNumber() {
        return number;
    }

    public String getSuitString(){
        switch (suit){
            case 1:
                return "黑桃";
            case 2:
                return "紅心";
            case 3:
                return "方塊";
            case 4:
                return "梅花";
            default:
                return "未知錯誤";
        }
    }

    public String getNumberString(){
        switch (number){
            case 1:
                return "A";
            case 11:
                return "J";
            case 12:
                return "Q";
            case 13:
                return "K";
            default:
                return Short.toString(number);
        }
    }

    public String getCardString(){
        return getSuitString() + " " + getNumberString();
    }
}
