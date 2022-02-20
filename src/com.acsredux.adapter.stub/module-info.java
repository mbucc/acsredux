/**
    The Stub adapter provides an in-memory implementation of persistance and notification ports.
 */
module com.acsredux.adapter.stub {
  requires transitive com.acsredux.core.members;
  requires com.acsredux.core.admin;
  requires transitive com.acsredux.core.articles;
  exports com.acsredux.adapter.stub ;
  provides com.acsredux.core.members.ports.MemberReader
    with com.acsredux.adapter.stub.Stub;
  provides com.acsredux.core.members.ports.MemberWriter
    with com.acsredux.adapter.stub.Stub;
  provides com.acsredux.core.members.ports.MemberNotifier
    with com.acsredux.adapter.stub.Stub;
}
