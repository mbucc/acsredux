# Secret Management

Goals

- Keep unencrypted passwords out of environmental variables.
- Move bootstrap secret away from service after service startup.

Approach

Run the service start script as root, and have it:

    1. Copy the bootstrap secret to a place the service can read it.
    2. Using su, start the service as a non-root user.
    3. After service has started up, delete the bootstrap secret.

While you can drop privileges using JNI, this seemed simpler.

A developer on their local host you can just start the service.

Notes

In this case, the bootstrap secret is a ChaCha20 encrytion key.

Setup

$ make

$ ./scripts/setup-security
