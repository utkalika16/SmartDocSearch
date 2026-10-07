public class AlignmentDP {
    public static int needlemanWunsch(String a, String b) {
        int n=a.length(),m=b.length(); int[] prev=new int[m+1],cur=new int[m+1];
        for(int j=0;j<=m;j++) prev[j]=-j;
        for(int i=1;i<=n;i++){cur[0]=-i; for(int j=1;j<=m;j++){int match=prev[j-1]+(a.charAt(i-1)==b.charAt(j-1)?1:-1); int del=prev[j]-1,ins=cur[j-1]-1; cur[j]=Math.max(match,Math.max(del,ins));} int[] t=prev;prev=cur;cur=t;}
        return prev[m];
    }
    public static int smithWaterman(String a, String b) {
        int n=a.length(),m=b.length(),best=0; int[] prev=new int[m+1],cur=new int[m+1];
        for(int i=1;i<=n;i++){cur[0]=0; for(int j=1;j<=m;j++){int match=prev[j-1]+(a.charAt(i-1)==b.charAt(j-1)?2:-1); int del=prev[j]-1,ins=cur[j-1]-1; cur[j]=Math.max(0,Math.max(match,Math.max(del,ins))); if(cur[j]>best)best=cur[j];} int[] t=prev;prev=cur;cur=t;}
        return best;
    }
}
