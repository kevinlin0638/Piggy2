/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package scripting;

import client.MapleClient;
import constants.ServerConstants;
import server.quest.MapleQuest;
import tools.FileoutputUtil;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;

public class NPCScriptManager extends AbstractScriptManager {

    private static final NPCScriptManager instance = new NPCScriptManager();
    private final Map<MapleClient, NPCConversationManager> cms = new WeakHashMap<>();

    public static NPCScriptManager getInstance() {
        return instance;
    }

    public final void start(final MapleClient c, final int npc) {
        start(c, npc, null);
    }

    public final void start(final MapleClient c, final int npc, String script) {
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (c.getPlayer().isShowInfo()) {
                c.getPlayer().showInfo("NPC腳本", false, "開啟對話，NPC：" + npc + " 特殊腳本：" + script + c.getPlayer().getMap());
            }
            if (!cms.containsKey(c) && c.canClickNPC()) {
                Invocable iv;
                if (script == null || script.equals("0")) {
                    iv = getInvocable("npc/" + npc + ".js", c, true); //safe disposal
                } else {
                    iv = getInvocable("special/" + script + ".js", c, true); //safe disposal
                }
                if (iv == null) {
                    if (c.getPlayer().isShowErr()) {
                        c.getPlayer().showInfo("NPC腳本", true, "找不到NPCID:" + npc + " 特殊腳本:" + script + c.getPlayer().getMap());
                    }
                    System.out.println("\r\n找不到NPCID:" + npc + " 特殊腳本:" + script + c.getPlayer().getMap() + "\r\n");
                    iv = getInvocable("special/notcoded.js", c, true); //safe disposal
                    if (iv == null) {
                        dispose(c);
                        return;
                    }
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc, -1, script, ScriptType.NPC, iv);
                cms.put(c, cm);
                scriptengine.put("cm", cm);

                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                try {
                    iv.invokeFunction("start"); // Temporary until I've removed all of start
                } catch (NoSuchMethodException nsme) {
                    iv.invokeFunction("action", (byte) 1, (byte) 0, 0);
                }
            } else {
                if (c.getPlayer().isShowErr()) {
                    c.getPlayer().showInfo("NPC腳本", true, "無法執行腳本:已有腳本執行-" + cms.containsKey(c) + " | 允許執行腳本-" + c.canClickNPC());
                }
                NPCConversationManager cm = cms.get(c);
                if (cm == null || (cm.getType() != ScriptType.ON_USER_ENTER && cm.getType() != ScriptType.ON_FIRST_USER_ENTER)) {
                    c.getPlayer().dropMessage(-1, "你當前已經和1個NPC對話了. 如果不是請輸入 @解卡 指令進行解卡。");
                }
            }
        } catch (final ScriptException | NoSuchMethodException e) {
            System.err.println("Error executing NPC script, NPC ID : " + npc + "." + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing NPC script, NPC ID : " + npc + "." + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void action(final MapleClient c, final byte mode, final byte type, final int selection) {
        if (mode != -1) {
            final NPCConversationManager cm = cms.get(c);
            if (cm == null || cm.getLastMsg() != null) {
                return;
            }
            final Lock lock = c.getNPCLock();
            lock.lock();
            try {

                if (cm.pendingDisposal) {
                    dispose(c);
                } else {
                    c.setClickedNPC();
                    cm.getIv().invokeFunction("action", mode, type, selection);
                }
            } catch (final ScriptException | NoSuchMethodException e) {
                String str;
                switch (cm.getType()) {
                    case NPC:
                        str = "執行NPC腳本出錯 : NPC - " + cm.getNpc() + " 特殊腳本 - " + cm.getScript();
                        break;
                    case ITEM:
                        str = "執行道具腳本出錯 : NPC - " + cm.getNpc() + " 腳本 - " + cm.getScript();
                        break;
                    case ON_USER_ENTER:
                        str = "執行地圖onUserEnter腳本出錯 : 腳本 - " + cm.getScript();
                        break;
                    case ON_FIRST_USER_ENTER:
                        str = "執行地圖onFirstUserEnter腳本出錯 : 腳本 - " + cm.getScript();
                        break;
                    default:
                        str = "執行腳本出錯 : NPC - " + cm.getNpc() + " 腳本 - " + cm.getScript();
                }
                System.err.println(str + " ." + e);
                FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "\r\n\r\n" + str + " ." + e + "\r\n\r\n");
                dispose(c);
                notice(c, cm.getNpc(), cm.getScript(), cm.getType());
            } finally {
                lock.unlock();
            }
        }
    }

