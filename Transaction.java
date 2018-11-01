public class Transaction{
  
  private String sender; //username of the person giving money
  private String receiver; //username of the person receiving money
  private int amount; //number of bitcoins (assume integer value)
  
  public Transaction(String s, String r, int a){
    sender = s;
    receiver = r;
    amount = a;
  }//end Transaction
  
  /* Returns the amount of the Transaction
   * 
   * @return Amount
   */
  public int getAmount(){
    return amount;
  }//end getAmount
  
  /* Returns the receiver of the Transaction
   * 
   * @return Receiver
   */
  public String getReceiver(){
    return receiver;
  }//end getReceiver
  
  /* Returns the sender of the Transaction
   * 
   * @return Sender
   */
  public String getSender(){
    return sender;
  }//end getSender
  
  /* Generates the String representation of the Transaction class.
   * 
   * @return String representation
   */
  public String toString(){
    return sender + ":" + receiver + "=" + amount;
  }//end toString
  
}//end Transaction