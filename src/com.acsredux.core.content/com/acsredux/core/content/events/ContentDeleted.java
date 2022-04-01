package com.acsredux.core.content.events;

import com.acsredux.core.base.Event;
import com.acsredux.core.content.commands.DeleteContent;

public record ContentDeleted(DeleteContent cmd) implements Event {}
