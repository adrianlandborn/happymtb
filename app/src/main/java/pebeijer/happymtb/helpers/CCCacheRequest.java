package pebeijer.happymtb.helpers;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.CacheRequest;

public class CCCacheRequest extends CacheRequest {

	final ByteArrayOutputStream body = new ByteArrayOutputStream();

	public CCCacheRequest() {
	}

	public OutputStream getBody() {
		return body;
	}

	public void abort() {
	}
}
