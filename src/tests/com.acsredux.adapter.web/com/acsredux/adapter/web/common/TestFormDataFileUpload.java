package com.acsredux.adapter.web.common;

import static com.acsredux.adapter.web.common.FormData.BOUNDARY_PREFIX;
import static com.acsredux.adapter.web.common.FormData.isFormUpload;
import static com.acsredux.adapter.web.common.FormData.parseFilePart;
import static com.acsredux.adapter.web.common.WebUtil.CONTENT_TYPE;
import static com.acsredux.lib.testutil.TestData.projectRoot;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.acsredux.adapter.web.MockHttpExchange;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class TestFormDataFileUpload {

  public static final String BOUNDARY = "----WebKitFormBoundaryybJ2sgPCQZ05qgDP";

  private String testImage() {
    return (
      projectRoot() + "cypress/fixtures/10138-80-prospect-business-hours-medium.jpeg"
    );
  }

  private String imageUploadRequest() {
    return projectRoot() + "test/fileupload/photoUploadRequestBody.bytes";
  }

  @Test
  void testIsFormUploadTrueForMultiPartContent() throws IOException {
    // setup
    HttpExchange x = new MockHttpExchange("/", "POST");
    x.getRequestHeaders().add(CONTENT_TYPE, "multipart/form-data; boundary=" + BOUNDARY);

    // verify
    assertTrue(FormData.isFormUpload(x));
  }

  @Test
  void testIsFormUploadFalseFormFormConent() throws IOException {
    // setup
    HttpExchange x = new MockHttpExchange("/", "POST");
    x.getRequestHeaders().add(CONTENT_TYPE, "application/x-www-form-urlencoded");

    // verify
    assertFalse(FormData.isFormUpload(x));
  }

  @Test
  void testIsFormUploadFalseIfNoContentType() throws IOException {
    // setup
    HttpExchange x = new MockHttpExchange("/", "POST");

    // verify
    assertFalse(FormData.isFormUpload(x));
  }

  @Test
  void testparseUploadedFileSunny() throws IOException {
    // setup
    String x =
      """
      Content-Disposition: form-data; name="picker"; filename="my-file.txt"
      Content-Type: text/plain
      
      Hello World!
      """;

    // execute
    var y = parseFilePart(x.split("\n"), 0);

    // verify
    assertNotNull(y);
    assertEquals("my-file.txt", y.filename());
    assertEquals("text/plain", y.filetype());
    assertArrayEquals("Hello World!".getBytes(), y.val());
  }

  @Test
  void testParse() throws IOException {
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
    var y1 = y.getUploadedFiles();
    assertNotNull(y1);
    assertEquals(1, y1.size());
    var y2 = y1.get(0);
    assertEquals("my-file.txt", y2.filename());
    assertEquals("text/plain", y2.filetype());
    assertArrayEquals("Hello World!".getBytes(), y2.val());
  }
}
