package io.github.teggr.rebelcraft.showcase;

import j2html.tags.DomContent;

import static j2html.TagCreator.*;

public final class ShowcaseLayout {
    private ShowcaseLayout() {
    }

    public static DomContent page(String title, DomContent content, String stylesheet, String scriptPath) {
        return page(title, content, stylesheet, scriptPath, "../");
    }

    public static DomContent page(
            String title,
            DomContent content,
            String stylesheet,
            String scriptPath,
            String homePath
    ) {
        var pageHead = head().with(
                meta().withCharset("utf-8"),
                meta().withName("viewport").withContent("width=device-width, initial-scale=1"),
                title(title)
        );
        if (stylesheet != null && !stylesheet.isBlank()) {
            pageHead.with(link().withRel("stylesheet").withHref(stylesheet));
        }

        var pageBody = body().with(
                header().withClasses("site-header").with(
                        a("RebelCraft").withHref(homePath),
                        nav().with(
                                a("Getting started").withHref(homePath + "installation/getting-started.html"),
                                a("Guides").withHref(homePath + "guides/architecture.html"),
                                a("Projects").withHref(homePath + "projects/quick-starts.html")
                        )
                ),
                main().with(content),
                footer().with(
                        p("Built with j2html, Electrostatic, and TeaVM."),
                        a("Project documentation").withHref(homePath + "installation/getting-started.html")
                )
        );
        if (scriptPath != null && !scriptPath.isBlank()) {
            pageBody.with(script().withSrc(scriptPath), script("main();"));
        }
        return html().withLang("en").with(pageHead, pageBody);
    }
}
