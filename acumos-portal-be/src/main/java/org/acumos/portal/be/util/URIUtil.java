package org.acumos.portal.be.util;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.util.UriComponentsBuilder;

public class URIUtil {
	
	@Autowired
	private Environment env;
	
	public URIUtil() {}
	
	public  void setEnvironment(Environment envi) {
		env=envi;
	}
	
	public URI buildUri(final String[] path,
		final Map<String, Object> queryParams/* , RestPageRequest pageRequest */) {
		String baseUrl = env.getProperty("elk.url");
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl);
		for (int p = 0; p < path.length; ++p) {
			if (path[p] == null)
				throw new IllegalArgumentException("Unexpected null at path index " + Integer.toString(p));
			builder.pathSegment(path[p]);
		}
		if (queryParams != null && queryParams.size() > 0) {
			for (Map.Entry<String, ? extends Object> entry : queryParams.entrySet()) {
				if (entry.getKey() == null || entry.getValue() == null) {
					throw new IllegalArgumentException("Unexpected null key or value");
				} else if (entry.getValue() instanceof Instant) {
					// Server expects point-in-time as Long (not String)
					builder.queryParam(entry.getKey(), ((Instant) entry.getValue()).toEpochMilli());
				} else if (entry.getValue().getClass().isArray()) {
					Object[] array = (Object[]) entry.getValue();
					for (Object o : array) {
						if (o == null)
							builder.queryParam(entry.getKey(), "null");
						else if (o instanceof Instant)
							builder.queryParam(entry.getKey(), ((Instant) o).toEpochMilli());
						else
							builder.queryParam(entry.getKey(), o.toString());
					}
				} else {
					builder.queryParam(entry.getKey(), entry.getValue().toString());
				}
			}
		}
//			if (pageRequest != null) {
//				if (pageRequest.getSize() != null)
//					builder.queryParam("page", Integer.toString(pageRequest.getPage()));
//				if (pageRequest.getPage() != null)
//					builder.queryParam("size", Integer.toString(pageRequest.getSize()));
//				if (pageRequest.getFieldToDirectionMap() != null && pageRequest.getFieldToDirectionMap().size() > 0) {
//					for (Map.Entry<String, String> entry : pageRequest.getFieldToDirectionMap().entrySet()) {
//						String value = entry.getKey() + (entry.getValue() == null ? "" : ("," + entry.getValue()));
//						builder.queryParam("sort", value);
//					}
//				}
//			}
		return builder.build()/* .encode() */.toUri();
	}

}
