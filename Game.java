
package com.mycompany.uno;

/**
 *
 * @author aland
 */
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Game {
    private int currentPlayer;
    private String[] playerIds;

    private Deck deck;

    //Keep track of all players hands
    private ArrayList<ArrayList<Card>> playerHands;
    private ArrayList<Card> stockPile;

    private Card.Color validColor;
    private Card.Value validValue;

    boolean gameDirection;

    public Game(String[] playerIds){
        this.playerIds = playerIds;
        deck = new Deck();
        deck.shuffle();
        stockPile = new ArrayList<Card>();

        currentPlayer = 0;
        gameDirection = false;

        playerHands = new ArrayList<ArrayList<Card>>();

        for(int i = 0; i < playerIds.length; i++){
            ArrayList<Card> hand = new ArrayList<Card>(Arrays.asList(deck.drawCard(7)));
            playerHands.add(hand);
        }
    }

    public void start(Game game){
        Card card = deck.drawCard();
        validColor = card.getColor();
        validValue = card.getValue();

        if(card.getValue() == Card.Value.Wild || card.getValue() == Card.Value.DrawTwo || card.getValue() == Card.Value.WildFour){
            start(game);
        }

        if(card.getValue() == Card.Value.Skip){
            JLabel message = new JLabel(playerIds[currentPlayer] + " was skipped!");
            message.setFont(new Font("Arial", Font.BOLD, 48));
            JOptionPane.showMessageDialog(null, message);

            if(gameDirection == false){
                currentPlayer = (currentPlayer + 1) % playerIds.length;
            }else if(gameDirection == true){
                currentPlayer = (currentPlayer - 1) % playerIds.length;
                if(currentPlayer == -1){
                    currentPlayer = playerIds.length - 1;
                }
            }
        }

        if(card.getValue() == Card.Value.Reverse){
            JLabel message = new JLabel(playerIds[currentPlayer] + " Direction was reversed!");
            message.setFont(new Font("Arial", Font.BOLD, 48));
            JOptionPane.showMessageDialog(null, message);
            gameDirection ^= true;
            currentPlayer = playerIds.length - 1;
        }

        stockPile.add(card);
    }

    public Card getTopCard(){
        return new Card(validColor,validValue);
    }

    public ImageIcon getTopCardImage(){
        return new ImageIcon(validColor + "_" + validValue + ".png");
    }

    public boolean isGameOver(){
      for(String player : playerIds){
           //if(hasEmptyHand(player)){
             //   return true;
           //}
      }
      return false;
    }

    public String getCurrentPlayer(){
        return playerIds[currentPlayer];
    }

    public String getPreviousPlayer(int i){
        int index = currentPlayer - 1;

        if(index == -1){
            index = playerIds.length -1;
        }
        return playerIds[index];
    }

    public String[] getPlayeIds(){
        return playerIds;
    }

    public ArrayList<Card> getPlayerHand(String pid){
        int index = Arrays.asList(playerIds).indexOf(pid);
        return  playerHands.get(index);
    }

    public int getPlayerHandSize(String pid){
       return getPlayerHand(pid).size();
    }

    public Card getPlayerCard(String pid, int card){
        ArrayList<Card> hand = getPlayerHand(pid);
        return hand.get(card);
    }

    public boolean hasEmptyHand(String pid){
        return getPlayerHand(pid).isEmpty();
    }

    public boolean validCardPLay(Card card){
        return card.getColor() == validColor || card.getValue() == validValue;
    }

    public void checkPlayerTurn(String pid) throws InvalidPlayerTurnException{
        if(playerIds[currentPlayer] != pid){
            throw new InvalidPlayerTurnException("It isn't " + pid + "'s turn", pid);
        }
    }

    public void submitDraw(String pid) throws InvalidPlayerTurnException{
        checkPlayerTurn(pid);

        if(deck.isEmpty()){
            deck.replaceDeckWith(stockPile);
            deck.shuffle();
        }

        getPlayerHand(pid).add(deck.drawCard());
        if(gameDirection == false){
            currentPlayer = (currentPlayer + 1) % playerIds.length;
        }else {
            currentPlayer = (currentPlayer - 1) % playerIds.length;
            if(currentPlayer == -1){
                currentPlayer = playerIds.length - 1;
            }
        }
    }

    public void setCardColor(Card.Color color){
        validColor = color;
    }

    public void submitPlayerCard(String pid, Card card, Card.Color declaredColor)
    throws InvalidColorSubmissionException, InvalidValueSubmissionException, InvalidPlayerTurnException {
        checkPlayerTurn(pid);

        ArrayList<Card> playerHand = getPlayerHand(pid);
        if (!validCardPLay(card)) {
            if (card.getColor() == Card.Color.Wild) {
                validColor = card.getColor();
                validValue = card.getValue();
            }

            if (card.getColor() != validColor) {
                JLabel message = new JLabel("Invalid player move, expected color: " + validColor + " but got: " + card.getColor());
                message.setFont(new Font("Arial", Font.BOLD, 48));
                JOptionPane.showMessageDialog(null, message);
                throw new InvalidColorSubmissionException(message.getText(), card.getColor(), validColor);
            } else if (card.getValue() != validValue) {
                JLabel message2 = new JLabel("Invalid player move, expected value: " + validValue + " but got: " + card.getValue());
                message2.setFont(new Font("Arial", Font.BOLD, 48));
                JOptionPane.showMessageDialog(null, message2);
                throw new InvalidValueSubmissionException(message2.getText(), card.getValue(), validValue);
            }
        }

        playerHand.remove(card);

        if(hasEmptyHand(playerIds[currentPlayer])){
            JLabel message = new JLabel(playerIds[currentPlayer] + " has won the game!");
            message.setFont(new Font("Arial", Font.BOLD, 48));
            JOptionPane.showMessageDialog(null, message);
            System.exit(0);
        }

        validColor = card.getColor();
        validValue = card.getValue();
        stockPile.add(card);
        if(!gameDirection){
            currentPlayer = (currentPlayer + 1) % playerIds.length;
        }else if(gameDirection){
            currentPlayer = (currentPlayer - 1) % playerIds.length;
            if(currentPlayer == -1){
                currentPlayer = playerIds.length - 1;
            }
        }

        if(card.getColor() == Card.Color.Wild){
            validColor = declaredColor;
        }

        if(card.getValue() == Card.Value.DrawTwo){
            pid = playerIds[currentPlayer];
            getPlayerHand(pid).add(deck.drawCard());
            getPlayerHand(pid).add(deck.drawCard());
            JLabel messsage = new JLabel(pid + " Drew two cards!");
        }

        if(card.getValue() == Card.Value.WildFour){
            pid = playerIds[currentPlayer];
            getPlayerHand(pid).add(deck.drawCard());
            getPlayerHand(pid).add(deck.drawCard());
            getPlayerHand(pid).add(deck.drawCard());
            getPlayerHand(pid).add(deck.drawCard());
            JLabel messsage = new JLabel(pid + " Drew four cards!");
        }

        if(card.getValue() == Card.Value.Skip){
            JLabel message = new JLabel( playerIds[currentPlayer] + " Got Skipped!");
            message.setFont(new Font("Arial", Font.BOLD, 48));
            JOptionPane.showMessageDialog(null, message);
            if(!gameDirection){
                currentPlayer = (currentPlayer + 1) % playerIds.length;
            }else if(gameDirection){
                currentPlayer = (currentPlayer - 1) % playerIds.length;
                if(currentPlayer == -1){
                    currentPlayer = playerIds.length - 1;
                }
            }
        }

        if(card.getValue() == Card.Value.Reverse){
            JLabel message = new JLabel( pid + " changed direction!");
            message.setFont(new Font("Arial", Font.BOLD, 48));
            JOptionPane.showMessageDialog(null, message);

            gameDirection ^= true;
            if(gameDirection){
                currentPlayer = (currentPlayer - 2) % playerIds.length;
                if(currentPlayer == -1){
                    currentPlayer = playerIds.length - 1;
                }

                if(currentPlayer == -2){
                    currentPlayer= playerIds.length -2;
                }
            }else if(!gameDirection){
                currentPlayer = (currentPlayer + 2) % playerIds.length;
            }
        }
    }
}

class InvalidPlayerTurnException extends Exception {
    String pid;
    
    public InvalidPlayerTurnException(String message, String pid){
        super(message);
        this.pid = pid;
    }

    public String getPid(){
        return pid;
    }


}

class InvalidColorSubmissionException extends Exception {
    private Card.Color expectedColor;
    private Card.Color actualColor;

    public InvalidColorSubmissionException(String message, Card.Color actualColor, Card.Color expectedColor){
        this.expectedColor = expectedColor;
        this.actualColor = actualColor;
    }
}

class InvalidValueSubmissionException extends Exception {
    private Card.Value expectedValue;
    private Card.Value actualValue;

    public InvalidValueSubmissionException(String message, Card.Value actualValue, Card.Value expectedValue){
        this.expectedValue = expectedValue;
        this.actualValue = actualValue;
    }
}



