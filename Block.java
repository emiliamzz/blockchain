import java.sql.Timestamp;

public class Block{
  
  private int index; //the index of the block in the list
  private String hash; //hash of the block (hash of string obtained from previous variables via toString() method)
  private String nonce; //random string (for proof of work)
  private String previousHash; //previous hash (in first block, set to string of zeroes of size of complexity "00000")
  private Timestamp timestamp; //time at which transaction has been processed
  private Transaction transaction; //the transaction occuring in the block
  
  public Block(int i, long time, String h, String n, String p, Transaction trans){
    index = i;
    hash = h;
    nonce = n;
    previousHash = p;
    timestamp = new Timestamp(time);
    transaction = trans;
  }//end Block
  
  /* Returns the amount of the Transaction in the Block
   * 
   * @return Amount of the Transaction
   */
  public int getAmount(){
    return transaction.getAmount();
  }//end getAmount
  
  /* Returns the hash of the Block
   * 
   * @return Hash
   */
  public String getHash(){
    return hash;
  }//end getHash
 
  /* Returns the index of the Block
   * 
   * @return Index
   */
  public int getIndex(){
    return index;
  }//end getIndex
  
  /* Returns the nonce of the Block
   * 
   * @return Nonce
   */
  public String getNonce(){
    return nonce;
  }//end getNonce
  
  /* Returns the previous Block's hash
   * 
   * @return Previous hash
   */
  public String getPreviousHash(){
    return previousHash;
  }//end getPreviousHash
  
  /* Returns the receiver of the Transaction in the Block
   * 
   * @return Receiver of Transaction
   */
  public String getReceiver(){
    return transaction.getReceiver();
  }//end getReceiver
  
  /* Returns the sender of the Transaction in the Block
   * 
   * @return Sender of Transaction
   */
  public String getSender(){
    return transaction.getSender();
  }//end getSender
  
  /* Returns the timestamp of the Block as a long
   * 
   * @return Timestamp
   */
  public long getTimestamp(){
    return timestamp.getTime();
  }//end getTimestamp
  
  /* Generates the String representation of the Block class
   * 
   * @return String representation
   */
  public String toString(){
    return timestamp.toString() + ":" + transaction.toString() + "." + nonce + previousHash;
  }//end toString
  
}//end Block