import java.util.*;
import java.util.stream.Collectors;

public class pokerok {
    private static final List<String> SUIT = Arrays.asList("C", "D", "H", "S");
    private static final List<String> VALUE = Arrays.asList("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A");
    private static final Map<String, Integer> RANKING = new HashMap<>();
    private List<String> deck;

    static {
        for (int i = 0; i < VALUE.size(); i++) {
            RANKING.put(VALUE.get(i), i);
        }
    }

    public static void main(String[] args) {
        pokerok game = new pokerok();
        game.startGame();
    }

    public interface Dealer {
        Board dealCardsToPlayers();
        Board dealFlop(Board board);
        Board dealTurn(Board board);
        Board dealRiver(Board board);
        String decideWinner(Board board) throws InvalidPokerBoardException;
    }

    private List<String> createDeck() {
        List<String> DECK = new ArrayList<>();
        for (String suit : SUIT) {
            for (String nominal : VALUE) {
                DECK.add(nominal + suit);
            }
        }
        Collections.shuffle(DECK);
        return DECK;
    }

    private class PokerDealer implements Dealer {
        @Override
        public Board dealCardsToPlayers() {
            List<Card> player1Cards = Arrays.asList(new Card(deck.removeFirst()), new Card(deck.removeFirst()));
            List<Card> player2Cards = Arrays.asList(new Card(deck.removeFirst()), new Card(deck.removeFirst()));
            return new Board(player1Cards, player2Cards);
        }

        @Override
        public Board dealFlop(Board board) {
            List<Card> communityCards = new ArrayList<>(board.getCommunityCards());
            for (int i = 0; i < 3; i++) {
                communityCards.add(new Card(deck.removeFirst()));
            }
            return new Board(board.getPlayer1Cards(), board.getPlayer2Cards(), communityCards);
        }

        @Override
        public Board dealTurn(Board board) {
            List<Card> communityCards = new ArrayList<>(board.getCommunityCards());
            communityCards.add(new Card(deck.removeFirst()));
            return new Board(board.getPlayer1Cards(), board.getPlayer2Cards(), communityCards);
        }

        @Override
        public Board dealRiver(Board board) {
            List<Card> communityCards = new ArrayList<>(board.getCommunityCards());
            communityCards.add(new Card(deck.removeFirst()));
            return new Board(board.getPlayer1Cards(), board.getPlayer2Cards(), communityCards);
        }

        @Override
        public String decideWinner(Board board) throws InvalidPokerBoardException {
            Hand player1Hand = new Hand(board.getPlayer1Cards(), board.getCommunityCards());
            Hand player2Hand = new Hand(board.getPlayer2Cards(), board.getCommunityCards());

            PokerGame pokerGame = new PokerGame(Arrays.asList(player1Hand, player2Hand), board.getCommunityCards());
            return pokerGame.determineWinner();
        }
    }

    public void startGame() {
        deck = createDeck();
        PokerDealer dealer = new PokerDealer();

        Board board = dealer.dealCardsToPlayers();
        System.out.println("Игрок 1: " + board.getPlayer1Cards());
        System.out.println("Игрок 2: " + board.getPlayer2Cards());

        board = dealer.dealFlop(board);
        System.out.println("Флоп: " + board.getCommunityCards());

        board = dealer.dealTurn(board);
        System.out.println("Терн: " + board.getCommunityCards().get(3));

        board = dealer.dealRiver(board);
        System.out.println("Ривер: " + board.getCommunityCards().get(4));

        try {
            String result = String.valueOf(dealer.decideWinner(board));
            System.out.println(result);
        } catch (InvalidPokerBoardException e) {
            System.err.println(e.getMessage());
        }
    }

//    static class PokerResult {
//        private final int winnerIndex;
//
//        public PokerResult(int winnerIndex) {
//            this.winnerIndex = winnerIndex;
//        }
//
//        public int getWinnerIndex() {
//            return winnerIndex;
//        }
//    }
}