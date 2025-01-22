import java.util.*;
import java.util.stream.Collectors;

class Hand {
    private final List<Card> playerCards;
    private final List<Card> communityCards;

    public Hand(List<Card> playerCards, List<Card> communityCards) {
        this.playerCards = playerCards;
        this.communityCards = communityCards;
    }

    List<Card> getAllCards() {
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
