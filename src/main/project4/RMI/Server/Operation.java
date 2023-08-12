package main.project4.RMI.Server;

class Operation {

  String type;
  String key;
  String value;

  Operation(String type, String key, String value) {
    this.type = type;
    this.key = key;
    this.value = value;
  }
}