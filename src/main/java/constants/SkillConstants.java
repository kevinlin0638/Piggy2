package constants;

/**
 * Created by Weber on 2017/10/2.
 */
public class SkillConstants {

    public static int getJobBySkill(int skillId) {
        int result = skillId / 10000;
        if (skillId / 10000 == 8000) {
            result = skillId / 100;
        }
        return result;
    }

    public static boolean isKeyDownSkillWithPos(int skillId) {
        return skillId == 2121001 || skillId == 2221001 || skillId == 2321001;
    }

}
