package ua.rassokha.servlets;

import ua.rassokha.di.anotations.SentiComponent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SentiComponent
public class PathTree {
    private Node root = new Node("", null, new ArrayList<>(), null);

    public PathTree() {
    }

    public static void main(String[] args) {
        PathTree p = new PathTree();
        p.addChild("/", String.class.getDeclaredMethods()[0]);
        p.addChild("ua/nure/tutu", PathTree.class.getDeclaredMethods()[0]);
        p.addChild("ua/nure/olha/rassokha", PathTree.class.getDeclaredMethods()[0]);
        p.addChild("ua/nure/olha/rassokha/double/double", PathTree.class.getDeclaredMethods()[0]);
        System.out.println(p.getMethod("/"));
    }

    public void addChild(String path, Method method) {
        List<String> elements = Arrays.asList(path.split("/"));
        if (path.equals("/")) {
            root.setMethod(method);
            return;
        }
        Node current = root;
        add(elements, current, method);

    }

    private void add(List<String> elements, Node current, Method method) {
        for (int i = 0; i < current.getChildren().size(); i++) {
            if (current.getChildren().get(i).getPath().equals(elements.get(0))) {
                add(elements.subList(i + 1, elements.size()), current.getChildren().get(i), method);
                return;
            }
        }
        if (elements.size() == 0) {
            throw new IllegalArgumentException();
        }
        for (String element : elements) {
            if (elements.indexOf(element) != elements.size() - 1) {
                Node newNode = new Node(element, current, new ArrayList<>(), null);
                current.getChildren().add(newNode);
                current = newNode;
            } else {
                Node newNode = new Node(element, current, new ArrayList<>(), method);
                current.getChildren().add(newNode);
                return;
            }
        }
    }

    public Method getMethod(String path) {
        if (path.equals("/")) {
            return root.getMethod();
        }
        String[] elements = path.split("/");
        Node current = root;
        for (String element : elements) {
            for (Node node : current.getChildren()) {
                if (node.getPath().equals(element)) {
                    current = node;
                    break;
                }
            }
        }
        return current.getMethod();
    }

    class Node {
        private String path;
        private Method method;
        private Node parent;
        private List<Node> children;

        Node(String path, Node parent, List<Node> children, Method method) {
            this.path = path;
            this.parent = parent;
            this.children = children;
            this.method = method;
        }

        String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        List<Node> getChildren() {
            return children;
        }

        public void setChildren(List<Node> children) {
            this.children = children;
        }
    }

}