package main.project4.RMI.Server;

class Pair<K, T> {

  private T value;
  private K key;

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }

  public K getKey() {
    return key;
  }

  public void setKey(K key) {
    this.key = key;
  }

  Pair(K key, T value) {
    this.key = key;
    this.value = value;
  }
}