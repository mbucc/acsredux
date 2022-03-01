package com.acsredux.core.articles.commands;

import com.acsredux.core.base.Command;

public sealed interface BaseArticleCommand extends Command permits CreatePhotoDiary {}
