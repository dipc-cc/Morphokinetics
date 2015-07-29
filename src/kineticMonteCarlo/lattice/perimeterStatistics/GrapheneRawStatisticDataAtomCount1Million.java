/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice.perimeterStatistics;

/**
 * Estos datos corresponden a un estudio estadístico donde se mide la reentrada de átomos de una
 * región activa circular.
 *
 * Para cierta cantidad de átomos se mide, la cantidad de átomos que han entrado en función del
 * desfase de ángulo del átomo original (cuantos con un desfase de 0º, cuantos con un desfase de
 * 1º).
 *
 * La disposición de la matriz 2D es [radio de la zona activa][ángulo de desfase respecto a la
 * salida]:
 *
 * Primer direccionamiento: pos 0: radio de 20 atomos, incrementos de 5 unidades. Máximo, 195 atomos
 *
 * Segundo direccionamiento: pos:0 desfase de 0 grados. Incrementos de 1 grado. Máximo 180 grados.
 * La última columna almacena la suma de todos, que debe ser 1 millon de átomos.
 *
 * El valor almacenado es la cantidad de átomos reentrados con dicho desfase.
 *
 * @author U010531
 */
public class GrapheneRawStatisticDataAtomCount1Million extends AbstractStatistics {
 
  public GrapheneRawStatisticDataAtomCount1Million(){
    this.setData(rawData);
  }
    
