package com.cams.core.rulesui.drl;

import com.cams.core.rulesui.config.ScmProperties;
import com.cams.core.rulesui.scm.ScmClient;
import com.cams.core.rulesui.scm.cache.ScmCache;
import com.cams.core.rulesui.scm.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DrlService {

    private final ScmClient scmClient;
    private final ScmCache<ScmFile> cache;
    private final ScmProperties props;

    public DrlService(ScmClient scmClient, ScmCache<ScmFile> cache, ScmProperties props) {
        this.scmClient = scmClient;
        this.cache = cache;
        this.props = props;
    }

    public List<String> listDrlFiles() {
        RepoRef repo = new RepoRef(props.getBaseUrl(), props.getProject());
        return scmClient.listFiles(repo, props.getDefaultBranch(), props.getDrlBasePath());
    }

    public ScmFile getDrlFileByName(String fileName) {
        String fullPath = joinPath(props.getDrlBasePath(), sanitizeFileName(fileName));
        return getDrlFileByPath(fullPath);
    }

    private ScmFile getDrlFileByPath(String fullPath) {
        RepoRef repo = new RepoRef(props.getBaseUrl(), props.getProject());
        String branch = props.getDefaultBranch();

        String normalized = normalize(fullPath);
        String cacheKey = repo.project() + "|" + branch + "|" + normalized;

        return cache.get(cacheKey).orElseGet(() -> {
            ScmFile file = scmClient.getFile(new FileRef(repo, branch, normalized));
            cache.put(cacheKey, file);
            return file;
        });
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null) throw new IllegalArgumentException("name is required");
        fileName = fileName.trim().replace("\\", "/");
        if (fileName.contains("/")) {
            fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
        }
        if (!fileName.toLowerCase().endsWith(".drl")) {
            throw new IllegalArgumentException("Only .drl files are supported");
        }
        return fileName;
    }

    private String normalize(String p) {
        p = (p == null ? "" : p).trim().replace("\\", "/");
        while (p.startsWith("/")) p = p.substring(1);
        return p;
    }

    private String joinPath(String a, String b) {
        if (a.endsWith("/")) a = a.substring(0, a.length() - 1);
        if (b.startsWith("/")) b = b.substring(1);
        return a + "/" + b;
    }
}
