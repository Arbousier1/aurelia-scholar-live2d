# Final Split Order

This file completes the planning pass for the flattened reference image. It is ready for an artist to redraw hidden areas and build the production PSD.

## Pass 0 — Commercial cleanup

- Remove third-party text, marks, stamps and company identifiers.
- Replace all logo-like marks with Aurelia Starflower originals.
- Keep only the warm-orange scholar companion concept, not the original branded UI card.

## Pass 1 — Depth separation

1. Background frame and compass.
2. Optional scene props: books, cup, vase, flowers, papers.
3. Back hair base and long hair mass.
4. Cape back, cape left, cape right and transparent veil layers.
5. Body base, legs, blouse, skirt and sleeves.
6. Arms, fingers and book prop.
7. Neck bow, chest symbol, watch and chain.
8. Head, face, eyes, glasses, mouth, bangs, ahoge and head accessories.
9. Foreground particles and sparkles.

## Pass 2 — Hidden-area redraw

- Face under bangs, glasses and hair.
- Scalp under ahoge and bang roots.
- Neck and shoulders under collar and hair.
- Torso under blouse, arms and book.
- Hands/fingers under book edges.
- Legs under skirt and sock folds.
- Cape and skirt cloth hidden behind hair and arms.

## Pass 3 — PSD export rules

- Use the names from `final-layer-inventory.csv` exactly.
- Preserve transparent pixels around hair tips, lace, ribbons and chains.
- Do not flatten glasses with eyes, hands with book, or cape with hair.
- Export source PSD plus Cubism-ready texture atlas after rig validation.
