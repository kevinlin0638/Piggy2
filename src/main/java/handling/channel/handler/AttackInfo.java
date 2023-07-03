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
package handling.channel.handler;

import client.MapleCharacter;
import client.skill.Skill;
import client.skill.SkillFactory;
import constants.GameConstants;
import server.MapleStatEffect;
import tools.types.AttackPair;

import java.awt.*;
import java.util.ArrayList;

public class AttackInfo {

    public int skill, charge, lastAttackTickCount;
    public ArrayList<AttackPair> allDamage;
    public Point position;
    public int display;
    public byte level, hits, targets, tbyte, speed, csstar, AOE, slot, direction;
    public boolean real = true;
    public boolean isMeleeAttack = false;
    public boolean isShootAttack = false;
    public boolean isMagicAttack = false;
    public boolean isBodyAttack = false;

    public final MapleStatEffect getAttackEffect(final MapleCharacter chr, int skillLevel, final Skill skill_) {
        if (GameConstants.isMulungSkill(skill) || GameConstants.isPyramidSkill(skill) || GameConstants.isInflationSkill(skill)) {
            skillLevel = 1;
        } else if (skillLevel <= 0) {
            if (chr.isShowErr()) {
                chr.showInfo("AttackEffect", true, "技能等級<=0");
            }
            return null;
        }

        int dd = display >> 15;
        if (GameConstants.isLinkedAranSkill(skill)) {
            final Skill skillLink = SkillFactory.getSkill(skill);
            if (dd > SkillFactory.Delay.magic6.i && dd != SkillFactory.Delay.shot.i && dd != SkillFactory.Delay.fist.i) {
                if (skillLink.getAnimation() == -1 || Math.abs(skillLink.getAnimation() - dd) > 0x10) {
                    if (skillLink.getAnimation() == -1) {
                        chr.dropMessage(5, "Please report this: animation for skill " + skillLink.getId() + " doesn't exist");
                    } else {

                        return null;
                        //AutobanManager.getInstance().autoban(chr.getClient(), "No delay hack, SkillID : " + skillLink.getWorldId() + ", animation: " + dd + ", expected: " + skillLink.getAnimation());
                    }
                    return null;
                }
            }
            return skillLink.getEffect(skillLevel);
        } // i'm too lazy to calculate the new skill types =.=
        /*if (dd > SkillFactory.Delay.magic6.i && dd != SkillFactory.Delay.shot.i && dd != SkillFactory.Delay.fist.i) {
            if (skill_.getAnimation() == -1 || Math.abs(skill_.getAnimation() - dd) > 0x10) {
				if (skill_.getAnimation() == -1) {
					chr.dropMessage(5, "Please report this: animation for skill " + skill_.getWorldId() + " doesn't exist");
				} else {
					AutobanManager.getInstance().autoban(chr.getClient(), "No delay hack, SkillID : " + skill_.getWorldId() + ", animation: " + dd + ", expected: " + skill_.getAnimation());
				}
                return null;
            }
        }*/
        return skill_.getEffect(skillLevel);
    }
}
