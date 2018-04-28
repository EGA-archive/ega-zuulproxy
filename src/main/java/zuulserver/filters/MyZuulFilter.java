/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zuulserver.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import okhttp3.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UrlPathHelper;
import zuulserver.config.MyServerSettings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import zuulserver.dto.Permission;
import zuulserver.dto.Permissions;

/**
 * @author asenf
 */
@Component
public class MyZuulFilter extends ZuulFilter {

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Autowired
    private MyServerSettings serverSettings;

    private static String getBasicEncoded(String user, String password) {
        String authString = user + ":" + password;
        System.out.println("auth string: " + authString);
        byte[] authEncBytes = Base64.encode(authString.getBytes());
        String authStringEnc = new String(authEncBytes);
        System.out.println("Base64 encoded auth string: " + authStringEnc);
        return authStringEnc;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        final String requestURI = this.urlPathHelper.getPathWithinApplication(ctx.getRequest());
        if (!(requestURI.contains("data") ||
                requestURI.contains("central"))) {
            throw new NullPointerException();

        }

        String access_token = "";
        try {
            access_token = StringUtils.substringAfterLast((String) ctx.getRequest().getHeader("Authorization"), "Bearer ");
        } catch (Throwable th) {
        }

        // Only perform this for ELIXIR tokens - pass EGA tokens straight to the API
        boolean isElixirToken = determineTokenOrigin(access_token);
        if (isElixirToken) {
            String userElixirSub = null;
            try {
                userElixirSub = getElixirSub(access_token);
            } catch (InvalidTokenException ex) {
                ctx.setResponseBody(ex.toString());
                System.out.println("X: " + ex.toString());
                return null;
            }

System.out.println("Elixir Sub: " + userElixirSub);
            /*
             * Old Code
             */
            // Contact EGA for Datasets & Elixir User
    //        List<String> permissions = getElixirPermissions(userElixirSub);
    //        String permissions_ = "";
    //        for (int i = 0; i < permissions.size(); i++) { // Neet to test: Is this always 1 String now??
    //            permissions_ += permissions.get(i);
    //            if (i < permissions.size() - 1)
    //                permissions_ += ","; // New - is a Base64 encoded, signed String from EGA
    //        }
    //        ctx.addZuulRequestHeader("X-Permissions", permissions_);
            
            /*
             * New Code
             */
            Permission p = getElixirPermissionsNew(userElixirSub);
            ctx.addZuulRequestHeader("X-Permissions", p.getSignatureString());
System.out.println("Signature String: " + p.getSignatureString());
            
        } else {
        }

        return null;
    }
/*
    private String getElixirUserEmail(String accessToken) {
        String url = serverSettings.getElixirUrl();
        UserInfoTokenServices tokenService = new UserInfoTokenServices(url, "");
        OAuth2Authentication auth = tokenService.loadAuthentication(accessToken);
        LinkedHashMap<String, String> details = (LinkedHashMap<String, String>) auth.getUserAuthentication().getDetails();
        String email = details.get("email");

        return email;
    }

    private String getElixirUserId(String accessToken) {
        String url = serverSettings.getElixirUrl();
        UserInfoTokenServices tokenService = new UserInfoTokenServices(url, "");
        OAuth2Authentication auth = tokenService.loadAuthentication(accessToken);
        LinkedHashMap<String, String> details = (LinkedHashMap<String, String>) auth.getUserAuthentication().getDetails();
        String id = details.get("id");

        return id;
    }
*/
    private String getElixirSub(String accessToken) {
        String url = serverSettings.getElixirUrl();
        UserInfoTokenServices tokenService = new UserInfoTokenServices(url, serverSettings.getElixirClient());
        OAuth2Authentication auth = tokenService.loadAuthentication(accessToken);
        LinkedHashMap<String, String> details = (LinkedHashMap<String, String>) auth.getUserAuthentication().getDetails();
        String sub = details.get("sub");

        return sub;
    }

