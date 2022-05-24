package igraTetris;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.event.*;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.io.*;
import java.util.*;

public class Deska extends JPanel implements ActionListener {
    
    int zaBarvo=0;
    int vrhDeske=0;
    int tekociX=0;
    int tekociY=0;
    final int SirinaDeske=10;
    final int VisinaDeske=22;
    Liki likNaDeski[]= new Liki[SirinaDeske*VisinaDeske];
    boolean jeKonecPada;
    boolean jeZačeto;
    int štOdstranjenihVrstic;
    Timer timer;
    public int štPolnih=0;
    Tetris x; 
    
    List<Player> highScores=new ArrayList<Player>();
    Player topPlayer = new Player("",0);
        
    private int sirinaKvadrata()
    {
        return (int)getSize().getWidth()/SirinaDeske;
    }
    private int visinaKvadrata()
    {
        return (int)getSize().getHeight()/VisinaDeske;
    }
    
    public Deska(Tetris t)
    {
        t.addKeyListener(new TAdapter());
        x=t;
        timer=new Timer(400,this);
        timer.start();
    }

    Lik lik=new Lik();
    private void novLik()
    {
        lik.setRandomLik();
        tekociX=SirinaDeske/2-1;
        tekociY=VisinaDeske-1+lik.getMinY();
    }
    
