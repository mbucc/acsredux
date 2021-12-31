package com.acsredux.core.members.entities;

import com.acsredux.core.members.values.Email;
import com.acsredux.core.members.values.FirstName;
import com.acsredux.core.members.values.LastName;
import com.acsredux.core.members.values.MemberID;

public record Member(MemberID id, Email email, FirstName first, LastName last) {}
