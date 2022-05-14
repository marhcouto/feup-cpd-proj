package utils;

import java.util.*;

public class FixedSizeCircularArray<E> {
    private List<E> internalArray = new ArrayList<>();
    private int curIndex = 0;
    private int maxSize;

    public FixedSizeCircularArray(int maxSize) {
        this.maxSize = maxSize;
    }

    public FixedSizeCircularArray<E> add(E elem) {
        if (internalArray.size() == maxSize) {
            internalArray.set(curIndex, elem);
        } else {
            internalArray.add(elem);
        }
        curIndex = (curIndex + 1) % maxSize;
        return this;
    }

    public E get(int index) {
        return internalArray.get(index);
    }

    @Override
    public String toString() {
        return internalArray.toString();
    }
}
