package pl.eurekin.experimental;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ObservableListWrapper<T> implements ObservableList<T> {

    private List<T> delegateList;
    private StatelessPropertyChangeSupport<List<T>> changeSupport = new StatelessPropertyChangeSupport<>();

    public ObservableListWrapper(List<T> delegateList) {
        this.delegateList = delegateList;
    }

    public boolean add(T t) {
        boolean add = delegateList.add(t);
        changed();
        return add;
    }

    public void changed() {
        changeSupport.firePropertyChangeEvent(null, this);
    }

    public void add(int index, T element) {
        delegateList.add(index, element);
        changed();
    }

    public boolean addAll(Collection<? extends T> c) {
        boolean b = delegateList.addAll(c);
        changed();
        return b;
    }

    public boolean addAll(int index, Collection<? extends T> c) {
        boolean b = delegateList.addAll(index, c);
        changed();
        return b;
    }

    @Override
    public void clear() {
        delegateList.clear();
        changed();
    }

    @Override
    public boolean contains(Object o) {
        return delegateList.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegateList.containsAll(c);
    }

    @Override
    public boolean equals(Object o) {
        return delegateList.equals(o);
    }

    @Override
    public T get(int index) {
        return delegateList.get(index);
    }

    @Override
    public int hashCode() {
        return delegateList.hashCode();
    }

    @Override
    public int indexOf(Object o) {
        return delegateList.indexOf(o);
    }

    @Override
    public boolean isEmpty() {
        return delegateList.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return delegateList.iterator();
    }

    @Override
    public int lastIndexOf(Object o) {
        return delegateList.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return delegateList.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return delegateList.listIterator(index);
    }

    @Override
    public T remove(int index) {
        T remove = delegateList.remove(index);
        changed();
        return remove;
    }

    @Override
    public boolean remove(Object o) {
        boolean remove = delegateList.remove(o);
        changed();
        return remove;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean b = delegateList.removeAll(c);
        changed();
        return b;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean b = delegateList.retainAll(c);
        changed();
        return b;
    }

    public T set(int index, T element) {
        T set = delegateList.set(index, element);
        changed();
        return set;
    }

    @Override
    public int size() {
        return delegateList.size();
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return delegateList.subList(fromIndex, toIndex);
    }

    @Override
    public Object[] toArray() {
        return delegateList.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return delegateList.toArray(a);
    }

    @Override
    public void registerChangeListener(ChangedPropertyListener<List<T>> listener) {
        changeSupport.registerNewListener(listener);
    }

    @Override
    public void unregisterChangeListener(ChangedPropertyListener<List<T>> listener) {
        changeSupport.unregisterListener(listener);
    }
}
