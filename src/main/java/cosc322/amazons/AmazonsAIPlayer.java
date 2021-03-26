package cosc322.amazons;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import decision.logic.AlphaBetaExperiment;
import decision.logic.AlphaBetaSearch;
import models.Move;
import models.RootMove;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;

import static utils.Constant.N;


/**
 * Basic AI player class
 *
 * @author Group 21
 */
public class AmazonsAIPlayer extends GamePlayer {

    private boolean isWhitePlayer = false;
    private GameClient gameClient = null;
    private BaseGameGUI gamegui = null;
    private GameBoard gameBoard = null;

    private String userName = null;
    private String passwd = null;
    private int delay = 0;
    private int turnNumber;
    private int goHard = 1;

    int iterativeDeepeningAlpha = 25;
    int territoryDepthAlpha = 15;
    private int upper;
    private byte territoryDepth;

    public AmazonsAIPlayer(String userName, String passwd) {
        setUserName(userName);
        setPassword(passwd);
        setGameGUI(new BaseGameGUI(this));
        turnNumber = 1;
    }

    // Second constructor for if we want to pass the delay parameter
    public AmazonsAIPlayer(String userName, String passwd, int delay) {
        this(userName, passwd);
        this.delay = delay;
    }

    /**
     * Login event handlers
     */
    @Override
    public void onLogin() {
        System.out.println("Login Successful!\n");
        String username = gameClient.getUserName();
        setUserName(username);

        if (this.getGameGUI() != null) {
            this.getGameGUI().setRoomInformation(this.getGameClient().getRoomList());
        } else {
            System.err.println("Error: Could not load game UI");
            // Will break out of program since this method is void
        }
    }

    /**
     * Sets the players username
     *
     * @param userName Must not be null, further constraints may need to be added
     */
    public void setUserName(String userName) {
        if (userName.isEmpty()) {
            System.err.println("Error: Username cannot be empty");
        } else {
            this.userName = userName;
        }
    }

    /**
     * Handles the varying messages from the server
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
        try {
            switch (messageType) {
                // set gui/board
                case GameMessage.GAME_STATE_BOARD:
                    // initialize game board here so we can simply join a new room to play a new game without restarting the bot
                    turnNumber = 1;
                    setGameBoard(new GameBoard());
                    gamegui.setGameState((ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.GAME_STATE));
                    gameBoard.setBoardState((ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.GAME_STATE), false);
                    break;

                case GameMessage.GAME_ACTION_START:
                    long start = System.currentTimeMillis();
                    this.handleStart(msgDetails);
                    System.out.println("Move Time: " + (System.currentTimeMillis() - start));
                    break;

                case GameMessage.GAME_ACTION_MOVE:
                    /**
                     * Now the only place conversion from server to local occurs
                     */
                    byte[] queenPosCurr = toLocalFormat((ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR));
                    byte[] queenPosNext = toLocalFormat((ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.Queen_POS_NEXT));
                    byte[] arrowPos = toLocalFormat((ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.ARROW_POS));

                    gameBoard.updateBoard(queenPosCurr, queenPosNext, arrowPos);
                    gamegui.updateGameState(msgDetails);

