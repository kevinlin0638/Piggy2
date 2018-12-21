package Gui;

import com.alee.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import tools.types.Pair;

/**
 * 初始化服務器配置，比如更新數據庫格式等
 */
public class InitializeServer {

    public static boolean initializeRedis(boolean reload, Start.ProgressBarObservable observable) {
        try {
            List<String> indexs = new ArrayList<>(Arrays.asList(
                    "請稍後...",
                    "請稍後...",
                    "請稍後...",
                    "請稍後...",
                    "請稍後...",
                    "請稍後...",
                    "請稍後...",
                    "請稍後...",
                    "請稍後...",
                    "請稍後...",
                    "請稍後...",
                    "請稍後..."
            ));

            int index = -1;
            int currPro = observable.getProgress();
            int singlePro = (100 - currPro) / (indexs.size() + 1);
            int dey = 50;
            // 加載技能冷卻時間
            observable.setProgress(new Pair<>(indexs.get(++index), currPro + singlePro * (index + 1)));
            ThreadUtils.sleepSafely(dey);
            // 加載技能數據
            observable.setProgress(new Pair<>(indexs.get(++index), currPro + singlePro * (index + 1)));
            ThreadUtils.sleepSafely(dey);
            // 加載複製技能
            observable.setProgress(new Pair<>(indexs.get(++index), currPro + singlePro * (index + 1)));
            ThreadUtils.sleepSafely(dey);
            // 加載道具數據
            observable.setProgress(new Pair<>(indexs.get(++index), currPro + singlePro * (index + 1)));
            ThreadUtils.sleepSafely(dey);
            // 加載套裝數據
            observable.setProgress(new Pair<>(indexs.get(++index), currPro + singlePro * (index + 1)));
            ThreadUtils.sleepSafely(dey);
            // 加載潛能數據
            observable.setProgress(new Pair<>(indexs.get(++index), currPro + singlePro * (index + 1)));
            ThreadUtils.sleepSafely(dey);
            // 加載星巖數據
            observable.setProgress(new Pair<>(indexs.get(++index), currPro + singlePro * (index + 1)));
            ThreadUtils.sleepSafely(dey);
            // 加載地圖信息
            observable.setProgress(new Pair<>(indexs.get(++index), currPro + singlePro * (index + 1)));
            ThreadUtils.sleepSafely(dey);
            // 加載NPC名稱與任務數量
            observable.setProgress(new Pair<>(indexs.get(++index), currPro + singlePro * (index + 1)));
            ThreadUtils.sleepSafely(dey);
            // 加載怪物爆率
            observable.setProgress(new Pair<>(indexs.get(++index), currPro + singlePro * (index + 1)));
            ThreadUtils.sleepSafely(dey);
            // 加載怪物名稱
            ThreadUtils.sleepSafely(dey);
            // 加載怪物技能信息
            observable.setProgress(new Pair<>(indexs.get(++index), currPro + singlePro * (index + 1)));
            ThreadUtils.sleepSafely(dey);
            // 加載商店數據
            observable.setProgress(new Pair<>(indexs.get(++index), 90));
            ThreadUtils.sleepSafely(dey);
            // 將數據庫內所有角色數據保存到Redis
            //clearAllPlayerCache();
            observable.setProgress(new Pair<>("服務端初始化完成，正在啟動主介面...", 100));
        } catch (Exception e) {
            //log.error("服務端初始化失敗", e);
            Start.showMessage("服務端初始化失敗", "錯誤", 0);
//            RedisUtil.flushall();
            System.exit(0);
        }

        return true;
    }
}
