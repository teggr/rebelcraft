package io.github.teggr.rebelcraft.showcase;

import j2html.tags.ContainerTag;

import static j2html.TagCreator.*;

public final class ProjectCard {
    private ProjectCard() {
    }

    public static ContainerTag<?> create(String name, String description, String url, String version) {
        return article().withClasses("project-card").with(
                div().withClasses("project-card__top").with(
                        h3(name),
                        span(version).withClasses("project-card__version")
                ),
                p(description),
                a("Explore project").withHref(url).withClasses("project-card__link")
        );
    }
}
