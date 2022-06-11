package igraTetris;

import java.awt.*;
import javax.swing.*;

public class Tetris extends JFrame  {
    
    JLabel status;
    JLabel tipke;
    Timer timer;
    
    public Tetris(){
       status=new JLabel("");
       tipke=new JLabel(" ");
       add(status, BorderLayout.NORTH);
       Deska d= new Deska(this);
       add(d);
       setTitle("Tetris");
       setMinimumSize(new Dimension(300, 600));
       setMaximumSize(new Dimension(300, 600));
       setSize(400,800);
       setResizable(false);
       d.start();
       setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public JLabel getStatus()
    {
      return status;
    }
    
    public static void main(String[]args)
    {  
        Tetris t=new Tetris();
        t.setLocationRelativeTo(null);
        t.setVisible(true);
        
    }
}