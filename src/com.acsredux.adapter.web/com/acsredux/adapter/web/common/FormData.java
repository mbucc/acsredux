package com.acsredux.adapter.web.common;

import static com.acsredux.adapter.web.common.WebUtil.CONTENT_TYPE;
import static com.acsredux.adapter.web.common.WebUtil.getContentType;
import static com.acsredux.adapter.web.common.WebUtil.getContentTypeFromString;
import static com.acsredux.adapter.web.common.WebUtil.getHeaderParameter;
import static com.acsredux.adapter.web.common.WebUtil.readRequestBody;
import static com.acsredux.adapter.web.common.WebUtil.toUTF8String;

import com.acsredux.adapter.web.auth.MemberHttpPrincipal;
import com.sun.net.httpserver.HttpExchange;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class FormData {

  static final String BOUNDARY_PREFIX = "--";
  private final Map<String, List<String>> data = new HashMap<>();
  private MultipartFilePart file;

  public FormData add(String key, String val) {
    if (!data.containsKey(key)) {
      data.put(key, new ArrayList<>(1));
    }
    List<String> xs = data.get(key);
    xs.add(val);
    return this;
  }

  public FormData addPrincipal(HttpExchange x) {
    if (x.getPrincipal() != null) {
      add("principalName", x.getPrincipal().getUsername());
      if (x.getPrincipal() instanceof MemberHttpPrincipal y) {
        add("principalID", String.valueOf(y.getMember().id().val()));
      }
    }
    return this;
  }

  public long getPrincipalID() {
    String id = get("principalID");
    if (id == null) {
      return 0;
    }
    return Long.parseLong(id);
  }

  public String get(String key) {
    if (data.containsKey(key)) {
      return data.get(key).get(0);
    }
    return null;
  }

  List<String> getAll(String key) {
    return data.get(key);
  }

  public Map<String, Object> asMap() {
    Map<String, Object> ys = new HashMap<>(this.data.size());
    for (Map.Entry<String, List<String>> kv : this.data.entrySet()) {
      ys.put(kv.getKey(), firstOrList(kv));
    }
    return ys;
  }

  private Object firstOrList(Map.Entry<String, List<String>> x) {
    if (x.getValue().size() == 1) {
      return x.getValue().get(0);
    } else {
      return x.getValue();
    }
  }

  @Override
  public String toString() {
    String[] patterns = { "pwd", "password", "passwd" };
    StringBuilder buf = new StringBuilder();
    buf.append("<FormData: ");
    for (Map.Entry<String, List<String>> kv : this.data.entrySet()) {
      buf.append(kv.getKey());
      buf.append("=");
      String y = firstOrList(kv).toString();
      for (String pattern : patterns) {
        if (kv.getKey().toLowerCase().contains(pattern)) {
          y = "********";
          break;
        }
      }
      buf.append(y);
      buf.append(", ");
    }
    buf.deleteCharAt(buf.length() - 1);
    buf.deleteCharAt(buf.length() - 1);
    buf.append(">");
    return buf.toString();
  }

  private static FormData parseFormDataFromQueryString(HttpExchange x) {
    return FormData.parse(x.getRequestURI().getRawQuery());
  }

  // Example request body:
  //
  //      1: ------WebKitFormBoundaryybJ2sgPCQZ05qgDP
  //      2: Content-Disposition: form-data; filename="picker"; filename="10138-80-prospect-business-hours-medium.jpeg"
  //      3: Content-Type: image/jpeg
  //      4:
  //      5: ����
  //    ...  [lots more binary data]
  //    178: ------WebKitFormBoundaryybJ2sgPCQZ05qgDP
  //    179: Content-Disposition: form-data; filename="submit"
  //    180:
  //    181: Upload image to your 2022: back yard diary
  //    182: ------WebKitFormBoundaryybJ2sgPCQZ05qgDP--
  //
  // To extract the binary image:
  //    1. Take all lines between first and second boundary.
  //    2. Delete lines from beginning up to and including first blank line (line 4 above).
  //    3. Remove any \n and \r characters at end of the byte array (end of line 177 above).
  //
  private static FormData parseMultiPartForm(HttpExchange x) {
    var y = new FormData();

    // Read MIME type boundary from header.
    var boundary = getHeaderParameter(getContentType(x), "boundary");

    // bytes [] -> ISO_8859_1 -> byte[] is lossless.
    var body = new String(readRequestBody(x), StandardCharsets.ISO_8859_1);
    var lines = body.split("\\n");

    // Parse body into it's parts.
    int j = 0;
    StringBuilder sb = new StringBuilder();
    int i = readUpToAndIncluding(lines, j, "content-disposition:");
    Consumer<byte[]> byteConsumer;
    while (i < lines.length) {
      j = readUpToAndIncluding(lines, i, boundary);

      if (lines[i].toLowerCase().contains("filename=")) {
        String filename = getHeaderParameter(lines[i], "filename");
        String name = getHeaderParameter(lines[i++], "name");
        String filetype = getContentTypeFromString(lines[i++]);
        byteConsumer =
          bytes -> y.file = new MultipartFilePart(name, filename, filetype, bytes);
      } else {
        String name = getHeaderParameter(lines[i++], "name");
        byteConsumer = bytes -> y.add(name, new String(bytes));
      }

      //
      //      Read and consume remaining content in this part
      //
      i++; //   blank line between content type and content
      sb.setLength(0);
      for (int k = i; k < j; k++) {
        sb.append(lines[k]);
        sb.append("\n");
      }
      byteConsumer.accept(sb.toString().trim().getBytes(StandardCharsets.ISO_8859_1));

      i = j + 1;
    }

    return y;
  }

  //            pattern is not a regex!
  private static int readUpToAndIncluding(String[] lines, int i, String notAtRegex) {
    for (int j = i; j < lines.length; j++) {
      if (lines[j].toLowerCase().trim().contains(notAtRegex.toLowerCase())) {
        return j;
      }
    }
    String fmt = "can't find '%s' between lines %d and %d.  line[%d]=\n%s";
    throw new IllegalStateException(
      String.format(fmt, notAtRegex, i, lines.length, i, lines[i])
    );
  }

  private static FormData parseFormData(HttpExchange x) {
    var requestBody = readRequestBody(x);
    if (requestBody == null || requestBody.length == 0) {
      return new FormData();
    }
    return FormData.parse(toUTF8String(requestBody));
  }

  public static boolean isFormUpload(HttpExchange x) {
    var headers = x.getRequestHeaders();
    var ys = headers.get(CONTENT_TYPE);
    if (ys == null || ys.isEmpty()) {
      return false;
    }
    return ys.get(0).toLowerCase().contains("multipart/form-data");
  }

  /**
   * Parse form data from an application/x-www-form-urlencoded string.
   */
  public static FormData parse(String x) {
    var y = new FormData();
    if (x == null || x.isBlank()) {
      return y;
    }
    try {
      for (String pair : x.split("&")) {
        String[] pieces = pair.split("=");
        String k = URLDecoder.decode(pieces[0], StandardCharsets.UTF_8);
        String v = "";
        if (pieces.length > 1) {
          v = URLDecoder.decode(pieces[1], StandardCharsets.UTF_8);
        }
        y.add(k, v);
      }
    } catch (Exception e) {
      throw new IllegalStateException("invalid data: " + x, e);
    }
    return y;
  }

  /**
   * Extract form data from an HTTP request.
   *
   * <p>If the request is a query or a delete, the data is non-null and empty.
   *  It is spec-compliant to ignore the request body for GET or DELETE.
   *  For a good discussion of the GET method and request bodies, see
   *  https://stackoverflow.com/a/983458/1789168.
   */
  public static FormData of(HttpExchange x) {
    final FormData y;

    if (isFormUpload(x)) {
      y = parseMultiPartForm(x);
    } else {
      y =
        switch (x.getRequestMethod()) {
          case "PUT", "PATCH", "POST" -> {
            if (WebUtil.getContentType(x).startsWith("text/")) {
              var o = new FormData();
              o.add("body", new String(readRequestBody(x)));
              yield o;
            } else {
              yield parseFormData(x);
            }
          }
          case "GET" -> {
            WebUtil.drainRequestBody(x);
            yield parseFormDataFromQueryString(x);
          }
          default -> {
            WebUtil.drainRequestBody(x);
            yield new FormData();
          }
        };
    }

    y.addPrincipal(x);

    return y;
  }

  public MultipartFilePart getUploadedFile() {
    return file;
  }
}
