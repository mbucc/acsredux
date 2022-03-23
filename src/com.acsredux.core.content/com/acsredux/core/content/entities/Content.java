package com.acsredux.core.content.entities;

import com.acsredux.core.base.MemberID;
import com.acsredux.core.content.values.*;
import com.acsredux.core.members.values.CreatedOn;

public record Content(
  ContentID id,
  ContentID refersTo,
  MemberID createdBy,
  Title title,

  CreatedOn createdOn,
  FromDateTime from,
  UptoDateTime upto,

  ContentType contentType,
  BlobType blobType,
  BlobBytes content
) {}
