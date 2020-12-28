package ShareMe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


class Trigger extends  Thread
{
    Socket socket;
    public  Trigger(Socket socket)
    {
        this.socket=socket;
    }

    @Override
    public void run()
    {
        try {
             InputStream inputStream= socket.getInputStream();
             while(true)
             {
                 int k=inputStream.read();
                 if (k==100)
                 {
                     thread.exit=true;
                     JOptionPane.showMessageDialog(null,"There are some problem at receiver side ");
                     return;
                 }
             }
        } catch (IOException e) {
        }
    }
}



class thread extends  Thread
{
    static  boolean exit=false;
    static Socket socket;
    static  String getHumanReadableFormat(long bytes)
    {
        String result="";
        long kb=bytes/1024;
        long mb=kb/1024;
        long remaining_kb=kb%1024;
        result=mb+" Mb "+remaining_kb+" Kb";
        result+=" : ";
        result+=(mb/1024)+" Gb "+(mb%1024)+" Mb ";
        return result;
    }
    JLabel label2,fsize,percent;
    JProgressBar progressBar;
    String ip;
    File file;
    public thread(File file,String ip,JLabel label2,JProgressBar progressBar,JLabel fsize,JLabel percent) throws Exception {
        this.file=file;
        this.ip=ip;
        this.label2=label2;
        this.progressBar=progressBar;
        this.fsize=fsize;
        this.percent=percent;
    }
    static int percentage(float bytes,long total)
    {
        float result=(bytes/total);
        result=Float.parseFloat(String.format("%.2f",result));
        return (int)(result*100);
    }
    @Override
    public  void run()
    {
        OutputStream outputStream=null;
        FileInputStream inputStream = null;
        try {
            if (ip.length()<7)
            {
                JOptionPane.showMessageDialog(null,"Ip address is not correct !!! ");
                return;
            }
            JOptionPane.showMessageDialog(null,"Connecting to the receiver .....");
             socket = new Socket(ip, 3030);
            JOptionPane.showMessageDialog(null,"Connected Successfully");
            new Trigger(socket).start();
             outputStream = socket.getOutputStream();
            inputStream = new FileInputStream(file);

        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            JOptionPane.showMessageDialog(null,"There is some error !!!");
            return;
        }
        try {
            //sending file_size
            Long length=file.length();
            String length_=length.toString();
            byte length_bytes[]=length_.getBytes();
            outputStream.write(length_bytes.length);
            outputStream.write(length_bytes);

            //sending file_name
            byte bytes[]=file.getName().getBytes();
            outputStream.write(bytes.length);
            outputStream.write(bytes);


        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,"There is some error");

        }
        long bytes=0;
        fsize.setText(thread2.getSize(file.length()));
        progressBar.setMaximum( 100);
        progressBar.setMinimum(0);
        while(true)
        {

            if (exit)
            {
                try {
                    socket.close();
                    socket=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return;
            }
            try {
                int cha1=inputStream.read();
                if (cha1==-1)
                {
                    JOptionPane.showMessageDialog(null,"Sent Successfully !!!!");
                    socket.close();
                    socket=null;
                    return;
                }
                outputStream.write(cha1);
                bytes++;
                String sent=getHumanReadableFormat(bytes);
                label2.setText("Sent :-> \n"+sent);
                progressBar.setValue(percentage(bytes,file.length()));
                float per=Float.parseFloat(String.format("%.2f",progressBar.getPercentComplete()));
                percent.setText((per*100)+" % ");

            } catch (IOException e) {

                if (socket!=null)
                {
                    try {
                        socket.close();
                        socket=null;
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
                JOptionPane.showMessageDialog(null,"There is some error while sending !!");
                return;
            }

        }
    }
}
class thread2 extends  Thread{
    String file_name;
    JLabel label;
    static Socket socket1;
    static  ServerSocket socket;
    JProgressBar progressBar;
    JLabel fname,fsize,percent;
    public thread2(String file_name,JLabel label,JProgressBar progressBar,JLabel fname,JLabel fsize,JLabel percent)
    {

        this.fname=fname;
        this.fsize=fsize;
        this.percent=percent;
        this.progressBar=progressBar;
        this.label=label;
        this.file_name=file_name;
    }
    static public  String getSize(long bytes)
    {
        String result="";
        long kb=bytes/1024;
        long mb=kb/1024;
        long remaining_kb=kb%1024;
        result=mb+" Mb "+remaining_kb+" Kb";
        return result;
    }
    @Override
    public  void run()
    {
        boolean flag=true;
        try {
             socket=new ServerSocket(3030);
            JOptionPane.showMessageDialog(null," Listening ......");
             socket1=socket.accept();
            JOptionPane.showMessageDialog(null,"Accepted");
            InputStream stream=socket1.getInputStream();;

            //Reading file size
            int file_length_=stream.read();
            byte file_size[]=stream.readNBytes(file_length_);
            String temp="";
            for(int i=0;i<file_length_;i++)
                temp+=(char)file_size[i];
            long file_length=Long.parseLong(temp);

            //Reading file_name
            int total_bytes=stream.read();
            byte name[]=stream.readNBytes(total_bytes);
            file_name=file_name.trim();
            file_name+="/";
            String temp_="";
            for(int i=0;i<total_bytes;i++)
            {
                temp_+=(char)name[i];
                file_name+=(char)name[i];
            }
            fname.setText("Name : "+temp_);
            flag=true;
            FileOutputStream outputStream=new FileOutputStream(file_name);
            flag=false;
            long bytes=0;
            fsize.setText("File Size : "+getSize(file_length));

            while (true)
            {
                int i=stream.read();
                outputStream.write(i);
                bytes++;
                label.setText("Received :-> "+thread.getHumanReadableFormat(bytes));
                progressBar.setValue(thread.percentage(bytes,file_length));
                float perc=Float.parseFloat(String.format("%.2f",progressBar.getPercentComplete()));
                percent.setText((perc*100)+" % ");
                if (file_length==bytes)
                {
                    JOptionPane.showMessageDialog(null,"Successfully Received");
                    JOptionPane.showMessageDialog(null,"Connection closed !!!!");
                    socket.close();
                    socket=null;
                    socket1=null;
                    return;
                }
            }


        } catch (Exception e) {
            if (!flag)
            {
                File file=new File(file_name);
                file.delete();
            }
            if (socket!=null)
            {

                try {
                    if (socket1!=null)
                        socket1.getOutputStream().write(100);

                    socket.close();
                    socket=null;
                    socket1=null;
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            JOptionPane.showMessageDialog(null,"There are some error !!!");
                JOptionPane.showMessageDialog(null,"Connection is Closed , Can be reconnected");


        }
    }

}
public class Share {


  static  JFrame send,receive;
    public static void main(String[] args) {

        JFrame frame=new JFrame();
        frame.setTitle("ANI_SHARE !!!!!!!");
        frame.setSize(400,400);
        frame.setResizable(false);
        frame.setLayout(null);
        JButton sender=new JButton("Sender");
        JButton receiver=new JButton("Receiver");
        sender.setBounds(140,100,100,50);
        sender.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send=new JFrame("Sender.........");
                frame.setVisible(false);
                sender();
                sender.setBackground(Color.CYAN);
            }
        });
        receiver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                receive=new JFrame("Receiver........");
                frame.setVisible(false);
                receiver();
                receiver.setBackground(Color.CYAN);
            }
        });
        receiver.setBounds(140,200,100,50);
        frame.add(sender);
        frame.add(receiver);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
         JOptionPane.showMessageDialog(frame,"Note : Use reverse proxy tool or port forwarding if you face  connecting to receiver problem");
    }
     static File file;
    public  static void sender()
    {

        Font font=new Font(Font.SERIF, Font.BOLD, 12);
        send.setSize(600,500);
        send.setLayout(null);
        send.setResizable(false);
        JLabel label=new JLabel("Enter Ip address ");
        label.setForeground(Color.RED);
        label.setBounds(200,30,170,30);
        send.add(label);
        label.setFont(font);
        JTextField ipaddress=new JTextField();
        ipaddress.setColumns(20);
        ipaddress.setBackground(Color.WHITE);
        ipaddress.setForeground(Color.RED);
        ipaddress.setBounds(200,70,170,20);
        send.add(ipaddress);
        ipaddress.setFont(font);

        JButton button=new JButton("Choose");
        button.setBounds(200,100,100,30);
        send.add(button);
        button.setFont(font);

        JLabel label1=new JLabel("No File  Chosen !!!!");
        label1.setBounds(200,140,380,30);
        label1.setForeground(Color.RED);
        send.add(label1);
        label1.setFont(font);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (thread.socket == null) {
                    JFileChooser fileChooser = new JFileChooser();
                    int result = fileChooser.showOpenDialog(null);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        file = fileChooser.getSelectedFile();
                        label1.setText(file.getName());

                    } else {
                        label1.setText("No File Chosen !!!!");
                        file = null;
                    }

                }
                else{
                    JOptionPane.showMessageDialog(null,"Disconnect First !!!!");
                }
        }});

        JButton sendnow=new JButton("Send");
        sendnow.setBounds(200,180,100,30);
        send.add(sendnow);
        sendnow.setFont(font);
        JLabel label2=new JLabel(" Status ");
        label2.setBounds(200,220,350,30);
        label2.setForeground(Color.RED);
        send.add(label2);
        label2.setFont(font);


        JLabel fsize =new JLabel("File Size : ... ");
        JLabel percent=new JLabel("Received : ... %");
        fsize.setFont(font);
        percent.setFont(font);


        fsize.setForeground(Color.BLUE);
        percent.setForeground(Color.BLUE);

        fsize.setBounds(200,260,350,30);
        percent.setBounds(200,300,250,30);

        send.add(fsize);
        send.add(percent);

        JProgressBar progressBar=new JProgressBar();
        progressBar.setBounds(200,350,250,20);
        progressBar.setForeground(Color.BLACK);
        send.add(progressBar);
        sendnow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ip=ipaddress.getText();
                try {
                    if(file!=null) {
                        if (thread.socket == null) {
                            Thread thread1 = new thread(file, ip, label2, progressBar,fsize,percent);
                            thread1.start();
                        } else {
                            JOptionPane.showMessageDialog(null, "Already One Connection Exists Close that first");
                        }
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null,"Select File first ");
                    }
                } catch (Exception exp) {
                    JOptionPane.showMessageDialog(null,"There is some error");
                }
            }
        });
      JButton button1 =new JButton("Disconnect");
      button1.setBounds(200,380,150,30);
      send.add(button1);
      button1.setFont(font);
      button1.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
              if (thread.socket==null)
              {
                  JOptionPane.showMessageDialog(null," Not Connected to anyone ");
              }
              else
              {
                  try {
                      thread.socket.close();
                      thread.socket=null;
                      JOptionPane.showMessageDialog(null,"Disconnected Successfully");
                  } catch (IOException ioException) {
                      JOptionPane.showMessageDialog(null,"Some Error while disconnecting");
                      ioException.printStackTrace();
                  }
              }
          }
      });

      JButton button2 =new JButton("Help");
      button2.setBounds(200,420,100,30);
      send.add(button2);
      button2.setFont(font);
      button2.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
              JFrame help=new JFrame();
              help.setTitle("Help");
              help.setLayout(null);
              help.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
              help.setSize(400,100);
              JLabel label1=new JLabel("Email Id : aniketranag123@gmail.com");
              label1.setBounds(100,20,350,20);
              help.add(label1);
              label1.setFont(font);
              help.setVisible(true);
          }
      });
        send.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        send.setVisible(true);
    }
    public static  void receiver()
    {
        Font font=new Font(Font.SERIF, Font.BOLD, 12);
        receive.setSize(600,500);
        receive.setLayout(null);
        receive.setResizable(false);
        JLabel enterD=new JLabel("Enter Directory  Name !!!");
        enterD.setForeground(Color.RED);
        enterD.setBounds(200,50,250,30);
        enterD.setFont(font);
        JTextField dirc=new JTextField();
        dirc.setColumns(50);
        dirc.setForeground(Color.RED);
        dirc.setBackground(Color.CYAN);
        dirc.setBounds(200,90,150,30);
        dirc.setFont(font);
        receive.add(enterD);
        receive.add(dirc);
        JButton REC=new JButton("Receive");
        REC.setBounds(200,130,100,30);
        receive.add(REC);
        receive.setFont(font);
        JLabel label=new JLabel("Status");
        label.setForeground(Color.RED);
        label.setBounds(200,170,300,30);
        receive.add(label);
        label.setFont(font);


        JProgressBar progressBar =new JProgressBar();
        progressBar.setForeground(Color.RED);
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setBounds(200,210,250,20);

        receive.add(progressBar);
        JLabel fname=new JLabel("File Name : ... ");
        JLabel fsize =new JLabel("File Size : ... ");
        JLabel percent=new JLabel("Received : ... %");
        fname.setFont(font);
        fsize.setFont(font);
        percent.setFont(font);

        fname.setForeground(Color.BLUE);
        fsize.setForeground(Color.BLUE);
        percent.setForeground(Color.BLUE);

        fname.setBounds(200,240,350,30);
        fsize.setBounds(200,280,350,30);
        percent.setBounds(200,320,250,30);

        receive.add(fname);
        receive.add(fsize);
        receive.add(percent);
        REC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dirc.getText().length()==0)
                {
                    JOptionPane.showMessageDialog(null,"Enter File Name ");
                }
                else
                {

                    File file=new File(dirc.getText());
                    if(!file.exists())
                    {
                     JOptionPane.showMessageDialog(null,"Directory name is not  correct ");
                    }
                    else {


                        if (thread2.socket==null) {
                            thread2 thread_ = new thread2(dirc.getText(), label, progressBar,fname,fsize,percent);
                            thread_.start();
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(null,"Already Listening ...");
                        }
                    }
                }
            }
        });






        JButton button2 =new JButton("Help");
        button2.setBounds(200,370,100,30);
        receive.add(button2);
        button2.setFont(font);
        JButton button3=new JButton("Stop Listening");
        button3.setBounds(200,410,170,30);
        receive.add(button3);
        button3.setFont(font);
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (thread2.socket!=null)
                {
                    try {

                        if (thread2.socket1!=null)
                        thread2.socket1.getOutputStream().write(100);
                        thread2.socket.close();
                        thread2.socket=null;
                        thread2.socket1=null;
                        JOptionPane.showMessageDialog(null,"Done ...");
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(null,"Already Closed ... ");
                }
            }
        });




        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Font font=new Font(Font.SERIF, Font.BOLD, 12);
                JFrame help=new JFrame();
                help.setTitle("Help");
                help.setLayout(null);
                help.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                help.setSize(400,100);
                JLabel label1=new JLabel("Email Id : aniketranag123@gmail.com");
                label1.setForeground(Color.RED);
                label1.setFont(font);
                label1.setBounds(100,20,350,20);
                help.add(label1);
                help.setVisible(true);
            }
        });


        receive.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        receive.setVisible(true);
    }
}
