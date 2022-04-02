package com.acsredux.adapter.web.views;

import com.acsredux.core.content.entities.Content;

record ContentSummaryView(String title, long id) {
  static ContentSummaryView of(Content x) {
    return new ContentSummaryView(x.title().val(), x.id().val());
  }
}
