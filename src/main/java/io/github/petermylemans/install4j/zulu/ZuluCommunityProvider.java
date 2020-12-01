package io.github.petermylemans.install4j.zulu;

import com.install4j.jdk.spi.JdkCategoryNode;
import com.install4j.jdk.spi.JdkNode;
import com.install4j.jdk.spi.JdkProvider;
import com.install4j.jdk.spi.JdkReleaseNode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class ZuluCommunityProvider implements JdkProvider {

    @Override
    public String getId() {
        return "Azul Zulu Community";
    }

    @Override
    public Collection<? extends JdkNode> getReleases() {
        final Map<ZuluVersion, ZuluCommunityReleaseNode> releaseNodeMap = new TreeMap<>(Comparator.reverseOrder());

        final JSONArray jsonArray = getReleaseInfoFromApi();
        for (final Object o : jsonArray) {
            if (o instanceof JSONObject) {
                final JSONArray zuluVersionArray = ((JSONObject) o).getJSONArray("zulu_version");
                final JSONArray jdkVersionArray = ((JSONObject) o).getJSONArray("jdk_version");
                final String url = ((JSONObject) o).getString("url");

                final ZuluVersion zuluVersion = new ZuluVersion(zuluVersionArray);
                final ZuluCommunityReleaseNode jdkReleaseNode = releaseNodeMap.computeIfAbsent(zuluVersion,
                    ZuluCommunityReleaseNode::new);

                jdkReleaseNode.setOpenJDKVersion(new OpenJDKVersion(jdkVersionArray));
                jdkReleaseNode.addPlatform(url);
            }
        }

        return createCategoryTree(releaseNodeMap);
    }

    private JSONArray getReleaseInfoFromApi() {
        try {
            final HttpClient client = HttpClient.newHttpClient();
            final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.azul.com/zulu/download/community/v1.0/bundles/" +
                    "?bundle_type=jdk&release_status=ga&javafx=false"))
                .build();
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return new JSONArray(response.body());
            } else {
                final String uri = request.uri().toString();
                throw new IllegalStateException("Got a status code " + response.statusCode() + " for " + uri);
            }
        } catch (final IOException | InterruptedException e) {
            throw new IllegalStateException("Could not parse the output from the API", e);
        }
    }

    /**
     * Creates a category tree with major zulu versions as nodes (e.g. zulu11) and actual versions as the leaves.
     */
    private List<JdkCategoryNode> createCategoryTree(final Map<ZuluVersion, ZuluCommunityReleaseNode> releaseNodeMap) {
        final List<JdkCategoryNode> result = new ArrayList<>();
        ZuluCommunityCategory currentCategory = null;
        for (final ZuluCommunityReleaseNode releaseNode : releaseNodeMap.values()) {
            if (releaseNode.getPlatforms().isEmpty()) {
                continue;
            }

            if (currentCategory == null || releaseNode.getVersion().getMajor() != currentCategory.getMajorVersion()) {
                currentCategory = new ZuluCommunityCategory(releaseNode.getVersion().getMajor());
                result.add(currentCategory);
            }

            currentCategory.getChildren().add(releaseNode);
        }

        return result;
    }

    @Override
    public JdkReleaseNode getByConfigKey(final String configKey) {
        final ZuluCommunityReleaseNode jdkReleaseNode = new ZuluCommunityReleaseNode(new ZuluVersion(configKey));
        final ZuluVersion zuluVersion = jdkReleaseNode.getVersion();

        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(
                "https://api.azul.com/zulu/download/community/v1.0/bundles/" +
                    "?bundle_type=jdk&release_status=ga&javafx=false&zulu_version=" + zuluVersion.asExactString()))
            .build();

        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                final JSONArray jsonArray = new JSONArray(response.body());
                for (final Object o : jsonArray) {
                    if (o instanceof JSONObject) {
                        final JSONArray jdkVersionArray = ((JSONObject) o).getJSONArray("jdk_version");
                        final String url = ((JSONObject) o).getString("url");

                        jdkReleaseNode.setOpenJDKVersion(new OpenJDKVersion(jdkVersionArray));
                        jdkReleaseNode.addPlatform(url);
                    }
                }
            } else {
                final String uri = request.uri().toString();
                throw new IllegalStateException("Got a status code " + response.statusCode() + " for " + uri);
            }
        } catch (final IOException | InterruptedException e) {
            throw new IllegalStateException("Could not parse the output from the API", e);
        }

        return jdkReleaseNode;
    }

    /**
     * Removes first directory as the archive prefix.
     * <p>
     * On some platforms (such as MacOS) an additional zuluXX directory is added, which also needs to be skipped.
     */
    @Override
    public File removeArchivePrefix(final File relativeFile, final String release, final String platform) {
        final String path = relativeFile.getPath().replace(File.separatorChar, '/');
        final int firstDir = path.indexOf('/');
        if (firstDir < 0) {
            return new File("");
        } else {
            final String newPath = path.substring(firstDir + 1);
            if (newPath.startsWith("zulu")) {
                return removeArchivePrefix(new File(newPath), release, platform);
            } else {
                return new File(newPath);
            }
        }
    }

}
