package com.acsredux.core.content.ports;

import com.acsredux.core.content.commands.CreatePhotoDiary;
import com.acsredux.core.content.values.ContentID;

public interface ContentWriter {
  ContentID createContent(CreatePhotoDiary x);
}
