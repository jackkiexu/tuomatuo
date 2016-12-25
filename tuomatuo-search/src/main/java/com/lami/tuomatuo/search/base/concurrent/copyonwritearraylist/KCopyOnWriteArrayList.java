package com.lami.tuomatuo.search.base.concurrent.copyonwritearraylist;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A thread-safe variant of {@link java.util.ArrayList} in which all mutative
 * operations ({@code add}, {@code set}, and so on) are implemented by
 * making a fresh copy of the underlying array
 *
 * <p>
 *     This is ordinarily too costly, but may be <em>more</em> efficient
 *     than alternatives when traversal operations vastly outnumber
 *     mutations, and is useful when you cannot or don't want to
 *     synchronize traversals, yet need to preclude interference among
 *     concurrent threads. The "snapshot" style iterator method uses a
 *     reference to the state of the array at the point that the iterator
 *     was created. This array never changes during the lifetime of the
 *     iterator, so interference is impossible and the itarator is
 *     guaranteed not all to throw {@code ConcurrentModificationException}.
 *     The iterator will not reflect additions, removals, or changes to
 *     the list since the iterator was created. Element-changing
 *     operations on iterators themselves ({@code remove}, {@code set}, and
 *     {@code add}) are not supported. These methods throw
 *     {@code UnsupportedOperationException}
 * </p>
 *
 * <p>
 *     All elements are permitted, including {@code null}
 * </p>
 *
 * <p>
 *     Memory consistency effects: AS WITH OTHER CONCURRENT
 *     collections, actions in a thread prior to placing an object nto a
 *     {@code KCopyOnWriteArrayList}
 *     <a href="package-summary.html#MemoryVisibility">
 *          <i>happen-before</i>
 *     </a>
 *     actions subsequent to the access or removal of that element from
 *     the {@code KCopyOnWriteArrayList} in another thread
 * </p>
 *
 * <p>
 *     This class is a member of the
 *     <a href="{@docRoot}/../technotes/guides/collections/index.html">
 *      Java Collections Framework
 *     </a>
 * </p>
 *
 * Created by xjk on 12/25/16.
 */
public class KCopyOnWriteArrayList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable{

    private static final long serialVersionUID = -8382908316577068931L;

    /** The lock protecting all mutators */
    final transient ReentrantLock lock = new ReentrantLock();

    /** The array, accessed only via getArray/setArray */
    private transient volatile Object[] array;

    /**
     * Gets the array. Non-private so as to also be accessible
     * from KCopyOnWriteArraySet class.
     * @return
     */
    final Object[] getArray() { return array; }

    /**
     * Sets the array
     * @param a
     */
    final void setArray(Object[] a) { array = a; }

    /**
     * Creates an empty list.
     */
    public KCopyOnWriteArrayList() { setArray(new Object[0]); }

    public KCopyOnWriteArrayList(Collection<? extends E> c){
        Object[] elements;
        if(c.getClass() == KCopyOnWriteArrayList.class){
            elements = ((KCopyOnWriteArrayList<?>)c).getArray();
        }
        else{
            elements = c.toArray();
            // c.toArray might (incorrectly) not return Object[] (see 6260652)
            if(elements.getClass() != Object[].class){
                elements = Arrays.copyOf(elements, elements.length, Object[].class);
            }
        }
        setArray(elements);
    }

    /**
     * Creates a list holding a copy the given array
     *
     * @param toCopyIn the array (a copy of this array is used as the internal array)
     */
    public KCopyOnWriteArrayList(E[] toCopyIn){
        setArray(Arrays.copyOf(toCopyIn, toCopyIn.length, Object[].class));
    }

    /**
     * Returns the number of elements in this list
     * @return the number of elements in this list
     */
    @Override
    public int size() {
        return getArray().length;
    }

    /**
     * Returns {@code true} if this list contains no elements
     * @return {@code true} if this list contains no elements
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Tests for equality, coping with nulls
     * @param o1
     * @param o2
     * @return
     */
    private static boolean eq(Object o1, Object o2){
        return (o1 == null) ? o2 == null : o1.equals(o2);
    }

    /**
     * static version of indexOf, to allow repeated calls without
     * needing to re-acquire array each time
     *
     * @param o element to search
     * @param elements the array
     * @param index first index to search
     * @param fence one past last index to search
     * @return index of element, or -1 if absent
     */
    private static int indexOf(Object o, Object[] elements, int index, int fence){
        if(o == null){
            for(int i = index; i < fence; i++){
                if(elements[i] == null){
                    return i;
                }
            }
        }
        else{
            for(int i = index; i < fence; i++){
                if(o.equals(elements[i])){
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * static version of lastIndexof
     * @param o element to search for
     * @param elements the array
     * @param index first index to search
     * @return index of element, or -1 if absent
     */
    private static int lastIndexOf(Object o, Object[] elements, int index){
        if(o == null){
            for(int i = index; i >= 0; i--){
                if(elements[i] == null){
                    return i;
                }
            }
        }
        else{
            for(int i = index; i >= 0; i--){
                if(o.equals(elements[i])){
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Returns {@code true} if this list contains the specified element.
     * More formally, returns {@code true} if and only if this list contains
     * at least one element {@code e} such that
     * <tt>
     *  (o == null)
     * </tt>
     *
     * @param o element whose presence in this list is to be tested
     * @return {@code true} if this list contains the specified element
     */
    @Override
    public boolean contains(Object o) {
        Object[] elements = getArray();
        return indexOf(o, elements, 0, elements.length) >= 0;
    }

    public int indexOf(Object o){
        Object[] elements = getArray();
        return indexOf(o, elements, 0, elements.length);
    }

    /**
     * Returns the index of the first occurrence of the specified element in
     * this list, searching forwards from {@code index}, or returns -1 if
     * the lement is not found
     * More formally, returns the lowest index {@code i} such that
     * or -1 if there is no such index
     *
     * @param e element to search for
     * @param index index to start searching from
     * @return the index of the first occurence of the element in
     *         this list at position {@code index} or later in the list
     *         {@code -1} if the element is not found
     */
    public int indeOf(E e, int index){
        Object[] elements = getArray();
        return indexOf(e, elements, index, elements.length);
    }

    public int lastIndexof(Object o){
        Object[] elements = getArray();
        return lastIndexOf(o, elements, elements.length - 1);
    }

    /**
     *
     * @param e
     * @param index
     * @return
     */
    public int lastIndexOf(E e, int index){
       Object[] elements = getArray();
        return lastIndexOf(e, elements, index);
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public boolean add(E e) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public E get(int index) {
        return null;
    }

    @Override
    public E set(int index, E element) {
        return null;
    }

    @Override
    public void add(int index, E element) {

    }

    @Override
    public E remove(int index) {
        return null;
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    @Override
    public ListIterator<E> listIterator() {
        return null;
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return null;
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return null;
    }
}