  private static int[][] rawData = {{168293, 36774, 52823, 117532, 44691, 147116, 42145, 23641, 52652, 38980, 47453, 13065, 4560, 33134, 5990, 13131, 10751, 2790, 12795, 7822, 5614, 5545, 3504, 6805, 4390, 1768, 4763, 3198, 3573, 4049, 783, 3062, 2483, 1870, 2789, 1293, 2346, 2108, 1463, 2313, 1360, 1462, 1802, 511, 1396, 1327, 476, 2381, 544, 612, 1702, 816, 1258, 579, 612, 1701, 476, 1532, 1090, 272, 2142, 340, 205, 918, 34, 1156, 578, 374, 544, 442, 1362, 170, 136, 987, 170, 612, 510, 272, 986, 545, 647, 680, 204, 478, 511, 307, 612, 340, 476, 408, 0, 748, 442, 273, 442, 138, 238, 442, 205, 408, 374, 613, 476, 34, 238, 340, 68, 816, 136, 170, 374, 307, 204, 102, 238, 544, 34, 102, 102, 34, 646, 68, 0, 374, 34, 510, 204, 136, 204, 306, 476, 136, 34, 442, 68, 306, 409, 68, 136, 204, 273, 238, 136, 272, 68, 102, 306, 170, 102, 374, 0, 340, 408, 170, 374, 68, 170, 170, 136, 136, 102, 272, 170, 102, 102, 170, 0, 340, 102, 204, 374, 170, 340, 102, 238, 102, 102, 239, 272, 170, 1000001,}, {152332, 42561, 181567, 37503, 131660, 44203, 83210, 49915, 43604, 16282, 22639, 12922, 20092, 8605, 15973, 7116, 9216, 4119, 8691, 5127, 5410, 5070, 4150, 3476, 2860, 4568, 2606, 3670, 2718, 2973, 1289, 2494, 2157, 2803, 1430, 1400, 1458, 1205, 1344, 1456, 1710, 1205, 785, 730, 1684, 1009, 1345, 645, 1204, 224, 925, 590, 1039, 364, 1402, 590, 672, 84, 1232, 308, 1293, 252, 1317, 253, 448, 448, 588, 280, 756, 560, 561, 224, 756, 224, 308, 112, 589, 337, 336, 420, 281, 420, 420, 253, 420, 280, 197, 336, 280, 392, 112, 477, 336, 364, 198, 252, 393, 280, 113, 281, 394, 280, 280, 168, 365, 253, 112, 140, 364, 196, 336, 84, 308, 280, 308, 84, 309, 56, 280, 28, 392, 56, 364, 28, 224, 112, 252, 252, 196, 28, 168, 113, 196, 196, 168, 140, 197, 56, 168, 168, 224, 112, 140, 140, 140, 196, 252, 196, 84, 224, 168, 196, 168, 196, 56, 252, 225, 196, 140, 224, 112, 84, 113, 56, 112, 112, 112, 140, 252, 140, 112, 140, 112, 28, 140, 112, 224, 28, 364, 112, 1000001,}, {168678, 69274, 153080, 161889, 52635, 72404, 49280, 52292, 19529, 28527, 19489, 7715, 17565, 5768, 9183, 9683, 4734, 7667, 3457, 5216, 3336, 4656, 4080, 1604, 4473, 2063, 2852, 2135, 1973, 2965, 879, 1673, 1004, 1625, 1766, 906, 1501, 975, 1507, 973, 929, 1226, 462, 1371, 836, 722, 1415, 465, 907, 369, 1089, 487, 767, 763, 115, 1018, 577, 372, 628, 325, 555, 371, 720, 326, 372, 580, 163, 510, 511, 184, 554, 347, 324, 210, 465, 556, 210, 347, 93, 371, 233, 277, 278, 325, 299, 254, 231, 231, 162, 326, 161, 348, 139, 184, 207, 70, 370, 92, 280, 210, 184, 115, 232, 186, 139, 211, 185, 93, 395, 117, 232, 348, 69, 394, 208, 47, 162, 208, 208, 46, 209, 140, 46, 116, 46, 116, 115, 186, 185, 210, 232, 23, 139, 0, 115, 187, 69, 184, 115, 70, 116, 116, 185, 70, 162, 115, 210, 185, 93, 256, 162, 230, 163, 186, 69, 94, 138, 92, 23, 92, 138, 115, 92, 185, 69, 233, 278, 92, 140, 139, 115, 47, 209, 185, 70, 186, 92, 115, 92, 70, 1000001,}, {171233, 99633, 141570, 185417, 76012, 52691, 53393, 26387, 23826, 22627, 18176, 5332, 9955, 10795, 6596, 6904, 5440, 4574, 4514, 4394, 3578, 2604, 3185, 3325, 1611, 2404, 2547, 1511, 2362, 1635, 1214, 2090, 1310, 1434, 1393, 1396, 757, 892, 1116, 739, 874, 1137, 716, 639, 995, 500, 676, 736, 796, 458, 595, 1032, 398, 695, 515, 218, 318, 458, 317, 360, 536, 260, 459, 434, 516, 200, 499, 474, 239, 373, 239, 140, 219, 300, 100, 299, 418, 140, 217, 277, 200, 298, 280, 358, 158, 139, 237, 220, 199, 159, 119, 140, 300, 199, 298, 180, 60, 220, 217, 180, 197, 180, 179, 180, 160, 179, 118, 179, 218, 20, 239, 240, 199, 60, 238, 80, 140, 179, 80, 100, 237, 59, 120, 199, 160, 39, 178, 40, 179, 220, 220, 40, 79, 177, 58, 158, 120, 80, 157, 60, 159, 140, 119, 180, 40, 120, 99, 60, 160, 159, 58, 158, 78, 118, 80, 20, 118, 159, 180, 139, 80, 80, 140, 60, 119, 40, 59, 100, 238, 98, 240, 159, 40, 139, 159, 59, 80, 160, 237, 120, 1000001,}, {166012, 214878, 129960, 89174, 117413, 58962, 28023, 29565, 19847, 20228, 11234, 11006, 6206, 9447, 5708, 5722, 4297, 3934, 4141, 3579, 3171, 2523, 2010, 2332, 2282, 2083, 1806, 1991, 1634, 1038, 1279, 1227, 1381, 1021, 1055, 734, 852, 1106, 802, 698, 801, 800, 803, 561, 698, 733, 580, 647, 530, 426, 581, 680, 494, 273, 510, 427, 462, 119, 221, 512, 459, 497, 153, 255, 392, 459, 306, 273, 341, 393, 188, 221, 272, 255, 340, 255, 187, 341, 69, 273, 239, 238, 188, 239, 256, 238, 222, 187, 222, 68, 154, 238, 289, 187, 120, 221, 188, 188, 86, 136, 136, 136, 154, 238, 170, 153, 136, 188, 138, 188, 170, 221, 51, 136, 102, 51, 119, 52, 136, 137, 189, 102, 137, 85, 153, 153, 85, 85, 154, 85, 34, 136, 120, 187, 51, 136, 102, 103, 188, 120, 68, 68, 85, 154, 68, 51, 85, 119, 51, 86, 103, 34, 119, 222, 119, 68, 68, 51, 136, 119, 136, 85, 86, 85, 68, 34, 68, 68, 51, 137, 154, 103, 85, 68, 154, 68, 153, 34, 17, 204, 1000001,}, {176897, 202270, 185596, 105117, 79985, 41884, 36241, 23079, 21228, 12787, 11744, 8261, 8473, 5404, 4948, 5348, 4445, 3830, 3728, 2702, 2757, 3137, 2422, 2080, 2247, 1915, 1532, 1742, 1305, 1487, 1021, 1172, 1127, 1210, 1091, 934, 856, 941, 608, 665, 687, 722, 528, 623, 499, 572, 514, 423, 549, 576, 406, 357, 468, 390, 605, 327, 158, 353, 309, 339, 404, 446, 204, 384, 188, 376, 280, 219, 204, 202, 202, 155, 234, 186, 218, 231, 235, 265, 219, 248, 264, 264, 156, 46, 157, 187, 200, 204, 140, 171, 141, 109, 125, 186, 187, 140, 155, 109, 188, 219, 172, 80, 142, 154, 123, 80, 77, 48, 142, 109, 123, 172, 156, 62, 155, 31, 61, 140, 126, 46, 172, 93, 186, 46, 79, 125, 139, 48, 174, 91, 61, 92, 140, 63, 141, 123, 78, 123, 123, 108, 93, 62, 139, 77, 142, 107, 108, 48, 63, 92, 108, 96, 78, 107, 109, 60, 79, 47, 94, 93, 124, 63, 78, 94, 79, 94, 76, 61, 77, 93, 109, 92, 93, 63, 0, 94, 47, 158, 124, 107, 1000001,}, {186277, 201085, 205490, 127384, 56122, 45667, 27193, 19608, 14338, 14165, 9033, 8174, 6724, 5807, 5171, 3891, 5007, 3393, 2851, 2704, 2068, 2832, 2003, 2126, 1765, 1613, 1577, 1431, 1225, 1041, 1128, 1234, 1111, 798, 933, 630, 741, 880, 661, 671, 578, 592, 629, 591, 592, 546, 538, 479, 384, 535, 344, 478, 566, 250, 455, 383, 193, 288, 428, 397, 246, 399, 275, 218, 278, 235, 320, 151, 302, 180, 153, 112, 209, 273, 207, 193, 165, 204, 152, 138, 123, 109, 222, 95, 124, 55, 137, 152, 148, 137, 152, 193, 192, 110, 153, 111, 138, 96, 122, 110, 178, 180, 207, 69, 167, 95, 164, 84, 56, 182, 110, 54, 56, 55, 97, 107, 56, 83, 14, 56, 206, 42, 110, 95, 124, 83, 81, 166, 56, 123, 97, 41, 122, 40, 14, 56, 81, 55, 97, 81, 83, 109, 68, 96, 55, 83, 122, 42, 55, 68, 83, 55, 55, 139, 152, 41, 28, 81, 41, 125, 83, 70, 53, 39, 82, 81, 166, 82, 54, 54, 56, 55, 137, 56, 55, 111, 69, 98, 140, 152, 1000001,}, {179821, 218590, 194388, 127577, 62787, 52448, 24481, 19103, 16026, 10069, 9442, 7383, 4972, 5414, 4847, 3760, 3865, 3201, 2778, 2477, 2090, 2379, 1862, 1461, 1664, 1162, 1758, 1233, 1185, 1232, 1198, 788, 811, 932, 972, 901, 751, 799, 736, 656, 507, 472, 507, 493, 498, 411, 590, 384, 487, 415, 294, 467, 238, 265, 391, 384, 255, 218, 271, 268, 213, 215, 380, 231, 307, 344, 241, 278, 141, 262, 152, 219, 139, 104, 250, 151, 215, 255, 294, 166, 102, 263, 140, 215, 165, 148, 179, 129, 87, 139, 140, 113, 51, 151, 101, 102, 167, 150, 126, 114, 88, 127, 191, 100, 102, 111, 194, 165, 85, 101, 76, 103, 129, 78, 114, 152, 139, 77, 51, 25, 63, 88, 139, 50, 51, 127, 65, 64, 127, 50, 103, 51, 51, 102, 114, 77, 74, 100, 77, 90, 179, 128, 90, 64, 25, 39, 51, 65, 90, 51, 50, 76, 88, 100, 90, 51, 88, 75, 78, 89, 52, 50, 26, 38, 139, 74, 116, 52, 88, 39, 100, 39, 90, 63, 62, 64, 76, 0, 102, 140, 1000001,}, {190089, 234184, 207246, 141830, 49445, 26519, 21194, 18747, 14177, 10324, 8534, 5387, 5446, 5329, 4082, 3755, 2960, 2483, 2996, 2334, 2275, 2050, 1959, 1521, 1435, 1513, 989, 1165, 1206, 1008, 917, 760, 779, 866, 869, 829, 570, 548, 586, 580, 670, 576, 481, 525, 410, 566, 421, 425, 296, 418, 304, 425, 304, 254, 186, 250, 167, 404, 235, 341, 283, 195, 260, 211, 191, 233, 305, 175, 222, 199, 139, 189, 199, 104, 223, 105, 174, 153, 234, 93, 103, 184, 209, 142, 176, 142, 115, 71, 107, 94, 218, 120, 106, 82, 83, 58, 82, 69, 105, 107, 116, 117, 83, 106, 71, 94, 107, 115, 58, 95, 81, 80, 186, 153, 47, 46, 83, 105, 131, 81, 120, 23, 82, 93, 71, 58, 71, 93, 141, 104, 34, 119, 83, 127, 69, 92, 59, 60, 70, 94, 59, 35, 164, 46, 80, 105, 215, 80, 59, 71, 116, 72, 71, 70, 71, 82, 36, 116, 47, 94, 23, 48, 36, 24, 45, 106, 81, 83, 72, 71, 59, 81, 23, 34, 71, 58, 105, 117, 60, 93, 1000001,}, {195241, 286102, 193726, 105479, 47867, 33688, 22583, 15610, 12175, 10111, 6911, 5908, 5902, 4231, 3333, 3473, 3394, 2078, 2327, 2131, 1950, 1574, 1534, 1450, 1330, 1070, 1142, 1058, 996, 905, 699, 1002, 814, 733, 733, 679, 507, 584, 418, 520, 557, 563, 454, 442, 364, 332, 406, 448, 336, 302, 276, 256, 246, 204, 228, 355, 309, 183, 308, 214, 173, 278, 260, 151, 190, 183, 181, 147, 119, 243, 139, 109, 140, 94, 130, 161, 181, 147, 148, 106, 172, 159, 139, 97, 97, 116, 139, 139, 86, 107, 147, 106, 96, 74, 83, 97, 97, 33, 119, 54, 65, 117, 87, 96, 95, 94, 97, 32, 182, 83, 118, 118, 54, 108, 64, 53, 115, 85, 65, 86, 119, 33, 98, 85, 73, 95, 44, 53, 54, 76, 44, 85, 75, 107, 20, 52, 74, 63, 161, 33, 118, 52, 65, 44, 51, 75, 64, 42, 54, 106, 85, 44, 44, 54, 63, 55, 64, 64, 55, 32, 96, 53, 65, 41, 43, 53, 96, 97, 51, 66, 77, 62, 31, 54, 31, 75, 76, 98, 64, 95, 1000001,}, {191820, 352659, 173437, 69892, 55194, 28794, 19015, 15116, 9956, 10807, 6566, 5720, 5341, 3406, 4446, 3238, 2793, 2535, 1813, 2056, 1877, 1312, 1654, 1290, 1192, 1152, 1101, 915, 892, 1030, 717, 721, 678, 637, 635, 597, 531, 447, 635, 465, 438, 416, 380, 381, 329, 299, 338, 336, 380, 227, 355, 297, 247, 357, 319, 237, 320, 140, 250, 168, 189, 189, 176, 139, 208, 128, 189, 180, 140, 119, 70, 80, 229, 99, 230, 90, 110, 169, 108, 147, 70, 79, 118, 137, 170, 109, 118, 179, 140, 139, 110, 167, 119, 88, 59, 78, 89, 70, 69, 69, 109, 80, 60, 139, 139, 109, 129, 40, 60, 100, 99, 40, 88, 69, 60, 38, 89, 30, 50, 110, 90, 70, 50, 49, 70, 100, 49, 50, 40, 77, 79, 50, 120, 30, 30, 39, 80, 150, 69, 60, 50, 100, 89, 39, 60, 49, 100, 69, 40, 70, 68, 30, 30, 70, 67, 60, 118, 49, 90, 70, 29, 59, 39, 50, 79, 60, 100, 69, 98, 30, 20, 20, 50, 50, 69, 90, 69, 50, 70, 130, 1000001,}, {207750, 357856, 157109, 85157, 44121, 30655, 16518, 15604, 9637, 8268, 6123, 5559, 4084, 3681, 3144, 3089, 2430, 2074, 1923, 2107, 1757, 1836, 1458, 1084, 1088, 1038, 1005, 948, 945, 774, 717, 603, 622, 634, 563, 364, 592, 438, 528, 455, 380, 411, 390, 383, 521, 362, 300, 212, 337, 374, 283, 284, 192, 290, 183, 200, 92, 212, 190, 272, 180, 136, 192, 225, 90, 202, 182, 157, 127, 220, 212, 164, 155, 164, 137, 74, 82, 101, 81, 144, 128, 91, 108, 148, 93, 127, 128, 110, 72, 81, 92, 82, 110, 99, 37, 36, 73, 84, 91, 126, 117, 45, 55, 36, 27, 72, 56, 82, 65, 65, 56, 73, 55, 74, 73, 81, 55, 93, 55, 74, 46, 27, 63, 74, 91, 137, 54, 99, 63, 55, 27, 82, 91, 74, 36, 27, 82, 45, 27, 54, 64, 54, 63, 36, 90, 19, 56, 36, 0, 27, 90, 63, 64, 28, 54, 84, 46, 55, 72, 45, 63, 63, 45, 46, 73, 84, 45, 47, 36, 82, 91, 54, 45, 18, 54, 93, 72, 90, 72, 72, 1000001,}, {216357, 375458, 157605, 73113, 43320, 23466, 16900, 11001, 10778, 7911, 5986, 5383, 4201, 3835, 2756, 2595, 2309, 1927, 1816, 1661, 1498, 1632, 1292, 1234, 1034, 1052, 1003, 813, 827, 735, 847, 476, 824, 656, 627, 597, 368, 500, 366, 409, 357, 388, 310, 304, 388, 294, 366, 274, 269, 423, 258, 222, 199, 250, 194, 165, 186, 207, 214, 191, 97, 132, 129, 225, 155, 216, 179, 149, 184, 140, 130, 139, 120, 76, 123, 85, 200, 77, 113, 145, 121, 128, 147, 175, 70, 109, 151, 102, 107, 124, 79, 95, 69, 94, 96, 63, 97, 51, 76, 119, 34, 79, 62, 79, 79, 70, 71, 68, 17, 58, 70, 86, 69, 93, 95, 33, 96, 69, 43, 61, 86, 44, 51, 35, 78, 54, 60, 61, 89, 42, 69, 62, 85, 43, 79, 43, 53, 44, 90, 70, 61, 61, 61, 18, 80, 52, 35, 35, 71, 35, 8, 35, 42, 61, 36, 44, 52, 70, 60, 52, 61, 36, 34, 53, 43, 35, 34, 60, 35, 52, 97, 27, 18, 52, 42, 78, 81, 16, 53, 41, 1000001,}, {217801, 370593, 184007, 58301, 40870, 24245, 15392, 11316, 10494, 6659, 5560, 4885, 3960, 3121, 3183, 2747, 2277, 2191, 1743, 1509, 1737, 1222, 1255, 992, 1104, 930, 988, 866, 702, 675, 602, 494, 630, 607, 453, 449, 448, 354, 348, 380, 353, 343, 346, 299, 302, 306, 374, 234, 313, 202, 244, 208, 253, 139, 201, 122, 219, 161, 154, 154, 145, 136, 144, 120, 149, 120, 171, 170, 187, 137, 128, 128, 96, 113, 121, 169, 122, 81, 144, 92, 105, 89, 98, 162, 152, 105, 146, 65, 130, 72, 88, 57, 96, 40, 74, 130, 40, 58, 88, 89, 80, 72, 72, 49, 105, 64, 64, 88, 56, 105, 48, 89, 100, 64, 16, 72, 74, 34, 114, 98, 16, 57, 66, 65, 73, 32, 66, 73, 72, 59, 73, 66, 32, 32, 25, 80, 41, 64, 40, 8, 72, 40, 48, 72, 40, 40, 49, 48, 16, 40, 76, 40, 48, 40, 24, 64, 82, 57, 40, 65, 40, 32, 32, 57, 49, 40, 97, 8, 33, 72, 49, 42, 64, 32, 32, 57, 24, 25, 48, 89, 1000001,}, {230147, 366110, 180215, 66819, 36728, 18465, 16531, 11436, 8970, 6380, 5792, 4657, 4229, 3354, 2727, 2477, 2330, 1881, 1829, 1492, 1214, 1287, 1224, 1039, 975, 1009, 955, 766, 623, 494, 664, 595, 493, 525, 493, 408, 372, 481, 274, 304, 435, 291, 308, 317, 284, 222, 209, 238, 217, 280, 317, 137, 238, 169, 189, 113, 224, 146, 200, 173, 159, 178, 113, 165, 144, 175, 138, 154, 162, 123, 81, 101, 179, 130, 92, 98, 143, 134, 116, 119, 133, 86, 145, 115, 60, 62, 117, 107, 54, 113, 67, 45, 63, 76, 62, 116, 70, 83, 61, 92, 46, 85, 100, 47, 85, 54, 58, 62, 76, 53, 92, 55, 83, 79, 53, 55, 30, 61, 61, 87, 54, 44, 48, 55, 54, 30, 61, 68, 62, 46, 38, 40, 61, 29, 29, 55, 46, 30, 53, 48, 40, 61, 44, 61, 22, 16, 71, 47, 46, 61, 39, 52, 56, 63, 78, 36, 38, 37, 64, 69, 16, 32, 31, 31, 76, 24, 58, 54, 31, 15, 39, 61, 7, 53, 47, 62, 32, 54, 51, 51, 1000001,}, {244995, 364146, 176069, 63861, 34140, 22261, 14853, 10825, 8054, 6510, 5144, 4138, 3751, 2919, 2780, 2349, 2075, 1624, 1657, 1390, 1249, 1415, 1068, 980, 926, 840, 854, 684, 706, 635, 566, 628, 459, 597, 499, 362, 405, 427, 366, 401, 358, 297, 368, 205, 332, 246, 217, 288, 211, 253, 212, 177, 178, 196, 169, 205, 211, 135, 185, 126, 143, 205, 161, 141, 127, 113, 73, 99, 112, 105, 78, 100, 105, 136, 106, 98, 105, 63, 64, 77, 99, 77, 84, 70, 77, 113, 107, 107, 105, 49, 77, 28, 100, 70, 112, 126, 71, 77, 112, 84, 57, 77, 92, 63, 63, 84, 64, 70, 28, 56, 43, 50, 70, 57, 50, 35, 56, 50, 64, 56, 78, 49, 43, 49, 71, 14, 99, 63, 57, 57, 50, 42, 49, 49, 59, 50, 28, 21, 86, 56, 63, 35, 51, 14, 15, 42, 64, 28, 71, 21, 77, 14, 43, 42, 42, 15, 57, 14, 28, 28, 50, 50, 63, 7, 21, 36, 56, 43, 70, 43, 14, 63, 21, 35, 70, 56, 21, 36, 80, 14, 1000001,}, {255885, 374747, 163256, 64518, 31889, 20925, 12992, 10389, 7961, 6088, 5440, 4035, 3343, 2833, 2429, 2100, 1950, 1767, 1534, 1350, 1210, 1037, 931, 965, 902, 696, 728, 630, 742, 561, 602, 534, 521, 493, 361, 339, 438, 269, 326, 430, 316, 345, 321, 299, 284, 215, 235, 211, 201, 257, 221, 244, 206, 144, 209, 139, 161, 193, 159, 210, 124, 180, 83, 159, 105, 118, 97, 104, 81, 126, 124, 111, 138, 137, 130, 119, 139, 89, 48, 76, 84, 90, 84, 90, 104, 97, 76, 98, 115, 40, 63, 49, 67, 56, 97, 42, 56, 69, 49, 28, 42, 76, 110, 40, 70, 13, 56, 21, 41, 84, 41, 62, 47, 49, 27, 33, 70, 27, 41, 70, 27, 35, 48, 56, 48, 83, 56, 41, 75, 42, 63, 63, 82, 49, 49, 7, 62, 14, 49, 27, 49, 28, 28, 28, 14, 41, 48, 42, 20, 42, 48, 34, 21, 56, 42, 28, 54, 40, 42, 55, 77, 28, 63, 42, 84, 34, 42, 42, 35, 14, 27, 21, 34, 14, 42, 28, 14, 62, 42, 42, 1000001,}, {275476, 400625, 133669, 57933, 29668, 17071, 13272, 9339, 7672, 5634, 5065, 3924, 3563, 2680, 2191, 1945, 1623, 1905, 1395, 1355, 1142, 1105, 969, 856, 770, 837, 730, 550, 641, 561, 700, 560, 450, 400, 363, 405, 445, 393, 470, 386, 328, 263, 187, 292, 302, 237, 207, 134, 201, 122, 171, 185, 218, 154, 111, 179, 160, 117, 159, 169, 163, 119, 131, 121, 151, 73, 140, 73, 153, 119, 111, 126, 112, 90, 132, 77, 44, 124, 87, 103, 87, 76, 91, 72, 38, 54, 94, 67, 59, 79, 129, 34, 84, 58, 111, 84, 69, 46, 74, 71, 60, 50, 59, 56, 26, 58, 74, 45, 52, 66, 46, 33, 53, 44, 60, 14, 33, 53, 38, 46, 13, 46, 39, 51, 67, 40, 65, 26, 57, 67, 63, 27, 46, 72, 60, 44, 26, 34, 52, 65, 59, 33, 27, 32, 52, 25, 72, 19, 59, 53, 72, 47, 45, 41, 45, 26, 39, 74, 41, 19, 45, 19, 32, 49, 30, 37, 59, 12, 60, 66, 20, 47, 32, 14, 47, 31, 52, 50, 52, 81, 1000001,}, {296187, 384965, 143071, 48784, 28155, 18979, 12015, 9586, 6514, 5212, 4686, 3695, 2978, 2862, 2055, 1997, 1841, 1567, 1529, 1140, 1073, 997, 1038, 668, 775, 727, 690, 598, 553, 535, 425, 477, 413, 364, 403, 382, 345, 320, 366, 325, 310, 294, 235, 230, 158, 243, 225, 189, 219, 211, 169, 213, 120, 242, 193, 150, 107, 153, 107, 146, 124, 119, 163, 123, 113, 92, 70, 126, 82, 109, 88, 116, 94, 124, 98, 85, 89, 81, 115, 118, 75, 65, 135, 38, 94, 95, 80, 44, 82, 62, 62, 56, 65, 91, 68, 38, 45, 20, 49, 46, 42, 25, 43, 55, 36, 54, 49, 14, 63, 50, 58, 43, 25, 58, 31, 26, 56, 45, 45, 42, 61, 57, 50, 57, 31, 40, 31, 20, 50, 62, 18, 32, 18, 39, 36, 38, 49, 43, 61, 69, 32, 25, 25, 97, 69, 36, 19, 30, 66, 44, 19, 24, 52, 19, 32, 39, 25, 20, 63, 37, 18, 56, 57, 31, 65, 37, 38, 12, 57, 65, 26, 36, 37, 36, 50, 18, 56, 64, 51, 51, 1000001,}, {350847, 356581, 119956, 52037, 25771, 17329, 10941, 9696, 6478, 5337, 4447, 3713, 2712, 2655, 2441, 1985, 1610, 1493, 1288, 1034, 1038, 1067, 919, 762, 875, 763, 711, 663, 586, 472, 450, 397, 458, 361, 356, 308, 395, 274, 295, 315, 230, 262, 200, 207, 241, 194, 233, 246, 170, 245, 121, 164, 189, 176, 135, 136, 167, 143, 128, 124, 157, 104, 115, 86, 96, 119, 129, 85, 94, 119, 144, 85, 113, 88, 135, 95, 90, 102, 87, 63, 89, 68, 85, 52, 57, 48, 72, 39, 81, 70, 106, 67, 53, 52, 51, 66, 57, 62, 57, 77, 62, 45, 63, 79, 36, 50, 57, 29, 51, 39, 73, 16, 34, 36, 62, 56, 39, 34, 24, 34, 57, 18, 75, 56, 35, 51, 28, 34, 54, 22, 35, 23, 29, 66, 34, 29, 45, 30, 34, 23, 38, 41, 39, 44, 27, 21, 45, 11, 39, 18, 59, 57, 22, 32, 27, 40, 52, 52, 23, 41, 16, 52, 23, 28, 40, 48, 38, 29, 34, 35, 40, 18, 29, 35, 29, 30, 17, 23, 18, 52, 1000001,}, {372638, 341773, 119178, 49426, 25775, 16806, 11169, 8429, 6883, 4712, 4268, 3173, 2945, 2634, 2190, 1938, 1511, 1371, 1435, 1085, 963, 1016, 836, 776, 623, 764, 600, 527, 470, 467, 413, 467, 381, 427, 386, 361, 340, 307, 194, 308, 238, 227, 199, 299, 208, 241, 220, 229, 216, 157, 190, 144, 210, 167, 114, 100, 134, 87, 122, 170, 116, 91, 109, 133, 84, 89, 99, 94, 133, 96, 102, 107, 106, 66, 61, 115, 52, 73, 106, 81, 76, 61, 76, 39, 94, 74, 108, 79, 79, 41, 40, 60, 56, 65, 62, 39, 108, 45, 28, 51, 35, 62, 21, 52, 26, 33, 37, 31, 56, 44, 52, 52, 50, 35, 74, 26, 26, 79, 50, 71, 33, 23, 45, 23, 35, 28, 44, 68, 34, 41, 60, 54, 51, 46, 45, 40, 50, 23, 44, 35, 49, 44, 41, 70, 54, 42, 29, 37, 41, 44, 23, 30, 28, 53, 12, 34, 38, 21, 18, 38, 18, 27, 28, 49, 29, 45, 35, 28, 18, 28, 57, 44, 17, 44, 29, 44, 51, 29, 11, 59, 1000001,}, {373678, 346497, 123433, 44840, 25087, 14314, 12231, 7035, 6456, 4541, 4304, 3149, 2676, 2359, 2011, 1796, 1703, 1480, 1219, 1066, 899, 881, 850, 587, 719, 692, 577, 507, 445, 555, 470, 388, 488, 418, 299, 365, 352, 332, 356, 253, 249, 237, 235, 242, 231, 153, 227, 195, 185, 169, 200, 155, 98, 106, 226, 202, 126, 68, 155, 140, 140, 55, 119, 88, 124, 95, 131, 136, 118, 105, 78, 102, 62, 61, 116, 63, 55, 59, 87, 79, 74, 58, 83, 134, 71, 75, 46, 69, 41, 59, 65, 29, 33, 51, 77, 45, 82, 23, 44, 68, 82, 21, 56, 44, 40, 45, 21, 61, 35, 39, 27, 46, 37, 55, 41, 27, 60, 27, 49, 61, 24, 27, 44, 58, 50, 28, 38, 39, 27, 35, 45, 34, 66, 35, 36, 30, 10, 27, 48, 67, 54, 21, 39, 26, 17, 39, 81, 73, 35, 37, 34, 28, 28, 27, 31, 43, 16, 23, 62, 53, 38, 10, 16, 49, 28, 21, 44, 37, 26, 11, 11, 40, 10, 22, 32, 28, 49, 28, 40, 45, 1000001,}, {372648, 354594, 121119, 43845, 23644, 15389, 10400, 8021, 6148, 4711, 3855, 2930, 2670, 2370, 1747, 1683, 1385, 1244, 1046, 1061, 1090, 728, 787, 684, 731, 671, 677, 535, 512, 385, 379, 374, 388, 294, 355, 347, 279, 289, 195, 259, 259, 244, 236, 213, 207, 151, 164, 157, 246, 136, 144, 170, 181, 172, 115, 101, 133, 93, 116, 70, 112, 128, 94, 125, 129, 107, 76, 131, 116, 107, 105, 83, 95, 87, 92, 59, 64, 57, 92, 64, 78, 112, 63, 98, 71, 63, 52, 78, 47, 69, 64, 87, 47, 41, 39, 47, 27, 75, 51, 60, 33, 63, 58, 53, 58, 48, 26, 69, 90, 53, 65, 25, 49, 75, 27, 16, 17, 32, 63, 25, 53, 22, 0, 34, 47, 44, 46, 39, 42, 31, 27, 18, 53, 27, 47, 53, 39, 32, 17, 38, 32, 51, 63, 36, 32, 55, 41, 27, 46, 31, 27, 43, 57, 16, 38, 33, 39, 26, 33, 16, 22, 17, 44, 33, 29, 0, 28, 42, 32, 27, 26, 37, 50, 50, 33, 26, 38, 36, 50, 16, 1000001,}, {373378, 381105, 94923, 48056, 22307, 13712, 10718, 7466, 5321, 4455, 3869, 3182, 2745, 2120, 1897, 1534, 1478, 1308, 1109, 1023, 963, 770, 767, 769, 604, 459, 530, 479, 448, 473, 419, 385, 375, 402, 384, 293, 198, 265, 232, 248, 193, 201, 246, 159, 216, 204, 197, 180, 154, 153, 157, 111, 129, 163, 154, 117, 187, 149, 85, 59, 111, 93, 118, 69, 85, 66, 92, 101, 62, 78, 96, 80, 76, 66, 64, 65, 67, 79, 51, 76, 53, 45, 96, 67, 37, 69, 113, 88, 58, 38, 48, 34, 63, 63, 47, 41, 24, 59, 62, 57, 58, 50, 42, 49, 52, 46, 27, 54, 42, 38, 43, 58, 32, 47, 43, 54, 42, 39, 52, 47, 44, 33, 20, 24, 38, 10, 62, 55, 23, 33, 25, 57, 37, 33, 43, 45, 43, 29, 22, 32, 47, 27, 29, 33, 23, 22, 30, 25, 33, 55, 14, 24, 25, 37, 14, 23, 29, 24, 29, 24, 43, 30, 30, 34, 57, 28, 54, 43, 20, 20, 23, 8, 25, 33, 37, 30, 59, 33, 45, 19, 1000001,}, {379699, 394415, 84623, 39690, 24300, 13408, 10382, 6893, 5605, 4605, 3568, 2945, 2443, 1970, 1775, 1515, 1343, 1263, 1073, 1005, 847, 766, 692, 661, 631, 496, 564, 464, 456, 486, 453, 395, 289, 345, 296, 299, 240, 222, 297, 172, 277, 225, 203, 159, 155, 149, 172, 171, 147, 133, 157, 142, 131, 108, 106, 131, 135, 122, 91, 104, 122, 70, 85, 61, 70, 115, 98, 98, 91, 62, 74, 88, 96, 56, 72, 65, 79, 97, 54, 81, 101, 63, 55, 72, 48, 62, 62, 39, 59, 32, 61, 57, 38, 38, 53, 33, 64, 42, 53, 53, 36, 59, 32, 32, 37, 58, 33, 28, 34, 47, 44, 34, 24, 27, 48, 37, 58, 37, 0, 68, 32, 52, 29, 32, 38, 25, 53, 34, 43, 28, 34, 37, 22, 38, 20, 18, 44, 15, 34, 40, 19, 33, 51, 39, 17, 28, 23, 24, 38, 60, 31, 24, 28, 23, 42, 34, 41, 37, 41, 9, 32, 24, 29, 33, 28, 4, 15, 28, 30, 34, 15, 32, 32, 34, 38, 39, 30, 48, 27, 38, 1000001,}, {382106, 390554, 95935, 34213, 21866, 13834, 9345, 7220, 5659, 4214, 3235, 2805, 2166, 2074, 1682, 1446, 1340, 1142, 1149, 1009, 825, 762, 717, 609, 630, 631, 505, 485, 431, 432, 341, 327, 386, 258, 249, 323, 306, 295, 175, 192, 175, 173, 176, 173, 174, 150, 142, 121, 183, 205, 160, 135, 137, 109, 126, 122, 122, 139, 87, 126, 114, 87, 106, 81, 89, 79, 65, 64, 82, 50, 30, 67, 68, 76, 93, 89, 121, 86, 79, 65, 48, 81, 48, 57, 59, 67, 56, 66, 56, 54, 58, 36, 64, 37, 55, 51, 37, 46, 23, 36, 78, 41, 60, 41, 9, 45, 35, 22, 42, 41, 38, 28, 37, 48, 22, 28, 37, 26, 42, 38, 39, 40, 46, 54, 31, 37, 24, 51, 69, 17, 25, 36, 40, 14, 31, 37, 0, 57, 40, 34, 16, 18, 19, 18, 21, 18, 36, 26, 13, 25, 22, 22, 31, 26, 32, 10, 31, 9, 26, 5, 18, 32, 31, 55, 36, 45, 38, 38, 31, 19, 18, 41, 34, 23, 32, 42, 23, 48, 27, 32, 1000001,}, {386327, 391087, 92712, 36683, 20922, 14014, 8730, 6793, 4920, 3812, 3117, 2652, 2431, 1841, 1590, 1243, 1331, 1151, 997, 973, 840, 746, 680, 608, 594, 578, 521, 444, 364, 374, 407, 374, 297, 252, 272, 226, 262, 287, 232, 190, 241, 173, 217, 161, 144, 159, 176, 180, 116, 158, 143, 131, 142, 144, 120, 136, 131, 101, 120, 105, 107, 124, 110, 79, 83, 56, 103, 65, 112, 74, 73, 52, 82, 63, 50, 37, 69, 67, 39, 61, 87, 64, 50, 64, 39, 61, 43, 52, 58, 21, 74, 41, 49, 44, 69, 39, 22, 46, 51, 35, 42, 45, 35, 27, 48, 13, 54, 31, 63, 35, 45, 28, 46, 39, 39, 40, 35, 61, 51, 49, 28, 36, 45, 13, 48, 17, 52, 49, 30, 54, 37, 31, 18, 18, 17, 17, 19, 12, 33, 25, 41, 44, 18, 35, 34, 30, 13, 53, 17, 36, 48, 18, 60, 23, 26, 23, 32, 40, 50, 19, 18, 4, 35, 14, 21, 27, 56, 22, 24, 37, 26, 31, 17, 40, 32, 23, 22, 26, 22, 32, 1000001,}, {391116, 394513, 86341, 36580, 21062, 12912, 8522, 6978, 4907, 3947, 2901, 2632, 2219, 1886, 1711, 1478, 1278, 1227, 976, 824, 736, 730, 582, 684, 538, 484, 382, 427, 409, 422, 334, 364, 337, 346, 283, 244, 269, 230, 239, 266, 188, 250, 204, 224, 191, 132, 156, 138, 119, 117, 132, 86, 110, 157, 126, 113, 83, 117, 72, 95, 119, 86, 101, 58, 74, 85, 96, 54, 86, 46, 72, 60, 74, 47, 77, 36, 56, 70, 46, 42, 32, 68, 82, 45, 46, 52, 40, 45, 44, 35, 62, 52, 58, 41, 57, 59, 34, 39, 46, 42, 26, 59, 49, 28, 59, 25, 42, 40, 46, 29, 42, 30, 43, 26, 61, 18, 33, 35, 22, 46, 20, 29, 34, 40, 20, 17, 40, 41, 24, 45, 29, 21, 28, 52, 39, 34, 21, 22, 38, 35, 16, 30, 17, 46, 34, 26, 14, 41, 25, 13, 36, 30, 13, 39, 21, 26, 21, 33, 38, 26, 39, 18, 13, 53, 5, 25, 18, 63, 39, 4, 28, 47, 36, 9, 14, 17, 28, 39, 36, 47, 1000001,}, {396613, 388092, 91253, 37559, 19378, 11592, 8416, 5979, 5004, 3795, 3076, 2514, 2247, 1700, 1624, 1502, 1235, 1045, 957, 845, 766, 619, 565, 706, 571, 528, 508, 425, 388, 343, 295, 307, 296, 308, 295, 264, 261, 218, 212, 199, 168, 229, 182, 179, 177, 176, 144, 168, 141, 153, 116, 115, 134, 106, 89, 91, 114, 114, 118, 117, 81, 71, 95, 88, 103, 93, 75, 96, 53, 61, 74, 63, 33, 102, 53, 59, 52, 67, 46, 47, 68, 66, 56, 37, 45, 65, 41, 59, 44, 54, 30, 41, 38, 40, 53, 59, 38, 85, 25, 43, 47, 51, 13, 42, 39, 34, 17, 24, 41, 55, 39, 53, 16, 28, 33, 42, 21, 16, 45, 21, 16, 27, 16, 41, 51, 18, 20, 22, 25, 42, 16, 25, 13, 35, 29, 36, 16, 26, 20, 18, 73, 20, 21, 32, 13, 29, 16, 26, 36, 32, 35, 13, 43, 29, 20, 20, 30, 32, 29, 50, 20, 20, 33, 5, 41, 24, 33, 26, 26, 0, 36, 24, 21, 12, 10, 12, 48, 29, 24, 39, 1000001,}, {401226, 390883, 89389, 33594, 18538, 13115, 8296, 5899, 4853, 3479, 2803, 2253, 2089, 1694, 1362, 1357, 1167, 947, 895, 872, 745, 626, 651, 567, 502, 454, 492, 424, 339, 400, 350, 314, 290, 314, 315, 258, 250, 199, 210, 190, 142, 157, 140, 207, 170, 181, 171, 133, 133, 156, 135, 118, 148, 107, 118, 122, 49, 84, 142, 92, 87, 91, 97, 79, 73, 102, 82, 53, 77, 87, 124, 75, 77, 55, 69, 70, 65, 37, 73, 61, 45, 40, 36, 62, 45, 41, 24, 61, 58, 37, 28, 40, 57, 66, 44, 28, 53, 36, 36, 32, 45, 47, 28, 20, 53, 28, 40, 50, 32, 41, 30, 20, 42, 28, 29, 48, 32, 33, 34, 58, 29, 28, 30, 8, 31, 28, 16, 20, 20, 28, 17, 21, 33, 24, 17, 30, 24, 29, 12, 24, 52, 32, 16, 17, 25, 17, 28, 38, 38, 28, 32, 16, 40, 37, 24, 37, 28, 20, 17, 31, 17, 40, 29, 29, 21, 17, 33, 41, 21, 37, 21, 20, 20, 24, 45, 12, 20, 8, 16, 41, 1000001,}, {408774, 392195, 83007, 33206, 19651, 11246, 8392, 5913, 4396, 3575, 2792, 2355, 1932, 1769, 1358, 1137, 1150, 1153, 912, 684, 737, 541, 610, 568, 421, 488, 429, 408, 380, 291, 299, 340, 294, 278, 207, 311, 224, 175, 210, 219, 183, 220, 152, 112, 175, 124, 158, 180, 119, 112, 151, 127, 106, 82, 108, 98, 93, 94, 94, 102, 72, 95, 63, 67, 110, 39, 67, 80, 67, 52, 34, 30, 58, 58, 38, 43, 59, 43, 62, 71, 59, 61, 49, 19, 23, 39, 31, 58, 75, 61, 65, 50, 24, 26, 56, 20, 36, 32, 51, 60, 50, 28, 40, 34, 48, 48, 46, 28, 24, 35, 39, 47, 40, 46, 20, 45, 16, 29, 42, 28, 32, 23, 8, 35, 34, 15, 11, 28, 20, 20, 27, 39, 8, 28, 32, 16, 20, 30, 20, 31, 23, 31, 28, 23, 40, 40, 42, 16, 28, 31, 22, 23, 19, 31, 51, 20, 24, 20, 35, 31, 20, 26, 34, 12, 34, 44, 26, 30, 22, 12, 8, 25, 23, 14, 24, 20, 51, 22, 16, 30, 1000001,}, {417681, 396113, 73161, 33140, 16869, 12226, 7354, 5980, 4416, 3590, 2857, 2392, 1843, 1586, 1342, 1270, 1145, 1066, 840, 823, 686, 633, 473, 518, 529, 470, 436, 438, 362, 286, 309, 263, 291, 278, 257, 248, 291, 197, 190, 170, 212, 192, 178, 126, 174, 164, 168, 157, 133, 71, 116, 119, 117, 105, 89, 152, 66, 106, 111, 82, 93, 79, 89, 91, 81, 78, 72, 71, 63, 44, 70, 42, 28, 101, 46, 33, 48, 83, 33, 45, 45, 71, 64, 26, 36, 61, 47, 42, 56, 26, 56, 29, 52, 26, 35, 44, 55, 46, 42, 37, 58, 55, 26, 31, 31, 36, 43, 21, 46, 28, 19, 48, 36, 15, 31, 38, 31, 27, 11, 20, 18, 48, 28, 36, 15, 33, 11, 20, 39, 25, 35, 43, 47, 15, 29, 36, 19, 18, 26, 35, 26, 16, 31, 27, 27, 39, 19, 3, 19, 27, 26, 7, 30, 29, 8, 26, 15, 30, 31, 23, 22, 16, 28, 32, 14, 17, 19, 28, 18, 21, 28, 26, 27, 37, 28, 16, 27, 19, 12, 37, 1000001,}, {423950, 398062, 67636, 32514, 17040, 10684, 7967, 5591, 4304, 3270, 2609, 2253, 1918, 1807, 1428, 1336, 1089, 1059, 823, 849, 636, 653, 594, 523, 531, 411, 354, 315, 407, 286, 325, 310, 271, 268, 243, 265, 215, 248, 186, 188, 191, 200, 132, 187, 123, 157, 97, 99, 120, 125, 114, 117, 122, 62, 67, 79, 75, 70, 112, 75, 69, 68, 59, 56, 82, 52, 59, 45, 79, 67, 67, 96, 29, 60, 47, 53, 51, 37, 70, 63, 63, 43, 36, 50, 33, 35, 40, 42, 18, 27, 53, 21, 52, 52, 48, 30, 29, 71, 22, 28, 50, 44, 50, 58, 31, 38, 47, 44, 34, 15, 63, 38, 19, 45, 30, 47, 32, 21, 14, 57, 35, 38, 22, 38, 15, 8, 38, 26, 11, 43, 24, 25, 17, 35, 44, 22, 34, 22, 20, 6, 23, 30, 19, 20, 11, 23, 35, 12, 23, 14, 23, 28, 23, 33, 39, 12, 25, 34, 30, 27, 36, 20, 19, 14, 29, 34, 26, 16, 20, 14, 25, 20, 12, 29, 42, 8, 22, 22, 36, 29, 1000001,}, {434774, 387574, 73211, 28198, 17169, 11116, 7254, 5690, 4254, 3265, 2658, 2173, 1745, 1516, 1349, 1253, 1003, 813, 792, 724, 726, 493, 526, 461, 513, 401, 345, 401, 370, 270, 251, 290, 315, 233, 243, 236, 211, 155, 221, 173, 193, 175, 173, 155, 127, 153, 119, 105, 127, 130, 80, 95, 98, 100, 86, 113, 98, 96, 63, 98, 62, 74, 95, 85, 77, 82, 41, 58, 46, 67, 62, 41, 51, 65, 32, 58, 67, 51, 37, 57, 44, 60, 72, 30, 50, 50, 52, 48, 32, 73, 20, 39, 51, 30, 43, 48, 19, 35, 38, 22, 38, 22, 34, 42, 43, 32, 26, 38, 22, 34, 18, 43, 34, 18, 27, 34, 27, 41, 34, 18, 24, 30, 13, 20, 25, 36, 34, 29, 20, 26, 40, 31, 26, 34, 35, 14, 12, 15, 15, 31, 23, 13, 32, 20, 11, 38, 15, 18, 27, 49, 15, 30, 16, 14, 41, 32, 23, 26, 30, 55, 11, 15, 17, 31, 30, 46, 7, 25, 41, 22, 16, 40, 23, 22, 29, 19, 16, 34, 37, 44, 1000001,}, {455100, 369090, 72966, 30084, 15845, 10118, 7128, 5286, 3989, 3262, 2596, 2078, 1747, 1597, 1265, 1139, 954, 992, 794, 769, 690, 618, 625, 427, 451, 428, 370, 390, 420, 292, 297, 335, 283, 247, 209, 186, 196, 188, 141, 169, 132, 159, 190, 119, 117, 146, 120, 121, 113, 116, 98, 90, 81, 83, 55, 65, 40, 76, 101, 71, 59, 125, 60, 77, 44, 61, 46, 47, 73, 76, 65, 70, 64, 54, 57, 54, 49, 31, 67, 43, 37, 33, 47, 58, 73, 54, 32, 34, 47, 53, 47, 33, 28, 30, 43, 36, 36, 31, 16, 37, 46, 43, 48, 46, 33, 19, 26, 36, 22, 15, 32, 26, 23, 31, 21, 24, 6, 40, 14, 62, 26, 39, 34, 40, 15, 13, 35, 16, 28, 24, 17, 26, 34, 25, 16, 29, 28, 27, 33, 31, 20, 48, 24, 28, 33, 40, 23, 45, 36, 16, 33, 31, 18, 19, 9, 18, 39, 6, 19, 23, 39, 19, 16, 21, 30, 11, 23, 25, 29, 27, 8, 36, 17, 9, 24, 33, 10, 7, 31, 28, 1000001,}, {479749, 348247, 69745, 30192, 17032, 10235, 6995, 5070, 3971, 3156, 2306, 1955, 1810, 1288, 1252, 1016, 1004, 947, 811, 661, 642, 498, 451, 520, 445, 436, 370, 343, 321, 271, 272, 275, 212, 252, 179, 208, 178, 151, 220, 202, 143, 186, 183, 161, 155, 161, 102, 133, 113, 96, 81, 124, 101, 73, 95, 81, 86, 110, 60, 89, 68, 56, 56, 59, 54, 79, 61, 53, 23, 48, 56, 87, 83, 42, 48, 39, 62, 51, 48, 38, 34, 43, 30, 52, 64, 25, 27, 56, 64, 24, 30, 31, 23, 34, 30, 45, 49, 32, 23, 15, 38, 40, 24, 25, 23, 21, 29, 37, 21, 26, 17, 4, 20, 34, 47, 36, 52, 29, 18, 23, 36, 19, 15, 27, 33, 45, 13, 10, 24, 24, 19, 31, 33, 15, 40, 28, 25, 39, 15, 24, 24, 23, 10, 16, 10, 20, 15, 16, 32, 36, 10, 21, 30, 24, 30, 17, 13, 26, 16, 27, 31, 11, 25, 31, 13, 17, 13, 24, 31, 25, 20, 17, 31, 6, 22, 28, 12, 28, 33, 25, 1000001,}};

}
