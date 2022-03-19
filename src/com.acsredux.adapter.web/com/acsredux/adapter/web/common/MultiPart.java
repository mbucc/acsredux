package com.acsredux.adapter.web.common;

/**
 * One part of a multi-part upload.
 */
public sealed interface MultiPart permits FormFieldPart, MultipartFilePart {
  String name();
  byte[] val();
}
