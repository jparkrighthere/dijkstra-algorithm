
// --== CS400 File Header Information ==--
// Name: Jeonghyeon Park
// Email: jpark634@wisc.edu
// Group and Team: BM, red
// Group TA: Samuel Church
// Lecturer: Gary Dahl
// Notes to Grader: None

import java.util.PriorityQueue;
import java.util.Hashtable;
import java.util.List;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class extends the BaseGraph data structure with additional methods for
 * computing the total cost and list of node data along the shortest path
 * connecting a provided starting to ending nodes.  This class makes use of
 * Dijkstra's shortest path algorithm.
 */
public class DijkstraGraph<NodeType, EdgeType extends Number>
        extends BaseGraph<NodeType,EdgeType>
        implements GraphADT<NodeType, EdgeType> {

    /**
     * While searching for the shortest path between two nodes, a SearchNode
     * contains data about one specific path between the start node and another
     * node in the graph.  The final node in this path is stored in it's node
     * field.  The total cost of this path is stored in its cost field.  And the
     * predecessor SearchNode within this path is referened by the predecessor
     * field (this field is null within the SearchNode containing the starting
     * node in it's node field).
     *
     * SearchNodes are Comparable and are sorted by cost so that the lowest cost
     * SearchNode has the highest priority within a java.util.PriorityQueue.
     */
    protected class SearchNode implements Comparable<SearchNode> {
        public Node node;
        public double cost;
        public SearchNode predecessor;
        public SearchNode(Node node, double cost, SearchNode predecessor) {
            this.node = node;
            this.cost = cost;
            this.predecessor = predecessor;
        }
        public int compareTo(SearchNode other) {
            if( cost > other.cost ) return +1;
            if( cost < other.cost ) return -1;
            return 0;
        }
    }

    /**
     * This helper method creates a network of SearchNodes while computing the
     * shortest path between the provided start and end locations.  The
     * SearchNode that is returned by this method is represents the end of the
     * shortest path that is found: it's cost is the cost of that shortest path,
     * and the nodes linked together through predecessor references represent
     * all of the nodes along that shortest path (ordered from end to start).
     *
     * @param start the data item in the starting node for the path
     * @param end the data item in the destination node for the path
     * @return SearchNode for the final end node within the shortest path
     * @throws NoSuchElementException when no path from start to end is found
     *         or when either start or end data do not correspond to a graph node
     */
    protected SearchNode computeShortestPath(NodeType start, NodeType end) {
        //if start and end nodes are not in the graph, throw exception
        if(!nodes.containsKey(start) || !nodes.containsKey(end)) {
            throw new NoSuchElementException();
        }

        // Create priority queue and hashtable
        PriorityQueue<SearchNode> queue = new PriorityQueue<>();
        Hashtable<Node, Double> costs = new Hashtable<>();
        Hashtable<Node, SearchNode> predecessors = new Hashtable<>();

        // Find the start and end nodes
        Node startNode = nodes.get(start);
        Node endNode = nodes.get(end);

        // Initialize the cost of the start node to 0 and add it to the priority queue
        costs.put(startNode, 0.0);
        queue.add(new SearchNode(startNode, 0.0, null));

        // Loop until the priority queue is empty
        while (!queue.isEmpty()) {
            SearchNode current = queue.remove();

            //Return if current node is equal to the end node
            if (current.node == endNode) {
                return current;
            }

            //All edges that leaves current node
            List<Edge> possibleEdges = current.node.edgesLeaving;

            for (Edge edge : possibleEdges) {
                //Node objects for comparing each cost
                Node neighbor = edge.successor;
                double cost = costs.get(current.node) + edge.data.doubleValue();

                // If the neighbor node has not been visited yet or the cost to get to it is lower than
                // the previous cost, update its cost and predecessor and add it to the priority queue
                if (!costs.containsKey(neighbor) || cost < costs.get(neighbor)) {
                    costs.put(neighbor, cost);
                    SearchNode neighborNode = new SearchNode(neighbor, cost, current);
                    predecessors.put(neighbor, neighborNode);
                    queue.add(neighborNode);
                }
            }
        }
        //Throw exception if no path from start to end is found
        throw new NoSuchElementException();
    }


    /**
     * Returns the list of data values from nodes along the shortest path
     * from the node with the provided start value through the node with the
     * provided end value.  This list of data values starts with the start
     * value, ends with the end value, and contains intermediary values in the
     * order they are encountered while traversing this shorteset path.  This
     * method uses Dijkstra's shortest path algorithm to find this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end the data item in the destination node for the path
     * @return list of data item from node along this shortest path
     */
    public List<NodeType> shortestPathData(NodeType start, NodeType end) {
        SearchNode finalPath = computeShortestPath(start, end);
        //copy the final path
        LinkedList<NodeType> spd = new LinkedList<>();
        SearchNode current = finalPath;
        //Put all data in a new list
        while (current != null) {
            spd.addFirst(current.node.data);
            current = current.predecessor;
        }
        return spd;
    }

    /**
     * Returns the cost of the path (sum over edge weights) of the shortest
     * path freom the node containing the start data to the node containing the
     * end data.  This method uses Dijkstra's shortest path algorithm to find
     * this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end the data item in the destination node for the path
     * @return the cost of the shortest path between these nodes
     */
    public double shortestPathCost(NodeType start, NodeType end) {
        return computeShortestPath(start,end).cost;
    }

    //Junit Tests
    protected DijkstraGraph<String,Double> graph = null;

    @BeforeEach
    public void CreateInstance(){graph = new DijkstraGraph<>();}

    /**
     * This test checks if the method properly throws NoSuchElementException.
     */
    @Test
    public void JUnitTest1() {
        graph.insertNode("a");
        graph.insertNode("b");
        graph.insertNode("c");

        graph.insertEdge("a","b",1.0);
        graph.insertEdge("b","c",1.0);

        assertThrows(NoSuchElementException.class, () -> graph.computeShortestPath("a", "d"));
    }

    /**
     * This test checks if code functions properly with the examples from the class
     * The shortestPathCost should be 17.0.
     */
    @Test
    public void JUnitTest2() {
        //Insert nodes
        graph.insertNode("A");
        graph.insertNode("B");
        graph.insertNode("D");
        graph.insertNode("E");
        graph.insertNode("F");
        graph.insertNode("G");
        graph.insertNode("H");
        graph.insertNode("I");
        graph.insertNode("L");
        graph.insertNode("M");

        //Insert edges
        graph.insertEdge("A", "B", 1.0);
        graph.insertEdge("A", "H", 8.0);
        graph.insertEdge("A", "M", 5.0);
        graph.insertEdge("B", "M", 3.0);
        graph.insertEdge("D", "A", 7.0);
        graph.insertEdge("D", "G", 2.0);
        graph.insertEdge("F", "G", 9.0);
        graph.insertEdge("G", "L", 7.0);
        graph.insertEdge("H", "B", 6.0);
        graph.insertEdge("H", "I", 2.0);
        graph.insertEdge("I", "D", 1.0);
        graph.insertEdge("I", "L", 5.0);
        graph.insertEdge("M", "E", 3.0);
        graph.insertEdge("M", "F", 4.0);

        assertEquals(17.0, graph.shortestPathCost("D", "I"));
    }

    /**
     * This test checks if code functions properly with the examples from the class
     * The shortestPathData should be [D, A, H, I].
     */
    @Test
    public void JUnitTest3 () {
        //Insert nodes
        graph.insertNode("A");
        graph.insertNode("B");
        graph.insertNode("D");
        graph.insertNode("E");
        graph.insertNode("F");
        graph.insertNode("G");
        graph.insertNode("H");
        graph.insertNode("I");
        graph.insertNode("L");
        graph.insertNode("M");

        //Insert edges
        graph.insertEdge("A", "B", 1.0);
        graph.insertEdge("A", "H", 8.0);
        graph.insertEdge("A", "M", 5.0);
        graph.insertEdge("B", "M", 3.0);
        graph.insertEdge("D", "A", 7.0);
        graph.insertEdge("D", "G", 2.0);
        graph.insertEdge("F", "G", 9.0);
        graph.insertEdge("G", "L", 7.0);
        graph.insertEdge("H", "B", 6.0);
        graph.insertEdge("H", "I", 2.0);
        graph.insertEdge("I", "D", 1.0);
        graph.insertEdge("I", "L", 5.0);
        graph.insertEdge("M", "E", 3.0);
        graph.insertEdge("M", "F", 4.0);

        assertEquals("[D, A, H, I]", graph.shortestPathData("D", "I").toString());
    }

    @Test
    public void JUnitTest4() {
        /**
         * Leaving Edges
         * edges of a: b(3), d(7)
         * edges of b: e(1), d(2)
         * edges of c: f(1)
         * edges of d: f(2)
         * edges of e: c(1), f(4)
         * edges of f: none
         * ---------------------------
         * Start node = a, End node = f
         * Shortest path = a -> b -> e -> c -> f
         * Cost = 3(a,b) + 1(b,e) + 1(e,c) + 1(c,f) = 6
         */
        graph.insertNode("a");
        graph.insertNode("b");
        graph.insertNode("c");
        graph.insertNode("d");
        graph.insertNode("e");
        graph.insertNode("f");

        graph.insertEdge("a","b",3.0);
        graph.insertEdge("a","d",7.0);
        graph.insertEdge("b","e",1.0);
        graph.insertEdge("b","d",2.0);
        graph.insertEdge("c","f",1.0);
        graph.insertEdge("d","f",2.0);
        graph.insertEdge("e","f",4.0);
        graph.insertEdge("e","c",1.0);

        assertEquals("[a, b, e, c, f]",graph.shortestPathData("a","f").toString());
        assertEquals(6.0,graph.shortestPathCost("a","f"));
    }

    @Test
    public void JUnitTest5 () {
        /**
         * Leaving Edges
         * edges of S: H(5), Z(10)
         * edges of H: I(3), Z(9)
         * edges of N: E(4)
         * edges of Z: E(5)
         * edges of I: N(2), E(7)
         * edges of E: A(6)
         * edges of A: none
         * ---------------------------
         * Start node = S, End node = H
         * Shortest path = S -> H -> I -> N -> E
         * Cost = 5(S,H) + 3(H,I) + 2(I,N) + 4(N,E) = 14
         */
        graph.insertNode("S");
        graph.insertNode("H");
        graph.insertNode("N");
        graph.insertNode("Z");
        graph.insertNode("I");
        graph.insertNode("E");
        graph.insertNode("A");

        graph.insertEdge("S","H",5.0);
        graph.insertEdge("S","Z",10.0);
        graph.insertEdge("H","I",3.0);
        graph.insertEdge("H","Z",9.0);
        graph.insertEdge("N","E",4.0);
        graph.insertEdge("Z","E",5.0);
        graph.insertEdge("I","N",2.0);
        graph.insertEdge("I","E",7.0);
        graph.insertEdge("E","A",6.0);

        assertEquals("[S, H, I, N, E]",graph.shortestPathData("S","E").toString());
        assertEquals(14.0,graph.shortestPathCost("S","E"));

    }


}