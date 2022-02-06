/** The Web adapter renders an HTML interface to the core business objects. */
module com.acsredux.adapter.web {
  requires com.acsredux.core.members;
  requires com.acsredux.adapter.stub;
  requires jdk.httpserver;
  requires com.github.mustachejava;
  requires com.github.leopard2a5.resultflow;

  exports com.acsredux.adapter.web ;
}
