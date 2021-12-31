module com.acsredux.core.members {
  requires transitive com.acsredux.core.admin;
  requires transitive com.acsredux.core.base;
  exports com.acsredux.core.members.commands ;
  exports com.acsredux.core.members.queries ;
  exports com.acsredux.core.members.values ;
  exports com.acsredux.core.members.entities ;
  exports com.acsredux.core.members.ports ;
  exports com.acsredux.core.members.events ;
  exports com.acsredux.core.members ;
}
