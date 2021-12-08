package com.acsredux.user.queries;

import com.acsredux.base.Query;
import com.acsredux.base.values.UserID;

public record FindUserDashboard(UserID userID) implements Query {}
