package A_Star_simulation;

import java.util.ArrayList;

public class SortedArrayListPQ extends ArrayList<Node> {
    int n = 0;

    @Override
    public int size() {
        return n;
    }

    @Override
    public boolean add(Node node) {
        n += 1;

        if (this.isEmpty()) {
            this.add(0, node);
        } else {
            this.add(this.size() - 1, node);
            for (int i = 0; i < this.size(); i++) {
                if (node.get_fValue() >= this.get(i).get_fValue()) {
                    for (int j = this.size() - 1; j > i; j--) {
                        Node temp = this.get(j - 1);
                        this.set(j, temp);
                    }

                    this.set(i, node);
                    break;
                }
            }
        }

        return true;
    }

    @Override
    public Node remove(int index) {
        n -= 1;
        return super.remove(index);
    }

    @Override
    public boolean remove(Object o) {
        n -= 1;
        return super.remove(o);
    }

    public boolean insert(Node node) {
        return add(node);
    }

    public Node min() {
        return this.get(this.size()-1);
    }

    public Node removeMin() {
        return remove(this.size()-1);
    }
}
