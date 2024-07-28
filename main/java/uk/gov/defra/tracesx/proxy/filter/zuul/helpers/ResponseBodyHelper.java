package uk.gov.defra.tracesx.proxy.filter.zuul.helpers;

import com.netflix.zuul.context.RequestContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Component
public class ResponseBodyHelper {

  public static final java.lang.String UTF_8 = "UTF-8";

  public String readResponseBody(RequestContext context) throws IOException {
    InputStream stream = context.getResponseDataStream();
    if (context.getResponseGZipped() && stream != null) {
      return StreamUtils.copyToString(new GZIPInputStream(stream), Charset.forName(UTF_8));
    } else {
      return StreamUtils.copyToString(stream, Charset.forName(UTF_8));
    }
  }

  public void writeResponseBody(String body, RequestContext context) throws IOException {
    if (context.getResponseGZipped()) {
      ByteArrayOutputStream bos = new ByteArrayOutputStream(body.length());
      GZIPOutputStream gzip = new GZIPOutputStream(bos);
      gzip.write(body.getBytes(UTF_8));
      gzip.close();
      byte[] compressed = bos.toByteArray();
      bos.close();
      context.setResponseDataStream(new ByteArrayInputStream(compressed));
    } else {
      context.setResponseBody(body);
    }
  }
}
