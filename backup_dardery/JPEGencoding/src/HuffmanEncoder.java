import java.util.*;

public class HuffmanEncoder<T> {
    class Node implements Comparable<Node> {
        T value;
        int freq;
        Node left, right;

        @Override
        public int compareTo(Node o) {
            //Integer.compare(freq, o.freq)
            return freq != o.freq ? Integer.compare(freq, o.freq) : 1;
        }
    }

    public Map<T, String> findEncoding(Map<T, Integer> mp) {
        PriorityQueue<Node> pq = new PriorityQueue<>();

        for (Map.Entry<T, Integer> x : mp.entrySet()) {
            Node node = new Node();
            node.value = x.getKey();
            node.freq = x.getValue();
            pq.add(node);
        }

        while (pq.size() > 1) {
            Node a = pq.poll();
            Node b = pq.poll();
            Node combined = new Node();
            combined.freq = a.freq + b.freq;
            combined.right = a;
            combined.left = b;
            pq.add(combined);
        }
        Map<T, String> vals = new HashMap<>();
        assignValues(vals, pq.poll(), "");

        return vals;
    }

    private void assignValues(Map<T, String> vals, Node poll, String thusfar) {
        if (poll.left == null || poll.right == null) {
            vals.put(poll.value, thusfar);
            return;
        }
        assignValues(vals, poll.left, thusfar + '0');
        assignValues(vals, poll.right, thusfar + '1');
    }
}
