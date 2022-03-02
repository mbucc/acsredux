package com.acsredux.adapter.web.common;

import com.acsredux.adapter.web.auth.MemberHttpPrincipal;
import com.sun.net.httpserver.HttpExchange;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormData {

  private final Map<String, List<String>> data = new HashMap<>();

  public void add(String key, String val) {
    if (!data.containsKey(key)) {
      data.put(key, new ArrayList<>(1));
    }
    List<String> xs = data.get(key);
    xs.add(val);
  }

  public void addPrincipal(HttpExchange x) {
    if (x.getPrincipal() != null) {
      add("principalName", x.getPrincipal().getUsername());
      if (x.getPrincipal() instanceof MemberHttpPrincipal y) {
        add("principalID", String.valueOf(y.getMember().id().val()));
      }
    }
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

  public static FormData of(String encodedRequestBody) {
    FormData y = new FormData();
    if (encodedRequestBody == null || encodedRequestBody.isBlank()) {
      return y;
    }
    try {
      for (String pair : encodedRequestBody.split("&")) {
        String[] pieces = pair.split("=");
        String k = URLDecoder.decode(pieces[0], StandardCharsets.UTF_8);
        String v = "";
        if (pieces.length > 1) {
          v = URLDecoder.decode(pieces[1], StandardCharsets.UTF_8);
        }
        y.add(k, v);
      }
    } catch (Exception e) {
      // Some form posted invalid form data
      throw new IllegalStateException("invalid data: " + encodedRequestBody, e);
    }
    return y;
  }
}
