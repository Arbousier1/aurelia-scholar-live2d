#target photoshop
app.bringToFront();

/*
Aurelia Scholar Live2D Photoshop Split Bootstrap

Usage:
1. Open the clean 1024x1536 reference image in Photoshop.
2. Save a copy before running.
3. File > Scripts > Browse... > select this JSX.
4. The script creates Live2D PSD groups and first-pass copied region layers.
5. Refine every mask edge manually, redraw hidden areas, then export PSD/Cubism assets.

This is a bootstrap, not final magic extraction. Rectangular selections are only the first pass.
*/

if (!app.documents.length) {
  alert('Open the clean Aurelia reference image before running this script.');
  throw new Error('No active document');
}

var doc = app.activeDocument;
app.preferences.rulerUnits = Units.PIXELS;

var groups = [
  '00_GUIDE_COMMERCIAL_CLEANUP',
  '01_BODY_BASE',
  '02_HEAD_FACE',
  '03_EYES_BROWS_MOUTH_GLASSES',
  '04_HAIR',
  '05_OUTFIT_BLOUSE_BOW',
  '06_CAPE_SKIRT_TRANSPARENT_CLOTH',
  '07_ARMS_HANDS_BOOK',
  '08_LEGS_SOCKS_SHOES_WATCH',
  '09_HEAD_ACCESSORIES_EFFECTS',
  '10_SCENE_OPTIONAL'
];

var groupMap = {};
for (var i = 0; i < groups.length; i++) {
  var set = doc.layerSets.add();
  set.name = groups[i];
  groupMap[groups[i]] = set;
}

var regions = [
  ['00_REFERENCE', 'character-full__SOURCE_COPY', [155, 38, 910, 1518]],
  ['02_HEAD_FACE', 'Head_Face_Base__MASK_REFINE', [423, 122, 616, 337]],
  ['03_EYES_BROWS_MOUTH_GLASSES', 'Eyes_Glasses_All__SPLIT_REQUIRED', [430, 205, 605, 276]],
  ['03_EYES_BROWS_MOUTH_GLASSES', 'Mouth_Nose__EXPRESSION_SET', [489, 265, 556, 316]],
  ['04_HAIR', 'Ahoge_01_03__PHYSICS', [459, 32, 584, 130]],
  ['04_HAIR', 'Bangs_All__STRAND_SPLIT', [360, 92, 648, 254]],
  ['04_HAIR', 'SideHair_L__MULTI_CHAIN', [190, 158, 481, 925]],
  ['04_HAIR', 'SideHair_R__MULTI_CHAIN', [568, 145, 884, 902]],
  ['04_HAIR', 'BackHair_Base__BEHIND_BODY', [221, 95, 875, 1170]],
  ['09_HEAD_ACCESSORIES_EFFECTS', 'HeadFlower_Ribbon__PETAL_SPLIT', [611, 123, 747, 251]],
  ['05_OUTFIT_BLOUSE_BOW', 'NeckBow_Starflower__COMMERCIAL_MARK', [428, 336, 570, 457]],
  ['07_ARMS_HANDS_BOOK', 'Arm_L_Hand__FINGER_SPLIT', [337, 300, 493, 596]],
  ['07_ARMS_HANDS_BOOK', 'Arm_R_Hand__BOOK_GRIP', [483, 430, 691, 637]],
  ['07_ARMS_HANDS_BOOK', 'Book_Prop__HINGED', [482, 394, 679, 598]],
  ['05_OUTFIT_BLOUSE_BOW', 'Blouse_Sleeves_Cuffs__RUFFLE_SPLIT', [294, 342, 728, 784]],
  ['06_CAPE_SKIRT_TRANSPARENT_CLOTH', 'Cape_L__TRANSPARENT_CLOTH', [158, 533, 426, 1407]],
  ['06_CAPE_SKIRT_TRANSPARENT_CLOTH', 'Cape_R__TRANSPARENT_CLOTH', [617, 506, 911, 1428]],
  ['06_CAPE_SKIRT_TRANSPARENT_CLOTH', 'Skirt_Lace_Frills__SEGMENT_SPLIT', [300, 646, 631, 894]],
  ['08_LEGS_SOCKS_SHOES_WATCH', 'PocketWatch_Chain__PENDULUM', [580, 610, 672, 823]],
  ['08_LEGS_SOCKS_SHOES_WATCH', 'Legs_Base__REDRAW_UNDER_SKIRT', [391, 810, 620, 1375]],
  ['08_LEGS_SOCKS_SHOES_WATCH', 'Sock_Shoe_L__PART_SPLIT', [405, 1190, 559, 1530]],
  ['08_LEGS_SOCKS_SHOES_WATCH', 'Sock_Shoe_R__PART_SPLIT', [545, 1130, 710, 1462]],
  ['08_LEGS_SOCKS_SHOES_WATCH', 'GarterFlower_Ribbons__PETAL_SPLIT', [489, 860, 604, 1012]]
];

function selectRect(bounds) {
  var l = bounds[0], t = bounds[1], r = bounds[2], b = bounds[3];
  doc.selection.select([[l,t], [r,t], [r,b], [l,b]]);
}

function copyRegionToLayer(groupName, layerName, bounds) {
  selectRect(bounds);
  doc.selection.copy();
  var newLayer = doc.paste();
  newLayer.name = layerName;
  if (groupMap[groupName]) {
    newLayer.move(groupMap[groupName], ElementPlacement.INSIDE);
  }
  doc.selection.deselect();
}

for (var j = 0; j < regions.length; j++) {
  var g = regions[j][0];
  if (g === '00_REFERENCE') {
    var refSet = doc.layerSets.add();
    refSet.name = '00_REFERENCE';
    groupMap[g] = refSet;
  }
  copyRegionToLayer(regions[j][0], regions[j][1], regions[j][2]);
}

var noteLayer = doc.artLayers.add();
noteLayer.name = 'README_DO_NOT_EXPORT__manual_mask_refine_required';
noteLayer.kind = LayerKind.TEXT;
noteLayer.textItem.contents = 'Bootstrap complete. Refine masks, redraw hidden areas, split strands/fingers/lace/chains into final layers per final-layer-inventory.csv.';
noteLayer.textItem.size = 24;
noteLayer.textItem.position = [40, 60];

alert('Aurelia Live2D split bootstrap complete. Refine masks and redraw hidden areas manually.');
