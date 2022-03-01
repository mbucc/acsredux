package com.acsredux.core.content.values;

import com.acsredux.core.members.values.MemberID;
import java.util.List;

public record Content(
  ContentID id,
  MemberID author,
  Title title,
  List<Section> sections,
  PublishedDate published
) {}
