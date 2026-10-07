public class AhoCorasick {
    private static class Node { int[] next = new int[128]; int fail; boolean out; Node(){ for(int i=0;i<128;i++) next[i]=-1; } }
    public static int count(String text, String[] patterns) {
        if (patterns.length == 0) return 0;
        Node[] nodes = new Node[1024]; int size = 1; nodes[0] = new Node();
        for (int p=0;p<patterns.length;p++) {
            int cur=0; String pat=patterns[p];
            for(int i=0;i<pat.length();i++) { int ch=pat.charAt(i); if(ch>=128) continue; if(nodes[cur].next[ch]==-1){nodes[size]=new Node(); nodes[cur].next[ch]=size++;} cur=nodes[cur].next[ch]; }
            nodes[cur].out=true;
        }
        int[] q=new int[size]; int head=0,tail=0;
        for(int c=0;c<128;c++){int v=nodes[0].next[c]; if(v!=-1){nodes[v].fail=0;q[tail++]=v;} else nodes[0].next[c]=0;}
        while(head<tail){int v=q[head++]; for(int c=0;c<128;c++){int u=nodes[v].next[c]; if(u!=-1){nodes[u].fail=nodes[nodes[v].fail].next[c]; nodes[u].out=nodes[u].out||nodes[nodes[u].fail].out; q[tail++]=u;} else nodes[v].next[c]=nodes[nodes[v].fail].next[c];}}
        int cur=0, matches=0; for(int i=0;i<text.length();i++){int ch=text.charAt(i); if(ch>=128){cur=0;continue;} cur=nodes[cur].next[ch]; if(nodes[cur].out) matches++;}
        return matches;
    }
}
