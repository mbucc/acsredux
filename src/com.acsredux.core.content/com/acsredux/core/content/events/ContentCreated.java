package com.acsredux.core.content.events;

import com.acsredux.core.base.Event;
import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.content.values.NewContent;

public record ContentCreated(NewContent cmd, ContentID id) implements Event {}
