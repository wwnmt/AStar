import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class SolveByAStar {

    private Node startStatus;
    private String result = "";

    //OPEN集和CLOSED集
    private Queue<Node> open = new LinkedList<Node>();
    private HashSet<Node> close = new HashSet<Node>();  //Set保证唯一

    public SolveByAStar(int[] startStatue) {
        this.startStatus = new Node(startStatue);
    }

    public String solve() {
        return process(startStatus);
    }

    //A* search
    private String process(Node node) {

        int space = 0;
        Node parentNode;
        Node nextOpen;

        node.preNode = null;
        open.add(node);

        while (!open.isEmpty()) {
            parentNode = open.peek();   //peek队列首部为父节点，在接口强制排序下，必为代价最小的节点
            open.remove();
            close.add(parentNode);

            //找出空格
            for (int i = 0; i < 9; i++) {
                if (parentNode.arrange[i] == 0) {
                    space = i;
                    break;
                }
            }

            for (int i = 0; i < 4; i++) {
                nextOpen = new Node();
                System.arraycopy(parentNode.arrange, 0, nextOpen.arrange, 0, 9);
                //空格上移
                if (i == 0) {
                    if (space / 3 == 0) //在第0行
                        continue;

                    //交换空格和另一个
                    nextOpen.arrange[space] = nextOpen.arrange[space - 3];
                    nextOpen.arrange[space - 3] = 0;

                    nextOpen.operation = "up";
                }
                //下移
                else if (i == 1) {
                    if (space / 3 == 2) //在第2行
                        continue;

                    //交换空格和另一个
                    nextOpen.arrange[space] = nextOpen.arrange[space + 3];
                    nextOpen.arrange[space + 3] = 0;

                    nextOpen.operation = "down";
                }
                //左移
                else if (i == 2) {
                    if (space % 3 == 0) //在第0列
                        continue;

                    //交换空格和另一个
                    nextOpen.arrange[space] = nextOpen.arrange[space - 1];
                    nextOpen.arrange[space - 1] = 0;

                    nextOpen.operation = "left";
                }
                //右移
                else {
                    if (space % 3 == 2) //在第2列
                        continue;

                    //交换空格和另一个
                    nextOpen.arrange[space] = nextOpen.arrange[space + 1];
                    nextOpen.arrange[space + 1] = 0;

                    nextOpen.operation = "right";
                }

                //若close表不包含nextOpen状态，那么将其加入open表
                if (!close.contains(nextOpen)) {
                    nextOpen.step = parentNode.step + 1;
                    nextOpen.preNode = parentNode;
                    open.add(nextOpen);
                }

                //判断是否为最终状态
                if (EightDigital.finish(nextOpen.arrange)) {
                    nextOpen.preNode = parentNode;
                    result += "A*搜索之后，最少需要的步数为：" + nextOpen.step + "\n\n";
                    print(nextOpen);
                    return result;
                }
            }
        }
        return result;
    }

    //递归添加上一个状态
    private void print(Node node) {
        if (node.preNode != null)
            print(node.preNode);
        if (node.step == 0) {
            result += "初始状态\n" + node;
        } else {
            result += "第" + node.step + "步：" + node.operation + "\n" + node;
        }
    }

    //这个接口强制对每个对象进行整体排序，因此把比较因子修改为代价比较即可
    class Node implements Comparable<Node> {

        private int[] arrange = new int[9];
        private Node preNode;
        private int step = 0;
        private String operation = null;//记录上次的操作是把空格挪到哪里

        private Node(int[] arrange) {
            this.arrange = arrange;
        }

        private Node() {
        }

        public int compareTo(Node other) {
            if (f(this) > f(other))
                return 1;
            else if (f(this) < f(other))
                return -1;
            else
                return 0;
        }

        //A* 关键
        private int f(Node node) {
            return node.step + manhattan(node);
        }

        //求状态a与目标状态的曼哈顿距离
        private int manhattan(Node node) {
            int[] a = node.arrange;
            int startX, startY;
            int endX, endY;
            int manhattanDistance = 0;
            for (int i = 0; i < 9; i++) {
                if (a[i] == 0) continue;
                startX = i / 3;
                startY = i % 3;
                endX = (a[i] - 1) / 3;
                endY = (a[i] - 1) % 3;
                manhattanDistance += Math.abs(startX - endX) + Math.abs(startY - endY);
            }
            return manhattanDistance;
        }

        @Override
        public int hashCode() {
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum = sum * 10 + arrange[i];
            }
            return sum;
        }

        @Override
        public boolean equals(Object node) {
            return (this.hashCode() == node.hashCode());
        }

        @Override
        public String toString() {
            String result = "";
            for (int i = 0; i < 9; i++) {
                result = result + arrange[i] + " ";
                if (i % 3 == 2) result += "\n";
            }
            return result + "\n";
        }
    }
}
