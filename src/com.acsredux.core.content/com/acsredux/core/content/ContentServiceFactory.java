package com.acsredux.core.content;

import com.acsredux.core.content.ports.ContentReader;
import com.acsredux.core.content.ports.ContentWriter;
import com.acsredux.core.content.ports.ImageWriter;
import com.acsredux.core.content.services.ContentServiceProvider;
import java.time.Clock;
import java.time.ZoneId;

public class ContentServiceFactory {

  private ContentServiceFactory() {
    throw new UnsupportedOperationException("static only");
  }

  public static ContentService getArticleService(
    ZoneId tz,
    ContentReader r,
    ContentWriter w,
    ImageWriter iw
  ) {
    return new ContentServiceProvider(Clock.system(tz), r, w, iw);
  }
}
