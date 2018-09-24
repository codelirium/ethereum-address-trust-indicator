package io.codelirium.argent.indicator.model.dto;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.nonNull;


public class TreeNode<T> implements Iterable<TreeNode<T>> {

	private T data;
	private TreeNode<T> parent;
	private List<TreeNode<T>> children;
	private List<TreeNode<T>> elementsIndex;


	private boolean isRoot() {

		return parent == null;

	}


	public boolean isLeaf() {

		return children.size() == 0;

	}


	public TreeNode(final T data) {

		this.data = data;
		this.children = new LinkedList<>();
		this.elementsIndex = new LinkedList<>();
		this.elementsIndex.add(this);
	}


	public TreeNode<T> addChild(final T child) {

		final TreeNode<T> node = new TreeNode<>(child);

		node.parent = this;

		this.children.add(node);
		this.registerChildForSearch(node);


		return node;
	}


	public int getLevel() {

		if (this.isRoot()) {

			return 0;

		} else {

			return parent.getLevel() + 1;

		}
	}


	private void registerChildForSearch(final TreeNode<T> node) {

		elementsIndex.add(node);

		if (nonNull(parent)) {

			parent.registerChildForSearch(node);

		}
	}


	public TreeNode<T> findTreeNode(final Comparable<T> comparable) {

		for (final TreeNode<T> element : this.elementsIndex) {

			final T data = element.data;

			if (comparable.compareTo(data) == 0) {

				return element;

			}
		}


		return null;
	}


	public TreeNode<T> getParent() {

		return parent;

	}


	public T getData() {

		return data;

	}


	public List<TreeNode<T>> getChildren() {

		return children;

	}


	@Override
	public String toString() {

		return nonNull(data) ? data.toString() : "[null]";

	}


	@Override
	public Iterator<TreeNode<T>> iterator() {

		return new TreeNodeIterator<>(this);

	}
}
