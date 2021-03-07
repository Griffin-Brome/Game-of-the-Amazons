package cosc322.milestone1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;

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
    private boolean verbose = false;

    public AmazonsAIPlayer(String userName, String passwd) {
        setUserName(userName);
        setPassword(passwd);
        setGameGUI(new BaseGameGUI(this));
        setGameBoard(new GameBoard());
    }

    // Second constructor for if we want to pass the verbose parameter
    public AmazonsAIPlayer(String userName, String passwd, boolean verbose) {
        this(userName, passwd);
        this.verbose = verbose;
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
                    gamegui.setGameState((ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.GAME_STATE));
                    gameBoard.setBoardState((ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.GAME_STATE), false);
                    break;

                case GameMessage.GAME_ACTION_START:
                    this.handleStart(msgDetails);
                    break;

                case GameMessage.GAME_ACTION_MOVE:
                    /**
                     * Now the only place conversion from server to local occurs
                     */
                    ArrayList<Integer> queenPosCurr = toLocalFormat((ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR));
                    ArrayList<Integer> queenPosNext = toLocalFormat((ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.Queen_POS_NEXT));
                    ArrayList<Integer> arrowPos = toLocalFormat((ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.ARROW_POS));

                    gameBoard.updateBoard(queenPosCurr, queenPosNext, arrowPos);
                    gamegui.updateGameState(msgDetails);

                    // Now we make a move
                    this.move();

                    break;
            }

        } catch (Exception e) {
            System.out.println("Something went wrong handling a game message from the server:");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Handles game start message and sets the isWhitePlayer flag.
     * If so, perform move.
     *
     * @param msgDetails
     */
    public void handleStart(Map<String, Object> msgDetails) {
        if (msgDetails.get(AmazonsGameMessage.PLAYER_BLACK).equals(this.userName)) {
            this.isWhitePlayer = false;
        } else if (msgDetails.get(AmazonsGameMessage.PLAYER_WHITE).equals(this.userName)) {
            this.isWhitePlayer = true;
            this.move();
        }
    }

    /**
     * Does not currently work as expected, game loop should be replaced when proper search strategy is implemented.
     */
    public void move() {
        if (verbose) {
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() < start + 5000) ;
        }

        ActionFactoryRecursive af = new ActionFactoryRecursive(gameBoard);
        ArrayList<Move> possibleMoves = af.getPossibleMoves(isWhitePlayer);

        if (possibleMoves.isEmpty()) {
            String player = isWhitePlayer ? "White Player" : "Black Player";
            System.out.println("Game over... " + player);
        } else {
            ArrayList<Integer> oldPosList = new ArrayList<>();
            ArrayList<Integer> newPosList = new ArrayList<>();
            ArrayList<Integer> arrowPosList = new ArrayList<>();

            Move move = randomMove(possibleMoves); // pick move and remove it

            byte[] oldPos = move.getOldPos();
            byte[] newPos = move.getNewPos();
            byte[] arrowPos = move.getArrowPos();

            // old position of the moving queen
            oldPosList.add((int) oldPos[0]);
            oldPosList.add((int) oldPos[1]);

            // add to appropriate arrayList
            newPosList.add((int) newPos[0]); // new pos
            newPosList.add((int) newPos[1]);

            // add to arraylist for server message
            arrowPosList.add((int) arrowPos[0]); // arrow position
            arrowPosList.add((int) arrowPos[1]);

            gameBoard.updateBoard(oldPosList, newPosList, arrowPosList);
            gamegui.updateGameState(oldPosList, newPosList, arrowPosList);

            /**
             * Only place where we have to convert to server format of (y, x) and 1 indexed is now here.
             */
            oldPosList = toServerFormat(oldPosList);
            newPosList = toServerFormat(newPosList);
            arrowPosList = toServerFormat(arrowPosList);

            gameClient.sendMoveMessage(oldPosList, newPosList, arrowPosList);
        }
    }

    /**
     * Convert from 0 indexed (x, y) to 1 indexed (y, x)
     *
     * @param pos
     * @return
     */
    public ArrayList<Integer> toServerFormat(ArrayList<Integer> pos) {
        ArrayList<Integer> serverPos = new ArrayList<>();
        serverPos.add(pos.get(1) + 1);
        serverPos.add(pos.get(0) + 1);
        return serverPos;
    }

    /**
     * Convert from 1 indexed (y, x) coordinates to 0 indexed (x, y)
     *
     * @param serverPos
     * @return
     */
    public ArrayList<Integer> toLocalFormat(ArrayList<Integer> serverPos) {
        ArrayList<Integer> pos = new ArrayList<>();
        pos.add(serverPos.get(1) - 1);
        pos.add(serverPos.get(0) - 1);
        return pos;
    }

    /**
     * Random move picked from list - old
     *
     * @param positions
     * @return
     */
    public Move randomMove(ArrayList<Move> positions) {
        int idx = (int) (Math.random() * positions.size());
        Move pos = positions.get(idx);
        positions.remove(idx);
        return pos;
    }

    @Override
    public String userName() {
        return userName;
    }

    @Override
    public GameClient getGameClient() {
        // TODO Auto-generated method stub
        return this.gameClient;
    }

    @Override
    public BaseGameGUI getGameGUI() {
        // TODO Auto-generated method stub
        return this.gamegui;
    }

    @Override
    public void connect() {
        gameClient = new GameClient(userName, passwd, (GamePlayer) this);
    }

    public boolean handleMessage(String type, String msg) {
        return true;
    }

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
