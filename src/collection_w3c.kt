package jm.lib

import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import org.w3c.dom.NodeList

// -------------------------------------------------------------------------
class w3c_NodeIterable(private val list : NodeList) : Iterable<Node> {
  class w3c_NodeIterator(private val list : NodeList) : Iterator<Node> {
    private var idx = 0
    override fun hasNext() : Boolean = idx < list.length
    override fun next() : Node = list.item(idx++)
  }

  override fun iterator() : Iterator<Node> = w3c_NodeIterator(list)
}

operator fun NodeList.iterator()
  = w3c_NodeIterable(this).iterator()

inline fun NodeList.forEachIndexed(action : (Int, Node) -> Unit)
  = w3c_NodeIterable(this).forEachIndexed { i, p -> action(i, p) }

inline fun NodeList.forEach(action : (Node) -> Unit)
  = w3c_NodeIterable(this).forEach { action(it) }

class w3c_NamedNodeMapIterable(private val list : NamedNodeMap) : Iterable<Node> {
  class w3c_NamedNodeMapIterator(private val list : NamedNodeMap) : Iterator<Node> {
    private var idx = 0
    override fun hasNext() : Boolean = idx < list.length
    override fun next() : Node = list.item(idx++)
  }

  override fun iterator() : Iterator<Node> = w3c_NamedNodeMapIterator(list)
}

operator fun NamedNodeMap.iterator()
  = w3c_NamedNodeMapIterable(this).iterator()

inline fun NamedNodeMap.forEachIndexed(action : (Int, Node) -> Unit)
  = w3c_NamedNodeMapIterable(this).forEachIndexed { i, p -> action(i, p) }

inline fun NamedNodeMap.forEach(action : (Node) -> Unit)
  = w3c_NamedNodeMapIterable(this).forEach { action(it) }

