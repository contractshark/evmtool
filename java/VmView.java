package org.hyperledger.besu.ethereum.vm;
 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tuweni.bytes.Bytes;
import org.hyperledger.besu.ethereum.core.Address;
 
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
 
// Just handle single contract calls at the moment.
public class VmView {
 
  private static final Logger LOG = LogManager.getLogger();
 
  private static Map<Address, ContractExecution> everything;
 
  private static Address entryPointAddress;
  private static Bytes entryPointFunctionId;
 
 
  public static void resetForNewCall(final Address addr, final Bytes funcId) {
    LOG.info("**************RESET FOR NEW CALL: Address: {}, Function: {}", addr, funcId);
    entryPointAddress = addr;
    entryPointFunctionId = funcId;
    everything = new HashMap<>();
  }
 
  public static void addCodeTouched(final Address addr, final Bytes funcId, final Code code, final int pc, final int opLength) {
    ContractExecution contract = everything.get(addr);
    if (contract == null) {
      contract = new ContractExecution(addr, funcId, code);
      everything.put(addr, contract);
    }
    contract.addCodeTouched(pc, opLength);
  }
 
  public static void addStorageRead(final Address addr, final BigInteger location) {
    ContractExecution contract = everything.get(addr);
    if (contract == null) {
      LOG.error("Shouldn't be able to get here");
      throw new Error("Shouldn't be able to get here");
    }
    contract.addStorageRead(location);
  }
 
  public static void addStorageWritten(final Address addr, final BigInteger location) {
    ContractExecution contract = everything.get(addr);
    if (contract == null) {
      LOG.error("Shouldn't be able to get here");
      throw new Error("Shouldn't be able to get here");
    }
    contract.addStorageWritten(location);
  }
 
  public static String executionSummary() {
    StringBuilder buf = new StringBuilder();
    buf.append("Entry Point: Contract at ").append(entryPointAddress);
    buf.append(", Function: ").append(entryPointFunctionId).append("\n");
    for (ContractExecution contract: everything.values()) {
      buf.append(contract.executionSummary());
    }
    return buf.toString();
  }
 
 
  public static class ContractExecution {
    Address address;
    Bytes functionId;
    Code codeExecuted;
    boolean[] codeTouched;
 
    Map<BigInteger, Integer> storageRead;
    Map<BigInteger, Integer> storageWritten;
 
    public ContractExecution(final Address addr, final Bytes funcId, final Code code) {
      this.address = addr;
      this.functionId = funcId;
      this.codeExecuted = code;
      this.codeTouched = new boolean[this.codeExecuted.getSize()];
      this.storageRead = new HashMap<>();
      this.storageWritten = new HashMap<>();
    }
 
    public void addCodeTouched(final int pc, final int opLength) {
      for (int i = pc; i < pc+opLength; i++) {
        this.codeTouched[i] = true;
      }
    }
 
    public void addStorageRead(final BigInteger location) {
      Integer numTimes = this.storageRead.get(location);
      if (numTimes == null) {
        numTimes = 0;
      }
      numTimes++;
      this.storageRead.put(location, numTimes);
    }
 
    public void addStorageWritten(final BigInteger location) {
      Integer numTimes = this.storageWritten.get(location);
      if (numTimes == null) {
        numTimes = 0;
      }
      numTimes++;
      this.storageWritten.put(location, numTimes);
    }
 
    public String executionSummary() {
      StringBuilder buf = new StringBuilder();
      buf.append("Contract at ").append(address);
      buf.append(", Function: ").append(functionId).append("\n");
      buf.append(codeExecuted.toString()).append("\n");
      int count = 0;
 
      int count2 = 0;
      boolean counted2 = false;
      int count4 = 0;
      boolean counted4 = false;
      int count8 = 0;
      boolean counted8 = false;
      int count16 = 0;
      boolean counted16 = false;
      int count32 = 0;
      boolean counted32 = false;
      int count64 = 0;
      boolean counted64 = false;
      int count128 = 0;
      boolean counted128 = false;
      int count256 = 0;
      boolean counted256 = false;
      int count512 = 0;
      boolean counted512 = false;
      int count1024 = 0;
      boolean counted1024 = false;
 
 
      buf.append("             ");
      for (int i=0; i < codeExecuted.getSize(); i++) {
        if (i % 2 == 0) {
          counted2 = false;
        }
        if (i % 4 == 0) {
          counted4 = false;
        }
        if (i % 8 == 0) {
          counted8 = false;
        }
        if (i % 16 == 0) {
          counted16 = false;
        }
        if (i % 32 == 0) {
          counted32 = false;
        }
        if (i % 64 == 0) {
          counted64 = false;
        }
        if (i % 128 == 0) {
          counted128 = false;
        }
        if (i % 256 == 0) {
          counted256 = false;
        }
        if (i % 512 == 0) {
          counted512 = false;
        }
        if (i % 1024 == 0) {
          counted1024 = false;
        }
 
        if (codeTouched[i]) {
          buf.append("XX");
          count++;
 
          if (!counted2) {
            count2++;
            counted2 = true;
          }
          if (!counted4) {
            count4++;
            counted4 = true;
          }
          if (!counted8) {
            count8++;
            counted8 = true;
          }
          if (!counted16) {
            count16++;
            counted16 = true;
          }
          if (!counted32) {
            count32++;
            counted32 = true;
          }
          if (!counted64) {
            count64++;
            counted64 = true;
          }
          if (!counted128) {
            count128++;
            counted128 = true;
          }
          if (!counted256) {
            count256++;
            counted256 = true;
          }
          if (!counted512) {
            count512++;
            counted512 = true;
          }
          if (!counted1024) {
            count1024++;
            counted1024 = true;
          }
 
        }
        else {
          buf.append("__");
        }
      }
      buf.append("\n");
      buf.append("Code Bytes touched: ").append(count).append("\n");
      buf.append("Code 2 Byte blocks touched: ").append(count2).append("\n");
      buf.append("Code 4 Byte blocks touched: ").append(count4).append("\n");
      buf.append("Code 8 Byte blocks touched: ").append(count8).append("\n");
      buf.append("Code 16 Byte blocks touched: ").append(count16).append("\n");
      buf.append("Code 32 Byte blocks touched: ").append(count32).append("\n");
      buf.append("Code 64 Byte blocks touched: ").append(count64).append("\n");
      buf.append("Code 128 Byte blocks touched: ").append(count128).append("\n");
      buf.append("Code 256 Byte blocks touched: ").append(count256).append("\n");
      buf.append("Code 512 Byte blocks touched: ").append(count512).append("\n");
      buf.append("Code 1024 Byte blocks touched: ").append(count1024).append("\n");
 
      buf.append("Number of storage locations read: ").append(storageRead.size()).append("\n");
      buf.append("Number of storage locations written: ").append(storageWritten.size()).append("\n");
 
      for (BigInteger location: storageRead.keySet()) {
        buf.append("Storage [").append(location).append("] read ").append(storageRead.get(location)).append(" time(s)").append("\n");
      }
      for (BigInteger location: storageWritten.keySet()) {
        buf.append("Storage [").append(location).append("] written ").append(storageWritten.get(location)).append(" time(s)").append("\n");
      }
      return buf.toString();
    }
 
 
  }
 
 
}
