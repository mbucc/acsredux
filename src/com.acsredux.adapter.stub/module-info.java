module com.acsredux.adapter.stub {
  requires transitive com.acsredux.core.members;
  requires com.acsredux.core.admin;
  exports com.acsredux.adapter.stub ;
  provides com.acsredux.core.members.ports.Reader with com.acsredux.adapter.stub.Stub;
  provides com.acsredux.core.members.ports.Writer with com.acsredux.adapter.stub.Stub;
  provides com.acsredux.core.members.ports.Notifier with com.acsredux.adapter.stub.Stub;
}
