package com.acsredux.core.content;

import com.acsredux.core.base.MemberID;
import com.acsredux.core.content.ports.ImageWriter;
import com.acsredux.core.content.values.*;

public class MockImageWriter implements ImageWriter {

  @Override
  public void save(MemberID x1, BlobBytes x2, FileName x3) {}

  @Override
  public void delete(MemberID mid, FileName std) {}
}
