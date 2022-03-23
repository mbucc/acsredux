package com.acsredux.core.content;

import static com.acsredux.lib.testutil.TestData.TEST_IMAGE_BLOB;

import com.acsredux.core.content.ports.ImageReader;

public class MockImageReader implements ImageReader {

  @Override
  public byte[] readPlaceholderImage() {
    return TEST_IMAGE_BLOB.val();
  }
}
