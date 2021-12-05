December 5, 2021

## Purpose

Enable Java sources to use InstantSource and sealed classes.

## Steps

1. Open https://jdk.java.net/17/ and **DOWNLOAD** the GPL v2 open-source build for your platform.

1. Open a terminal, **CHANGE** in your $HOME directory.
1. **UNTAR** downloaded tarball. `tar xzvf ~/Downloads/openjdk-17.0.1_macos-x64_bin.tar.gz`

1. Using sudo, **MOVE** jdk-17.0.1.jdk to the standard Java location on OSX.
   `sudo mv jdk-17.0.1.jdk /Library/Java/JavaVirtualMachines/`

1. **VERIFY** OSX sees the new version: `/usr/libexec/java_home -v 17` should
   output `/Library/Java/JavaVirtualMachines/jdk-17.0.1.jdk/Contents/Home`

1. **UPDATE** JAVA_HOME in $HOME/.zshrc

```
% sed 's/java_home -v 16/java_home -v 17/' < ~/.zshrc > t
% diff ~/.zshrc t
7c7
< export JAVA_HOME=`/usr/libexec/java_home -v 16`
---
> export JAVA_HOME=`/usr/libexec/java_home -v 17`
% mv t ~/.zshrc
%
```
