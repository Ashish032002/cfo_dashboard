package com.cams.core.rulesui.config;

import com.cams.core.rulesui.scm.ScmClient;
import com.cams.core.rulesui.scm.cache.InMemoryScmCache;
import com.cams.core.rulesui.scm.cache.ScmCache;
import com.cams.core.rulesui.scm.dto.ScmFile;
import com.cams.core.rulesui.scm.gitlab.GitlabScmClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(ScmProperties.class)
public class AppConfig {

    @Bean
    public WebClient scmWebClient(ScmProperties props) {
        return WebClient.builder()
            .baseUrl(props.getBaseUrl())
            .build();
    }

    @Bean
    public ScmClient scmClient(ScmProperties props, WebClient scmWebClient) {
        return switch (props.getProvider()) {
            case GITLAB -> new GitlabScmClient(scmWebClient);
            case GITHUB -> throw new UnsupportedOperationException("GitHub client not implemented yet");
        };
    }

    @Bean
    public ScmCache<ScmFile> scmFileCache() {
        return new InMemoryScmCache<>();
    }
}
