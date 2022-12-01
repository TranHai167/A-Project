package A_Star_simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class MyPanel extends JPanel implements ActionListener {

    // Setup interface:
    static int WIDTH_SCREEN = 1200;
    static int HEIGHT_SCREEN = 700;
    int unitSize = 20;
    int boundX = WIDTH_SCREEN / unitSize;
    int boundY = HEIGHT_SCREEN / unitSize;
    int[][] array;
    final int SPEED = 10;

    // Running state: -1: Not run yet, 0: running, 1: finish running
    int run = -1;

    // Components:
    JButton button1 = new JButton();
    JButton button2 = new JButton();
    JButton button3 = new JButton();
    JButton startBut = new JButton();

    // State of buttons
    boolean checkBt1, checkBt2, checkBt4, clickWall, fillBegin, fillDes, ready, found = true;

    // Coordinates of beginning and destination point
    Node begin = new Node(0, 0);
    boolean clickBegin;
    Node des = new Node(0, 0);
    boolean clickDes;

    // List to save Opening and Closing List
    SortedArrayListPQ openList = new SortedArrayListPQ();
    ArrayList<Node> closedList = new ArrayList<>();

    // Font:
    private final Font font = new Font("Arial", Font.BOLD, 15);

    // Wall objects to create Map:
    GetWallCase wall = new GetWallCase();

    // Timer object to repaint map:
    Timer timer;

    public void setupButton1() {

        // Setup button 1
        button1.setFont(font);
        button1.setText("Simple 1");
        button1.setBounds(25, HEIGHT_SCREEN + 20, 150, 40);
        button1.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button1.setBackground(new Color(139, 168, 173));
                array = wall.getRandomMap(boundX, boundY, 15);
            }

            public void mouseExited(MouseEvent evt) {
                if (!checkBt1) {
                    button1.setBackground(UIManager.getColor("control"));
                    array = wall.getBoundary(boundX, boundY);
                }
            }

            public void mouseClicked(MouseEvent evt) {
                if (checkBt1) {
                    checkBt1 = false;
                    button1.setBackground(UIManager.getColor("control"));
                } else {
                    checkBt1 = true;
                    button1.setBackground(new Color(139, 168, 173));
                    button2.setBackground(UIManager.getColor("control"));
                }
            }
        });
    }

    public void setupButton2() {
        // Setup button 2
        button2.setFont(font);
        button2.setText("Simple 2");
        button2.setBounds(200, HEIGHT_SCREEN + 20, 150, 40);
        button2.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button2.setBackground(new Color(139, 168, 173));
                array = wall.getRandomMap(boundX, boundY, 30);
            }

            public void mouseExited(MouseEvent evt) {
                if (!checkBt2) {
                    button2.setBackground(UIManager.getColor("control"));
                    array = wall.getBoundary(boundX, boundY);
                }
            }

            public void mouseClicked(MouseEvent evt) {
                if (checkBt2) {
                    checkBt2 = false;
                    button2.setBackground(UIManager.getColor("control"));
                } else {
                    checkBt2 = true;
                    button2.setBackground(new Color(139, 168, 173));
                    button1.setBackground(UIManager.getColor("control"));
                }
            }
        });
    }

    public void setupButton3() {
        // Setup button random
        button3.setFont(font);
        button3.setText("Continue >>");
        button3.setBounds(WIDTH_SCREEN - 175, HEIGHT_SCREEN + 20, 150, 40);
        button3.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button3.setBackground(new Color(139, 168, 173));
            }

            public void mouseExited(MouseEvent evt) {
                button3.setBackground(UIManager.getColor("control"));
            }

            public void mouseClicked(MouseEvent evt) {
                removeButton();
                checkBt4 = true;
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2D = (Graphics2D) g;

        // Before Searching
        if (run == 0) {
            computing(g);
        }

        if (!fillBegin || !fillDes) {
            selectBeginPoint(g);
            selectDesPoint(g);
        }

        if (clickWall) {
            selectAgain(g);
        }

        // Start searching

        for (Node point : openList) {
            g2D.setPaint(Color.GREEN);
            g2D.fillRect(point.getX() * unitSize, point.getY() * unitSize, unitSize, unitSize);
        }

        for (Node point : closedList) {
            g2D.setPaint(Color.RED);
            g2D.fillRect(point.getX() * unitSize, point.getY() * unitSize, unitSize, unitSize);
        }

        g2D.setPaint(Color.black);

        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                if (array[i][j] == 1) {
                    g2D.fillRect(j * unitSize, i * unitSize, unitSize, unitSize);
                }
            }
        }

        if (run == 1) {
            g2D.setPaint(Color.BLUE);
            if (found) {
                paintPath(g2D);
            }

            ending(g2D);
        }

        if (fillBegin) {
            g2D.setPaint(Color.CYAN);
            g2D.fillRect(begin.x * unitSize, begin.y * unitSize, unitSize, unitSize);
        }

        if (fillDes) {
            g2D.setPaint(Color.MAGENTA);
            g2D.fillRect(des.x * unitSize, des.y * unitSize, unitSize, unitSize);
        }

        g2D.setPaint(Color.black);
        for (int i = 0; i < WIDTH_SCREEN; i = i + unitSize) {
            g2D.drawLine(i, 0, i, HEIGHT_SCREEN);
            for (int j = 0; j < HEIGHT_SCREEN; j = j + unitSize) {
                g2D.drawLine(0, j, WIDTH_SCREEN, j);
            }
        }
    }

    // Tim duong di giua cac phan tu tiep theo
    public void searching() {
        Node currentPoint = openList.min();
        if (currentPoint.x == des.x && currentPoint.y == des.y) {
            des.previous = currentPoint;
            run = 1;
            return;
        }

        for (int i = -1; i < 2; i++) {
            int a = currentPoint.y + i;
            for (int j = -1; j < 2; j++) {
                int b = currentPoint.x + j;
                if ((i == 0 && j == 0) || (i * j != 0) || a < 0 || b < 0 || a == boundY || b == boundX || array[a][b] == 1 || !checkVisited(b, a) || !checkValue(b, a, currentPoint.get_fValue())) {
                    continue;
                }

                double dis = get_hValue(b, a, des.x, des.y);
                Node neighbor = new Node(b, a, 1 + currentPoint.get_gValue(), dis);
                neighbor.previous = currentPoint;
                openList.insert(neighbor);
            }
        }

        closedList.add(currentPoint);
        openList.removeMin();
    }

    public boolean checkValue(int posX, int posY, double fValue) {
        // Tìm trong openList phần tử có f nhỏ hơn để xét
        for (int i = 0; i < openList.size(); i++) {
            if (openList.get(i).getX() == posX && openList.get(i).getY() == posY) {
                if (openList.get(i).get_fValue() < fValue) {
                    return false;
                } else {
                    openList.remove(i);
                    return true;
                }
            }
        }

        return true;
    }

    // Kiểm tra xem node đang xét có trong closedlist hay chưa
    public boolean checkVisited(int posX, int posY) {
        for (Node point : closedList) {
            if (point.getX() == posX && point.getY() == posY) {
                return false;
            }
        }

        return true;
    }

    public void stop() {
        if (run == 1) {
            timer.stop();
        }
    }

    public void paintPath(Graphics2D g) {
        Node current = des;

        while (current != begin) {
            g.fillRect(current.getX() * unitSize, current.getY() * unitSize, unitSize, unitSize);
            current = current.previous;
        }
    }

    public void setupStartButton() {
        startBut.setFont(font);
        startBut.setText("Start finding");
        startBut.setBounds(400, 720, 200, 40);
        startBut.setBackground(new Color(45, 235, 95));
        startBut.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                removeButton();
                run = 0;
                ready = false;
                begin = new Node(begin.x, begin.y, 0, get_hValue(begin.x, begin.y, des.x, des.y));
                openList.insert(begin);
            }
        });
    }

    public MyPanel() {
        setupButton1();
        setupButton2();
        setupButton3();
        setupStartButton();

        this.add(button1);
        this.add(button2);
        this.add(button3);
        this.array = wall.getBoundary(boundX, boundY);
        getStart(SPEED);

        this.setLayout(null);
        this.setPreferredSize(new Dimension(WIDTH_SCREEN, HEIGHT_SCREEN + 70));
    }

    // Remove button when clicking 'continue' button!
    public void removeButton() {
        this.remove(button1);
        this.remove(button2);
        this.remove(button3);
        this.remove(startBut);
    }

    // Select begin point label
    public void selectBeginPoint(Graphics g) {
        if (!clickDes && clickBegin && !clickWall) {
            g.setColor(Color.GREEN);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Click to choose begin point", (WIDTH_SCREEN - metrics.stringWidth("Click to choose begin point")) / 2, 750);
        }
    }

    // Select destination point label
    public void selectDesPoint(Graphics g) {
        if (!clickWall && clickDes && !clickBegin) {
            g.setColor(Color.RED);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Click to choose destination point", (WIDTH_SCREEN - metrics.stringWidth("Click to choose destination point")) / 2, 750);
        }
    }

    // Select again label
    public void selectAgain(Graphics g) {
        if (clickWall) {
            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Impossible pos, pls select again!", (WIDTH_SCREEN - metrics.stringWidth("Impossible pos, pls select again!")) / 2, 750);
        }
    }

    // Computing label
    public void computing(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Calculating...", (WIDTH_SCREEN - metrics.stringWidth("Calculating...")) / 2, 750);
    }

    // Ending
    public void ending(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        if (found) {
            g.drawString("Path found!  Cost: " + des.previous.get_gValue() + "$", (WIDTH_SCREEN - metrics.stringWidth("Path found!  Cost: " + des.previous.get_gValue() + "$")) / 2, 750);
        } else {
            g.drawString("Can not find path! ", (WIDTH_SCREEN - metrics.stringWidth("Can not find path!")) / 2, 750);
        }
    }

    // Start timer
    public void getStart(int delay) {
        timer = new Timer(delay, this);
        timer.start();
    }

    // Get h value
    public double get_hValue(int fromX, int fromY, int toX, int toY) {
        //    return Math.sqrt((fromX - toX) * (fromX - toX) + (fromY - toY) * (fromY - toY));
        return Math.abs(fromX - toX) + Math.abs(fromY - toY);
    }

    // Repaint the panel each delay time of timer!
    @Override
    public void actionPerformed(ActionEvent e) {
        if (run == -1) {
            // Activate clicking beginning point
            if (checkBt4) {
                clickBegin = true;
                checkBt4 = false;
            }

            // Operation with choosing phase
            if (clickBegin || clickDes) {
                this.addMouseListener(new MyMouseAdapter());
            }

            if (ready) {
                this.add(startBut);
            }
        } else {
            try {
                searching();
            } catch (IndexOutOfBoundsException ex) {
                found = false;
                run = 1;
            }

            stop();
        }

        repaint();
    }

    public class MyMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int x = e.getX() / unitSize * unitSize;
            int y = e.getY() / unitSize * unitSize;
            if (array[y / unitSize][x / unitSize] == 1) {
                clickWall = true;
            } else if (clickBegin) {
                begin.x = x / unitSize;
                begin.y = y / unitSize;
                clickWall = false;
                clickBegin = false;
                clickDes = true;
                fillBegin = true;

                try {
                    timer.wait(200);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            } else if (clickDes) {
                des.x = x / unitSize;
                des.y = y / unitSize;
                clickWall = false;
                clickDes = false;
                fillDes = true;
                ready = true;
            }
        }
    }
}