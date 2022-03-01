package com.acsredux.core.content.events;

import com.acsredux.core.base.Event;
import com.acsredux.core.content.commands.CreatePhotoDiary;
import com.acsredux.core.content.values.ContentID;

public record PhotoDiaryCreated(CreatePhotoDiary cmd, ContentID id) implements Event {}
