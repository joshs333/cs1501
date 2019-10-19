/** A PHPArray is a hybrid of a hash table and a linked list.
* It allows hash table access, indexed integer access, and
* sequential access.
* @author Sherif Khattab
*
**/

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.Comparator;
import java.lang.NumberFormatException;

public class PHPArray<T> implements Iterable<T> {
    /**** Some static parameters ****/
    //! The inital capacity for a hash table
    private static final int INIT_CAPACITY = 4;
    //! The threshold at which the table should be resized
    //! triggerd by (entry_count >= table_size / INCREASE_SIZE_THRESHOLD).
    private static final int INCREASE_SIZE_THRESHOLD = 2;
    //! How much to increase the size of the table when it reaches the threshold
    private static final int INCREASE_SIZE_RATIO = 2;
    //! The threshold at which the table should be resized
    //! triggered by (entry_count <= table_size / DECREASE_SIZE_RATIO).
    private static final int DECREASE_SIZE_THRESHOLD = 8;
    //! How much to decreases the size of the table when it reaches the threshold
    private static final int DECREASE_SIZE_RATIO = 2;

    /**** Usable variables ****/
    //! The number of key-value pairs contained
    private int entry_count;
    //! entries.length (table size)
    private int table_size;
    //! The table containing pair entries
    private Pair<T>[] entries;
    //! Head of the doubly linked list preserving insertion order
    private Pair<T> head;
    //! Tail of the double linked list preserving insertion order
    private Pair<T> tail;
    //! Pointer to entries on the linked list used to iterate via 'each()'
    private Pair<T> each_pointer;

    /************************************/
    /**** Basic Hash Table Functions ****/
    /************************************/
    /**
     * @brief creates an empty hash table with INIT_CAPACITY entries
     **/
    public PHPArray() {
        this(INIT_CAPACITY);
    } /* PHPArray() */

    /**
     * @brief creates a PHPArray with a given size
     * @param initial_capacity the starting size of the hash table
     **/
    public PHPArray(int initial_capacity) {
        // Set up the has table
        table_size = initial_capacity;

        // TODO(joshua.spisak): figure out how to make this prettier
        @SuppressWarnings("unchecked")
        Pair<T>[] temp = (Pair<T>[]) new Pair[table_size];
        entries = temp;

        // Set up the linked list
        head = tail = null;
        entry_count = 0;
    } /* PHPArray(initial_capacity) */

    /**
     * @brief allows the insertion of a value with an integer key
     * @param key the integer to use as a key
     * @param val the value to associate with the key
     * @details converts the integer to a string an puts that
     **/
    public void put(Integer key, T val) {
        put(key.toString(), val);
    } /* put(int, V) */

    /**
     * @brief insert a key value pair into the hash table
     * @param key the string to use as the key in the table
     * @param val the value to associate with the key
     **/
    public void put(String key, T val) {
        // Handle Removing the value
        if (val == null)
            unset(key);

        // increase table size based on parameters
        if (entry_count >= table_size / INCREASE_SIZE_THRESHOLD)
            resize(INCREASE_SIZE_RATIO * table_size);

        // find an empty entry in the table
        int i;
        for (i = hash(key); entries[i] != null; i = (i + 1) % table_size) {
            // update the value if key already exists
            if (entries[i].key.equals(key)) {
                entries[i].value = val;
                return;
            }
        }

        // Create the new entry
        entries[i] = new Pair<T>(key, val);

        // Handle the linked list
        if(head == null) {
            each_pointer = head = tail = entries[i];
        } else {
            entries[i].prev = tail;
            tail.next = entries[i];
            tail = entries[i];
        }
        ++entry_count;
    } /* put(String, V) */

    /**
     * @brief gets an integer key from the table
     * @param key the key to retrieve
     * @return the values associated with the key or null if it doesn't exist
     * @details converts the integer to a string and retrieves that
     **/
    public T get(Integer key) {
        return get(key.toString());
    } /* get(int) */

    /**
     * @brief gets a key from the table
     * @param key the key to retrieve
     * @return the values associated with the key or null if it doesn't exist
     **/
    public T get(String key) {
        for (int i = hash(key); entries[i] != null; i = (i + 1) % entry_count)
            if (entries[i].key.equals(key))
                return entries[i].value;
        return null;
    } /* get(string) */

    /**
     * @brief unsets a value from the table by an integer key
     * @param key the key to unset in the table
     * @details converts the integer to a string and unsets that key
     **/
    public void unset(Integer key) {
        unset(key.toString());
    } /* unset(Integer) */

