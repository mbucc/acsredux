/**
 The content core defines the services, data types, and ports for creating
 member content.
 */
module com.acsredux.core.content {
  requires transitive com.acsredux.core.base;
  requires transitive com.acsredux.core.members;

  exports com.acsredux.core.content.ports ;
  exports com.acsredux.core.content.values ;
  exports com.acsredux.core.content ;
  exports com.acsredux.core.content.events ;
  exports com.acsredux.core.content.commands ;
  exports com.acsredux.core.content.entities ;
}
