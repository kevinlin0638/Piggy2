/*弓箭手轉蛋機*/

var ttt6 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
var time = new Date();
var hour = time.getHours(); //獲得小時
var minute = time.getMinutes(); //獲得分鐘
var second = time.getSeconds(); //獲得秒
var Year = time.getFullYear();
var month = time.getMonth() + 1; //獲取当前月份(0-11,0代表1月)
var dates = time.getDate(); //獲取当前日(1-31)
var status = -1;
var rand = 0;
var nx = false;
var nxx = false;
var price = 250000;
var priceD = 3;
var debug = false;




/*以下為要修改的*/
var rates = [10000,60000,430000,500000];//總共100萬 最好->最差

//物品代碼 數量 時間期限(沒有就0)  只有Advanced會廣播
var itemListAdvanced = Array(
	Array(2049122, 1, 0),
	Array(2049120, 1, 0),//1
	Array(2049120, 1, 0),
	Array(2049120, 1, 0),
	Array(2049119, 1, 0),//4
	Array(2049119, 1, 0),
	Array(2049119, 1, 0),
	Array(2049119, 1, 0),
	Array(2049118, 1, 0),//8
	Array(2049118, 1, 0),
	Array(2049118, 1, 0),
	Array(2049118, 1, 0),
	Array(2049118, 1, 0)
);
var itemListGold = Array(
	Array(2049121, 1, 0),
	Array(2046002, 1, 0),
	Array(2046003, 1, 0),
	Array(2046102, 1, 0),
	Array(2046103, 1, 0),

	Array(2040206, 1, 0),
	Array(2040301, 1, 0),
	Array(2340000, 1, 0),
	Array(2049300, 1, 0),
	Array(2049117, 1, 0),
	Array(2049100, 1, 0)
);
var itemListSilver = Array(
	Array(2043401, 1, 0), //锟組锟斤拷?锟絆锟斤拷? 60% 
	Array(2043001, 1, 0), //?锟斤拷?锟斤拷?锟斤拷?60%
	Array(2043004, 1, 0), //?锟斤拷?锟斤拷??锟紾锟斤拷?70%
	Array(2043006, 1, 0), //?锟斤拷?锟絔锟絆?锟紾锟斤拷?70%
	Array(2043009, 1, 0), //?锟斤拷?锟絔锟絆锟斤拷?60%
	Array(2043011, 1, 0), //?锟斤拷?锟斤拷?锟斤拷?65%
	Array(2043016, 1, 0), //?锟斤拷?锟絉锟斤拷锟絭锟斤拷?70%
	Array(2043017, 1, 0), //?锟斤拷?锟絉锟斤拷锟絭锟斤拷?60%
	Array(2043024, 1, 0), //?锟斤拷?锟絉锟斤拷锟斤拷?65%
	Array(2043101, 1, 0), //?锟斤拷锟斤拷?锟斤拷?60%
	Array(2043104, 1, 0), //?锟斤拷锟斤拷??锟紾锟斤拷?70%
	Array(2043106, 1, 0), //?锟斤拷锟斤拷?锟斤拷?65%
	Array(2043111, 1, 0), //?锟斤拷锟絉锟斤拷锟絭锟斤拷?70%
	Array(2043112, 1, 0), //?锟斤拷锟絉锟斤拷锟絭锟斤拷?60%
	Array(2043118, 1, 0), //?锟斤拷锟絉锟斤拷锟斤拷?65%
	Array(2043201, 1, 0), //?锟斤拷?锟斤拷锟斤拷?锟斤拷?60%
	Array(2043204, 1, 0), //?锟斤拷?锟斤拷锟斤拷??锟紾锟斤拷?70%
	Array(2043206, 1, 0), //?锟斤拷?锟斤拷锟斤拷?锟斤拷?65%
	Array(2043211, 1, 0), //?锟斤拷?锟斤拷锟絉锟斤拷锟絭锟斤拷?70%
	Array(2043212, 1, 0), //?锟斤拷?锟斤拷锟絉锟斤拷锟絭锟斤拷?60%
	Array(2043218, 1, 0), //?锟斤拷?锟斤拷锟絉锟斤拷锟斤拷?65%
	Array(2043301, 1, 0), //锟絬?锟斤拷?锟斤拷?60%
	Array(2043304, 1, 0), //锟絬?锟斤拷??锟紾锟斤拷?70%
	Array(2043306, 1, 0), //锟絬?锟斤拷?锟斤拷?65%
	Array(2043701, 1, 0), //锟絬锟斤拷锟絔锟絆锟斤拷?60%
	Array(2043704, 1, 0), //锟絬锟斤拷锟絔锟絆?锟紾锟斤拷?70%
	Array(2043706, 1, 0), //锟絬锟斤拷锟絔锟絆锟斤拷?65%
	Array(2043801, 1, 0), //?锟斤拷锟絔锟絆锟斤拷?60%
	Array(2043804, 1, 0), //?锟斤拷锟絔锟絆?锟紾锟斤拷?70%
	Array(2043806, 1, 0), //?锟斤拷锟絔锟絆锟斤拷?65%
	Array(2044001, 1, 0), //?锟斤拷?锟斤拷?锟斤拷?60%
	Array(2044004, 1, 0), //?锟斤拷?锟斤拷??锟紾锟斤拷?70%
	Array(2044006, 1, 0), //?锟斤拷?锟斤拷?锟斤拷?65%
	Array(2044011, 1, 0), //?锟斤拷?锟絉锟斤拷锟絭锟斤拷?70%
	Array(2044012, 1, 0), //?锟斤拷?锟絉锟斤拷锟絭锟斤拷?60%
	Array(2044026, 1, 0), //?锟斤拷?锟絉锟斤拷锟斤拷?65%
	Array(2044101, 1, 0), //?锟斤拷锟斤拷?锟斤拷?60%
	Array(2044104, 1, 0), //?锟斤拷锟斤拷??锟紾锟斤拷?70%
	Array(2044106, 1, 0), //?锟斤拷锟斤拷?锟斤拷?65%
	Array(2044111, 1, 0), //?锟斤拷锟絉锟斤拷锟絭锟斤拷?70%
	Array(2044112, 1, 0), //?锟斤拷锟絉锟斤拷锟絭锟斤拷?60%
	Array(2044118, 1, 0), //?锟斤拷锟絉锟斤拷锟斤拷?65%
	Array(2044201, 1, 0), //?锟斤拷?锟斤拷锟斤拷?锟斤拷?60%
	Array(2044204, 1, 0), //?锟斤拷?锟斤拷锟斤拷??锟紾锟斤拷?70%
	Array(2044206, 1, 0), //?锟斤拷?锟斤拷锟斤拷?锟斤拷?65%
	Array(2044211, 1, 0), //?锟斤拷?锟斤拷锟絉锟斤拷锟絭锟斤拷?70%
	Array(2044212, 1, 0), //?锟斤拷?锟斤拷锟絉锟斤拷锟絭锟斤拷?60%
	Array(2044218, 1, 0), //?锟斤拷?锟斤拷锟絉锟斤拷锟斤拷?65%
	Array(2044301, 1, 0), //?锟斤拷?锟斤拷?60%
	Array(2044304, 1, 0), //?锟斤拷??锟紾锟斤拷?70%
	Array(2044306, 1, 0), //?锟斤拷?锟斤拷?65%
	Array(2044311, 1, 0), //?锟絉锟斤拷锟絭锟斤拷?70%
	Array(2044312, 1, 0), //?锟絉锟斤拷锟絭锟斤拷?60%
	Array(2044318, 1, 0), //?锟絉锟斤拷锟斤拷?65%
	Array(2044401, 1, 0), //锟劫э拷?锟斤拷?60%
	Array(2044404, 1, 0), //锟劫э拷??锟紾锟斤拷?70%
	Array(2044406, 1, 0), //锟劫э拷?锟斤拷?65%
	Array(2044411, 1, 0), //锟劫㏑锟斤拷锟絭锟斤拷?70%
	Array(2044412, 1, 0), //锟劫㏑锟斤拷锟絭锟斤拷?60%
	Array(2044418, 1, 0), //锟劫㏑锟斤拷锟斤拷?65%
	Array(2044501, 1, 0), //锟絵锟斤拷?锟斤拷?60%
	Array(2044504, 1, 0), //锟絵锟斤拷??锟紾锟斤拷?70%
	Array(2044506, 1, 0), //锟絵锟斤拷?锟斤拷?65%
	Array(2044601, 1, 0), //锟斤拷锟斤拷?锟斤拷?60%
	Array(2044604, 1, 0), //锟斤拷锟斤拷??锟紾锟斤拷?70%
	Array(2044606, 1, 0), //锟斤拷锟斤拷?锟斤拷?65%
	Array(2044701, 1, 0), //锟斤拷锟組锟斤拷?锟斤拷?60%
	Array(2044704, 1, 0), //锟斤拷锟組锟斤拷??锟紾锟斤拷?70%
	Array(2044706, 1, 0), //锟斤拷锟組锟斤拷?锟斤拷?65%
	Array(2044801, 1, 0), //锟斤拷锟揭э拷?锟斤拷?60%
	Array(2044803, 1, 0), //锟斤拷锟揭э拷?锟斤拷?70%
	Array(2044806, 1, 0), //锟斤拷锟揭㏑锟斤拷锟絭锟斤拷?70%
	Array(2044807, 1, 0), //锟斤拷锟揭㏑锟斤拷锟絭锟斤拷?60%
	Array(2044811, 1, 0), //锟斤拷锟揭э拷?锟斤拷?65%
	Array(2044813, 1, 0), //锟斤拷?锟絉锟斤拷锟斤拷?65%
	Array(2044901, 1, 0), //锟絬?锟斤拷?锟斤拷?60%
	Array(2044903, 1, 0), //锟絬?锟斤拷?锟斤拷?70%
	Array(2044906, 1, 0) //锟絬?锟斤拷?锟斤拷?65%
	
);
var itemListNormal = Array(
	//锟斤拷戮锟絶锟斤拷锟斤拷
	Array(2040501, 1, 0),/*锟組锟紸锟捷┦憋拷*/
	Array(2040504, 1, 0),
	Array(2040513, 1, 0),
	Array(2040516, 1, 0),
	Array(2040532, 1, 0),
	
	Array(2040804, 1, 0),/*锟斤拷M锟斤拷锟斤拷锟斤拷*/
	Array(2040817, 1, 0),
	
	Array(2043400, 1, 0), //锟組锟斤拷?锟絆锟斤拷? 100%
	Array(2043402, 1, 0), //锟組锟斤拷?锟絆锟斤拷? 10%
	Array(2043000, 1, 0), //?锟斤拷?锟斤拷?锟斤拷?100%
	Array(2043002, 1, 0), //?锟斤拷?锟斤拷?锟斤拷?10%
	Array(2043005, 1, 0), //?锟斤拷?锟斤拷??锟紾锟斤拷?30%
	Array(2043007, 1, 0), //?锟斤拷?锟絔锟絆?锟紾锟斤拷?30%
	Array(2043008, 1, 0), //?锟斤拷?锟絔锟絆锟斤拷?10%
	Array(2043010, 1, 0), //?锟斤拷?锟絔锟絆锟斤拷?100%
	Array(2043012, 1, 0), //?锟斤拷?锟斤拷?锟斤拷?15%
	Array(2043015, 1, 0), //?锟斤拷?锟絉锟斤拷锟絭锟斤拷?100%
	Array(2043018, 1, 0), //?锟斤拷?锟絉锟斤拷锟絭锟斤拷?30%
	Array(2043019, 1, 0), //?锟斤拷?锟絉锟斤拷锟絭锟斤拷?10%
	Array(2043025, 1, 0), //?锟斤拷?锟絉锟斤拷锟斤拷?15%
	Array(2043100, 1, 0), //?锟斤拷锟斤拷?锟斤拷?100%
	Array(2043102, 1, 0), //?锟斤拷锟斤拷?锟斤拷?10%
	Array(2043105, 1, 0), //?锟斤拷锟斤拷??锟紾锟斤拷?30%
	Array(2043107, 1, 0), //?锟斤拷锟斤拷?锟斤拷?15%
	Array(2043110, 1, 0), //?锟斤拷锟絉锟斤拷锟絭锟斤拷?100%
	Array(2043113, 1, 0), //?锟斤拷锟絉锟斤拷锟絭锟斤拷?30%
	Array(2043114, 1, 0), //?锟斤拷锟絉锟斤拷锟絭锟斤拷?10%
	Array(2043119, 1, 0), //?锟斤拷锟絉锟斤拷锟斤拷?15%
	Array(2043200, 1, 0), //?锟斤拷?锟斤拷锟斤拷?锟斤拷?100%
	Array(2043202, 1, 0), //?锟斤拷?锟斤拷锟斤拷?锟斤拷?10%
	Array(2043205, 1, 0), //?锟斤拷?锟斤拷锟斤拷??锟紾锟斤拷?30%
	Array(2043207, 1, 0), //?锟斤拷?锟斤拷锟斤拷?锟斤拷?15%
	Array(2043210, 1, 0), //?锟斤拷?锟斤拷锟絉锟斤拷锟絭锟斤拷?100%
	Array(2043213, 1, 0), //?锟斤拷?锟斤拷锟絉锟斤拷锟絭锟斤拷?30%
	Array(2043214, 1, 0), //?锟斤拷?锟斤拷锟絉锟斤拷锟絭锟斤拷?10%
	Array(2043219, 1, 0), //?锟斤拷?锟斤拷锟絉锟斤拷锟斤拷?15%
	Array(2043300, 1, 0), //锟絬?锟斤拷?锟斤拷?100%
	Array(2043302, 1, 0), //锟絬?锟斤拷?锟斤拷?10%
	Array(2043305, 1, 0), //锟絬?锟斤拷??锟紾锟斤拷?30%
	Array(2043307, 1, 0), //锟絬?锟斤拷?锟斤拷?15%
	Array(2043700, 1, 0), //锟絬锟斤拷锟絔锟絆锟斤拷?100%
	Array(2043702, 1, 0), //锟絬锟斤拷锟絔锟絆锟斤拷?10%
	Array(2043705, 1, 0), //锟絬锟斤拷锟絔锟絆?锟紾锟斤拷?30%
	Array(2043707, 1, 0), //锟絬锟斤拷锟絔锟絆锟斤拷?15%
	Array(2043800, 1, 0), //?锟斤拷锟絔锟絆锟斤拷?100%
	Array(2043802, 1, 0), //?锟斤拷锟絔锟絆锟斤拷?10%
	Array(2043805, 1, 0), //?锟斤拷锟絔锟絆?锟紾锟斤拷?30%
	Array(2043807, 1, 0), //?锟斤拷锟絔锟絆锟斤拷?15%
	Array(2044000, 1, 0), //?锟斤拷?锟斤拷?锟斤拷?100%
	Array(2044002, 1, 0), //?锟斤拷?锟斤拷?锟斤拷?10%
	Array(2044005, 1, 0), //?锟斤拷?锟斤拷??锟紾锟斤拷?30%
	Array(2044007, 1, 0), //?锟斤拷?锟斤拷?锟斤拷?15%
	Array(2044010, 1, 0), //?锟斤拷?锟絉锟斤拷锟絭锟斤拷?100%
	Array(2044013, 1, 0), //?锟斤拷?锟絉锟斤拷锟絭锟斤拷?30%
	Array(2044014, 1, 0), //?锟斤拷?锟絉锟斤拷锟絭锟斤拷?10%
	Array(2044027, 1, 0), //?锟斤拷?锟絉锟斤拷锟斤拷?15%
	Array(2044100, 1, 0), //?锟斤拷锟斤拷?锟斤拷?100%
	Array(2044102, 1, 0), //?锟斤拷锟斤拷?锟斤拷?10%
	Array(2044105, 1, 0), //?锟斤拷锟斤拷??锟紾锟斤拷?%30
	Array(2044107, 1, 0), //?锟斤拷锟斤拷?锟斤拷?15%
	Array(2044110, 1, 0), //?锟斤拷锟絉锟斤拷锟絭锟斤拷?100%
	Array(2044113, 1, 0), //?锟斤拷锟絉锟斤拷锟絭锟斤拷?30%
	Array(2044114, 1, 0), //?锟斤拷锟絉锟斤拷锟絭锟斤拷?10%
	Array(2044119, 1, 0), //?锟斤拷锟絉锟斤拷锟斤拷?15%
	Array(2044200, 1, 0), //?锟斤拷?锟斤拷锟斤拷?锟斤拷?100%
	Array(2044202, 1, 0), //?锟斤拷?锟斤拷锟斤拷?锟斤拷?10%
	Array(2044205, 1, 0), //?锟斤拷?锟斤拷锟斤拷??锟紾锟斤拷?30%
	Array(2044207, 1, 0), //?锟斤拷?锟斤拷锟斤拷?锟斤拷?15%
	Array(2044210, 1, 0), //?锟斤拷?锟斤拷锟絉锟斤拷锟絭锟斤拷?100%
	Array(2044213, 1, 0), //?锟斤拷?锟斤拷锟絉锟斤拷锟絭锟斤拷?30%
	Array(2044214, 1, 0), //?锟斤拷?锟斤拷锟絉锟斤拷锟絭锟斤拷?10%
	Array(2044219, 1, 0), //?锟斤拷?锟斤拷锟絉锟斤拷锟斤拷?15%
	Array(2044300, 1, 0), //?锟斤拷?锟斤拷?100%
	Array(2044302, 1, 0), //?锟斤拷?锟斤拷?10%
	Array(2044305, 1, 0), //?锟斤拷??锟紾锟斤拷?30%
	Array(2044307, 1, 0), //?锟斤拷?锟斤拷?15%
	Array(2044310, 1, 0), //?锟絉锟斤拷锟絭锟斤拷?100%
	
	Array(2044313, 1, 0), //?锟絉锟斤拷锟絭锟斤拷?30%
	Array(2044314, 1, 0), //?锟絉锟斤拷锟絭锟斤拷?10%
	Array(2044319, 1, 0), //?锟絉锟斤拷锟斤拷?15%
	Array(2044400, 1, 0), //锟劫э拷?锟斤拷?100%
	Array(2044402, 1, 0), //锟劫э拷?锟斤拷?10%
	Array(2044405, 1, 0), //锟劫э拷??锟紾锟斤拷?30%
	Array(2044407, 1, 0), //锟劫э拷?锟斤拷?15%
	Array(2044410, 1, 0), //锟劫㏑锟斤拷锟絭锟斤拷?100%
	Array(2044413, 1, 0), //锟劫㏑锟斤拷锟絭锟斤拷?30%
	Array(2044414, 1, 0), //锟劫㏑锟斤拷锟絭锟斤拷?10%
	Array(2044419, 1, 0), //锟劫㏑锟斤拷锟斤拷?15%
	Array(2044500, 1, 0), //锟絵锟斤拷?锟斤拷?100%
	Array(2044502, 1, 0), //锟絵锟斤拷?锟斤拷?10%
	Array(2044505, 1, 0), //锟絵锟斤拷??锟紾锟斤拷?30%
	Array(2044507, 1, 0), //锟絵锟斤拷?锟斤拷?15%
	Array(2044600, 1, 0), //锟斤拷锟斤拷?锟斤拷?100%
	Array(2044602, 1, 0), //锟斤拷锟斤拷?锟斤拷?10%
	Array(2044605, 1, 0), //锟斤拷锟斤拷??锟紾锟斤拷?30%
	Array(2044607, 1, 0), //锟斤拷锟斤拷?锟斤拷?15%
	Array(2044700, 1, 0), //锟斤拷锟組锟斤拷?锟斤拷?100%
	Array(2044702, 1, 0), //锟斤拷锟組锟斤拷?锟斤拷?10%
	Array(2044705, 1, 0), //锟斤拷锟組锟斤拷??锟紾锟斤拷?30%
	Array(2044707, 1, 0), //锟斤拷锟組锟斤拷?锟斤拷?15%
	Array(2044800, 1, 0), //锟斤拷锟揭э拷?锟斤拷?100%
	Array(2044802, 1, 0), //锟斤拷锟揭э拷?锟斤拷?10%
	Array(2044804, 1, 0), //锟斤拷锟揭э拷?锟斤拷?30%
	Array(2044805, 1, 0), //锟斤拷锟揭㏑锟斤拷锟絭锟斤拷?100%
	Array(2044808, 1, 0), //锟斤拷锟揭㏑锟斤拷锟絭锟斤拷?30%
	Array(2044809, 1, 0), //锟斤拷锟揭㏑锟斤拷锟絭锟斤拷?10%
	Array(2044812, 1, 0), //锟斤拷?锟斤拷?锟斤拷?15%
	Array(2044814, 1, 0), //锟斤拷?锟絉锟斤拷锟斤拷?15%
	Array(2044900, 1, 0), //锟絬?锟斤拷?锟斤拷?100%
	Array(2044902, 1, 0), //锟絬?锟斤拷?锟斤拷?10%
	Array(2044904, 1, 0), //锟絬?锟斤拷?锟斤拷?30%
	Array(2044907, 1, 0) //锟絬?锟斤拷?锟斤拷?15%
);
function doCheck(){
	temp = [];
	for(var i in itemListAdvanced){
		if(cm.ExistItem(itemListAdvanced[i][0]))
			temp.push(itemListAdvanced[i])
	}
	itemListAdvanced = temp;
	
	temp = [];
	for(var i in itemListGold){
		if(cm.ExistItem(itemListGold[i][0]))
			temp.push(itemListGold[i])
	}
	itemListGold = temp;
	
	temp = [];
	for(var i in itemListSilver){
		if(cm.ExistItem(itemListSilver[i][0]))
			temp.push(itemListSilver[i])
	}
	itemListSilver = temp;
	
	temp = [];
	for(var i in itemListNormal){
		if(cm.ExistItem(itemListNormal[i][0]))
			temp.push(itemListNormal[i])
	}
	itemListNormal = temp;
}
	
