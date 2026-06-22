# Phase 1 Cut Order

This is the first practical split pass for the supplied reference image.

## 1. Clean commercial brand areas

1. Remove/replace the large left logo and text block.
2. Replace the profile card copy with original Aurelia profile text.
3. Replace top-right brand text and bottom-left stamp.
4. Replace all star-like third-party marks with the original Aurelia Starflower mark.

## 2. Extract/redraw main model silhouette

1. Isolate the full character silhouette from background props.
2. Rebuild hidden face, scalp, neck, torso, arms, legs and clothing under occlusions.
3. Keep back hair and cape behind the body as separate depth layers.

## 3. Split high-priority Live2D parts

1. Face, eyes, brows, mouth and glasses.
2. Ahoge, bangs, side hair, back hair and hair tips.
3. Neck bow, chest starflower and blouse/cuffs.
4. Hands, fingers and book grip.
5. Cape, skirt lace and transparent cloth layers.
6. Legs, socks, shoes, watch and chain.

## 4. Move scene-only assets out of the main model

1. Floating papers and petals become particle scene items.
2. Book stack, cup, vase and flowers become optional foreground props.
3. Compass/backdrop becomes a background overlay.

## 5. QA before Cubism

- Every moving part must have hidden-area redraw.
- No third-party names, logos or company marks remain.
- All transparent cloth layers include separate texture, shadow, highlight and edge layers.
- Hands and book are not flattened together.
- Hair strands are not merged into a single blob.
