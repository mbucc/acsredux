module com.acsredux.adapter.sendgrid {
  requires transitive com.acsredux.core.members;
  requires com.acsredux.core.base;
  requires com.acsredux.lib.env;
  requires java.net.http;
  exports com.acsredux.adapter.sendgrid ;
  provides com.acsredux.core.members.ports.Notifier
    with com.acsredux.adapter.sendgrid.SendGridNotifier;
}
