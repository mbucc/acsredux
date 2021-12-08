package com.acsredux.base.entities;

import com.acsredux.base.values.Email;
import com.acsredux.base.values.FirstName;
import com.acsredux.base.values.GrowingZone;
import com.acsredux.base.values.LastName;
import com.acsredux.base.values.UserID;

public record User(
  UserID id,
  Email email,
  FirstName first,
  LastName last,
  GrowingZone zone
) {}
