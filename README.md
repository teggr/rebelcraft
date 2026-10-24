# RebelCraft Java HTML showcase

An end-to-end example of building j2html components in Java, previewing them
in a generated catalog, and publishing an Electrostatic site with a
TeaVM-compiled GitHub repository explorer.

## Modules

- `showcase-plugin` (`io.github.teggr:rebelcraft-showcase-plugin`) owns every
  reusable j2html component and layout, the `@Preview` examples, styles, and
  the TeaVM Java source compiled into the explorer's browser asset.
- `website` (`io.github.teggr:rebelcraft-website`) owns the Electrostatic
  content and configuration, assembles the plugin assets, and builds the
  deployable static site.

The Maven group ID is `io.github.teggr`; Java code uses the
`io.github.teggr.rebelcraft` package root.

## Build

Requires Java 21 or newer and Maven 3.9 or newer:

```sh
mvn --batch-mode --no-transfer-progress --projects website --also-make verify
```

The generated website is in `website/target/generated-site`. The component
catalog is also copied into that site at `catalog/index.html`.

## Run locally

From the repository root, build the site and start Electrostatic:

```sh
mvn --batch-mode --no-transfer-progress --projects website --also-make verify && mvn --file website/pom.xml -Delectrostatic.inputDirectory=target/site-source -Delectrostatic.outputDirectory=target/generated-site -Delectrostatic.baseUrl=http://localhost:8000/ -Delectrostatic.port=8000 -Delectrostatic.watch=true io.github.teggr:electrostatic-maven-plugin:0.0.4:serve
```

Then open http://localhost:8000/. The repository explorer is at
http://localhost:8000/repos/, and the component catalog is at
http://localhost:8000/catalog/. Press Ctrl+C to stop the server. The watch
option monitors `website/target/site-source` for changes; it does not recompile
Java sources.

## Published ecosystem projects

- [j2html-toolkit](https://github.com/teggr/j2html-toolkit) provides the
  j2html components and layout API.
- [java-html-toolkit](https://github.com/teggr/java-html-toolkit) provides
  `@Preview` and the generated HTML catalog.
- [Electrostatic](https://github.com/teggr/electrostatic) builds and serves
  the project's documentation site.
- [TeaVM](https://teavm.org/docs/intro/getting-started.html) compiles the
  Java repository explorer to browser JavaScript.

See the [end-to-end tutorial](website/src/main/resources/site/_installation/getting-started.md),
[module architecture](website/src/main/resources/site/_guides/architecture.md),
and [ecosystem quick starts](website/src/main/resources/site/_projects/quick-starts.md).

## GitHub Pages

On pushes to `main`, GitHub Actions builds the site, uploads
`website/target/generated-site`, and deploys it to Pages. A manual trigger is
also available in the Actions tab. Set the repository's Pages source to
**GitHub Actions**. The published site URL is
https://teggr.github.io/rebelcraft/.
