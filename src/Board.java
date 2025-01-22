import java.util.ArrayList;
import java.util.List;

class Board {
    private final List<Card> player1Cards;
    private final List<Card> player2Cards;
    private final List<Card> communityCards;

    public Board(List<Card> player1Cards, List<Card> player2Cards) {
        this.player1Cards = player1Cards;
        this.player2Cards = player2Cards;
        this.communityCards = new ArrayList<>();
    }

    public Board(List<Card> player1Cards, List<Card> player2Cards, List<Card> communityCards) {
        this.player1Cards = player1Cards;
        this.player2Cards = player2Cards;
        this.communityCards = communityCards;
    }

    public List<Card> getPlayer1Cards() {
        return player1Cards;
    }

    public List<Card> getPlayer2Cards() {
        return player2Cards;
    }

    public List<Card> getCommunityCards() {
        return communityCards;
    }
}
