import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

public class Main extends JComponent implements ActionListener, MouseListener{
    static Boolean mouseClicked = false;
    static int mouseButton = 0;
    static int action = 0;
    static int mouseX = 0;
    static int mouseY = 0;

    static int size = 1;
    static int[][] map = new int[1][1];
    static boolean[][] mapShown = new boolean[1][1];
    static boolean[][] mapWaiting = new boolean[1][1];
    static boolean[][] mapFlags = new boolean[1][1];
    static boolean lost = false;

    static Random rand = new Random();

    //User-defined functions
    public static void main(String[] args) {
        JFrame window = new JFrame("MINESWEEPER");
        Main game = new Main();
        window.add(game);
        window.pack();
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        Timer t = new Timer(5, game);
        t.start();

        window.addMouseListener(game);


        //Start of the game

        int[] size_index = {50,30,15};
        size = size_index[size-1];
        //Creating map
        map = new int[size][size];
        mapShown = new boolean[size][size];
        mapWaiting = new boolean[size][size];
        mapFlags = new boolean[size][size];
        for(int e = 0; e < size*6; e++) {
            map[rand.nextInt(size)][rand.nextInt(size)] = -1;
        }
        for(int e = 0; e < map.length; e++) {
            for (int i = 0; i < map.length; i++) {
                if(map[e][i] != -1) {
                    int mines = 0;
                    int a = ((e == 0) ? 1 : 0) - 1;
                    int A = 1 - ((e == map.length-1) ? 1 : 0);
                    int b = ((i == 0) ? 1 : 0) - 1;
                    int B = 1 - ((i == map[e].length-1) ? 1 : 0);
                    for (int c = a; c <= A; c++) {
                        for(int d = b; d <= B; d++) {
                            if(map[e+c][i+d] == -1) {
                                mines++;
                            }
                        }
                    }
                    map[e][i] = mines;
                }
            }
        }
        while(!lost) {}
        t.stop();
    }


    private static int mouseToBoxConverter(int coord) {
        return (int) Math.floor(coord/10);
    }

    private static void drawNum(int num, int x, int y, Graphics g) {
        int[] Xs = new int[14];
        int[] Ys = new int[14];
        Color color = new Color(0,0,0);
        switch(num) {
            case 0:
                Xs = new int[]{5, 6, 4, 7, 4, 7, 4, 7, 4, 7, 4, 7, 5, 6};
                Ys = new int[]{2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8};
                color = new Color(241,73,234);
                break;
            case 1:
                Xs = new int[]{5,4,5,5,5,5,5,4,5,6};
                Ys = new int[]{1,2,2,3,4,5,6,7,7,7};
                color = new Color(246,62,58);
                break;
            case 2:
                Xs = new int[]{3,4,5,6,6,5,4,3,3,4,5,6};
                Ys = new int[]{2,1,1,2,3,4,5,6,7,7,7,7};
                color = new Color(32,135,198);
                break;
            case 3:
                Xs = new int[]{3,4,5,6,6,5,4,5,6,6,5,4,3};
                Ys = new int[]{1,1,1,1,2,3,4,4,5,6,7,7,6};
                color = new Color(28,0,241);
                break;
            case 4:
                Xs = new int[]{3,5,3,5,3,5,3,4,5,6,5,5,5};
                Ys = new int[]{1,1,2,2,3,3,4,4,4,4,5,6,7};
                color = new Color(105,226,126);
                break;
            case 5:
                Xs = new int[]{3,4,5,6,3,3,4,5,6,6,6,5,4,3};
                Ys = new int[]{1,1,1,1,2,3,3,3,4,5,6,7,7,6};
                color = new Color(223,158,0);
                break;
            case 6:
                Xs = new int[]{6,5,4,3,3,4,5,3,6,3,6,4,5};
                Ys = new int[]{1,1,2,3,4,4,4,5,5,6,6,7,7};
                color = new Color(255,255,0);
                break;
            case 7:
                Xs = new int[]{3,4,5,6,7,7,6,5,5,5,5};
                Ys = new int[]{1,1,1,1,1,2,3,4,5,6,7};
                color = new Color(253,167,205);
                break;
            case 8:
                Xs = new int[]{4,5,3,6,3,6,4,5,3,6,3,6,4,5};
                Ys = new int[]{1,1,2,2,3,3,4,4,5,5,6,6,7,7};
                color = new Color(0,255,0);
                break;
            case 9:
                Xs = new int[]{4,5,3,4,5,2,3,4,5,5,5,4,5,6,3,4,5,6,7};
                Ys = new int[]{1,1,2,2,2,3,3,3,3,4,5,6,6,6,7,7,7,7,7};
                color = new Color(255,0,0);
                break;
            case -1:
                Xs = new int[]{5,2,4,7,3,4,5,6,1,3,4,5,6,7,2,3,4,5,6,8,3,4,5,6,2,5,7,4};
                Ys = new int[]{1,2,2,2,3,3,3,3,4,4,4,4,4,4,5,5,5,5,5,5,6,6,6,6,7,7,7,8};
                color = new Color(0,0,0);
        }
        g.setColor(color);
        for(int b = 0; b < Xs.length; b++) {
            g.drawRect(Xs[b] + x, Ys[b] + y, 1, 1);
        }
    }

