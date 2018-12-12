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
import server.MaplePortal;
import tools.FileoutputUtil;
import tools.packet.CWvsContext;

import javax.script.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class PortalScriptManager {

    private static final PortalScriptManager instance = new PortalScriptManager();
    private final static ScriptEngineFactory sef = new ScriptEngineManager().getEngineByName("nashorn").getFactory();
    private final Map<String, PortalScript> scripts = new HashMap<String, PortalScript>();

    public final static PortalScriptManager getInstance() {
        return instance;
    }

    private final PortalScript getPortalScript(final String scriptName) {
        if (scripts.containsKey(scriptName)) {
            return scripts.get(scriptName);
        }

        final File scriptFile = new File("scripts/portal/" + scriptName + ".js");
        if (!scriptFile.exists()) {
            return null;
        }

        FileReader fr = null;
        final ScriptEngine portal = sef.getScriptEngine();
        try {
            fr = new FileReader(scriptFile);
            CompiledScript compiled = ((Compilable) portal).compile(fr);
            compiled.eval();
        } catch (final Exception e) {
            System.err.println("Error executing Portalscript: " + scriptName + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing Portal script. (" + scriptName + ") " + e);
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (final IOException e) {
                    System.err.println("ERROR CLOSING" + e);
                }
            }
        }
        final PortalScript script = ((Invocable) portal).getInterface(PortalScript.class);
        scripts.put(scriptName, script);
        return script;
    }

    public final void executePortalScript(final MaplePortal portal, final MapleClient c) {
        final PortalScript script = getPortalScript(portal.getScriptName());

        if (script != null) {
            try {
                script.enter(new PortalPlayerInteraction(c, portal));
            } catch (Exception e) {
                System.err.println("Error entering Portalscript: " + portal.getScriptName() + " : " + e);
            }
        } else {
            System.out.println("Unhandled portal script " + portal.getScriptName() + " on map " + c.getPlayer().getMapId());
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Unhandled portal script " + portal.getScriptName() + " on map " + c.getPlayer().getMapId());
            //    try {
            //        createPortalScript(c,portal.getScriptName(), c.getPlayer().getLastMap(), c.getPlayer().getLastPortal());
            //    } catch (IOException ex) {
            //        ex.printStackTrace();
            //    }
        }
    }

    private void createPortalScript(MapleClient c, String name, int mapid, MaplePortal portal) throws IOException {
        int portalnpc = 9270031;// choose an NPC for this
        if (portalnpc == 0)
            return;
        NPCScriptManager.getInstance().start(c, portalnpc);// ask if trying to get back
        c.sendPacket(CWvsContext.enableActions());
        if (!c.getPlayer().getReturningToMap())
            return; // want to get back = true
        String fname = "./scripts/portal/" + name + ".js";
        if (new File(fname).exists()) return; // make sure script does not exist already.
        PrintWriter pw = new PrintWriter(fname);
        pw.println("function enter(pi) {");
        pw.format("   pi.warp(%s,%s);\r\n", mapid, portal.getId());
        pw.println("}");
        pw.flush();
        pw.close();
        System.out.print("Script created back to map [" + mapid + "]\r\n");
    }

    public final void clearScripts() {
        scripts.clear();
    }
}
