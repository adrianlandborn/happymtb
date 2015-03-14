package org.pebeijer.happymtb.helpers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CacheResponse;
import java.util.List;
import java.util.Map;

public class CCCacheResponse extends CacheResponse {

	Map<String, List<String>> headers;
	ByteArrayOutputStream body;

	public CCCacheResponse(Map<String, List<String>> headers, OutputStream body) {
		this.headers = headers;
		// should be the output stream defined in a companion CacheRequest.
		this.body = (ByteArrayOutputStream) body;
	}

	@Override
	public InputStream getBody() {
		return new ByteArrayInputStream(body.toByteArray());
	}

	@Override
	public Map<String, List<String>> getHeaders() {
		return headers;
	}
}
