class Card {
    private final String rank;
    private final String suit;

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