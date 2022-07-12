package com.foundation.common.knife4j.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author
 * @date 2022-06-30 15:42
 */
@EnableOpenApi
@EnableKnife4j
@ConditionalOnProperty(name = "knife4j.enabled", matchIfMissing = true)
@AutoConfiguration
@EnableWebMvc
public class Knife4jConfig extends WebMvcConfigurationSupport {

    /**
     * 默认的排除路径，排除Spring Boot默认的错误处理路径和端点
     */
    private static final List<String> DEFAULT_EXCLUDE_PATH = Arrays.asList("/error", "/actuator/**");

    private static final String BASE_PATH = "/**";

    @Bean
    @ConditionalOnMissingBean
    public Knife4jProperties swaggerProperties() {
        return new Knife4jProperties();
    }

    @Bean
    public Docket docket(Knife4jProperties knife4jProperties){
        // base-path处理
        if (knife4jProperties.getBasePath().isEmpty()) {
            knife4jProperties.getBasePath().add(BASE_PATH);
        }
        // exclude-path处理
        if (knife4jProperties.getExcludePath().isEmpty()) {
            knife4jProperties.getExcludePath().addAll(DEFAULT_EXCLUDE_PATH);
        }
        ApiSelectorBuilder builder = new Docket(DocumentationType.OAS_30).host(knife4jProperties.getHost())
                .apiInfo(apiInfo(knife4jProperties))
                .select()
                .apis(RequestHandlerSelectors.basePackage(knife4jProperties.getBasePackage()));

        knife4jProperties.getBasePath().forEach(p -> builder.paths(PathSelectors.ant(p)));
        knife4jProperties.getExcludePath().forEach(p -> builder.paths(PathSelectors.ant(p).negate()));

        return builder.build().securitySchemes(securitySchemes()).securityContexts(securityContexts()).pathMapping("/");
    }

    /**
     * 安全模式，这里指定token通过Authorization头请求头传递
     */
    private List<SecurityScheme> securitySchemes() {
        List<SecurityScheme> apiKeyList = new ArrayList<SecurityScheme>();
        apiKeyList.add(new ApiKey("Authorization", "Authorization", "header"));
        return apiKeyList;
    }

    /**
     * 安全上下文
     */
    private List<SecurityContext> securityContexts() {
        List<SecurityContext> securityContexts = new ArrayList<>();
        securityContexts.add(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .operationSelector(o -> o.requestMappingPattern().matches("/.*"))
                        .build());
        return securityContexts;
    }

    /**
     * 默认的全局鉴权策略
     *
     * @return
     */
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        List<SecurityReference> securityReferences = new ArrayList<>();
        securityReferences.add(new SecurityReference("Authorization", authorizationScopes));
        return securityReferences;
    }

    private ApiInfo apiInfo(Knife4jProperties knife4jProperties) {
        return new ApiInfoBuilder()
                .title(knife4jProperties.getTitle())
                .description(knife4jProperties.getDescription())
                .license(knife4jProperties.getLicense())
                .licenseUrl(knife4jProperties.getLicenseUrl())
                .termsOfServiceUrl(knife4jProperties.getTermsOfServiceUrl())
                .contact(new Contact(knife4jProperties.getContact().getName(), knife4jProperties.getContact().getUrl(), knife4jProperties.getContact().getEmail()))
                .version(knife4jProperties.getVersion())
                .build();
    }

    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resource/");
//        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resource/webjars");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resource/");
    }

}
