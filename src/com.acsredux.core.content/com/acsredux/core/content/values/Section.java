package com.acsredux.core.content.values;

import com.acsredux.core.content.entities.Content;
import java.util.List;

public record Section(Content section, List<Content> subSections) {}
