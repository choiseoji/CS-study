public class ClientConnection implements Closeable {

   // ...

   public ClientConnection(Socket socket) throws IOException {
      this.socket = socket;
      this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.writer = new PrintWriter(socket.getOutputStream(), true);
    }

   @Override
   public void close() throws IOException {
      try (Writer writer = this.writer; Reader reader = this.reader; Socket socket = this.socket) {
         // resources all closed when this block exits
      }
   }
}