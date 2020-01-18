import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class ServerWeb{
    ServerSocketChannel server;
    int pto=8000;
    Selector sel;
    int index=0;
    int count=0;
    int serverpto[]={1234,1235};
    public ServerWeb()throws Exception{
        server=ServerSocketChannel.open();
        server.configureBlocking(false);
        server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        server.socket().bind(new InetSocketAddress(pto));
        sel = Selector.open();
        server.register(sel,SelectionKey.OP_ACCEPT);
        System.out.println("Servicio iniciado..esperando clientes..");
       
    }
    public void Talk() throws Exception{
        
        while(true){
            sel.select();
            Iterator<SelectionKey> it=sel.selectedKeys().iterator();
            while(it.hasNext()){
                SelectionKey k=(SelectionKey)it.next();
                it.remove();
                if(k.isAcceptable()){
                    SocketChannel cl=server.accept();
                    System.out.println("Cliente conectado desde->"+cl.socket().getInetAddress().getHostAddress()+":"+cl.socket().getPort());
                    cl.configureBlocking(false);
                    cl.register(sel,SelectionKey.OP_READ);
                    continue;
                }
                if(k.isReadable()){
                    int n;
                    SocketChannel ch=(SocketChannel)k.channel();
                    ByteBuffer b=ByteBuffer.allocate(3000);
                    b.clear();
                    ch.read(b);
                    b.flip();
                    count++;
                    index=count % serverpto.length;
                    int newpto=serverpto[index];
                    System.out.println("Sevidor: "+index);
                    Socket cl=new Socket("127.0.0.1",newpto);
                    BufferedOutputStream bos=new BufferedOutputStream(cl.getOutputStream());
                    //BufferedInputStream br=new BufferedInputStream(cl.getInputStream());
                    byte[] buf=new byte[1024];
                    bos.write(b.array());
                    bos.flush();
                    BufferedInputStream br=new BufferedInputStream(cl.getInputStream());
                    ByteArrayOutputStream byos=new ByteArrayOutputStream();
                    BufferedOutputStream dos=new BufferedOutputStream(byos);
                    while((n=br.read(buf,0,buf.length))!=-1){
                        dos.write(buf,0,n);
                    } 
                    dos.flush();
                    ByteBuffer b2=ByteBuffer.wrap(byos.toByteArray());
                    ch.write(b2);
                    ch.close();
                    continue;
                }
            }
        }

    }
    public static void main(String[] args) {
        try {
            ServerWeb s=new ServerWeb();
            s.Talk();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}