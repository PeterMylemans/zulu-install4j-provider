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

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.azul.com/zulu/download/community/v1.0/bundles/?bundle_type=jdk&release_status=ga&javafx=false"))
                    .build();
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                final JSONArray jsonArray = new JSONArray(response.body());
                for (Object o : jsonArray) {
                    if (o instanceof JSONObject) {
                        final JSONArray zuluVersionArray = ((JSONObject) o).getJSONArray("zulu_version");
                        final JSONArray jdkVersionArray = ((JSONObject) o).getJSONArray("jdk_version");
                        final ZuluCommunityReleaseNode jdkReleaseNode = releaseNodeMap.computeIfAbsent(new ZuluVersion(zuluVersionArray, jdkVersionArray), ZuluCommunityReleaseNode::new);
                        jdkReleaseNode.addPlatform(((JSONObject) o).getString("url"));
                    }
                }
            } else {
                throw new IllegalStateException("Got a status code " + response.statusCode() + " for " + request.uri().toString());
            }
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Could not parse the output from the API", e);
        }

        releaseNodeMap.entrySet().removeIf(entry -> entry.getValue().getPlatforms().isEmpty());

        final List<JdkCategoryNode> result = new ArrayList<>();
        ZuluCommunityCategory currentCategory = null;
        for (ZuluCommunityReleaseNode releaseNode : releaseNodeMap.values()) {
            if (currentCategory == null || releaseNode.getVersion().getMajor() != currentCategory.getMajorVersion()) {
                currentCategory = new ZuluCommunityCategory(releaseNode.getVersion().getMajor());
                result.add(currentCategory);
            }

            currentCategory.getChildren().add(releaseNode);
        }

        return result;
    }

    @Override
    public JdkReleaseNode getByConfigKey(String configKey) {
        final ZuluCommunityReleaseNode jdkReleaseNode = new ZuluCommunityReleaseNode(new ZuluVersion(configKey));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.azul.com/zulu/download/community/v1.0/bundles/?bundle_type=jdk&release_status=ga&javafx=false&zulu_version=" + jdkReleaseNode.getVersion().getZuluVersion()))
                .build();

        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                final JSONArray jsonArray = new JSONArray(response.body());
                for (Object o : jsonArray) {
                    if (o instanceof JSONObject) {
                        jdkReleaseNode.addPlatform(((JSONObject) o).getString("url"));
                    }
                }
            } else {
                throw new IllegalStateException("Got a status code " + response.statusCode() + " for " + request.uri().toString());
            }
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Could not parse the output from the API", e);
        }

        return jdkReleaseNode;
    }

    @Override
    public File removeArchivePrefix(File relativeFile, String release, String platform) {
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
