---
title: GitHub repository explorer
description: Explore public repositories using Java compiled to browser JavaScript with TeaVM.
order: 2
---

The explorer is a j2html component in `showcase-plugin`, not a handwritten
HTML page. It provides a visitor-editable GitHub owner field and an accessible
status region; `website` supplies the Electrostatic content around it.

TeaVM compiles the explorer's Java entry point to JavaScript. That compiled
asset uses TeaVM's DOM and XMLHttpRequest APIs to read the GitHub public API,
then creates repository cards with DOM methods and text nodes. The source does
not embed JavaScript snippets or a GitHub token. The j2html page layout invokes
the bundle's exported `main()` entry point after loading it. Cards are added in sequence
with a small delay so the list animates as it fills, and the stylesheet
respects reduced-motion preferences.

The explorer requests up to 100 repositories, sorted by most recently updated.
GitHub may return rate-limit, network, or unknown-owner errors; each is shown
inline so the visitor can adjust the owner or try again later.
