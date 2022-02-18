package com.acsredux.core.articles.values;

import java.util.List;

public record Article(Title title, List<Section> sections, PublishedDate published) {}