    //Computer functions
    public Dimension getPreferredSize() {
        return new Dimension(500,500);
    }

    protected void paintComponent(Graphics g) {
        //Draw the grid
        g.setColor(new Color(0,0,0));
        g.drawRect(0,0,500,500);

        g.setColor(new Color(255,255,255));

        for(int a = 0; a < 500; a+=10) {
            g.drawLine(a,0,a,500);
            g.drawLine(0,a,500,a);
        }
        for(int y = 0; y < map.length; y++) {
            for(int x = 0; x < map[y].length; x++) {
                if(mapShown[y][x]) {
                    drawNum(map[y][x], 10*x, 10*y, g);
                    if(map[y][x] == -1) {
                        lost = true;
                    }
                } else if(mapFlags[y][x]) {
                    drawNum(9, 10*x, 10*y, g);
                }
            }
        }
        if(lost) {
            for(int e = 0; e < map.length; e++) {
                for(int i = 0; i < map[e].length; i++) {
                    if(map[e][i] == -1) {
                        drawNum(-1, 10*i,10*e,g);
                    }
                }
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        int xCoord = mouseToBoxConverter(mouseX);
        int yCoord = mouseToBoxConverter(mouseY);
        if(mouseClicked) {
            //Select a square
            if(action == 1) {
                if (!mapShown[yCoord][xCoord]) {
                    int y = yCoord;
                    int x = xCoord;
                    mapWaiting[y][x] = true;

                    boolean j = true;
                    while (j) {
                        j = false;
                        for (int z = 0; z < map.length; z++) {
                            for (int i = 0; i < map[z].length; i++) {
                                if (mapWaiting[z][i]) {
                                    int X;
                                    int Y;
                                    if (map[z][i] == 0) {
                                        for (X = i - 1; X <= i + 1; X++) {
                                            for (Y = z - 1; Y <= z + 1; Y++) {
                                                if(X >= 0 && X < size && Y >= 0 && Y < size) {
                                                    if (!mapShown[Y][X]) {
                                                        if (map[Y][X] == 0) {
                                                            mapWaiting[Y][X] = true;
                                                        } else {
                                                            mapShown[Y][X] = true;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    mapWaiting[z][i] = false;
                                    mapShown[z][i] = true;
                                    j = true;
                                }
                            }
                        }
                    }
                }
            }else{
                mapFlags[yCoord][xCoord] = !mapFlags[yCoord][xCoord];
            }
            mouseClicked = false;
        }
        if(!lost) {
            repaint();
        }
    }

    public void mouseClicked(MouseEvent e) {
        mouseClicked = true;
        mouseButton = (SwingUtilities.isLeftMouseButton(e) ? 1 : 0) + (SwingUtilities.isMiddleMouseButton(e) ? 2 : 0) + (SwingUtilities.isRightMouseButton(e) ? 3 : 0);
        action = (SwingUtilities.isLeftMouseButton(e) ? 1 : 0) + (SwingUtilities.isRightMouseButton(e) ? 2 : 0);
        mouseX = e.getX() - 8;
        mouseY = e.getY() - 31;
    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {
    	
    }

    public void mouseExited(MouseEvent e) {

    }
}
