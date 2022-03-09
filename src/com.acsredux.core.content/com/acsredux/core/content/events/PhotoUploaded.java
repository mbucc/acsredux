package com.acsredux.core.content.events;

import com.acsredux.core.base.Event;
import com.acsredux.core.content.commands.UploadPhoto;
import com.acsredux.core.content.values.PhotoID;

public record PhotoUploaded(UploadPhoto cmd, PhotoID photoID) implements Event {}
