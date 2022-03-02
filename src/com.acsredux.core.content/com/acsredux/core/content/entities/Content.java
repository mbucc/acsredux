package com.acsredux.core.content.entities;

import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.content.values.PublishedDate;
import com.acsredux.core.content.values.Section;
import com.acsredux.core.content.values.Title;
import com.acsredux.core.members.values.MemberID;
import java.util.List;

public record Content(
  ContentID id,
  MemberID author,
  Title title,
  List<Section> sections,
  PublishedDate published
) {}
