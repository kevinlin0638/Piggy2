/**
 * @author: Eric of Maple Ascension
 * @rev: 2.2 - Enabled None for Rollback workarounds
 * @desc: Eric's Occupational System
 */
package constants;

public enum Occupations {

    None(0), // fixes the possibility of returning null by default 0
    Pioneer(1),
    Sniper(100),
    Leprechaun(200),
    NX_Addict(300),
    Hacker(400),
    Eric_IdoL(500),
    The_Transformers_AutoBots(600),
    Smega_Whore(700),
    Terrorist(800),
    TrollMaster(9001);

    final int jobid;

    private Occupations(int id) {
        jobid = id;
    }

    public static Occupations getById(int id) {
        for (Occupations i : Occupations.values()) {
            if (i.getId() == id) {
                return i;
            }
        }
        return null;
    }

    public static String getNameById(int id) {
        switch (id) {
            case 0:
                return "None";
            case 1:
                return "Pioneer";
            case 100:
                return "Sniper";
            case 200:
                return "Leprechaun";
            case 300:
                return "NX Addict";
            case 400:
                return "Hacker";
            case 500:
                return "Eric IdoL"; // unachievable
            case 600:
                return "The Transformers AutoBots";
            case 700:
                return "Smega Whore";
            case 800:
                return "Terorrist";
            case 9001:
                return "Troll Master"; // unachievable
        }
        return "{{Occupation is -1}}"; // -1, cause 0 = none and 1 = default as Wizer
    }

    public static final String toString(final String occName) {
        StringBuilder builder = new StringBuilder(occName.length() + 1);
        for (String word : occName.split("_")) {
            if (word.length() <= 2) {
                builder.append(word); // assume that it's an abbrevation
            } else {
                builder.append(word.charAt(0));
                builder.append(word.substring(1).toLowerCase());
            }
            builder.append(' ');
        }
        return builder.substring(0, occName.length());
    }

    public int getId() {
        return jobid;
    }

    public boolean is(Occupations job) {
        return getId() >= job.getId() && getId() / 10 == job.getId() / 10;
    }
}
