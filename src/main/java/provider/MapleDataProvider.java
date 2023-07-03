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
package provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class MapleDataProvider {
    private File root;
    private MapleDataDirectoryEntry rootForNavigation;

    public MapleDataProvider(File fileIn) {
        root = fileIn;
        rootForNavigation = new MapleDataDirectoryEntry(fileIn.getName(), 0, 0, null);
        fillMapleDataEntitys(root, rootForNavigation);
    }

    private void fillMapleDataEntitys(File rootPath, MapleDataDirectoryEntry wzdir) {
        File[] files = rootPath.listFiles();
        if (files == null) {
            System.err.println(rootPath.getName() + " not found");
            return;
        } else {
            Arrays.stream(files).forEachOrdered(file -> {
                String fileName = file.getName();
                if (file.isDirectory() && !fileName.endsWith(".img")) {
                    MapleDataDirectoryEntry newDir = new MapleDataDirectoryEntry(fileName, 0, 0, wzdir);
                    wzdir.addDirectory(newDir);
                    fillMapleDataEntitys(file, newDir);
                } else if (fileName.endsWith(".xml")) {
                    wzdir.addFile(new MapleDataFileEntry(fileName.substring(0, fileName.length() - 4), 0, 0, wzdir));
                }
            });
        }
    }

    public MapleData getData(String path) throws RuntimeException {
        File dataFile = new File(root, path + ".xml");
        File imageDataDir = new File(root, path);
        FileInputStream fis;
        try {
            fis = new FileInputStream(dataFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("檔案: " + path + " 不存在 " + root.getAbsolutePath());
        }
        final MapleData domMapleData;
        domMapleData = new MapleData(fis, imageDataDir.getParentFile());
        try {
            fis.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return domMapleData;
    }

    public MapleDataDirectoryEntry getRoot() {
        return rootForNavigation;
    }
}
