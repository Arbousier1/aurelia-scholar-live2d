# Photoshop Split Bootstrap

This folder contains a Photoshop JSX script that starts the actual PSD split work for the clean 1024×1536 character reference.

## Files

- `aurelia_live2d_split_bootstrap.jsx` — creates Live2D production groups and copies first-pass rectangular source regions into named layers.
- `../split/clean-reference-coordinate-map.json` — coordinate map used by the script.
- `../split/final-layer-inventory.csv` — final target layer list for manual refinement.

## How to run

1. Open the clean 1024×1536 reference image in Photoshop.
2. Save a copy as your working PSD.
3. Run `File > Scripts > Browse...` and select `aurelia_live2d_split_bootstrap.jsx`.
4. Photoshop will create groups and region layers.
5. Manually refine masks, split hair strands/fingers/lace/chains, and redraw hidden areas according to `final-layer-inventory.csv`.

## Important

The script is a professional bootstrap, not a one-click final PSD. Rectangular selections must be refined by a human artist to match the original illustration quality.