    /**
     * @brief unsets a value from the table by a key
     * @param key the key to unset in the table
     **/
    public void unset(String key) {
        // If we can't find it then exit
        if (get(key) == null)
            return;

        // find where the key is (i)
        int i;
        for(i = hash(key); !key.equals(entries[i].key); i = (i + 1) % table_size);

        // delete node from hash table
        Pair<T> toDelete = entries[i];
        entries[i] = null;

        // delete node from linked list
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
        i = (i + 1) % table_size;
        while (entries[i] != null) {
            // delete and reinsert
            Pair<T> nodeToRehash = entries[i];
            entries[i] = null;
            rehash(nodeToRehash);
            i = (i + 1) % table_size;
        }

        --entry_count;

        // halves size of array if it's 12.5% full or less
        if (entry_count > 0 && entry_count <= table_size / DECREASE_SIZE_THRESHOLD)
            resize(table_size / DECREASE_SIZE_RATIO);
    } /* unset(String) */

    /**
     * @brief gets the number of entries in the table
     * @return the entry_count
     **/
    public int length() {
        return entry_count;
    } /* length */

    /**
     * @brief gets a list of keys
     * @return an ArrayList of keys in the table
     **/
    public ArrayList<String> keys() {
        ArrayList<String> result = new ArrayList<String>();
        for(Pair<T> it = head; it != null; it = it.next) {
            result.add(it.key);
        }
        return result;
    } /* keys */

    /**
     * @brief gets a list of values
     * @return an ArrayList of values in the table
     **/
    public ArrayList<T> values() {
        ArrayList<T> result = new ArrayList<T>();
        for(Pair<T> it = head; it != null; it = it.next) {
            result.add(it.value);
        }
        return result;
    } /* values */

    /**
     * @brief prints the contents of the hash table (utility function)
     **/
    public void showTable() {
        for(int i = 0; i < entries.length; ++i) {
            if(entries[i] == null)
                System.out.println(i + ": null");
            else
                System.out.println(i + ": Key: " + entries[i].key + " Value: " + entries[i].value);
        }
    } /* show table */

    /**
     * @brief flips the array keys/values if the values are strings
     * @return a new PHPArray with flipped values
     **/
    @SuppressWarnings("unchecked")
    public PHPArray<String> array_flip() {
        PHPArray<String> temp = new PHPArray<String>(table_size);

        for(Pair<String> it = (Pair<String>)head; it != null; it = it.next) {
            temp.put((String)it.value, it.key);
        }
        return temp;
    } /* array_flip */

    /***************************************/
    /**** Internal Hash Table Functions ****/
    /***************************************/
    /**
     * @brief resizes the table to a higher capacity
     * @param new_capacity the new capacity of the table
     * @details creates a new hash table of the new capacity, inserts all the
     *  current pairs in order then adopts the new hash tables values
     **/
    private void resize(int new_capacity) {
        // sim a hash table then adopt the values contained
        PHPArray<T> sim_hash_table = new PHPArray<T>(new_capacity);

        // Save the initial each_pointer then use each()
        Pair<T> initial_each_pointer = each_pointer;
        this.reset();
        Pair<T> node;
        while((node = this.each()) != null){
            sim_hash_table.put(node.key, node.value);
            // Save the node where the initial_each_pointer was
            if(node == initial_each_pointer) {
                sim_hash_table.each_pointer = sim_hash_table.tail;
            }
        }

        // Adopt the values
        entry_count     = sim_hash_table.entry_count;
        table_size      = sim_hash_table.table_size;
        entries         = sim_hash_table.entries;
        head            = sim_hash_table.head;
        tail            = sim_hash_table.tail;
        each_pointer    = sim_hash_table.each_pointer;
    } /* resize */

    /**
     * @brief rehashes a node after a prior node in the cluster has been removed
     * @param node the node to rehash into the table
     **/
    private void rehash(Pair<T> node){
        int i;
        // Find the next empty entry in the table
        for (i = hash(node.key); entries[i] != null; i = (i + 1) % table_size);
        entries[i] = node;
    } /* rehash */

    /**
     * @brief hashs a key
     * @return an int between 0 and table_size - 1 (withing range of table indexes)
     **/
    private int hash(String key) {
        return (key.hashCode() & 0x7fffffff) % table_size;
    } /* hash */

    /**
     * @brief pair of keys/values in a linked list of values in the array
     **/
    static public class Pair<T> {
        //! The node key
        public String key;
        //! The node value
        public T value;
        //! The next pair in the linked list
        private Pair<T> next;
        //! The previous pair in the linked list
        private Pair<T> prev;

        /**
         * @brief creates a new pair with no other linked nodes
         * @param key the key of this node
         * @param value the value of this node
         **/
        Pair(String key, T value){
            this(key, value, null, null);
        } /* Pair(key, value) */