    private List<String> getElixirPermissions(String user_id) {
        String baseApi = serverSettings.isInternal() ? getInternalUrl() : serverSettings.getElixirUrl(); // MUST BE CHANGED TO "https://ega.ebi.ac.uk/elixir/central/.." IN LOCAL (NON-EBI) INSTALLATIONS
        String accessToken = serverSettings.getToken();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = builder
                .addNetworkInterceptor(new ResponseCacheInterceptor()) // Enable response caching
                .cache(new Cache(new File("apiResponses"), 5 * 1024 * 1024)) // Set the cache location and size (5 MB)
                .build();

        // List all Datasets
        Request datasetRequest = new Request.Builder()
                .url(baseApi + "/app/elixir/" + user_id + "/datasets")
                //.addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Authorization", "Basic " + getBasicEncoded(serverSettings.getBasicUser(), serverSettings.getBasicPass()))
                .build();
        Moshi MOSHI = new Moshi.Builder().build();
        JsonAdapter<List<String>> STRING_JSON_ADAPTER =
                MOSHI.adapter(Types.newParameterizedType(List.class, String.class));

        List<String> result = new ArrayList<String>(); // Prevent result from bein Null
        try {
            // Execute the request and retrieve the response.
            Response response = null;
            int tryCount = 9;
            while (tryCount-- > 0 && (response == null || !response.isSuccessful())) {
                try {
                    response = okHttpClient.newCall(datasetRequest).execute();
                } catch (Exception ex) {
                }
            }
            ResponseBody body = response.body();
            result = STRING_JSON_ADAPTER.fromJson(body.source());
            body.close();
            System.out.println(result.size() + " Datasets.");

        } catch (IOException ex) {
            System.out.println("Error getting Datasets: " + ex.toString());
        }

        return result;
    }

    private Permission getElixirPermissionsNew(String user_id) {
        String baseApi = serverSettings.isInternal() ? getInternalUrl() : serverSettings.getElixirUrl(); // MUST BE CHANGED TO "https://ega.ebi.ac.uk/elixir/central/.." IN LOCAL (NON-EBI) INSTALLATIONS
        String accessToken = serverSettings.getToken();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = builder
                .addNetworkInterceptor(new ResponseCacheInterceptor()) // Enable response caching
                .cache(new Cache(new File("apiResponses"), 5 * 1024 * 1024)) // Set the cache location and size (5 MB)
                .build();

        // List all Datasets
        Request datasetRequest = new Request.Builder()
                .url(baseApi + "/app/user/" + user_id + "/")
                .addHeader("Authorization", "Basic " + getBasicEncoded(serverSettings.getBasicUser(), serverSettings.getBasicPass()))
                .build();
        Moshi MOSHI = new Moshi.Builder().build();
        JsonAdapter<Permissions> STRING_JSON_ADAPTER = MOSHI.adapter(Permissions.class);

        Permissions result = null;
        try {
            // Execute the request and retrieve the response.
            Response response = null;
            int tryCount = 3;
            while (tryCount-- > 0 && (response == null || !response.isSuccessful())) {
                try {
System.out.println(" --- " + datasetRequest.toString());
                    response = okHttpClient.newCall(datasetRequest).execute();
                } catch (Exception ex) {
System.out.println("ERROR " + ex.toString());
                }
            }
            if (response.code() != 200) {
                throw new IOException(response.toString());
            }
            
            ResponseBody body = response.body();
            result = STRING_JSON_ADAPTER.fromJson(body.source());
            body.close();

        } catch (IOException ex) {
            System.out.println("Error getting Datasets: " + ex.toString());
        }

        return result.getPermissions()[0];
    }
    
    // Returns 'true' if token originated from ELIXIR
    private boolean determineTokenOrigin(String access_token) {
        if (access_token == null || access_token.length() == 0)
            return false;

        Jwt decoded_token = JwtHelper.decode(access_token);
        String claims = decoded_token.getClaims();
        return (claims.contains(".elixir-czech."));
    }

    // Returns URL for CENTRAL (Internal Only)
    private String getInternalUrl() {
        String[] baseApi = {"http://pg-ega-pro-06.ebi.ac.uk:9153",
                "http://pg-ega-pro-07.ebi.ac.uk:9153",
                "http://pg-ega-pro-08.ebi.ac.uk:9153"};

        Random r = new Random();
        int index = r.nextInt(3);
        return baseApi[index];
    }

    /**
     * Interceptor to cache data and maintain it for 10 minutes.
     * <p>
     * If the same network request is sent within a minute,
     * the response is retrieved from cache.
     */
    private static class ResponseCacheInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            okhttp3.Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, max-age=" + 600)
                    .build();
        }
    }
}
