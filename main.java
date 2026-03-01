package com.cams.core.rulesui.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "scm")
public class ScmProperties {

    public enum Provider { GITLAB, GITHUB }

    private Provider provider = Provider.GITLAB;

    /** e.g. https://gitlab.company.com */
    private String baseUrl;

    /** e.g. group/subgroup/repo-core-backend OR numeric project id */
    private String project;

    /** e.g. internal_develop */
    private String defaultBranch = "internal_develop";

    /** e.g. order-acceptance-service/src/main/resources/working-payload */
    private String drlBasePath;

    public Provider getProvider() { return provider; }
    public void setProvider(Provider provider) { this.provider = provider; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getProject() { return project; }
    public void setProject(String project) { this.project = project; }

    public String getDefaultBranch() { return defaultBranch; }
    public void setDefaultBranch(String defaultBranch) { this.defaultBranch = defaultBranch; }

    public String getDrlBasePath() { return drlBasePath; }
    public void setDrlBasePath(String drlBasePath) { this.drlBasePath = drlBasePath; }
}
