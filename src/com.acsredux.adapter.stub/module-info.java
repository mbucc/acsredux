module com.acsredux.adapter.stub {
  requires com.acsredux.user;
  requires com.acsredux.base;
  provides com.acsredux.user.ports.Reader with com.acsredux.adapter.stub.Stub;
  provides com.acsredux.user.ports.Writer with com.acsredux.adapter.stub.Stub;
  provides com.acsredux.user.ports.Notifier with com.acsredux.adapter.stub.Stub;
}
