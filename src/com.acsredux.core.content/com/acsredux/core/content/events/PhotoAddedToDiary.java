package com.acsredux.core.content.events;

import com.acsredux.core.base.Event;
import com.acsredux.core.content.commands.UploadPhoto;

public record PhotoAddedToDiary(UploadPhoto cmd) implements Event {}
