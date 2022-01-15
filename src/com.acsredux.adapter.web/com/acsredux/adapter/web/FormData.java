package com.acsredux.adapter.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

class FormData {

  private final Map<String, List<String>> data = new HashMap<>();

  private String lc(String key) {
    return key == null ? null : key.toLowerCase();
  }

  void add(String key, String val) {
    if (!data.containsKey(lc(key))) {
      data.put(lc(key), new ArrayList<>(1));
    }
    List<String> xs = data.get(lc(key));
    xs.add(val);
  }

  String get(String key) {
    if (data.containsKey(lc(key))) {
      return data.get(lc(key)).get(0);
    }
    return null;
  }

  List<String> getAll(String key) {
    return data.get(lc(key));
  }

  Map<String, Object> asMap() {
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
    StringBuffer buf = new StringBuffer();
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
}
