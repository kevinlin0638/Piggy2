/* global ms */

function action(mode, type, selection) {
    ms.resetMap(ms.getMapId());
    ms.spawnMob(9300422, -2100, 1);
    ms.spawnMob(9300422, -2480, 1);
    ms.spawnMob(9300422, -3060, 1);
    ms.spawnMob(9300422, -2850, -330);
    ms.spawnMob(9300422, -2380, -330);
    ms.spawnMob(9300422, -2170, -330);
    ms.spawnMob(9300422, -1855, -612);
    ms.spawnMob(9300422, -2020, -612);
    ms.spawnMob(9300422, -3000, 1);
    ms.spawnMob(9300422, -3200, 1);
    ms.dispose();
}