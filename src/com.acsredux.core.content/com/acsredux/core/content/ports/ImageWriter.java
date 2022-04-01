package com.acsredux.core.content.ports;

import com.acsredux.core.base.MemberID;
import com.acsredux.core.content.values.BlobBytes;
import com.acsredux.core.content.values.FileName;
import com.acsredux.core.content.values.ImageSource;

public interface ImageWriter {
  void save(MemberID x1, BlobBytes x2, FileName x3);

  default ImageSource src(MemberID x1, FileName x2) {
    return new ImageSource(String.format("/static/members/%d/%s", x1.val(), x2.val()));
  }

  void delete(MemberID mid, FileName std);
}
