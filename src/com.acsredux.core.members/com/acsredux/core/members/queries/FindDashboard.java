package com.acsredux.core.members.queries;

import com.acsredux.core.base.MemberID;
import com.acsredux.core.base.Query;

public record FindDashboard(MemberID memberID) implements Query {}
