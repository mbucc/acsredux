/**
    The auth core enforces a declarative security policy.
 */
module com.acsredux.core.auth {
  requires com.google.gson;
  requires transitive com.acsredux.core.members;
  requires transitive com.acsredux.core.content;

  exports com.acsredux.core.auth ;
  exports com.acsredux.core.auth.values ;
}
