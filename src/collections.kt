package jm.lib

/**Changes
 *
 * 12.01.2017
 *   + MutableList.filterInplace(), MutableList.filterInplaceIndexed()
*/

// -------------------------------------------------------------------------
/**Add all elements to list*/
fun <T> MutableList<T>.addAll(vararg v : T) : MutableList<T> {
  //Impossible to use addAll: its need a collection -> double conversion
  for (a in v) add(a)
  return this
}

/**Add all elements to list starting from position*/
fun <T> MutableList<T>.addAllAt(idx : Int, vararg v : T) : MutableList<T> {
  //Impossible to use addAll: its need a collection -> double conversion
  for (i in 0..v.size - 1)
    add(idx + i, v[i])
  return this
}

/**Filter list content inplace. Remove all elements not match given predicate. */
fun <T> MutableList<T>.filterInplace(predicate : (it : T) -> Boolean) {
  var n = 0
  while (n < size) {
    if (!predicate(get(n)))
      removeAt(n)
    else
      n++

  }
}

/**Filter list content inplace. Remove all elements not match given predicate. */
fun <T> MutableList<T>.filterInplaceIndexed(predicate : (it : T, idx : Int) -> Boolean) {
  var n = 0
  while (n < size) {
    if (!predicate(get(n), n))
      removeAt(n)
    else
      n++

  }
}

/**Add item to the list in the map.
 * Add to existing key or create new key with new list as [mutableListOf](value).
 *
 * Base must be declared as ``ANY_MUTABLE_MAP_TYPE<KEY, MutableList<ITEM>>``,
 * where "ANY_MUTABLE_MAP_TYPE" is the type of class implemented [MutableMap] interface.
 * ```
 * mutableMapOf<KEY, MutableList<ITEM>>()
 *    or
 * HashMap<KEY, MutableList<ITEM>>()
 * ```
 */
fun <K, T> MutableMap<K, MutableList<T>>.addToList(key : K, value : T) : T =
  addMapListItem(this, key, value)

/**Add item to the list in the map.
 * Add to existing key or create new key with new list as [mutableListOf](value).
 *
 * Base must be declared as ``ANY_MUTABLE_MAP_TYPE<KEY, MutableList<ITEM>>``,
 * where "ANY_MUTABLE_MAP_TYPE" is the type of class implemented [MutableMap] interface.
 * ```
 * mutableMapOf<KEY, MutableList<ITEM>>()
 *    or
 * HashMap<KEY, MutableList<ITEM>>()
 * ```
 */
fun <K, T> addMapListItem(ar : MutableMap<K, MutableList<T>>, key : K, value : T) : T {
  val l = ar.get(key)
  if (l != null)
    l.add(value)
  else
    ar.put(key, mutableListOf(value))
  return value
}

/**Add item to the set in the map.
 * Add to existing key or create new key with new set as [mutableSet](value).
 *
 * Base must be declared as ``ANY_MUTABLE_SET_TYPE<KEY, MutableList<ITEM>>()``,
 * where "ANY_MUTABLE_SET_TYPE" is the type of class implemented [MutableSet] interface.
 * ```
 * mutableMapOf<KEY, MutableList<ITEM>>()
 *    or
 * HashMap<KEY, MutableList<ITEM>>()
 * ```
 */
fun <K, T> MutableMap<K, MutableSet<T>>.addToSet(key : K, value : T) : T =
  addMapSetItem(this, key, value)

/**Add item to the set in the map.
 * Add to existing key or create new key with new set as [mutableSet](value).
 *
 * Base must be declared as ``ANY_MUTABLE_SET_TYPE<KEY, MutableList<ITEM>>()``,
 * where "ANY_MUTABLE_SET_TYPE" is the type of class implemented [MutableSet] interface.
 * ```
 * mutableMapOf<KEY, MutableList<ITEM>>()
 *    or
 * HashMap<KEY, MutableList<ITEM>>()
 * ```
 */
fun <K, T> addMapSetItem(ar : MutableMap<K, MutableSet<T>>, key : K, value : T) : T {
  val l = ar.get(key)
  if (l != null)
    l.add(value)
  else
    ar.put(key, mutableSetOf(value))
  return value
}

// ---------------------------------------------------------------------------------------
class IntIterator(private val limit : Int) : Iterator<Int> {
  private var idx = 0
  override fun hasNext() : Boolean = idx < limit
  override fun next() : Int = idx++
}

/**Itterate on this value in range ``[0..this-1]``
 *
 * Can be used in **for** loops:
 *
 * ```
 * val count : Int
 * for( n in count )  //Will itterate [0..count-1]
 *   //
 * ```
 **/
operator fun Int.iterator() = IntIterator(this)

// ---------------------------------------------------------------------------------------
class LongIterator(private val limit : Long) : Iterator<Long> {
  private var idx : Long = 0L
  override fun hasNext() : Boolean = idx < limit
  override fun next() : Long = idx++
}

/**Itterate on this value in range ``[0..this-1]``
 *
 * Can be used in **for** loops:
 *
 * ```
 * val count : Long
 * for( n in count )  //Will itterate [0..count-1]
 *   //
 * ```
 **/
operator fun Long.iterator() = LongIterator(this)
