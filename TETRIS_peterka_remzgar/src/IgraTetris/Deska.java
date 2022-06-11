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
    final int SirinaDeske=11;
    final int VisinaDeske=24;
    Liki likNaDeski[]= new Liki[SirinaDeske*VisinaDeske];
    boolean KonecPada;
    boolean zacetek;
    int polnaVrsta;
    Timer timer;
    public int tocke=0;
    Tetris x; 
    
    List<Player> highScores=new ArrayList<Player>();
    Player najbolsiIgralec = new Player("",0);
        
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
        String vnos= JOptionPane.showInputDialog("Konec igre!!! \nVnesite vaše ime: ");
        try {
            if (najbolsiIgralec.score<=tocke && tocke>0){
                fw = new FileWriter("score.txt",true);
                fw.write(vnos+": "+Integer.toString(tocke)+System.getProperty("line.separator"));
                fw.close();
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    
    void beriTocke() {
        File f = new File("score.txt");
        try {
            Scanner sc = new Scanner(f);
            while(sc.hasNextLine()) {
                String s = sc.nextLine();
                int separator = s.indexOf(':');
                String resultStr = s.substring(separator+2,s.length());
                Player trenutniIgralec=new Player("",0);
                trenutniIgralec.name=s.substring(0,separator);
                trenutniIgralec.score=Integer.parseInt(resultStr);
                highScores.add(trenutniIgralec);
                if (trenutniIgralec.score>najbolsiIgralec.score)
                {
                    najbolsiIgralec.score=trenutniIgralec.score;
                    najbolsiIgralec.name=trenutniIgralec.name;
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
        
            Color barva = new Color (33, 55, 50); // Barva ozadja
            if (zaBarvo==1)
                barva = new Color (255, 255, 255);
            if (zaBarvo==2){
                barva = new Color (100, 150, 255);
            }
            if (zaBarvo==3){
                barva = new Color (100, 255, 200);
            }
            
            g.setColor(barva);
            g.fillRect(0, 0, width, height-5);
            if (lik.getLik()!= Liki.Nič)
            {
                for (int i = 0; i < 4; i++) {
                    int x=tekociX+lik.getX(i);
                    int y=tekociY+lik.getY(i);
                    izrisiKvadrata(g,0+x*sirinaKvadrata(),
                    vrhDeske+(VisinaDeske-y-1)*visinaKvadrata(), lik.getLik());
                }
            }
            for (int i=0;i<VisinaDeske;i++){
                for (int j=0;j<SirinaDeske;j++)
                {
                    Liki l=likNa(j,VisinaDeske-i-1);
                    if (l!=Liki.Nič)
                      izrisiKvadrata(g,0+j*sirinaKvadrata(),vrhDeske+i*visinaKvadrata(),l);  
                }
            }
            
    }

    private void izrisiKvadrata(Graphics g, int x, int y, Liki lik) {
        Color barve[]=
        {new Color(0, 255, 255),new Color(255, 255, 0),new Color(128, 0, 128),
        new Color(0, 255, 0),new Color(255, 0, 0),new Color(0, 0, 255),
        new Color(255, 127, 0),new Color(127, 127, 127)};
        Color barva=barve[lik.ordinal()];
        g.setColor(barva);
        g.fillRect(x+1, y+1, sirinaKvadrata()-2, visinaKvadrata()-2);
    
    }
    public void počistiVrstico()
    {
        for (int i=0;i<VisinaDeske*SirinaDeske;i++){
            likNaDeski[i]=Liki.Nič;
        }
        
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (KonecPada)
        {
            KonecPada=false;
            novLik();
        }
        else{
            enaVrsticaDol();
        }
    }
    public void start()
    {
        zacetek=true;
        KonecPada=false;
        počistiVrstico();
        polnaVrsta=0;
        tocke=0;
        beriTocke();
        x.status.setText("  Score: "+0+"   Highscore: "+najbolsiIgralec.score+
                "  ("+najbolsiIgralec.name+")"+"  Commands: ←↑↓→");

        novLik();
    } 
    private void enaVrsticaDol()
    {
        if(!poskusiPremik(lik,tekociX,tekociY-1))
            likOdvrzen();
    }

    private boolean poskusiPremik(Lik noviLik,int noviX,int noviY)
    {
        for (int i=0;i<4;i++)
        {
            int x=noviX+noviLik.getX(i); 
            int y=noviY+noviLik.getY(i);
            if (x<0||x>=SirinaDeske||y<0||y>=VisinaDeske)
                return false; 
            if (likNa(x,y)!=Liki.Nič)
                return false;
        } 
        lik=noviLik;
        tekociX=noviX;
        tekociY=noviY;
        repaint();
        return true;
    }
    private void likOdvrzen()
    {   
        try{
            for (int i=0;i<4;i++)
            {
                int x=tekociX+lik.getX(i);
                int y=tekociY+lik.getY(i);
                likNaDeski[y*SirinaDeske+x]=lik.getLik(); 
            }
            odstraniPolneVrste();
            if (!KonecPada)
                novLik();
        }
        catch(Exception e)
        {
           
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
            }
            if (jeVrstaPolna)
            {
                tocke++;
                x.status.setText("  Score: "+tocke+"    Highscore: "+najbolsiIgralec.score+
                        "  ("+najbolsiIgralec.name+")"+"  Commands: ←↑↓→");
                for(int k=i;k<VisinaDeske-1;k++)
                {
                    for (int j=0;j<SirinaDeske;j++)
                    likNaDeski[k*SirinaDeske+j]=likNa(j,k+1);
                }
            }
        }
        if (tocke>0)
        {
            polnaVrsta+=tocke;
            KonecPada=true;
            lik.nastaviLik(Liki.Nič);
        }
    }
    private void odvrziLik()
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
            if (!zacetek||lik.getLik()==Liki.Nič){
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
                odvrziLik();
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
                    x.status.setText("  Score: "+tocke+"    Highscore: "+najbolsiIgralec.score+
                        "  ("+najbolsiIgralec.name+")"+"  Commands: ←↑↓→");
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