package com.acsredux.core.content.ports;

import com.acsredux.core.base.MemberID;
import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.values.ContentID;
import java.util.List;
import java.util.Optional;

public interface ContentReader {
  Content getByID(ContentID x);

  default Optional<Content> findByID(ContentID x) {
    try {
      return Optional.of(getByID(x));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  List<Content> findByMemberID(MemberID x);
  List<Content> findChildrenOfID(ContentID x);
}
