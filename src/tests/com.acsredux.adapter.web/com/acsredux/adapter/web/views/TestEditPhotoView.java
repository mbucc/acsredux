package com.acsredux.adapter.web.views;

import static com.acsredux.adapter.web.views.EditPhotoView.uriToContentID;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;

public class TestEditPhotoView {

  @Test
  void testUriToContentID() throws URISyntaxException {
    URI x = new URI("/photo-diary/123/add-image");
    assertEquals(123L, uriToContentID(x));
  }
}
