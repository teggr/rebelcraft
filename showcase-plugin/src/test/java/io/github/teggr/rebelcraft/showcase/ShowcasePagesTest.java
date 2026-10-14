package io.github.teggr.rebelcraft.showcase;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShowcasePagesTest {
    @Test
    void explorerPageIsComposedFromAccessibleJ2htmlComponents() {
        String page = new ShowcasePages().repositoryExplorerPage().render();

        assertTrue(page.contains("id=\"repository-owner\""));
        assertTrue(page.contains("id=\"repository-grid\""));
        assertTrue(page.contains("aria-live=\"polite\""));
        assertTrue(page.contains("../repository-explorer.js"));
        assertTrue(page.contains("main();"));
        assertTrue(page.contains("https://github.com/tipsy/j2html"));
        assertTrue(page.contains("j2html-toolkit"));
        assertTrue(page.contains("TeaVM"));
        assertTrue(page.contains("https://teavm.org/docs/intro/getting-started.html"));
    }

    @Test
    void landingPageIsTheInteractiveShowcaseWithWebsiteNavigation() {
        String page = new ShowcasePages().landingPage().render();

        assertTrue(page.contains("id=\"repository-owner\""));
        assertTrue(page.contains("id=\"repository-grid\""));
        assertTrue(page.contains("src=\"repository-explorer.js\""));
        assertTrue(page.contains("href=\"./catalog/\""));
        assertTrue(page.contains("href=\"./installation/getting-started.html\""));
        assertTrue(page.contains("href=\"./projects/quick-starts.html\""));
        assertTrue(page.contains("href=\"./guides/architecture.html\""));
    }

    @Test
    void layoutPreviewDoesNotRequireTheProductionSiteAssets() {
        String preview = new ShowcasePages().pageLayout();

        assertFalse(preview.contains("<script"));
        assertFalse(preview.contains("href=\"null\""));
    }
}
