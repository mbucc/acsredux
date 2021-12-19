module com.acsredux.adapter.stub {
  requires transitive com.acsredux.members;
  exports com.acsredux.adapter.stub ;
  provides com.acsredux.members.ports.Reader with com.acsredux.adapter.stub.Stub;
  provides com.acsredux.members.ports.Writer with com.acsredux.adapter.stub.Stub;
  provides com.acsredux.members.ports.Notifier with com.acsredux.adapter.stub.Stub;
}
