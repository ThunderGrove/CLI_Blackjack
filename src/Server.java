import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) throws Exception {
        try (var listener = new ServerSocket(8000)) {
            System.out.println("Broken blackjack");
            var pool=Executors.newFixedThreadPool(200);
            while(true){
                Game game=new Game();
                pool.execute(game.new Player(listener.accept()));
                pool.execute(game.new Player(listener.accept()));
            }
        }
    }
}

class Game{
    private ArrayList<Integer> deck=new ArrayList<Integer>();
    private int topCardIndex=0;

    private String[] cardNames={
            "Null",
            "Hjerter es",
            "Hjerter 2",
            "Hjerter 3",
            "Hjerter 4",
            "Hjerter 5",
            "Hjerter 6",
            "Hjerter 7",
            "Hjerter 8",
            "Hjerter 9",
            "Hjerter 10",
            "Hjerter knægt",
            "Hjerter dame",
            "Hjerter konge",
            "Ruder es",
            "Ruder 2",
            "Ruder 3",
            "Ruder 4",
            "Ruder 5",
            "Ruder 6",
            "Ruder 7",
            "Ruder 7",
            "Ruder 8",
            "Ruder 9",
            "Ruder 10",
            "Ruder knægt",
            "Ruder dame",
            "Ruder konge",
            "Spar es",
            "Spar 2",
            "Spar 3",
            "Spar 4",
            "Spar 5",
            "Spar 6",
            "Spar 7",
            "Spar 8",
            "Spar 9",
            "Spar 10",
            "Spar knægt",
            "Spar dame",
            "Spar konge",
            "Klør es",
            "Klør 2",
            "Klør 3",
            "Klør 4",
            "Klør 5",
            "Klør 6",
            "Klør 7",
            "Klør 7",
            "Klør 8",
            "Klør 9",
            "Klør 10",
            "Klør knægt",
            "Klør dame",
            "Klør konge"
    };

    private Player[] board=new Player[2];

    Player currentPlayer;

    Game(){
        for(int i=1;i<=52;i++){
            deck.add(i);
        }
        Collections.shuffle(deck);
    }

    class Player implements Runnable{
        ArrayList<Integer>hand=new ArrayList<Integer>();
        Player opponent;
        Socket socket;
        Scanner input;
        PrintWriter output;

        public Player(Socket socket) {
            this.socket=socket;
        }

        @Override
        public void run(){
            try{
                setup();
                output.println("TEST");
                processCommands();
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                if(opponent!=null&&opponent.output!=null){
                    opponent.output.println("OTHER_PLAYER_LEFT");
                }
                try{
                    socket.close();
                }catch(IOException e){
                }
            }
        }

        private void setup()throws IOException{
            input=new Scanner(socket.getInputStream());
            output=new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Player connecting and adding first card to hand");
            hand.add(Game.this.deck.get(topCardIndex));
            topCardIndex++;
            System.out.println(cardNames[hand.get(0)]);
        }

        private void processCommands(){
            System.out.println("Waiting for input");
            while(input.hasNextLine()){
                System.out.println("Input received");
                var command=input.nextLine();
                if(command.startsWith("EXIT")){
                    return;
                }else if(command.startsWith("STAND")){

                }else if(command.startsWith("HIT")){
                    hand.add(Game.this.deck.get(topCardIndex));
                    topCardIndex++;
                    System.out.println(hand.get(0));
                }
            }
        }

        private void processHandValue(){

        }
    }
}