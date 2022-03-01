package com.acsredux.core.content.values;

import java.util.List;

public record Paragraph(List<Word> words) implements SectionElement {}
