//105403517
//���V�a
//���3A
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.sun.prism.paint.Color;



public class MazeFrame extends JFrame{

	
	//�̭���new imageIcon �O����ɮרæs��image ���A�Ӱ��j�p���A�~���Anew �̦��O�Nimage ���A�A�ܦ^imageIcon
	public ImageIcon wallIcon = new ImageIcon(
				new ImageIcon(getClass().getResource("brickwall.png")).getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
	public Icon heartIcon = new ImageIcon(
				new ImageIcon(getClass().getResource("heart.png")).getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
	public Icon diamondIcon = new ImageIcon(
				new ImageIcon(getClass().getResource("diamond.png")).getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
	//private final JLabel structureLabel[];
	private final JPanel allPnl = new JPanel();
	private final List<JLabel> blockLabel = new ArrayList<JLabel>();
	private final BloodPanel bloodPnl = new BloodPanel();
	public ScheduledExecutorService seService= Executors.newScheduledThreadPool(10);
	
	public MazeFrame() {
		super("Maze");
		//�]�w�̤j��Frame �ƪ�
		setLayout(new BorderLayout());
		//�]�w10*10���g�c�a�ϱƪ�
		allPnl.setLayout(new GridLayout(10,10));
		
		//�إߵ{�Ǧ�
		ScheduledExecutorService seService = Executors.newScheduledThreadPool(10);
		//�o�ӬO�Ψө���ܷR�ߪ�
		ExecutorService executorService = Executors.newCachedThreadPool();
		
		//ImageIcon wallImg = new ImageIcon(wallIcon.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
		
		
	/*	try(
				Stream<String> stream =
					Files.lines(Paths.get("map.txt"))){
			stream.forEach(System.out::println);
		}catch(Exception e) {
			
		}
		*/
			SecureRandom random = new SecureRandom();
			Pattern pattern = Pattern.compile("\\s+");   //�ΨӤ��j�a��0,1�������Ů�
		List<String> list;
		try {
			 list = 
					Files.lines(Paths.get("map.txt"))//Ū��  �a�Ϫ��榡�O0101010���զ�0�O��  1 �h�j
					.flatMap(line->pattern.splitAsStream(line))   //�ΪŮ�j�}�åB�i�}
					.collect(Collectors.toList());
			 
			 
			 random.ints(10,0,100)  //�H���Q���ܼơA0��100   ��list �bx ���ɭԼƭȬO1(�N�����)�A�ܦ�3
			 		.boxed()
			 		.sorted()
			 		.collect(Collectors.groupingBy(Function.identity()))
			 		//.forEach((a,b)->System.out.println(a));
			 		.forEach((x,y)->{System.out.println(x);
			 						if(list.get(x).equals("1")) {
			 							list.set(x, "3");
			 						}//end if
			 		});
			 
			 list.stream()
				.forEach(x->{
					//System.out.println(x);//�M���C�@���A�۹������Ʀr�N�����۹�����icon�A�ñN���[��GUI�W
					if(x.equals("0")) {
						blockLabel.add(new JLabel());
						
						allPnl.add(blockLabel.get(blockLabel.size()-1));
						//�o�̩�listener �άOAnonymous ���S���@��
						blockLabel.get(blockLabel.size()-1).addMouseListener(new mouseHandler(0));
				
					}//end 
					else if(x.equals("1")) {
						
						blockLabel.add(new JLabel(wallIcon));
						allPnl.add(blockLabel.get(blockLabel.size()-1));
						blockLabel.get(blockLabel.size()-1).addMouseListener(new mouseHandler(1));
					}//end else if
					else if(x.equals("2")) {
						blockLabel.add(new JLabel(diamondIcon));
						allPnl.add(blockLabel.get(blockLabel.size()-1));
						blockLabel.get(blockLabel.size()-1).addMouseListener(new mouseHandler(2));
					}//end else if
					else if(x.equals("3")) {
						blockLabel.add(new JLabel(heartIcon));
						allPnl.add(blockLabel.get(blockLabel.size()-1));
						blockLabel.get(blockLabel.size()-1).addMouseListener(
								new mouseHandler(3,blockLabel.get(blockLabel.size()-1)));
						//�o�̨C����l�Ƥ@��Label ���n�N�L��i�@�Ӱ������
						executorService.execute(new ChangeHeart(blockLabel.get(blockLabel.size()-1)));
						
					}//end else if
					
				});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//blockLabel.get(0).addMouseListener(new mouseHandler());
		//allPnl.addMouseListener(new mouseHandler());
		
		add(allPnl,BorderLayout.CENTER);
		add(bloodPnl,BorderLayout.NORTH);
		
		
		
	}//end constructor
	
	
	
	private class mouseHandler extends MouseAdapter{
		public int blocktype=0;
		public JLabel label;
		
		public mouseHandler(int blocktype) {
			this.blocktype = blocktype;
			
		}//end constructor
		//�L��
		public mouseHandler(int blocktype, JLabel label) {
			this.blocktype = blocktype;//����   1�N������A0�N��D���A3�N��R�� 2�O�p��
			this.label = label;
		}//end constructor
		
		
		@Override
		public void mouseEntered(MouseEvent e) {
			//�o�Ӱ�������l�O�Ψө񰱤U�ӬO����Ϊ��AScheduledThreadPool�O�i�H�b���w���
			//�������A�٦��b���w�g�����ư���
			 seService= Executors.newScheduledThreadPool(10);
			IdleHurting idleHurting = new IdleHurting(blocktype);
			//�]�w�Ĥ@������b�@���A����g�����@��A�N��O�C�@���|����@��
			//�`�N�o����ǳ��O�b�i�J�@��JPanel �|����A�bMouseExited �̭��A�R��
			//����O�C�����u�|���@�Ӱ�����QĲ�o�B����(�N�����|�|�[����)
			seService.scheduleAtFixedRate(idleHurting, 1, 1,TimeUnit.SECONDS );
			switch(blocktype) {
			case 0:
				System.out.println("-2");
				bloodPnl.hitRoad();
				break;
			case 1:
				
				System.out.println("-20");
				bloodPnl.hitWall();
				break;
			case 2:
				System.out.println("finish");
				JOptionPane.showMessageDialog(null, "Congraduation!!", "Success", JOptionPane.DEFAULT_OPTION);
				System.exit(0);
				break;
			case 3:
				//�ĤT�ر��p���ӥu���R�ߦӤw�A���O�{�b�n���L�i�H�^����A�ܦ^�R�ߡA�ҥH��label��icon �ӧP�_�{�b�O���@�ت��p
				System.out.println("+10");
				if(label.getIcon().equals(wallIcon)) {
					bloodPnl.hitWall();
				}//end if
				else {
					bloodPnl.hitHeart();
				}//end else
				break;
			}//end switch
			
			if(bloodPnl.blood<1) {
				bloodPnl.relife(); //��^��
				seService.shutdown();
		}//end if
		}//end mouseEntered
		
		
		@Override
		public void mouseExited(MouseEvent e) {
			seService.shutdown();
		}//end mouseExited
		
		
		
		
		
	}//end mouseHandler
	
	
	//���ܷR�ߪ������
	public class ChangeHeart implements Runnable{
		public JLabel label;
		SecureRandom generator = new SecureRandom();
		
		public ChangeHeart(JLabel label) {
			this.label = label;
			
		}//end constructor
		
		public void run() {
			while(true) {
				
			//�o�����L�C������ɳ��H����ı    �̦h�𮧨��
			try {
				Thread.sleep(generator.nextInt(2000));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//�n�ܰʡA�ҥH�쥻�O����n�ܷR�ߡA�R�߭n�����
			if(label.getIcon().equals(wallIcon)) {
				label.setIcon(heartIcon);
			}//end if
			else {
				label.setIcon(wallIcon);
			}//end else
			}//end while
		}//end run
	}//end class changeHeart
	
	//�o�����L���U�Ӫ��ɭԦ���Ϊ�������
	public class IdleHurting implements Runnable{
		public int blocktype;
		
		public IdleHurting(int blocktype) {
			this.blocktype = blocktype;
		}//end constructor
		
		public void run() {
			
			switch(blocktype) {
			case 0:
				bloodPnl.idleRoad();
				System.out.println("-2");
				break;
			case 1:
				bloodPnl.idleWall();
				break;
				
			}//end switch
			
		}//end running
	}//end class IdleHurting
	
}//end mazeFrame
