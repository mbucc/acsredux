package com.acsredux.core.content.values;

import com.acsredux.core.base.MemberID;
import com.acsredux.core.members.values.CreatedOn;

public record Photo(
  MemberID memberID,
  ImageSource imageSource,
  ImageOrientation orientation,
  ImageDate takenOn,
  CreatedOn uploadedOn
) {}
