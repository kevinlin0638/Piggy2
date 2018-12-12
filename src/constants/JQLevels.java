package constants;

/**
 * @author: Eric of Maple Ascension
 * @rev: 1.2 - Added a case-switching getNameById
 * @desc: Eric's JQ Leveling System
 */

public enum JQLevels {
    Newbie(1),
    Beginner(2),
    Intermediate(3),
    JQer(4),
    Advanced_JQer(5),
    Insane_JQer(6),
    JQ_Addict(7),
    Super_JQer(8),
    Ultimate_JQer(9),
    JQ_Master(10),
    God_Of_JQs(100);

    final int jqlevelid;

    private JQLevels(int id) {
        this.jqlevelid = id;
    }

    public static JQLevels getById(int id) {
        for (JQLevels Id : values()) {
            if (Id.getId() == id) {
                return Id;
            }
        }
        return null;
    }

    public static String getNameById(int id) {
        switch (id) {
            case 1:
                return "Newbie";
            case 2:
                return "Beginner";
            case 3:
                return "Intermediate";
            case 4:
                return "JQer";
            case 5:
                return "Advanced JQer";
            case 6:
                return "Insane JQer";
            case 7:
                return "JQ Addict";
            case 8:
                return "Super JQer";
            case 9:
                return "<Ultimate JQer>"; // reachable
            case 10:
                return "<JQ Master>"; // reachable
            case 100:
                return "<God of JQs>"; // unreachable, requires quest
        }
        return "None"; // 0, cause 1 = default as Newbie
    }

    public int getId() {
        return this.jqlevelid;
    }

    public boolean isA(JQLevels level) {
        return (getId() >= level.getId()) && (getId() / 10 == level.getId() / 10);
    }
}