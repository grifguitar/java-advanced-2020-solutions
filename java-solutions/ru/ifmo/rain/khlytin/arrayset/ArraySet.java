package ru.ifmo.rain.khlytin.arrayset;

import java.util.*;

/**
 * @author khlyting
 */
public final class ArraySet<E extends Comparable<E>> extends AbstractSet<E> implements NavigableSet<E> {
    private final List<E> inverseList;

    private final List<E> elementsList;

    private final Comparator<? super E> actualComparator;

    private final Comparator<? super E> comparator;

    public ArraySet(Collection<? extends E> collection, Comparator<? super E> comparator) {
        this.comparator = comparator;
        this.actualComparator = Objects.requireNonNullElseGet(comparator, Comparator::naturalOrder);
        elementsList = new ArrayList<>();
        List<E> tmp = new ArrayList<>(collection);
        tmp.sort(comparator);
        for (int i = 0; i < tmp.size(); i++) {
            if (!elementsList.isEmpty()) {
                E lastElement = elementsList.get(elementsList.size() - 1);
                if (comparator != null) {
                    if (comparator.compare(tmp.get(i), lastElement) == 0) {
                        continue;
                    }
                }
            }
            elementsList.add(tmp.get(i));
        }
        inverseList = new ArrayList<>(elementsList);
        Collections.reverse(inverseList);
    }

    public ArraySet() {
        inverseList = new ArrayList<>();
        elementsList = new ArrayList<>();
        actualComparator = (e1, e2) -> 0;
        comparator = null;
    }

    private ArraySet(List<E> elementsList, List<E> inverseList, Comparator<? super E> comparator,
                     Comparator<? super E> actualComparator) {
        this.inverseList = inverseList;
        this.elementsList = elementsList;
        this.actualComparator = actualComparator;
        this.comparator = comparator;
    }

    public ArraySet(Collection<? extends E> collection) {
        this(collection, null);
    }

    private int insertionIndex(E e) {
        return Collections.binarySearch(elementsList, e, actualComparator);
    }

    private E checkedGet(int index) {
        if (-1 < index && index < elementsList.size()) {
            return elementsList.get(index);
        } else {
            return null;
        }
    }

    private int lowerIndex(E e, boolean inclusive) {
        int index = insertionIndex(e);
        if (index < 0) {
            return -index - 2;
        }
        return inclusive ? index : index - 1;
    }

    private int higherIndex(E e, boolean inclusive) {
        int index = insertionIndex(e);
        if (index < 0) {
            return -index - 1;
        }
        return inclusive ? index : index + 1;
    }

    @Override
    public E lower(E e) {
        return checkedGet(lowerIndex(e, false));
    }

    @Override
    public E floor(E e) {
        return checkedGet(lowerIndex(e, true));
    }

    @Override
    public E higher(E e) {
        return checkedGet(higherIndex(e, false));
    }

    @Override
    public E ceiling(E e) {
        return checkedGet(higherIndex(e, true));
    }

    @Override
    public E pollFirst() {
        throw new UnsupportedOperationException("Poll first is not supported");
    }

    @Override
    public E pollLast() {
        throw new UnsupportedOperationException("Poll last is not supported");
    }

    @Override
    public boolean contains(Object o) {
        return insertionIndex((E) o) >= 0;
    }

    @Override
    public Iterator<E> iterator() {
        return Collections.unmodifiableList(elementsList).iterator();
    }

    @Override
    public NavigableSet<E> descendingSet() {
        if (comparator != null) {
            return new ArraySet<>(inverseList, elementsList, comparator.reversed(), actualComparator.reversed());
        }
        return new ArraySet<>(inverseList, elementsList, null, actualComparator.reversed());
    }

    @Override
    public Iterator<E> descendingIterator() {
        return descendingSet().iterator();
    }

    @Override
    public NavigableSet<E> tailSet(E e, boolean condition) {
        int pos;
        if (condition) {
            pos = lowerIndex(e, false) + 1;
        } else {
            pos = lowerIndex(e, true) + 1;
        }
        return new ArraySet<>(elementsList.subList(pos, size()),
                inverseList.subList(0, size() - pos), comparator, actualComparator);
    }

    @Override
    public NavigableSet<E> headSet(E e, boolean condition) {
        int pos;
        if (condition) {
            pos = lowerIndex(e, true) + 1;
        } else {
            pos = lowerIndex(e, false) + 1;
        }
        return new ArraySet<>(elementsList.subList(0, pos),
                inverseList.subList(size() - pos, size()), comparator, actualComparator);
    }

    @Override
    public NavigableSet<E> subSet(E e, boolean firstCondition, E e1, boolean secondCondition) {
        if (actualComparator.compare(e, e1) <= 0) {
            return headSet(e1, secondCondition).tailSet(e, firstCondition);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public SortedSet<E> subSet(E e, E e1) {
        return subSet(e, true, e1, false);
    }

    @Override
    public SortedSet<E> headSet(E e) {
        return headSet(e, false);
    }

    @Override
    public SortedSet<E> tailSet(E e) {
        return tailSet(e, true);
    }

    @Override
    public E first() {
        if (!isEmpty()) {
            return elementsList.get(0);
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public E last() {
        if (!isEmpty()) {
            return elementsList.get(size() - 1);
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public int size() {
        return elementsList.size();
    }
}