package com.acsredux.adapter.web;

class CommandLineArguments {

  int port;
  String host;
  int threads;
  int backlog;
  String documentRoot;

  private CommandLineArguments() {
    this.port = 8000;
    this.host = "localhost";
    this.threads = 10;
    this.backlog = 0;
    this.documentRoot = ".";
  }

  String portAsString() {
    return String.valueOf(port);
  }

  private static void usage() {
    System.err.print(
      "valid arguments:\n" +
      "-h <host> (default=localhost)\n" +
      "-p <port> (default=8000)\n" +
      "-r <document_root>\n" +
      "-b <backlog> (default=0)\n" +
      "-t <threads> (default=10)\n"
    );
  }

  static CommandLineArguments scan(String[] args) {
    CommandLineArguments y = new CommandLineArguments();
    int n = args.length;
    for (int i = 0; i < n - 1; i += 2) {
      switch (args[i]) {
        case "-h" -> y.host = args[i + 1];
        case "-p" -> y.port = Integer.parseInt(args[i + 1]);
        case "-r" -> y.documentRoot = args[i + 1];
        case "-b" -> y.backlog = Integer.parseInt(args[i + 1]);
        case "-t" -> y.threads = Integer.parseInt(args[i + 1]);
        default -> {
          System.err.printf("unknown argument '%s'", args[i]);
          usage();
          System.exit(1);
        }
      }
    }
    return y;
  }
}
