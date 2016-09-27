package com.lami.tuomatuo.manage.helper;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class ImgCodeHelper {
	private  static final char[] ch = "ABCDEFGHJKMNPQRSTUVWXYZ23456789".toCharArray();  
	public static final String SESSION_CODE="session_code";
	public static final String SESSION_ADMIN_CODE="session_admin_code";
	
	public static void  getImg(HttpSession session,OutputStream os) throws IOException{
        BufferedImage img = new BufferedImage(100, 30,BufferedImage.TYPE_INT_RGB);  
        Graphics g = img.getGraphics();  
        Random r = new Random();  
        Color c = new Color(172, 214, 255);  
        g.setColor(c);  
        g.fillRect(0, 0, 100, 30);  
        StringBuffer sb = getStringCode();
        for (int i = 0; i < 4; i ++) {  
               g.setColor(new Color(r.nextInt(88), r.nextInt(188), r.nextInt(255)));  
               g.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 24));                 
               g.drawString("" + sb.charAt(i), (i * 22) + 6, 24);
        }  
        session.setAttribute(SESSION_CODE, sb.toString());
        ImageIO.write(img, "JPG", os);
	}
	public static boolean checkImgCode(HttpSession session,String code){
		String sysCode = (String)session.getAttribute(SESSION_CODE);
		if(code!=null&&sysCode!=null&&code.toLowerCase().equals(sysCode.toLowerCase()))
			return true;
		return false;
	}
	
	public static StringBuffer getStringCode(){
		Random r = new Random();
		StringBuffer sb = new StringBuffer();  
		 int index=0;
		 int len = ch.length;  
		 for (int i = 0; i < 4; i ++) {  
			  index = r.nextInt(len);  
			  sb.append(ch[index]);  
		 }
		 return sb;
	}
	
	public static boolean checkAdminImgCode(HttpSession session,String code){
		String sysCode = (String)session.getAttribute(SESSION_ADMIN_CODE);
		if(code!=null&&sysCode!=null&&code.toLowerCase().equals(sysCode.toLowerCase()))
			return true;
		return false;
	}

	public static void getAdminImg(HttpSession session, ServletOutputStream os) throws IOException {
		BufferedImage img = new BufferedImage(65, 26,BufferedImage.TYPE_INT_RGB);  
        Graphics g = img.getGraphics();  
        Random r = new Random();  
        Color c = new Color(172, 214, 255);  
        g.setColor(c);  
        g.fillRect(0, 0, 65, 26);  
        StringBuffer sb = getStringCode();
        for (int i = 0; i < 4; i ++) {  
               g.setColor(new Color(r.nextInt(88), r.nextInt(188), r.nextInt(255)));  
               g.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 20));                 
               g.drawString("" + sb.charAt(i), (i * 15) + 3, 22);
        }  
        session.setAttribute(SESSION_ADMIN_CODE, sb.toString());
        ImageIO.write(img, "JPG", os);
	}

}
