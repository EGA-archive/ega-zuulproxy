/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zuulserver.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UrlPathHelper;

/**
 * @author asenf
 */
@Component
public class MyZuulFilter extends ZuulFilter {

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

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
        if (!(requestURI.contains("data") || requestURI.contains("central") || requestURI.contains("tickets"))) {
            throw new NullPointerException();
        }
        return null;
    }
}