    public final void startQuest(final MapleClient c, final int npc, final int quest) {
        if (!MapleQuest.getInstance(quest).canStart(c.getPlayer(), null)) {
            if (c.getPlayer().isShowErr()) {
                c.getPlayer().showInfo("任務開始腳本", true, "無法開始任務 NPC：" + npc + " 任務：" + quest);
            }
            return;
        }
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (c.getPlayer().isShowInfo()) {
                c.getPlayer().showInfo("任務開始腳本", false, "腳本 - 開始任務 NPC：" + npc + " 任務：" + quest);
            }
            if (!cms.containsKey(c) && c.canClickNPC()) {
                final Invocable iv = getInvocable("quest/" + quest + ".js", c, true);
                if (iv == null) {
                    dispose(c);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc, quest, null, ScriptType.QUEST_START, iv);
                cms.put(c, cm);
                scriptengine.put("qm", cm);

                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                iv.invokeFunction("start", (byte) 1, (byte) 0, 0); // start it off as something
            } else if (c.getPlayer().isShowErr()) {
                c.getPlayer().showInfo("任務開始腳本", true, "無法執行腳本:已有腳本執行-" + cms.containsKey(c) + " | 允許執行腳本-" + c.canClickNPC());
            }
        } catch (final ScriptException | NoSuchMethodException e) {
            System.err.println("執行任務開始腳本出錯 : NPC - " + npc + "任務 - " + quest + " ." + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "\r\n\r\n執行任務開始腳本出錯 : NPC - " + npc + "任務 - " + quest + " ." + e + "\r\n\r\n");
            dispose(c);
            notice(c, npc, String.valueOf(quest), ScriptType.QUEST_START);
        } finally {
            lock.unlock();
        }
    }

    public final void startQuest(final MapleClient c, final byte mode, final byte type, final int selection) {
        final Lock lock = c.getNPCLock();
        final NPCConversationManager cm = cms.get(c);
        if (cm == null || cm.getLastMsg() != null) {
            return;
        }
        lock.lock();
        try {
            if (cm.pendingDisposal) {
                dispose(c);
            } else {
                c.setClickedNPC();
                cm.getIv().invokeFunction("start", mode, type, selection);
            }
        } catch (ScriptException | NoSuchMethodException e) {
            System.err.println("執行任務開始腳本出錯 : NPC - " + cm.getNpc() + "任務 - " + MapleQuest.getInstance(cm.getQuest()).getId() + " ." + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "\r\n\r\n執行任務開始腳本出錯 : NPC - " + cm.getNpc() + "任務 - " + MapleQuest.getInstance(cm.getQuest()).getId() + " ." + e + "\r\n\r\n");
            dispose(c);
            notice(c, cm.getNpc(), String.valueOf(cm.getQuest()), cm.getType());
        } finally {
            lock.unlock();
        }
    }

    public final void endQuest(final MapleClient c, final int npc, final int quest, final boolean customEnd) {
        if (!customEnd && !MapleQuest.getInstance(quest).canComplete(c.getPlayer(), null)) {
            if (c.getPlayer().isShowErr()) {
                c.getPlayer().showInfo("任務完成腳本", true, "無法完成任務 NPC：" + npc + " 任務：" + MapleQuest.getInstance(quest).getId());
            }
            return;
        }
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (c.getPlayer().isShowInfo()) {
                c.getPlayer().showInfo("任務完成腳本", false, "腳本 - 完成任務 NPC：" + npc + " 任務：" + MapleQuest.getInstance(quest).getId());
            }
            if (!cms.containsKey(c) && c.canClickNPC()) {
                final Invocable iv = getInvocable("quest/" + quest + ".js", c, true);
                if (iv == null) {
                    dispose(c);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc, quest, null, ScriptType.QUEST_END, iv);
                cms.put(c, cm);
                scriptengine.put("qm", cm);

                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                //System.out.println("NPCID started: " + npc + " endquest " + quest);
                iv.invokeFunction("end", (byte) 1, (byte) 0, 0); // start it off as something
            }
        } catch (ScriptException | NoSuchMethodException e) {
            System.err.println("執行任務完成腳本出錯 : NPC - " + npc + "任務 - " + MapleQuest.getInstance(quest) + " ." + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "\r\n\r\n執行任務完成腳本出錯 : NPC - " + npc + "任務 - " + MapleQuest.getInstance(quest).getId() + " ." + e + "\r\n\r\n");
            dispose(c);
            notice(c, npc, String.valueOf(quest), ScriptType.QUEST_END);
        } finally {
            lock.unlock();
        }
    }

    public final void endQuest(final MapleClient c, final byte mode, final byte type, final int selection) {
        final Lock lock = c.getNPCLock();
        final NPCConversationManager cm = cms.get(c);
        if (cm == null || cm.getLastMsg() != null) {
            return;
        }
        lock.lock();
        try {
            if (cm.pendingDisposal) {
                dispose(c);
            } else {
                c.setClickedNPC();
                cm.getIv().invokeFunction("end", mode, type, selection);
            }
        } catch (ScriptException | NoSuchMethodException e) {
            System.err.println("執行任務完成腳本出錯 : NPC - " + cm.getNpc() + "任務 - " + MapleQuest.getInstance(cm.getQuest()).getId() + " ." + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "\r\n\r\n執行任務完成腳本出錯 : NPC - " + cm.getNpc() + "任務 - " + MapleQuest.getInstance(cm.getQuest()).getId() + " ." + e + "\r\n\r\n");
            dispose(c);
            notice(c, cm.getNpc(), String.valueOf(cm.getQuest()), cm.getType());
        } finally {
            lock.unlock();
        }
    }

    public final void onUserEnter(final MapleClient c, final String script) {
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (c.getPlayer().isShowInfo()) {
                c.getPlayer().showInfo("onUserEnter腳本", false, "開始onUserEnter腳本：" + script + c.getPlayer().getMap());
            }
            if (!cms.containsKey(c)) {
                final Invocable iv = getInvocable("地圖/onUserEnter/" + script + ".js", c, true);
                if (iv == null) {
                    if (c.getPlayer().isShowErr()) {
                        c.getPlayer().showInfo("onUserEnter腳本", true, "找不到onUserEnter腳本:" + script + c.getPlayer().getMap());
                    }
                    System.out.println("\r\n找不到onUserEnter腳本:" + script + c.getPlayer().getMap() + "\r\n");
                    dispose(c);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, 0, -1, script, ScriptType.ON_USER_ENTER, iv);
                cms.put(c, cm);
                scriptengine.put("ms", cm);
                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                try {
                    iv.invokeFunction("start");
                } catch (NoSuchMethodException nsme) {
                    iv.invokeFunction("action", (byte) 1, (byte) 0, 0);
                }
            } else if (ServerConstants.DEBUG) {
                c.getPlayer().showInfo("onUserEnter腳本", true, "無法執行腳本:已有腳本執行-" + cms.containsKey(c));
            }
        } catch (final ScriptException | NoSuchMethodException e) {
            System.err.println("執行onUserEnter腳本出錯 : " + script + ". " + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "執行onUserEnter腳本出錯 : " + script + ". " + e);
            dispose(c);
            notice(c, 0, script, ScriptType.ON_USER_ENTER);
        } finally {
            lock.unlock();
        }
    }

    public final void onFirstUserEnter(final MapleClient c, final String script) {
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (c.getPlayer().isShowInfo()) {
                c.getPlayer().showInfo("onFirstUserEnter腳本", false, "開始onFirstUserEnter腳本：" + script + c.getPlayer().getMap());
            }
            if (!cms.containsKey(c)) {
                final Invocable iv = getInvocable("地圖/onFirstUserEnter/" + script + ".js", c, true);
                if (iv == null) {
                    if (c.getPlayer().isShowErr()) {
                        c.getPlayer().showInfo("onFirstUserEnter腳本", true, "找不到onFirstUserEnter腳本:" + script + c.getPlayer().getMap());
                    }
                    System.out.println("\r\n找不到onFirstUserEnter腳本:" + script + c.getPlayer().getMap() + "\r\n");
                    dispose(c);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, 0, -1, script, ScriptType.ON_FIRST_USER_ENTER, iv);
                cms.put(c, cm);
                scriptengine.put("ms", cm);
                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                try {
                    iv.invokeFunction("start");
                } catch (NoSuchMethodException nsme) {
                    iv.invokeFunction("action", (byte) 1, (byte) 0, 0);
                }
            } else if (c.getPlayer().isShowErr()) {
                c.getPlayer().showInfo("onFirstUserEnter腳本", true, "無法執行腳本:已有腳本執行-" + cms.containsKey(c));
            }
        } catch (final ScriptException | NoSuchMethodException e) {
            System.err.println("執行地圖onFirstUserEnter腳本出錯 : " + script + ". " + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "執行地圖onFirstUserEnter腳本出錯 : " + script + ". " + e);
            dispose(c);
            notice(c, 0, script, ScriptType.ON_FIRST_USER_ENTER);
        } finally {
            lock.unlock();
        }
    }

    public final void dispose(final MapleClient c) {
        final NPCConversationManager npccm = cms.get(c);

        if (npccm != null) {
            if (c.getPlayer().isShowInfo()) {
                c.getPlayer().showInfo("NPC腳本", false, "結束對話，NPC：" + npccm.getNpc() + " 特殊腳本：" + npccm.getScript() + c.getPlayer().getMap());
            }
            cms.remove(c);
            switch (npccm.getType()) {
                case NPC:
                    c.removeScriptEngine("scripts/npc/" + npccm.getNpc() + ".js");
                    c.removeScriptEngine("scripts/special/" + npccm.getScript() + ".js");
                    c.removeScriptEngine("scripts/special/notcoded.js");
                    break;
                case ITEM:
                    c.removeScriptEngine("scripts/item/" + npccm.getScript() + ".js");
                    break;
                case ON_USER_ENTER:
                    c.removeScriptEngine("scripts/map/onUserEnter/" + npccm.getScript() + ".js");
                    break;
                case ON_FIRST_USER_ENTER:
                    c.removeScriptEngine("scripts/map/onFirstUserEnter/" + npccm.getScript() + ".js");
                    break;
                default:
                    c.removeScriptEngine("scripts/quest/" + npccm.getQuest() + ".js");
            }
        }
        if (c.getPlayer() != null && c.getPlayer().getConversation() == 1) {
            c.getPlayer().setConversation(0);
        }
    }

    public final NPCConversationManager getCM(final MapleClient c) {
        return cms.get(c);
    }

    private void notice(MapleClient c, int npcId, String script, ScriptType type) {
        String str;
        switch (type) {
            case NPC:
                str = "NPC腳本出錯-NPC:" + npcId + (script != null ? " 特殊腳本:" + script : "");
                break;
            case ITEM:
                str = "道具腳本出錯-NPC:" + npcId + " 腳本:" + script;
                break;
            case ON_USER_ENTER:
                str = "onUserEnter腳本出錯-腳本:" + script;
                break;
            case ON_FIRST_USER_ENTER:
                str = "onFirstUserEnter腳本出錯-腳本:" + script;
                break;
            default:
                str = "腳本出錯-NPC:" + npcId + " 腳本:" + script;
        }
        c.getPlayer().dropMessage(1, str);
    }
}
