import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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

