package com.acsredux.core.content;

import com.acsredux.core.content.ports.ContentReader;
import com.acsredux.core.content.ports.ContentWriter;
import com.acsredux.core.content.services.ContentServiceProvider;
import java.time.Clock;
import java.time.ZoneId;

public class ContentServiceFactory {

  private ContentServiceFactory() {
    throw new UnsupportedOperationException("static only");
  }

  public static ContentService getArticleService(
    ContentReader r,
    ContentWriter w,
    ZoneId tz
  ) {
    return new ContentServiceProvider(r, w, Clock.system(tz));
  }
}
