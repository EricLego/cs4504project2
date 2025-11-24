import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/**
 * PollClient - Client application for the opinion poll system.
 * 
 * Responsibilities:
 * - Look up PollService from RMI registry
 * - Provide interactive menu for voting
 * - Display current poll results
 * - Handle remote exceptions gracefully
 * 
 * User interface options:
 * 1) Vote YES
 * 2) Vote NO
 * 3) Vote DON'T CARE
 * 4) Show current counts
 * 5) Exit
 */
public class PollClient {
    
    private PollService service;
    private Scanner scanner;
    private String registryHost;
    private int registryPort;
    
    /**
     * Constructor initializes the client.
     */
    public PollClient(String host, int port) {
        this.registryHost = host;
        this.registryPort = port;
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Connects to the RMI registry and looks up PollService.
     */
    public boolean connect() {
        try {
            Registry registry = LocateRegistry.getRegistry(registryHost, registryPort);
            service = (PollService) registry.lookup("PollService");
            System.out.println("Successfully connected to PollService at " + registryHost + ":" + registryPort);
            return true;
        } catch (RemoteException e) {
            System.err.println("RemoteException: Could not connect to server. Is the server running?");
            System.err.println("Details: " + e.getMessage());
            return false;
        } catch (NotBoundException e) {
            System.err.println("NotBoundException: PollService not found in registry.");
            System.err.println("Details: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Displays the main menu and handles user input.
     */
    public void run() {
        if (!connect()) {
            System.out.println("Failed to connect to server. Exiting.");
            scanner.close();
            return;
        }
        
        System.out.println("\n=== Opinion Poll Client ===\n");
        
        boolean running = true;
        while (running) {
            displayMenu();
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    submitVote("YES");
                    break;
                case "2":
                    submitVote("NO");
                    break;
                case "3":
                    submitVote("DONT_CARE");
                    break;
                case "4":
                    showCounts();
                    break;
                case "5":
                    running = false;
                    System.out.println("Thank you for voting. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.\n");
            }
        }
        
        scanner.close();
    }
    
    /**
     * Displays the menu options.
     */
    private void displayMenu() {
        System.out.println("\n=== Poll Menu ===");
        System.out.println("1) Vote YES");
        System.out.println("2) Vote NO");
        System.out.println("3) Vote DON'T CARE");
        System.out.println("4) Show current counts");
        System.out.println("5) Exit");
        System.out.print("Enter your choice (1-5): ");
    }
    
    /**
     * Submits a vote to the server.
     */
    private void submitVote(String choice) {
        try {
            service.submitVote(choice);
            System.out.println("Your vote has been recorded.\n");
        } catch (RemoteException e) {
            System.err.println("RemoteException: Failed to submit vote.");
            System.err.println("Details: " + e.getMessage());
            System.out.println("Please try again.\n");
        }
    }
    
    /**
     * Requests and displays current vote counts.
     */
    private void showCounts() {
        try {
            String counts = service.getCurrentCounts();
            System.out.println("\n" + counts + "\n");
        } catch (RemoteException e) {
            System.err.println("RemoteException: Failed to retrieve counts.");
            System.err.println("Details: " + e.getMessage());
            System.out.println("Please try again.\n");
        }
    }
    
    /**
     * Main method: creates client and starts the interaction loop.
     */
    public static void main(String[] args) {
        String host = "localhost";
        int port = 1099;
        
        // Allow command-line arguments for host and port
        if (args.length > 0) {
            host = args[0];
        }
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port 1099.");
            }
        }
        
        PollClient client = new PollClient(host, port);
        client.run();
    }
}
