package com.cams.core.rulesui.scm.gitlab;

import com.cams.core.rulesui.scm.ScmClient;
import com.cams.core.rulesui.scm.dto.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class GitlabScmClient implements ScmClient {

    private final WebClient webClient;

    public GitlabScmClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public ScmFile getFile(FileRef ref) {
        String encodedProject = UriUtils.encodePathSegment(ref.repo().project(), StandardCharsets.UTF_8);
        String encodedFilePath = UriUtils.encodePathSegment(ref.path(), StandardCharsets.UTF_8);

        GitlabFileResponse resp = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/v4/projects/{project}/repository/files/{filePath}")
                .queryParam("ref", ref.branch())
                .build(encodedProject, encodedFilePath)
            )
            .retrieve()
            .bodyToMono(GitlabFileResponse.class)
            .block();

        if (resp == null || resp.content == null) {
            throw new IllegalStateException("Empty response from GitLab for file: " + ref.path());
        }

        String decoded = new String(Base64.getDecoder().decode(resp.content), StandardCharsets.UTF_8);
        String revision = resp.last_commit_id != null ? resp.last_commit_id : resp.blob_id;

        return new ScmFile(ref.path(), decoded, revision);
    }

    @Override
    public List<String> listFiles(RepoRef repo, String branch, String folderPath) {
        String encodedProject = UriUtils.encodePathSegment(repo.project(), StandardCharsets.UTF_8);

        GitlabTreeItem[] items = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/v4/projects/{project}/repository/tree")
                .queryParam("ref", branch)
                .queryParam("path", folderPath)
                .queryParam("recursive", true)
                .build(encodedProject)
            )
            .retrieve()
            .bodyToMono(GitlabTreeItem[].class)
            .block();

        if (items == null) return List.of();

        return Arrays.stream(items)
            .filter(i -> "blob".equalsIgnoreCase(i.type))
            .map(i -> i.path)
            .filter(p -> p.toLowerCase().endsWith(".drl"))
            .toList();
    }

    private static class GitlabFileResponse {
        public String content; // base64
        public String blob_id;
        public String last_commit_id;
    }

    private static class GitlabTreeItem {
        public String type; // blob/tree
        public String path;
    }
}
