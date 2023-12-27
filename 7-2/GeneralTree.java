import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;




class GeneralTree<E> {
    static class Node<E> implements Position<E> {
        private E element;
        private Node<E> parent;
        private ArrayList<Position<E>> children;

        public Node(E e, Node<E> above, ArrayList<Position<E>> a) {
            element = e;
            parent = above;
            children = a;
        }

        public E getElement() {
            return element;
        }

        public Node<E> getParent() {
            return parent;
        }

        public ArrayList<Position<E>> getChildren() {
            return children;
        }

        public void setElement(E e) {
            element = e;
        }

        public void addChild(Position<E> p) {
            children.add(p);
        }
    }

    protected Node<E> createNode(E e, Node<E> parent, ArrayList<Position<E>> a) {
        return new Node<E>(e, parent, a);
    }

    protected Node<E> root = null;
    private int size = 0;

    protected Node<E> validate(Position<E> p) throws IllegalArgumentException {
        if (!(p instanceof Node))
            throw new IllegalArgumentException("Not valid position type");
        Node<E> node = (Node<E>) p;
        if (node.getParent() == node)
            throw new IllegalArgumentException("p is no longer in the tree");
        return node;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Position<E> root() {
        return root;
    }

    public Position<E> parent(Position<E> p) throws IllegalArgumentException {
        Node<E> node = validate(p);
        return node.getParent();
    }

    public ArrayList<Position<E>> children(Position<E> p) {
        Node<E> node = validate(p);
        return node.getChildren();
    }

    public int numChildren(Position<E> p) {
        return children(p).size();
    }

    public boolean isInternal(Position<E> p) {
        return numChildren(p) > 0;
    }

    public boolean isExternal(Position<E> p) {
        return numChildren(p) == 0;
    }

    public boolean isRoot(Position<E> p) {
        return p == root();
    }

    public Position<E> addRoot(E e) throws IllegalStateException {
        if (!isEmpty())
            throw new IllegalStateException("Tree is Not Empty");
        root = createNode(e, null, new ArrayList<>());
        size = 1;
        return root;
    }

    public E set(Position<E> p, E e) throws IllegalArgumentException {
        Node<E> node = validate(p);
        E temp = node.getElement();
        node.setElement(e);
        return temp;
    }

    public Position<E> addChild(Position<E> p, E e) throws IllegalArgumentException {
        Node<E> parent = validate(p);
        Node<E> child = createNode(e, parent, new ArrayList<>());
        parent.addChild(child);
        size++;
        return child;
    }

    public int depth(Position<E> p) {
        if (isRoot(p))
            return 0;
        else
            return 1 + depth(parent(p));
    }

    public Iterable<Position<E>> preorder() {
        List<Position<E>> snapshot = new ArrayList<>();
        if (!isEmpty())
            preorderSubtree(root(), snapshot);
        return snapshot;
    }

    private void preorderSubtree(Position<E> p, List<Position<E>> snapshot) {
        snapshot.add(p);
        for (Position<E> c : children(p))
            preorderSubtree(c, snapshot);
    }

    private void postorderSubtree(Position<E> p, List<Position<E>> snapshot) {
        for (Position<E> c : children(p))
            postorderSubtree(c, snapshot);
        snapshot.add(p);
    }

    public Iterable<Position<E>> postorder() {
        List<Position<E>> snapshot = new ArrayList<>();
        if (!isEmpty())
            postorderSubtree(root(), snapshot);
        return snapshot;
    }

    public Iterable<Position<E>> positions() {
        return preorder();
    }
}

class Information {
    public String name;
    public boolean type;
    public long size;
    public long date;
}

public class FileSystemManager {
    public static GeneralTree<Information> inforTree = new GeneralTree<>();

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the directory path: ");
        String directoryPath = null;
        try {
            directoryPath = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File rootDirectory = new File(directoryPath);
        if (!rootDirectory.exists() || !rootDirectory.isDirectory()) {
            System.out.println("Invalid directory path.");
            return;
        }

        Information rootInformation = new Information();
        setInformation(rootInformation, rootDirectory);
        inforTree.addRoot(rootInformation);
        constructTree(inforTree.root(), rootDirectory);

        DateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy hh:mm a");
        for (Position<Information> pos : inforTree.positions()) {
            String fileType = pos.getElement().type ? "Directory" : "File";
            System.out.println(
                    pos.getElement().size + "\t" +
                            sdf.format(pos.getElement().date) + "\t" +
                            pos.getElement().name + "\t" + fileType
            );
        }
    }

    public static Information setInformation(Information i, File root) {
        i.name = root.getName();
        i.date = root.lastModified();
        i.type = root.isDirectory();
        i.size = root.length();
        return i;
    }

    public static Position<Information> addNode(Position<Information> parent, File root) {
        Information i = new Information();
        i = setInformation(i, root);
        return inforTree.addChild(parent, i);
    }

    public static void constructTree(Position<Information> parent, File root) {
        if (root != null && root.isDirectory()) {
            for (String childName : root.list()) {
                File child = new File(root, childName);
                Position<Information> temp = addNode(parent, child);
                constructTree(temp, child);
            }
        }
    }
    
    
}