    private Liki likNa(int x, int y)
    {          
       return likNaDeski[y*SirinaDeske+x];
    }
    
    
    void pisiVDat(){
        FileWriter fw;
        String vnos= JOptionPane.showInputDialog("Prosim vnesite ime: ");
        try {
            if (topPlayer.score<=štPolnih && štPolnih>0){
                fw = new FileWriter("score.txt",true);
                fw.write(vnos+": "+Integer.toString(štPolnih)+System.getProperty("line.separator"));
                fw.close();
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    
    void beriScore() {
        File f = new File("score.txt");
        try {
            Scanner sc = new Scanner(f);
            while(sc.hasNextLine()) {
                String s = sc.nextLine();
                int separator = s.indexOf(':');
                String resultStr = s.substring(separator+2,s.length());
                Player currentPlayer=new Player("",0);
                currentPlayer.name=s.substring(0,separator);
                currentPlayer.score=Integer.parseInt(resultStr);
                highScores.add(currentPlayer);
                if (currentPlayer.score>topPlayer.score)
                {
                    topPlayer.score=currentPlayer.score;
                    topPlayer.name=currentPlayer.name;
                }  
            }    
        } 
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paintComponent(Graphics g){
        
            int width=this.getWidth();
            int height=this.getHeight();
    
            super.paintComponent(g);
        
            Color barva = new Color (0, 0, 0);
            if (zaBarvo==1)
                barva = new Color (255, 255, 255);
            if (zaBarvo==2){
                barva = new Color (0, 150, 255);
            }
            if (zaBarvo==3){
                barva = new Color (0, 255, 200);
            }
            
            g.setColor(barva);
            g.fillRect(0, 0, width, height-5);
            if (lik.getLik()!= Liki.Nič)
            {
                for (int i = 0; i < 4; i++) {
                    int x=tekociX+lik.getX(i);
                    int y=tekociY+lik.getY(i);
                    izrisiKvadrat(g,0+x*sirinaKvadrata(),
                    vrhDeske+(VisinaDeske-y-1)*visinaKvadrata(), lik.getLik());
                }
            }
            //liki, ki so že padli
            for (int i=0;i<VisinaDeske;i++){
                for (int j=0;j<SirinaDeske;j++)
                {
                    Liki l=likNa(j,VisinaDeske-i-1); //iščem lik v i-ti vrstici in j-tem stolpcu
                    if (l!=Liki.Nič)
                      izrisiKvadrat(g,0+j*sirinaKvadrata(),vrhDeske+i*visinaKvadrata(),l);  
                }
            }
            
    }

    private void izrisiKvadrat(Graphics g, int x, int y, Liki lik) {
        Color barve[]=
        {new Color(0,5,0),new Color(204,102,102),new Color(102,204,102),
        new Color(102,102,204),new Color(204,204,102),new Color(204,102,204),
        new Color(102,204,204),new Color(218,170,0)};
        Color barva=barve[lik.ordinal()];
        g.setColor(barva);
        g.fillRect(x+1, y+1, sirinaKvadrata()-2, visinaKvadrata()-2);
    
    }
    public void počistiDesko()
    {
        for (int i=0;i<VisinaDeske*SirinaDeske;i++){
            likNaDeski[i]=Liki.Nič;
        }
        
    }
    @Override
    public void actionPerformed(ActionEvent e) {
    //ko preteče 400ms
        if (jeKonecPada)
        {
            jeKonecPada=false;
            novLik();
        }
        else{
            enaVrsticaDol();
        }
    }
    public void start()
    {
        jeZačeto=true;
        jeKonecPada=false;
        počistiDesko();
        štOdstranjenihVrstic=0;
        štPolnih=0;
        beriScore();
        x.status.setText("  Score: "+0+"    Highscore: "+topPlayer.score+
                "  ("+topPlayer.name+")"+"  Tipke: ←↑↓→ d p b spacebar");

        novLik();
    } 
    private void enaVrsticaDol()
    {
        if(!poskusiPremik(lik,tekociX,tekociY-1))
            likOdvržen();
    }

    private boolean poskusiPremik(Lik noviLik,int noviX,int noviY)
    {
        for (int i=0;i<4;i++)
        {
            int x=noviX+noviLik.getX(i); //izračunaj nov x glede na tekočiX
            int y=noviY+noviLik.getY(i); //izračunaj nov y glede na tekočiY
            if (x<0||x>=SirinaDeske||y<0||y>=VisinaDeske)
                return false; //če x ali y nista več na deski ne moremo več vrstice navzdol
            if (likNa(x,y)!=Liki.Nič) //če se na (x,y) že nahaja lik, ne moremo navzdol
                return false;
        } 
        lik=noviLik;
        tekociX=noviX;
        tekociY=noviY;
        repaint();
        return true;
    }
    private void likOdvržen()
    {   
        try{
            for (int i=0;i<4;i++)
            {
                int x=tekociX+lik.getX(i);
                int y=tekociY+lik.getY(i);
                likNaDeski[y*SirinaDeske+x]=lik.getLik(); //napolni tabelo likiNaDeski z odvrženim likom
            }
            odstraniPolneVrste();
            if (!jeKonecPada)
                novLik();
        }
        catch(Exception e)
        {
            
            JOptionPane.showMessageDialog(null, "   KONEC IGRE!");
            pisiVDat();
            start();
            
        }
    }
    public void odstraniPolneVrste()
    {
        
        for (int i=VisinaDeske-1;i>=0;--i){
            boolean jeVrstaPolna=true;
            for (int j=0;j<SirinaDeske;j++)
            {
                if (likNa(j,i)==Liki.Nič)
                {
                    jeVrstaPolna=false;
                    break;
                } 
                //če je en prazen lik
                //vrsta ni polna, nadaljuj z naslednjo vrsto 
                //break vrže iz zanke for j           
            }
            if (jeVrstaPolna)
            {
                štPolnih++;
                x.status.setText("  Score: "+štPolnih+"    Highscore: "+topPlayer.score+
                        "  ("+topPlayer.name+")"+"  Tipke: ←↑↓→ d p b spacebar");
                for(int k=i;k<VisinaDeske-1;k++)
                {
                    for (int j=0;j<SirinaDeske;j++)
                    //premakni vse like v tabeli eno vrstico bolj dol
                    likNaDeski[k*SirinaDeske+j]=likNa(j,k+1);
                }
            }
        }
        if (štPolnih>0)
        {
            štOdstranjenihVrstic+=štPolnih;
            jeKonecPada=true;
            lik.nastaviLik(Liki.Nič);
            //repaint();
        }
    }
    private void odvržiLik()
    {
        int noviY=tekociY;
        while(noviY>=0)
        {
            if (!poskusiPremik(lik,tekociX,tekociY-1)){
                break;
            }
            noviY=tekociY-tekociY;
        }
    }   
    class TAdapter extends KeyAdapter
    {
        @Override
        public void keyPressed(KeyEvent e)
        {        
            if (!jeZačeto||lik.getLik()==Liki.Nič){
                return;
            }
            int koda=e.getKeyCode();
            switch(koda)
            {
                case KeyEvent.VK_LEFT:
                poskusiPremik(lik,tekociX-1,tekociY);
                break;
                case KeyEvent.VK_RIGHT:
                poskusiPremik(lik,tekociX+1,tekociY);
                break;
                case KeyEvent.VK_DOWN:
                poskusiPremik(lik.obrniDesno(),tekociX,tekociY);
                break;
                case KeyEvent.VK_UP:
                poskusiPremik(lik.obrniLevo(),tekociX,tekociY);
                break;
                case KeyEvent.VK_SPACE:
                odvržiLik();
                break;
                case 'D':
                enaVrsticaDol();
                break;
                case 'P':
                if (timer.isRunning()){
                    timer.stop(); 
                    x.status.setText("  Paused");
                }
                else{
                    timer.start();
                    x.status.setText("  Score: "+štPolnih+"    Highscore: "+topPlayer.score+
                        "  ("+topPlayer.name+")"+"  Tipke: ←↑↓→ d p b spacebar");
                }
                break;   
                case 'B':
                    if (zaBarvo!=3)
                        zaBarvo++;
                    else
                        zaBarvo=0;
                break;                 
            }
        } 
    }
}