import java.io.File;
import java.io.IOException;

class thread2 extends Thread
{
    Thread t;
    thread2(Thread t)
    {
        this.t=t;
    }
    public void run()
    {
        System.out.println("10");
        while(true){
            try {
                Thread.sleep(1000);
                System.out.println(t.getName()+" : "+t.isAlive());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
class thread extends  Thread
{
    Thread t;
    thread(Thread t)
    {
        this.t=t;
    }
    @Override
    public void run()
    {
        System.out.println(t.isAlive());
        new thread2(t).start();
    }
}
public class Checking {
    public static void main(String[] args) throws InterruptedException, IOException {


        //System.out.println(Long.rotateRight(4l,0));
  //Runtime.getRuntime().exec("thoptv");
    //ProcessBuilder builder=new ProcessBuilder();
    //builder.directory(new File("/usr/bin"));
    //builder.command("./ls");
    //builder.redirectOutput(new File("/home/ARTHAKUR/config.txt"));
    //builder.redirectError(new File("/home/ARTHAKUR/error.txt"));
    //Process process=builder.start();
    //File file=new File("/home/ARTHAKUR/aniket.mp3");
    //System.out.println(file.getName());
    //System.out.println((int)2l);
 // File filee=new File("/home/ARTHAKUR");
  //System.out.println(filee.exists());
        System.out.println((int)(Float.parseFloat(String.format("%.2f",0.77777777))*100));

       thread t;
        (t=new thread(Thread.currentThread())).start();
        Thread.sleep(100);
        System.out.println(t.isAlive());

       // System.out.println("Exit");
    }
}
