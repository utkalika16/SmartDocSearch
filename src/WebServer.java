import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class WebServer {
    static SearchEngine engine = new SearchEngine();
    public static void main(String[] args) throws Exception {
        engine.loadFolder("docs");
        HttpServer server = HttpServer.create(new InetSocketAddress(9090), 0);
        server.createContext("/", WebServer::serveFrontend);
        server.createContext("/search", WebServer::search);
        server.createContext("/compare", WebServer::compare);
        server.createContext("/guide", WebServer::guide);
        server.createContext("/alignment", WebServer::alignment);
        server.createContext("/add", WebServer::addDocument);
        server.createContext("/document-analysis", WebServer::documentAnalysis);
        server.createContext("/stats", WebServer::stats);
        server.start();
        System.out.println("Smart Document Search Web Server started.");
        System.out.println("Open: http://localhost:9090");
        System.out.println("Indexed documents: " + engine.getDocumentCount());
    }
    static void serveFrontend(HttpExchange e) throws IOException {
        String p=e.getRequestURI().getPath();
        String file;
        if(p.equals("/")||p.equals("/index.html")) file="web/index.html";
        else if(p.equals("/compare.html")) file="web/compare.html";
        else if(p.equals("/guide.html")) file="web/guide.html";
        else if(p.equals("/lab.html")) file="web/lab.html";
        else if(p.equals("/add.html")) file="web/add.html";
        else if(p.equals("/document.html")) file="web/document.html";
        else if(p.equals("/style.css")) file="web/style.css";
        else if(p.equals("/script.js")) file="web/script.js";
        else { send(e,404,"Not Found","text/plain"); return; }
        File f=new File(file);
        if(!f.exists()){send(e,404,"Missing "+file,"text/plain");return;}
        byte[] d=read(f);
        String type=file.endsWith(".css")?"text/css":file.endsWith(".js")?"application/javascript":"text/html";
        sendBytes(e,200,d,type);
    }
    static void search(HttpExchange e)throws IOException{
        String q=param(e,"q"); String method=param(e,"method");
        ResultList rs=engine.search(q,method);
        String selected=method.length()==0?"Automatic":method;
        StringBuilder j=new StringBuilder("{\"query\":\"").append(esc(q)).append("\",\"method\":\"").append(esc(selected)).append("\",\"results\":[");
        for(int i=0;i<rs.size();i++){SearchResult r=rs.get(i);if(i>0)j.append(',');j.append("{\"document\":\"").append(esc(r.document.getName())).append("\",\"score\":").append(String.format(java.util.Locale.US,"%.2f",r.score)).append(",\"reason\":\"").append(esc(r.reason)).append("\",\"preview\":\"").append(esc(engine.getPreview(r,q))).append("\"}");}
        j.append("]}"); send(e,200,j.toString(),"application/json");
    }
    static void compare(HttpExchange e)throws IOException{
        String q=param(e,"q");
        StringBuilder all=new StringBuilder();
        for(int i=0;i<engine.getDocumentCount();i++){Document d=engine.getDocument(i);if(all.length()>0)all.append('\n');all.append(d.getNormalized());}
        send(e,200,BenchmarkService.compare(all.toString(),q),"application/json");
    }
    static void guide(HttpExchange e)throws IOException{send(e,200,BenchmarkService.guide(),"application/json");}
    static void alignment(HttpExchange e)throws IOException{send(e,200,BenchmarkService.alignment(param(e,"a"),param(e,"b")),"application/json");}
    static void stats(HttpExchange e)throws IOException{send(e,200,"{\"documents\":"+engine.getDocumentCount()+"}","application/json");}
    static void documentAnalysis(HttpExchange e)throws IOException{
        if(!e.getRequestMethod().equalsIgnoreCase("POST")){send(e,405,"POST required","text/plain");return;}
        String body=new String(read(e.getRequestBody()),StandardCharsets.UTF_8);
        String text=getJson(body,"text"), query=getJson(body,"query");
        if(text.length()==0||query.length()==0){send(e,400,"Document text and query required","text/plain");return;}
        send(e,200,BenchmarkService.compare(text,query),"application/json");
    }
    static void addDocument(HttpExchange e)throws IOException{
        if(!e.getRequestMethod().equalsIgnoreCase("POST")){send(e,405,"POST required","text/plain");return;}
        String body=new String(read(e.getRequestBody()),StandardCharsets.UTF_8);
        String title=getJson(body,"title"), content=getJson(body,"content");
        if(title.length()==0||content.length()==0){send(e,400,"Title and content required","text/plain");return;}
        File f=new File("docs",title.endsWith(".txt")?title:title+".txt");
        try(FileWriter w=new FileWriter(f)){w.write(content);}
        engine.loadFile(f);
        send(e,200,"{\"message\":\"Document added\",\"document\":\""+esc(f.getName())+"\",\"count\":"+engine.getDocumentCount()+"}","application/json");
    }
    static String getJson(String b,String key){String x="\""+key+"\":";int p=b.indexOf(x);if(p<0)return "";p+=x.length();while(p<b.length()&&Character.isWhitespace(b.charAt(p)))p++;if(p>=b.length()||b.charAt(p)!='\"')return "";p++;StringBuilder s=new StringBuilder();boolean slash=false;for(;p<b.length();p++){char c=b.charAt(p);if(slash){s.append(c);slash=false;}else if(c=='\\')slash=true;else if(c=='\"')break;else s.append(c);}return s.toString();}
    static String param(HttpExchange e,String k)throws IOException{String q=e.getRequestURI().getRawQuery();if(q==null)return "";for(String part:q.split("&")){String[] z=part.split("=",2);if(z.length==2&&z[0].equals(k))return URLDecoder.decode(z[1],StandardCharsets.UTF_8);}return "";}
    static byte[] read(File f)throws IOException{return read(new FileInputStream(f));}
    static byte[] read(InputStream in)throws IOException{ByteArrayOutputStream o=new ByteArrayOutputStream();byte[] b=new byte[4096];int n;while((n=in.read(b))!=-1)o.write(b,0,n);in.close();return o.toByteArray();}
    static void send(HttpExchange e,int status,String s,String type)throws IOException{sendBytes(e,status,s.getBytes(StandardCharsets.UTF_8),type);}
    static void sendBytes(HttpExchange e,int status,byte[] d,String type)throws IOException{e.getResponseHeaders().set("Content-Type",type+"; charset=UTF-8");e.sendResponseHeaders(status,d.length);try(OutputStream o=e.getResponseBody()){o.write(d);}}
    static String esc(String s){return s.replace("\\","\\\\").replace("\"","\\\"").replace("\r","\\r").replace("\n","\\n");}
}
