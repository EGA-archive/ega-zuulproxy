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

/**
 * @author asenf
 */
public class MyServerSettings {
    private final String internal;
    private final String url;
    private final String token;
    private final String elixirUrl;
    private final String elixirIntrospect;
    private final String elixirClient;
    private final String elixirSecret;
    private final String basicUser;
    private final String basicPass;

    public MyServerSettings(String internal,
                            String url,
                            String token,
                            String elixirUrl,
                            String elixirIntrospect,
                            String elixirClient,
                            String elixirSecret,
                            String basicUser,
                            String basicPass) {
        this.internal = internal;
        this.url = url;
        this.token = token;
        this.elixirUrl = elixirUrl;
        this.elixirIntrospect = elixirIntrospect;
        this.elixirClient = elixirClient;
        this.elixirSecret = elixirSecret;
        this.basicUser = basicUser;
        this.basicPass = basicPass;
    }

    public boolean isInternal() {
        return this.internal.equalsIgnoreCase("true");
    }

    public String getUrl() {
        return this.url;
    }

    public String getToken() {
        return this.token;
    }

    public String getElixirUrl() {
        return this.elixirUrl;
    }

    public String getElixirIntrospect() {
        return this.elixirIntrospect;
    }

    public String getElixirClient() {
        return this.elixirClient;
    }

    public String getElixirSecret() {
        return this.elixirSecret;
    }

    public String getBasicUser() {
        return this.basicUser;
    }

    public String getBasicPass() {
        return this.basicPass;
    }
}
