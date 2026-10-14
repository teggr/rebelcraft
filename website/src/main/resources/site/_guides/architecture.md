---
title: How the modules fit together
description: Follow components, compiled browser code, and Electrostatic content through the Maven build.
order: 1
---

## `showcase-plugin`

`showcase-plugin` is a reusable Java library with the j2html components and
layouts that make up the showcase page. Its `@Preview` methods render the same
components used by the site. The HTML catalog generator discovers those
examples from the module's compiled classes.

The repository explorer is written in Java and uses TeaVM's browser APIs for
the DOM and GitHub API request. The TeaVM Maven goal compiles that source into
the plugin's `repository-explorer.js` resource; the site build copies the
compiled output rather than introducing handwritten client-side JavaScript.
The j2html layout loads that bundle and calls its TeaVM-exported `main()`
entry point; all explorer behavior runs from the compiled Java code.

## `website`

`website` owns the editable Markdown content and `site-config.xml`. During
`process-classes`, its asset generator consumes the plugin's j2html layout,
compiled TeaVM asset, stylesheet, and generated catalog. Electrostatic then
builds the content and static files into `website/target/generated-site`.

The explorer defaults to `teggr`. Visitors can provide a GitHub user or
organization; the browser makes an unauthenticated request to the GitHub
public repositories API. Only the first 100 repositories sorted by last
update are shown. GitHub applies unauthenticated rate limits; rate-limit,
network, invalid-account, and empty-result states are displayed in the page.

## Build and deploy

The same Maven `verify` command used locally is run in GitHub Actions. On a
push to `main`, Actions uploads `website/target/generated-site` as a Pages
artifact and deploys it. The workflow can also be started manually from the
Actions tab. Configure the repository's Pages source as **GitHub Actions**.
