package io.codelirium.argent.indicator.model.dto;

import java.util.Iterator;

import static io.codelirium.argent.indicator.model.dto.TreeNodeIterator.ProcessStages.ProcessChildCurrentNode;
import static io.codelirium.argent.indicator.model.dto.TreeNodeIterator.ProcessStages.ProcessChildSubNode;
import static io.codelirium.argent.indicator.model.dto.TreeNodeIterator.ProcessStages.ProcessParent;


public class TreeNodeIterator<T> implements Iterator<TreeNode<T>> {

	enum ProcessStages {
		ProcessParent,
		ProcessChildCurrentNode,
		ProcessChildSubNode
	}


	private TreeNode<T> treeNode;
	private ProcessStages doNext;
	private TreeNode<T> next;
	private Iterator<TreeNode<T>> childrenCurrentNodeIterator;
	private Iterator<TreeNode<T>> childrenSubNodeIterator;


	TreeNodeIterator(final TreeNode<T> treeNode) {

		this.treeNode = treeNode;
		this.doNext = ProcessParent;
		this.childrenCurrentNodeIterator = treeNode.getChildren().iterator();
	}


	@Override
	public boolean hasNext() {

		if (this.doNext == ProcessParent) {

			this.next = this.treeNode;
			this.doNext = ProcessChildCurrentNode;


			return true;
		}


		if (this.doNext == ProcessChildCurrentNode) {

			if (childrenCurrentNodeIterator.hasNext()) {

				final TreeNode<T> child = childrenCurrentNodeIterator.next();

				childrenSubNodeIterator = child.iterator();
				this.doNext = ProcessChildSubNode;


				return hasNext();

			} else {

				this.doNext = null;


				return false;
			}
		}


		if (this.doNext == ProcessChildSubNode) {

			if (childrenSubNodeIterator.hasNext()) {

				this.next = childrenSubNodeIterator.next();


				return true;

			} else {

				this.next = null;
				this.doNext = ProcessChildCurrentNode;


				return hasNext();
			}
		}


		return false;
	}


	@Override
	public TreeNode<T> next() {

		return this.next;

	}


	@Override
	public void remove() {

		throw new UnsupportedOperationException();

	}
}
