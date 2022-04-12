//
// Copyright 2017 Philipp Hagemeister <phihag@phihag.de>
// Copyright 2022 Mark Bucciarelli <mkbucc@gmail.com>
//
// Licensed is Attribution-ShareAlike 3.0 Unported (CC BY-SA 3.0)
// https://creativecommons.org/licenses/by-sa/3.0/
//
// This is a modified version of code posted to https://stackoverflow.com/a/42404471.
//
package com.acsredux.adapter.web.staticfiles;

import static com.acsredux.adapter.web.common.WebUtil.clientError;
import static com.acsredux.adapter.web.common.WebUtil.internalError;
import static com.acsredux.adapter.web.common.WebUtil.notFound;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class StaticFileHandler implements HttpHandler {

  private static final Map<String, String> MIME_MAP = new HashMap<>();

  static {
    MIME_MAP.put("appcache", "text/cache-manifest");
    MIME_MAP.put("css", "text/css");
    MIME_MAP.put("gif", "image/gif");
    MIME_MAP.put("html", "text/html");
    MIME_MAP.put("js", "application/javascript");
    MIME_MAP.put("json", "application/json");
    MIME_MAP.put("jpg", "image/jpeg");
    MIME_MAP.put("jpeg", "image/jpeg");
    MIME_MAP.put("mp4", "video/mp4");
    MIME_MAP.put("pdf", "application/pdf");
    MIME_MAP.put("png", "image/png");
    MIME_MAP.put("svg", "image/svg+xml");
    MIME_MAP.put("xml", "application/xml");
    MIME_MAP.put("zip", "application/zip");
    MIME_MAP.put("md", "text/plain");
    MIME_MAP.put("txt", "text/plain");
    MIME_MAP.put("php", "text/plain");
  }

  private final String filesystemRoot;
  private final String urlPrefix;

  public StaticFileHandler(String urlPrefix, String filesystemRoot) {
    this.urlPrefix = urlPrefix;
    assert filesystemRoot.endsWith("/");
    try {
      this.filesystemRoot = new File(filesystemRoot).getCanonicalPath();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void handle(HttpExchange x) {
    try {
      String method = x.getRequestMethod();

      if (!("HEAD".equals(method) || "GET".equals(method))) {
        internalError(x, new IllegalArgumentException("Unsupported HTTP method"));
        return;
      }

      String wholeUrlPath = x.getRequestURI().getPath();
      if (wholeUrlPath.endsWith("/")) {
        internalError(x, new IllegalArgumentException("Directory listing not supported"));
        return;
      }
      if (!wholeUrlPath.startsWith(urlPrefix)) {
        internalError(
          x,
          new RuntimeException("Path is not in prefix - incorrect routing?")
        );
        return;
      }
      String urlPath = wholeUrlPath.substring(urlPrefix.length());

      File f = new File(filesystemRoot, urlPath);
      File canonicalFile;
      try {
        canonicalFile = f.getCanonicalFile();
      } catch (IOException e) {
        // This may be more benign (i.e. not an attack, just a 403),
        // but we don't want the attacker to be able to discern the difference.
        reportPathTraversal(x);
        return;
      }

      String canonicalPath = canonicalFile.getPath();
      if (!canonicalPath.startsWith(filesystemRoot)) {
        reportPathTraversal(x);
        return;
      }

      FileInputStream fis;
      try {
        fis = new FileInputStream(canonicalFile);
      } catch (FileNotFoundException e) {
        notFound(x);
        return;
      }

      String mimeType = lookupMime(urlPath);
      x.getResponseHeaders().set("Content-Type", mimeType);
      try {
        if ("GET".equals(method)) {
          x.sendResponseHeaders(200, canonicalFile.length());
          OutputStream os = x.getResponseBody();
          copyStream(fis, os);
          os.close();
        } else {
          x.sendResponseHeaders(200, -1);
        }
      } catch (Exception e) {
        internalError(x, e);
      }
    } finally {
      x.close();
    }
  }

  private void copyStream(InputStream is, OutputStream os) throws IOException {
    byte[] buf = new byte[4096];
    int n;
    while ((n = is.read(buf)) >= 0) {
      os.write(buf, 0, n);
    }
  }

  private void reportPathTraversal(HttpExchange x) {
    clientError(x, "Path traversal attempt detected");
  }

  private static String getExt(String path) {
    int slashIndex = path.lastIndexOf('/');
    String basename = (slashIndex < 0) ? path : path.substring(slashIndex + 1);

    int dotIndex = basename.lastIndexOf('.');
    if (dotIndex >= 0) {
      return basename.substring(dotIndex + 1);
    } else {
      return "";
    }
  }

  private static String lookupMime(String path) {
    String ext = getExt(path).toLowerCase();
    return MIME_MAP.getOrDefault(ext, "application/octet-stream");
  }
}
