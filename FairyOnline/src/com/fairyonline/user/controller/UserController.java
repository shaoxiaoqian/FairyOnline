package com.fairyonline.user.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.fairyonline.user.entity.User;
import com.fairyonline.user.entity.UserLogin;

import com.fairyonline.user.service.UserServiceImpl;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;




@Controller
@RequestMapping("user")
public class UserController {
	/*
	    private static List<UserLogin> userLoginList;
		
		public UserController() {
			super();
			userLoginList = new ArrayList<UserLogin>();
		}
	*/
		
		@Resource
		private UserServiceImpl userServiceImpl;
		
		@RequestMapping("/userList1")
		public String list(Model model) {
			List<User> list = this.userServiceImpl.listAll();
			model.addAttribute("list",list);
			return "user/userList1"; 
		}
		
		@RequestMapping("/regist1")
		public String regist(String userName)throws IOException{
			List<UserLogin> list = this.userServiceImpl.allUserLogin();
			System.out.println(userName);
			boolean flag = true;
			if(userName != null) {
			for(int i=0;i<list.size();i++) {
				if(list.get(i).getUserName().equals(userName)) {
					flag=false;
					break;
				}
			}
			}else {
				flag=false;
			}
			System.out.println(flag);
			ObjectMapper x = new ObjectMapper();
			String isExist = x.writeValueAsString(flag);
			return isExist;
		}
		
		@RequestMapping("/regist")
		public String userRegist(HttpServletRequest request, HttpServletResponse response){
			String userName = request.getParameter("UserName");
			String passWord = request.getParameter("PassWord");
			List<UserLogin> list = this.userServiceImpl.allUserLogin();
			UserLogin userLogin = new UserLogin();
			userLogin.setUserName(userName);
			userLogin.setPassWord(passWord);
			list.add(userLogin);
			//UserLogin userLogin = new UserLogin("UserName","PassWord");
			//User user = new User("zhangsan","dddfdfd","zhangsan","女",userLogin);
			this.userServiceImpl.addUserLogin(userLogin);
			//this.userServiceImpl.addUser(user);
			return "user/personal";
			
			
		} 
		
		@RequestMapping("/login")
		public String userLogin(Model model,HttpServletRequest request,HttpServletResponse response)throws IOException{
			UserLogin userLogin2 = new UserLogin();
			String userName2;
			HttpSession session = request.getSession();
			if(session.getAttribute("userLogin")!=null) {
				session.removeAttribute("userLogin");
			}
			if(session.getAttribute("userLogin")==null) {
				String userName = request.getParameter("UserName");
				String passWord = request.getParameter("PassWord");
				UserLogin userLogin = this.userServiceImpl.login(userName,passWord);
				userLogin2 = userLogin;
				userName2 = userName;
			}else {
				String userName=(String)session.getAttribute("userLogin");
				UserLogin userLogin = this.userServiceImpl.findUserLogin(userName);
				userLogin2 = userLogin;
				userName2 = userName;
			}
			if(userLogin2!=null) {
				session.setAttribute("userLogin",userName2);
				model.addAttribute("admin",userName2);
				System.out.println("login执行成功");
				return "user/index";
			}else {
				model.addAttribute("errormsg","用户名或密码错误");
				return "user/login";
			}
			
		}
		
		
		@RequestMapping(value="/updateitem",method= {RequestMethod.POST,RequestMethod.GET})
		public String updateItems(MultipartFile picture, String UserName,HttpServletRequest request,HttpServletResponse response) throws Exception {
			String userName = request.getParameter("UserName");
			List<UserLogin> list = this.userServiceImpl.allUserLogin();
			if(userName != null) {
			for(int i=0;i<list.size();i++) {
				if(list.get(i).getUserName().equals(userName)) {
					String petName = request.getParameter("PetName");
					String sex = request.getParameter("Sex");
					String img = request.getParameter("Img");
					String tName = request.getParameter("TName");
					UserLogin userLogin = this.userServiceImpl.findUserLogin(userName);
					List<User> list1 = this.userServiceImpl.listAll();
					
					User user = new User();
					user.setPetName(petName);
					//user.setImg(img);
					user.setSex(sex);
					user.settName(tName);
					user.setUserLogin(userLogin);
					
					
					/*
					// 处理上传的单个图片    
				    String originalFileName = picture.getOriginalFilename();// 原始名称
				    // 上传图片
				    if (picture != null && originalFileName != null && originalFileName.length() > 0) {
				    	 System.out.println("get add imgs  success");
				    	// String pic_path = "E:\\temp\\images\\";
				    	 String pic_path = "fairyonline\\user\\images\\";
				    	 String newFileName = UUID.randomUUID()
				                 + originalFileName.substring(originalFileName
				                         .lastIndexOf("."));     
				         File newFile = new File(pic_path + newFileName);//新图片
				         picture.transferTo(newFile);// 将内存中的数据写入磁盘
				         user.setImg(newFileName);// 将新图片名称写到itemsCustom中
				    }else {
				    	System.out.println("else  success");
				    	//如果用户没有选择图片就上传了，还用原来的图片
				       // User user = this.userServiceImpl.findUserById(user.getID());
				       // user.setImg(user.getImg());
				    }
				    */
					
					if(picture != null) {
						String name = picture.getName();
						System.out.println(name);
						String originalFileName = picture.getOriginalFilename();
						System.out.println(originalFileName);
						String pictureName = originalFileName.substring(originalFileName.lastIndexOf("\\")+1);
						byte[] bytes = picture.getBytes();
						String realPath = request.getServletContext().getRealPath("/upload");
						File f = new File(realPath);
						FileOutputStream fo = new FileOutputStream(f);
						fo.write(bytes);
						fo.flush();
						fo.close();
						String imgurl = originalFileName;
						user.setImg(originalFileName);
					} 
					
					list1.add(user);
					this.userServiceImpl.addUser(user); 
				}
			  }
			
			   
		   }
			 return "user/index";
			/*User items = new User("PetName","TName","Sex");
			this.userServiceImpl.addupUser(items);
			 */
		}
       
		@RequestMapping("/searchUser")
		public String userlist(Model model,String userName,HttpServletRequest request,HttpServletResponse response){
			userName = request.getParameter("found");
			List<UserLogin> list = this.userServiceImpl.getUserByPartName(userName);
		    model.addAttribute("list",list);
			return "user/searchUserResult";
		}  

		@RequestMapping("/homePage")
		public String userlist1(Model model,String userName) {
			UserLogin user = this.userServiceImpl.findUser1(userName);
			if(user != null) {
				model.addAttribute("user",user);
				return "user/homePage";
			}else {
				System.out.println("失败");
				return "user/index";
			}
		}
		
		 
}
