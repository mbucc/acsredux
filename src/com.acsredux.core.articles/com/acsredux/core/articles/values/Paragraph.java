package com.acsredux.core.articles.values;

import java.util.List;

public record Paragraph(List<Word> words) implements SectionElement {}
