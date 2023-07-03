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
package tools;

import provider.MapleDataDirectoryEntry;
import provider.MapleDataFileEntry;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;

import java.io.FileOutputStream;
import java.io.IOException;

/*
 * Author: Xerdox
*/
public class HairAndEyeCreator {
    public static void main(String args[]) throws IOException {
        MapleDataProvider hairSource = MapleDataProviderFactory.getDataProvider("Character.wz/Hair");
        MapleDataProvider faceSource = MapleDataProviderFactory.getDataProvider("Character.wz/Face");
        final MapleDataDirectoryEntry root = hairSource.getRoot();
        StringBuilder sb = new StringBuilder();
        FileOutputStream out = new FileOutputStream("hairAndFacesID.txt", true);
        System.out.println("Loading Male Hairs!");
        sb.append("hairMale:\r\n");
        for (MapleDataFileEntry topDir : root.getFiles()) {
            int id = Integer.parseInt(topDir.getName().substring(0, 8));
            if ((id / 1000 == 30 || id / 1000 == 33) && id % 10 == 0) {
                sb.append(id).append(", ");
            }
        }
        System.out.println("Loading Female Hairs!");
        sb.append("\r\n\r\n");
        sb.append("hairFemale:\r\n");
        for (MapleDataFileEntry topDir : root.getFiles()) {
            int id = Integer.parseInt(topDir.getName().substring(0, 8));
            if ((id / 1000 == 31 || id / 1000 == 34) && id % 10 == 0) {
                sb.append(id).append(", ");
            }
        }
        System.out.println("Loading Male Faces!");
        sb.append("\r\n\r\n");
        sb.append("faceMale:\r\n");
        final MapleDataDirectoryEntry root2 = faceSource.getRoot();
        for (MapleDataFileEntry topDir2 : root2.getFiles()) {
            int id = Integer.parseInt(topDir2.getName().substring(0, 8));
            if ((id / 1000 == 20) && id % 1000 < 100) {
                sb.append(id).append(", ");
            }
        }
        System.out.println("Loading Female Faces!");
        sb.append("\r\n\r\n");
        sb.append("faceFemale:\r\n");
        for (MapleDataFileEntry topDir2 : root2.getFiles()) {
            int id = Integer.parseInt(topDir2.getName().substring(0, 8));
            if ((id / 1000 == 21) && id % 1000 < 100) {
                sb.append(id).append(", ");
            }
        }
        sb.append("\r\n\r\n");
        sb.append("Author: Xerdox\r\n");
        sb.append("Happy Mapling!");
        out.write(sb.toString().getBytes());
    }
}  