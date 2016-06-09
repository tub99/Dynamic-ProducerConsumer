import java.awt.*;  
import java.awt.image.*;  
import java.awt.event.*; 
import java.util.*;  
import java.applet.*;  
import java.net.*;  
import java.awt.Component.*; 
import java.awt.Color.*; 
import java.awt.Graphics; 
import java.util.Random; 
import java.lang.Math; 
import java.awt.event.WindowListener; 
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;



 
public class DProducerConsumer extends Applet {
	Frame window;
    Button runButton;
    Button closebutton;
	TextArea textarea = new TextArea(24, 80);
	Label l1,l2,labelSize;
	TextField t1, t2,bufferSize;
 
    List<Integer> l = new LinkedList<Integer>();
    private Object lock = new Object();
    volatile boolean done = false;
    private int listSize=5;
    public void init() 
	{		
    	 
    	
    	window = new Frame ("GUI Producer Consumer");
    	window.setSize(600, 600);
    	//window.reshape (100, 100, 600, 100);
    	textarea.setFont(new Font("Helvetica", Font.PLAIN, 12));
    	textarea.setEditable(false);
    	window.add("Center", textarea);
    	textarea.appendText("*******Synchronized Producer Consumer*****\n");
    	 t1 = new TextField(10);
         t2 = new TextField(10);
         l1=new Label();l2=new Label();labelSize=new Label();
         l1.setText("No of Producer");l2.setText("No of consumer");labelSize.setText("Buffer size");
         bufferSize=new TextField(10);
         add(l1);
         add(t1);
         add(l2);
         add(t2);
         add(labelSize);
         add(bufferSize);

         runButton = new Button();
         runButton.setLabel("RUN");
         
         add(runButton);
         // Attach actions to the components 
        // runButton.addActionListener(runButton); 
    	//window.show();

  	}
    public boolean action(Event event, Object arg) 
    {
            if (event.target == runButton)
            {
                    window.show();
                    //repaint();
                    runner();

            }


            return true;
    }
    public void runner(){

    	int nProducer =Integer.parseInt(t1.getText()), 
    		nConsumer = Integer.parseInt(t2.getText());
        //ProducerConsumerAdvanced pc = new ProducerConsumerAdvanced();
    	setListSize(Integer.parseInt(bufferSize.getText()));
        Thread[] arr = new Thread[nProducer + nConsumer];
        int index = 0;

	       
	        for (int i=0; i<nProducer; i++){
	                Thread t1 = new Thread(new Runnable() {
	                    public void run() {
	                        try {
	                           produce();
	                        } catch (Exception e) {
	                            e.printStackTrace();
	                        }
	                    }
	                },"Producer-id:"+i+" "
	       
	                );
	                arr[index++] = t1;
	        }
	       
	        for(int i =0; i<nConsumer; i++){
	                Thread t2 = new Thread(new Runnable() {
	                    public void run() {
	                        try {
	                            consume();
	                        } catch (Exception e) {
	                            e.printStackTrace();
	                        }
	                    }
	                },"Consumer-id:"+i+" "
	       
	                );
	                arr[index++] = t2;
	        }
	        for(int i =arr.length - 1; i>=0; i--){
	                arr[i].start();
	        }
	        for(int i =arr.length - 1; i>=0; i--){
	            try {
					arr[i].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    textarea.appendText("****************THE END************************\n");
        System.out.println("****************THE END************************");
     
    }
    public void stop() 
    {
    	window.dispose();
    }
    private void setListSize(int s){
    	listSize=s;
    }
   
    public void produce() throws InterruptedException {
        int value = 0;
        while (true) {
            synchronized (lock) {
                while (l.size() >= 10) {
                    //while list is full don't add
                    lock.wait();
                }
                textarea.appendText("Current thread is "+Thread.currentThread().getName()+"\n");
                textarea.appendText("-----------------------------------------------------------\n");
                textarea.appendText("Producing item : "+value+"\n\n");
                l.add(value++);
                lock.notify();
                if (value == listSize){
                    done = true;
                    break;
                }
            }
            Thread.sleep(2000);
        }
    }
 
    public void consume() throws InterruptedException {
        outer:
        while (true) {
            synchronized (lock) {
                while (l.size() == 0){
                    if(done == true) {
                        break outer;
                    }
                    //Can't consume if there is nothing
                    //Hence call Producer
                    lock.wait();
                }
                textarea.appendText("Current thread is "+Thread.currentThread().getName()+"\n");
                textarea.appendText("-----------------------------------------------------------\n");
                textarea.appendText(" Number of items produced is " + l.size()+"\n"); 
                System.out.println("Number of items produced is " + l.size());
                
                int value = l.remove(0);
                textarea.appendText("Value consumed from List is:" + value+"\n\n"); 
                System.out.println("Value consumed from List is:" + value);
                
                System.out.println("*****************************");
                lock.notify();
            }
            Thread.sleep(2000);
        }
    }
   
 
}
