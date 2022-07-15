package com.foundation.common.auth.service.impl;

import com.foundation.common.core.constant.AuthConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

/**
 * OAuth2 客户端信息
 */
@Service
@RequiredArgsConstructor
public class ClientDetailsServiceImpl implements ClientDetailsService {

//    private final OAuthClientFeignClient oAuthClientFeignClient;

    @Override
//    @Cacheable(cacheNames = "auth", key = "'oauth-client:'+#clientId")
    public ClientDetails loadClientByClientId(String clientId) {
        try {
//            Result<ClientAuthDTO> result = oAuthClientFeignClient.getOAuth2ClientById(clientId);
//            if (Result.success().getCode().equals(result.getCode())) {
//                ClientAuthDTO client = result.getData();
//                BaseClientDetails clientDetails = new BaseClientDetails(
//                        client.getClientId(),
//                        client.getResourceIds(),
//                        client.getScope(),
//                        client.getAuthorizedGrantTypes(),
//                        client.getAuthorities(),
//                        client.getWebServerRedirectUri()
//                );
//                clientDetails.setClientSecret(PasswordEncoderTypeEnum.NOOP.getPrefix() + client.getClientSecret());
//                clientDetails.setAccessTokenValiditySeconds(client.getAccessTokenValidity());
//                clientDetails.setRefreshTokenValiditySeconds(client.getRefreshTokenValidity());
//                return clientDetails;
//            } else {
//                throw new NoSuchClientException("No client with requested id: " + clientId);
//            }
            return new BaseClientDetails(
                    AuthConstant.TEST_CLIENT_ID,"","all","authorization_code,password,refresh_token", "", "http://localhost:8081/login");
        } catch (EmptyResultDataAccessException var4) {
            throw new NoSuchClientException("No client with requested id: " + clientId);
        }
    }
}
