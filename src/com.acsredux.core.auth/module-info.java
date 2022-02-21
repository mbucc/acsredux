/**
    The auth core enforces a declarative security policy.
 */
module com.acsredux.core.auth {
  requires Validol;
  requires Either.java;
  requires com.google.gson;

  exports com.acsredux.core.auth ;
}
