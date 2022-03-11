/** The Web adapter renders an HTML interface to the core business objects. */
module com.acsredux.adapter.web {
  requires com.acsredux.core.members;
  requires com.acsredux.adapter.stub;
  requires jdk.httpserver;
  requires com.github.mustachejava;
  requires com.github.leopard2a5.resultflow;
  requires com.acsredux.core.auth;
  requires com.acsredux.core.content;
  requires Either.java;
  requires com.acsredux.lib.env;
  requires com.acsredux.adapter.mailgun;
  requires com.acsredux.adapter.filesystem;

  exports com.acsredux.adapter.web ;
  opens com.acsredux.adapter.web to com.github.mustachejava;
  opens com.acsredux.adapter.web.views to com.github.mustachejava;
}
