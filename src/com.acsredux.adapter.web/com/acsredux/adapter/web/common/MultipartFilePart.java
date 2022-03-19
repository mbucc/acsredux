package com.acsredux.adapter.web.common;

public record MultipartFilePart(String name, String filename, String filetype, byte[] val)
  implements MultiPart {}
