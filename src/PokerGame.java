import java.util.*;

class PokerGame {
    private final List<Hand> playerHands;
    private final List<Card> communityCards;

    public PokerGame(List<Hand> playerHands, List<Card> communityCards) {
        this.playerHands = playerHands;
        this.communityCards = communityCards;
    }
    public String determineWinner() throws InvalidPokerBoardException {
        validateBoard();
        int highestScore = -1;
        List<Integer> winners = new ArrayList<>();

        for (int i = 0; i < playerHands.size(); i++) {
            Hand hand = playerHands.get(i);
            int score = hand.evaluateHand();

            if (score > highestScore) {
                highestScore = score;
                winners.clear();
                winners.add(i + 1);
            } else if (score == highestScore) {
                winners.add(i + 1);
            }
        }
        if (winners.size() > 1) {
            Card highestCard1 = getHighestCard(playerHands.get(winners.get(0) - 1));
            Card highestCard2 = getHighestCard(playerHands.get(winners.get(1) - 1));
            if (highestCard1.getRank().equals(highestCard2.getRank())) {
                return "Ничья между игроками";
            } else {
                return "Победитель: Игрок " + winners.getFirst();
            }
        } else {
            return "Победитель: Игрок " + winners.getFirst();
        }
    }

    private Card getHighestCard(Hand hand) {
        return hand.getAllCards().stream()
                .max(Comparator.comparingInt(card -> cardValue(card.getRank())))
                .orElseThrow(() -> new IllegalStateException("Не удалось получить старшую карту"));
    }

    private int cardValue(String rank) {
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

    private void validateBoard() throws InvalidPokerBoardException {
        if ((communityCards.size()) != 5) {
            throw new InvalidPokerBoardException("Количество карт на столе должно быть 5 (флоп, терн и ривер).");
        }
        Set<Card> uniqueCards = new HashSet<>(communityCards);
        if (uniqueCards.size() < communityCards.size()) {
            throw new InvalidPokerBoardException("Карты на столе дублируются");
        }
    }
}