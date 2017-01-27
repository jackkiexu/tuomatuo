package com.lami.tuomatuo.search.base.concurrent.copyonwritearraylist;

import com.lami.tuomatuo.search.base.concurrent.unsafe.UnSafeClass;
import sun.misc.Unsafe;

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
        // 获取 o 在 数组 elements 中的位置
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
     * Returns the index of the the first occurence of the specified element in
     * this last, searching forwards from {@code index}, or return -1 if
     * the element is not found
     * More formally, returns the lowest index {@code i} such that
     * or -1 if there is no such index
     *
     * @param e element to search for
     * @param index index to start searching from
     * @return the idex of the forst occurence of the element in
     *          this list at position {@code index} or index in the list
     *          {@code -1} if the lement is not found
     */
    public int lastIndexOf(E e, int index){
       Object[] elements = getArray();
        return lastIndexOf(e, elements, index);
    }


    /**
     * Returns a shallow copy this list, (The lement themselves
     * are not copied)
     *
     * @return a clone of this list
     */
    public Object clone(){
        try{
            KCopyOnWriteArrayList c = (KCopyOnWriteArrayList)(super.clone());
            c.resetLock();
            return c;
        }catch (CloneNotSupportedException e){
            // this shouldn't happen, since we are Cloneable
        }
        return null;
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

    /**
     * Appends the specified element to the end of this list
     *
     * @param e element to be appeded to this list
     * @return {@code true} (as specified by {@link Collection#add})
     */
    @Override
    public boolean add(E e) {
        /**
         * 增加元素 e 到数组的末尾
         * 操作步骤:
         *  1. 获取全局的 reentrantLock
         *  2. 将原来的 array1 copy 到一个 array.length + 1 的数组 array2 里面
         *  3. 将 先添加的元素e添加到新数组 array2 的最后一个空间里面 (array2[array2.length - 1] = e)
         *  4. 将 新数组 array2 赋值给 CopyOnWriteArrayList 中的 array
         */
        final ReentrantLock lock = this.lock;
        lock.lock();                                                    // 1. 获取 全局 lock
        try{
            Object[] elements = getArray();                             // 2. 获取原来的数组
            int len = elements.length;
            Object[] newElements = Arrays.copyOf(elements, len + 1);    // 3. 新建一个 array2 将原来的数据赋值到这个新建的数组里面
            newElements[len] = e;                                       // 4. 将 e 赋值给 array2的最后一个空间里面
            setArray(newElements);                                      // 5. 将新数组 array2 赋值给 CopyOnWriteArrayList 中的 array
            return true;
        }finally {
            lock.unlock();                                              // 6. 释放锁
        }

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
        return get(getArray(), index); // 获取指定位置 index 上的元素, 其实就是取数组 index 下标上的元素
    }

    /**
     * Replaces the element at the specified position in this list with the
     * specified element
     */
    @Override
    public E set(int index, E element) {
        /**
         * 将数组 array 指定位置 index 用元素 element 进行替代
         * 操作步骤:
         *      0. 获取全局的 ReentrantLock
         *      1. 获取数组指定下标 index 上的元素
         *      2. 判断 element 是否与来源数组中的元素一致
         *          1) 不一致, 则获取原数组的 一个 snapshot, 并且将对应位置 index 进行替换
         *          2) 一致, setArray(elements) <- 这个其实是说明都没做
         *      3. 在 finally 中释放 锁
         *
         */
        final ReentrantLock lock = this.lock;
        lock.lock();                                                    // 0. 获取锁
        try {
            Object[] elements = getArray();
            E oldValue = get(elements, index);                          // 1. 获取原数组中对应index位置的元素

            if(oldValue != element){
                int len = elements.length;
                Object[] newElements = Arrays.copyOf(elements, len);    // 2. 获取原数组的一个 snapshot 版本
                newElements[index] = element;                           // 3. 在 index 位置进行 set 新的值
                setArray(newElements);                                  // 4. 将 snapshot 版本的数组覆盖原来的数组
            }else{
                // Not quite a no-op; ensures volatile write semantics
                setArray(elements);
            }
        }finally {
            lock.unlock();                                              // 5. 释放锁
        }
        return null;
    }

    /**
     * Inserts the specified element at the specified position in this
     * list. Shifts the lement currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices)
     */
    @Override
    public void add(int index, E element) {
        /**
         * 将元素 e 插入到数组 指定的索引下标 index 下
         * 操作步骤:
         *      1. 获取全局的锁
         *      2. 获取 CopyOnWriteArrayList 的 array, 及 array.length
         *      3. 进行参数校验 (index > len || index < 0) 则直接抛异常 -> 说明元素的插入只能在 0 - array.length 之间(包含两个端点)
         *      4. 获取插入点 index 与 array.length 之间的步长, 进行分类讨论
         *          1) 插入的数据正好在 原array数组的后一个节点 (numMoved = len), 则直接新建一个 array, 将原来的 array copy 过来
         *          2) 插入的 index 满足 0 <= index <= len - 1, 则新建一个数组, 原来 o -> index(index不包含) 拷贝来, index后面的数据拷贝到新数组的 index + 1 的空间
         *      5. 将 e 设置到 新 array 的 index 位置
         *      6. 将 新 array 设置到 CopyOnWriteArrayList 里面
         */
        final ReentrantLock lock = this.lock;
        lock.lock();                                                                    // 1. 获取全局的锁
        try{
            Object[] elements = getArray();
            int len = elements.length;
            if(index > len || index < 0){
                throw new IndexOutOfBoundsException("Index: " + index + ", Size:" + len);
            }
            Object[] newElements;
            int numMoved = len - index;
            if(numMoved == 0){ // 走到这一步, 说明 数据是插入到 oldArray.length(这个值是指下标) 位置上的元素
                newElements = Arrays.copyOf(elements, len + 1); // 直接拷贝原数组到一个新的 array 数组中, 这个数组的长度是 len + 1
            }else{
                newElements = new Object[len + 1];
                System.arraycopy(elements, 0, newElements, 0, index); // 将原数组 index 前的数组都拷贝到新的数组里面
                System.arraycopy(elements, index, newElements, index + 1, numMoved); // 将原数组 index 以后的元素都 copy到新的数组里面(包括index位置的元素)
            }
            newElements[index] = element; // 将 index 赋值 element
            setArray(newElements); // 将 新的 array set到 CopyOnWriteArrayList 上
        }finally {
            lock.unlock();
        }

    }

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices). Returns the lement that was removed from the list
     */
    @Override
    public E remove(int index) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            Object[] elements = getArray();
            int len = elements.length;
            E oldValue = get(elements, index);
            int numMoved = len - index - 1;
            if(numMoved == 0){ // 说明删除的元素的位置在 len - 1 上, 直接拷贝原数组的前 len - 1 个元素
                setArray(Arrays.copyOf(elements, len - 1));
            }else{
                Object[] newElements = new Object[len - 1];
                System.arraycopy(elements, 0, newElements, 0, index); // 拷贝原数组 0 - index之间的元素 (index 不拷贝)
                System.arraycopy(elements, index + 1, newElements, index, numMoved); // 拷贝原数组 index+1 到末尾之间的元素 (index＋1也进行拷贝)
                setArray(newElements);
            }
        }finally {
            lock.unlock();
        }
        return null;
    }

    /**
     *  Removes the first occurrence of the specified element from this list,
     *  if it is present. If this list does not contain the element, it is
     *  unchanged. More formally, removes the element with the lowest index
     *  {@code i} such that
     *  ( o == null get(i) == null o.equals(get(i)))
     *  (if such an element exist). Returns {@code true} if this list
     *  contained the specified element (or equivalently), if this list
     *  changed as a result of the call
     *
     *  @param o element to be removed from this list, if present
     *  @return {@code true} if this list contained the specified element
     *
     */
    public boolean remove(Object o){
        Object[] snapshot = getArray();
        // 获取 index 在 snapshot 中的位置, －1 表示不存在
        int index = indexOf(o, snapshot, 0, snapshot.length);
        return (index < 0) ? false : remove(o, snapshot, index);
    }

    /**
     * A version of remove(Object) using the strong hint that given
     * recent snapshot contains o at the given index
     */
    private boolean remove(Object o, Object[] snapshot, int index){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] current = getArray();
            int len = current.length;
            // findIndex: <- 这个用法平时用的比较少, 在这里, 只要 break findIndex, 那 if(snapshot != current) 这里括号里面的其他代码就不执行了, 直接跳到括号外面, 建议写个小demo试一下
            if(snapshot != current) findIndex:{ // snapshot != current 表示数组被另外一个线程操作过, 有变化
                /**
                 * 下面的操作是发生在 调用方法 "remove(Object o)" 中的 "indexOf"后 , 数组 array 发生变化而做的查询修正工作
                 * 主要分 下面 4 中情况:
                 *  1. 从 index,len 中取出一个较小的值 prefix, 从 current的prefix前个元素中寻找元素 o, 找到后, 直接 break, 执行下面的操作
                 *  2. 若 index >= len, 则说明 元素 o 在另外的线程中已经被删除, 直接 return
                 *  3. current[index] = o, 则说明, index 位置上的元素 o 还在那边, 直接 break
                 *  4. 最后 在 index 与 len 之间寻找元素, 找到位置直接接下来的代码, 没找到 直接 return
                 */
                int prefix = Math.min(index, len);
                for(int i = 0; i < prefix; i++){
                    // 找出 current 数组里面 元素 o 所在的位置 i, 并且赋值给 index
                    if(current[i] != snapshot[i] && eq(o, current[i])){
                        index = i;
                        break findIndex;
                    }
                }

                if(index >= len){ // index >= len 表示元素 o 已经被删除掉
                    return false;
                }
                if(current[index] == o){ // 元素 o 也在数组 current 的 index 位置
                    break findIndex;
                }
                index = indexOf(o, current, index, len); // 在 current 中寻找元素 o 所在的位置 (这里不会出现 index > len 的情况, 上面的代码中已经做了判断)
                if(index < 0){ // 要删除的元素 在另外的线程中被删除掉了, 直接 return false
                    return false;
                }
            }

            Object[] newElements = new Object[len - 1]; // 新建一个 len - 1 长度的数组
            System.arraycopy(current, 0, newElements, 0, index); // 拷贝老数组前 index 个元素
            System.arraycopy(current, index + 1, newElements, index, len - index - 1); // 拷贝 老数组 index + 1 后的元素 ( index + 1 包含)
            setArray(newElements);
            return true;
        }finally {
            lock.unlock();
        }
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

    void removeRange(int fromIndex, int toIndex){

    }

    private E get(Object[] a, int index){
        return (E)a[index];
    }


    /**
     * Sublist for CopyOnWriteArrayList.
     * This class extends AbstractList merely for convenience, to
     * avoiding having to define addAll, etc. This doesn't hurt, but
     * is wasteful. This class does not need or use modCount
     * mechanics in AbstractList, but does need to check for
     * concurrent modification using similar mechanics. On each
     * operation,the array that we expect the backing list to use
     * is checked and updated. Since we do this for all of the
     * base operations invoked by those defined in AbstractList,
     * all is well. While inefficient, this is not worth
     * improving. The kinds of list operations inherited from
     * AbstractList are already so slow on COW sublists that
     * adding a bit more space/time doesn't seem even noticeable
     *
     * @param <E>
     */
    private static class COWSubList<E> extends AbstractList<E> implements RandomAccess{

        private final KCopyOnWriteArrayList<E> l;
        private final int offset;
        private int size;
        private Object[] expectedArray;

        // only call this holding l's lock
        public COWSubList(KCopyOnWriteArrayList<E> list, int fromIndex, int toIndex) {
            l = list;
            expectedArray = l.getArray();
            offset = fromIndex;
            size = toIndex - fromIndex;
        }

        // only call this holding l's lock
        private void checkForComodification(){
            if(l.getArray() != expectedArray){
                throw new ConcurrentModificationException();
            }
        }

        private void rangeCheck(int index){
            if(index < 0 || index >= size){
                throw new IndexOutOfBoundsException("Index: " + index + ", Size:" + size());
            }
        }

        public E set(int index, E element){
            final ReentrantLock lock = l.lock;
            lock.lock();
            try{
                rangeCheck(index);
                checkForComodification();
                E x = l.set(index + offset, element);
                expectedArray = l.getArray();
                return x;
            }finally {
                lock.unlock();
            }
        }

        @Override
        public E get(int index) {
            final ReentrantLock lock = l.lock;
            lock.lock();
            try{
                rangeCheck(index);
                checkForComodification();
                return l.get(index + offset);
            }finally {
                lock.unlock();
            }
        }

        @Override
        public int size() {
            final ReentrantLock lock = l.lock;
            lock.lock();
            try{
                checkForComodification();
                return size;
            }finally {
                lock.unlock();
            }
        }

        public void add(int index, E element){
            final ReentrantLock lock = l.lock;
            lock.lock();
            try{
                checkForComodification();
                if(index < 0 || index > size){
                    throw new IndexOutOfBoundsException();
                }
                l.add(index + offset, element);
                expectedArray = l.getArray();
                size++;
            }finally {
                lock.unlock();
            }
        }

        public void clear(){
            final ReentrantLock lock = l.lock;
            lock.lock();
            try {
                checkForComodification();
            }finally {
                lock.unlock();
            }
        }
    }

    private static class COWSubListIterator<E> implements ListIterator<E>{

        private final ListIterator<E> i;
        private final int index;
        private final int offset;
        private final int size;

        public COWSubListIterator(List<E> l, int index, int offset, int size) {
            this.index = index;
            this.offset = offset;
            this.size = size;
            i = l.listIterator();
        }

        @Override
        public boolean hasNext() {
            return nextIndex() < size;
        }

        @Override
        public E next() {
            if(hasNext()){
                return i.next();
            }else{
                throw new NoSuchElementException();
            }
        }

        @Override
        public boolean hasPrevious() {
            return previousIndex() >= 0;
        }

        @Override
        public E previous() {
            if(hasPrevious()){
                return i.previous();
            }else{
                throw new NoSuchElementException();
            }
        }

        @Override
        public int nextIndex() {
            return i.nextIndex() - offset;
        }

        @Override
        public int previousIndex() {
            return i.previousIndex() - offset;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(E e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(E e) {
            throw new UnsupportedOperationException();
        }
    }

    // Support for resetting lock while deserializing
    private void resetLock(){
        unsafe.putObject(this, lockOffset, new ReentrantLock());
    }

    // 为什么 unsafe 只能通过反射得到
    // Unsafe.getUnsafe() 通过这种方式来获取 unsafe 时, 代码必须是受信任的
    // 比如 java -Xbootclasspath:rt.jar 来获取
    private static final Unsafe unsafe ;
    private static final long lockOffset;

    static {
        try {
            unsafe = UnSafeClass.getInstance();
            Class<?> k = KCopyOnWriteArrayList.class;
            lockOffset = unsafe.objectFieldOffset(k.getDeclaredField("lock"));
        }catch (Exception e){
            throw new Error(e);
        }
    }


}
