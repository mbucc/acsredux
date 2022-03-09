package com.acsredux.core.content.ports;

import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.members.values.MemberID;
import java.util.List;

public interface ContentReader {
  Content getByID(ContentID x);
  List<Content> findByMemberID(MemberID x);
}
