package com.acsredux.core.content.ports;

import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.content.values.NewContent;

public interface ContentWriter {
  ContentID save(NewContent x);
  void update(Content y);
}