        /**
         * @brief creates a new pair and links to other nodes
         * @param key the key of this node
         * @param value the value of this node
         * @param next the next node in the list
         * @param prev the previous node in the list
         **/
        Pair(String key, T value, Pair<T> next, Pair<T> prev){
            this.key = key;
            this.value = value;
            this.next = next;
            this.prev = prev;
        } /* Pair(key, value, next, prev) */

        /**
         * @brief copies the data in this node
         * @return a copy of this node without the list context
         **/
        Pair<T> copy() {
            return new Pair<T>(key, value);
        } /* copy */

        /**
         * @brief deep copies the data in this node and the attached nodes
         * @returns a copy of this node with copies the following nodes as well
         **/
        Pair<T> deepCopy() {
            Pair<T> new_head = this.copy();
            if(this.next != null)
                new_head.next = this.next.deepCopy();
            return new_head;
        } /* deepCopy */
    } /* Pair<T> */

    /***************************************/
    /**** Iteration Functions and types ****/
    /***************************************/
    /**
     * @brief makes an iterator for this array
     * @return an iterator for this data starting at the head (in order of insertion)
     **/
    public Iterator<T> iterator() {
        return new PairIterator(head);
    }

    /**
     * @brief an iterator class for the pairs contained in the array
     **/
    private class PairIterator implements Iterator<T> {
        //! The current pair in the iterator
        private Pair<T> current;

        /**
         * @brief creates a new iterator
         * @param start the node to start the iterator at
         **/
        public PairIterator(Pair<T> start) {
            current = start;
        }

        /**
         * @brief sees whether or not there are more nodes to iterate over
         * @return true if there are more nodes, false if not
         **/
        public boolean hasNext() {
            return current != null;
        }

        /**
         * @brief gets the next value and increments the iterator to the next pair
         * @return the current value of the iterator
         **/
        public T next() {
            if(!hasNext()) {
                throw new NoSuchElementException();
            }
            T result = current.value;
            current = current.next;
            return result;
        }
    } /* PairIterator */

    /**
     * @brief provides iteration over the Pairs
     * @return a copy of the current pair (unlinked from the list)
     **/
    public Pair<T> each() {
        Pair<T> current = each_pointer;
        if(current == null || current.next == null) {
            each_pointer = null;
        } else {
            each_pointer = current.next;
        }
        if(current != null) {
            return current.copy();
        } else {
            return current;
        }
    } /* each */

    /**
     * @brief resets the pointer for the 'each()' iterator to the head'
     **/
    public void reset() {
        each_pointer = head;
    } /* reset */

    /*******************************************/
    /**** Table Sorting Functions and types ****/
    /*******************************************/
    // TODO(joshua.spisak): if I have time, make my sorting stuff more general
    /**
     * @brief a sorted list used in merge sort, also contains links to the next
     *  sorted list.
     **/
    public class SortedList<Q> {
        //! The head of the (should be) sorted linked list
        Pair<Q> head;
        //! The tail of the (should be) sorted linked list
        Pair<Q> tail;
        //! The next sorted list
        SortedList next_list;

        /**
         * @brief makes a new sorted list
         * @param start the head of the sorted linked list
         **/
        SortedList(Pair<Q> start, Pair<Q> end) {
            head = start;
            tail = end;
        }
    }

    @SuppressWarnings("unchecked")
    public <C extends Comparable<? super C>> void sort() {;
        Comparator<Pair<C>> comparator = new Comparator<Pair<C>>() {
            @Override
            public int compare(Pair<C> left, Pair<C> right) {
                return left.value.compareTo(right.value);
            }
        };
        executeMergeSort(comparator, false);
        // Take the sorted list
    }

    public <C extends Comparable<? super C>> void asort() {
        Comparator<Pair<C>> comparator = new Comparator<Pair<C>>() {
            @Override
            public int compare(Pair<C> left, Pair<C> right) {
                return left.value.compareTo(right.value);
            }
        };
        executeMergeSort(comparator, true);
    }


    public <C extends Comparable<? super C>> void
    executeMergeSort(Comparator<Pair<C>> comparer, boolean keep_keys) {
        boolean rename_keys = !keep_keys; // makes the code more readable
        SortedList<C> root = buildSortLists((Pair<C>)head, comparer);
        // We will sort until we only have one list
        while(root.next_list != null) {
            // We will sort each of the list for each iteration
            for(SortedList<C> current_list = root; current_list != null; current_list = current_list.next_list) {
                // If we have can sort two lists together...
                SortedList<C> next_list = current_list.next_list;
                if(next_list != null) {
                    // We now combine two sorted lists
                    Pair<C> list_a = current_list.head;
                    Pair<C> list_b = current_list.next_list.head;
                    // We put it in current_list
                    SortedList<C> new_list = null;
                    if(root.next_list.next_list == null && rename_keys) {
                        // We will let this function efficiently generate a
                        // whole new hash table and fill the current one in
                        new_list = combineSortedPairs(list_a, list_b, comparer, true);
                        return;
                    }else {
                        new_list = combineSortedPairs(list_a, list_b, comparer, false);
                        current_list.head = new_list.head;
                        current_list.tail = new_list.tail;
                        // Get rid of the now empty next_list
                        current_list.next_list = next_list.next_list;
                        next_list.head = null;
                        next_list.next_list = null;
                    }
                } else if(rename_keys) {
                    // I don't think we ever get here and care...
                    // Integer i = 0;
                    // for(Pair<C> value_to_add = current_list.head; value_to_add != null; value_to_add = value_to_add.next) {
                    //     //unset(value_to_add.key);
                    //     //put(i, (T) value_to_add.value);
                    //     ++i;
                    // }
                }
            }
        }
        this.head = (Pair<T>)root.head;
        this.tail = (Pair<T>)root.tail;
    }

