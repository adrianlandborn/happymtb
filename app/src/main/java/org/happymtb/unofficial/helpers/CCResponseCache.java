package org.happymtb.unofficial.helpers;

import java.io.IOException;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.ResponseCache;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CCResponseCache extends ResponseCache {

	Map<URI, CacheResponse> mCache = new HashMap<>();

	@Override
	public CacheResponse get(URI uri, String requestMethod,
			Map<String, List<String>> requestHeaders) {
		return mCache.get(uri);
	}

	@Override
	public CacheRequest put(URI uri, URLConnection conn) {

		CacheRequest req = new CCCacheRequest();

		Map<String, List<String>> headers = conn.getHeaderFields();
		CacheResponse resp = null;
		try {
			resp = new CCCacheResponse(headers, req.getBody());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			// e1.printStackTrace();
		}

		// For some reason the path of the URI being passed is an empty string.
		// Get a good URI from the connection object.
		try {
			uri = conn.getURL().toURI();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		mCache.put(uri, resp);

		return req;
	}
}