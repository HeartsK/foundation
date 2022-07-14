//package com.foundation.common.knife4j.config;
//
//import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
//import com.google.common.collect.Lists;
//import io.swagger.annotations.Api;
//import org.springframework.boot.autoconfigure.AutoConfiguration;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.OAuthBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.oas.annotations.EnableOpenApi;
//import springfox.documentation.service.*;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spi.service.contexts.SecurityContext;
//import springfox.documentation.spring.web.plugins.Docket;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * oauth2的knife4j的配置
// * @date 2022-07-13 16:21
// */
//@EnableOpenApi
//@EnableKnife4j
//@ConditionalOnProperty(name = "knife4j.enabled", matchIfMissing = true)
//@AutoConfiguration
//@EnableWebMvc
//public class Knife4jOauth2Config {
//
//    @Resource
//    private Knife4jProperties knife4jProperties;
//
//    @Bean
//    public Docket restApi() {
//        //schema
//        List<GrantType> grantTypes = new ArrayList<>();
//        //密码模式
//        ResourceOwnerPasswordCredentialsGrant resourceOwnerPasswordCredentialsGrant =
//                new ResourceOwnerPasswordCredentialsGrant(knife4jProperties.getOauth2TokenUrl());
//        grantTypes.add(resourceOwnerPasswordCredentialsGrant);
//        OAuth oAuth = new OAuthBuilder().name("oauth2")
//                .grantTypes(grantTypes).build();
//        //context
//        //scope方位
//        List<AuthorizationScope> scopes = new ArrayList<>();
//        scopes.add(new AuthorizationScope("read", "read  resources"));
//        scopes.add(new AuthorizationScope("write", "write resources"));
//        scopes.add(new AuthorizationScope("reads", "read all resources"));
//        scopes.add(new AuthorizationScope("writes", "write all resources"));
//
//        SecurityReference securityReference = new SecurityReference("oauth2", scopes.toArray(new AuthorizationScope[]{}));
//        SecurityContext securityContext = SecurityContext.builder().securityReferences(Lists.newArrayList(securityReference)).operationSelector(o -> o.requestMappingPattern().matches("/.*")).build();
//        //schemas
//        List<SecurityScheme> securitySchemes = Lists.newArrayList(oAuth);
//        //securyContext
//        List<SecurityContext> securityContexts = Lists.newArrayList(securityContext);
//        return new Docket(DocumentationType.OAS_30)
//                .select()
//                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
//                .paths(PathSelectors.any())
//                .build()
//                .securityContexts(securityContexts)
//                .securitySchemes(securitySchemes)
//                .apiInfo(apiInfo());
//    }
//
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder().title(knife4jProperties.getTitle())
//                .description(knife4jProperties.getDescription())
//                .termsOfServiceUrl(knife4jProperties.getTermsOfServiceUrl())
//                .contact(new Contact(knife4jProperties.getContact().getName(), knife4jProperties.getContact().getUrl(), knife4jProperties.getContact().getEmail()))
//                .license(knife4jProperties.getLicense())
//                .licenseUrl(knife4jProperties.getLicenseUrl())
//                .version(knife4jProperties.getVersion())
//                .build();
//    }
//}
