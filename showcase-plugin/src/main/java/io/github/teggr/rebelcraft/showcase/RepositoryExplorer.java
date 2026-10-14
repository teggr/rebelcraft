package io.github.teggr.rebelcraft.showcase;

import j2html.tags.ContainerTag;

import static j2html.TagCreator.*;

public final class RepositoryExplorer {
    private RepositoryExplorer() {
    }

    public static ContainerTag<?> create() {
        return section().withId("repository-explorer").withClasses("explorer-panel").with(
                div().withClasses("explorer-panel__heading").with(
                        span("LIVE FROM GITHUB").withClasses("eyebrow"),
                        h2("Explore public repositories"),
                        p("Choose a GitHub account to load its latest public repositories.")
                ),
                form().withId("repository-form").withClasses("owner-form").with(
                        label("GitHub owner").withFor("repository-owner"),
                        div().withClasses("owner-form__controls").with(
                                input().withType("text")
                                        .withId("repository-owner")
                                        .withName("owner")
                                        .withValue("teggr")
                                        .withMaxlength("39")
                                        .attr("autocomplete", "off")
                                        .attr("required", ""),
                                button("Load repositories").withType("submit").withId("load-repositories")
                        ),
                        p("Showing up to 100 public repositories, ordered by recently updated.")
                                .withClasses("helper-copy")
                ),
                div().withId("repository-status").withClasses("status-message")
                        .attr("role", "status")
                        .attr("aria-live", "polite")
                        .withText("Enter an account name to load repositories."),
                div().withId("repository-grid").withClasses("repository-grid")
                        .attr("aria-live", "polite")
        );
    }
}
