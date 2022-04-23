package com.acsredux.adapter.filesystem;

import static com.acsredux.adapter.filesystem.FileSystem.SITEINFO_JSON_FILE;
import static com.acsredux.adapter.filesystem.FileSystem.rename;
import static com.acsredux.lib.testutil.TestData.TEST_NEW_CONTENT_DIARY;
import static com.acsredux.lib.testutil.TestData.newContentDiaryWithID;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.acsredux.core.content.values.BlobBytes;
import com.acsredux.core.content.values.ContentID;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TestFileSystem {

  @BeforeAll
  static void writeSiteInfoJSON() {
    GsonBuilder builder = new GsonBuilder();
    builder.setPrettyPrinting();
    Gson gson = builder.create();
    String tmp = SITEINFO_JSON_FILE + ".tmp";
    SiteInfoDTO x = new SiteInfoDTO();
    x.siteDescription = "test site desription";
    x.siteEmail = "test@example.com";
    x.analyticsScriptTag = "";
    x.siteTitle = "test site";
    try (FileWriter writer = new FileWriter(tmp)) {
      gson.toJson(x, writer);
      rename(tmp, SITEINFO_JSON_FILE);
    } catch (Exception e) {
      throw new IllegalStateException("can't write '" + SITEINFO_JSON_FILE + "'", e);
    }
  }

  @AfterAll
  static void removeSiteInfoJSON() {
    var x = new File(SITEINFO_JSON_FILE);
    if (x.exists()) {
      boolean ok = x.delete();
      if (!ok) {
        throw new IllegalStateException("can't delete " + x);
      }
    }
  }

  @Test
  void testContextTextUpdate() {
    // setup
    FileSystem fs = new FileSystem();
    ContentID newID = fs.save(TEST_NEW_CONTENT_DIARY);
    BlobBytes newText = new BlobBytes("abc123".getBytes(StandardCharsets.UTF_8));
    var x = newContentDiaryWithID(newID).withText(newText);

    // execute
    fs.update(x);

    // verify
    assertEquals(newText, fs.getByID(newID).content());
  }
}