    private <C extends Comparable<? super C>> SortedList<C>
    combineSortedPairs(Pair<C> list_a, Pair<C> list_b, Comparator<Pair<C>> comparer, boolean rename_keys) {
        PHPArray<T> sim_hash_table = null;
        if(rename_keys) {
            sim_hash_table = new PHPArray<T>(table_size);
        }
        Pair<C> result_head = null;
        Pair<C> result_current = null;
        // We sort from both sub sorted lists until they are both empty
        Integer i = 0;
        while(list_a != null || list_b != null) {
            // We will pick the smallest value from the lists available
            Pair<C> value_to_add;
            if(list_a != null && list_b != null) {
                // we need to compare....
                if(comparer.compare(list_a, list_b) > 0) {
                    // list a > list b (take b)
                    value_to_add = list_b;
                    list_b = list_b.next;
                } else {
                    // list b > list a (take a)
                    value_to_add = list_a;
                    list_a = list_a.next;
                }
            } else if(list_a != null) {
                // Only list a exists.. so lets take that
                value_to_add = list_a;
                list_a = list_a.next;
            } else {
                // Only list b exists.. so lets take that
                value_to_add = list_b;
                list_b = list_b.next;
            }
            if(rename_keys){
                // Do a similar trick to resizing the table...
                // This feels dirty lol
                sim_hash_table.put(i, (T) value_to_add.value);
            } else {
                if(result_head == null) {
                    result_head = result_current = value_to_add;
                } else {
                    // Build a new linked list...
                    value_to_add.prev = result_current;
                    value_to_add.next = null;
                    result_current.next = value_to_add;
                    result_current = value_to_add;
                }
            }
            ++i;
        } /* while(either list is not empty) */
        if(rename_keys) {
            entry_count     = sim_hash_table.entry_count;
            table_size      = sim_hash_table.table_size;
            entries         = sim_hash_table.entries;
            head            = sim_hash_table.head;
            tail            = sim_hash_table.tail;
            each_pointer    = sim_hash_table.each_pointer;
            return null;
        }
        return new SortedList<C>(result_head, result_current);
    } /* combineSortedPairs */

    /**
     * @brief this both builds the data structs for a merge sort of a
     *  linked list and also performs the first comparisons (lowest pairs)
     * @param list the list to put into SortedLists
     * @param comparer the comparison function to use to sort
     * @return a SortedList (is a linked list of sorted lists) of all the values in list
     **/
    private <C extends Comparable<? super C>> SortedList<C>
    buildSortLists(Pair<C> list, Comparator<Pair<C>> comparer) {
        SortedList<C> head = null;
        SortedList<C> current_sort_node = null;
        for(Pair<C> list_node = list; list_node != null;) {
            SortedList<C> new_list;
            if(list_node.next != null) {
                // We can compare the current node and the next
                Pair<C> node_a = list_node;
                Pair<C> node_b = list_node.next;
                if(comparer.compare(node_a, node_b) > 0) {
                    // node_a > node_b (swap order)
                    node_a.next = node_b.next;
                    node_b.prev = null;
                    node_a.prev = node_b;
                    node_b.next = node_a;
                    new_list = new SortedList<C>(node_b, node_a);
                } else {
                    // next_node >= node (keep order)
                    node_a.prev = null;
                    new_list = new SortedList<C>(node_a, node_b);
                }
            } else {
                // The list has an odd number of values, we can create a sorted
                // list with the last value
                new_list = new SortedList<C>(list_node, list_node);
            }
            if(head == null) {
                // Start the sorted lists
                head = current_sort_node = new_list;
            } else {
                // Append the new sorted list in order
                current_sort_node.next_list = new_list;
                current_sort_node = new_list;
            }
            list_node = new_list.tail.next;
            new_list.tail.next = null;
        }
        return head;
    } /* buildSortLists */
} /* PHPArray */
