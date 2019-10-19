/** A PHPArray is a hybrid of a hash table and a linked list.
* It allows hash table access, indexed integer access, and
* sequential access.
* @author Sherif Khattab
*
**/

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ArrayList;

public class PHPArray<V> implements Iterable<V> {
    private static final int INIT_CAPACITY = 4;

    private int N;           // number of key-value pairs in the symbol table
    private int M;           // size of linear probing table
    private Pair<V>[] entries;  // the table
    private Pair<V> head;       // head of the linked list
    private Pair<V> tail;       // tail of the linked list
    private Pair<V> each_pointer;

    // create an empty hash table - use 16 as default size
    public PHPArray() {
        this(INIT_CAPACITY);
    }

    // create a PHPArray of given capacity
    public PHPArray(int capacity) {
        M = capacity;
        @SuppressWarnings("unchecked")
        Pair<V>[] temp = (Pair<V>[]) new Pair[M];
        entries = temp;
        head = tail = null;
        N = 0;
    }

    public void put(Integer key, V val) {
        put(key.toString(), val);
    }

    // insert the key-value pair into the symbol table
    public void put(String key, V val) {
        if (val == null) unset(key);

        // double table size if 50% full
        if (N >= M/2) resize(2*M);

        // linear probing
        int i;
        for (i = hash(key); entries[i] != null; i = (i + 1) % M) {
            // update the value if key already exists
            if (entries[i].key.equals(key)) {
                entries[i].value = val; return;
            }
        }

        // found an empty entry
        entries[i] = new Pair<V>(key, val);

        //insert the node into the linked list
        if(head == null) {
            each_pointer = head = tail = entries[i];
        } else {
            entries[i].prev = tail;
            tail.next = entries[i];
            tail = entries[i];
        }

        N++;
    }

    public V get(Integer key) {
        return get(key.toString());
    }

    // return the value associated with the given key, null if no such value
    public V get(String key) {
        for (int i = hash(key); entries[i] != null; i = (i + 1) % M)
            if (entries[i].key.equals(key))
                return entries[i].value;
        return null;
    }

    // resize the hash table to the given capacity by re-hashing all of the keys
    private void resize(int capacity) {
        PHPArray<V> temp = new PHPArray<V>(capacity);

        //rehash the entries in the order of insertion
        Pair<V> current = head;
        while(current != null){
            temp.put(current.key, current.value);
            current = current.next;
        }
        each_pointer = temp.each_pointer;
        entries = temp.entries;
        head    = temp.head;
        tail    = temp.tail;
        M       = temp.M;
    }

    // rehash a node while keeping it in place in the linked list
    private void rehash(Pair<V> node){
        // TODO Write the implementation of this function
        int i;
        for (i = hash(node.key); entries[i] != null; i = (i + 1) % M);
            entries[i] = node;
    }

    public void unset(Integer key) {
        unset(key.toString());
    }

    // delete the key (and associated value) from the symbol table
    public void unset(String key) {
        if (get(key) == null) return;

        // find position i of key
        int i = hash(key);
        while (!key.equals(entries[i].key)) {
            i = (i + 1) % M;
        }

        // delete node from hash table
        Pair<V> toDelete = entries[i];
        entries[i] = null;

        if(toDelete == head) {
            head = toDelete.next;
        }
        if(toDelete == tail) {
            tail = toDelete.prev;
        }
        if(toDelete.prev != null) {
            toDelete.prev.next = toDelete.next;
        }
        if(toDelete.next != null) {
            toDelete.next.prev = toDelete.prev;
        }

        // rehash all keys in same cluster
        i = (i + 1) % M;
        while (entries[i] != null) {
            // delete and reinsert
            Pair<V> nodeToRehash = entries[i];
            entries[i] = null;
            rehash(nodeToRehash);
            i = (i + 1) % M;
        }

        N--;

        // halves size of array if it's 12.5% full or less
        if (N > 0 && N <= M/8) resize(M/2);
    }

    // hash function for keys - returns value between 0 and M-1
    private int hash(String key) {
        return (key.hashCode() & 0x7fffffff) % M;
    }

    //An inner class to store nodes of a doubly-linked list
    //Each node contains a (key, value) pair
    static public class Pair<V> {
        public String key;
        public V value;
        private Pair<V> next;
        private Pair<V> prev;

        Pair(String key, V value){
            this(key, value, null, null);
        }

        Pair(String key, V value, Pair<V> next, Pair<V> prev){
            this.key = key;
            this.value = value;
            this.next = next;
            this.prev = prev;
        }
    }

    public Iterator<V> iterator() {
        return new MyIterator(head);
    }

    private class MyIterator implements Iterator<V> {
        private Pair<V> current;

        public MyIterator(Pair<V> start) {
            current = start;
        }

        public boolean hasNext() {
            return current != null;
        }

        public V next() {
            if(!hasNext()) {
                throw new NoSuchElementException();
            }
            V result = current.value;
            current = current.next;
            return result;
        }
    }

    public Pair<V> each() {
        Pair<V> current = each_pointer;
        if(current == null || current.next == null) {
            each_pointer = null;
        } else {
            each_pointer = current.next;
        }
        return current;
    }

    public void reset() {
        each_pointer = head;
    }

    public ArrayList<String> keys() {
        ArrayList<String> result = new ArrayList<String>();
        for(Pair<V> it = head; it != null; it = it.next) {
            result.add(it.key);
        }
        return result;
    }

    public ArrayList<V> values() {
        ArrayList<V> result = new ArrayList<V>();
        for(Pair<V> it = head; it != null; it = it.next) {
            result.add(it.value);
        }
        return result;
    }

    public void showTable() {
        for(int i = 0; i < entries.length; ++i) {
            if(entries[i] == null)
                System.out.println(i + ": null");
            else
                System.out.println(i + ": Key: " + entries[i].key + " Value: " + entries[i].value);
        }
    }

    @SuppressWarnings("unchecked")
    public void array_flip() {
        PHPArray<String> temp = new PHPArray<String>(M);

        for(Pair<String> it = (Pair<String>)head; it != null; it = it.next) {
            temp.put((String)it.value, it.key);
        }
        each_pointer = (Pair<V>)temp.each_pointer;
        entries = (Pair<V>[])temp.entries;
        head    = (Pair<V>)temp.head;
        tail    = (Pair<V>)temp.tail;
        M       = temp.M;
    }

    public void sort() {

    }

    public void asort() {

    }
}
