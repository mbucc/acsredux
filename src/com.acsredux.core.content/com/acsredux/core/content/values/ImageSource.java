package com.acsredux.core.content.values;

// Use String not URL type because "/static/img/t.png" is not a valid URL.
public record ImageSource(String val) {}
