package com.acsredux.core.articles.values;

import com.acsredux.core.members.values.MemberID;
import java.util.List;

public record Article(
  MemberID author,
  Title title,
  List<Section> sections,
  PublishedDate published
) {}
