/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice.perimeterStatistics;

/**
 *
 * @author Nestor
 */
public class AgAgRawStatisticDataAtomCount1Million extends AbstractStatistics {
   
    public AgAgRawStatisticDataAtomCount1Million(){
      this.setData(rawData);
    }
    
 private static int[][] rawData={{71901,198142,218496,149317,42453,70414,23546,25411,25885,14065,18605,8566,10688,6360,5392,8387,3351,6872,2756,2995,5887,1533,4216,1879,2160,3533,1290,2209,1461,1160,2493,965,1329,1762,823,1957,982,814,1371,464,1431,587,586,1328,379,944,531,660,928,691,861,395,605,823,327,520,330,515,345,415,467,327,671,478,274,564,224,342,324,291,433,119,411,153,295,606,138,343,259,207,363,140,323,206,138,432,258,224,205,188,428,171,224,327,85,172,53,291,208,85,344,257,105,103,85,189,170,105,243,153,137,171,155,207,87,155,223,138,223,138,103,85,206,120,172,172,87,138,85,136,137,72,119,173,172,275,53,188,18,119,208,53,330,86,104,190,172,104,137,52,207,85,138,261,51,276,35,119,310,52,259,88,397,155,69,412,138,124,418,153,104,654,172,138,798,136,1357,363,120,8386,1000035,
},{111795,252755,251792,48416,96361,15262,46792,6498,31086,4701,18842,3053,9689,6076,8008,3938,4863,3918,3983,1967,3534,2036,3172,1569,2329,1170,2307,1140,1809,736,2086,690,1442,732,1400,926,1073,805,888,820,889,524,859,774,790,522,605,410,552,253,535,241,582,129,680,339,705,171,434,395,322,380,351,171,492,140,525,113,369,84,494,171,386,185,210,155,280,226,371,84,299,169,295,157,383,126,253,254,266,112,197,99,268,70,212,128,141,114,140,156,182,84,154,225,185,113,142,56,200,28,183,0,242,70,154,28,84,70,200,98,169,130,240,56,185,14,169,42,239,28,197,28,70,84,183,98,197,168,169,112,196,126,127,99,143,58,56,114,84,71,140,29,112,98,157,70,184,71,170,28,198,42,267,43,155,141,240,84,240,71,451,56,736,70,967,59,183,1751,57,6771,1000015,
},{135677,310525,194721,104029,30599,49853,18166,23351,13286,9182,11758,6672,8272,5580,3843,4281,3033,3966,3388,2333,2733,2227,1431,1880,1119,1688,1209,1427,1429,673,1390,815,1228,857,852,888,565,808,462,555,644,645,606,669,494,448,333,429,552,390,472,322,470,450,208,367,162,348,276,264,265,278,290,385,151,471,208,309,218,172,254,126,291,207,161,241,161,266,158,199,185,161,186,196,105,126,162,193,229,117,209,92,148,161,141,115,115,195,107,116,115,208,136,104,71,104,140,150,23,81,128,22,102,139,70,183,35,103,115,159,68,117,92,159,57,174,33,163,127,68,103,58,90,34,108,138,80,127,79,70,68,71,80,104,90,177,24,137,95,47,127,80,160,158,68,171,70,79,70,81,105,36,140,175,34,210,57,162,213,36,208,70,414,689,57,926,70,1805,80,5195,1000060,
},{158470,380093,139896,90769,52111,20871,25791,20189,8333,8884,9548,6730,4404,6276,3866,2846,3633,2948,2535,2175,2095,1992,1617,1640,1547,963,1422,1549,992,902,981,732,755,957,781,446,725,545,629,502,410,524,425,443,521,265,337,435,254,316,376,317,198,370,422,207,334,309,226,287,276,215,156,225,186,100,178,186,90,268,199,208,119,214,156,167,190,138,197,139,118,194,109,109,117,79,149,179,79,139,108,169,100,197,146,88,157,79,90,80,129,158,89,60,148,79,60,168,87,87,195,80,99,107,127,48,80,50,70,79,79,80,79,58,147,58,90,60,68,139,59,90,59,79,69,99,98,39,69,40,128,80,108,88,148,59,120,99,40,39,110,89,50,107,59,39,129,69,70,30,98,109,20,89,139,110,100,165,49,158,150,80,90,234,523,20,681,129,1627,4951,1000051,
},{182463,442294,109276,53850,49181,29271,21760,13165,8138,10351,7944,5892,3960,3833,4155,2951,2420,2398,1957,1813,1853,1790,1462,1457,1055,1165,1085,934,845,884,858,840,600,676,589,739,627,413,541,550,487,366,295,443,340,352,404,261,339,335,336,264,227,286,323,243,269,139,164,311,202,231,165,62,213,132,278,174,130,213,194,209,121,141,189,148,136,166,132,121,112,120,106,87,163,110,68,86,70,71,67,111,44,103,124,139,103,122,71,105,105,95,43,129,60,62,86,105,61,61,104,71,45,120,137,75,79,41,60,97,35,103,42,97,60,97,147,61,36,123,86,69,78,95,35,62,79,52,137,105,41,78,62,78,76,26,109,78,26,79,52,101,17,86,104,52,81,96,54,71,42,62,124,44,101,95,96,61,42,44,147,157,43,167,244,309,635,43,1464,3921,1000001,
},{204865,425548,136813,57485,32883,21016,17572,13185,11598,7466,7209,4977,4386,4193,2529,2534,2182,2217,1980,1753,1293,1431,1308,1248,1397,904,1043,739,895,761,852,594,596,522,532,518,492,329,398,508,374,403,396,395,219,340,254,462,349,249,390,243,242,274,246,149,195,213,203,198,135,177,209,119,209,93,172,142,226,130,149,188,220,124,117,96,150,93,130,131,126,100,151,103,93,125,111,146,132,110,126,95,88,119,79,101,72,161,47,32,103,78,101,56,78,39,16,71,47,70,48,62,63,61,71,48,64,64,77,77,72,111,55,56,63,80,61,71,93,55,46,23,107,64,47,39,93,78,48,60,95,86,31,69,54,62,40,87,102,24,71,56,71,39,63,47,61,24,70,47,39,85,71,32,47,86,54,102,95,63,136,64,102,110,218,299,133,563,982,3202,1000032,
},{231234,413997,126556,63464,34715,22040,16494,11651,7597,7640,5674,4418,4177,2999,2937,2745,1890,2129,1747,1337,1380,1381,1185,1194,1130,1021,695,763,819,666,857,674,486,527,508,462,449,342,369,384,360,339,376,312,333,315,307,256,200,216,265,236,168,152,268,245,220,137,194,219,129,186,171,113,156,134,152,134,120,117,130,86,93,130,166,99,137,99,121,115,93,129,113,116,107,122,93,102,102,65,101,80,64,65,95,108,79,58,93,43,121,65,99,92,64,50,93,56,71,81,50,58,63,49,64,63,50,72,36,29,79,71,95,63,63,50,43,44,57,58,73,63,58,51,71,79,57,71,86,73,43,57,71,86,58,67,64,58,36,21,59,21,66,43,43,42,36,50,22,29,57,50,21,57,35,29,78,49,65,64,35,173,21,73,160,189,308,624,1052,2969,1000023,
},{249078,412853,121298,61003,36346,22529,14123,9947,8081,5708,4813,4233,3549,3078,2874,2485,2239,1780,1612,1509,1162,1084,1032,1053,825,865,775,879,648,610,473,493,485,422,465,433,353,382,394,348,355,338,278,202,282,294,276,173,212,174,235,214,138,204,189,242,238,192,102,138,153,150,161,144,136,157,158,156,83,195,106,114,116,113,181,104,104,116,86,78,85,72,37,100,82,92,99,91,63,78,79,45,101,120,89,78,62,89,51,77,36,72,73,69,83,51,100,31,60,60,72,26,55,56,40,39,46,52,52,51,48,47,64,42,51,72,32,71,40,54,67,38,45,33,80,116,45,71,87,70,19,27,31,73,38,38,55,53,45,68,33,40,32,53,19,39,55,26,37,51,70,45,53,43,13,40,85,97,92,115,97,6,84,163,164,220,215,370,948,2950,1000007,
},{272179,418516,123783,52292,25329,17276,12061,10880,8127,5637,4671,3673,3749,2925,2308,1718,1607,1806,1551,1542,1113,971,1020,796,773,707,720,547,714,557,576,409,451,416,422,435,375,383,284,340,299,220,314,205,289,219,232,249,232,204,184,163,266,191,183,184,148,136,160,186,84,142,169,90,136,106,124,132,123,94,126,118,138,72,95,71,77,113,106,60,107,47,102,70,60,95,52,82,100,102,88,99,82,66,89,84,48,48,84,90,78,53,72,47,66,59,36,77,30,53,42,35,70,78,35,35,29,54,35,35,35,34,47,53,23,53,18,65,36,59,36,41,30,24,35,66,41,53,48,54,83,72,71,48,54,36,30,42,54,83,18,18,53,54,54,35,23,36,71,45,59,30,48,42,23,30,90,54,42,82,47,78,66,78,65,113,216,261,543,900,1002611,
},{296968,426812,102828,44442,27542,20441,10130,7669,7675,5089,4069,3914,3042,2389,2329,2226,1683,1265,1396,1037,1019,977,921,864,782,734,685,669,664,530,338,406,459,457,462,359,331,337,350,316,288,296,245,196,213,246,227,119,254,163,203,126,202,141,205,172,210,104,149,239,110,163,137,106,79,175,72,119,128,61,59,97,101,60,76,93,67,82,78,85,102,94,100,117,108,84,76,60,73,65,71,49,78,76,84,76,39,70,43,76,53,38,58,43,32,72,56,40,23,77,32,59,40,50,67,39,48,27,34,44,28,62,32,25,16,45,39,65,67,34,53,33,89,77,48,56,44,17,49,22,87,66,52,16,43,27,39,55,49,33,26,45,49,40,54,64,49,44,31,42,51,55,70,39,22,33,63,64,68,95,46,89,99,113,101,76,317,178,394,516,1002676,
},{327854,418393,90389,39139,29040,16045,12519,7843,5768,5782,3630,3478,2894,2385,2284,1665,1839,1155,1215,1305,1017,1068,900,719,725,664,742,686,527,500,518,450,477,383,318,314,422,321,311,209,225,268,214,194,271,233,191,121,185,170,157,191,115,147,101,106,154,162,133,170,122,181,127,99,148,97,140,93,92,129,43,102,110,57,42,74,109,74,76,99,84,64,79,65,59,140,56,85,69,73,79,72,71,50,72,62,109,58,32,68,64,61,26,46,61,52,37,27,73,50,38,47,22,56,39,52,36,20,49,40,35,52,28,67,52,32,31,57,52,51,38,38,64,59,47,41,56,47,36,35,36,31,66,80,36,37,58,31,47,31,21,26,46,31,26,52,43,22,42,41,68,41,43,11,52,47,32,77,35,94,57,70,64,62,53,65,242,223,293,684,1002210,
},{353582,414558,72487,52695,17528,17801,8683,8849,5130,5000,3716,3047,2393,2300,1784,1959,1353,1424,1086,1178,1011,885,760,750,671,600,541,477,471,513,452,426,466,345,412,226,338,309,264,267,234,201,218,262,173,244,144,198,125,222,195,149,76,186,150,127,96,138,113,143,102,152,111,142,70,98,100,93,67,117,71,87,93,85,64,63,39,112,53,57,43,79,55,29,58,65,94,95,90,27,76,73,42,86,39,58,62,63,63,24,44,76,30,58,34,56,37,32,73,63,34,67,55,62,49,34,40,29,19,33,66,28,33,38,23,39,24,38,39,50,53,33,15,68,29,48,57,19,32,35,82,50,38,47,15,52,40,34,39,37,44,24,32,33,29,13,52,52,25,38,39,43,5,45,9,69,30,49,39,50,69,62,92,59,65,90,135,306,357,726,1002067,
},{381018,392514,83334,36603,25558,11886,10556,7578,5329,4648,3142,3309,2308,1886,1781,1492,1487,1194,1101,1242,738,825,825,684,655,664,577,588,452,426,513,389,292,293,357,337,269,333,248,206,178,266,220,170,158,192,174,160,165,214,198,123,133,145,118,92,146,109,103,116,65,153,106,90,132,41,108,81,58,60,68,64,80,84,81,56,59,86,70,99,49,64,42,96,52,78,69,92,45,50,82,67,57,44,42,46,61,20,43,62,50,32,70,40,51,31,70,52,27,44,40,58,45,29,38,38,47,31,33,62,33,64,52,37,57,25,26,25,55,33,22,49,18,47,43,24,46,34,43,55,54,68,18,39,27,51,42,22,40,37,59,40,34,27,22,51,20,16,33,39,26,35,26,30,40,46,45,30,45,41,36,52,74,38,78,117,68,156,417,688,1001880,
},{413434,363638,91115,33288,20296,14840,8728,6426,5630,4102,2850,2937,2559,1846,1633,1558,1363,1061,1077,1022,708,882,724,612,576,571,489,531,475,400,334,368,323,241,318,262,299,229,200,237,243,226,197,142,192,145,166,150,183,104,126,136,137,115,117,142,116,143,113,114,134,132,87,72,75,74,83,94,110,89,42,85,82,47,65,73,66,59,58,59,48,29,64,45,62,44,84,57,70,62,38,59,51,70,59,59,46,65,62,56,32,36,61,26,49,54,30,32,46,34,25,30,51,52,50,41,25,46,34,34,58,24,64,44,29,69,29,16,9,50,20,29,40,45,37,25,42,45,64,21,41,50,20,21,41,58,38,29,41,45,36,33,34,42,30,25,32,24,25,25,24,36,29,49,20,16,21,47,33,41,45,49,57,65,77,108,106,168,381,638,1001849,
},{444341,337391,89843,34981,20207,11847,9032,7002,4861,4118,2941,2665,2151,2054,1602,1383,1092,1185,1056,951,694,737,590,641,564,475,429,420,419,444,336,419,329,303,300,239,270,210,193,275,198,196,148,186,177,151,176,158,156,110,122,175,88,117,108,102,106,100,115,100,96,95,135,96,75,72,78,99,73,68,52,71,78,48,35,69,90,53,56,47,44,71,52,52,79,44,82,43,28,60,59,48,39,48,42,72,38,48,64,48,32,52,32,48,36,80,36,32,64,31,48,20,20,32,23,23,38,59,54,40,28,15,46,48,36,8,28,40,32,51,20,48,28,31,36,48,20,27,52,56,24,47,55,36,64,48,23,24,40,12,43,12,32,16,32,52,24,36,28,32,36,20,39,36,24,48,38,19,47,47,43,36,35,48,68,62,142,118,425,499,1001668,
},{472911,319672,80969,36480,19244,12815,8676,6005,4698,4001,2887,2413,2074,1799,1514,1313,1223,1118,937,787,641,649,675,622,520,479,457,401,451,379,337,248,270,268,237,275,243,249,162,203,195,196,195,168,158,152,145,161,134,101,104,129,132,89,119,90,80,109,117,68,99,117,71,92,35,106,70,88,64,55,79,52,70,54,107,72,66,56,37,42,31,42,53,70,61,54,66,48,40,68,38,50,32,50,39,32,23,61,67,66,36,41,47,56,54,39,37,33,50,36,28,46,31,16,40,22,42,37,51,39,25,35,44,30,22,23,39,29,17,62,36,38,10,36,45,24,34,32,54,34,17,40,50,19,25,7,46,36,37,44,16,34,15,21,15,35,18,23,19,30,46,53,39,55,25,40,27,8,39,47,27,61,42,44,72,88,64,136,360,618,1001747,
},{512557,304974,67449,30826,17345,12064,7545,6206,4327,3846,2886,2493,1841,1533,1562,1232,1125,947,877,760,719,679,559,566,545,467,463,417,407,317,303,226,247,323,267,202,280,216,197,190,156,164,166,168,176,132,161,118,176,97,172,131,90,57,110,110,79,122,65,69,97,92,60,59,90,90,73,70,45,26,72,73,66,52,67,49,73,78,56,47,52,31,44,42,56,50,53,41,36,52,50,49,43,53,38,58,23,72,47,47,27,50,47,24,28,19,45,51,23,25,45,54,16,61,25,21,23,38,42,41,39,40,49,41,26,20,21,43,32,50,24,27,45,29,30,16,28,39,35,26,20,41,49,41,32,40,36,11,44,33,18,27,11,29,33,24,27,22,18,34,28,33,54,17,43,17,19,33,26,41,45,36,39,52,53,67,79,161,142,794,1001492,
},{547390,278301,68082,27080,17351,10098,7341,5803,4165,3503,2653,2546,1838,1495,1237,1141,1222,819,896,820,640,681,478,588,524,429,448,367,365,346,256,294,273,225,269,222,193,167,188,178,184,179,166,165,132,135,68,140,110,108,117,104,121,99,87,110,88,61,80,89,85,99,92,54,44,62,52,77,73,45,68,43,34,46,70,40,66,43,50,27,47,58,21,41,52,48,39,59,46,73,58,42,40,38,50,34,28,28,42,44,52,49,42,46,40,28,42,22,44,37,55,35,30,23,29,34,33,27,39,22,39,36,46,35,43,27,7,33,3,52,22,46,45,13,30,41,22,31,30,36,13,37,46,25,37,24,29,43,22,27,14,29,20,31,20,19,35,25,26,20,36,33,30,32,17,39,27,17,36,31,30,30,55,34,31,49,86,78,203,606,1001281,
},{588000,243940,62340,28744,16573,11513,6836,4974,3910,3407,2704,1991,1894,1569,1265,1352,877,872,765,735,725,587,540,498,405,393,435,356,347,288,292,288,248,278,209,203,221,181,226,118,136,154,123,145,133,123,104,100,132,114,110,91,94,82,86,77,81,93,73,90,73,84,74,65,61,83,44,66,67,70,60,42,55,63,53,48,50,47,34,62,46,29,60,43,52,61,53,38,62,36,34,38,38,31,50,46,32,31,53,35,41,43,45,22,35,42,51,39,16,27,37,47,40,40,23,49,29,35,44,30,40,41,21,18,50,26,21,12,34,44,27,22,20,24,29,40,43,9,38,38,44,35,24,33,19,29,15,23,22,24,23,28,30,20,41,18,12,9,32,30,19,35,44,25,15,39,31,29,23,27,13,45,32,34,51,54,89,106,197,626,1001316,
},{622913,215301,59099,30639,15033,9550,7159,5127,3653,2963,2652,1908,1752,1539,1298,987,1018,881,798,674,700,556,452,446,465,438,410,409,341,288,234,225,220,202,164,169,201,222,196,167,143,111,135,143,125,94,116,120,103,84,119,129,115,57,74,64,65,74,75,87,106,39,80,90,78,80,71,71,63,66,71,45,62,47,67,40,39,37,45,56,43,45,41,64,59,31,26,52,40,33,25,31,49,28,42,28,45,38,50,53,21,41,40,31,36,21,41,24,45,25,43,34,45,45,36,34,38,46,32,45,34,22,15,33,34,23,36,36,24,46,24,25,27,24,27,28,24,31,41,25,24,26,18,8,20,28,30,22,34,15,31,22,25,25,30,34,21,26,25,25,27,18,37,34,25,12,31,24,15,34,38,27,54,39,48,45,66,99,198,556,1001226,
},{626613,212227,64278,27127,13924,10842,6175,4973,3756,2675,2698,1764,1588,1399,1228,1112,841,821,565,748,661,511,528,445,437,364,348,330,308,247,247,223,210,234,200,158,186,174,169,153,157,163,128,134,117,129,107,107,93,106,134,92,93,89,72,97,65,101,60,74,68,52,70,69,61,58,66,90,75,44,71,54,54,64,41,45,48,42,33,43,42,45,39,48,57,72,24,39,33,70,27,25,26,30,33,38,33,32,51,17,36,27,27,36,33,45,47,39,47,38,36,15,24,39,24,36,36,33,21,21,38,48,44,36,27,33,21,39,27,27,32,29,15,33,27,24,27,15,33,33,27,18,26,30,27,27,29,21,19,21,24,36,36,21,21,15,30,21,20,24,18,24,21,21,39,21,29,18,36,27,36,54,38,38,32,72,39,80,169,621,1001274,
},{629020,214511,64571,24421,15162,8842,6958,4344,3771,2650,2305,1717,1656,1188,1137,891,990,677,633,575,573,483,479,402,429,365,388,301,265,272,211,261,249,224,190,184,231,193,142,116,139,108,147,129,144,115,116,85,107,96,106,65,102,81,87,102,96,67,81,46,82,77,60,63,61,41,67,55,64,53,63,50,57,47,47,35,49,48,49,38,45,35,33,53,47,29,45,18,37,46,47,58,15,46,48,49,37,21,30,34,20,31,22,42,31,46,35,32,36,24,35,35,16,31,44,36,51,41,17,31,36,35,21,30,34,26,37,22,36,40,11,36,11,34,11,27,34,23,20,50,34,17,22,32,14,20,26,23,8,31,15,21,23,28,30,19,29,18,21,24,27,31,29,17,25,15,24,27,22,51,15,23,23,44,28,31,52,100,175,523,1001160,
}}; 
    
}
