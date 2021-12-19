package com.acsredux.members.entities;

import com.acsredux.members.values.Email;
import com.acsredux.members.values.FirstName;
import com.acsredux.members.values.GrowingZone;
import com.acsredux.members.values.LastName;
import com.acsredux.members.values.MemberID;

public record Member(
  MemberID id,
  Email email,
  FirstName first,
  LastName last,
  GrowingZone zone
) {}
