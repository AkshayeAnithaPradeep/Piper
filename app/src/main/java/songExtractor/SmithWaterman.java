package songExtractor; /**
 * Created by nirant on 27/4/17.
 */

import java.util.Scanner;
import static java.lang.Math.max;

interface degerler
{
    final static int GAP_SCORE=-1;
    final static int MISS_MATCH=-2;
    final static int MATCH=2;
}

class Solution implements degerler
{

    private String a;
    private String b;
    private int matrix[][],i,j;
    private String a_final,b_final;

    public Solution(String a1,String b1,int i1,int j1)
    {
        a=a1; b=b1; i=i1; j=j1;
        matrix=new int [a.length()+1][b.length()+1] ;
        for(int k=0;k<i;k++) matrix[k][0]=0;
        for(int k=0;k<j;k++) matrix[0][k]=0;
        a_final="";
        b_final="";

    }
    public void build_Matrix()
    {
        for(int i1=1;i1<i;i1++)
        {
            for(int j1=1;j1<j;j1++)
            {

                if(a.charAt(i1)==b.charAt(j1)) //Match
                {

                    matrix[i1][j1]=matrix[i1-1][j1-1]+MATCH;

                }
                else
                {
                    find_value(i1,j1);
                }
            }
        }

    }


    public void find_value(int i1,int j1)
    {

        if(matrix[i1-1][j1] > matrix [i1-1][j1-1] && matrix[i1-1][j1] > matrix[i1][j1-1] )
        {
            if ((matrix[i1-1][j1]+GAP_SCORE) < 0)
                matrix[i1][j1] = 0;
            else
                matrix[i1][j1] = matrix[i1-1][j1]+GAP_SCORE;

        }

        else if(matrix[i1-1][j1-1] > matrix [i1-1][j1] && matrix[i1-1][j1-1] > matrix[i1][j1-1] )
        {
            if ((matrix[i1-1][j1-1]+MISS_MATCH) < 0)
                matrix[i1][j1]= 0;
            else
                matrix[i1][j1]=matrix[i1-1][j1-1]+MISS_MATCH;

        }

        else if(matrix[i1][j1-1] > matrix [i1-1][j1-1] && matrix[i1][j1-1] > matrix[i1-1][j1] )
        {
            if ((matrix[i1][j1-1]+GAP_SCORE) < 0)
                matrix[i1][j1] = 0;
            else
                matrix[i1][j1]=matrix[i1][j1-1]+GAP_SCORE;

        }

    }

    public int Find_String()
    {
        int r_max=0;
        int max = matrix[0][0];
        int i_temp=0,j_temp=0;

        for(int i1=0;i1<i;i1++)
            for(int j1=0;j1<j;j1++){
                if(matrix[i1][j1]>max) max = matrix[i1][j1];
            }
        return max;
    }

    public int return_max(int n1,int n2,int n3, int n4)
    {
        return max(max(max(n1,n2),n3),n4);
    }
}

public class SmithWaterman {

    public String findGenre(String text) {
        // TODO Auto-generated method stub

        String a = "-" + text;

        String b = "-POP";
        String c = "-JAZZ";
        String d = "-CLASSICAL";
        String e = "-METAL";

        Solution sB = new Solution(a, b, a.length(), b.length());
        sB.build_Matrix();
        int scorePop = sB.Find_String();

        Solution sC = new Solution(a, c, a.length(), c.length());
        sC.build_Matrix();
        int scoreJazz = sC.Find_String();

        Solution sD = new Solution(a, d, a.length(), d.length());
        sD.build_Matrix();
        int scoreClassical = sD.Find_String();

        Solution sE = new Solution(a, e, a.length(), e.length());
        sE.build_Matrix();
        int scoreMetal = sE.Find_String();
        String res;

        if (scoreMetal >= scoreClassical && scoreMetal >= scoreJazz && scoreMetal >= scorePop)
            res = "METAL";
        else if (scorePop >= scoreClassical && scorePop >= scoreMetal && scorePop >= scoreJazz)
            res = "POP";
        else if (scoreClassical >= scoreJazz && scoreClassical >= scorePop && scoreClassical >= scoreMetal)
            res = "CLASSICAL";
        else
            res = "JAZZ";

        System.out.println(res);
        return res;
    }
}
