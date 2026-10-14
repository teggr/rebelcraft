---
title: Getting started
description: Build the j2html showcase, component catalog, and Electrostatic site.
order: 1
---

## Requirements

- Java 21 or newer
- Maven 3.9 or newer

The root Maven project uses the group ID `io.github.teggr` and Java package root
`io.github.teggr.rebelcraft`. Its `showcase-plugin` module contains reusable
j2html components, layouts, previews, and the TeaVM explorer. The `website`
module owns the Electrostatic content and consumes the component module.

## Create and preview a component

Add reusable markup to `showcase-plugin` as a j2html component:

```java
package io.github.teggr.rebelcraft.showcase;

import j2html.tags.DomContent;

import static j2html.TagCreator.article;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.p;

public final class WelcomeCard {
    private WelcomeCard() {
    }

    public static DomContent create(String heading, String message) {
        return article().with(h2(heading), p(message));
    }
}
```

Expose that component to the preview catalog in the same plugin module:

```java
package io.github.teggr.rebelcraft.showcase;

import io.github.teggr.html.preview.Preview;

public class WelcomeCardPreviews {
    @Preview(value = "Welcome card", group = "Getting started")
    public String welcomeCard() {
        return WelcomeCard.create("Hello, Java HTML", "This is a reusable j2html component.")
                .render();
    }
}
```

The generic catalog runtime renders the preview's returned HTML string. The
build discovers it automatically and adds it to the generated catalog.

## Build everything

From the repository root, run:

```sh
mvn --batch-mode --no-transfer-progress --projects website --also-make verify
```

The build compiles the repository explorer to JavaScript with TeaVM, generates
the HTML preview catalog, assembles the plugin components into the website's
static assets, and builds Electrostatic output in
`website/target/generated-site`.

Add or revise website prose in
`website/src/main/resources/site`; the generated catalog and interactive
explorer are assembled from the plugin module during the same build.

Open `website/target/generated-site/index.html` in a browser. The generated
catalog is available at `website/target/generated-site/catalog/index.html`.

## Preview locally

Start Electrostatic's local server with the website's assembled source and a
local base URL:

```sh
mvn --batch-mode --no-transfer-progress --projects website --also-make verify
mvn --file website/pom.xml \
  -Delectrostatic.inputDirectory=target/site-source \
  -Delectrostatic.outputDirectory=target/generated-site \
  "-Delectrostatic.baseUrl=http://localhost:8000/" \
  -Delectrostatic.port=8000 \
  io.github.teggr:electrostatic-maven-plugin:0.0.4:serve
```

Open `http://localhost:8000/` for the site, `http://localhost:8000/repos/` to
try the explorer, or `http://localhost:8000/catalog/` for the component
previews. The GitHub repository API requires a browser origin, so test the
explorer through Electrostatic's local server rather than opening a file
directly.

## Publish the website

Push the changes to `main`. The repository's
`.github/workflows/build-site.yml` runs the same Maven build, uploads the
Electrostatic output, and deploys it to GitHub Pages. Enable **Settings →
Pages → Build and deployment → GitHub Actions** once in the repository; the
workflow also supports manual runs from the Actions tab.

## Published dependencies

The showcase consumes these published modules:

| Project | Artifact used | Version |
| --- | --- | --- |
| [j2html-toolkit](https://github.com/teggr/j2html-toolkit) | `j2html-toolkit-essentials` | `0.0.6` |
| [java-html-toolkit](https://github.com/teggr/java-html-toolkit) | `java-html-toolkit-html-catalog` | `1.0.3` |
| [Electrostatic](https://github.com/teggr/electrostatic) | `electrostatic-maven-plugin` | `0.0.4` |
| [TeaVM](https://teavm.org/docs/intro/getting-started.html) | `teavm-maven-plugin` | `0.16.0` |

The preview methods return rendered HTML strings so the published generic
catalog runtime can render the selected j2html release without crossing the
older adapter's incompatible preview API package.
