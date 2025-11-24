import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for the opinion poll system.
 * 
 * Defines the operations accessible to clients:
 * - submitVote: Accepts a vote choice (YES, NO, DONT_CARE)
 * - getCurrentCounts: Returns current vote tallies
 * 
 * All methods throw RemoteException as required by RMI contract.
 */
public interface PollService extends Remote {

    /**
     * Submits a vote to the poll.
     * 
     * @param choice One of: "YES", "NO", "DONT_CARE" (case-insensitive)
     * @throws RemoteException if remote communication fails
     */
    void submitVote(String choice) throws RemoteException;

    /**
     * Retrieves the current vote counts.
     * 
     * @return A formatted string showing current tally
     * @throws RemoteException if remote communication fails
     */
    String getCurrentCounts() throws RemoteException;
}
