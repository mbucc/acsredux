package com.acsredux.adapter.web.members;

import com.acsredux.core.base.MemberID;
import com.acsredux.core.members.values.SessionID;

record MemberSession(MemberID mid, SessionID sid) {}
