package com.acsredux.adapter.web.members;

import com.acsredux.core.members.values.MemberID;
import com.acsredux.core.members.values.SessionID;

record MemberSession(MemberID mid, SessionID sid) {}
