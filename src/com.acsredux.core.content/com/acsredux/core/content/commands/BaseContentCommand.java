package com.acsredux.core.content.commands;

import com.acsredux.core.base.Command;

public sealed interface BaseContentCommand
  extends Command
  permits CreatePhotoDiary, DeleteContent, UploadPhoto {}
