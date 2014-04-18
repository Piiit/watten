package cards;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xml.Loadable;
import xml.Node;
import xml.SimpleXML;

/**
 * <pre>MultipeCards is a set of 0 or more cards. They are stored in a indexed list.
 * 
 * The main functionality provides:
 * - adding, removing cards from the list
 * - counting
 * - sorting and shuffling
 * - iterative access to cards with a moving pointer
 * - history of already chosen cards
 * - some Exceptions to handle boundaries</pre>
 * @author Peter Moser (pemoser)
 *
 */
public class MultipleCards implements Loadable {
	
	private List<Card> cards = new ArrayList<Card>();
	private int index = 0;
	
	/**
	 * Iterative access: Retrieve the current index
	 * @return current index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Iterative access: set the current index to a specific position
	 * @throws ArrayIndexOutOfBoundsException
	 * @param index to set
	 */
	public void setIndex(int index) {
		if (index >= cards.size() || index < 0) {
			throw new ArrayIndexOutOfBoundsException("Card index out of bounds!");
		}
		this.index = index;
	}

	/**
	 * Append a card to the list
	 * @param iCard card to add
	 */
	public void addCard(Card iCard) {
		cards.add(iCard);
	}
	
	/**
	 * Count the cards
	 * @return count
	 */
	public int getCount() {
		return cards.size();
	}
	
	/**
	 * Get the card indicated by the current index 
	 * @return card
	 */
	public Card getCard() {
		return cards.get(index);
	}
	
	/**
	 * Get the card indicated by the passed index
	 * @param idx passed index
	 * @return card
	 */
	public Card getCardById(int idx) {
		return cards.get(idx);
	}
	
	/**
	 * Get the next card of the list and increments the index pointer.
	 * @return card
	 * @throws ArrayIndexOutOfBoundsException if there aren't no more cards
	 */
	public Card getNextCard() throws ArrayIndexOutOfBoundsException {
		if (index >= cards.size()) {
			throw new ArrayIndexOutOfBoundsException("Out of cards!");
		}

		index++;
		return cards.get(index-1);
	}
	
	/**
	 * Retrieve the full list of all cards
	 * @return all cards as a list
	 */
	public List<Card> getAllCards() {
		return cards;
	}
	
	/**
	 * Retrieve the list of cards from the beginning to the current card (excluded) 
	 * @return history of cards as a list
	 */
	public List<Card> getHistory() {
		return new ArrayList<Card>(cards.subList(0, index));
	}
	
	/**
	 * Check if the card (suit:rank) is present within this set of cards
	 * @param card
	 * @return true if same card is present
	 */
	public boolean hasCard(Card card) {
		try {
			getIndex(card);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public int getIndex(Card card) throws Exception {
		int i = 0;
		for(Card c : cards) {
			if(c.equals(card)) {
				return i;
			}
			i++;
		}
		throw new Exception("Card " + card + " not found");
	}

	/**
	 * Remove a card with the specified index from the list
	 * @param idx index of the card to remove
	 * @return removed card
	 */
	public Card removeCard(int idx) {
		return cards.remove(idx);
	}

	/**
	 * Sort the cards according to their rank. Cards which are face down will be shown last. 
	 */
	public void sort() {
		index = 0;
		Collections.sort(cards);
	}
	
	/**
	 * Shuffle the cards.
	 */
	public void shuffle() {
		index = 0;
		Collections.shuffle(cards);
	}
	
	/**
	 * Remove all cards from the list and reset the index pointer.
	 */
	public void clear() {
		cards.clear();
		reset();
	}
	
	/**
	 * Reset the index pointer.
	 */
	public void reset() {
		index = 0;
	}
	
	/**
	 * Serialize all cards and the index pointer.
	 */
	public String serialize() {
		String output = SimpleXML.createTag("index", index);
		
		for (Card c : cards) {
			output += c.serialize() + ',';
		}
		if (output.endsWith(",")) {
			output = output.substring(0,output.length()-1);
		}
		return SimpleXML.createTag("cards", output);
	}
	
	/**
	 * Load all cards from a XML tree node.
	 */
	public void load(Node n) {
		if (n == null) {
			throw new NullPointerException("MultipleCards: Parameter must be a valid node!");
		}

		Node nCards = n.getNode("cards");
		Node nIndex = nCards.getNode("index");
		clear();
		index = Integer.parseInt(nIndex.getData());
		if (nCards.getData() != null) {
			String arr[] = nCards.getData().split(",");
			for (String c : arr) {
				addCard(new Card(c));
			}
		}
	}

	@Override
	public String toString() {
		String output = "";
		for (Card c : cards) {
			output += c;
		}
		return output;
	}

}
