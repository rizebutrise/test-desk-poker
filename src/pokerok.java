import java.util.*;
import java.util.stream.Collectors;
public class pokerok {
    private static final List<String> SUIT = Arrays.asList("C", "D", "H", "S");
    private static final List<String> VALUE = Arrays.asList("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A");
    private static final Map<String, Integer> RANKING = new HashMap<>();
    static {
        for (int i = 0; i < VALUE.size(); i++) {
            RANKING.put(VALUE.get(i), i);
        }
    }
    public static void main(String[] args) {
        pokerok game = new pokerok();
        game.startGame();
    }
    private List<String> createDeck() {
        List<String> DECK = new ArrayList<>(); // создание колоды
        for (String suit : SUIT) {
            for (String nominal : VALUE) {
                DECK.add(nominal + suit); // формирование каждой карты (номинал и масть)
            }
        }
        Collections.shuffle(DECK); // перемешивание колоды
        return DECK;
    }
    public void startGame() {
        List<String> DECK = createDeck();

        // выдача карт в руки
        List<Card> player1Cards = Arrays.asList(new Card(DECK.removeFirst()), new Card(DECK.removeFirst()));
        List<Card> player2Cards = Arrays.asList(new Card(DECK.removeFirst()), new Card(DECK.removeFirst()));
        System.out.println("Игрок 1: " + player1Cards);
        System.out.println("Игрок 2: " + player2Cards);

        // выдача карт на стол
        List<Card> communityCards = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            communityCards.add(new Card(DECK.removeFirst()));
        }
        System.out.println("Флоп: " + communityCards);

        Card turn = new Card(DECK.removeFirst());
        System.out.println("Терн: " + turn);

        Card river = new Card(DECK.removeFirst());
        System.out.println("Ривер: " + river);

        // определяем победителя
        Hand player1Hand = new Hand(player1Cards, communityCards);
        Hand player2Hand = new Hand(player2Cards, communityCards);

        PokerGame pokerGame = new PokerGame(Arrays.asList(player1Hand, player2Hand), communityCards);
        try {
            int winnerIndex = pokerGame.determineWinner();
            System.out.println("Победитель: Игрок " + winnerIndex);
        } catch (InvalidPokerBoardException e) {
            System.err.println(e.getMessage());
        }
    }
    static class Card {
        private final String rank; // имеет значения от 2 до туза
        private final String suit; // имеет значения c d h s
        public Card(String card) {
            this.rank = card.substring(0, card.length() - 1);
            this.suit = card.substring(card.length() - 1);
        }
        public String getRank() {
            return rank;
        }
        public String getSuit() {
            return suit;
        }
        @Override
        public String toString() {
            return rank + suit;
        }
    }
    static class Hand {
        private final List<Card> playerCards;
        private final List<Card> communityCards;

        public Hand(List<Card> playerCards, List<Card> communityCards) {
            this.playerCards = playerCards;
            this.communityCards = communityCards;
        }
        private List<Card> getAllCards() {
            List<Card> allCards = new ArrayList<>(playerCards);
            allCards.addAll(communityCards);
            return allCards;
        }
        private Map<String, Integer> countRanks() {
            Map<String, Integer> counts = new HashMap<>();
            for (Card card : getAllCards()) {
                counts.put(card.getRank(), counts.getOrDefault(card.getRank(), 0) + 1);
            }
            return counts;
        }
        private boolean isFlush() {
            String suit = getAllCards().getFirst().getSuit();
            return getAllCards().stream().allMatch(card -> card.getSuit().equals(suit));
        }
        private boolean isStraight() {
            Set<Integer> uniqueRanks = new HashSet<>();
            for (Card card : getAllCards()) {
                uniqueRanks.add(getCardValue(card.getRank()));
            }
            if (uniqueRanks.size() < 5) return false;
            int maxRank = Collections.max(uniqueRanks);
            int minRank = Collections.min(uniqueRanks);
            return (maxRank - minRank == 4);
        }
        private boolean isThreeOfAKind() {
            Map<String, Integer> counts = countRanks();
            return counts.containsValue(3);
        }
        private boolean isTwoPair() {
            Map<String, Integer> counts = countRanks();
            return Collections.frequency(new ArrayList<>(counts.values()), 2) == 2;
        }
        private boolean isOnePair() {
            Map<String, Integer> counts = countRanks();
            return counts.containsValue(2);
        }
        private boolean isFullHouse() {
            Map<String, Integer> counts = countRanks();
            return counts.containsValue(3) && counts.containsValue(2);
        }
        private boolean isFourOfAKind() {
            Map<String, Integer> counts = countRanks();
            return counts.containsValue(4);
        }
        private boolean isStraightFlush() {
            return isFlush() && isStraight();
        }
        private boolean isRoyalFlush() {
            if (!isFlush()) return false;
            List<String> royalRanks = Arrays.asList("10", "J", "Q", "K", "A");
            return getAllCards().stream().map(Card::getRank).collect(Collectors.toSet()).containsAll(royalRanks);
        }
        private int getCardValue(String rank) {
            return switch (rank) {
                case "2" -> 2;
                case "3" -> 3;
                case "4" -> 4;
                case "5" -> 5;
                case "6" -> 6;
                case "7" -> 7;
                case "8" -> 8;
                case "9" -> 9;
                case "10" -> 10;
                case "J" -> 11;
                case "Q" -> 12;
                case "K" -> 13;
                case "A" -> 14;
                default -> throw new IllegalArgumentException("Invalid rank");
            };
        }
        public int evaluateHand() {
            if (isRoyalFlush()) return 10; // Флеш-рояль
            if (isStraightFlush()) return 9; // Стрит-флеш
            if (isFourOfAKind()) return 8; // Каре
            if (isFullHouse()) return 7; // Фулл Хаус
            if (isFlush()) return 6; // Флеш
            if (isStraight()) return 5; // Стрит
            if (isThreeOfAKind()) return 4; // Тройка
            if (isTwoPair()) return 3; // Две пары
            if (isOnePair()) return 2; // Пара
            return 1; // Высшая карта
        }
    }
    static class PokerGame {
        private final List<Hand> playerHands;
        private final List<Card> communityCards;
        public PokerGame(List<Hand> playerHands, List<Card> communityCards) {
            this.playerHands = playerHands;
            this.communityCards = communityCards;
        }
        public int determineWinner() throws InvalidPokerBoardException {
            validateBoard();
            Hand winningHand = null;
            int highestScore = -1;
            int winnerIndex = -1;
            for (int i = 0; i < playerHands.size(); i++) {
                Hand hand = playerHands.get(i);
                int score = hand.evaluateHand();
                if (score > highestScore) {
                    highestScore = score;
                    winningHand = hand;
                    winnerIndex = i + 1;
                }
            }
            return winnerIndex;
        }
        private void validateBoard() throws InvalidPokerBoardException {
            if ((communityCards.size() + 2) != 5) {
                throw new InvalidPokerBoardException("Количество карт на столе должно быть 5 (флоп, терн и ривер).");
            }
            Set<Card> uniqueCards = new HashSet<>(communityCards);
            if (uniqueCards.size() < communityCards.size()) {
                throw new InvalidPokerBoardException("Карты на столе дублируются!");
            }
        }

    }
    static class InvalidPokerBoardException extends Exception {
        public InvalidPokerBoardException(String message) {
            super(message);
        }
    }
}