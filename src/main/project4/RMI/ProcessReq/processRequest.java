package main.project4.RMI.ProcessReq;


public class processRequest {

  public processRequest(Boolean status, String message, String value) {
    this.status = status;
    this.message = message;
    this.value = value;
  }

  public Boolean status;
  public String message;
  public String value;
}
