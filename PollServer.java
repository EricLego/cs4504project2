import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * PollServer - Implements the remote poll service.
 * 
 * Responsibilities:
 * 1. Maintains poll counts in memory (yes, no, don't care)
 * 2. Enforces mutual exclusion on vote updates (synchronized methods)
 * 3. Binds to RMI registry for client access
 * 
 * Design notes:
 * - Vote counts stored as transient integers (lost when server stops)
 * - synchronized methods ensure thread-safety for concurrent RMI calls
 * - RMI creates a separate thread per remote call, so synchronization is needed
 */
public class PollServer extends UnicastRemoteObject implements PollService {
    
    private static final long serialVersionUID = 1L;
    
    // Vote counters - protected by synchronized methods
    private int yesCount = 0;
    private int noCount = 0;
    private int dontCareCount = 0;
    
    /**
     * Constructor must declare RemoteException.
     */
    public PollServer() throws RemoteException {
        super();
    }
    
    /**
     * Submits a vote. Synchronized to prevent concurrent modifications.
     * 
     * @param choice One of: "YES", "NO", "DONT_CARE" (case-insensitive)
     * @throws RemoteException if remote communication fails
     */
    @Override
    public synchronized void submitVote(String choice) throws RemoteException {
        if (choice == null) {
            System.out.println("Invalid vote: null");
            return;
        }
        
        String normalized = choice.toUpperCase().trim();
        
        switch (normalized) {
            case "YES":
                yesCount++;
                System.out.println("Vote recorded: YES (Total: " + yesCount + ")");
                break;
            case "NO":
                noCount++;
                System.out.println("Vote recorded: NO (Total: " + noCount + ")");
                break;
            case "DONT_CARE":
                dontCareCount++;
                System.out.println("Vote recorded: DON'T CARE (Total: " + dontCareCount + ")");
                break;
            default:
                System.out.println("Invalid vote ignored: " + choice);
        }
    }
    
    /**
     * Returns current vote counts. Synchronized to ensure consistent snapshot.
     * 
     * @return Formatted string with current tallies
     * @throws RemoteException if remote communication fails
     */
    @Override
    public synchronized String getCurrentCounts() throws RemoteException {
        return String.format(
            "Current Counts: %d yes, %d no, %d don't care",
            yesCount, noCount, dontCareCount
        );
    }
    
    /**
     * Main method: starts the RMI registry and binds the service.
     */
    public static void main(String[] args) {
        try {
            // Create RMI registry on port 1099 (default)
            int registryPort = 1099;
            LocateRegistry.createRegistry(registryPort);
            System.out.println("RMI Registry created on port " + registryPort);
            
            // Create the poll service instance
            PollServer service = new PollServer();
            System.out.println("PollServer instance created");
            
            // Bind to registry
            String bindName = "PollService";
            java.rmi.Naming.rebind("rmi://localhost:" + registryPort + "/" + bindName, service);
            System.out.println("PollService bound to registry as '" + bindName + "'");
            
            System.out.println("\nServer is running. Waiting for client connections...");
            System.out.println("Press Ctrl+C to stop the server.\n");
            
            // Keep the server running
            Object lock = new Object();
            synchronized (lock) {
                lock.wait();
            }
            
        } catch (RemoteException e) {
            System.err.println("RMI error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
