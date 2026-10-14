package io.github.teggr.rebelcraft.showcase;

import java.util.ArrayList;
import java.util.List;

import org.teavm.jso.browser.Window;
import org.teavm.jso.core.JSArrayReader;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.html.HTMLAnchorElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.html.HTMLInputElement;
import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.json.JSON;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public final class RepositoryExplorerApp {
    private static final String API_BASE = "https://api.github.com/users/";
    private static final int MAX_REPOSITORIES = 100;
    private static final int CARD_DELAY_MILLIS = 22;

    private static XMLHttpRequest activeRequest;
    private static int renderGeneration;

    private RepositoryExplorerApp() {
    }

    public static void main(String[] args) {
        HTMLDocument document = HTMLDocument.current();
        HTMLElement form = document.getElementById("repository-form");
        HTMLElement ownerField = document.getElementById("repository-owner");
        HTMLElement status = document.getElementById("repository-status");
        HTMLElement results = document.getElementById("repository-grid");
        if (form == null || ownerField == null || status == null || results == null) {
            return;
        }

        HTMLInputElement ownerInput = (HTMLInputElement) ownerField;
        form.addEventListener("submit", event -> {
            event.preventDefault();
            loadRepositories(ownerInput, status, results);
        });
        loadRepositories(ownerInput, status, results);
    }

    private static void loadRepositories(HTMLInputElement ownerInput, HTMLElement status, HTMLElement results) {
        String owner = ownerInput.getValue() == null ? "" : ownerInput.getValue().trim();
        if (!isValidOwner(owner)) {
            ownerInput.setCustomValidity("Enter a GitHub account name using letters, numbers, or hyphens.");
            ownerInput.reportValidity();
            status.setTextContent("Enter a valid GitHub owner name to continue.");
            return;
        }
        ownerInput.setCustomValidity("");

        if (activeRequest != null) {
            activeRequest.abort();
        }
        int generation = ++renderGeneration;
        results.clear();
        setStatus(status, "loading", "Loading public repositories for " + owner + "…");

        XMLHttpRequest request = new XMLHttpRequest();
        activeRequest = request;
        request.open(
                "GET",
                API_BASE + Window.encodeURIComponent(owner) + "/repos?per_page=" + MAX_REPOSITORIES + "&sort=updated"
        );
        request.setRequestHeader("Accept", "application/vnd.github+json");
        request.setRequestHeader("X-GitHub-Api-Version", "2022-11-28");
        request.onLoad(event -> {
            if (activeRequest != request) {
                return;
            }
            if (request.getStatus() < 200 || request.getStatus() >= 300) {
                showRequestError(request, owner, status);
                return;
            }
            try {
                @SuppressWarnings("unchecked")
                JSArrayReader<RepositoryData> response =
                        (JSArrayReader<RepositoryData>) JSON.parse(request.getResponseText());
                List<RepositoryData> repositories = new ArrayList<>();
                for (int index = 0; index < response.getLength(); index++) {
                    repositories.add(response.get(index));
                }
                if (repositories.isEmpty()) {
                    setStatus(status, "empty", "No public repositories were found for " + owner + ".");
                    return;
                }
                setStatus(status, "loading", "Loading " + repositories.size() + " repositories…");
                appendRepository(repositories, 0, generation, status, results);
            } catch (RuntimeException exception) {
                setStatus(status, "error", "GitHub returned a response that could not be read. Try again later.");
            }
        });
        request.onError(event -> {
            if (activeRequest == request) {
                setStatus(status, "error", "Could not connect to GitHub. Check your connection and try again.");
            }
        });
        request.send();
    }

    private static boolean isValidOwner(String owner) {
        if (owner.isEmpty() || owner.length() > 39 || owner.charAt(0) == '-' || owner.charAt(owner.length() - 1) == '-') {
            return false;
        }
        for (int index = 0; index < owner.length(); index++) {
            char character = owner.charAt(index);
            if (!isAsciiLetterOrDigit(character) && character != '-') {
                return false;
            }
        }
        return true;
    }

    private static boolean isAsciiLetterOrDigit(char character) {
        return (character >= 'a' && character <= 'z')
                || (character >= 'A' && character <= 'Z')
                || (character >= '0' && character <= '9');
    }

    private static void showRequestError(XMLHttpRequest request, String owner, HTMLElement status) {
        if (request.getStatus() == 404) {
            setStatus(status, "error", "No public GitHub account named " + owner + " was found.");
        } else if (request.getStatus() == 403 || request.getStatus() == 429) {
            setStatus(status, "error", "GitHub's public API rate limit was reached. Please try again later.");
        } else {
            setStatus(status, "error", "GitHub could not load these repositories (HTTP " + request.getStatus() + ").");
        }
    }

    private static void appendRepository(
            List<RepositoryData> repositories,
            int index,
            int generation,
            HTMLElement status,
            HTMLElement results
    ) {
        if (generation != renderGeneration) {
            return;
        }
        if (index >= repositories.size()) {
            setStatus(status, "success", "Loaded " + repositories.size() + " public repositories.");
            return;
        }
        results.appendChild(createRepositoryCard(repositories.get(index)));
        Window.setTimeout(
                () -> appendRepository(repositories, index + 1, generation, status, results),
                CARD_DELAY_MILLIS
        );
    }

    private static HTMLElement createRepositoryCard(RepositoryData repository) {
        HTMLDocument document = HTMLDocument.current();
        HTMLElement card = document.createElement("article");
        card.setClassName("repository-card");

        HTMLElement title = document.createElement("h3");
        String name = repository.getName() == null ? "Untitled repository" : repository.getName();
        String url = repository.getHtmlUrl();
        if (url != null && url.startsWith("https://github.com/")) {
            HTMLAnchorElement link = (HTMLAnchorElement) document.createElement("a");
            link.setHref(url);
            link.setTextContent(name);
            link.setTarget("_blank");
            link.setRel("noopener noreferrer");
            title.appendChild(link);
        } else {
            title.setTextContent(name);
        }
        card.appendChild(title);

        HTMLElement description = document.createElement("p");
        String descriptionText = repository.getDescription();
        description.setTextContent(
                descriptionText == null || descriptionText.isEmpty()
                        ? "No description provided."
                        : descriptionText
        );
        card.appendChild(description);

        HTMLElement meta = document.createElement("div");
        meta.setClassName("repository-card__meta");
        String language = repository.getLanguage();
        if (language != null && !language.isEmpty()) {
            appendMeta(meta, "language", language);
        }
        appendMeta(meta, "stars", repository.getStargazersCount() + " stars");
        if (repository.isFork()) {
            appendMeta(meta, "fork", "Fork");
        }
        card.appendChild(meta);
        return card;
    }

    private static void appendMeta(HTMLElement parent, String className, String value) {
        HTMLElement item = HTMLDocument.current().createElement("span");
        item.setClassName("repository-card__" + className);
        item.setTextContent(value);
        parent.appendChild(item);
    }

    private static void setStatus(HTMLElement status, String state, String message) {
        status.setClassName("status-message status-message--" + state);
        status.setTextContent(message);
    }

    private interface RepositoryData extends JSObject {
        @JSProperty("name")
        String getName();

        @JSProperty("description")
        String getDescription();

        @JSProperty("html_url")
        String getHtmlUrl();

        @JSProperty("language")
        String getLanguage();

        @JSProperty("stargazers_count")
        int getStargazersCount();

        @JSProperty("fork")
        boolean isFork();
    }
}