function action(mode, type, selection) {
	var InsertData = false;
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
        }
        status--;
    }
    if (status == 0) {
		if(debug && cm.getPlayer().getGMLevel() < 4){
			cm.sendOk("尚未開放，將於二月啟動!");
            cm.dispose();
			return;
		}
		var conn = cm.getConnection();
		var ps = conn.prepareStatement("SELECT * FROM lottery WHERE type=9330118 ORDER BY id desc LIMIT 5");
        var RankDataBase = ps.executeQuery();
        var text = ""
        var i = 1;
        text += "#d#e最新抽中大獎消息：#k#n#r\r\n\r\n-----------------------------------------------\r\n";
        while (RankDataBase.next()) {
            text += "#r" + RankDataBase.getString("charName") + "#k 在 #b" + RankDataBase.getString("time") + "#k 抽中 #r" + RankDataBase.getString("itemid") + "#k"
            text += "\r\n"
            i++;
        }
		var ttxt = cm.getPlayer().getGMLevel() > 4?"#L3#【GM測試1百抽】#l":"";
        text += "-----------------------------------------------\r\n#L0##b【抽獎 1張 轉蛋券/次】#l #L1##d【獎品預覽】#l\r\n#L2##r【抽獎 "+ priceD +" 贊助點/次】#l#k"+ ttxt +" \r\n\r\n \r\n";
        RankDataBase.close();
		ps.close();
        cm.sendSimple(text);

    } else if (status == 1) {
        if (selection == 0 || selection == 2) {
			if(cm.getInventory(1).getNextFreeSlot() < 1 || cm.getInventory(2).getNextFreeSlot() < 1 || cm.getInventory(3).getNextFreeSlot() < 1 || cm.getInventory(4).getNextFreeSlot() < 1|| cm.getInventory(5).getNextFreeSlot() < 1)
			{
				cm.sendOk("對不起，您的背包每個欄位至少皆要有一個空位!");
                cm.dispose();
				return;
			}
			var canUse = false;
			if(selection == 0 && cm.haveItem(5220000) >= 1)
				canUse = true;
			else if(selection == 2 && cm.getPlayer().getPoints() >= priceD)
				canUse = true;
            var ii = cm.getItemInfo();
            if (canUse) {
                var item;
                var xxx = Math.floor(Math.random() * 1000000);
                if (xxx < rates[0]) {//傳說物品
                    rand = Math.floor(Math.random() * itemListAdvanced.length);
					item = itemListAdvanced[rand];
                    InsertData = true;
                } else if (xxx < rates[1]) {//金牌物品
                    rand = Math.floor(Math.random() * itemListGold.length);
                    item = itemListGold[rand];
                } else if (xxx < rates[2]) {//銀牌物品
                    rand = Math.floor(Math.random() * itemListSilver.length);
                    item = itemListSilver[rand];
                } else {//垃圾
                    rand = Math.floor(Math.random() * itemListNormal.length);
                    item = itemListNormal[rand];
                }
                if (item == -1) {
                    cm.sendOk("對不起，您的背包已經滿了");
                    cm.dispose();
                } else {
					if(selection == 0 && cm.haveItem(5220000) >= 1)
						cm.gainItem(5220000,-1);
					else if(selection == 2 && cm.getPlayer().getPoints() >= priceD)
						cm.getPlayer().gainPoints(-priceD);
					else{//不該發生
						cm.sendOk("發生錯誤!");
						cm.dispose();
						return;
					}
                    
                    cm.setEventCount("累計明星抽獎", 1);
                    if (InsertData) {
						var conn = cm.getConnection();
                        var insert = conn.prepareStatement("INSERT INTO lottery (id,itemid,charid,charName,time, type) VALUES(?,?,?,?,?,?)"); // 载入數据
                        insert.setString(1, null); //载入记录ID
                        insert.setString(2, "#t" + item[0] + "#"); //载入记录ID
                        insert.setString(3, cm.getPlayer().getId());
                        insert.setString(4, cm.getPlayer().getName());
                        insert.setString(5, Year + "-" + month + "-" + dates + "");
                        insert.setInt(6, 9330118);
                        insert.executeUpdate(); //更新
                        insert.close();
						var time = item[2] == 0?"":("為期" +item[2]+ "天");
						cm.gainGachaponItem(item[0], item[1], "恭喜玩家 " + cm.getChar().getName() + " 抽中大獎", true, item[2]);
                        cm.getMap().startMapEffect("恭喜玩家 " + cm.getChar().getName() + " 人品爆發抽中大獎。", 5120012);
                    }else{
						cm.gainItemPeriod(item[0], item[1], item[2]);
					}
                    status = -1;
                    cm.sendOk("恭喜您從幸運抽獎中獲得 #b#t" + item + "##k.");
                }
            } else {
                cm.sendOk("您沒有足夠的轉蛋券或贊助點!");//暂時關闭。增加物品中。
                cm.safeDispose();
            }
        } else if (selection == 1) {
			text = "#r-----------------各等級資訊--------------------\r\n";
			text += "#g傳說級人品 : " + rates[0]/10000 +"%\r\n";
			text += "#r尊貴級人品 : " + rates[1]/10000 +"%\r\n";
			text += "#b普通級人品 : " + rates[2]/10000 +"%\r\n";
			text += "#d垃圾級人品 : " + rates[3]/10000 +"%\r\n";
			text += "------------------各物品資訊--------------------\r\n#b";
			text += "#g傳說級:\r\n"
			
			text += '#b#v'+itemListAdvanced[0]+'##z'+itemListAdvanced[0]+'#\r\n';
			text += '#b#v'+itemListAdvanced[1]+'##z'+itemListAdvanced[1]+'#\r\n';
			text += '#b#v'+itemListAdvanced[4]+'##z'+itemListAdvanced[4]+'#\r\n';
			text += '#b#v'+itemListAdvanced[8]+'##z'+itemListAdvanced[8]+'#\r\n';
			
			text += "\r\n#r尊貴級:\r\n"
			for(var i in itemListGold){
				text += '#b#v'+itemListGold[i]+'##z'+itemListGold[i]+'#\r\n';
			}
			text += "\r\n#b普通級:\r\n"
			for(var i in itemListSilver){
				text += '#b#v'+itemListSilver[i]+'##z'+itemListSilver[i]+'#\r\n';
			}
			text += "\r\n#b垃圾級:\r\n"
			for(var i in itemListNormal){
				text += '#b#v'+itemListNormal[i]+'##z'+itemListNormal[i]+'#\r\n';
			}
            cm.sendOk(text);
            cm.dispose();
        } else if (selection == 3) {
			var a=0,b=0,c=0,d=0
			for(i = 0;i < 100;i++){
				var xxx = Math.floor(Math.random() * 1000000);
				if (xxx < rates[0]) {//傳說物品
                    a++;
                } else if (xxx < rates[1]) {//金牌物品
                    b++;
                } else if (xxx < rates[2]) {//銀牌物品
                    c++;
                } else {//垃圾
                    d++;
                }
			}
			text = "#b本次測試 #r100#b 次\r\n\r\n";
			text += "#g傳說級人品 : " + a +"次\r\n";
			text += "#r尊貴級人品 : " + b +"次\r\n";
			text += "#b普通級人品 : " + c +"次\r\n";
			text += "#d垃圾級人品 : " + d +"次\r\n";
            cm.sendOk(text);
            cm.dispose();
        }else {
            cm.sendOk("請聯繫管理員。")
            cm.dispose();
        }
    }
}