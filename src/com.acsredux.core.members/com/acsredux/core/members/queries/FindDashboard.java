package com.acsredux.core.members.queries;

import com.acsredux.core.base.Query;
import com.acsredux.core.members.values.MemberID;

public record FindDashboard(MemberID memberID) implements Query {}
