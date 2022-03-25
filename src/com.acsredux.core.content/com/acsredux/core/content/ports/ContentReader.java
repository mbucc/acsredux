package com.acsredux.core.content.ports;

import com.acsredux.core.base.MemberID;
import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.values.ContentID;
import java.util.List;

public interface ContentReader {
  Content getByID(ContentID x);
  List<Content> findByMemberID(MemberID x);
  List<Content> findChildrenOfID(ContentID x);
}
