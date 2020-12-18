package com.mycompany.uno;

/**
 *
 * @author aland
 */
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;
/*
* The deck consists of 108 cards: four each of "Wild" and "Wild Draw Four,"
* and 25 each of four different colors (red, yellow, green, blue). Each color consists of one zero,
* two each of 1 through 9, and two each of "Skip," "Draw Two," and "Reverse." These last three types
* are known as "action cards."
*/

public class Deck {
    private Card[] cards;
    private int cardsInDeck;

    public Deck(){
        cards = new Card[108];
        reset();
    }

    public void reset(){
        //populate an array with possible colors from Color enumerator in Card class
        Card.Color[] colors = Card.Color.values();
        Card.Value[] values = new Card.Value[]{ Card.Value.Skip, Card.Value.Reverse, Card.Value.DrawTwo};
        cardsInDeck = 0;

        for(int i = 0; i < colors.length - 1; i++){
            Card.Color color =  colors[i];

            //make all the 0 cards first (only one of each color in deck)
            cards[cardsInDeck++] = new Card(color, Card.Value.getValue(0));

            for(int j = 1; j < 10; j++){
                //two cards of each number in every color
                cards[cardsInDeck++] = new Card(color,Card.Value.getValue(j));
                cards[cardsInDeck++] = new Card(color,Card.Value.getValue(j));
            }

            //create two of each action card for every color
            for(Card.Value value: values){
                cards[cardsInDeck++] = new Card(color,value);
                cards[cardsInDeck++] = new Card(color,value);
            }
        }
        Card.Value[] wilds = new Card.Value[]{Card.Value.Wild, Card.Value.WildFour};
        for(Card.Value val: wilds){
            for(int i = 0; i < 4; i++){
                cards[cardsInDeck++] = new Card(Card.Color.Wild, val);
            }
        }

    }

    /*
     * @param cards (stockpile)
     * replace current deck with discard pile cards
     */
    public void replaceDeckWith(ArrayList<Card> cards){
        this.cards = cards.toArray(new Card[cards.size()]);
        this.cardsInDeck = this.cards.length;
    }

    public boolean isEmpty(){
        return cardsInDeck == 0;
    }

    public void shuffle(){
        int n = cards.length;
        Random random = new Random();

        for(int i = 0; i < cards.length; i++){
            //get a random index of the array past the current index
            //  the argument is an exclusive bound
            // swap the random value with the current value

            int randomValue = i + random.nextInt(n - i);
            Card randomCard = cards[randomValue];
            cards[randomValue] = cards[i];
            cards[i] = randomCard;
        }
    }

    public Card drawCard() throws IllegalArgumentException{
        if(isEmpty()){
            throw new IllegalArgumentException("Deck is empty can't draw card");
        }
        return cards[--cardsInDeck];
    }

    public Card[] drawCard(int n){
        if(n < 0){
            throw new IllegalArgumentException("Must draw positive # of cards, tried to draw negative");
        }

        if(n > cardsInDeck){
            throw new IllegalArgumentException("Cannot draw that many cards");
        }

        Card[] need = new Card[n];
        for(int i = 0; i < n; i++){
            need[i] = cards[--cardsInDeck];
        }

        return need;
    }

    public ImageIcon drawCardImage() throws  IllegalArgumentException{
        if(isEmpty()){
            throw new IllegalArgumentException("Deck is emopty can't draw card");
        }

        return new ImageIcon(cards[--cardsInDeck].toString() + ".png");
    }

    public int getCardsInDeck() {
        return cardsInDeck;
    }

    public void setCardsInDeck(int cardsInDeck) {
        this.cardsInDeck = cardsInDeck;
    }
}


