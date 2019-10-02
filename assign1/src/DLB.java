/**
 * @file DLB.java
 * @author Joshua Spisak <jjs231@pitt.edu>
 * @date 09/18/2019
 * @brief implements a DLB tree to store strings
 **/

/**
 * @brief a node in the DLB tree. Any node can serve as root.
 **/
public class DLB implements DictInterface {
    //! The value of this node
    private char value;
    //! Boolean flag to mark this node as the end of a word
    private boolean end;
    //! Reference to the child nodes
    private DLB child;
    //! Reference to the sibling node that for some reason I called 'brother'...
    private DLB brother;

    /**
     * @brief makes the root DLB node
     **/
    public DLB() {
        value = 0;
        end = false;
    }

    /**
     * @brief makes a DLB node of a certain value
     **/
    public DLB(char value) {
        this.value = value;
        end = false;
    }

    /**
     * @brief inplements the add method of the Dictionary interface
     * @param value the string to add to the dictionary
     * @return true if inserted, false if not
     **/
    public boolean add(String value) {
        return add(value, 0);
    }

    /**
     * @brief implements add for the DLB tree
     * @param value the string to add in
     * @param index the current index of that string being inserted
     * @return true if successfully inserted, false if not
     **/
    public boolean add(String value, int index) {
        // Sees if this node is the value being added
        if(this.value == value.charAt(index)) {
            // If there is more to add
            if(index < value.length() - 1) {
                // Create the child if none exists (with that value)
                if(this.child == null) {
                    this.child = new DLB(value.charAt(index + 1));
                }
                // Then add the string to that child
                this.child.add(value, index + 1);
            } else {
                // We are the value and there is no more to add... we are an end
                this.end = true;
            }
        // We are not the value that needs added
        } else {
            // Make a brother to accept the value if none exist
            if(this.brother == null) {
                this.brother = new DLB(value.charAt(index));
            }
            // Add the string to them
            this.brother.add(value, index);
        }
        // We always are successful! :-)
        return true;
    } /* add(String value, int index) */

    /**
     * Implements a searchPrefix function from DictInterface
     **/
    public int searchPrefix(StringBuilder s) {
        return searchPrefix(s, 0, s.length()-1, 0);
    }

    /**
     * Implements a searchPrefix function from DictInterface
     **/
    public int searchPrefix(StringBuilder s, int start, int end) {
        return searchPrefix(s, start, end, start);
    }

    /**
     * @brief implements the DLB search prefix
     * @param s the string to look up
     * @param start the start index to look in the string builder for
     * @param end the end index to look in the string builder for
     * @param index the current index of the character we are looking
     *  for in the string
     * @return 0: if the string does not exist at all in the dictionary
     *  1: if the string exists only as a prefix in the dictionary
     *  2: if the string exists only as a word in the dictionary
     *  3: if the string exists both as a word and a prefix in the dictionary
     **/
    public int searchPrefix(StringBuilder s, int start, int end, int index) {
        // Similar logic to the add function, but returns based on what exists
        //  instead of creating new children/brethren
        if(this.value == s.charAt(index)) {
            // We have more string to find
            if(index < end) {
                // If we don't have children
                if(this.child == null) {
                    // This is nothing
                    return 0;
                } else {
                    // Else search the child
                    return this.child.searchPrefix(s, start, end, index + 1);
                }
            // We have no more string to search
            } else {
                // If we have no child
                if(this.child == null) {
                    if(this.end) {
                        // we are a word but not prefix
                        return 2;
                    } else {
                        // If we have no children this should also be an end
                        System.out.println("You somehow messed up!");
                        return 0;
                    }
                } else {
                    if(this.end) {
                        // we are a word and a prefix
                        return 3;
                    } else {
                        // we are only a prefix
                        return 1;
                    }
                }
            }
        } else {
            // Check the brother for that value if he exists
            if(this.brother == null) {
                // this value does not exist
                return 0;
            } else {
                return this.brother.searchPrefix(s, start, end, index);
            }
        }
    } /* searchPrefix(StringBuilder s, int start, int end, int index) */

    /**
     * @brief allows words to be deleted from the dictionary
     * @param string the string to delete
     * @return true if the string was found and deleted, false if not
     **/
    public boolean delete(String string) {
        // NOTE(joshua.spisak): This could be dangerous if it tries to
        //  delete the root node... but the root node will never contain
        //  the value that needs deleted and will always defer to the sibling
        //  or return false.
        //
        // Arguably better is to just call delete on the brother...
        return this.delete(string, this, 0);
    } /* delete(StringBuilder string) */

    /**
     * @brief allows words to be deleted from the dictionary
     * @param string the string to delete
     * @param parent the node that called delete on the current one
     * @param index the current position in the string being deleted
     * @return true if the string was found and deleted, false if not
     **/
    public boolean delete(String string, DLB parent, int index) {
        if(this.value == string.charAt(index)) {
            boolean deleted;
            if(index < string.length() - 1) {
                if(this.child == null) {
                    return false;
                }
                deleted = this.child.delete(string, this, index + 1);
            } else {
                // I am what is being deleted!!!!! :)
                // Make sure I am actually the word...
                if(!this.end) {
                    deleted = false;
                } else {
                    // I am no longer a word...
                    this.end = false;
                    deleted = true;
                }
            }
            // If a node was deleted perform pruning
            if(deleted) {
                // Pruning means: If I am not an end node and have no children
                //  Then I disappear.
                if(this.end == false) {
                    if(this.child == null) {
                        // Replace the parents relationship from me to my brother..
                        if(parent.child == this) {
                            parent.child =  this.brother;
                        } else {
                            parent.brother =  this.brother;
                        }
                    }
                }
            }
            return deleted;
        } else {
            if(this.brother == null) {
                return false;
            }
            return this.brother.delete(string, this, index);
        }
    } /* delete(StringBuilder string, DLB parent, int index) */

    /**
     * @brief prints out the contents of the tree
     * @param verbose whether or not to print only the words or all links
     **/
    public void print_nodes(boolean verbose) {
        this.print_nodes(new StringBuilder(), verbose);
    }

    /**
     * @brief prints out the contents of the tree
     * @param parent_data the string prefix showing the links from the parent
     * @param verbose whether or not to print only the words or all links
     **/
    public void print_nodes(StringBuilder parent_data, boolean verbose) {
        if(this.value == 0) {
            parent_data.append("ROOT");
        } else {
            parent_data.append("->" + value);
        }
        if(this.end) {
            System.out.println(parent_data.toString());
        }
        if(this.child != null) {
            this.child.print_nodes(parent_data, false);
        }
        if(this.value != 0)
            parent_data.delete(parent_data.length() - 3, parent_data.length());
        if(this.brother != null) {
            this.brother.print_nodes(parent_data, true);
        }
    } /* print_nodes(StringBuilder parent_data, boolean verbose) */

} /* DLB */
