package com.acsredux.members.queries;

import com.acsredux.base.Query;
import com.acsredux.members.values.MemberID;

public record FindMemberDashboard(MemberID memberID) implements Query {}
