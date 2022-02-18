/**
    The articles core defines the services, data types, and ports for writing articles.
 */
module com.acsredux.core.articles {
  requires transitive com.acsredux.core.base;
  exports com.acsredux.core.articles.ports ;
  exports com.acsredux.core.articles.values ;
  exports com.acsredux.core.articles ;
  exports com.acsredux.core.articles.events ;
  exports com.acsredux.core.articles.commands ;
}
