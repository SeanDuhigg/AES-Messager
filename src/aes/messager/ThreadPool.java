package aes.messager;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.*;
import java.io.*;
/**
 *
 * @author Sean
 */

class ThreadPool implements Runnable {
    
    private final ServerSocket incSocket;    // Create a server socket.
    private final ExecutorService pool; 
    private volatile Boolean stop = false;  
    
    public ThreadPool(int port, int poolSize) throws IOException{
        
        incSocket = new ServerSocket(port);
        pool = Executors.newFixedThreadPool(poolSize);
        
    }
    
    public void run(){
                
        try {
            for(;;){                 
                pool.execute(new ClientHandler(incSocket.accept()));
            }
        }catch(IOException e){
            pool.shutdown();
        }
    }
    
    public void stopService()
{
        stop = true;  // Mark the service as stopped.

        // Try to stop the blocking accept call.
        try {
                incSocket.close();
        } catch (IOException e) {
                e.printStackTrace();
        }

        System.out.print("Shutting down the service...");
        // Shutdown the pool.
        pool.shutdown();
        try{
                // Wait upto a minute for the pool to shutdown.
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                {
                        pool.shutdownNow(); // Force destroy the pool.

                        // Wait upto a minute for threads to handle the cancel.
                        if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                                System.err.println("Pool did not gracefully stop.");
                }
        } catch (InterruptedException ie) {
                pool.shutdownNow();
                Thread.currentThread().interrupt();
        }

        System.out.println(" [ DONE ]");
}
}
