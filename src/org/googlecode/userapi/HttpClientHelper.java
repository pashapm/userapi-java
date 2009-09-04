package org.googlecode.userapi;

import org.apache.http.*;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.*;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class HttpClientHelper {

    public static void setAcceptAllCookies(AbstractHttpClient httpClient) {
        CookieSpecFactory acceptAllFactory = new CookieSpecFactory() {
            public CookieSpec newInstance(HttpParams params) {
                return new BrowserCompatSpec() {
                    public void validate(Cookie cookie, CookieOrigin origin)
                            throws MalformedCookieException {
//                        all cookies are accepted
                    }
                };
            }
        };
        httpClient.getCookieSpecs().register("accept_all", acceptAllFactory);
        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, "accept_all");
    }

    public static void addGzipCompression(AbstractHttpClient httpClient) {
        httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
            public void process(
                    final HttpRequest request,
                    final HttpContext context) throws HttpException, IOException {
                if (!request.containsHeader("Accept-Encoding")) {
                    request.addHeader("Accept-Encoding", "gzip");
                }
            }
        });

        httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
            public void process(
                    final HttpResponse response,
                    final HttpContext context) throws HttpException, IOException {
                HttpEntity entity = response.getEntity();
                Header ceheader = entity.getContentEncoding();
                if (ceheader != null) {
                    HeaderElement[] codecs = ceheader.getElements();
                    for (int i = 0; i < codecs.length; i++) {
                        if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                            response.setEntity(
                                    new GzipDecompressingEntity(response.getEntity()));
                            return;
                        }
                    }
                }
            }

            class GzipDecompressingEntity extends HttpEntityWrapper {
                public GzipDecompressingEntity(final HttpEntity entity) {
                    super(entity);
                }

                @Override
                public InputStream getContent()
                        throws IOException, IllegalStateException {
                    // the wrapped entity's getContent() decides about repeatability
                    InputStream wrappedin = wrappedEntity.getContent();
                    return new GZIPInputStream(wrappedin);
                }

                @Override
                public long getContentLength() {
                    // length of ungzipped content is not known
                    return -1;
                }
            }
        });
    }

}
