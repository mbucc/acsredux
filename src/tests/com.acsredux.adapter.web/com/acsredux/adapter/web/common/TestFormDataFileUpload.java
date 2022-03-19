package com.acsredux.adapter.web.common;

import static com.acsredux.adapter.web.common.FormData.BOUNDARY_PREFIX;
import static com.acsredux.adapter.web.common.FormData.isFormUpload;
import static com.acsredux.adapter.web.common.WebUtil.CONTENT_TYPE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.acsredux.adapter.web.MockHttpExchange;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;

class TestFormDataFileUpload {

  public static final String BOUNDARY = "----WebKitFormBoundaryybJ2sgPCQZ05qgDP";

  @Test
  void testIsFormUploadTrueForMultiPartContent() {
    // setup
    HttpExchange x = new MockHttpExchange("/", "POST");
    x.getRequestHeaders().add(CONTENT_TYPE, "multipart/form-data; boundary=" + BOUNDARY);

    // verify
    assertTrue(FormData.isFormUpload(x));
  }

  @Test
  void testIsFormUploadFalseFormFormConent() {
    // setup
    HttpExchange x = new MockHttpExchange("/", "POST");
    x.getRequestHeaders().add(CONTENT_TYPE, "application/x-www-form-urlencoded");

    // verify
    assertFalse(FormData.isFormUpload(x));
  }

  @Test
  void testIsFormUploadFalseIfNoContentType() {
    // setup
    HttpExchange x = new MockHttpExchange("/", "POST");

    // verify
    assertFalse(FormData.isFormUpload(x));
  }

  @Test
  void testParse() {
    // setup
    String requestBody =
      BOUNDARY_PREFIX +
      BOUNDARY +
      "\r\n" +
      """
        Content-Disposition: form-data; name="picker"; filename="my-file.txt"
        Content-Type: text/plain
        
        Hello World!
        """ +
      BOUNDARY_PREFIX +
      BOUNDARY +
      "\r\n" +
      """
          Content-Disposition: form-data; name="submit"
          
          Upload image to your 2022: back yard diary
          """ +
      BOUNDARY_PREFIX +
      BOUNDARY;
    var x = new MockHttpExchange("/", "POST", requestBody);
    x.getRequestHeaders().add(CONTENT_TYPE, "multipart/form-data; boundary=" + BOUNDARY);

    // execute
    var y = FormData.of(x);

    // verify
    assertNotNull(y);
    assertTrue(isFormUpload(x));
    var y1 = y.getUploadedFile();
    assertNotNull(y1);
    assertEquals("my-file.txt", y1.filename());
    assertEquals("text/plain", y1.filetype());
    assertArrayEquals("Hello World!".getBytes(), y1.val());
  }
}
