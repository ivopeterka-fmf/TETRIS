package igraTetris;

public class Lik {
    

    private Liki trenutniLik;
    private int koordinate[][];
    private int [][][] tabelaKoordinat;
    
    public Lik()
    {
        koordinate=new int[4][2];
        nastaviLik(Liki.Nič);
    }
    public void nastaviLik(Liki l){
        tabelaKoordinat=new int[][][]
        {
            {{0,0},{0,0},{0,0},{0,0}}, //nič
            {{0,-1},{0,0},{-1,0},{-1,1}}, //Z-lik
            {{0,-1},{0,0},{1,0},{1,1}}, //S-lik
            {{0,-1},{0,0},{0,1},{0,2}}, //I-lik
            {{-1,0},{0,0},{1,0},{0,1}}, //T-tik
            {{0,0},{1,0},{0,1},{1,1}}, //Kvadrat
            {{-1,-1},{0,-1},{0,0},{0,1}}, //L-lik
            {{1,-1},{0,-1},{0,0},{0,1}}, //Zrcalni-lik
        };
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                koordinate[i][j]=tabelaKoordinat[l.ordinal()][i][j];
            }
        trenutniLik=l;    
        }
    }
    private void setX(int indeks, int x)
    {
        koordinate[indeks][0]=x;
    }
    private void setY(int indeks, int y)
    {
        koordinate[indeks][1]=y;
    }
    public int getX(int i){
        return koordinate[i][0];
    }
    public int getY(int i){
        return koordinate[i][1];
    }
    public Liki getLik(){
        return trenutniLik;
    }
    public void setRandomLik()
    {
        int x=(int)(1+(Math.random()*7));
        Liki[]v=Liki.values();
        nastaviLik(v[x]);
    }
    public int getMinX()
    {
        int m=koordinate[0][0];
        for (int i = 0; i < 4; i++) {
            m=Math.min(m,koordinate[i][0]);
        }
        return m;
    }
    public int getMinY()
    {
        int m=koordinate[0][0];
        for (int i = 0; i < 4; i++) {
            m=Math.min(m,koordinate[i][1]);
        }
        return m;
    }
    public Lik obrniLevo()
    {
        if (trenutniLik==Liki.Nič) {
            return this;
        }
        Lik l=new Lik();
        l.trenutniLik=trenutniLik;
        for (int i = 0; i < 4; i++) {
            l.setX(i, getY(i));
            l.setY(i, -getX(i));
        }
        return l;
    }
    public Lik obrniDesno()
    {
        if(trenutniLik==Liki.Nič)
            return this;
        Lik l=new Lik();
        l.trenutniLik=trenutniLik;
        for (int i = 0; i < 4; i++) {
            l.setX(i, -getY(i));
            l.setY(i,getX(i));
        }
        return l;
    }

}
