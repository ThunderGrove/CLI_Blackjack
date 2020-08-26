import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) throws Exception {
        try (var listener = new ServerSocket(8000)) {
            System.out.println("Tic Tac Toe Server is Running...");
            var pool = Executors.newFixedThreadPool(200);
            while (true) {
                Game game = new Game();
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

    private Player[] board = new Player[2];

    Player currentPlayer;

    Game(){
        for(int i=1;i<=52;i++){
            deck.add(i);
        }
        Collections.shuffle(deck);
    }

    public boolean hasWinner() {
        return (board[0] != null && board[0] == board[1] && board[0] == board[2])
                || (board[3] != null && board[3] == board[4] && board[3] == board[5])
                || (board[6] != null && board[6] == board[7] && board[6] == board[8])
                || (board[0] != null && board[0] == board[3] && board[0] == board[6])
                || (board[1] != null && board[1] == board[4] && board[1] == board[7])
                || (board[2] != null && board[2] == board[5] && board[2] == board[8])
                || (board[0] != null && board[0] == board[4] && board[0] == board[8])
                || (board[2] != null && board[2] == board[4] && board[2] == board[6]);
    }

    public boolean boardFilledUp() {
        return Arrays.stream(board).allMatch(p -> p != null);
    }

    public synchronized void move(int location, Player player) {
        if (player != currentPlayer) {
            throw new IllegalStateException("Not your turn");
        } else if (player.opponent == null) {
            throw new IllegalStateException("You don't have an opponent yet");
        } else if (board[location] != null) {
            throw new IllegalStateException("Cell already occupied");
        }
        board[location] = currentPlayer;
        currentPlayer = currentPlayer.opponent;
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
                processCommands();
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                if(opponent!=null&&opponent.output!=null){
                    opponent.output.println("OTHER_PLAYER_LEFT");
                }
                try{
                    socket.close();
                }catch (IOException e){
                }
            }
        }

        private void setup() throws IOException{
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream(), true);
            output.println("WELCOME");
            hand.add(Game.this.deck.get(topCardIndex));
            topCardIndex++;
        }

        private void processCommands(){
            while(input.hasNextLine()){
                var command=input.nextLine();
                if(command.startsWith("EXIT")) {
                    return;
                }else if(command.startsWith("STAND")){

                }else if(command.startsWith("HIT")){
                    hand.add(Game.this.deck.get(topCardIndex));
                    topCardIndex++;
                }
            }
        }

        private void processHandValue(){

        }

        private void processMoveCommand(int location) {
            try {
                move(location, this);
                output.println("VALID_MOVE");
                opponent.output.println("OPPONENT_MOVED " + location);
                if (hasWinner()) {
                    output.println("VICTORY");
                    opponent.output.println("DEFEAT");
                } else if (boardFilledUp()) {
                    output.println("TIE");
                    opponent.output.println("TIE");
                }
            } catch (IllegalStateException e) {
                output.println("MESSAGE " + e.getMessage());
            }
        }
    }
}