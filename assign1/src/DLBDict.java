

public class DLBDict implements DictInterface {
    private char value = 0;
    private boolean end = false;
    private DLBDict child;
    private DLBDict brother;

    public DLBDict() {
    }

    public DLBDict(char value) {
        this.value = value;
    }

    public boolean add(String value) {
        return add(value, 0);
    }

    public boolean add(String value, int index) {
        if(this.value == value.charAt(index)) {
            if(index + 1 < value.length()) {
                if(this.child == null) {
                    this.child = new DLBDict(value.charAt(index + 1));
                }
                this.child.add(value, index + 1);
            } else {
                this.end = true;
            }
        } else {
            if(this.brother == null) {
                this.brother = new DLBDict(value.charAt(index));
            }
            this.brother.add(value, index);
        }
        return true;
    }

    public int searchPrefix(StringBuilder s) {
        return searchPrefix(s, 0, s.length()-1, 0);
    }

    public int searchPrefix(StringBuilder s, int start, int end) {
        return searchPrefix(s, start, end, start);
    }

    public int searchPrefix(StringBuilder s, int start, int end, int index) {
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
            if(this.brother == null) {
                // this value does not exist
                return 0;
            } else {
                return this.brother.searchPrefix(s, start, end, index);
            }
        }
    }

}