                    // Now we make a move
                    start = System.currentTimeMillis();
                    move();
                    System.out.println("Turn Number: " + turnNumber + "\tMove Time: " + (System.currentTimeMillis() - start));
                    break;
            }

        } catch (Exception e) {
            System.out.println("Something went wrong handling a game message from the server:");
            e.printStackTrace();
        }
        return false;
    }

    public void setTuningParameters(int turnNumber, int moveSize) {
        this.goHard = 1 + turnNumber / 4;
        this.upper = 4;

        if (moveSize < 150) {
            this.upper = 8;
        }

        this.territoryDepth = (byte) (2 + turnNumber / territoryDepthAlpha);
    }

    /**
     * Handles game start message and sets the isWhitePlayer flag.
     * If so, perform move.
     *
     * @param msgDetails
     */
    public void handleStart(Map<String, Object> msgDetails) throws ExecutionException, InterruptedException {
        if (msgDetails.get(AmazonsGameMessage.PLAYER_WHITE).equals(this.userName)) {
            this.isWhitePlayer = true;
        } else if (msgDetails.get(AmazonsGameMessage.PLAYER_BLACK).equals(this.userName)) {
            this.isWhitePlayer = false;
            move();
        }
    }

    /**
     * Game DecisionLogic needs to be implemented here, that class should implement AlphaBeta
     */
    public void move() throws ExecutionException, InterruptedException {
        int numThreads = 4;
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() < start + delay) ;

        ActionFactory af = new ActionFactory(gameBoard, isWhitePlayer);
        ArrayList<Move> possibleMoves = af.getPossibleMoves();

        if (possibleMoves.isEmpty()) {
            String player = isWhitePlayer ? "White Player" : "Black Player";
            System.out.println("Game over for " + player);
        } else {
            Move move = possibleMoves.get(0);
            setTuningParameters(turnNumber, possibleMoves.size());
            RootMove root = new RootMove();
            root.addAllChildMove(possibleMoves);

            if(false) { // possibleMoves.size() < numThreads * 10
                List<ArrayList<Move>> possibleMovesList = new ArrayList<>();
                for (int i = 1; i < numThreads + 1; i++) {
                    possibleMovesList.add(new ArrayList<>(possibleMoves.subList(possibleMoves.size() / numThreads * (i - 1), possibleMoves.size() / numThreads * i)));
                }

                boolean waitonce = false;
                for (int i = 1; i < upper; i++) {
                    ExecutorService pool = Executors.newFixedThreadPool(numThreads);
                    List<AlphaBetaSearch> abList = new ArrayList<>();
                    List<Future<Move>> futureList = new ArrayList<>();
                    List<Move> bestMoves = new ArrayList<>();

                    for (int j = 0; j < numThreads; j++) {
                        abList.add(new AlphaBetaSearch(gameBoard, i, isWhitePlayer, this.goHard, this.territoryDepth, possibleMovesList.get(j)));
                        futureList.add(pool.submit(abList.get(j)));

                    }

                    if (!waitonce) {
                        pool.awaitTermination(23, TimeUnit.SECONDS);
                        waitonce = true;
                    }
                    for (int k = 0; k < numThreads; k++) {
                        bestMoves.add(futureList.get(k).get());
                    }

                    int bestScore = Integer.MIN_VALUE;
                    for (Move m : bestMoves) {
                        if (m.getScore() > bestScore) {
                            move = m;
                            bestScore = m.getScore();
                        }
                    }
                    System.out.println("Check |\tUpper current: " + i + "\tTerritory Depth: " + territoryDepth);
                    pool.shutdown();
                }
            } else {
                for(int i = 1; i < upper; i++) {
//                    AlphaBetaSearch ab = new AlphaBetaSearch(gameBoard, i, isWhitePlayer, this.goHard, this.territoryDepth, possibleMoves);
                    System.out.println("UPPER: " + i);
                    AlphaBetaExperiment ab = new AlphaBetaExperiment(gameBoard, i, isWhitePlayer, this.goHard, this.territoryDepth, start, root);
                    Move temp = ab.getBestMove();
                    if(temp != null) {
                        move = temp;
                    }
                }

//                for (Move m: root.getChildMoves().get(0).getChildMoves()) {
//                    System.out.println(m);
//                }
            }

            byte[] oldPos = move.getOldPos();
            byte[] newPos = move.getNewPos();
            byte[] arrowPos = move.getArrowPos();

            // IMPORTANT: update board before converting to server format
            gameBoard.updateBoard(oldPos, newPos, arrowPos);

            // Only place where we have to convert to server format of (y, x) and 1 indexed is now here.
            ArrayList<Integer> oldPosList = toServerFormat(oldPos);
            ArrayList<Integer> newPosList = toServerFormat(newPos);
            ArrayList<Integer> arrowPosList = toServerFormat(arrowPos);

            // IMPORTANT: update gui after converting to server format
            gamegui.updateGameState(oldPosList, newPosList, arrowPosList);

            gameClient.sendMoveMessage(oldPosList, newPosList, arrowPosList);
            ++turnNumber;
        }
    }

    /**
     * Convert from matrix format (i.e. [row, col] 0 indexed) to server format
     *
     * @param localPos
     * @return
     */
    public ArrayList<Integer> toServerFormat(byte[] localPos) {
        ArrayList<Integer> serverPos = new ArrayList<>();
        // for the row, we set it to be N - localPos[0] to handle the conversion
        serverPos.add(N - localPos[0]);
        // column just needs to have 1 added to it
        serverPos.add(localPos[1] + 1);
        return serverPos;
    }

    /**
     * Convert from server format to matrix format (i.e. [row, col] 0 indexed)
     *
     * @param serverPos
     * @return
     */
    public byte[] toLocalFormat(ArrayList<Integer> serverPos) {
        // for the row, we set it to be N - serverPos[0] to handle the conversion
        // column just needs to have 1 subtracted from it
        return new byte[]{(byte) (N - serverPos.get(0)), (byte) (serverPos.get(1) - 1)};
    }

    public String userName() {
        return userName;
    }

    public GameClient getGameClient() {
        return this.gameClient;
    }

    public BaseGameGUI getGameGUI() {
        return this.gamegui;
    }

    public void connect() {
        gameClient = new GameClient(userName, passwd, this);
    }

    //TODO: Not too sure what these do but we should implement them properly
    public boolean handleMessage(String type, String msg) {
        return true;
    }

    //TODO: Not too sure what these do but we should implement them properly
    public boolean handleMessage(String type) {
        return true;
    }

    public void setPassword(String pw) {
        this.passwd = pw;
    }

    public boolean isWhitePlayer() {
        return this.isWhitePlayer;
    }

    public void setIsWhitePlayer(boolean isWhitePlayer) {
        this.isWhitePlayer = isWhitePlayer;
    }

    public void setGameGUI(BaseGameGUI gui) {
        this.gamegui = gui;
    }

    public void setGameBoard(GameBoard board) {
        this.gameBoard = board;
    }
}
