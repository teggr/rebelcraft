package io.github.teggr.rebelcraft.website;

import io.github.teggr.rebelcraft.showcase.ShowcasePages;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class SiteAssetGenerator {
    private SiteAssetGenerator() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Expected the Electrostatic source directory and HTML catalog directory.");
        }

        Path siteSource = Path.of(args[0]);
        Path catalogSource = Path.of(args[1]);
        Path staticDirectory = siteSource.resolve("_static");
        Path landingPage = staticDirectory.resolve("index.html");
        Files.createDirectories(landingPage.getParent());
        Files.writeString(landingPage, new ShowcasePages().landingPage().render());

        Path repositoryPage = staticDirectory.resolve("repos/index.html");
        Files.createDirectories(repositoryPage.getParent());
        Files.writeString(repositoryPage, new ShowcasePages().repositoryExplorerPage().render());

        copyResource("/site-assets/showcase.css", staticDirectory.resolve("showcase.css"));
        copyResource("/site-assets/repository-explorer.js", staticDirectory.resolve("repository-explorer.js"));
        copyCatalog(catalogSource, staticDirectory.resolve("catalog"));
    }

    private static void copyResource(String resourcePath, Path destination) throws IOException {
        try (InputStream resource = SiteAssetGenerator.class.getResourceAsStream(resourcePath)) {
            if (resource == null) {
                throw new IOException("Required showcase asset is missing from the plugin module: " + resourcePath);
            }
            Files.copy(resource, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void copyCatalog(Path source, Path destination) throws IOException {
        Path index = source.resolve("index.html");
        if (Files.notExists(index)) {
            throw new IOException("The HTML catalog was not generated at " + source);
        }
        try (var files = Files.walk(source)) {
            for (Path file : files.filter(Files::isRegularFile).toList()) {
                Path target = destination.resolve(source.relativize(file));
                Files.createDirectories(target.getParent());
                Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}
