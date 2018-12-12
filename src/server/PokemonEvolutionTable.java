/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

/**
 * @author FateJiki
 */
public class PokemonEvolutionTable {

    public static boolean doesEvolve(int lv) {
        return Evolutions.getEvolutionById(lv) != null;
    }

    private enum Evolutions {
        FIRST(25),
        SECOND(130),
        THIRD(250),
        FOURTH(400),;

        final int reqid;

        Evolutions(int reqid) {
            this.reqid = reqid;
        }

        public static Evolutions getEvolutionById(int lv) {
            for (Evolutions e : Evolutions.values()) {
                if (e.reqid == lv) {
                    return e;
                }
            }

            return null;
        }

    }
}
