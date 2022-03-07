package com.acsredux.adapter.web.common;

public record FilePart(String name, String filename, String filetype, byte[] val)
  implements MultiPart {}
