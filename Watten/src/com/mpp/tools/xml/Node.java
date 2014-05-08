package com.mpp.tools.xml;
import java.util.ArrayList;


/**
 * This is a node to build a node tree. Every node except of the root node has a parent and with that
 * it builds a hierarchical tree structure. Some nodes can have child-nodes, if a node hasn't any children
 * it is a leaf.
 * @author Peter Moser (pemoser)
 *
 */
public class Node {
	private String name;
	private String data;
	private Node parent = null;
	private ArrayList<Node> children = new ArrayList<Node>();
	
	
	/**
	 * Create a new node with the given name.
	 * @param name
	 */
	public Node(String name) {
		this.name = name;
	}
	
	
	/**
	 * Add some child-nodes to this node.
	 * @param child
	 */
	public void addChild(Node child) {
		child.parent = this;
		children.add(child);
	}

	public String getName() {
		return name;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	public String getData() {
		return data;
	}

	
	/**
	 * Return all child-nodes as a hierarchical node-subtree.
	 * @return list of child-nodes.
	 */
	public ArrayList<Node> getChildren() {
		return children;
	}
	
	
	/**
	 * Check if this node has children.
	 * @return true if this node has children.
	 */
	public boolean hasChildren() {
		return !children.isEmpty();
	}
	
	
	/**
	 * Gets the parent of this node.
	 * @return parent if exists or null if it is a root node.
	 */
	public Node getParent() {
		return parent;
	}
	
	
	/**
	 * Set the new parent (changes the hierarchical order)
	 * @param p
	 */
	public void setParent(Node p) {
		parent = p;
	}
	
	
	/**
	 * Returns a child-node with the given name if exists, else throws an IllegalArgumentException
	 * @param name This is the child-node which we're looking for. 
	 * @return child-node if exists.
	 * @throws IllegalArgumentException if this node hasn't any children or no children with the provided name.
	 */
	public Node getNode(String name) throws IllegalArgumentException {
		if (hasChildren()) {
			for (Node n : children) {
				if (n.getName().compareToIgnoreCase(name) == 0) {
					return n;
				}
			}
		}
		throw new IllegalArgumentException("Node " + name + " not found!");
	}
}
