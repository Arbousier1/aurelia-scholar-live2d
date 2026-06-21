# Claude Scholar Prototype QA Checklist

## Browser prototype

- [ ] `index.html` opens directly from disk.
- [ ] `python3 -m http.server` serves the prototype without console errors.
- [ ] All expression buttons change visible state.
- [ ] All motion buttons trigger an animation and return to idle.
- [ ] Hair, cape, ribbon, watch and paper layers animate independently.
- [ ] The layout remains usable at desktop and mobile widths.

## Cubism migration readiness

- [ ] `model-manifest.json` lists the same expressions and motions as the UI.
- [ ] `runtime/claude-scholar.model3.json` references every expression and motion file.
- [ ] `runtime/expressions/*.exp3.json` parameter IDs match the premium spec.
- [ ] `runtime/motions/*.motion3.json` includes fade timing and non-looping interaction motions.
- [ ] `runtime/physics.json` covers hair, cape, ribbon and watch physical groups.
- [ ] `layer-map.json` maps prototype selectors to PSD groups and Cubism part/parameter names.

## Production handoff

- [ ] Replace placeholder browser SVG with full-resolution PSD-split artwork.
- [ ] Replace placeholder `Moc` and screenshot texture references with `.moc3` and texture atlas files.
- [ ] Confirm third-party brand text/symbols are replaced before commercial/public release.
