/**
    Runs an in-memory DB that is loaded from and saved to the disk.
 */
module com.acsredux.adapter.filesystem {
  requires transitive com.acsredux.core.members;
  requires transitive com.acsredux.core.content;
  requires com.acsredux.core.admin;
  requires com.google.gson;

  exports com.acsredux.adapter.filesystem ;

  provides com.acsredux.core.members.ports.MemberReader
    with com.acsredux.adapter.filesystem.FileSystem;
  provides com.acsredux.core.members.ports.MemberWriter
    with com.acsredux.adapter.filesystem.FileSystem;

  opens com.acsredux.adapter.filesystem to com.google.gson;
}
