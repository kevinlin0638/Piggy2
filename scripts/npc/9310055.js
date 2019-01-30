/*
npc:克里夏
功能:萬能NPC
作者:彥儒louis

*/
var se1 ;
var se2 ;
var se3 ;
var se4 ;
var status 
//地圖類型
var map_type_List=[
	["村莊"],
	["BOSS圖"],
	["練功圖"],
	["#r武陵道場#k"],
	["#r自由市場#k"],
	["#b拍照聖地#k"],
	["#b轉蛋地圖#k"]
]
//練功圖
var practice_maps_List=[//id,  text
	[541010010,"#b幽靈船2#k","#b幽靈清潔工#k"],
	[541020500,"#b烏魯城中心#k","#b很多強怪#k"],
	[261010103,"#b研究所203號房#k","#b杯子#k"],
]
//村莊圖 
var city_maps_List=[//id,  text
	 [120000000,"#b鯨魚號#k"],[102000000,"#b勇士之村#k"], [101000000,"#b魔法森林#k"], [140000000,"#b瑞恩村#k"], [100000000,"#b弓箭手村#k"], [103000000,"#b墮落城市#k"],
	 [680000000,"#b結婚小鎮#k"],[200000000,"#b天空之城#k"],[110000000,"#b黃金海岸#k"],[221000000,"#b地球防禦本部#k"],[222000000,"#b童話村#k"],
	 [230000000,"#b水世界#k"],[211000000,"#b冰原雪域#k"],[220000000,"#b玩具城#k"],[260000000,"#b沙漠#k"],[250000000,"#b桃花仙境#k"],
	 [105040300,"#b奇幻村#k"],[600000000,"#b新葉城#k"],[ 800000000,"#b古代神社#k"],[801000000,"#b昭和村#k"],[682000000,"#b鬧鬼宅邸外部#k"],[240000000,"#b神木村#k"],
	 [270000100,"#b時間神殿#k"],[130000200,"#b耶雷佛#k"],[702100000,"#b少林寺#k"],[501000000,"#b黃金寺廟#k"],
	 [802000101,"#b未來東京#k"],[103040000,"#b台北101#k"],[540000000,"#b新加坡中心商務區#k"],[541000000,"#b新加坡駁船碼頭城#k"]
]
//BOSS圖
var selectedMap = 910000000;
var Boss_maps_List = [//id,  text
	//[220080000,"#r拉圖斯#k"],
	[800020130,"#r天狗"],
	[800040410,"#r天皇#k"],
	[240050400,"#r暗黑龍王(11頻以上渾沌)"],
	[211042200,"#r殘暴炎魔"],
	[270050000,"#r皮卡啾#k"],
	[551030100,"#r雄獅王#k"],
	[272020110,"#r阿卡伊農#k"],
	[211070000,"#r獅子王城#k"],
	[271040000,"#r希格諾斯#k"]
]

function start() {
	status = -1;
    action(1, 0, 0);
	
}

function action(mode, type, selection) {

	if (mode == 0) {
	cm.dispose();
	return;
    } else if (mode == 1){
	status++;
    } else {
	status--;
    }
	
	switch(status){
		case 0:
			var Text = "我可以進行地圖傳送，請問要取哪裡呀？#b\r\n\r\n";
			for(var i = 0; i < map_type_List.length;i++){
				if(i%5!=0||i==0)
				{
					Text += "#L" + i + "#" + map_type_List[i];
				}
				else
				{
					Text += "\r\n"+"#L" + i + "#" + map_type_List[i];
				}
					
				
			}
			cm.sendOk(Text);	
			
			break;	
		case 1:
				switch(selection){
					case 0:
						var Text = "選擇你的目的地#b\r\n\r\n";
						for (var i = 0; i < city_maps_List.length; i++) {
							Text += "\r\n#L" + i + "#" + city_maps_List[i][1]+ "";
						}
						se3=0;
						cm.sendOk(Text);		
					
					
					break;
					case 1:
						var Text = "選擇你要挑戰的#b\r\n\r\n";
						for (var i = 0; i < Boss_maps_List.length; i++) {
                            Text += "\r\n#L" + i + "#" + Boss_maps_List[i][1]+ "";
						}
						se3=1;
						cm.sendOk(Text);		
					
					
					break;
					case 2:
						var Text = "選擇你要練功的地方#b\r\n\r\n";
						for (var i = 0; i < practice_maps_List.length; i++) {
                            Text += "\r\n#L" + i + "#" + practice_maps_List[i][1]+ "";
						}
						se3=2;
						cm.sendOk(Text);	
					break;	
					case 3:
						cm.warp(925020000, 0);
						cm.dispose();
					break;
					case 4:
						cm.warp(910000000, 0);
						cm.dispose();
					break;
					case 5:
						cm.warp(970000000, 0);
						cm.dispose();
					break;
					case 6:
						cm.warp(749050400, 0);
						cm.dispose();
					break;
				}
			break;
		case 2:
			if(se3==0)
			{
				cm.sendYesNo("你真的要去#r" +  city_maps_List[selection][1] + "嗎?");
                selectedMap = selection;
				se4=0;
			}
			else if(se3==1)
			{
				cm.sendYesNo("你真的要打#r" + Boss_maps_List[selection][1] + "嗎?");
                selectedMap = selection;
				se4=1;
			}
			else if(se3==2)
			{
				cm.sendYesNo("你真的要打#r" + practice_maps_List[selection][1] + "嗎?\r\n那裡有"+practice_maps_List[selection][2]+"喔!");
                selectedMap = selection;
				se4=2;
			}
		break;
		case 3:
			if(se4==0)
			{
				cm.warp(city_maps_List[selectedMap][0], 0);
                cm.dispose();
			}
			else if(se4==1)
			{
				cm.warp( Boss_maps_List[selectedMap][0], 0);
                cm.dispose();
			}
			else if(se4==2)
			{
				cm.warp( practice_maps_List[selectedMap][0], 0);
                cm.dispose();
			}
		break;
	}
	

}
