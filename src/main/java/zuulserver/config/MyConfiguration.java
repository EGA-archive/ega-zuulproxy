/*
 * Copyright 2017 ELIXIR EGA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package zuulserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author asenf
 */
@Configuration
public class MyConfiguration {
    @Value("${ega.server.internal}") String configInternal;
    @Value("${ega.server.url}") String configUrl;
    @Value("${ega.server.token}") String configToken;
    @Value("${spring.oauth2.resource.userInfoUri}") String elixirUserInfo;
    @Value("${manual.basic.user}") String basicUser;
    @Value("${manual.basic.password}") String basicPass;

    @Bean
    public MyServerSettings MyServerSettings() {
        return new MyServerSettings(configInternal, configUrl, configToken, elixirUserInfo, basicUser, basicPass);
    }
}
