import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {

        int tileSize = 32;
        int rows = 16;
        int columns = 16;
        int boardWidth = tileSize * columns; //32 * 16 = 512px
        int boardHeight = tileSize * rows; //21 * 16 = 512px;
        //Window Variables

        JFrame frame = new JFrame("Z Invaders"); //Title of JFrame
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null); //Places the window at the centre of the screen
        frame.setResizable(false); //Cannot resize window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  //Pressing the 'x' button on the window terminates the program
        ImageIcon icon = new ImageIcon(App.class.getResource("./Z.png")); // Load the icon
        frame.setIconImage(icon.getImage()); // Set it as the frame icon

        ZInvaders zInvaders = new ZInvaders(); //Creates an instance of the JPanel
        frame.add(zInvaders); //Adds the JPanel onto the window
        frame.pack();
        zInvaders.requestFocus();
        frame.setVisible(true); //Frame is now visible
    }
}
